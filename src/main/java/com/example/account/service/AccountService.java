package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.account.type.AccountStatus.IN_USE;
import static com.example.account.type.AccountStatus.UNREGISTERED;
import static com.example.account.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    //사용자 조회를 위한 repository
    private final AccountUserRepository accountUserRepository;

    /**
     *  사용자 있는지 조회
     *  계좌번호 생성
     *  계좌를 저장 후 정보를 넘긴다
     * @param userId
     * @param initialBalance
     * @return
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {//userId, initialBalance로 계좌를 생성한다
        // AccountUserRepository의 JpaRepository<AccountUser, Long> 덕분에 findById 사용 가능
        // 사용자 있는지 findById로 조회하여 사용자를 조회 한다
        AccountUser accountUser = accountUserRepository.findById(userId)
                //값이 없는 경우 커스텀 exception AccountException 던진다
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        //1명의 계좌가 10개인 경우 exception 발생
        validateCreatAccoutn(accountUser);


        //계좌번호 생성
        //findFirstByOrderByIdDesc로 가장 최근의 값을 가져와서 새로 계좌번호를 새로 만든다
        String newAccountNumber =  accountRepository.findFirstByOrderByIdDesc()
                .map(account ->
                        //가장 마지막 계좌번호 가져와서 +1 한 값을 String으로 바꾼다
                        (Integer.parseInt(account.getAccountNumber()))+1+"")
                //만약 계좌번호 값이 없다면 계좌번호를 "100000000"로 설정한다
                .orElse("1000000000");

        //계좌정보를 컨트롤러 쪽으로 넘긴다
        //save는 Entity 클래스 사용할 때 트랜잭션이 없어서 오류가 날 수 있고
        //컨트롤러로 응답하는 부분에서 사용하는 데이터가 몇개가 필요할 지 모르기 때문에
        //그럴 때 Entity를 바꿀 수 없기 때문에 컨트롤러와 서비스 사이의 통신을 위한 DTO를 만들어서 사용한다
        //변수를 사용하지 않고 바로 return하는 이유는 중간에 로직이 추가되면 결과가 변경 될 수 있기 때문에
        return  AccountDto.fromEntity( accountRepository.save(
                //save할 대상은 Account, 따라서 Account의 정보를 설정해준다
                Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build()));
    }

    private void validateCreatAccoutn(AccountUser accountUser) {
        if(accountRepository.countByAccountUser(accountUser)>=10){
            throw new AccountException(MAX_COUND_PER_USER_10);
        }
    }

    @Transactional
    public Account getAccount(Long id){
        if(id < 0){
            throw new RuntimeException("Id is negative");
        }
        return accountRepository.findById(id).get();
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                //값이 없는 경우 커스텀 exception AccountException 던진다
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        //계좌번호 찾기
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new AccountException(ACCOUNT_NOT_FOUND));

        validateDeleteAccount(accountUser, account);

        account.setAccountStatus(UNREGISTERED);
        account.setUnregisteredAt(LocalDateTime.now());

        return  AccountDto.fromEntity(account);
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        if(!Objects.equals(accountUser.getId(), account.getAccountUser().getId())){
            throw new AccountException(USER_ACCOUNT_UNMATCHED);
        }

        if (account.getAccountStatus().equals(UNREGISTERED)) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTRED);
        }

        if(account.getBalance() > 0 ){
            throw new AccountException(ACCOUNT_HAS_BALANACE);
        }
    }

    @Transactional
    public List<AccountDto> getAccountsByUserId(Long userId) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                    .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        List<Account> accountList = accountRepository.findByAccountUser(accountUser);

        return accountList.stream().map(AccountDto::fromEntity).collect(Collectors.toList());
    }
}
