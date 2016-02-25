package com.worth.ifs.commons.error.exception;

import java.util.List;

/**
 * Created by rav on 18/02/2016.
 */
public class LengthRequiredException extends IFSRuntimeException {

    public LengthRequiredException() {
    }

    public LengthRequiredException(List<Object> arguments) {
        super(arguments);
    }

    public LengthRequiredException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public LengthRequiredException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public LengthRequiredException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public LengthRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
