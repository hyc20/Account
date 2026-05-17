package com.example.account.dto;

import com.example.account.aop.AccountLockIdInterface;
import com.example.account.type.TransactionResultType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class CancelBalance {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request implements AccountLockIdInterface {
        /**
         * 요청
         * "transactionId":"c2033bb6d82a4250aecf8e27c49b63f6"
         * "accountNumber":"1000000000",
         * "amount":1000
         */
        @NotNull
        @NotBlank
        private String transactionId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(1000)//최소 거래 금액
        @Max(1_000_000_000)//최대 거래 금액
        private Long amount;

    }

    /**
     * 응답
     "accountNumber":"1000000000",
     "transactionResult":"S",
     "transactionId":"5d011bb6d82cc50aecf8e27cdabb6772",
     "amount":1000,
     "transactedAt":"2022-06-01T23:26:14.671859"
     *
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String accountNumber;
        private TransactionResultType transactionResult; //SUCCESS, FAIL
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;

        public static Response fromEntity(TransactionDto transactionDto) {
            return Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResult(transactionDto.getTransactionResultType())
                    .transactionId(transactionDto.getTransactionId())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }

    }
}

