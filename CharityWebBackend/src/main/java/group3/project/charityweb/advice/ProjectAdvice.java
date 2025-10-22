package group3.project.charityweb.advice;

import group3.project.charityweb.dto.MessageDto;
import group3.project.charityweb.exception.DonorNotFoundException;
import group3.project.charityweb.exception.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectAdvice {
    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public MessageDto processProjectNotFoundException(ProjectNotFoundException e) {
        MessageDto dto = new MessageDto();
        dto.setMessage(e.getMessage());
        dto.setType("NOT_FOUND");
        return dto;
    }
}
