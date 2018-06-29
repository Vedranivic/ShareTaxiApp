package hr.ferit.vedran.sharetaxi;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.Locale;

public class MyLocationIntentService extends IntentService {

    private static final String IDENTIFIER = "MyLocationIntentService";
    private ResultReceiver addressResultReceiver;

    public MyLocationIntentService() {
        super(IDENTIFIER);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String msg;
        if(intent != null) {
            addressResultReceiver = intent.getParcelableExtra("add_receiver");
        }

        if (addressResultReceiver == null) {
            Log.e("GetAddressIntentService", "No receiver, not processing the request further");
            return;
        }

        Location location = intent.getParcelableExtra("add_location");

        if (location == null) {
            msg = "No location, can't go further without location";
            sendResultsToReceiver(0, msg);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (Exception ioException) {
            Log.e("", "Error in getting address for the location");
        }

        if (addresses == null || addresses.size()  == 0) {
            msg = "No address found for the location";
            sendResultsToReceiver(1, msg);
        } else {
            Address address = addresses.get(0);

            //String adressResult = "Ul. Ivana Gorana Kovacica 4";
            String adressResult = address.getAddressLine(0).split(",")[0];

            sendResultsToReceiver(2,adressResult);
        }
    }

    private void sendResultsToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString("address_result", message);
        addressResultReceiver.send(resultCode, bundle);
    }

}
