package com.example.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("사용자가 없습니다."),
    MAX_COUND_PER_USER_10("사용자 소유 계좌는 최대 10개입니다"),
    ACCOUNT_NOT_FOUND("계좌가 없습니다"),
    USER_ACCOUNT_UNMATCHED("사용자와 계좌 소유주가 다릅니다"),
    ACCOUNT_ALREADY_UNREGISTRED("계좌가 이미 해제 되었습니다"),
    ACCOUNT_HAS_BALANACE("잔액이 있는 계좌는 해제할 수 없습니다"),
    AMOUNT_EXCEED_BALANCE("거래 금액이 계좌 잔액보다 큽니다");
    private final String description;
}
