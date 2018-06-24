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
    private String ownerId;
    private String passengerList;

    public Ride(){

    }

    public Ride(String id, String from, String to, String passengers, String date, String time, String ownerId, String passengerList) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.date = date;
        this.time = time;
        this.ownerId = ownerId;
        this.passengerList = passengerList;
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

    public String getOwnerId() { return ownerId; }

    public String getPassengerList() { return passengerList; }

    public void addPassenger(String userID){
        this.passengerList = userID;
    }
}
