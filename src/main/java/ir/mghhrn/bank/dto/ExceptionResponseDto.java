package ir.mghhrn.bank.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ExceptionResponseDto {

    private LocalDateTime happenedAt = LocalDateTime.now();
    private String message;
    private String cause;
    private String trackingCode;
}
