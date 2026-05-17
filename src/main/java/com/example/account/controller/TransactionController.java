package com.example.account.controller;

import com.example.account.aop.AccountLock;
import com.example.account.dto.CancelBalance;
import com.example.account.dto.QueryTransactionResponse;
import com.example.account.dto.TransactionDto;
import com.example.account.dto.UseBalance;
import com.example.account.exception.AccountException;
import com.example.account.service.AccountService;
import com.example.account.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 잔액 관련 컨트롤러
 * 1. 잔액 사용
 * 2. 잔액 사용 취소
 * 3. 거래 확인
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @PostMapping("/transaction/use")
    @AccountLock
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request) throws InterruptedException {
        try {
            Thread.sleep(5000L);//2개의 요청을 보내는 것은 까다롭기에 스레드 sleep을 사용
            return UseBalance.Response.fromEntity(transactionService.useBalance(request.getUserId(),
                    request.getAccountNumber(), request.getAmount()));
        } catch (AccountException e) {
            log.error("failed to use balance");

            transactionService.saveFailedUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount());

            throw e;
        }
    }

    @PostMapping("/transaction/cancel")
    @AccountLock
    public CancelBalance.Response useCancel(
            @Valid @RequestBody CancelBalance.Request request) {

        try {
            return CancelBalance.Response.fromEntity(transactionService.cancelBalance(request.getTransactionId(),
                    request.getAccountNumber(), request.getAmount()));
        } catch (AccountException e) {
            log.error("failed to cancel balance");

            transactionService.saveFailedCancelUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount());

            throw e;
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(
            @PathVariable String transactionId) {
        return QueryTransactionResponse.fromEntity(transactionService.queryTransaction(transactionId));

    }

}
