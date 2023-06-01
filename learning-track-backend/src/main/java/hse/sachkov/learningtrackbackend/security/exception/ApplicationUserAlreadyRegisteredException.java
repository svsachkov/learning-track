package hse.sachkov.learningtrackbackend.security.exception;

import hse.sachkov.learningtrackbackend.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ApplicationUserAlreadyRegisteredException extends ApiException {

    public ApplicationUserAlreadyRegisteredException() {
        super("This username is already taken!", HttpStatus.CONFLICT);
    }
}
