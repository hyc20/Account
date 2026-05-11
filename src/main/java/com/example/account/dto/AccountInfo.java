package com.example.account.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountInfo { //클라이언트와 DTO 사이의 정보 교환
    private String accountNumber;   /*계좌번호*/
    private Long balance; /*계좌잔액*/
}
