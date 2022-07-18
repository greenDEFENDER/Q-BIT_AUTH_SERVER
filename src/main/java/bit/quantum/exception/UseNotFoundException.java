package bit.quantum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such User")  // 404
public class UseNotFoundException extends RuntimeException {
    public UseNotFoundException(String message) {
        super(message);
    }
}
