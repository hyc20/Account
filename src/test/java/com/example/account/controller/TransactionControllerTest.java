package com.example.account.controller;

import com.example.account.dto.AccountDto;
import com.example.account.dto.CancelBalance;
import com.example.account.dto.TransactionDto;
import com.example.account.dto.UseBalance;
import com.example.account.service.TransactionService;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
        //테스트코드용
    void successUseBalance() throws Exception {
        //given
        given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .accountNumber("1000000000")
                        .transactionResultType(TransactionResultType.SUCCESS)
                        .transactionId("transactionId")
                        .amount(12345L)
                        .transactedAt(LocalDateTime.now())
                        .build());
        //when

        //then
        mockMvc.perform(post("/transaction/use")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(
                                new UseBalance.Request(1L, "2000000000", 3000L)
                        ))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1000000000"))
                .andExpect(jsonPath("$.transactionResult").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionId").value("transactionId"))
                .andExpect(jsonPath("$.amount").value(12345L));

    }
    @Test
        //테스트코드용
    void successCancelBalance() throws Exception {
        //given
        given(transactionService.cancelBalance(anyString(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .accountNumber("1000000000")
                        .transactionResultType(TransactionResultType.SUCCESS)
                        .transactionId("transactionId")
                        .amount(54321L)
                        .transactedAt(LocalDateTime.now())
                        .build());
        //when

        //then
        mockMvc.perform(post("/transaction/cancel")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(
                                new CancelBalance.Request("transcationId", "2000000000", 3000L)
                        ))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1000000000"))
                .andExpect(jsonPath("$.transactionResult").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionId").value("transactionId"))
                .andExpect(jsonPath("$.amount").value(54321L));

    }
    @Test
    void successQueryTransaction() throws Exception {
        //given
        given(transactionService.queryTransaction(anyString()))
                .willReturn(TransactionDto.builder()
                        .accountNumber("1000000000")
                        .transactionResultType(TransactionResultType.SUCCESS)
                        .transactionType(TransactionType.USE)
                        .transactedAt(LocalDateTime.now())
                        .transactionId("transactionForCancel")
                        .transactionType(TransactionType.USE)
                        .amount(54321L)
                        .build());

        //when

        //then
        mockMvc.perform(get("/transaction/12345"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1000000000"))
                .andExpect(jsonPath("$.transactionType").value("USE"))
                .andExpect(jsonPath("$.transactionResult").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionId").value("transactionForCancel"))
                .andExpect(jsonPath("$.amount").value(54321L));

    }
}