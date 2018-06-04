package hr.ferit.vedran.sharetaxi;

/**
 * Created by vedra on 3.6.2018..
 */

public class Ride {
    private String from;
    private String to;

    public Ride(){

    }

    public Ride(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

}
