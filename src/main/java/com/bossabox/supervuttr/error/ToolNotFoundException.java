package com.bossabox.supervuttr.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ToolNotFoundException extends RuntimeException {
    public ToolNotFoundException() {
    }

    public ToolNotFoundException(String message) {
        super(message);
    }
}
