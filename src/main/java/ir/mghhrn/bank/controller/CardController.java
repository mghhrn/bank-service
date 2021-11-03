package ir.mghhrn.bank.controller;

import ir.mghhrn.bank.dto.*;
import ir.mghhrn.bank.entity.Card;
import ir.mghhrn.bank.entity.Session;
import ir.mghhrn.bank.enums.AuthenticationMode;
import ir.mghhrn.bank.exception.BankGeneralException;
import ir.mghhrn.bank.repository.CardRepository;
import ir.mghhrn.bank.repository.SessionRepository;
import ir.mghhrn.bank.service.CardService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/card")
public class CardController {

    private static final Logger logger = LogManager.getLogger(CardController.class);
    private static final String AUTHENTICATION_TOKEN_HEADER = "AUTHENTICATION_TOKEN";

    private final CardRepository cardRepository;
    private final CardService cardService;
    private final SessionRepository sessionRepository;

    public CardController(
            CardRepository cardRepository,
            CardService cardService,
            SessionRepository sessionRepository) {
        this.cardRepository = cardRepository;
        this.cardService = cardService;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/verification")
    public ResponseEntity<CardVerificationResponseDto> verifyCard(
            @RequestBody @Valid CardVerificationRequestDto requestDto) {
        Card card = cardRepository.findByCardNumber(requestDto.getCardNumber())
                .orElseThrow(() -> new BankGeneralException("card not found"));
        if (card.getBlocked()) {
            throw new BankGeneralException("card is blocked");
        }
        AuthenticationMode authenticationMode = card.getAuthenticationMode();
        ResponseEntity<CardVerificationResponseDto> responseEntity =
                ResponseEntity.ok(new CardVerificationResponseDto(authenticationMode));
        logger.info("Card verification successfully finished for card number {} in atm {}",
                requestDto.getCardNumber(), requestDto.getAtmSerialNumber());
        return responseEntity;
    }

    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponseDto> authenticateCard(
            @RequestBody @Valid AuthenticationRequestDto requestDto) {
        AuthenticationResponseDto responseDto = cardService.authenticateCard(requestDto);
        logger.info("Card authentication successfully finished for card number {} in atm {}",
                requestDto.getCardNumber(), requestDto.getAtmSerialNumber());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/close-session")
    public ResponseEntity<Void> closeSession(
            @RequestHeader(AUTHENTICATION_TOKEN_HEADER) String authenticationToken) {
        Session session = sessionRepository.findByToken(authenticationToken)
                .orElseThrow(() -> new BankGeneralException("token is invalid"));
        sessionRepository.deleteById(session.getId());
        logger.info("The session is closed successfully for card with id {}", session.getCardId());
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/account/check-balance")
    public ResponseEntity<CheckBalanceResponseDto> getBalance(
            @RequestHeader(AUTHENTICATION_TOKEN_HEADER) String authenticationToken) {
        Session session = sessionRepository.findByToken(authenticationToken)
                .orElseThrow(() -> new BankGeneralException("token is invalid"));
        Card card = cardRepository.findById(session.getCardId())
                .orElseThrow(() -> new BankGeneralException("session not found"));
        Double accountBalance = card.getAccount().getBalance();
        CheckBalanceResponseDto responseDto = new CheckBalanceResponseDto();
        responseDto.setBalance(accountBalance);
        ResponseEntity<CheckBalanceResponseDto> responseEntity = ResponseEntity.ok(responseDto);
        logger.info("Getting account balance was successful for card number {}", card.getCardNumber());
        return responseEntity;
    }

    @PutMapping("/account/withdraw")
    public ResponseEntity<AccountTransactionResponseDto> withdrawMoney(
            @RequestBody @Valid WithdrawRequestDto requestDto,
            @RequestHeader(AUTHENTICATION_TOKEN_HEADER) String authenticationToken) {
        AccountTransactionResponseDto responseDto = cardService.withdraw(requestDto.getAmount(), authenticationToken);
        ResponseEntity<AccountTransactionResponseDto> responseEntity = ResponseEntity.ok(responseDto);
        logger.info("successfully withdrew amount of {} from card with number as {}", requestDto.getAmount(), responseDto.getCardNumber());
        return responseEntity;
    }


    @PutMapping("/account/deposit")
    public ResponseEntity<AccountTransactionResponseDto> depositMoney(
            @RequestBody @Valid DepositRequestDto requestDto,
            @RequestHeader(AUTHENTICATION_TOKEN_HEADER) String authenticationToken) {
        AccountTransactionResponseDto responseDto = cardService.deposit(requestDto.getDepositAmount(), authenticationToken);
        ResponseEntity<AccountTransactionResponseDto> responseEntity = ResponseEntity.ok(responseDto);
        logger.info("successfully deposited amount of {} from card with number as {}", requestDto.getDepositAmount(), responseDto.getCardNumber());
        return responseEntity;
    }

    @PutMapping("/authentication-mode")
    public ResponseEntity<Void> changeAuthenticationMode(
            @RequestBody @Valid AuthenticationModeRequestDto requestDto,
            @RequestHeader(AUTHENTICATION_TOKEN_HEADER) String authenticationToken) {
        String cardNumber = cardService.changeAuthenticationMode(requestDto.getAuthenticationMode(), authenticationToken);
        logger.info("authentication mode for card number {} has changed successfully to {}",
                cardNumber, requestDto.getAuthenticationMode());
        return ResponseEntity.ok().build();
    }
}
