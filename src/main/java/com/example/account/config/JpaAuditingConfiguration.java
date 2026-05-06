package com.example.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing/*@EntityListeners(AuditingEntityListener.class) 인식하기 위한 어노테이션*/
public class JpaAuditingConfiguration {

}
