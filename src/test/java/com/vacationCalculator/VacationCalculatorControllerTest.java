package com.vacationCalculator;

import com.vacationCalculator.VacationCalculatorController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VacationCalculatorController.class)
public class VacationCalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testVacationCalculation_ByDays() throws Exception {
        mockMvc.perform(get("/calculate")
                        .param("averageSalary", "60000")
                        .param("vacationDays", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Vacation Pay Calculation:"))
                .andExpect(jsonPath("$.amount").value(20477.82));
    }

    @Test
    public void testVacationCalculation_ByDates() throws Exception {
        mockMvc.perform(get("/calculate")
                        .param("averageSalary", "60000")
                        .param("startDate", "2025-04-15")
                        .param("endDate", "2025-04-21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Vacation Pay Calculation:"))
                .andExpect(jsonPath("$.amount").value(10238.91)); // 6 рабочих дней * 2047.78
    }

    @Test
    public void testVacationCalculation_InvalidSalary() throws Exception {
        mockMvc.perform(get("/calculate")
                        .param("averageSalary", "0")
                        .param("vacationDays", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Average salary must be greater than 0")));
    }

    @Test
    public void testVacationCalculation_MissingAllInputs() throws Exception {
        mockMvc.perform(get("/calculate")
                        .param("averageSalary", "60000"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Vacation days must be provided")));
    }

    @Test
    public void testVacationCalculation_EndBeforeStart() throws Exception {
        mockMvc.perform(get("/calculate")
                        .param("averageSalary", "60000")
                        .param("startDate", "2025-04-21")
                        .param("endDate", "2025-04-15"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("End date must be after start date")));
    }
}
