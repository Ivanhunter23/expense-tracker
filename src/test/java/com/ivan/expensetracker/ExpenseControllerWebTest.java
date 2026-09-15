package com.ivan.expensetracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService expenseService;

    @Test
    void shouldReturnFieldErrorsForInvalidExpense() throws Exception {
        String requestBody =
                """
                        {
                          "description": " ",
                          "amount": 5
                        }
                        """;

        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.description").value("must not be blank"))
                .andExpect(jsonPath("$.errors.amount").doesNotExist())
                .andExpect(jsonPath("$.errors.category").value("must not be null"))
                .andExpect(jsonPath("$.errors.date").value("must not be null"));

        verifyNoInteractions(expenseService);
    }
}