package com.yellowbus.project.place.search.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.dao.DataIntegrityViolationException;

@Setter @Getter
public class SignupException extends DataIntegrityViolationException {

    String message;

    public SignupException(String msg) {
        super(msg);
    }

}
