package ir.mghhrn.bank.service;

import ir.mghhrn.bank.dto.AuthenticationModeRequestDto;
import ir.mghhrn.bank.dto.AuthenticationRequestDto;
import ir.mghhrn.bank.dto.AuthenticationResponseDto;
import ir.mghhrn.bank.dto.AccountTransactionResponseDto;
import ir.mghhrn.bank.enums.AuthenticationMode;

public interface CardService {
    AuthenticationResponseDto authenticateCard(AuthenticationRequestDto requestDto);
    AccountTransactionResponseDto withdraw(Double amount, String authenticationToken);
    AccountTransactionResponseDto deposit(Double requestDto, String authenticationToken);
    String changeAuthenticationMode(AuthenticationMode authenticationMode, String authenticationToken);
}
