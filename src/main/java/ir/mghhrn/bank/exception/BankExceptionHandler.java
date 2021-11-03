package ir.mghhrn.bank.exception;

import ir.mghhrn.bank.dto.ExceptionResponseDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;

@ControllerAdvice
public class BankExceptionHandler {

    private static final Logger logger = LogManager.getLogger(BankExceptionHandler.class);

    @ExceptionHandler(BankGeneralException.class)
    public ResponseEntity<ExceptionResponseDto> handleBankGeneralException(BankGeneralException exception) {
        String trackingCode = UUID.randomUUID().toString();
        String causeMessage = exception.getCause() != null ? exception.getCause().getMessage() : "no cause";
        logger.error("A BankGeneralException happened with tracking code as {}", trackingCode, exception);
        ExceptionResponseDto dto = new ExceptionResponseDto();
        dto.setTrackingCode(trackingCode);
        dto.setMessage(exception.getMessage());
        dto.setCause(causeMessage);
        return ResponseEntity.badRequest().body(dto);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ExceptionResponseDto> handleBankGeneralException(ObjectOptimisticLockingFailureException exception) {
        String trackingCode = UUID.randomUUID().toString();
        String causeMessage = exception.getCause() != null ? exception.getCause().getMessage() : "no cause";
        logger.error("A ObjectOptimisticLockingFailureException happened with tracking code as {}", trackingCode, exception);
        ExceptionResponseDto dto = new ExceptionResponseDto();
        dto.setTrackingCode(trackingCode);
        dto.setMessage("there was a problem, please try your operation again");
        dto.setCause(causeMessage);
        return ResponseEntity.badRequest().body(dto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleException(Exception exception) {
        String trackingCode = UUID.randomUUID().toString();
        String causeMessage = exception.getCause() != null ? exception.getCause().getMessage() : "no cause";
        logger.error("An Exception happened with tracking code as {}", trackingCode, exception);
        ExceptionResponseDto dto = new ExceptionResponseDto();
        dto.setTrackingCode(trackingCode);
        dto.setMessage(exception.getMessage());
        dto.setCause(causeMessage);
        return ResponseEntity.badRequest().body(dto);
    }
}
