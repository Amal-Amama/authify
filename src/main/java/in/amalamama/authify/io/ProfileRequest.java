//this the request object
package in.amalamama.authify.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor //lezim il instance min hedhi tkoune fiha les oaram il kol
public class ProfileRequest {
    private String name;
    private String password;
    private String email;
}
