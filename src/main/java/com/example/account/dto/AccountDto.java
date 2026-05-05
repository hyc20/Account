package com.example.account.dto;

import com.example.account.domain.Account;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private Long userId;    /*pk*/

    private String accountNumber;   /*계좌번호*/

    private Long balance; /*계좌잔액*/
    private LocalDateTime registeredAt;   /*계좌등록일시*/
    private LocalDateTime unregisteredAt;/*계좌해지일시*/

    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .userId(account.getId())
                .accountNumber(account.getAccountNumber())
                .registeredAt(account.getRegisteredAt())
                .unregisteredAt(account.getUnregisteredAt())
                .build();
    }
}

