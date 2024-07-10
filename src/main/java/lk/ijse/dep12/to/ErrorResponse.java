package lk.ijse.dep12.to;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Set;

@Data
@NotBlank
@AllArgsConstructor
public class ErrorResponse {
    private int code;
    private String status;
    private String message;
    private HashMap<String, String> errors = new HashMap<>();

    public ErrorResponse(int code, String status, String message, Set<ConstraintViolation<User>> violationSet) {
        this.code = code;
        this.status = status;
        this.message = message;
        violationSet.forEach(violation -> errors.put(violation.getPropertyPath().toString(),
                violation.getMessage().toString()));
    }
}
