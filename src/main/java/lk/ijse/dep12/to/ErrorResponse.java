package lk.ijse.dep12.to;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
@NotBlank
@AllArgsConstructor
public class ErrorResponse { //RFC 9457 standard
    private String type;
    private int status;
    private String title;
    private String detail;
    private List<HashMap<String, String>> errors = new ArrayList<>();

    public ErrorResponse(String type, int status, String title, String detail, Set<ConstraintViolation<User>> violationSet) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        violationSet.forEach(violation -> {
            HashMap<String, String> error = new HashMap<>();
            error.put("detail", violation.getMessage()); // details about the problem
            error.put("pointer", violation.getPropertyPath().toString()); // where did the problem occur
            errors.add(error);
        });
    }
}
