package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.repository.AccountRepository;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks //accountRepository,accountUserRepository 2개의 Mock을 AccountService에 주입한다
    //@ExtendWith(MockitoExtension.class)를 통해서 Mock을 사용하게 된다
    private AccountService accountService;

    @Test //테스트코드용
    @DisplayName("계좌 조회 성공")
    void testSuccess() {
        //given
        given(accountRepository.findById(anyLong()))
                                    .willReturn(Optional.of(Account.builder()
                                            .accountStatus(AccountStatus.UNREGISTERED)
                                            .accountNumber("65789").build()));
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        //when
        Account account = accountService.getAccount(2345L);
        //then
        verify(accountRepository, times(1)).findById(captor.capture());
        verify(accountRepository, times(0)).save(any());
        assertEquals(2345L, captor.getValue());
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }
    @Test //테스트코드용
    @DisplayName("계좌 조회 실패")
    void testFailToSearchAccount() {
        //given
        //when
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> accountService.getAccount(-10L));
        //then

        assertEquals("Id is negative", runtimeException.getMessage());
    }
    @Test //테스트코드용
    @DisplayName("Test 이름 변경")
    void getAccount() {
        //given
        accountService.createAccount(anyLong(), anyLong());
        //when
        Account account = accountService.getAccount(1L);
        //then
        assertEquals("40000", account.getAccountNumber());
        assertEquals(AccountStatus.IN_USE, account.getAccountStatus());

    }

    @Test //테스트코드용
    void getAccount2() {
        //given
        accountService.createAccount(anyLong(), anyLong());
        //when
        Account account = accountService.getAccount(2L);
        //then
        assertEquals("40000", account.getAccountNumber());
        assertEquals(AccountStatus.IN_USE, account.getAccountStatus());

    }

    @Test
    void createAccountSuccess() {
        //given
        //AccountUser를 만드는데 id는 12, 이름은 Pobi로 만든다
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong())) //accountUserRepository에서 id로 유저를 찾으면
                .willReturn(Optional.of(user));//찾은 유저를 넘겨주면 테스트 결과를 넘긴다
        given(accountRepository.findFirstByOrderByIdDesc())//accountRepository에서 아이디로 오름차순정리한 것으로 계좌를 찾으면
                .willReturn(Optional.of(Account.builder() //  accountNumber가 100000012인 값을 가진 Account를 리턴한다
                        .accountNumber("100000012").build()));
        given(accountRepository.save(any())) //계좌 정로를 저장할 때 Account를 저장하는데
                .willReturn(Account.builder()
                        .accountUser(user)   //accountUser를 설정하고
                        .accountNumber("100000013").build());//가져온  accountNumber가 100000012보다 1큰 계좌번호를 가진 값으로 계좌번호를 설정한다

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        //userId가 1, 초기 잔액이 1000으로 계좌를 생성하면 AccoutDto가 생겨서 컨트롤러로 넘길 수 있게 된다
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        //then
        //accountRepository가 1번 저장을 할 건데 captor의 captrue로 저장할 정보를 저장한다 그 정보가 accountDto에 담긴 정보다
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("100000013", accountDto.getAccountNumber());
    }

    @Test
    void createFirstAccount() {
        //given
        //AccountUser를 만드는데 id는 12, 이름은 Pobi로 만든다
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong())) //accountUserRepository에서 id로 유저를 찾으면
                .willReturn(Optional.of(user));//찾은 유저를 넘겨주면 테스트 결과를 넘긴다
        given(accountRepository.findFirstByOrderByIdDesc())//accountRepository에서 아이디로 오름차순정리한 것으로 계좌를 찾으면
                .willReturn(Optional.empty()); // 계좌가 없을 때 경우
        given(accountRepository.save(any())) //계좌 정로를 저장할 때 Account를 저장하는데
                .willReturn(Account.builder()
                        .accountUser(user)   //accountUser를 설정하고
                        .accountNumber("100000000").build());//가져온  accountNumber가 100000012보다 1큰 계좌번호를 가진 값으로 계좌번호를 설정한다

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        //userId가 1, 초기 잔액이 1000으로 계좌를 생성하면 AccoutDto가 생겨서 컨트롤러로 넘길 수 있게 된다
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        //then
        //accountRepository가 1번 저장을 할 건데 captor의 captrue로 저장할 정보를 저장한다 그 정보가 accountDto에 담긴 정보다
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(15L, accountDto.getUserId());
        assertEquals("100000000", accountDto.getAccountNumber());
    }


    @Test
    @DisplayName("유저 없음 계좌 생성 실패")
    void createAccount_UserNotFound() {
        //given
        given(accountUserRepository.findById(anyLong())) //accountUserRepository에서 id로 유저를 찾으면
                .willReturn(Optional.empty());//유저가 없을 경우

        //when
        //유저가 없는 경유 exception 발생
        AccountException accountException = assertThrows(AccountException.class, () -> accountService.createAccount(1L, 1000L));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());
    }

    @Test //테스트코드용
    @DisplayName("유저 당 계좌 쵀대 수는 10")
    void createAccount_maxAccountIs10() {
        //given
        //AccountUser를 만드는데 id는 12, 이름은 Pobi로 만든다
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong())) //accountUserRepository에서 id로 유저를 찾으면
                .willReturn(Optional.of(user));//찾은 유저를 넘겨주면 테스트 결과를 넘긴다
        given(accountRepository.countByAccountUser(user))//accountRepository에서 아이디로 오름차순정리한 것으로 계좌를 찾으면
                .willReturn(10); // 계좌가 10개인 경우
        //when
        //유저가 없는 경유 exception 발생
        AccountException accountException = assertThrows(AccountException.class, () -> accountService.createAccount(1L, 1000L));

        //then
        assertEquals(ErrorCode.MAX_COUND_PER_USER_10, accountException.getErrorCode());
    }

    @Test
    void deleteAccountSuccess() {
        //given
        //AccountUser를 만드는데 id는 12, 이름은 Pobi로 만든다
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong())) //accountUserRepository에서 id로 유저를 찾으면
                .willReturn(Optional.of(user));//찾은 유저를 넘겨주면 테스트 결과를 넘긴다

        Account account = Account.builder()
                .accountUser(user)//  accountNumber가 100000012인 값을 가진 Account를 리턴한다
                .balance(0L)
                .accountStatus(AccountStatus.IN_USE)
                .accountNumber("100000012").build();
        given(accountRepository.findByAccountNumber(anyString()))//accountRepository에서 아이디로 오름차순정리한 것으로 계좌를 찾으면
                .willReturn(Optional.of(account));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        //userId가 1, 초기 잔액이 1000으로 계좌를 생성하면 AccoutDto가 생겨서 컨트롤러로 넘길 수 있게 된다
        AccountDto accountDto = accountService.deleteAccount(1L, "1234567890");

        //then
        assertEquals(12L, accountDto.getUserId());
        assertEquals("100000012", accountDto.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }


    @Test
    @DisplayName("해당 유저 없음 계좌 해지 실패")
    void deleteAccount_UserNotFound() {
        //given
        given(accountUserRepository.findById(anyLong())) //accountUserRepository에서 id로 유저를 찾으면
                .willReturn(Optional.empty());//유저가 없을 경우

        //when
        //유저가 없는 경유 exception 발생
        AccountException accountException = assertThrows(AccountException.class, () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());
    }

    @Test
    @DisplayName("해당 계좌 없음 - 계좌 해지 실패")
    void deleteAccount_AcoountNotFound() {
        //given
        //AccountUser를 만드는데 id는 12, 이름은 Pobi로 만든다
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong())) //accountUserRepository에서 id로 유저를 찾으면
                .willReturn(Optional.of(user));//찾은 유저를 넘겨주면 테스트 결과를 넘긴다

        given(accountRepository.findByAccountNumber(anyString()))//accountRepository에서 아이디로 오름차순정리한 것으로 계좌를 찾으면
                .willReturn(Optional.empty());

        AccountException accountException = assertThrows(AccountException.class, () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, accountException.getErrorCode());
    }

}