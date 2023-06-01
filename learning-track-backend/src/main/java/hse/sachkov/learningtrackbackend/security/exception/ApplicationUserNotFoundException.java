package hse.sachkov.learningtrackbackend.security.exception;

import hse.sachkov.learningtrackbackend.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ApplicationUserNotFoundException extends ApiException {

    public ApplicationUserNotFoundException() {
        super("User with this username not found!", HttpStatus.NOT_FOUND);
    }
}
