package hse.sachkov.learningtrackbackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@ControllerAdvice
public class ApiResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> generateResponse(Exception exception, HttpServletRequest request, HttpStatus status) {
        log.warn(status.value() + " " + status.getReasonPhrase() + ": " + exception.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(status, exception.getMessage(), request.getServletPath()),
                new HttpHeaders(),
                status
        );
    }

    @ExceptionHandler(value = ApiException.class)
    protected ResponseEntity<Object> handleApiException(ApiException exception, HttpServletRequest request) {
        return generateResponse(exception, request, exception.getStatus());
    }

    @ExceptionHandler(value = AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception, HttpServletRequest request) {
        return generateResponse(exception, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        return generateResponse(exception, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleException(Exception exception, HttpServletRequest request) {
        return generateResponse(exception, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
