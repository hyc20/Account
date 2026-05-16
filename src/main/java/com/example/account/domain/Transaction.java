package com.example.account.domain;

import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)//생성시간과 업데이트 시간이 자동 업데이트
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;    /*pk 검색할 키, 순차적으로 증가하는 값*/

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; //USE, CANCEL
    @Enumerated(EnumType.STRING)
    private TransactionResultType transactionResultType; //SUCCESS FAIL

    @ManyToOne
    private Account account; /*특정 account가 거래 여러개와 연결되게 설정*/
    private Long amount;
    private Long balanceSnpaShot;

    private String transactionId;//거래 구별하는 아이디
    private LocalDateTime transactedAt;//거래 시간

    @CreatedDate/*자동으로 저장*/
    private LocalDateTime createdAt;/*생성일시*/
    @LastModifiedDate/*자동으로 저장*/
    private LocalDateTime updatedAt;/*최종수정일시*/

}
