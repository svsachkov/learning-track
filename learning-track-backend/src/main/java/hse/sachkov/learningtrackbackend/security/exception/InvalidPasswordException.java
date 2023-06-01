package hse.sachkov.learningtrackbackend.security.exception;

import hse.sachkov.learningtrackbackend.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends ApiException {

    public InvalidPasswordException() {
        super("The password is invalid!", HttpStatus.CONFLICT);
    }
}
