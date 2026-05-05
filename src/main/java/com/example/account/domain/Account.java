package com.example.account.domain;

import com.example.account.type.AccountStatus;
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
@EntityListeners(AuditingEntityListener.class)
public class Account {
    @Id
    @GeneratedValue
    private Long id;    /*pk*/
    @ManyToOne
    private AccountUser accountUser; /*소유자 정보 사용자 테이블과 n:1*/

    private String accountNumber;   /*계좌번호*/

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus; /*계좌상태 IN_USE, UNREGISTERED*/

    private Long balance; /*계좌잔액*/
    private LocalDateTime registeredAt;   /*계좌등록일시*/
    private LocalDateTime unregisteredAt;/*계좌해지일시*/

    @CreatedDate
    private LocalDateTime createdAt;/*생성일시*/
    @LastModifiedDate
    private LocalDateTime updatedAt;/*최종수정일시*/
}
