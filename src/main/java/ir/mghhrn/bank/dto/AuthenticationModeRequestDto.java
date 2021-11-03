package ir.mghhrn.bank.dto;

import ir.mghhrn.bank.enums.AuthenticationMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticationModeRequestDto {
    @NotNull
    private AuthenticationMode authenticationMode;
}
