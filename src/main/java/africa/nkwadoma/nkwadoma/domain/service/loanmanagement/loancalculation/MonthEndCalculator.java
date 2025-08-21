package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class MonthEndCalculator {
    public static List<LocalDate> getMonthEnds(LocalDateTime startDateTime, LocalDate endDate) {
        List<LocalDate> monthEnds = new ArrayList<>();

        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate effectiveEndDate = (endDate != null) ? endDate : LocalDate.now();

        // Start from the month of startDate
        YearMonth currentMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(effectiveEndDate);

        // If end date is NOT month-end, stop at the previous month
        if (!effectiveEndDate.equals(endMonth.atEndOfMonth())) {
            endMonth = endMonth.minusMonths(1);
        }

        while (!currentMonth.isAfter(endMonth)) {
            LocalDate monthEnd = currentMonth.atEndOfMonth();
            if (!monthEnd.isBefore(startDate)) {
                monthEnds.add(monthEnd);
            }
            currentMonth = currentMonth.plusMonths(1);
        }

        return monthEnds;
    }





        public static BigDecimal calculateOutstanding(
                LocalDate startDate,
                BigDecimal loanAmount,
                List<RepaymentHistory> repayments,
                LocalDate today
        ) {
            BigDecimal outstanding = loanAmount;
            BigDecimal monthlyInterestAccrued = BigDecimal.ZERO;

            LocalDate currentDate = startDate;
            int repaymentIndex = 0;

            while (!currentDate.isAfter(today)) {
                // daily interest accrual
//                BigDecimal dailyInterest = outstanding.multiply(dailyRate);
                monthlyInterestAccrued = monthlyInterestAccrued.add(dailyInterest);

                // check if repayment on this day
                while (repaymentIndex < repayments.size() &&
                        repayments.get(repaymentIndex).getPaymentDateTime().toLocalDate().isEqual(currentDate)) {
                    BigDecimal payment = repayments.get(repaymentIndex).getAmountPaid();
                    outstanding = outstanding.subtract(payment);
                    repaymentIndex++;
                }

                // if end of month and not the current month
                if (isEndOfMonth(currentDate) && !isSameMonth(currentDate, today)) {
                    outstanding = outstanding.add(monthlyInterestAccrued);
                    monthlyInterestAccrued = BigDecimal.ZERO;
                }

                currentDate = currentDate.plusDays(1);
            }

            // For current month → interest not yet added, just accrued
            return outstanding.add(monthlyInterestAccrued);
        }

        private static boolean isEndOfMonth(LocalDate date) {
            return date.getDayOfMonth() == date.lengthOfMonth();
        }

        private static boolean isSameMonth(LocalDate d1, LocalDate d2) {
            return d1.getMonth() == d2.getMonth() && d1.getYear() == d2.getYear();
        }

}
