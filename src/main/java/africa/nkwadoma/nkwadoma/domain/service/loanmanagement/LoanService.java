package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.LoanUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceNotFoundException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanDetail;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoanOfferMapper;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages.LOAN_DECISION;

@RequiredArgsConstructor
@Slf4j
@EnableAsync
@Service
public class LoanService implements CreateLoanProductUseCase, ViewLoanProductUseCase, ViewLoanReferralsUseCase,
        RespondToLoanReferralUseCase, LoanOfferUseCase, LoanUseCase {
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
    private final ProgramLoanDetailOutputPort programLoanDetailOutputPort;
    private final OrganizationLoanDetailOutputPort organizationLoanDetailOutputPort;
    private final LoaneeUseCase loaneeUseCase;
    private final LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private final LoanMapper loanMapper;
    private final LoaneeLoanAggregateOutputPort loaneeLoanAggregateOutputPort;

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
  //    TODO  investmentVehicle.setTotalAvailableAmount(investmentVehicle.getTotalAvailableAmount().subtract(loanProduct.getLoanProductSize()));
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
        MeedlValidator.validateDataElement(loanProductName, LoanMessages.LOAN_PRODUCT_NAME_REQUIRED.getMessage());
        return loanProductOutputPort.search(loanProductName,pageSize,pageNumber);
    }



    @Override
    public LoanProduct updateLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct, LoanMessages.LOAN_PRODUCT_REQUIRED.getMessage());
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
        log.info("------> loan---> {}", loan);
        MeedlValidator.validateObjectInstance(loan, LoanMessages.LOAN_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loan.getLoaneeId(), LoaneeMessages.PLEASE_PROVIDE_A_VALID_LOANEE_IDENTIFICATION.getMessage());
        Loanee foundLoanee = loaneeOutputPort.findLoaneeById(loan.getLoaneeId());
        LoanOffer loanOffer = loanOfferOutputPort.findLoanOfferById(loan.getLoanOfferId());
        log.info("-----> Loan offer ----> {}", loanOffer);
        log.info("-----> offer response ----> {}", loanOffer.getLoaneeResponse());
        if (loanOffer.getLoaneeResponse() == null) {
            log.info("Loanee response is null");
            throw new LoanException(LOAN_DECISION.getMessage());
        }
        if (loanOffer.getLoaneeResponse().equals(LoanDecision.DECLINED)){
            log.error("{}", LoanMessages.CANNOT_START_LOAN_FOR_LOAN_OFFER_THAT_AS_BEEN_DECLINED.getMessage());
            throw new LoanException(LoanMessages.CANNOT_START_LOAN_FOR_LOAN_OFFER_THAT_AS_BEEN_DECLINED.getMessage());
        }
        Optional<Loan> foundLoan = loanOutputPort.findLoanByLoanOfferId(loanOffer.getId());
        if (foundLoan.isPresent()) {
            log.error("{}", LoanMessages.LOAN_ALREADY_EXISTS_FOR_THIS_LOANEE.getMessage());
            throw new LoanException(LoanMessages.LOAN_ALREADY_EXISTS_FOR_THIS_LOANEE.getMessage());
        }
        if (loan.getStartDate() != null){
            log.info("Loan start date was provided as {}", loan.getStartDate());
            loan = loan.buildLoan(foundLoanee, getLoanAccountId(foundLoanee),loan.getLoanOfferId(), loan.getStartDate());
        }else {
            log.info("Loan start date wasn't provided");
            loan = loan.buildLoan(foundLoanee, getLoanAccountId(foundLoanee), loan.getLoanOfferId());
        }

        Loan savedLoan = loanOutputPort.save(loan);
        log.info("Saved loan: {}", savedLoan);
        updateLoanDetail(foundLoanee,loanOffer,savedLoan.getStartDate());
        String referBy = loanOutputPort.findLoanReferal(savedLoan.getId());
        updateLoanDisbursalOnLoamMatrics(referBy);
        updateInvestmentVehicleTalentFunded(savedLoan);
        return savedLoan;
    }

    private void updateLoanDetail(Loanee loanee ,LoanOffer loanOffer,LocalDateTime localDateTime) throws MeedlException {

        updateLoaneeLoanAggregate(loanee, loanOffer);

        updateLoaneeLoanDetail(loanOffer,localDateTime);

        updateCohortLoanDetail(loanOffer);

        Cohort cohort = cohortOutputPort.findCohortById(loanOffer.getCohortId());

        updateProgramDetail(loanOffer, cohort);

        updateOrganizationLoanDetail(loanOffer, cohort);
    }

    private void updateLoaneeLoanAggregate(Loanee loanee, LoanOffer loanOffer) throws MeedlException {
        LoaneeLoanAggregate loaneeLoanAggregate = loaneeLoanAggregateOutputPort.findByLoaneeId(loanee.getId());
        loaneeLoanAggregate.setNumberOfLoans(loaneeLoanAggregate.getNumberOfLoans() + 1);
        loaneeLoanAggregate.setHistoricalDebt(loaneeLoanAggregate.getHistoricalDebt().add(loanOffer.getAmountApproved()));
        loaneeLoanAggregate.setTotalAmountOutstanding(loaneeLoanAggregate.getTotalAmountOutstanding().add(loanOffer.getAmountApproved()));
        loaneeLoanAggregateOutputPort.save(loaneeLoanAggregate);
    }

    private void updateOrganizationLoanDetail(LoanOffer loanOffer, Cohort cohort) throws MeedlException {
        OrganizationLoanDetail organizationLoanDetail = organizationLoanDetailOutputPort.findByOrganizationId(cohort.getOrganizationId());
        log.info("current total amount received for organization  {}",organizationLoanDetail.getAmountReceived());
        log.info("loanee amount disbursed {}", loanOffer.getAmountApproved());
        organizationLoanDetail.setAmountReceived(organizationLoanDetail.getAmountReceived()
                .add(loanOffer.getAmountApproved()));
        organizationLoanDetail.setOutstandingAmount(organizationLoanDetail.getOutstandingAmount()
                .add(loanOffer.getAmountApproved()));
        organizationLoanDetailOutputPort.save(organizationLoanDetail);
    }

    private void updateProgramDetail(LoanOffer loanOffer, Cohort cohort) throws MeedlException {
        ProgramLoanDetail programLoanDetail = programLoanDetailOutputPort.findByProgramId(cohort.getProgramId());
        log.info("current total amount received for program  {}",programLoanDetail.getAmountReceived());
        log.info("loanee amount disbursed {}", loanOffer.getAmountApproved());
        programLoanDetail.setAmountReceived(programLoanDetail.getAmountReceived()
                .add(loanOffer.getAmountApproved()));
        programLoanDetail.setOutstandingAmount(programLoanDetail.getOutstandingAmount()
                .add(loanOffer.getAmountApproved()));
        programLoanDetailOutputPort.save(programLoanDetail);
    }

    private void updateCohortLoanDetail(LoanOffer loanOffer) throws MeedlException {
        CohortLoanDetail cohortLoanDetail = cohortLoanDetailOutputPort.findByCohortId(loanOffer.getCohortId());
        log.info("current total amount received for cohort {}",cohortLoanDetail.getAmountReceived());
        log.info("loanee amount disbursed {}", loanOffer.getAmountApproved());
        cohortLoanDetail.setAmountReceived(cohortLoanDetail.getAmountReceived().
                add(loanOffer.getAmountApproved()));
        cohortLoanDetail.setOutstandingAmount(cohortLoanDetail.getOutstandingAmount().
                add(loanOffer.getAmountApproved()));
        cohortLoanDetail = cohortLoanDetailOutputPort.save(cohortLoanDetail);
        log.info("total amount received updated for cohort after adding == {} is {}",
                cohortLoanDetail.getAmountReceived(), loanOffer.getAmountApproved());
        log.info("total amount outstanding updated for cohort after adding == {} is {}",
                cohortLoanDetail.getOutstandingAmount(), loanOffer.getAmountApproved());

        log.info("cohort program id == {} cohort organization id == {}",
                cohortLoanDetail.getCohort().getProgramId(),cohortLoanDetail.getCohort().getOrganizationId());
    }

    private void updateLoaneeLoanDetail(LoanOffer loanOffer,LocalDateTime localDateTime) throws MeedlException {
        LoaneeLoanDetail loaneeLoanDetail =
                loaneeLoanDetailsOutputPort.findByCohortLoaneeId(loanOffer.getCohortLoaneeId());

        loaneeLoanDetail.setAmountReceived(loanOffer.getAmountApproved());
        loaneeLoanDetail.setAmountOutstanding(loanOffer.getAmountApproved());
        loaneeLoanDetail.setLoanStartDate(localDateTime);
        loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
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
    public Loan viewLoanDetails(String loanId) throws MeedlException {
        MeedlValidator.validateUUID(loanId, LoanMessages.INVALID_LOAN_ID.getMessage());
        Loan foundLoan = loanOutputPort.viewLoanById(loanId).
                orElseThrow(()-> new LoanException(LoanMessages.LOAN_NOT_FOUND.getMessage()));
        log.info("Found loan {}", foundLoan);
        List<LoaneeLoanBreakdown> loaneeLoanBreakdowns =
                loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(foundLoan.getCohortLoaneeId());
        log.info("Loanee loan breakdowns returned: {}", loaneeLoanBreakdowns);
        foundLoan.setLoaneeLoanBreakdowns(loaneeLoanBreakdowns);
        BigDecimal amountReceived = foundLoan.getLoanAmountApproved();
        if (amountReceived !=  null && amountReceived.compareTo(BigDecimal.ZERO) > 0) {
            foundLoan.setDebtPercentage(foundLoan.getLoanAmountOutstanding()
                    .divide(foundLoan.getLoanAmountApproved(), RoundingMode.UP).multiply(BigDecimal.valueOf(100)));
            foundLoan.setRepaymentRate(foundLoan.getLoanAmountRepaid()
                    .divide(foundLoan.getLoanAmountApproved(), RoundingMode.UP).multiply(BigDecimal.valueOf(100)));
        }else {
            foundLoan.setDebtPercentage(BigDecimal.ZERO);
            foundLoan.setRepaymentRate(BigDecimal.ZERO);
        }
        return foundLoan;
    }

    @Override
    public Page<Loan> viewAllLoans(Loan loan) throws MeedlException {
        MeedlValidator.validatePageSize(loan.getPageSize());
        MeedlValidator.validatePageNumber(loan.getPageNumber());
        UserIdentity userIdentity = userIdentityOutputPort.findById(loan.getActorId());

        if (userIdentity.getRole().equals(IdentityRole.LOANEE)){
            return loanOutputPort.findAllLoanDisburedToLoanee(userIdentity.getId(),loan.getPageNumber(),loan.getPageSize());
        }
        if (StringUtils.isNotEmpty(loan.getOrganizationId())) {
            return loanOutputPort.findAllByOrganizationId(loan.getOrganizationId(), loan.getPageSize(), loan.getPageNumber());
        }if (StringUtils.isNotEmpty(loan.getLoaneeId())){
            MeedlValidator.validateUUID(loan.getLoaneeId(),LoaneeMessages.LOANEES_ID_CANNOT_BE_EMPTY.getMessage());
            return loanOutputPort.findAllLoanDisburedToLoaneeByLoaneeId(loan.getLoaneeId(),loan.getPageSize(), loan.getPageNumber());
        }else {
            return loanOutputPort.findAllLoan(loan.getPageSize(), loan.getPageNumber());
        }
    }

    @Override
    public LoanDetailSummary viewLoanTotal(String actorId) throws MeedlException {
        MeedlValidator.validateUUID(actorId,UserMessages.INVALID_USER_ID.getMessage());
        LoanDetailSummary loanDetailSummary = null;
        IdentityRole identityRole = userIdentityOutputPort.findById(actorId).getRole();
        if (identityRole.equals(IdentityRole.LOANEE)) {
            loanDetailSummary = loaneeLoanDetailsOutputPort.getLoaneeLoanSummary(actorId);
            log.info("Found loanee  loan summary {}", loanDetailSummary);
            return loanDetailSummary;
        }if (identityRole.isMeedlRole()){
            loanDetailSummary = loaneeLoanAggregateOutputPort.getLoanAggregationSummary();
            log.info("Found meedl  loan summary {}", loanDetailSummary);
        }else {
            OrganizationEmployeeIdentity organizationEmployeeIdentity =
                    organizationEmployeeIdentityOutputPort.findByMeedlUserId(actorId)
                            .orElseThrow(() -> new IdentityException("User is not an employee of this organization"));
            loanDetailSummary = loaneeLoanDetailsOutputPort.getOrganizationLoanSummary(organizationEmployeeIdentity.getOrganization());
            log.info("Found organization loan summary {}", loanDetailSummary);
        }
        return loanDetailSummary;
    }

    @Override
    public Page<Loan> searchDisbursedLoan(Loan loan) throws MeedlException {
        MeedlValidator.validatePageSize(loan.getPageSize());
        MeedlValidator.validatePageNumber(loan.getPageNumber());
        UserIdentity userIdentity = userIdentityOutputPort.findById(loan.getActorId());
        if (userIdentity.getRole().isMeedlRole()) {
            MeedlValidator.validateUUID(loan.getLoaneeId(),LoaneeMessages.INVALID_LOANEE_ID.getMessage());
            return loanOutputPort.searchLoanByOrganizationNameAndLoaneeId(loan);
        }else {
            return loanOutputPort.searchLoanByOrganizationNameAndUserId(loan,userIdentity.getId());
        }
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
    public LoanReferral viewLoanReferral(String actorId, String loanReferralId) throws MeedlException {
        MeedlValidator.validateUUID(actorId,UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateUUID(loanReferralId, LoanMessages.LOAN_REFERRAL_ID_MUST_NOT_BE_EMPTY.getMessage());
        UserIdentity userIdentity = userIdentityOutputPort.findById(actorId);
        LoanReferral loanReferral = loanReferralOutputPort.findLoanReferralById(loanReferralId)
                .orElseThrow(() -> new ResourceNotFoundException(LoanMessages.LOAN_REFERRAL_NOT_FOUND.getMessage()));
        if (! userIdentity.getId().equals(loanReferral.getLoaneeUserId())){
            log.info("User identity does not match cohort loanee user identity");
            log.info("actor id {}", userIdentity.getId());
            log.info("cohort loanee user identity id {}", loanReferral.getLoaneeUserId());
            throw new LoanException(LoanMessages.LOAN_REFERRAL_NOT_ASSIGNED_TO_LOANEE.getMessage());
        }
        List<LoaneeLoanBreakdown> loaneeLoanBreakdowns =
                loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(
                        loanReferral.getCohortLoaneeId());
        log.info("Loanee loan breakdowns found from the DB : {}", loaneeLoanBreakdowns);
        loanReferral.setLoaneeLoanBreakdowns(loaneeLoanBreakdowns);
        log.info("Loanee loan breakdowns set to be returned: {}", loanReferral.getLoaneeLoanBreakdowns());
        return loanReferral;
    }

    @Override
    public Page<LoanReferral> viewLoanReferralsForLoanee(String userId, int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        Optional<Loanee> loanee = loaneeOutputPort.findByUserId(userId);
        String loaneeId = null;
        if (loanee.isPresent()) {
            loaneeId = loanee.get().getId();
        }
        Page<LoanReferral> loanReferrals = loanReferralOutputPort.findAllLoanReferralsForLoanee(loaneeId, pageNumber, pageSize);

        List<LoanReferral> updatedLoanReferrals = loanReferrals.getContent().stream()
                .peek(loanReferral -> {
                    try {
                        List<LoaneeLoanBreakdown> loaneeLoanBreakdowns = loaneeLoanBreakDownOutputPort
                                .findAllLoaneeLoanBreakDownByCohortLoaneeId(loanReferral.getCohortLoaneeId());
                        log.info("Loanee loan breakdowns found from the DB for cohortLoaneeId {}: {}",
                                loanReferral.getCohortLoaneeId(), loaneeLoanBreakdowns);
                        loanReferral.setLoaneeLoanBreakdowns(loaneeLoanBreakdowns);
                    } catch (MeedlException e) {
                        log.error("Failed to fetch loanee loan breakdowns for cohortLoaneeId {}: {}",
                                loanReferral.getCohortLoaneeId(), e.getMessage());
                    }
                })
                .collect(Collectors.toList());

        return new PageImpl<>(updatedLoanReferrals, loanReferrals.getPageable(), loanReferrals.getTotalElements());
    }

    @Override
    public LoanReferral respondToLoanReferral(LoanReferral loanReferral) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanReferral, LoanMessages.LOAN_REFERRAL_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanReferral.getId(), LoanMessages.INVALID_LOAN_REFERRAL_ID.getMessage());
        LoanReferral foundLoanReferral = loanReferralOutputPort.findById(loanReferral.getId());
        if (foundLoanReferral == null) {
            log.info("FoundLoanReferral is null");
            throw new LoanException(LoanMessages.LOAN_REFERRAL_NOT_FOUND.getMessage());
        }
        log.info("loanee verification state === {}", foundLoanReferral.getCohortLoanee().getLoanee().getUserIdentity().isIdentityVerified());

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
            updateNumbersIfLoaneeIsVerified(foundLoanReferral,loanRequest);



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

    private void updateNumbersIfLoaneeIsVerified(LoanReferral loanReferral, LoanRequest loanRequest) throws MeedlException {
        if (loanReferral.getCohortLoanee().getLoanee().getUserIdentity().isIdentityVerified()
                || loanReferral.getCohortLoanee().getLoanee().getOnboardingMode()
                .equals(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS)) {
            log.info("about to update loan request count on loan metrics: {}", loanReferral.getCohortLoanee().getReferredBy());
            updateLoanMetricsLoanRequestCount(loanReferral.getCohortLoanee().getReferredBy());
            log.info("done with loan metrics update");

            Cohort cohort = updateLoanRequestCountOnCohort(loanReferral);

            log.info("done with cohort update");
            updateLoanAmountRequestedOnCohortLoanDetail(loanRequest, cohort);

            log.info("done with cohort loan details update");
            updateLoanAmountRequestedOnProgramLoanDetail(loanRequest, cohort);

            log.info("done with program loan details update");
            updateLoanAmountRequestOnOrganizationLoanDetail(loanRequest, cohort);
            log.info("done with organization loan details update");
        }
    }

    private void updateLoanAmountRequestOnOrganizationLoanDetail(LoanRequest loanRequest, Cohort cohort) throws MeedlException {
        OrganizationLoanDetail organizationLoanDetail = organizationLoanDetailOutputPort.findByOrganizationId(cohort.getOrganizationId());
        organizationLoanDetail.setAmountRequested(organizationLoanDetail.getAmountRequested()
                .add(loanRequest.getLoanAmountRequested()));
        organizationLoanDetailOutputPort.save(organizationLoanDetail);
    }

    private void updateLoanAmountRequestedOnProgramLoanDetail(LoanRequest loanRequest, Cohort cohort) throws MeedlException {
        ProgramLoanDetail programLoanDetail = programLoanDetailOutputPort.findByProgramId(cohort.getProgramId());
        log.info("program loan details id {}", programLoanDetail.getId());
        programLoanDetail.setAmountRequested(programLoanDetail.getAmountRequested()
                .add(loanRequest.getLoanAmountRequested()));
        programLoanDetailOutputPort.save(programLoanDetail);
    }

    private void updateLoanAmountRequestedOnCohortLoanDetail(LoanRequest loanRequest, Cohort cohort) throws MeedlException {
        CohortLoanDetail foundCohort = cohortLoanDetailOutputPort.findByCohortId(cohort.getId());
        log.info("current total amount requested for cohort {}", foundCohort.getAmountRequested());
        log.info("loanee amount requested {}", loanRequest.getLoanAmountRequested());
        foundCohort.setAmountRequested(foundCohort.getAmountRequested().
                add(loanRequest.getLoanAmountRequested()));
        cohortLoanDetailOutputPort.save(foundCohort);
        log.info("total amount requested updated for cohort after adding == {} is {}",
                loanRequest.getLoanAmountRequested(), foundCohort.getAmountRequested());
    }

    private Cohort updateLoanRequestCountOnCohort(LoanReferral loanReferral) throws MeedlException {
        log.info("Updating number of loan request on cohort: {}", loanReferral.getCohortLoanee());
        Cohort cohort = loanReferral.getCohortLoanee().getCohort();
        log.info("found cohort == {}", cohort);
        log.info("current number of loan request == {}", cohort.getNumberOfLoanRequest());
        cohort.setNumberOfLoanRequest(cohort.getNumberOfLoanRequest() + 1);
        cohort = cohortOutputPort.save(cohort);
        log.info(" number of loan request after adding 1 == {}", cohort.getNumberOfLoanRequest());
        return cohort;
    }

    private void updateLoanMetricsLoanRequestCount(String referBy) throws MeedlException {
        Optional<OrganizationIdentity> organization =
                organizationIdentityOutputPort.findOrganizationByName(referBy);
        if (organization.isEmpty()) {
            throw new ResourceNotFoundException(OrganizationMessages.ORGANIZATION_NOT_FOUND.getMessage());
        }
        Optional<LoanMetrics> loanMetrics =
                loanMetricsOutputPort.findByOrganizationId(organization.get().getId());
        if (loanMetrics.isEmpty()) {
            throw new ResourceNotFoundException("Organization has no loan metrics");
        }
        loanMetrics.get().setLoanRequestCount(
                loanMetrics.get().getLoanRequestCount() + 1
        );
        loanMetricsOutputPort.save(loanMetrics.get());
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
    public LoaneeLoanAccount acceptLoanOffer(LoanOffer loanOffer, OnboardingMode onboardingMode) throws MeedlException {
        loanOffer.validateForAcceptOffer();
        log.info("Loan offer identity validated : {}", loanOffer);
        LoanOffer offer = loanOfferOutputPort.findLoanOfferById(loanOffer.getId());
        String referBy = offer.getReferredBy();
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
            LoaneeLoanAccount loaneeLoanAccount = acceptLoanOffer(loanee.getUserIdentity(), loanOffer, offer,referBy);
            if (!OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS.equals(onboardingMode)){
                log.info("Sending pm notification on accepting loan offer, loanee is not via file upload.");
                notifyPortfolioManager(offer, loanee.getUserIdentity());
            }
            return loaneeLoanAccount;

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
        return loanOfferOutputPort.findAllLoanOfferedToLoaneesInOrganization(organizationId, pageSize, pageNumber);
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
    public Page<LoanOffer> viewAllLoanOffers(LoanOffer loanOffer) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(loanOffer.getUserId());
        if (userIdentity.getRole().equals(IdentityRole.ORGANIZATION_ADMIN)){
           OrganizationEmployeeIdentity organizationEmployeeIdentity =
                   organizationEmployeeIdentityOutputPort.findByCreatedBy(loanOffer.getUserId());
            return loanOfferOutputPort.findAllLoanOfferedToLoaneesInOrganization(organizationEmployeeIdentity.getOrganization(),
                   loanOffer.getPageSize(),loanOffer.getPageNumber());
        }if (userIdentity.getRole().equals(IdentityRole.LOANEE)){
            return loanOfferOutputPort.findAllLoanOfferAssignedToLoanee(userIdentity.getId(),loanOffer.getPageSize(),
                    loanOffer.getPageNumber());
        }if (ObjectUtils.isNotEmpty(loanOffer.getOrganizationId())){
            return loanOfferOutputPort.findAllLoanOfferedToLoaneesInOrganization(loanOffer.getOrganizationId(),
                    loanOffer.getPageSize(),loanOffer.getPageNumber());
        }
        return loanOfferOutputPort.findAllLoanOffer(loanOffer.getPageSize(),loanOffer.getPageNumber());
    }


    @Override
    public LoanOffer viewLoanOfferDetails(String actorId, String loanOfferId) throws MeedlException {
        MeedlValidator.validateUUID(loanOfferId, LoanOfferMessages.INVALID_LOAN_OFFER_ID.getMessage());
        UserIdentity userIdentity = userIdentityOutputPort.findById(actorId);
        LoanOffer loanOffer =
                 loanOfferOutputPort.findLoanOfferById(loanOfferId);
        List<LoaneeLoanBreakdown> loaneeLoanBreakdowns =
                loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(loanOffer.getCohortLoaneeId());
        log.info("Loanee loan breakdowns found for this loan offer : {}: {}", loanOffer.getCohortLoaneeId(),
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
