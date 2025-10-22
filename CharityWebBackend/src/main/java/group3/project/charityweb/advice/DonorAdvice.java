package group3.project.charityweb.advice;

import group3.project.charityweb.dto.MessageDto;
import group3.project.charityweb.exception.DonorNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.FailedLoginException;

@RestControllerAdvice
public class DonorAdvice {

    @ExceptionHandler(DonorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public MessageDto processDonorNotFoundException(DonorNotFoundException e) {
        MessageDto dto = new MessageDto();
        dto.setMessage(e.getMessage());
        dto.setType("NOT_FOUND");
        return dto;
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public MessageDto processIllegalStateException(IllegalStateException e) {
        MessageDto dto = new MessageDto();
        dto.setMessage(e.getMessage());
        dto.setType("ILLEGAL_STATE");
        return dto;
    }

    @ExceptionHandler(FailedLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public MessageDto processFailedLoginException(FailedLoginException e) {
        MessageDto dto = new MessageDto();
        dto.setMessage(e.getMessage());
        dto.setType("FAILED_LOGIN");
        return dto;
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public MessageDto processSecurityException(SecurityException e) {
        MessageDto dto = new MessageDto();
        dto.setMessage(e.getMessage());
        dto.setType("SECURITY");
        return dto;
    }
}
