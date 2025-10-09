package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.RepaymentHistoryUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.FinancialConstants;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RepaymentHistoryService implements RepaymentHistoryUseCase {

    private final RepaymentHistoryOutputPort repaymentHistoryOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final LoanOfferOutputPort loanOfferOutputPort;
    private final LoanOutputPort loanOutputPort;
    private final LoanProductOutputPort loanProductOutputPort;

    @Override
    public Page<RepaymentHistory> findAllRepaymentHistory(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException {
        log.info("request that got into service, actor =  {}, pageSize = {} , pageNumber = {}", repaymentHistory.getActorId()
                , pageSize, pageNumber);
        if (repaymentHistory.getMonth() != null) {
            if (repaymentHistory.getMonth() <= 0 || repaymentHistory.getMonth() > 12) {
                log.warn("Repayment history is not within 12 month stipulation.");
                repaymentHistory.setMonth(null);
            }
        }
        UserIdentity userIdentity = userIdentityOutputPort.findById(repaymentHistory.getActorId());
        if (userIdentity.getRole().isMeedlRole()) {
            log.info("Portfolio manager is viewing repayment history");
            Page<RepaymentHistory> repaymentHistories = repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory,
                    pageSize, pageNumber);
            log.info("repayment histories gotten from adapter == {}", repaymentHistories.getContent().stream().toList());
            return repaymentHistories;
        }
        Optional<Loanee> optionalLoanee = loaneeOutputPort.findByUserId(userIdentity.getId());
        optionalLoanee.ifPresent(loanee -> repaymentHistory.setLoaneeId(loanee.getId()));
        return repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory, pageSize, pageNumber);
    }


    @Override
    public Page<RepaymentHistory> searchRepaymentHistory(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException {
        if (repaymentHistory.getMonth() != null) {
            if (repaymentHistory.getMonth() <= 0 || repaymentHistory.getMonth() > 12) {
                repaymentHistory.setMonth(null);
            }
        }
        return repaymentHistoryOutputPort.searchRepaymemtHistoryByLoaneeName(repaymentHistory, pageSize, pageNumber);
    }

    @Override
    public RepaymentHistory getFirstRepaymentYearAndLastRepaymentYear(String actorId, String loaneeId,String loanId) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(actorId);
        if (userIdentity.getRole().isMeedlRole() && loanId == null) {
            return repaymentHistoryOutputPort.getFirstAndLastYear(loaneeId);
        }if (loaneeId != null) {
            MeedlValidator.validateUUID(loaneeId,"Loanee id cannot be empty or invalid");
            Loanee loanee = loaneeOutputPort.findByUserId(userIdentity.getId()).get();
            return repaymentHistoryOutputPort.getFirstAndLastYear(loanee.getId());
        }
        log.info("fetching first repayment year and last repayment year for a particular loan");
        MeedlValidator.validateUUID(loanId,"Loan Id cannot be empty or invalid");
        return repaymentHistoryOutputPort.getFirstAndLastYearOfLoanRepayment(loanId);
    }

    @Override
    public List<RepaymentHistory> generateRepaymentHistory(BigDecimal amountApproved, String loanProductId, String loanId) throws MeedlException {
        List<RepaymentHistory> repaymentSchedule = new ArrayList<>();
        LoanOffer loanOffer = new LoanOffer();

        int tenorMonths;
        int moratoriumMonths;
        if (amountApproved != null || loanProductId != null) {
            validateRequestForRepaymentSchedulingBeforeCreatingLoanOffer(amountApproved, loanProductId);
            LoanProduct loanProduct = loanProductOutputPort.findById(loanProductId);
            log.info("found loan product name {}", loanProduct.getName());
            setUpLoanOfferForScheduling(amountApproved, loanOffer, loanProduct);
            tenorMonths = loanProduct.getTenor();
            moratoriumMonths = loanProduct.getMoratorium();

        }else {
            log.info("setting up schedule generation in view loan details");
            MeedlValidator.validateUUID(loanId,"Loan id cannot be empty or invalid");
            Loan loan = loanOutputPort.findLoanById(loanId);
            loanOffer = loanOfferOutputPort.findById(loan.getLoanOfferId());
            loanOffer.setDateTimeOffered(loan.getStartDate());

            tenorMonths = loanOffer.getLoanProduct().getTenor();
            moratoriumMonths = loanOffer.getLoanProduct().getMoratorium();
        }

        log.info("scheduling about to start ------");
        BigDecimal monthlyRate = getMonthleyRate(loanOffer);

        BigDecimal principal = loanOffer.getAmountApproved().setScale(2, RoundingMode.HALF_UP);
        BigDecimal balance = principal;
        BigDecimal totalRepaid = BigDecimal.ZERO;
        BigDecimal expectedMonthlyRepayment =
                equatedMonthlyInstalment(monthlyRate, tenorMonths, moratoriumMonths, principal);

        LocalDateTime dateTimeOffered = loanOffer.getDateTimeOffered();
        LocalDate startDate = dateTimeOffered.toLocalDate();
        LocalDate endOfMonth = startDate.with(TemporalAdjusters.lastDayOfMonth());

        if (!startDate.equals(endOfMonth)) {
            long daysInMonth = ChronoUnit.DAYS.between(startDate.withDayOfMonth(1), endOfMonth.plusDays(1));
            long daysRemaining = ChronoUnit.DAYS.between(startDate, endOfMonth.plusDays(1));
            BigDecimal prorationFactor = BigDecimal.valueOf(daysRemaining)
                    .divide(BigDecimal.valueOf(daysInMonth), 10, RoundingMode.HALF_UP);
            BigDecimal partialMonthInterest = balance.multiply(monthlyRate)
                    .multiply(prorationFactor)
                    .setScale(2, RoundingMode.HALF_UP);

            balance = balance.add(partialMonthInterest).setScale(2, RoundingMode.HALF_UP);

            addScheduleToRepaymentScheduleList(repaymentSchedule, totalRepaid, balance, endOfMonth, partialMonthInterest);
        }

        LocalDate paymentDate = endOfMonth.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

        moratoriumAndTenorPeriodInterestAccruedIntoBalance(
                moratoriumMonths, balance, monthlyRate,
                repaymentSchedule, totalRepaid, paymentDate,
                tenorMonths, expectedMonthlyRepayment,
                startDate.getDayOfMonth()
        );

        RepaymentHistory last = repaymentSchedule.get(repaymentSchedule.size() - 1);
        last.setTenor(tenorMonths);
        last.setMoratorium(moratoriumMonths);

        return repaymentSchedule;
    }

    private static void validateRequestForRepaymentSchedulingBeforeCreatingLoanOffer(BigDecimal amountApproved, String loanProductId) throws MeedlException {
        log.info("setting up schedule generation before creating loan offer");
        MeedlValidator.validateUUID(loanProductId,"Loan product id cannot be empty or invalid");
        if (amountApproved == null) {
            throw new MeedlException("Amount approved cannot be empty");
        }
        MeedlValidator.validateNegativeAmount(amountApproved,"Approved ");
    }

    private static void setUpLoanOfferForScheduling(BigDecimal amountApproved, LoanOffer loanOffer, LoanProduct loanProduct) {
        loanOffer.setAmountApproved(amountApproved);
        loanOffer.setDateTimeOffered(LocalDateTime.now());
        loanOffer.setLoanProduct(loanProduct);
    }

    private static void addScheduleToRepaymentScheduleList(List<RepaymentHistory> repaymentSchedule, BigDecimal totalRepaid, BigDecimal balance, LocalDate endOfMonth, BigDecimal partialMonthInterest) {
        repaymentSchedule.add(
                RepaymentHistory.builder()
                        .totalAmountRepaid(totalRepaid)
                        .amountOutstanding(balance)
                        .paymentDate(endOfMonth)
                        .amountPaid(BigDecimal.ZERO)
                        .interestIncurred(partialMonthInterest)
                        .principalPayment(BigDecimal.ZERO)
                        .build()
        );
    }

    @Override
    public RepaymentHistory simulateRepayment(BigDecimal loanAmount, double interestRate, int repaymentPeriod) throws MeedlException {
        MeedlValidator.validateNegativeAmount(loanAmount,"Loan");
        MeedlValidator.validateFloatDataElement((float) interestRate,"Interest Rate cannot be zero or negative");
        MeedlValidator.validatePositiveNumber(repaymentPeriod,"Repayment period cannot be zero or less than zero");
        BigDecimal monthlyPayment;
        BigDecimal totalRepayment;
        BigDecimal totalInterestPaid;

        if (interestRate == 0) {
            monthlyPayment = loanAmount.divide(BigDecimal.valueOf(repaymentPeriod), 2, RoundingMode.HALF_UP);
            totalRepayment = loanAmount;
            totalInterestPaid = BigDecimal.ZERO;
        } else {


            double monthlyInterestRate = interestRate / FinancialConstants.MONTHS_PER_YEAR /
                    FinancialConstants.PERCENTAGE_BASE_INT;
            BigDecimal monthlyRateDecimal = BigDecimal.valueOf(monthlyInterestRate);

            BigDecimal onePlusMonthlyRate = BigDecimal.ONE.add(monthlyRateDecimal);
            BigDecimal ratePowerTotalMonths = onePlusMonthlyRate.pow(repaymentPeriod);

            BigDecimal paymentNumerator = loanAmount.multiply(monthlyRateDecimal).multiply(ratePowerTotalMonths);
            BigDecimal paymentDenominator = ratePowerTotalMonths.subtract(BigDecimal.ONE);

            monthlyPayment = paymentNumerator.divide(paymentDenominator, 2, RoundingMode.HALF_UP);

            totalRepayment = monthlyPayment.multiply(BigDecimal.valueOf(repaymentPeriod));

            totalInterestPaid = totalRepayment.subtract(loanAmount);
        }

        return RepaymentHistory.builder().totalAmountRepaid(totalRepayment)
                .principalPayment(monthlyPayment).interestIncurred(totalInterestPaid).build();

    }

    @Override
    public Page<RepaymentHistory> findAllRepaymentHistoryByLoanId(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(repaymentHistory.getLoanId(),"Loan id cannot be empty");
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        return repaymentHistoryOutputPort.findAllRepaymentHistoryByLoanId(repaymentHistory,pageSize,pageNumber);
    }

    private static BigDecimal getMonthleyRate(LoanOffer loanOffer) {
        BigDecimal annualRate = BigDecimal.valueOf(loanOffer.getLoanProduct().getInterestRate())
                .divide(BigDecimal.valueOf(FinancialConstants.PERCENTAGE_BASE_INT), 10, RoundingMode.HALF_UP);
        return annualRate.divide(BigDecimal.valueOf(FinancialConstants.MONTHS_PER_YEAR), 10, RoundingMode.HALF_UP);
    }

    private static BigDecimal equatedMonthlyInstalment(BigDecimal monthlyRate, int totalMonths, int moratoriumMonths, BigDecimal principal) {
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRatePowN = onePlusRate.pow(totalMonths - moratoriumMonths);
        return principal.multiply(monthlyRate)
                .multiply(onePlusRatePowN)
                .divide(onePlusRatePowN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }

    private static void moratoriumAndTenorPeriodInterestAccruedIntoBalance(
            int moratoriumMonths,
            BigDecimal balance,
            BigDecimal monthlyRate,
            List<RepaymentHistory> repaymentSchedule,
            BigDecimal totalRepaid,
            LocalDate paymentDate,
            int totalMonths,
            BigDecimal expectedMonthlyRepayment,
            int offerDayOfMonth
    ) {
        for (int eachMoratoriumMonth = 1; eachMoratoriumMonth <= moratoriumMonths; eachMoratoriumMonth++) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            balance = balance.add(interest).setScale(2, RoundingMode.HALF_UP);

            repaymentSchedule.add(
                    RepaymentHistory.builder()
                            .totalAmountRepaid(totalRepaid)
                            .amountOutstanding(balance)
                            .paymentDate(paymentDate)
                            .amountPaid(BigDecimal.ZERO)
                            .interestIncurred(interest)
                            .principalPayment(BigDecimal.ZERO)
                            .build()
            );

            paymentDate = paymentDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        }

        for (int eachTenorMonth = 1; eachTenorMonth <= totalMonths - moratoriumMonths; eachTenorMonth++) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPayment = expectedMonthlyRepayment.subtract(interest).setScale(2, RoundingMode.HALF_UP);

            if (eachTenorMonth == (totalMonths - moratoriumMonths)) {
                principalPayment = balance;
                expectedMonthlyRepayment = principalPayment.add(interest).setScale(2, RoundingMode.HALF_UP);
                balance = BigDecimal.ZERO;

                paymentDate = paymentDate.withDayOfMonth(
                        Math.min(offerDayOfMonth, paymentDate.lengthOfMonth())
                );
            }

            BigDecimal amountPaid = expectedMonthlyRepayment;
            totalRepaid = totalRepaid.add(amountPaid).setScale(2, RoundingMode.HALF_UP);

            addScheduleToRepaymentScheduleList(balance, repaymentSchedule, totalRepaid, paymentDate, amountPaid, interest, principalPayment);

            if (eachTenorMonth < (totalMonths - moratoriumMonths)) {
                paymentDate = paymentDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            }
        }
    }

    private static void addScheduleToRepaymentScheduleList(BigDecimal balance, List<RepaymentHistory> repaymentSchedule,
                                                           BigDecimal totalRepaid, LocalDate paymentDate, BigDecimal amountPaid,
                                                           BigDecimal interest, BigDecimal principalPayment) {
        repaymentSchedule.add(
                RepaymentHistory.builder()
                        .totalAmountRepaid(totalRepaid)
                        .amountOutstanding(balance)
                        .paymentDate(paymentDate)
                        .amountPaid(amountPaid)
                        .interestIncurred(interest)
                        .principalPayment(principalPayment)
                        .build()
        );
    }


}
