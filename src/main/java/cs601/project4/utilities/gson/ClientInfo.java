package cs601.project4.utilities.gson;

import java.io.Serializable;

/**
 * A class to maintain info about each client.
 */
public class ClientInfo implements Serializable {

    private String name;
    private String email;

    /**
     * Constructor
     * @param name
     */
    public ClientInfo(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * return name
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        return "ClientInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
