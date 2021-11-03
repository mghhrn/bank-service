package ir.mghhrn.bank.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CardVerificationRequestDto {

    @NotNull
    private String cardNumber;

    @NotNull
    private String atmSerialNumber;
}
