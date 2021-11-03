package ir.mghhrn.bank.service.impl;

import ir.mghhrn.bank.entity.Account;
import ir.mghhrn.bank.exception.BankGeneralException;
import ir.mghhrn.bank.repository.AccountRepository;
import ir.mghhrn.bank.service.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LogManager.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void withdraw(Long accountId, Double withdrawAmount) {
        Account account = getAccount(accountId);
        if (account.getBalance() < withdrawAmount) {
            throw new BankGeneralException("balance is lower than the requested amount");
        }
        account.setBalance(account.getBalance() - withdrawAmount);
        accountRepository.save(account);
    }

    @Override
    public void deposit(Long accountId, Double depositAmount) {
        Account account = getAccount(accountId);
        if (depositAmount <= 0) {
            throw new BankGeneralException("invalid deposit amount");
        }
        account.setBalance(account.getBalance() + depositAmount);
        accountRepository.save(account);
    }

    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BankGeneralException("account not found"));
    }
}
