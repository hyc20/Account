package com.example.account.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

public class CreateAccount {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request{ //createAccount 요청 시 필요한 정보 userId, initialBalance
        @NotNull //createAccount의 @Valid에서 인식하기 위해서 설정 필요
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
    public static class Response{ //createAccount 응답으로 필요한 정보 userId, accountNumber, registeredAt
        private Long userId;
        private String accountNumber;
        private LocalDateTime registeredAt;

        //AccountService에서 AccountController로 넘기는 응답을 위해 Response 타입으로 변환하는 메소드
        public static Response from(AccountDto accountDto){
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .registeredAt(accountDto.getRegisteredAt()).build();
        }
    }
}
