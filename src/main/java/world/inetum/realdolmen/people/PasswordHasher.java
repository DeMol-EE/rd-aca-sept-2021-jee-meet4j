package world.inetum.realdolmen.people;

import javax.ejb.Stateless;

@Stateless
public class PasswordHasher {

    public String hash(String password) {
        return String.valueOf(password.hashCode());
    }
}
