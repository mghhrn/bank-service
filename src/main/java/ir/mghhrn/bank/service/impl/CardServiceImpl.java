package ir.mghhrn.bank.service.impl;

import ir.mghhrn.bank.dto.AuthenticationRequestDto;
import ir.mghhrn.bank.dto.AuthenticationResponseDto;
import ir.mghhrn.bank.dto.AccountTransactionResponseDto;
import ir.mghhrn.bank.entity.Card;
import ir.mghhrn.bank.entity.Customer;
import ir.mghhrn.bank.entity.Session;
import ir.mghhrn.bank.enums.AuthenticationMode;
import ir.mghhrn.bank.exception.BankGeneralException;
import ir.mghhrn.bank.repository.CardRepository;
import ir.mghhrn.bank.repository.SessionRepository;
import ir.mghhrn.bank.service.AccountService;
import ir.mghhrn.bank.service.CardService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CardServiceImpl implements CardService {

    private static final Logger logger = LogManager.getLogger(CardServiceImpl.class);

    private final CardRepository cardRepository;
    private final SessionRepository sessionRepository;
    private final AccountService accountService;

    public CardServiceImpl(CardRepository cardRepository,
                           SessionRepository sessionRepository,
                           AccountService accountService) {
        this.cardRepository = cardRepository;
        this.sessionRepository = sessionRepository;
        this.accountService = accountService;
    }

    @Transactional
    @Override
    public AuthenticationResponseDto authenticateCard(AuthenticationRequestDto requestDto) {
        Card card = cardRepository.findByCardNumber(requestDto.getCardNumber())
                .orElseThrow(() -> new BankGeneralException("card not found"));
        if (card.getBlocked()) {
            throw new BankGeneralException("card is blocked");
        }
        Customer customer = card.getAccount().getCustomer();
        switch (requestDto.getAuthenticationMode()) {
            case PIN_NUMBER:
                if (!requestDto.getPinCode().equals(card.getPinCode())) {
                    throw new BankGeneralException("given pin is wrong");
                }
                break;
            case FINGERPRINT:
                if (!requestDto.getFingerprint().equals(card.getAccount().getCustomer().getFingerPrintInBase64())) {
                    throw new BankGeneralException("fingerprint does not match");
                }
        }
        Session session = new Session();
        session.setCustomer(customer);
        session.setCustomerId(customer.getId());
        session.setCard(card);
        session.setCardId(card.getId());
        session.setCreatedAt(LocalDateTime.now());
        session.setToken(UUID.randomUUID().toString());
        sessionRepository.save(session);
        AuthenticationResponseDto dto = new AuthenticationResponseDto();
        dto.setToken(session.getToken());
        return dto;
    }

    /**
     *
     * @param withdrawAmount The amount that is going to be withdrawn from the account.
     * @param authenticationToken The token of current session.
     *
     * Using optimistic locking technic to ensure data integrity of account balance
     */
    @Override
    @Transactional(readOnly = true)
    public AccountTransactionResponseDto withdraw(Double withdrawAmount, String authenticationToken) {
        Session session = sessionRepository.findByToken(authenticationToken)
                .orElseThrow(() -> new BankGeneralException("token is invalid"));
        Card card = cardRepository.findById(session.getCardId())
                .orElseThrow(() -> new BankGeneralException("session not found"));
        String accountTransactionLogNumber = UUID.randomUUID().toString();
        try {
            accountService.withdraw(card.getAccountId(), withdrawAmount);
            logger.info("successfully withdrew amount of {} from account with id {} having account transaction number as {}",
                    withdrawAmount, card.getAccountId(), accountTransactionLogNumber);
        } catch (ObjectOptimisticLockingFailureException e) {
            logger.info("another request has changed the account, " +
                    "we try again and if it is not successful, we will return an error to the user", e);
            accountService.withdraw(card.getAccountId(), withdrawAmount);
            logger.info("successfully withdrew amount of {} from account with id {} having account transaction number as {}",
                    withdrawAmount, card.getAccountId(), accountTransactionLogNumber);
        }
        AccountTransactionResponseDto responseDto = new AccountTransactionResponseDto();
        responseDto.setAccountTransactionNumber(accountTransactionLogNumber);
        responseDto.setCardNumber(card.getCardNumber());
        responseDto.setAccountId(card.getAccountId());
        return responseDto;
    }

    /**
     * Using optimistic locking technique to ensure data integrity of account balance.
     *
     * @param depositAmount The amount that is going to be withdrawn from the account.
     * @param authenticationToken The token of current session.
     * @return An instance of AccountTransactionResponseDto.
     */
    @Override
    public AccountTransactionResponseDto deposit(Double depositAmount, String authenticationToken) {
        Session session = sessionRepository.findByToken(authenticationToken)
                .orElseThrow(() -> new BankGeneralException("token is invalid"));
        Card card = cardRepository.findById(session.getCardId())
                .orElseThrow(() -> new BankGeneralException("session not found"));
        String accountTransactionLogNumber = UUID.randomUUID().toString();
        try {
            accountService.deposit(card.getAccountId(), depositAmount);
            logger.info("successfully deposited amount of {} from account with id {} having account transaction number as {}",
                    depositAmount, card.getAccountId(), accountTransactionLogNumber);
        } catch (ObjectOptimisticLockingFailureException e) {
            logger.info("another request has changed the account, " +
                    "we try again and if it is not successful, we will return an error to the user", e);
            accountService.deposit(card.getAccountId(), depositAmount);
            logger.info("successfully deposited amount of {} from account with id {} having account transaction number as {}",
                    depositAmount, card.getAccountId(), accountTransactionLogNumber);
        }
        AccountTransactionResponseDto responseDto = new AccountTransactionResponseDto();
        responseDto.setAccountTransactionNumber(accountTransactionLogNumber);
        responseDto.setCardNumber(card.getCardNumber());
        responseDto.setAccountId(card.getAccountId());
        return responseDto;
    }

    @Override
    @Transactional
    public String changeAuthenticationMode(AuthenticationMode authenticationMode, String authenticationToken) {
        Session session = sessionRepository.findByToken(authenticationToken)
                .orElseThrow(() -> new BankGeneralException("token is invalid"));
        Card card = cardRepository.findById(session.getCardId())
                .orElseThrow(() -> new BankGeneralException("session not found"));
        card.setAuthenticationMode(authenticationMode);
        cardRepository.save(card);
        return card.getCardNumber();
    }
}
