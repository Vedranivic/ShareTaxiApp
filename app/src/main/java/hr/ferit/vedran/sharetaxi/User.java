package hr.ferit.vedran.sharetaxi;

/**
 * Created by vedra on 27.6.2018..
 */

public class User {
    private String UID;
    private String email;
    private String name;
    private String provider;

    public User(String UID, String email, String name, String provider) {
        this.UID = UID;
        this.email = email;
        this.name = name;
        this.provider = provider;
    }

    public User(){

    }

    public String getUID() {
        return UID;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProvider() {
        return provider;
    }
}
