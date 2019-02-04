package myoo.votingapp.viewmodel;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

public class CurrentLocationListener extends LiveData<Location> {
    // singleton and build google api are ommited
    static CurrentLocationListener instance = null;

    GoogleApiClient googleApiClient;
    String TAG = "CurrentLocationListener";

    private FusedLocationProviderClient mFusedLocationClient= null ;

    static Context context;
    public static CurrentLocationListener getInstance(Context appContext) {
        if (instance == null) {
            instance = new CurrentLocationListener(appContext);
        }
        context = appContext ;
        return instance;
    }
    private CurrentLocationListener(Context appContext) {
        Log.d("location ", "in the private constructor" );
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext);
    }

    @Override
    protected void onActive() {
       // googleApiClient.connect();
        Log.d("location ", "in onactive" );
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = mFusedLocationClient ;

        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            setValue(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }



}