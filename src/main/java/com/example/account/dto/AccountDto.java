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
    private LocalDateTime unregisteredAt; /*계좌해지일시*/

    //Account 타입에서 AccountDto 타입으로 변환하는 메소드
    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .userId(account.getAccountUser().getId())//그냥 getId를 하면 nullexception 난다
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .registeredAt(account.getRegisteredAt())
                .unregisteredAt(account.getUnregisteredAt())
                .build();
    }
}

