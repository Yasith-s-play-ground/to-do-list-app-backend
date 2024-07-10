package lk.ijse.dep12.to;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.servlet.http.Part;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lk.ijse.dep12.validation.Password;
import lk.ijse.dep12.validation.ProfilePicture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    @NotBlank(message = "Name can't be null or empty")
    @Pattern(regexp = "^[A-Za-z ]+$",message = "Name must contain only letters and spaces")
    private String name;
    @NotBlank(message =  "Email can't be null or empty")
    private String email;
    @JsonIgnore
    @NotBlank(message = "User password can't be null or empty")
    @Password
    private String password;
    @JsonIgnore
    @ProfilePicture
    private Part picture;

}
