package com.partymaker.cinaitaren;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;


import org.json.JSONObject;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;


/**
 * Created by vladimir on 29.10.16.
 */

public class GPS {


    public static void _getLocation(final Context context, final YandexApi.OnYandexListener onYandexListener) {

        String stateProvider = checkGpsEnable(context);
//        if (stateProvider.equals("-1")) {
//            return;
//        }
//        if (stateProvider.equals(LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(context, "Рекомендуется использовать WI-FI и мобильные сети для определения местоположения.", Toast.LENGTH_LONG).show();
//        }

//        //final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        LocationManager locationManager= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//        String provider = locationManager.getBestProvider(criteria, true);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }


        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000);

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Subscription updatedLocationSubscription = locationProvider
                .getUpdatedLocation(request)
                .subscribe(new Action1<Location>() {
                    @Override
                    public void call(Location location) {
                        AppPreferences.setMyLat(context, location.getLatitude() + "");
                        AppPreferences.setMyLong(context, location.getLongitude() + "");

                        YandexApi.getOrganizations(context, onYandexListener);
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        L.d("_getLocation onLocationChanged = " + location);
                    }
                });

//        locationManager.requestLocationUpdates(provider, 1,
//                1, new LocationListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//                        AppPreferences.setMyLat(context, location.getLatitude() + "");
//                        AppPreferences.setMyLong(context, location.getLongitude() + "");
//
////                        YandexApi.getOrganizations(context, onYandexListener);
////                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                            return;
////                        }
//                        System.out.println("_getLocation onLocationChanged = " + location);
//                        L.d("_getLocation onLocationChanged = " + location);
//                    }
//
//                    @Override
//                    public void onStatusChanged(String provider, int status, Bundle extras) {
//                        L.d("_getLocation onStatusChanged = " + provider);
//                    }
//
//                    @Override
//                    public void onProviderEnabled(String provider) {
//                        L.d("_getLocation onProviderEnabled = " + provider);
//                    }
//
//                    @Override
//                    public void onProviderDisabled(String provider) {
//                        L.d("_getLocation onProviderDisabled = " + provider);
//                    }
//                });
    }

    private static String checkGpsEnable(final Context context){
        if(((Activity) context).isFinishing()) {
            return "-1";
        }
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        String providerState = "-1";

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(gps_enabled){
                providerState = LocationManager.GPS_PROVIDER;
            }

        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(network_enabled){
                providerState = LocationManager.NETWORK_PROVIDER;
            }
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Определение местоположения отключено");
            dialog.setMessage("Приложение не может определить где вы находитесь. Включите функцию \\\"Мое местоположение\\\". Рекомендуется использовать WI-FI и мобильные сети для определения местоположения.");
            dialog.setPositiveButton(("Настройки"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(("Закрыть"), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
            return providerState;
        }
        return providerState;
    }



    private static Location getLastBestLocation(Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

}
