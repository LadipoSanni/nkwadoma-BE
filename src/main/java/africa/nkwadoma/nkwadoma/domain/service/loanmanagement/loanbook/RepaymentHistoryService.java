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

        MeedlValidator.validateUUID(loanRequestId,"Loan request id cannot ");

        LoanOffer loanOffer = loanOfferOutputPort.findById(loanRequestId);

        int totalMonths = loanOffer.getLoanProduct().getTenor() * FinancialConstants.MONTHS_PER_YEAR;

        BigDecimal annualRate = BigDecimal.valueOf(loanOffer.getLoanProduct().getInterestRate())
                .divide(BigDecimal.valueOf(FinancialConstants.PERCENTAGE_BASE_INT), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(FinancialConstants.MONTHS_PER_YEAR), 10, RoundingMode.HALF_UP);

        BigDecimal principal = loanOffer.getAmountApproved().setScale(2, RoundingMode.HALF_UP);

        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRatePowN = onePlusRate.pow(totalMonths);
        BigDecimal expectedMonthlyInterest = principal.multiply(monthlyRate)
                .multiply(onePlusRatePowN)
                .divide(onePlusRatePowN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);


        BigDecimal balance = principal;
        BigDecimal totalRepaid = BigDecimal.ZERO;
        LocalDate paymentDate = LocalDate.from(loanOffer.getDateTimeOffered()
                .with(TemporalAdjusters.lastDayOfMonth()).plusMonths(1));


        for (int eachMonth = 1 ; eachMonth <= totalMonths ; eachMonth++) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);

            BigDecimal principalPayment = expectedMonthlyInterest.subtract(interest).setScale(2, RoundingMode.HALF_UP);

            balance = balance.subtract(principalPayment).setScale(2, RoundingMode.HALF_UP);

            totalRepaid = totalRepaid.add(expectedMonthlyInterest).setScale(2, RoundingMode.HALF_UP);

            RepaymentHistory scheduleRepayment = RepaymentHistory.builder().
                    totalAmountRepaid(totalRepaid).amountOutstanding(balance).paymentDate(paymentDate)
                    .amountPaid(expectedMonthlyInterest).interestIncurred(interest).principalPayment(principalPayment)
                    .build();

            repaymentSchedule.add(scheduleRepayment);
            paymentDate = paymentDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        }

        repaymentSchedule.get(repaymentSchedule.size() - 1).setTenor(loanOffer.getLoanProduct().getTenor());
        repaymentSchedule.get(repaymentSchedule.size() - 1).setMoratorium(loanOffer.getLoanProduct().getMoratorium());

        return repaymentSchedule;
    }

}
