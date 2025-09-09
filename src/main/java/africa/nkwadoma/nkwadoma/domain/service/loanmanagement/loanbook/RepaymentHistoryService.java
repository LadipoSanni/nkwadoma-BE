package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.RepaymentHistoryUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.FinancialConstants;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
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
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RepaymentHistoryService implements RepaymentHistoryUseCase {

    private final RepaymentHistoryOutputPort repaymentHistoryOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final LoanOfferOutputPort loanOfferOutputPort;

    @Override
    public Page<RepaymentHistory> findAllRepaymentHistory(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException {
        log.info("request that got into service, actor =  {}, pageSize = {} , pageNumber = {}",repaymentHistory.getActorId()
        , pageSize, pageNumber);
        if(repaymentHistory.getMonth() != null) {
            if (repaymentHistory.getMonth() <= 0 || repaymentHistory.getMonth() > 12) {
                log.warn("Repayment history is not within 12 month stipulation.");
                 repaymentHistory.setMonth(null);
            }
        }
        UserIdentity userIdentity = userIdentityOutputPort.findById(repaymentHistory.getActorId());
        if (userIdentity.getRole().equals(IdentityRole.PORTFOLIO_MANAGER)){
            log.info("Portfolio manager is viewing repayment history");
            Page<RepaymentHistory>  repaymentHistories = repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory,
                     pageSize, pageNumber);
             log.info("repayment histories gotten from adapter == {}",repaymentHistories.getContent().stream().toList());
            return repaymentHistories;
        }
        Loanee loanee = loaneeOutputPort.findByUserId(userIdentity.getId()).get();
        repaymentHistory.setLoaneeId(loanee.getId());
        return repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory, pageSize, pageNumber);
    }




    @Override
    public Page<RepaymentHistory> searchRepaymentHistory(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException {
        if(repaymentHistory.getMonth() != null) {
            if (repaymentHistory.getMonth() <= 0 || repaymentHistory.getMonth() > 12) {
                 repaymentHistory.setMonth(null);
            }
        }
        return repaymentHistoryOutputPort.searchRepaymemtHistoryByLoaneeName(repaymentHistory,pageSize,pageNumber);
    }

    @Override
    public RepaymentHistory getFirstRepaymentYearAndLastRepaymentYear(String actorId,String loaneeId) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(actorId);
        if (userIdentity.getRole().equals(IdentityRole.PORTFOLIO_MANAGER)){
            return repaymentHistoryOutputPort.getFirstAndLastYear(loaneeId);
        }
        Loanee loanee = loaneeOutputPort.findByUserId(userIdentity.getId()).get();
        return repaymentHistoryOutputPort.getFirstAndLastYear(loanee.getId());
    }

    @Override
    public List<RepaymentHistory> generateRepaymentHistory(String loanRequestId) throws MeedlException {
        List<RepaymentHistory> repaymentSchedule = new ArrayList<>();

        MeedlValidator.validateUUID(loanRequestId, "Loan request id cannot be null or invalid");
        LoanOffer loanOffer = loanOfferOutputPort.findById(loanRequestId);

        int tenorYears = loanOffer.getLoanProduct().getTenor();
        int totalMonths = tenorYears * FinancialConstants.MONTHS_PER_YEAR;
        int moratoriumMonths = loanOffer.getLoanProduct().getMoratorium();

        BigDecimal annualRate = BigDecimal.valueOf(loanOffer.getLoanProduct().getInterestRate())
                .divide(BigDecimal.valueOf(FinancialConstants.PERCENTAGE_BASE_INT), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(FinancialConstants.MONTHS_PER_YEAR), 10, RoundingMode.HALF_UP);

        BigDecimal principal = loanOffer.getAmountApproved().setScale(2, RoundingMode.HALF_UP);
        BigDecimal balance = principal;
        BigDecimal totalRepaid = BigDecimal.ZERO;

        // EMI calculation (for repayment phase only)
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRatePowN = onePlusRate.pow(totalMonths - moratoriumMonths);
        BigDecimal expectedMonthlyRepayment = principal.multiply(monthlyRate)
                .multiply(onePlusRatePowN)
                .divide(onePlusRatePowN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        LocalDate paymentDate = LocalDate.from(loanOffer.getDateTimeOffered()
                .with(TemporalAdjusters.lastDayOfMonth())
                .plusMonths(1));

        // 1) Moratorium period: interest accrues into balance
        for (int m = 1; m <= moratoriumMonths; m++) {
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

        // 2) Repayment period
        for (int m = 1; m <= totalMonths - moratoriumMonths; m++) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPayment = expectedMonthlyRepayment.subtract(interest).setScale(2, RoundingMode.HALF_UP);

            // Last installment adjustment
            if (m == (totalMonths - moratoriumMonths)) {
                principalPayment = balance;
                expectedMonthlyRepayment = principalPayment.add(interest).setScale(2, RoundingMode.HALF_UP);
                balance = BigDecimal.ZERO;
            } else {
                balance = balance.subtract(principalPayment).setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal amountPaid = expectedMonthlyRepayment;
            totalRepaid = totalRepaid.add(amountPaid).setScale(2, RoundingMode.HALF_UP);

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

            paymentDate = paymentDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        }

        // Add loan meta to last record
        RepaymentHistory last = repaymentSchedule.get(repaymentSchedule.size() - 1);
        last.setTenor(tenorYears);
        last.setMoratorium(moratoriumMonths);

        return repaymentSchedule;
    }



}
