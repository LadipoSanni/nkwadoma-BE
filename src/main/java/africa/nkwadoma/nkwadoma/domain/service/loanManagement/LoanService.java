package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
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
    private final LoanProductMapper loanProductMapper;
    private final LoanRequestMapper loanRequestMapper;
    private final LoanRequestOutputPort loanRequestOutputPort;
    private final IdentityManagerOutputPort identityManagerOutPutPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoanReferralOutputPort loanReferralOutputPort;
    private final LoanOfferOutputPort loanOfferOutputPort;
    private final LoanOfferMapper loanOfferMapper;
    private final LoanOutputPort loanOutputPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final LoaneeLoanAccountOutputPort loaneeLoanAccountOutputPort;


    @Override
    public LoanProduct createLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        loanProduct.validateLoanProductDetails();
        UserIdentity foundUser = userIdentityOutputPort.findById(loanProduct.getCreatedBy());
        identityManagerOutPutPort.verifyUserExistsAndIsEnabled(foundUser);
        log.info("Loan product {} created successfully", loanProduct.getName());
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
        MeedlValidator.validateObjectInstance(loan);
        MeedlValidator.validateUUID(loan.getLoaneeId(), "Please provide a valid loanee identification");
        Loanee foundLoanee = loaneeOutputPort.findByUserId(loan.getLoaneeId())
                            .orElseThrow(() -> new LoanException(LoanMessages.LOANEE_NOT_FOUND.getMessage()));
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

    private String getLoanAccountId(Loanee foundLoanee) {
        return null;
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
        if (ObjectUtils.isEmpty(loanReferral)) {
            log.info("Empty Loan referral returned");
            throw new LoanException(LoanMessages.LOAN_REFERRAL_NOT_FOUND.getMessage());
        }
        Optional<LoanReferral> loanReferralById = loanReferralOutputPort.
                findLoanReferralById(loanReferral.getId());
        if (loanReferralById.isEmpty()) {
            throw new LoanException(LoanMessages.LOAN_REFERRAL_NOT_FOUND.getMessage());
        }
        log.info("Found Loan referral by it's ID: {}", loanReferralById.get());
        return loanReferralById.get();

    }

    @Override
    public LoanReferral respondToLoanReferral(LoanReferral loanReferral) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanReferral, LoanMessages.LOAN_REFERRAL_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(loanReferral.getLoanReferralStatus(),
                LoanMessages.LOAN_REFERRAL_STATUS_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateLoanDecision(loanReferral.getLoanReferralStatus().name());
        LoanReferral foundLoanReferral = loanReferralOutputPort.findById(loanReferral.getId());
        log.info("Found Loan Referral: {}", foundLoanReferral);
        foundLoanReferral = updateLoanReferral(loanReferral, foundLoanReferral);
        return foundLoanReferral;
    }

    private LoanReferral updateLoanReferral(LoanReferral loanReferral, LoanReferral foundLoanReferral) throws MeedlException {
        if (loanReferral.getLoanReferralStatus().equals(LoanReferralStatus.ACCEPTED)) {
            LoanRequest loanRequest = loanRequestMapper.mapLoanReferralToLoanRequest(foundLoanReferral);
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
        loanRequest.setStatus(LoanRequestStatus.NEW);
        loanRequest.setCreatedDate(LocalDateTime.now());
        return loanRequestOutputPort.save(loanRequest);
    }

    @Override
    public LoanOffer createLoanOffer(LoanRequest loanRequest) throws MeedlException {
        LoanOffer loanOffer = new LoanOffer();
        if (loanRequest.getStatus() != LoanRequestStatus.APPROVED){
            throw new LoanException(LoanMessages.LOAN_REQUEST_MUST_HAVE_BEEN_APPROVED.getMessage());
        }
        loanOffer.setLoanRequest(loanRequest);
        loanOffer.setLoanOfferStatus(LoanOfferStatus.OFFERED);
        loanOffer.setDateTimeOffered(LocalDateTime.now());
        loanOffer.setLoanProduct(loanRequest.getLoanProduct());
        loanOffer = loanOfferOutputPort.save(loanOffer);
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
        loanOffer.setLoaneeId(loanee.get().getId());
        if (!offer.getLoanee().getId().equals(loanOffer.getLoaneeId())){
            throw new LoanException(LoanMessages.LOAN_OFFER_NOT_ASSIGNED_TO_LOANEE.getMessage());
        }
        List<UserIdentity> portfolioManagers = userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER);
        if (loanOffer.getLoaneeResponse().equals(LoanDecision.ACCEPTED)){
            //Loanee Wallet would be Created
            loanOfferMapper.updateLoanOffer(offer,loanOffer);
            offer.setDateTimeAccepted(LocalDateTime.now());
            loanOfferOutputPort.save(offer);
            notifyPortfolioManager(portfolioManagers,loanOffer);
            LoaneeLoanAccount loaneeLoanAccount = loaneeLoanAccountOutputPort.findByLoaneeId(loanOffer.getLoaneeId());
            if (ObjectUtils.isEmpty(loaneeLoanAccount)){
                loaneeLoanAccount = createLoaneeLoanAccount(loanOffer.getLoaneeId());
            }
            return loaneeLoanAccount;
        }
        loanOfferMapper.updateLoanOffer(offer,loanOffer);
        loanOfferOutputPort.save(offer);
        notifyPortfolioManager(portfolioManagers,loanOffer);
        return null;
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
}
