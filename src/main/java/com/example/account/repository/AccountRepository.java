package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    //id 오름차순으로 정렬해서 첫번째 값을 가져오겠다
    //값을 찾는데 없을 수도 있기 때문에 Optional 타입으로 설정
    Optional<Account> findFirstByOrderByIdDesc();

    Integer countByAccountUser(AccountUser accountUser);


    Optional<Account> findByAccountNumber(String accountNumber);
}
