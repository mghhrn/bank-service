package ir.mghhrn.bank.dto;

import ir.mghhrn.bank.enums.AuthenticationMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticationRequestDto {
    @NotNull
    private String cardNumber;

    @NotNull
    private String atmSerialNumber;

    @NotNull
    private AuthenticationMode authenticationMode;

    private String pinCode;
    private String fingerprint;
}
