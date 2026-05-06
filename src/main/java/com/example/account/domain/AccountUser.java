package com.example.account.domain;

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
@EntityListeners(AuditingEntityListener.class)/*자동으로 증가하는 값을 인식하기 위한 어노테이션 JpaAuditingConfiguration로 관리\*/
public class AccountUser {//계좌 사용자 정보
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//자동으로 값을 생성해준다
    private Long id;

    private String name;

    @CreatedDate/*자동으로 저장*/
    private LocalDateTime createdAt;//테이블의 메타정보

    @LastModifiedDate/*자동으로 저장*/
    private LocalDateTime updatedAt;//테이블의 메타정보


}
