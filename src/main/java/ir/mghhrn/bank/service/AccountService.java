package ir.mghhrn.bank.service;

public interface AccountService {
    void withdraw(Long accountId, Double withdrawAmount);
    void deposit(Long accountId, Double depositAmount);
}
