package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

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
    public static int getDaysInMonth(LocalDate date) {
        YearMonth ym = YearMonth.from(date);
        return date.getDayOfMonth();
    }

//    public static void main(String[] args) {
//        LocalDateTime start = LocalDateTime.of(2025, 1, 15, 0, 0);
//        LocalDate end = LocalDate.of(2025, 4, 10); // Try null or month-end too
//
//        List<LocalDate> result = getMonthEnds(start, end);
////        result.forEach(date -> System.out.println(""+getDaysInMonth(date)));
//        System.out.println(""+getDaysInMonth(LocalDate.of(2020, 4, 30)));
////        result.forEach(System.out::println);
//    }
}
