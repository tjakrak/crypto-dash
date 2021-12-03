package cs601.project4.utilities;

import java.io.Serializable;

/**
 * A class to maintain info about each client.
 */
public class ClientInfo implements Serializable {

    private String name;

    /**
     * Constructor
     * @param name
     */
    public ClientInfo(String name) {
        this.name = name;
    }

    /**
     * return name
     * @return
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
