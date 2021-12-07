package cs601.project4.utilities.gson;

import java.io.Serializable;

/**
 * A class to maintain info about each client.
 */
public class ClientInfo implements Serializable {

    private String name;
    private String email;
    private String uniqueId;
    private String zipcode;

    public ClientInfo() {
        this("", "", "", "");
    }

    /**
     * Constructor
     * @param name
     */
    public ClientInfo(String name, String email, String userId, String teamId) {
        this.name = name;
        this.email = email;
        this.uniqueId = userId + teamId;
        this.zipcode = "";
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

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
