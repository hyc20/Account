package com.example.account.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

public class DeleteAccount {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request{ //createAccount 요청 시 필요한 정보 userId, initialBalance
        @NotNull //createAccount의 @Valid에서 인식하기 위해서 설정 필요
        @Min(1)  //userId는 최소값이 1부터고 not null
        private Long userId;

        @NotBlank  // 공백없이
        @Size(min = 10, max = 10) //계좌번호는 10개의 문자
        private String accountNumber;;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private Long userId;
        private String accountNumber;
        private LocalDateTime unregisteredAt;

        //AccountService에서 AccountController로 넘기는 응답을 위해 Response 타입으로 변환하는 메소드
        public static Response from(AccountDto accountDto){
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .unregisteredAt(accountDto.getUnregisteredAt()).build();
        }
    }
}
