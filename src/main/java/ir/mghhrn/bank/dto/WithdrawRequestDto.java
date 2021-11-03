package ir.mghhrn.bank.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
public class WithdrawRequestDto {

    @NotNull
    @Positive
    private Double amount;
}
