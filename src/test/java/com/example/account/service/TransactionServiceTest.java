package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test//테스트코드용
    void successUseBalance() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong())) //accountUserRepository에서 id로 유저를 찾으면
                .willReturn(Optional.of(user));//찾은 유저를 넘겨주면 테스트 결과를 넘긴다
        given(accountRepository.findByAccountNumber(anyString()))//accountRepository에서 걔좌 번호로 계좌를 찾는다
                .willReturn(Optional.of(Account.builder()
                                .accountUser(user)
                                .accountNumber("1000000000")
                                .accountStatus(AccountStatus.IN_USE)
                                .balance(10000L).build()));//  accountNumber가 100000012인 값을 가진 Account를 리턴한다
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(Account.builder()
                                        .accountUser(user)
                                        .accountNumber("1000000000")
                                        .accountStatus(AccountStatus.IN_USE)
                                        .balance(10000L)//  accountNumber가 100000012인 값을 가진 Account를 리턴한다
                                        .build())
                        .transactionType(TransactionType.USE)
                        .transactionResultType(TransactionResultType.SUCCESS)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnpaShot(9000L).build());
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        TransactionDto transactionDto = transactionService.useBalance(1L, "1000000000", 1000L);

        verify(transactionRepository, times(1)).save(captor.capture());


        //then
        assertEquals(TransactionResultType.SUCCESS, transactionDto.getTransactionResultType());
        assertEquals(TransactionType.USE, transactionDto.getTransactionType());
        assertEquals(9000L, transactionDto.getBalanceSnpaShot());
        assertEquals(1000L, transactionDto.getAmount());
        assertEquals(1000L, captor.getValue().getAmount());
        assertEquals(9000L, captor.getValue().getBalanceSnpaShot());
    }

    @Test
    @DisplayName("유저 없음 잔액 사용 실패")
    void useBalance_UserNotFound() {
        //given
        given(accountUserRepository.findById(anyLong())) //accountUserRepository에서 id로 유저를 찾는데
                .willReturn(Optional.empty());//유저가 없다

        //when
        //유저가 없는 경유 exception 발생
        AccountException accountException = assertThrows(AccountException.class, ()
                -> transactionService.useBalance(1L, "1000000000", 1000L));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());

    }

    @Test
    @DisplayName("해당 계좌 없음 잔액 사용 실패")
    void deleteAccount_AccountNotFound() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong())) //accountUserRepository에서 id로 유저를 찾으면
                .willReturn(Optional.of(user));//찾은 유저를 넘겨주면 테스트 결과를 넘긴다
        given(accountRepository.findByAccountNumber(anyString()))//accountRepository에서 계좌를 못 찾으면
                .willReturn(Optional.empty());

        //when
        //유저가 없는 경유 exception 발생
        AccountException accountException = assertThrows(AccountException.class, ()
                -> transactionService.useBalance(1L, "1000000000", 1000L));

        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, accountException.getErrorCode());
    }

    @Test
    @DisplayName("계좌 소유주 다름 잔액 사용 실패")
    void deleteAccountFailed_UserUnMatch() {
        //given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        AccountUser harry = AccountUser.builder()
                .id(13L)
                .name("harry").build();

        given(accountUserRepository.findById(anyLong())) //id로 찾았는데 pobi가 나왔음
                .willReturn(Optional.of(pobi));//찾은 유저를 넘겨주면 테스트 결과를 넘긴다

        given(accountRepository.findByAccountNumber(anyString())) //계좌번호로 찾으면 harry가 나왔음
                .willReturn(Optional.of(Account.builder()
                        .accountUser(harry)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        //when
        //소유주가 다른 경우
        AccountException accountException = assertThrows(AccountException.class, ()
                -> transactionService.useBalance(12L, "1234567890", 1000L));

        //then
        assertEquals(ErrorCode.USER_ACCOUNT_UNMATCHED, accountException.getErrorCode());
    }

    @Test
    @DisplayName("해지 계좌는 사용 수 없다")
    void deleteAccountFailed_alreadyUnregistered() {
        //given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong())) //accountUserRepository에서 id로 유저를 찾으면
                .willReturn(Optional.of(pobi));//찾은 유저를 넘겨주면 테스트 결과를 넘긴다

        given(accountRepository.findByAccountNumber(anyString()))//accountRepository에서 아이디로 오름차순정리한 것으로 계좌를 찾으면
                .willReturn(Optional.of(Account.builder()
                        .accountUser(pobi)//  accountNumber가 100000012인 값을 가진 Account를 리턴한다
                        .balance(0L)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("100000012").build()));

        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1234567890", 1000L));
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTRED, accountException.getErrorCode());
    }

    @Test//테스트코드용
    @DisplayName("거래 금액이 잔액보다 큰 경우")
    void exceedAmount_UseBalance() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong())) //accountUserRepository에서 id로 유저를 찾으면
                .willReturn(Optional.of(user));//찾은 유저를 넘겨주면 테스트 결과를 넘긴다
        given(accountRepository.findByAccountNumber(anyString()))//accountRepository에서 걔좌 번호로 계좌를 찾는다
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000000")
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(100L).build()));//계좌에 100원만 있음

        //when
        //1000을 사용하려고 한다
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1234567890", 1000L));

        //then
        assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE, accountException.getErrorCode());
        //에러코드를 던지고 0번 저장한다 - 저장을 하지 않는다
        verify(transactionRepository, times(0)).save(any());
    }

    @Test//테스트코드용
    @DisplayName("실패 트랜잭션 저장 성공")
    void saveFailedUseTransation() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountRepository.findByAccountNumber(anyString()))//accountRepository에서 걔좌 번호로 계좌를 찾는다
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000000")
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(10000L).build()));//  accountNumber가 100000012인 값을 가진 Account를 리턴한다
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(Account.builder()
                                .accountUser(user)
                                .accountNumber("1000000000")
                                .accountStatus(AccountStatus.IN_USE)
                                .balance(10000L)//  accountNumber가 100000012인 값을 가진 Account를 리턴한다
                                .build())
                        .transactionType(TransactionType.USE)
                        .transactionResultType(TransactionResultType.SUCCESS)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnpaShot(9000L).build());
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        transactionService.saveFailedUseTransaction("1000000000", 1000L);

        verify(transactionRepository, times(1)).save(captor.capture());

        //then
        assertEquals(1000L, captor.getValue().getAmount());
        assertEquals(TransactionResultType.FAIL, captor.getValue().getTransactionResultType());
        assertEquals(10000L, captor.getValue().getBalanceSnpaShot());


    }
}