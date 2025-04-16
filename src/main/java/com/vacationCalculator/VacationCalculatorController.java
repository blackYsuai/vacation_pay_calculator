package com.vacationCalculator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
public class VacationCalculatorController {
    static final double averageDaysInMonth = 29.3; //  Среднее количество дней в месяце для рассчета отпускных


    @GetMapping("/calculate")
    public VacationResponse calculateVacationPay(
            @RequestParam("averageSalary") double averageSalary, //Средняя заработная плата
            @RequestParam(value = "vacationDays", required = false) Integer vacationDays, //Количество дней в отпуске
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, //Дата начала отпуска
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate //Дата окончания отпуска
    ) {
        if (averageSalary <= 0) {
            throw new IllegalArgumentException("Average salary must be greater than 0");
        }
        int quantityOfWorkDays; //Количество рабочих дней в отпуске
        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date must be after start date");
            }
            quantityOfWorkDays = countWorkDays(startDate, endDate);
        } else {
            if (vacationDays == null || vacationDays <= 0) {
                throw new IllegalArgumentException("Vacation days must be provided and greater than 0 if no dates are given");
            }
            quantityOfWorkDays = vacationDays;
        }

        double averageDaileEarnings = averageSalary / averageDaysInMonth; //Средний дневной заработок
        double result = averageDaileEarnings * quantityOfWorkDays; //Отпускные

        BigDecimal rounded = BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);

        return new VacationResponse("Vacation Pay Calculation:", rounded.doubleValue());
    }

    private int countWorkDays(LocalDate start, LocalDate end) {

        //Праздничные дни на 2025 год (с учетом переноса по постановлению правительства РФ)
        List<LocalDate> holidays = Arrays.asList(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2),
                LocalDate.of(2025, 1, 3),
                LocalDate.of(2025, 1, 6),
                LocalDate.of(2025, 1, 7),
                LocalDate.of(2025, 1, 8),
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 2),
                LocalDate.of(2025, 5, 8),
                LocalDate.of(2025, 5, 9),
                LocalDate.of(2025, 6, 12),
                LocalDate.of(2025, 6, 13),
                LocalDate.of(2025, 11, 4),
                LocalDate.of(2025, 12, 31)
        );

        int workDays = 0; //Количество рабочих дней
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY && !holidays.contains(date)) {
                workDays++;
            }
        }
        return workDays;
    }
}
