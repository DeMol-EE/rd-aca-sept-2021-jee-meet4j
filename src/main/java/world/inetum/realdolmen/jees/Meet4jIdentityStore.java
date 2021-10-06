package world.inetum.realdolmen.jees;

import world.inetum.realdolmen.people.PasswordHasher;
import world.inetum.realdolmen.people.Person;
import world.inetum.realdolmen.people.PersonResource;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Set;

@ApplicationScoped
public class Meet4jIdentityStore implements IdentityStore {

    @EJB
    PersonResource personResource;

    @EJB
    PasswordHasher passwordHasher;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential uc = (UsernamePasswordCredential) credential;
            String pwHash = passwordHasher.hash(uc.getPasswordAsString());
            try {
                Person person = personResource.findByEmailAndHashedPassword(
                        uc.getCaller(),
                        pwHash);
                return new CredentialValidationResult(
                        String.valueOf(person.getId()),
                        Set.of("USER"));
            } catch (Exception e) {
                return CredentialValidationResult.INVALID_RESULT;
            }
        } else {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }
    }
}
