package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanOfferException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoanOfferMapper;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanService implements CreateLoanProductUseCase, ViewLoanProductUseCase, ViewLoanReferralsUseCase,
        RespondToLoanReferralUseCase, LoanOfferUseCase {
    private final LoanProductOutputPort loanProductOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final LoanMetricsUseCase loanMetricsUseCase;
    private final LoanProductMapper loanProductMapper;
    private final LoanRequestMapper loanRequestMapper;
    private final LoanRequestOutputPort loanRequestOutputPort;
    private final IdentityManagerOutputPort identityManagerOutPutPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoanReferralOutputPort loanReferralOutputPort;
    private final LoanOfferOutputPort loanOfferOutputPort;
    private final LoanOfferMapper loanOfferMapper;
    private final LoanOutputPort loanOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final LoaneeLoanAccountOutputPort loaneeLoanAccountOutputPort;
    private final IdentityVerificationUseCase verificationUseCase;
    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;
    private final LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;
    private final ProgramOutputPort programOutputPort;
    private final LoanMetricsMapper loanMetricsMapper;

    @Override
    public LoanProduct createLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        loanProduct.validateLoanProductDetails();
        UserIdentity foundUser = userIdentityOutputPort.findById(loanProduct.getCreatedBy());
        identityManagerOutPutPort.verifyUserExistsAndIsEnabled(foundUser);
        log.info("Loan product {} created successfully", loanProduct.getName());
        InvestmentVehicle investmentVehicle =
                investmentVehicleOutputPort.findById(loanProduct.getInvestmentVehicleId());
        loanProduct.addInvestmentVehicleValues(investmentVehicle);
        return loanProductOutputPort.save(loanProduct);
    }

    @Override
    public void deleteLoanProductById(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        MeedlValidator.validateUUID(loanProduct.getId(), LoanMessages.INVALID_LOAN_PRODUCT_ID.getMessage());
        loanProductOutputPort.deleteById(loanProduct.getId());
    }

    @Override
    public Page<LoanProduct> viewAllLoanProduct(LoanProduct loanProduct) {
        return loanProductOutputPort.findAllLoanProduct(loanProduct);
    }

    @Override
    public List<LoanProduct> search(String loanProductName) throws MeedlException {
        MeedlValidator.validateDataElement(loanProductName, "Loan product name is required");
        return loanProductOutputPort.search(loanProductName);
    }



    @Override
    public LoanProduct updateLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        MeedlValidator.validateUUID(loanProduct.getId(), LoanMessages.INVALID_LOAN_PRODUCT_ID.getMessage());
        LoanProduct foundLoanProduct = loanProductOutputPort.findById(loanProduct.getId());
        if (foundLoanProduct.getTotalNumberOfLoanees() > BigInteger.ZERO.intValue()) {
            throw new LoanException("Loan product " + foundLoanProduct.getName() + " cannot be updated as it has already been loaned out");
        }
        foundLoanProduct = loanProductMapper.updateLoanProduct(foundLoanProduct, loanProduct);
        foundLoanProduct.setUpdatedAt(LocalDateTime.now());
        log.info("Loan product updated {}", foundLoanProduct);

        return loanProductOutputPort.save(foundLoanProduct);
    }

    @Override
    public Loan startLoan(Loan loan) throws MeedlException {
        MeedlValidator.validateObjectInstance(loan, LoanMessages.LOAN_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loan.getLoaneeId(), "Please provide a valid loanee identification");
        Loanee foundLoanee = loaneeOutputPort.findLoaneeById(loan.getLoaneeId());
        loan.setLoanee(foundLoanee);
        loan.setLoanAccountId(getLoanAccountId(foundLoanee));
        loan.setStartDate(LocalDateTime.now());
        if (loan.getStartDate().isAfter(LocalDateTime.now())) {
            throw new MeedlException("Start date cannot be in the future.");
        }
        loan.setLoanStatus(LoanStatus.PERFORMING);
        loan = loanOutputPort.save(loan);
        return loan;
    }

    @Override
    public Page<Loan> viewAllLoansByOrganizationId(Loan loan) throws MeedlException {
        MeedlValidator.validateObjectInstance(loan, LoanMessages.LOAN_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loan.getOrganizationId(), LoanMessages.LOAN_ID_REQUIRED.getMessage());
        MeedlValidator.validatePageSize(loan.getPageSize());
        MeedlValidator.validatePageNumber(loan.getPageNumber());
        Page<Loan> loans = loanOutputPort.findAllByOrganizationId
                (loan.getOrganizationId(), loan.getPageSize(), loan.getPageNumber());
        log.info("Loans returned from output port: {}", loans.getContent().toArray());
        return loans;
    }

    @Override
    public Loan viewLoanDetails(String loanId) throws MeedlException {
        MeedlValidator.validateUUID(loanId, LoanMessages.INVALID_LOAN_ID.getMessage());
        Loan foundLoan = loanOutputPort.viewLoanById(loanId).
                orElseThrow(()-> new LoanException(LoanMessages.LOAN_NOT_FOUND.getMessage()));
        log.info("Found loan {}", foundLoan);
        List<LoaneeLoanBreakdown> loaneeLoanBreakdowns =
                loaneeLoanBreakDownOutputPort.findAllByLoaneeId(foundLoan.getLoaneeId());
        log.info("Loanee loan breakdowns returned: {}", loaneeLoanBreakdowns);
        foundLoan.setLoaneeLoanBreakdowns(loaneeLoanBreakdowns);
        return foundLoan;
    }

    @Override
    public Page<Loan> viewAllLoans(int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        return loanOutputPort.findAllLoan(pageSize,pageNumber);
    }

    private String getLoanAccountId(Loanee foundLoanee) throws MeedlException {
        LoaneeLoanAccount loaneeLoanAccount = loaneeLoanAccountOutputPort.findByLoaneeId(foundLoanee.getId());
        log.info("Found loanee account: {}", loaneeLoanAccount);
        if (ObjectUtils.isEmpty(loaneeLoanAccount)) {
            log.info("Empty Loanee loan account returned: {}", loaneeLoanAccount);
            throw new LoanException(LoanMessages.LOANEE_ACCOUNT_NOT_FOUND.getMessage());
        }
        return loaneeLoanAccount.getId();
    }

    @Override
    public LoanProduct viewLoanProductDetailsById(String loanProductId) throws MeedlException {
        MeedlValidator.validateUUID(loanProductId, LoanMessages.INVALID_LOAN_PRODUCT_ID.getMessage());
        return loanProductOutputPort.findById(loanProductId);
    }

    @Override
    public LoanReferral viewLoanReferral(LoanReferral loanReferral) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanReferral, LoanMessages.LOAN_REFERRAL_CANNOT_BE_EMPTY.getMessage());
        loanReferral.validateViewLoanReferral();
        List<LoanReferral> foundLoanReferrals = loanReferralOutputPort.findLoanReferralByUserId(
                loanReferral.getLoanee().getUserIdentity().getId());
        if (foundLoanReferrals.isEmpty()) {
            throw new LoanException(LoanMessages.LOAN_REFERRAL_NOT_FOUND.getMessage());
        } else if (foundLoanReferrals.size() > 1){
            throw new LoanException(LoanMessages.MULTIPLE_LOAN_REFERRALS_IS_CURRENTLY_NOT_ALLOWED.getMessage());
        } else {
            return getLoanReferral(foundLoanReferrals);
        }
    }

    private LoanReferral getLoanReferral(List<LoanReferral> foundLoanReferrals) throws MeedlException {
        LoanReferral loanReferral = foundLoanReferrals.get(0);
        MeedlValidator.validateObjectInstance(loanReferral, LoanMessages.LOAN_REFERRAL_CANNOT_BE_EMPTY.getMessage());
        loanReferral = loanReferralOutputPort.findLoanReferralById(loanReferral.getId())
                .orElseThrow(()->  new LoanException(LoanMessages.LOAN_REFERRAL_NOT_FOUND.getMessage()));
        loanReferral.setIdentityVerified(loanReferral.getLoanee().getUserIdentity().isIdentityVerified());
        log.info("Found Loan referral by it's ID: {}, is verified : {}", loanReferral.getId(), loanReferral.isIdentityVerified());
        loanReferral.setLoaneeLoanBreakdowns
                (loaneeLoanBreakDownOutputPort.findAllByLoaneeId(loanReferral.getLoanee().getId()));
        return loanReferral;
    }

    @Override
    public LoanReferral respondToLoanReferral(LoanReferral loanReferral) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanReferral, LoanMessages.LOAN_REFERRAL_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanReferral.getId(), LoanMessages.INVALID_LOAN_REFERRAL_ID.getMessage());

        LoanReferral foundLoanReferral = loanReferralOutputPort.findById(loanReferral.getId());
        log.info("Found Loan Referral: {}", foundLoanReferral);
        checkLoanReferralHasBeenAcceptedOrDeclined(foundLoanReferral);
        loanReferral.validateLoanReferralStatus();

        return updateLoanReferral(loanReferral, foundLoanReferral);
    }

    private void checkLoanReferralHasBeenAcceptedOrDeclined(LoanReferral foundLoanReferral) throws MeedlException {
        if (foundLoanReferral.getLoanReferralStatus().equals(LoanReferralStatus.AUTHORIZED)) {
            throw new EducationException(LoanMessages.LOAN_REFERRAL_HAS_ALREADY_BEEN_ACCEPTED.getMessage());
        }
        else if (foundLoanReferral.getLoanReferralStatus().equals(LoanReferralStatus.UNAUTHORIZED)) {
            throw new EducationException(LoanMessages.LOAN_REFERRAL_HAS_ALREADY_BEEN_DECLINED.getMessage());
        }
    }

    private LoanReferral updateLoanReferral(LoanReferral loanReferral, LoanReferral foundLoanReferral) throws MeedlException {
        if (loanReferral.getLoanReferralStatus().equals(LoanReferralStatus.ACCEPTED)) {
            LoanRequest loanRequest = loanRequestMapper.mapLoanReferralToLoanRequest(foundLoanReferral);
            loanRequest.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
            log.info("Mapped loan request: {}", loanRequest);
            loanRequest = createLoanRequest(loanRequest);
            log.info("Created loan request: {}", loanRequest);
            foundLoanReferral.setLoanReferralStatus(LoanReferralStatus.AUTHORIZED);
        }
        else if (loanReferral.getLoanReferralStatus().equals(LoanReferralStatus.DECLINED)) {
            foundLoanReferral.setReasonForDeclining(loanReferral.getReasonForDeclining());
            foundLoanReferral.setLoanReferralStatus(LoanReferralStatus.UNAUTHORIZED);
        }
        foundLoanReferral = loanReferralOutputPort.save(foundLoanReferral);
        log.info("Updated loan referral: {}", foundLoanReferral);
        return foundLoanReferral;
    }

    public LoanRequest createLoanRequest(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest, LoanMessages.LOAN_REQUEST_CANNOT_BE_EMPTY.getMessage());
        loanRequest.validate();
        MeedlValidator.validateObjectInstance(loanRequest.getLoanReferralStatus(), LoanMessages.LOAN_REFERRAL_STATUS_CANNOT_BE_EMPTY.getMessage());
        if (!loanRequest.getLoanReferralStatus().equals(LoanReferralStatus.ACCEPTED)) {
            throw new LoanException(LoanMessages.LOAN_REFERRAL_STATUS_MUST_BE_ACCEPTED.getMessage());
        }
        loanRequest.setStatus(LoanRequestStatus.NEW);
        loanRequest.setCreatedDate(LocalDateTime.now());
        LoanRequest request = loanRequestOutputPort.save(loanRequest);
        log.info("Saved loan request: {}", request);
        updateLoanMetricsLoanRequestCount(request);
        return request;
    }

    private void updateLoanMetricsLoanRequestCount(LoanRequest loanRequest) throws MeedlException {
        Optional<OrganizationIdentity> organization =
                organizationIdentityOutputPort.findOrganizationByName(loanRequest.getReferredBy());
        if (organization.isEmpty()) {
            throw new EducationException(OrganizationMessages.ORGANIZATION_NOT_FOUND.getMessage());
        }
        LoanMetrics loanMetrics = new LoanMetrics();
        loanMetrics.setOrganizationId(organization.get().getId());
        loanMetrics.setLoanRequestCount(BigInteger.ONE.intValue());
        loanMetricsUseCase.save(loanMetrics);
    }

    @Override
    public LoanOffer createLoanOffer(LoanRequest loanRequest) throws MeedlException {
        log.info("Loan request input: {}", loanRequest);
        LoanOffer loanOffer = new LoanOffer();
        if (loanRequest.getStatus() != LoanRequestStatus.APPROVED) {
            throw new LoanException(LoanMessages.LOAN_REQUEST_MUST_HAVE_BEEN_APPROVED.getMessage());
        }
        loanOffer.setLoanRequest(loanRequest);
        loanOffer.setLoanOfferStatus(LoanOfferStatus.OFFERED);
        loanOffer.setDateTimeOffered(LocalDateTime.now());
        loanOffer.setLoanProduct(loanRequest.getLoanProduct());
        loanOffer.setAmountApproved(loanRequest.getLoanAmountApproved());
        Loanee loanee = loaneeOutputPort.findLoaneeById(loanRequest.getLoanee().getId());
        loanOffer.setLoanee(loanee);

        loanOffer = loanOfferOutputPort.save(loanOffer);
        log.info("Loan offer ID: {}", loanOffer.getId());
        Optional<OrganizationIdentity> organizationByName =
                organizationIdentityOutputPort.findOrganizationByName(loanRequest.getLoanee().getReferredBy());
        if (organizationByName.isEmpty()) {
            throw new MeedlException(OrganizationMessages.ORGANIZATION_NOT_FOUND.getMessage());
        }
        LoanMetrics loanMetrics = loanMetricsUseCase.save(
                LoanMetrics.builder().loanOfferCount(1).
                        organizationId(organizationByName.get().getId()).build());
        log.info("Saved loan metrics: {}", loanMetrics);
        return loanOffer;
    }

    @Override
    public LoaneeLoanAccount acceptLoanOffer(LoanOffer loanOffer) throws MeedlException {
        loanOffer.validateForAcceptOffer();
        LoanOffer offer = loanOfferOutputPort.findLoanOfferById(loanOffer.getId());
        Optional<Loanee> loanee = loaneeOutputPort.findByUserId(loanOffer.getUserId());
        if (loanee.isEmpty()) {
            throw new LoanException(LoanMessages.LOANEE_NOT_FOUND.getMessage());
        }
        if (!offer.getLoanee().getId().equals(loanee.get().getId())) {
            throw new LoanException(LoanMessages.LOAN_OFFER_NOT_ASSIGNED_TO_LOANEE.getMessage());
        }
        List<UserIdentity> portfolioManagers = userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER);
        if (loanOffer.getLoaneeResponse().equals(LoanDecision.ACCEPTED)){
            //Loanee Wallet would be Created
            loanOfferMapper.updateLoanOffer(offer,loanOffer);
            offer.setDateTimeAccepted(LocalDateTime.now());
            LoanProduct loanProduct = loanProductOutputPort.findById(offer.getLoanProduct().getId());
            offer.setLoanProduct(loanProduct);
            loanOfferOutputPort.save(offer);
            notifyPortfolioManager(portfolioManagers,loanOffer);
            LoaneeLoanAccount loaneeLoanAccount = loaneeLoanAccountOutputPort.findByLoaneeId(offer.getLoaneeId());
            if (ObjectUtils.isEmpty(loaneeLoanAccount)){
                loaneeLoanAccount = createLoaneeLoanAccount(offer.getLoaneeId());
            }
            return loaneeLoanAccount;
        }
        loanOfferMapper.updateLoanOffer(offer,loanOffer);
        loanOfferOutputPort.save(offer);
        notifyPortfolioManager(portfolioManagers,loanOffer);
        return null;
    }

    @Override
    public Page<LoanOffer> viewAllLoanOffersInOrganization(String organizationId, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId,OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        return loanOfferOutputPort.findLoanOfferInOrganization(organizationId, pageSize, pageNumber);
    }


    private void notifyPortfolioManager(List<UserIdentity> portfolioManagers, LoanOffer loanOffer) {
        //this and the template would be done on another branch
    }


    private LoaneeLoanAccount createLoaneeLoanAccount(String loaneeId) throws MeedlException {
        LoaneeLoanAccount loaneeLoanAccount = new LoaneeLoanAccount();
        loaneeLoanAccount.setLoaneeId(loaneeId);
        loaneeLoanAccount.setAccountStatus(AccountStatus.NEW);
        loaneeLoanAccount.setLoanStatus(LoanStatus.AWAITING_DISBURSAL);
        loaneeLoanAccount = loaneeLoanAccountOutputPort.save(loaneeLoanAccount);
        return loaneeLoanAccount;
    }

    @Override
    public Page<LoanOffer> viewAllLoanOffers(String userId,int pageSize , int pageNumber) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity.getRole().equals(IdentityRole.ORGANIZATION_ADMIN)){
           OrganizationEmployeeIdentity organizationEmployeeIdentity =
                   organizationEmployeeIdentityOutputPort.findByCreatedBy(userId);
            return loanOfferOutputPort.findLoanOfferInOrganization(organizationEmployeeIdentity.getOrganization(),
                   pageSize,pageNumber);
        }
        return loanOfferOutputPort.findAllLoanOffers(pageSize,pageNumber);
    }


    @Override
    public LoanOffer viewLoanOfferDetails(String actorId, String loanOfferId) throws MeedlException {
        MeedlValidator.validateUUID(loanOfferId);
        UserIdentity userIdentity = userIdentityOutputPort.findById(actorId);
        LoanOffer loanOffer =
                 loanOfferOutputPort.findLoanOfferById(loanOfferId);
        List<LoaneeLoanBreakdown> loaneeLoanBreakdowns =
                loaneeLoanBreakDownOutputPort.findAllByLoaneeId(loanOffer.getLoaneeId());
        log.info("Loanee loan breakdowns by loanee with ID: {}: {}", loanOffer.getLoaneeId(),
                loaneeLoanBreakdowns);
        loanOffer.setLoaneeBreakdown(loaneeLoanBreakdowns);
        Loanee loanee = loaneeOutputPort.findLoaneeById(loanOffer.getLoaneeId());
        if (userIdentity.getRole().equals(IdentityRole.LOANEE) &&
                ! loanee.getUserIdentity().getId().equals(userIdentity.getId())){
                throw new LoanOfferException(
                        LoanOfferMessages.LOAN_OFFER_IS_NOT_ASSIGNED_TO_LOANEE.getMessage());
            }
        return loanOffer;
    }

    @Override
    public Page<LoanDetail> searchLoan(LoanOffer loanOffer) throws MeedlException {
        MeedlValidator.validateUUID(loanOffer.getOrganizationId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateObjectName(loanOffer.getName(),LoaneeMessages.LOANEE_NAME_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanOffer.getProgramId(),ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validateObjectInstance(loanOffer.getType(),"Status cannot be empty");
        MeedlValidator.validatePageSize(loanOffer.getPageSize());
        MeedlValidator.validatePageNumber(loanOffer.getPageNumber());

        Program program = programOutputPort.findProgramById(loanOffer.getProgramId());
        OrganizationIdentity organizationIdentity = programOutputPort.findCreatorOrganization(program.getCreatedBy());
        if(!organizationIdentity.getId().equals(loanOffer.getOrganizationId())) {
            throw new LoanException("Program not in organization");
        }

        return searchResult(loanOffer);
    }

    private Page<LoanDetail> searchResult(LoanOffer loanOffer) throws MeedlException {
        Page<LoanDetail> loanDetails;
        if (loanOffer.getType().equals(LoanType.LOAN_OFFER)){
            Page<LoanOffer> loanOffers = loanOfferOutputPort.searchLoanOffer(loanOffer);
            loanDetails = loanOffers.map(loanMetricsMapper::mapLoanOfferToLoanLifeCycles);
            return loanDetails;
        }
        else if (loanOffer.getType().equals(LoanType.LOAN_REQUEST)){
            Page<LoanRequest> loanRequests = loanRequestOutputPort.searchLoanRequest(loanOffer.getProgramId(),
                    loanOffer.getOrganizationId(), loanOffer.getName(), loanOffer.getPageSize(), loanOffer.getPageNumber());
            loanDetails = loanRequests.map(loanMetricsMapper::mapLoanRequestToLoanLifeCycles);
            return loanDetails;
        }
        else if (loanOffer.getType().equals(LoanType.LOAN_DISBURSAL)){
            Page<Loan> loans = loanOutputPort.searchLoan(loanOffer.getProgramId(),
                    loanOffer.getOrganizationId(), loanOffer.getName(), loanOffer.getPageSize(), loanOffer.getPageNumber());
            loanDetails = loans.map(loanMetricsMapper::mapToLoans);
            return loanDetails;
        }
        throw new LoanException(loanOffer.getType().name()+" is not a loan type");
    }


    @Override
    public Page<LoanDetail> filterLoanByProgram(LoanOffer loanOffer) throws MeedlException {
        MeedlValidator.validateUUID(loanOffer.getOrganizationId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateUUID(loanOffer.getProgramId(),ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validateObjectInstance(loanOffer.getType(),"Status cannot be empty");
        MeedlValidator.validatePageSize(loanOffer.getPageSize());
        MeedlValidator.validatePageNumber(loanOffer.getPageNumber());

        Program program = programOutputPort.findProgramById(loanOffer.getProgramId());
        OrganizationIdentity organizationIdentity = programOutputPort.findCreatorOrganization(program.getCreatedBy());
        if(!organizationIdentity.getId().equals(loanOffer.getOrganizationId())) {
            throw new LoanException("Program not in organization");
        }

        return filterResult(loanOffer);
    }

    private Page<LoanDetail> filterResult(LoanOffer loanOffer) throws MeedlException {
        Page<LoanDetail> loanDetails;
        if (loanOffer.getType().equals(LoanType.LOAN_OFFER)){
            Page<LoanOffer> loanOffers = loanOfferOutputPort.filterLoanOfferByProgram(loanOffer);
            loanDetails = loanOffers.map(loanMetricsMapper::mapLoanOfferToLoanLifeCycles);
            return loanDetails;
        }
        else if (loanOffer.getType().equals(LoanType.LOAN_REQUEST)){
            Page<LoanRequest> loanRequests = loanRequestOutputPort.filterLoanRequestByProgram(loanOffer.getProgramId(),
                    loanOffer.getOrganizationId(),loanOffer.getPageSize(), loanOffer.getPageNumber());
            loanDetails = loanRequests.map(loanMetricsMapper::mapLoanRequestToLoanLifeCycles);
            return loanDetails;
        }
        else if (loanOffer.getType().equals(LoanType.LOAN_DISBURSAL)){
            Page<Loan> loans = loanOutputPort.filterLoanByProgram(loanOffer.getProgramId(),
                    loanOffer.getOrganizationId(), loanOffer.getPageSize(), loanOffer.getPageNumber());
            loanDetails = loans.map(loanMetricsMapper::mapToLoans);
            return loanDetails;
        }
        throw new LoanException(loanOffer.getType().name()+" is not a loan type");
    }


}
