package com.example.account.domain;

import com.example.account.exception.AccountException;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)/*자동으로 증가하는 값을 인식하기 위한 어노테이션 JpaAuditingConfiguration로 관리*/
public class Account {
    @Id
    @GeneratedValue
    private Long id;    /*pk 검색할 키, 순차적으로 증가하는 값*/
    @ManyToOne
    private AccountUser accountUser; /*소유자 정보 사용자 테이블과 n:1 계좌n개가 사용자 1명과 연결, 다른 서비스 사용자와 차별 위해 Account전용 user테이블 설계*/

    private String accountNumber;   /*계좌번호*/

    @Enumerated(EnumType.STRING)/*이렇게 설정해야 DB에 숫자가 아닌 IN_USE, UNREGISTERED 값이 등록된다*/
    private AccountStatus accountStatus; /*계좌상태 IN_USE, UNREGISTERED*/

    private Long balance; /*계좌잔액*/
    private LocalDateTime registeredAt;   /*계좌등록일시*/
    private LocalDateTime unregisteredAt;/*계좌해지일시*/

    @CreatedDate/*자동으로 저장*/
    private LocalDateTime createdAt;/*생성일시*/
    @LastModifiedDate/*자동으로 저장*/
    private LocalDateTime updatedAt;/*최종수정일시*/

    public void useBalance(Long amount){
        if(amount > balance){
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        this.balance = this.balance - amount;
    }
    public void cancelBalance(Long amount){
        if(amount < 0){
            throw new AccountException(ErrorCode.INVALID_REQUEST);
        }
        balance = this.balance + amount;
    }
}
