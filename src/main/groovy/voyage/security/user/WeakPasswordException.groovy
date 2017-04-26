package voyage.security.user

import org.springframework.http.HttpStatus
import voyage.common.error.AppException
import voyage.common.error.ErrorUtils

class WeakPasswordException extends AppException {
    private static final HTTP_STATUS  = HttpStatus.BAD_REQUEST
    private static final String DEFAULT_MESSAGE = 'The password did not meet the requirements.Password should contain 1 Upper case Character, ' +
            '1 Lower Case Character, 1 Special Character and should not contain any whitespace.'

    WeakPasswordException() {
        super(HTTP_STATUS, DEFAULT_MESSAGE)
    }

    WeakPasswordException(String message) {
        super(HTTP_STATUS, message)
    }

    @Override
    String getErrorCode() {
        ErrorUtils.getErrorCode(HTTP_STATUS.value(), 'week_password')
    }
}
