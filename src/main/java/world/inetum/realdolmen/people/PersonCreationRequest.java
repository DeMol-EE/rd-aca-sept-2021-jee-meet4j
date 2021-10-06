package world.inetum.realdolmen.people;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PersonCreationRequest {

    @NotBlank
    public String firstName;
    @NotBlank
    public String lastName;
    @Email
    @NotNull
    public String email;
    @NotBlank
    public String password;

}
