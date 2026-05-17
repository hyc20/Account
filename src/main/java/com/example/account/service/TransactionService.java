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
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    /**
     * 정책 : 사용자 없는 경우,
     * 사용자 아이디와 계좌 소유주가 다른 경우,
     * 계좌가 이미 해지 상태인 경우,
     * 거래금액이 잔액보다 큰 경우,
     * 거래금액이 너무 작거나 큰 경우 실패 응답
     * - 해당 계좌에서 거래(사용, 사용 취소)가 진행 중일 때
     * 다른 거래 요청이 오는 경우 해당 거래가 동시에 잘못 처리
     * 되는 것을 방지해야 한다.
     *
     */

    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {
        //계좌 사용자를 찾는다 사용자가 없는 경우 USER_NOT_FOUND 예외 발생
        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
        //계좌를 찾는다 계좌 번호로 찾았는데 계좌가 없으면 ACCOUNT_NOT_FOUND 예외 발생
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateBalance(user, account, amount);

        account.useBalance(amount);

        return TransactionDto.fromEntity(saveAndGetTransaction(TransactionType.USE, TransactionResultType.SUCCESS, account, amount));
    }

    private void validateBalance(AccountUser user, Account account, Long amount) {
        //사용자 아이디와 계좌 소유주 아이디가 다른 경우, 소유주가 다르다는 예외 발생
        if (!Objects.equals(user.getId(), account.getAccountUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCHED);
        }
        // 계좌가 이미 해지 상태인 경우 계좌가 해지 상태라는 예외 발생
        if (account.getAccountStatus() != AccountStatus.IN_USE) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTRED);
        }
        //거래금액이 너무 작거나 큰 경우 실패 응답 계좌 금액보다 거래 금액이 크다는 예외 발생
        if (account.getBalance() < amount) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }

    }

    @Transactional
    public void saveFailedUseTransaction(String accountNumber, Long amount) {
        /**
         * 계좌번호로 계좌를 찾지 못하면 ACCOUNT_NOT_FOUND 예외
         * */
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(TransactionType.USE,TransactionResultType.FAIL, account, amount);
    }

    @Nonnull
    private Transaction saveAndGetTransaction(TransactionType transactionType,TransactionResultType resultType, Account account, Long amount) {
        return transactionRepository.save(
                Transaction.builder()
                        .transactionType(transactionType)
                        .transactionResultType(resultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnpaShot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build());
    }

    @Transactional
    public TransactionDto cancelBalance(String transactionId,String accountNumber, Long amount) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(()-> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));

        //계좌를 찾는다 계좌 번호로 찾았는데 계좌가 없으면 ACCOUNT_NOT_FOUND 예외 발생
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateCancelBalance(transaction, account, amount);
        account.cancelBalance(amount);

        return TransactionDto.fromEntity(saveAndGetTransaction(TransactionType.CANCEL, TransactionResultType.SUCCESS, account, amount));

    }
    private void validateCancelBalance(Transaction transaction, Account account, Long amount) {
        if(!Objects.equals(transaction.getAccount().getId(), account.getId())) {
            throw new AccountException(ErrorCode.TRANSACTION_ACCOUNT_UNMATCH);
        }
        if(!Objects.equals(transaction.getAmount(), amount)) {
            throw new AccountException(ErrorCode.CANCEL_MUST_FULLY);
        }
        if(transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))) {
            throw new AccountException(ErrorCode.TOO_OLD_TO_CANCEL);
        }
    }

    @Transactional
    public void saveFailedCancelUseTransaction(String accountNumber, Long amount) {
        /**
         * 계좌번호로 계좌를 찾지 못하면 ACCOUNT_NOT_FOUND 예외
         * */
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(TransactionType.CANCEL,TransactionResultType.FAIL, account, amount);
    }

    public TransactionDto queryTransaction(String transactionId) {
        return TransactionDto.fromEntity(transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(()  -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND)));

    }
}
