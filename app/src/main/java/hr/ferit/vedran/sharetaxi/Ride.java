package hr.ferit.vedran.sharetaxi;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private String ownerName;
    private ArrayList<String> passengerList = new ArrayList<String>();

    public Ride(){

    }

    public Ride(String id, String from, String to, String passengers, String date, String time, String ownerId, String ownerName, ArrayList<String> passengerList) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.date = date;
        this.time = time;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
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

    public String getOwnerName() { return ownerName; }

    public ArrayList<String> getPassengerList() { return passengerList; }

    public void addPassenger(String userID){
        if(!passengerList.contains(userID)) {
            this.passengerList.add(userID);
            this.passengers = String.valueOf(Integer.parseInt(this.passengers) + 1);
        }
    }

    public void removePassenger(String userID){
        if(passengerList.contains(userID)) {
            this.passengerList.remove(userID);
            this.passengers = String.valueOf(Integer.parseInt(this.passengers) - 1);
        }
    }
}
