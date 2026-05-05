package com.example.account.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

public class CreateAccount {

    @Getter
    @Setter
    public static class Request{
        @NotNull //createAccount의 @Valid에서
        @Min(1)  //userId는 최소값이 1부터고 not null
        private Long userId;
        @NotNull  //not null
        @Min(100) //계좌 개설 위해 initialBalance는 100원부터 입금가능
        private Long initialBalance;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private Long userId;
        private String accountNumber;
        private LocalDateTime registeredAt;

        public static Response from(AccountDto accountDto){
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .registeredAt(accountDto.getRegisteredAt()).build();
        }
    }
}
