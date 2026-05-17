package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountInfo;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request) {
                //Account 만들 때 필요한 정보는 userId와 초기 잔액 initialBalance
                //@Valid를 쓴다고 인식되는 게 아니라 Request 쪽에 설정 해줘야 한다
        return CreateAccount.Response.from(
                accountService.createAccount(
                        request.getUserId(),
                        request.getInitialBalance()));
    }


    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request) {
        //Account 만들 때 필요한 정보는 userId와 초기 잔액 initialBalance
        //@Valid를 쓴다고 인식되는 게 아니라 Request 쪽에 설정 해줘야 한다
        return DeleteAccount.Response.from(
                accountService.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber()));
    }



    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

    @GetMapping("/account")
    public List<AccountInfo> getAccountsByUserId(
            @RequestParam("user_id") Long userId
    ) {

        return accountService.getAccountsByUserId(userId).stream()
                        .map(accountDto->
                        AccountInfo.builder()
                                .accountNumber(accountDto.getAccountNumber())
                                .balance(accountDto.getBalance())
                                .build())
                                .collect(Collectors.toList());
    }


}
