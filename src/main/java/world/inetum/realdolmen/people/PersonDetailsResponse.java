package world.inetum.realdolmen.people;

public class PersonDetailsResponse {

    private final long id;
    private final String firstName;
    private final String lastName;
    private final String email;

    public PersonDetailsResponse(
            long id,
            String firstName,
            String lastName,
            String email
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}
