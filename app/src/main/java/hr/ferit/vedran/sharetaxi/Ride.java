package hr.ferit.vedran.sharetaxi;

import java.sql.Time;
import java.util.Date;

/**
 * Created by vedra on 3.6.2018..
 */

public class Ride {
    private String id;
    private String from;
    private String to;
    private String passengers;
    private String date;
    private String time;

    public Ride(){

    }

    public Ride(String id, String from, String to, String passengers, String date, String time) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.date = date;
        this.time = time;
    }

    public String getId(){
        return id;
    }
    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getPassengers() { return passengers; }

    public String getDate() { return date; }

    public String getTime() { return time; }

}
