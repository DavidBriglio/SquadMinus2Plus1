package Users;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * Created by connor on 2/23/17.
 */

@Entity
@Component
public class User {

    /**
     * The unique ID for the User
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private long id;

    /**
     * The unique name used for logging in and displaying to other Users
     */
    @Column(unique = true)
    @Getter @Setter
    private String userName;

    /**
     * The User's first name
     */
    @Getter @Setter
    private String firstName;

    /**
     * The User's last name
     */
    @Getter @Setter
    private String lastName;

    /**
     * The unique email address used for logging in and contacting the User outside of the application
     */
    @Column(unique = true)
    @Getter @Setter
    private String email;

    /**
     * The password used for logging into the User's account
     */
    @Getter @Setter
    private String password;

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Constructor that takes in all required attributes
     * @param userName - the user name of the User
     * @param firstName - the first name of the User
     * @param lastName - the last name of the User
     * @param email - the email address of the User
     * @param password - the password for the User
     */
    public User(String userName, String firstName, String lastName, String email, String password) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /**
     * Constructor used for creating a session user
     * @param id - the unique ID of the User
     * @param userName - the user name of the User
     * @param firstName - the first name of the User
     * @param lastName - the last name of the User
     * @param email - the email address of the User
     */
    public User(Long id, String userName, String firstName, String lastName, String email) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Get the string representation of the User
     * @return the User as a string
     */
    @Override
    public String toString() {
        return "ID: " + this.id +
                "\nUser Name: " + this.userName +
                "\nFirst Name: " + this.firstName +
                "\nLast Name: " + this.lastName +
                "\nEmail: " + this.email +
                "\nPassword: " + this.password;
    }

    /**
     * Get a version of the User that does not have the password, to be used for a User's session
     * @return the User without a password
     */
    public User asSessionUser() {
        return new User(this.id,
                this.userName,
                this.firstName,
                this.lastName,
                this.email);
    }

}
