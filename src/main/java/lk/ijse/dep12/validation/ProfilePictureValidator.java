package lk.ijse.dep12.validation;

import jakarta.servlet.http.Part;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProfilePictureValidator implements ConstraintValidator<ProfilePicture, Part> {
    @Override
    public boolean isValid(Part picture, ConstraintValidatorContext context) {
        if (picture != null) {
            /*check content type of Part*/
            return picture.getContentType().startsWith("image/");
        }
        return true; // can keep picture null
    }
}
