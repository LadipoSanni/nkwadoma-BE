package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.FinancialConstants.*;

public class Simulation {

        // your existing method
        public BigDecimal calculateInterest(double interestRate, BigDecimal outstanding, long daysBetween) {
            BigDecimal interestRateInPercent = BigDecimal.valueOf(interestRate)
                    .divide(PERCENTAGE_BASE, NUMBER_OF_DECIMAL_PLACES + ADDITIONAL_PRECISION_SCALE, RoundingMode.HALF_UP);
            BigDecimal dailyRate = interestRateInPercent
                    .divide(BigDecimal.valueOf(DAYS_IN_YEAR), NUMBER_OF_DECIMAL_PLACES, RoundingMode.HALF_UP);

            System.out.println("Calculated daily rate = " + dailyRate
                    + " for annual interest rate " + interestRate
                    + ", interest rate in percent " + interestRateInPercent);

            return outstanding.multiply(dailyRate).multiply(BigDecimal.valueOf(daysBetween));
        }

    /**
     * A) Daily counts, but ONLY compound at month end.
     * - We loop each day and accumulate into a monthly bucket.
     * - At the end of each month, we add the bucket to outstanding.
     * - Then we RESET the bucket to zero.
     */
    public BigDecimal compoundDailyCountsButApplyMonthly() {
        BigDecimal outstanding = BigDecimal.valueOf(5_000_000);
        double interestRate = 10.0;

        int[] daysInMonths = {28, 31, 30, 31}; // Feb, Mar, Apr, May
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (int month = 0; month < daysInMonths.length; month++) {
            int days = daysInMonths[month];

            // Monthly accumulator (this is what "returns to zero" at month end)
            BigDecimal monthlyAccrued = BigDecimal.ZERO;

            // IMPORTANT: outstanding must not change within the month
            BigDecimal monthOpeningOutstanding = outstanding;

            for (int d = 1; d <= days; d++) {
                BigDecimal dailyInterest = calculateInterest(interestRate, monthOpeningOutstanding, 1);
                monthlyAccrued = monthlyAccrued.add(dailyInterest);
            }

            // Apply monthly compounding at EOM
            totalInterest = totalInterest.add(monthlyAccrued);
            outstanding = outstanding.add(monthlyAccrued);

            System.out.println("Month " + (month + 1) + " (" + days + " days):");
            System.out.println("  Opening Outstanding: " + monthOpeningOutstanding);
            System.out.println("  Monthly Accrued (to apply): " + monthlyAccrued);
            System.out.println("  New Outstanding: " + outstanding);

            // RESET accumulator to zero for the next month (this is your requirement)
            monthlyAccrued = BigDecimal.ZERO;
            System.out.println("  Monthly accumulator reset to: " + monthlyAccrued);
        }

        System.out.println("Total interest (daily-counts, apply monthly) = " + totalInterest);
        System.out.println("Final outstanding = " + outstanding);

        return totalInterest;
    }

    /**
     * B) Single-shot monthly compounding:
     * - Compute interest once per month using daysInMonth.
     * - Add to outstanding at EOM.
     */
    public BigDecimal compoundMonthlySingleShot() {
        BigDecimal outstanding = BigDecimal.valueOf(5_000_000);
        double interestRate = 10.0;

        int[] daysInMonths = {28, 31, 30, 31};
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (int month = 0; month < daysInMonths.length; month++) {
            int days = daysInMonths[month];
            BigDecimal opening = outstanding;

            BigDecimal monthlyInterest = calculateInterest(interestRate, opening, days);

            totalInterest = totalInterest.add(monthlyInterest);
            outstanding = outstanding.add(monthlyInterest);

            System.out.println("Month " + (month + 1) + " (" + days + " days):");
            System.out.println("  Opening Outstanding: " + opening);
            System.out.println("  Monthly Interest: " + monthlyInterest);
            System.out.println("  New Outstanding: " + outstanding);
        }

        System.out.println("Total interest (single-shot monthly) = " + totalInterest);
        System.out.println("Final outstanding = " + outstanding);

        return totalInterest;
    }

    // Test runner
    public static void main(String[] args) {
        Simulation sim = new Simulation();

        System.out.println("=== DAILY COUNTS (apply monthly) ===");
        BigDecimal dailyCountsMonthlyApply = sim.compoundDailyCountsButApplyMonthly();

        System.out.println("\n=== SINGLE-SHOT MONTHLY ===");
        BigDecimal monthlySingleShot = sim.compoundMonthlySingleShot();

        System.out.println("\n=== COMPARISON ===");
        int cmp = dailyCountsMonthlyApply.compareTo(monthlySingleShot);
        if (cmp == 0) {
            System.out.println("✅ SAME total interest for both strategies.");
        } else {
            System.out.println("⚠️ DIFFERENT totals. Difference = " +
                    dailyCountsMonthlyApply.subtract(monthlySingleShot));
        }
    }


}
