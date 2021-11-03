package ir.mghhrn.bank.dto;

import ir.mghhrn.bank.enums.AuthenticationMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardVerificationResponseDto {
    private String authenticationMode;

    public CardVerificationResponseDto(AuthenticationMode authenticationMode) {
        this.authenticationMode = authenticationMode.name();
    }
}
