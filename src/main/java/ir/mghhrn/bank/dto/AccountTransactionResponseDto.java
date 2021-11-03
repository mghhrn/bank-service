package ir.mghhrn.bank.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountTransactionResponseDto {
    private String cardNumber;
    private Long accountId;
    private String accountTransactionNumber;
}
