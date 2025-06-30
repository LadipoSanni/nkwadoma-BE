package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceNotFoundException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoanOfferMapper;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@EnableAsync
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
    private final LoanMetricsOutputPort loanMetricsOutputPort;
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;
    private final CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    private final CohortOutputPort cohortOutputPort;

    @Override
    public LoanProduct createLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct, LoanMessages.INVALID_LOAN_PRODUCT_REQUEST_DETAILS.getMessage());
        loanProduct.validateLoanProductDetails();
        UserIdentity foundUser = userIdentityOutputPort.findById(loanProduct.getCreatedBy());
        identityManagerOutPutPort.verifyUserExistsAndIsEnabled(foundUser);
        log.info("The user with {} email has been verified ", foundUser.getEmail());
        if (loanProductOutputPort.existsByNameIgnoreCase(loanProduct.getName())){
            log.error("Loan product {} already exists", loanProduct.getName() );
            throw new LoanException("Loan product " + loanProduct.getName() + " already exists");
        }
        log.info("Searching for investment vehicle with id {} ", loanProduct.getInvestmentVehicleId());
        InvestmentVehicle investmentVehicle = checkProductSizeNotMoreThanAvailableInvestmentAmount(loanProduct);
        //TODO Coming back to add restriction for available amount
//      TODO  investmentVehicle.setTotalAvailableAmount(investmentVehicle.getTotalAvailableAmount().subtract(loanProduct.getLoanProductSize()));
        loanProduct.addInvestmentVehicleValues(investmentVehicle);
        loanProduct.setTotalAmountAvailable(loanProduct.getLoanProductSize());
        log.info("Loan product to be saved in create loan product service method {}", loanProduct);
        investmentVehicleOutputPort.save(investmentVehicle);
        return loanProductOutputPort.save(loanProduct);
    }

    private InvestmentVehicle checkProductSizeNotMoreThanAvailableInvestmentAmount(LoanProduct loanProduct) throws MeedlException {
        InvestmentVehicle investmentVehicle =
                investmentVehicleOutputPort.findById(loanProduct.getInvestmentVehicleId());
        log.info("Loan product size is : {}", loanProduct.getLoanProductSize());
        log.info("Investment vehicle available balance is : {}", investmentVehicle.getTotalAvailableAmount());
//        if (loanProduct.getLoanProductSize().compareTo(investmentVehicle.getTotalAvailableAmount()) > BigInteger.ZERO.intValue()) {
//            log.warn("Attempt to create loan product that exceeds the investment vehicle available amount.");
//            throw new MeedlException("Loan product size cannot be greater than investment vehicle available amount.");
//        }
        return investmentVehicle;
    }

    @Override
    public void deleteLoanProductById(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct, LoanMessages.LOAN_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanProduct.getId(), LoanMessages.INVALID_LOAN_PRODUCT_ID.getMessage());
        loanProductOutputPort.deleteById(loanProduct.getId());
    }

    @Override
    public Page<LoanProduct> viewAllLoanProduct(LoanProduct loanProduct) {
        return loanProductOutputPort.findAllLoanProduct(loanProduct);
    }

    @Override
    public Page<LoanProduct> search(String loanProductName, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateDataElement(loanProductName, "Loan product name is required");
        return loanProductOutputPort.search(loanProductName,pageSize,pageNumber);
    }



    @Override
    public LoanProduct updateLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct, LoanMessages.LOAN_PRODUCT_NAME_REQUIRED.getMessage());
        MeedlValidator.validateUUID(loanProduct.getId(), LoanMessages.INVALID_LOAN_PRODUCT_ID.getMessage());
        LoanProduct foundLoanProduct = loanProductOutputPort.findById(loanProduct.getId());
        if (foundLoanProduct.getTotalNumberOfLoanee() > BigInteger.ZERO.intValue()) {
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
        MeedlValidator.validateUUID(loan.getLoaneeId(), LoaneeMessages.PLEASE_PROVIDE_A_VALID_LOANEE_IDENTIFICATION.getMessage());
        Loanee foundLoanee = loaneeOutputPort.findLoaneeById(loan.getLoaneeId());
        LoanOffer loanOffer = loanOfferOutputPort.findLoanOfferById(loan.getLoanOfferId());
        if (loanOffer.getLoaneeResponse().equals(LoanDecision.DECLINED)){
            throw new LoanException(LoanMessages.CANNOT_START_LOAN_FOR_LOAN_OFFER_THAT_AS_BEEN_DECLINED.getMessage());
        }
        Optional<Loan> foundLoan = loanOutputPort.findLoanByLoanOfferId(loanOffer.getId());
        if (foundLoan.isPresent()) {
            throw new LoanException(LoanMessages.LOAN_ALREADY_EXISTS_FOR_THIS_LOANEE.getMessage());
        }
        if (loan.getStartDate() != null){
            loan = loan.buildLoan(foundLoanee, getLoanAccountId(foundLoanee),loan.getLoanOfferId(), loan.getStartDate());
        }else {
            loan = loan.buildLoan(foundLoanee, getLoanAccountId(foundLoanee), loan.getLoanOfferId());
        }
        Loan savedLoan = loanOutputPort.save(loan);
        log.info("Saved loan: {}", savedLoan);
        updateCohortLoanDetail(loanOffer);
        String referBy = loanOutputPort.findLoanReferal(savedLoan.getId());
        updateLoanDisbursalOnLoamMatrics(referBy);
        updateInvestmentVehicleTalentFunded(savedLoan);
        return savedLoan;
    }

    private void updateCohortLoanDetail(LoanOffer loanOffer) throws MeedlException {
        CohortLoanDetail cohortLoanDetail = cohortLoanDetailOutputPort.findByCohortId(loanOffer.getCohortId());
        log.info("current total amount received for cohort {}",cohortLoanDetail.getTotalAmountRequested());
        log.info("loanee amount disbursed {}", loanOffer.getAmountApproved());
        cohortLoanDetail.setTotalAmountReceived(cohortLoanDetail.getTotalAmountRequested().
                add(loanOffer.getAmountApproved()));
        cohortLoanDetail.setTotalOutstandingAmount(cohortLoanDetail.getTotalOutstandingAmount().
                add(loanOffer.getAmountApproved()));
        cohortLoanDetail = cohortLoanDetailOutputPort.save(cohortLoanDetail);
        log.info("total amount received updated for cohort after adding == {} is {}",
                cohortLoanDetail.getTotalAmountReceived(), loanOffer.getAmountApproved());
        log.info("total amount outstanding updated for cohort after adding == {} is {}",
                cohortLoanDetail.getTotalOutstandingAmount(), loanOffer.getAmountApproved());
    }

    private void updateInvestmentVehicleTalentFunded(Loan savedLoan) throws MeedlException {
        InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findInvestmentVehicleByLoanOfferId(savedLoan.getLoanOfferId());
        investmentVehicle.setTalentFunded(
                investmentVehicle.getTalentFunded() + 1
        );
        investmentVehicleOutputPort.save(investmentVehicle);
    }

    private void updateLoanDisbursalOnLoamMatrics(String referBy) throws MeedlException {
        Optional<OrganizationIdentity> organizationByName =
                organizationIdentityOutputPort.findOrganizationByName(referBy);
        if (organizationByName.isEmpty()) {
            throw new ResourceNotFoundException(OrganizationMessages.ORGANIZATION_NOT_FOUND.getMessage());
        }
        Optional<LoanMetrics> loanMetrics =
                loanMetricsOutputPort.findByOrganizationId(organizationByName.get().getId());
        if (loanMetrics.isEmpty()) {
            throw new LoanException("Organization has no loan metrics");
        }
        loanMetrics.get().setLoanDisbursalCount(
                loanMetrics.get().getLoanDisbursalCount() + 1
        );
        loanMetricsOutputPort.save(loanMetrics.get());
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
                loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(foundLoan.getLoaneeId());
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
        log.info("Found Loan referral by it's ID: {}, is verified : {}", loanReferral.getId(), loanReferral.getLoanee().getUserIdentity().isIdentityVerified());
        List<LoaneeLoanBreakdown> loaneeLoanBreakdowns =
                loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(loanReferral.getLoanee().getId());
        log.info("Loanee loan breakdowns found from the DB : {}", loaneeLoanBreakdowns);
        loanReferral.setLoaneeLoanBreakdowns(loaneeLoanBreakdowns);
        log.info("Loanee loan breakdowns set to be returned: {}", loanReferral.getLoaneeLoanBreakdowns());
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
            throw new LoanException(LoanMessages.LOAN_REFERRAL_HAS_ALREADY_BEEN_ACCEPTED.getMessage());
        }
        else if (foundLoanReferral.getLoanReferralStatus().equals(LoanReferralStatus.UNAUTHORIZED)) {
            throw new LoanException(LoanMessages.LOAN_REFERRAL_HAS_ALREADY_BEEN_DECLINED.getMessage());
        }
    }

    private LoanReferral updateLoanReferral(LoanReferral loanReferral, LoanReferral foundLoanReferral) throws MeedlException {
        if (loanReferral.getLoanReferralStatus().equals(LoanReferralStatus.ACCEPTED)) {
            log.info("found loan referral == {}", foundLoanReferral );
            LoanRequest loanRequest = loanRequestMapper.mapLoanReferralToLoanRequest(foundLoanReferral);
            loanRequest.setStatus(LoanRequestStatus.NEW);
            loanRequest.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
            log.info("Mapped loan request: {}", loanRequest);
            loanRequest = createLoanRequest(loanRequest);
            log.info("Created loan request: {}", loanRequest);
            updateNumberOfLoanRequestOnCohort(loanReferral);

            foundLoanReferral.setLoanReferralStatus(LoanReferralStatus.AUTHORIZED);
        }
        else if (loanReferral.getLoanReferralStatus().equals(LoanReferralStatus.DECLINED)) {
            foundLoanReferral.setReasonForDeclining(loanReferral.getReasonForDeclining());
            foundLoanReferral.setLoanReferralStatus(LoanReferralStatus.UNAUTHORIZED);
        }
        foundLoanReferral = loanReferralOutputPort.save(foundLoanReferral);
        log.info("Updated loan referral: {}", foundLoanReferral);
        updateLoanReferralOnMetrics(foundLoanReferral);
        return foundLoanReferral;
    }

    private void updateNumberOfLoanRequestOnCohort(LoanReferral loanReferral) throws MeedlException {
        Cohort cohort = loanReferral.getCohortLoanee().getCohort();
        log.info("found cohort == {}",cohort);
        log.info("current number of loan request == {}",cohort.getNumberOfLoanRequest());
        cohort.setNumberOfLoanRequest(cohort.getNumberOfLoanRequest() + 1);
        cohort = cohortOutputPort.save(cohort);
        log.info(" number of loan request after adding 1 == {}",cohort.getNumberOfLoanRequest());
    }

    private void updateLoanReferralOnMetrics(LoanReferral foundLoanReferral) throws MeedlException {
        log.info("org name ====  {}",foundLoanReferral.getCohortLoanee().getReferredBy());
        Optional<OrganizationIdentity> organization =
                organizationIdentityOutputPort.findOrganizationByName(foundLoanReferral.getCohortLoanee().getReferredBy());
        if (organization.isEmpty()) {
            throw new LoanException(OrganizationMessages.ORGANIZATION_NOT_FOUND.getMessage());
        }
        Optional<LoanMetrics> loanMetrics =
                loanMetricsOutputPort.findByOrganizationId(organization.get().getId());
        if (loanMetrics.isEmpty()) {
            throw new LoanException("Organization has no loan metrics");
        }
        loanMetrics.get().setLoanReferralCount(
                loanMetrics.get().getLoanReferralCount() - 1
        );
        loanMetricsOutputPort.save(loanMetrics.get());
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
        log.info("About to increase loan request count on metrics : {}", request);
        increaseLoanRequestOnLoanMetrics(request.getReferredBy());
        return request;
    }

    private void increaseLoanRequestOnLoanMetrics(String refferBy) throws MeedlException {
        log.info("org name ====  {}",refferBy);
        Optional<OrganizationIdentity> organization =
                organizationIdentityOutputPort.findOrganizationByName(refferBy);
        if (organization.isEmpty()) {
            throw new LoanException(OrganizationMessages.ORGANIZATION_NOT_FOUND.getMessage());
        }
        Optional<LoanMetrics> loanMetrics =
                loanMetricsOutputPort.findByOrganizationId(organization.get().getId());
        if (loanMetrics.isEmpty()) {
            throw new LoanException("Organization has no loan metrics");
        }
        loanMetrics.get().setLoanRequestCount(
                loanMetrics.get().getLoanRequestCount() + 1
        );
        loanMetricsOutputPort.save(loanMetrics.get());
    }

    @Override
    public LoanOffer createLoanOffer(LoanRequest loanRequest) throws MeedlException {
        log.info("Loan request input: {}", loanRequest);
        LoanOffer loanOffer = new LoanOffer();
        if (loanRequest.getStatus() != LoanRequestStatus.APPROVED) {
            throw new LoanException(LoanMessages.LOAN_REQUEST_MUST_HAVE_BEEN_APPROVED.getMessage());
        }

        loanOffer.setLoanOfferStatus(LoanOfferStatus.OFFERED);
        loanOffer.setDateTimeOffered(LocalDateTime.now());
        loanOffer.setLoanProduct(loanRequest.getLoanProduct());
        loanOffer.setId(loanRequest.getId());
        loanOffer.setAmountApproved(loanRequest.getLoanAmountApproved());

        loanOffer = loanOfferOutputPort.save(loanOffer);
        log.info("Loan offer ID: {}", loanOffer.getId());
        increaseLoanOfferOnLoanMetrics(loanRequest);
        return loanOffer;
    }

    private void increaseLoanOfferOnLoanMetrics(LoanRequest loanRequest) throws MeedlException {
        Optional<OrganizationIdentity> organizationByName =
                organizationIdentityOutputPort.findOrganizationByName(loanRequest.getReferredBy());
        if (organizationByName.isEmpty()) {
            throw new ResourceNotFoundException(OrganizationMessages.ORGANIZATION_NOT_FOUND.getMessage());
        }
        Optional<LoanMetrics> loanMetrics =
                loanMetricsOutputPort.findByOrganizationId(organizationByName.get().getId());
        if (loanMetrics.isEmpty()) {
            throw new ResourceNotFoundException("Organization has no loan metrics");
        }
        loanMetrics.get().setLoanOfferCount(
                loanMetrics.get().getLoanOfferCount() + 1
        );
        loanMetricsOutputPort.save(loanMetrics.get());
    }

    @Override
    public LoaneeLoanAccount acceptLoanOffer(LoanOffer loanOffer) throws MeedlException {
        loanOffer.validateForAcceptOffer();
        log.info("Loan offer identity validated : {}", loanOffer);
        LoanOffer offer = loanOfferOutputPort.findLoanOfferById(loanOffer.getId());
        String referBy = offer.getLoanRequestReferredBy();
        log.info("found Loan offer : {}", offer);
        Optional<Loanee> optionalLoanee = loaneeOutputPort.findByUserId(loanOffer.getUserId());
        log.info("Loan offer: {}", loanOffer);
        if (optionalLoanee.isEmpty()) {
            log.info("Loanee is empty : {}", loanOffer);
            throw new ResourceNotFoundException(LoanMessages.LOANEE_NOT_FOUND.getMessage());
        }
        Loanee loanee = optionalLoanee.get();
        if (!offer.getLoanee().getId().equals(loanee.getId())) {
            log.info("offer not assigned to loanee: {}", loanOffer);
            throw new LoanException(LoanMessages.LOAN_OFFER_NOT_ASSIGNED_TO_LOANEE.getMessage());
        }
        if (ObjectUtils.isNotEmpty(offer.getLoaneeResponse())) {
            log.info("decision made previously : {}", loanOffer);
            throw new LoanException(LoanMessages.LOAN_OFFER_DECISION_MADE.getMessage());
        }

        if (loanOffer.getLoaneeResponse().equals(LoanDecision.ACCEPTED)){
            log.info("accept offer abt to start : {}", loanOffer);
            return acceptLoanOffer(loanee.getUserIdentity(), loanOffer, offer,referBy);
        }
        decreaseLoanOfferOnLoanMetrics(referBy);
        declineLoanOffer(loanee.getUserIdentity(), loanOffer, offer);
        return null;
    }

    private void declineLoanOffer(UserIdentity userIdentity, LoanOffer loanOffer, LoanOffer offer) throws MeedlException {
        loanOfferMapper.updateLoanOffer(offer, loanOffer);
        loanOfferOutputPort.save(offer);
        notifyPortfolioManager(offer, userIdentity);
    }

    private LoaneeLoanAccount acceptLoanOffer(UserIdentity userIdentity, LoanOffer loanOffer, LoanOffer offer,String referBy) throws MeedlException {
        log.info("got into accept method: {}", loanOffer);
        //Loanee Wallet would be Created
        loanOfferMapper.updateLoanOffer(offer, loanOffer);
        offer.setDateTimeAccepted(LocalDateTime.now());
        LoanProduct loanProduct = loanProductOutputPort.findById(offer.getLoanProduct().getId());
        log.info("loanProduct found : {}", loanProduct);
        offer.setLoanProduct(loanProduct);
        loanOfferOutputPort.save(offer);
        log.info("after saving offer : {}", offer);
        notifyPortfolioManager(offer, userIdentity);
        log.info("Loanee account abt to create : {}", loanOffer);
        LoaneeLoanAccount loaneeLoanAccount = loaneeLoanAccountOutputPort.findByLoaneeId(offer.getLoaneeId());
        log.info("Loanee account is found : {}", loaneeLoanAccount);
        if (ObjectUtils.isEmpty(loaneeLoanAccount)){
            log.info("Loanee account is abt to be created : {}", loaneeLoanAccount);
            loaneeLoanAccount = createLoaneeLoanAccount(offer.getLoaneeId());
            log.info("Loanee account is created : {}", loaneeLoanAccount);
        }
        decreaseLoanOfferOnLoanMetrics(referBy);
        log.info("done decreasing  : {}", offer);
        return loaneeLoanAccount;
    }

    private void decreaseLoanOfferOnLoanMetrics(String referBy) throws MeedlException {
        Optional<OrganizationIdentity> organizationByName =
                organizationIdentityOutputPort.findOrganizationByName(referBy);
        if (organizationByName.isEmpty()) {
            throw new ResourceNotFoundException(OrganizationMessages.ORGANIZATION_NOT_FOUND.getMessage());
        }
        Optional<LoanMetrics> loanMetrics =
                loanMetricsOutputPort.findByOrganizationId(organizationByName.get().getId());
        if (loanMetrics.isEmpty()) {
            throw new ResourceNotFoundException("Organization has no loan metrics");
        }
        loanMetrics.get().setLoanOfferCount(
                loanMetrics.get().getLoanOfferCount() - 1
        );
        loanMetricsOutputPort.save(loanMetrics.get());
    }

    @Override
    public Page<LoanOffer> viewAllLoanOffersInOrganization(String organizationId, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId,OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        return loanOfferOutputPort.findLoanOfferInOrganization(organizationId, pageSize, pageNumber);
    }


    private void notifyPortfolioManager(LoanOffer loanOffer, UserIdentity userIdentity) throws MeedlException {
        asynchronousNotificationOutputPort.notifyPortfolioManagerOfNewLoanOfferWithDecision(loanOffer, userIdentity);
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
        MeedlValidator.validateUUID(loanOfferId, LoanOfferMessages.INVALID_LOAN_OFFER_ID.getMessage());
        UserIdentity userIdentity = userIdentityOutputPort.findById(actorId);
        LoanOffer loanOffer =
                 loanOfferOutputPort.findLoanOfferById(loanOfferId);
        List<LoaneeLoanBreakdown> loaneeLoanBreakdowns =
                loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(loanOffer.getLoaneeId());
        log.info("Loanee loan breakdowns by loanee with ID: {}: {}", loanOffer.getLoaneeId(),
                loaneeLoanBreakdowns);
        loanOffer.setLoaneeBreakdown(loaneeLoanBreakdowns);
        Loanee loanee = loaneeOutputPort.findLoaneeById(loanOffer.getLoaneeId());
        if (userIdentity.getRole().equals(IdentityRole.LOANEE) &&
                ! loanee.getUserIdentity().getId().equals(userIdentity.getId())){
                throw new LoanException(
                        LoanOfferMessages.LOAN_OFFER_IS_NOT_ASSIGNED_TO_LOANEE.getMessage());
            }
        return loanOffer;
    }

    @Override
    public Page<LoanDetail> searchLoan(LoanOffer loanOffer) throws MeedlException {
        MeedlValidator.validateUUID(loanOffer.getOrganizationId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateObjectName(loanOffer.getName(),LoaneeMessages.LOANEE_NAME_CANNOT_BE_EMPTY.getMessage(),"Loan offer");
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
