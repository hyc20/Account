package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountStatus;
import com.example.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
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
        accountService.createAccount();
        //when
        Account account = accountService.getAccount(1L);
        //then
        assertEquals("40000", account.getAccountNumber());
        assertEquals(AccountStatus.IN_USE, account.getAccountStatus());

    }

    @Test //테스트코드용
    void getAccount2() {
        //given
        accountService.createAccount();
        //when
        Account account = accountService.getAccount(2L);
        //then
        assertEquals("40000", account.getAccountNumber());
        assertEquals(AccountStatus.IN_USE, account.getAccountStatus());

    }
}