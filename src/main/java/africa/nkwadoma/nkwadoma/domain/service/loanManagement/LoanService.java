package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
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
    private final LoanOutputPort loanOutputPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;


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
        if (foundLoanProduct.getTotalNumberOfLoanees() > BigInteger.ZERO.intValue()){
            throw new LoanException("Loan product " + foundLoanProduct.getName() + " cannot be updated as it has already been loaned out");
        }
        foundLoanProduct = loanProductMapper.updateLoanProduct(foundLoanProduct,loanProduct);
        foundLoanProduct.setUpdatedAt(LocalDateTime.now());
        log.info("Loan product updated {}",  foundLoanProduct);

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
        MeedlValidator.validateObjectInstance(loanReferral);
        loanReferral.validateViewLoanReferral();
        List<LoanReferral> foundLoanReferrals = loanReferralOutputPort.findLoanReferralByUserId(
                loanReferral.getLoanee().getUserIdentity().getId());
        if (foundLoanReferrals.isEmpty()) {
            throw new LoanException(LoanMessages.LOAN_REFERRAL_NOT_FOUND.getMessage());
        } else if (foundLoanReferrals.size() > 1){
            throw new LoanException("Multiple loan referrals is currently not allowed");
        } else {
            return getLoanReferral(foundLoanReferrals);
        }
    }

    private LoanReferral getLoanReferral(List<LoanReferral> foundLoanReferrals) throws MeedlException {
        LoanReferral loanReferral = foundLoanReferrals.get(0);
        if (ObjectUtils.isEmpty(loanReferral)) {
            throw new LoanException(LoanMessages.LOAN_REFERRAL_NOT_FOUND.getMessage());
        }
        Optional<LoanReferral> loanReferralById = loanReferralOutputPort.
                findLoanReferralById(loanReferral.getId());
        if (loanReferralById.isEmpty()) {
            throw new LoanException(LoanMessages.LOAN_REFERRAL_NOT_FOUND.getMessage());
        }
        return loanReferralById.get();
    }

    @Override
    public LoanReferral respondToLoanReferral(LoanReferral loanReferral) throws MeedlException {
        LoanReferral foundLoanReferral = viewLoanReferral(loanReferral);
        MeedlValidator.validateObjectInstance(loanReferral.getLoanReferralStatus());
        if (ObjectUtils.isEmpty(foundLoanReferral.getLoanee().getUserIdentity().getAlternateEmail())
                || ObjectUtils.isEmpty(foundLoanReferral.getLoanee().getUserIdentity().getAlternatePhoneNumber())
                || ObjectUtils.isEmpty(foundLoanReferral.getLoanee().getUserIdentity().getAlternateContactAddress())
        ) {
            throw new LoanException(LoanMessages.ADDITIONAL_DETAILS_REQUIRED.getMessage());
        }
        if (loanReferral.getLoanReferralStatus().equals(LoanReferralStatus.ACCEPTED)) {
            LoanRequest loanRequest = loanRequestMapper.mapLoanReferralToLoanRequest(foundLoanReferral);
            createLoanRequest(loanRequest);
            foundLoanReferral.setLoanReferralStatus(LoanReferralStatus.AUTHORIZED);
            foundLoanReferral = loanReferralOutputPort.saveLoanReferral(foundLoanReferral);
        }
        return foundLoanReferral;
    }

    public LoanRequest createLoanRequest(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest);
        loanRequest.validate();
        MeedlValidator.validateObjectInstance(loanRequest.getLoanReferralStatus());
        if (!loanRequest.getLoanReferralStatus().equals(LoanReferralStatus.ACCEPTED)) {
            throw new LoanException(LoanMessages.LOAN_REFERRAL_STATUS_MUST_BE_ACCEPTED.getMessage());
        }
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
    public Page<LoanOffer> viewAllLoanOffers(String userId,int pageSize , int pageNumber) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity.getRole().equals(IdentityRole.ORGANIZATION_ADMIN)){
           OrganizationEmployeeIdentity organizationEmployeeIdentity =
                   organizationEmployeeIdentityOutputPort.findByCreatedBy(userId);
           Page<LoanOffer> loanOffers = loanOfferOutputPort.findLoanOfferInOrganization(organizationEmployeeIdentity.getOrganization(),
                   pageSize,pageNumber);
        }
        return null;
    }
}
