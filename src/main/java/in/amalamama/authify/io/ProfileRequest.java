//this the request object
package in.amalamama.authify.io;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor //lezim il instance min hedhi tkoune fiha les param il kol
public class ProfileRequest {
    //here we just add the rules then we validate them in the controller

    @NotBlank(message= "Name should be not empty")
    private String name;

    @Email(message= "Enter valid email address")
    @NotNull(message=" Email should be not empty")
    private String email;

    @Size(min=6,message="password must be at least 6 characters")
    private String password;
}
