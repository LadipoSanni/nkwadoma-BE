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
        LoanOffer loanOffer = new LoanOffer();

        int tenorMonths;
        int moratoriumMonths;
        LocalDateTime disbursementDate;

        if (amountApproved != null || loanProductId != null) {
            validateRequestForRepaymentSchedulingBeforeCreatingLoanOffer(amountApproved, loanProductId);
            LoanProduct loanProduct = loanProductOutputPort.findById(loanProductId);
            log.info("found loan product name {}", loanProduct.getName());
            setUpLoanOfferForScheduling(amountApproved, loanOffer, loanProduct);
            tenorMonths = loanProduct.getTenor();
            moratoriumMonths = loanProduct.getMoratorium();
            disbursementDate = LocalDateTime.now();
        } else {
            log.info("setting up schedule generation in view loan details");
            MeedlValidator.validateUUID(loanId,"Loan id cannot be empty or invalid");
            Loan loan = loanOutputPort.findLoanById(loanId);
            loanOffer = loanOfferOutputPort.findById(loan.getLoanOfferId());
            disbursementDate = loan.getStartDate();
            tenorMonths = loanOffer.getLoanProduct().getTenor();
            moratoriumMonths = loanOffer.getLoanProduct().getMoratorium();
        }

        log.info("scheduling about to start ------");
        return generateCompleteSchedule(loanOffer, tenorMonths, moratoriumMonths, disbursementDate);
    }

    private List<RepaymentHistory> generateCompleteSchedule(LoanOffer loanOffer, int tenorMonths,
                                                            int moratoriumMonths, LocalDateTime disbursementDateTime) {
        List<RepaymentHistory> repaymentSchedule = new ArrayList<>();

        BigDecimal principal = loanOffer.getAmountApproved().setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyRate = getMonthlyRate(loanOffer);
        BigDecimal annualRate = getAnnualRate(loanOffer);

        LocalDate startDate = disbursementDateTime.toLocalDate();
        BigDecimal currentBalance = principal;

        boolean isStartDateAfter20th = startDate.getDayOfMonth() > 20;

        if (!isLastDayOfMonth(startDate)) {
            RepaymentHistory partialMonthEntry = createPartialFirstMonthEntry(startDate, currentBalance, annualRate);
            currentBalance = partialMonthEntry.getAmountOutstanding();
            repaymentSchedule.add(partialMonthEntry);
        }

        LocalDate currentDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        int actualMoratoriumMonths = isStartDateAfter20th ? moratoriumMonths : moratoriumMonths - 1;
        for (int moratoriumMonth = 1; moratoriumMonth <= actualMoratoriumMonths; moratoriumMonth++) {
            BigDecimal interest = currentBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            currentBalance = currentBalance.add(interest);

            LocalDate paymentDate = currentDate.plusMonths(moratoriumMonth).with(TemporalAdjusters.lastDayOfMonth());

            repaymentSchedule.add(RepaymentHistory.builder()
                    .totalAmountRepaid(BigDecimal.ZERO)
                    .amountOutstanding(currentBalance)
                    .paymentDate(paymentDate)
                    .amountPaid(BigDecimal.ZERO)
                    .interestIncurred(interest)
                    .principalPayment(BigDecimal.ZERO)
                    .build());
        }

        if (actualMoratoriumMonths > 0) {
            currentDate = currentDate.plusMonths(actualMoratoriumMonths).with(TemporalAdjusters.lastDayOfMonth());
        }

        int repaymentMonths = isStartDateAfter20th ? tenorMonths - moratoriumMonths : tenorMonths - moratoriumMonths + 1;
        BigDecimal emi = calculateEMI(currentBalance, monthlyRate, repaymentMonths);
        BigDecimal totalRepaid = BigDecimal.ZERO;

        for (int repaymentMonth = 1; repaymentMonth <= repaymentMonths; repaymentMonth++) {
            boolean isLastPayment = (repaymentMonth == repaymentMonths);

            LocalDate paymentDate;
            if (isLastPayment) {
                paymentDate = currentDate.plusMonths(1).withDayOfMonth(
                        Math.min(startDate.getDayOfMonth(), currentDate.plusMonths(1).lengthOfMonth())
                );
            } else {
                paymentDate = currentDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            }

            BigDecimal interest = currentBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPayment;
            BigDecimal amountPaid;

            if (isLastPayment && isStartDateAfter20th) {
                BigDecimal partialMonthAdjustment = calculatePartialMonthAdjustment(startDate, principal, annualRate);
                BigDecimal totalInterest = interest.add(partialMonthAdjustment);
                principalPayment = currentBalance;
                amountPaid = principalPayment.add(totalInterest);
                currentBalance = BigDecimal.ZERO;
            } else if (isLastPayment) {
                principalPayment = currentBalance;
                amountPaid = principalPayment.add(interest);
                currentBalance = BigDecimal.ZERO;
            } else {
                principalPayment = emi.subtract(interest);
                if (principalPayment.compareTo(currentBalance) > 0) {
                    principalPayment = currentBalance;
                    amountPaid = principalPayment.add(interest);
                    currentBalance = BigDecimal.ZERO;
                } else {
                    amountPaid = emi;
                    currentBalance = currentBalance.subtract(principalPayment);
                }
            }

            totalRepaid = totalRepaid.add(amountPaid);

            repaymentSchedule.add(RepaymentHistory.builder()
                    .totalAmountRepaid(totalRepaid)
                    .amountOutstanding(currentBalance)
                    .paymentDate(paymentDate)
                    .amountPaid(amountPaid)
                    .interestIncurred(interest)
                    .principalPayment(principalPayment)
                    .build());

            if (!isLastPayment) {
                currentDate = paymentDate;
            }
        }

        if (!repaymentSchedule.isEmpty()) {
            RepaymentHistory last = repaymentSchedule.get(repaymentSchedule.size() - 1);
            last.setTenor(tenorMonths);
            last.setMoratorium(moratoriumMonths);
        }

        return repaymentSchedule;
    }

    private boolean isLastDayOfMonth(LocalDate date) {
        return date.equals(date.with(TemporalAdjusters.lastDayOfMonth()));
    }

    private RepaymentHistory createPartialFirstMonthEntry(LocalDate startDate, BigDecimal balance, BigDecimal annualRate) {
        LocalDate endOfMonth = startDate.with(TemporalAdjusters.lastDayOfMonth());
        long daysRemaining = ChronoUnit.DAYS.between(startDate, endOfMonth.plusDays(1));

        BigDecimal dailyRate = annualRate.divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);
        BigDecimal interest = balance.multiply(dailyRate)
                .multiply(BigDecimal.valueOf(daysRemaining))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal newBalance = balance.add(interest);

        return RepaymentHistory.builder()
                .totalAmountRepaid(BigDecimal.ZERO)
                .amountOutstanding(newBalance)
                .paymentDate(endOfMonth)
                .amountPaid(BigDecimal.ZERO)
                .interestIncurred(interest)
                .principalPayment(BigDecimal.ZERO)
                .build();
    }

    private BigDecimal calculatePartialMonthAdjustment(LocalDate startDate, BigDecimal originalPrincipal, BigDecimal annualRate) {
        int daysFromStartToMonthEnd = startDate.until(startDate.with(TemporalAdjusters.lastDayOfMonth()))
                .getDays() + 1;
        BigDecimal dailyRate = annualRate.divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);
        return originalPrincipal.multiply(dailyRate)
                .multiply(BigDecimal.valueOf(daysFromStartToMonthEnd))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private LocalDate calculateFinalPaymentDate(LocalDate lastPaymentDate, LocalDate originalStartDate, boolean isStartDateAfter20th) {
        if (isStartDateAfter20th) {
            return lastPaymentDate.plusMonths(1).withDayOfMonth(
                    Math.min(originalStartDate.getDayOfMonth(), lastPaymentDate.plusMonths(1).lengthOfMonth())
            );
        } else {
            return lastPaymentDate.withDayOfMonth(
                    Math.min(originalStartDate.getDayOfMonth(), lastPaymentDate.lengthOfMonth())
            );
        }
    }

    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal monthlyRate, int numberOfPayments) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(numberOfPayments), 2, RoundingMode.HALF_UP);
        }

        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRate.pow(numberOfPayments));
        BigDecimal denominator = onePlusRate.pow(numberOfPayments).subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal getMonthlyRate(LoanOffer loanOffer) {
        BigDecimal annualRate = BigDecimal.valueOf(loanOffer.getLoanProduct().getInterestRate())
                .divide(BigDecimal.valueOf(FinancialConstants.PERCENTAGE_BASE_INT), 10, RoundingMode.HALF_UP);
        return annualRate.divide(BigDecimal.valueOf(FinancialConstants.MONTHS_PER_YEAR), 10, RoundingMode.HALF_UP);
    }

    private BigDecimal getAnnualRate(LoanOffer loanOffer) {
        return BigDecimal.valueOf(loanOffer.getLoanProduct().getInterestRate())
                .divide(BigDecimal.valueOf(FinancialConstants.PERCENTAGE_BASE_INT), 10, RoundingMode.HALF_UP);
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

}
