package com.partymaker.cinaitaren;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.partymaker.cinaitaren.camera.CameraSourcePreview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int RC_HANDLE_GMS = 9001;
    private static final int INITIAL_REQUEST=1337;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA
    };
    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;

    private List<Pair<String,Map<String,Object>>> organizations = new ArrayList<>();
    private SensorManager mSensorManager;

    private Float currentDegree = 0f;
    private OrganizationsView viewOV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        viewOV = (OrganizationsView) findViewById(R.id.viewOV);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
            initLocation();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(INITIAL_PERMS, LOCATION_REQUEST);
            }
        }

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    private void initLocation(){
        GPS._getLocation(this, new YandexApi.OnYandexListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if(jsonObject == null){
                    Toast.makeText(getApplicationContext(),"Либо проблемы с соединением, либо закончились бесплатные запросы на яндекс API.", Toast.LENGTH_LONG).show();
                }
                organizations = new ArrayList<>();
                try {
                    JSONArray features = jsonObject.getJSONArray("features");
                    for(int i = 0; i< features.length(); i++){
                        if(features.getJSONObject(i).has("properties") && features.getJSONObject(i).getJSONObject("properties").has("CompanyMetaData") && features.getJSONObject(i).getJSONObject("properties").getJSONObject("CompanyMetaData").has("Categories") && features.getJSONObject(i).getJSONObject("properties").getJSONObject("CompanyMetaData").getJSONArray("Categories").length() != 0 && features.getJSONObject(i).getJSONObject("properties").getJSONObject("CompanyMetaData").getJSONArray("Categories").optJSONObject(0).has("class")){

                            String orgType = features.getJSONObject(i).getJSONObject("properties").getJSONObject("CompanyMetaData").getJSONArray("Categories").optJSONObject(0).getString("class");
                            String name = features.getJSONObject(i).getJSONObject("properties").getJSONObject("CompanyMetaData").getJSONArray("Categories").optJSONObject(0).getString("name");
                            String companyName = features.getJSONObject(i).getJSONObject("properties").getString("name");

                            JSONArray geometry = features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");

                            Map<String,Object> values = new HashMap<>();
                            values.put("angle",getAngle(geometry.getDouble(1),geometry.getDouble(0),Double.parseDouble(AppPreferences.getMyLat(getApplicationContext())),Double.parseDouble(AppPreferences.getMyLong(getApplicationContext()))));
                            values.put("distance",getDistance(geometry.getDouble(1),geometry.getDouble(0),Double.parseDouble(AppPreferences.getMyLat(getApplicationContext())),Double.parseDouble(AppPreferences.getMyLong(getApplicationContext()))));
                            values.put("name",name);



                            values.put("companyName",companyName);

                            Pair<String,Map<String,Object>> org = new Pair<>(orgType, values);

                            organizations.add(org);
                            viewOV.setOrganizations(organizations);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    public double getAngle(double startLat, double startLng, double endLat, double endLng) throws JSONException {
        double longitude1 = startLng;
        double longitude2 = endLng;
        double latitude1 = Math.toRadians(startLat);
        double latitude2 = Math.toRadians(endLat);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);
        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }

    private static final int earthRadius = 6371;
    public static double getDistance(double lat1, double lon1, double lat2, double lon2)
    {
        double dLat = (double) Math.toRadians(lat2 - lat1);
        double dLon = (double) Math.toRadians(lon2 - lon1);
        double a =
                (double) (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2));
        double c = (double) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        double d = earthRadius * c;

        return Math.round(d * 1000);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);

        currentDegree = degree - 180;
        if(currentDegree < 0){
            currentDegree = 360 + currentDegree;
        }
//        L.d("currentDegree = " + currentDegree);
        viewOV.setCurrentDegrees(currentDegree.doubleValue());
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        detector.setProcessor( new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
        .build());
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
        mSensorManager.unregisterListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    private void startCameraSource() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker();
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {

        GraphicFaceTracker() {
        }
        @Override
        public void onNewItem(int faceId, Face item) {

        }

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {

        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {

        }

        @Override
        public void onDone() {

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        L.d("requestCode = " + requestCode + " permissions = " + permissions + " grantResults = " + grantResults);
        switch(requestCode) {
            case LOCATION_REQUEST:
                AppPreferences.setAgreePermission(getApplicationContext());
                createCameraSource();
                initLocation();
                break;
        }
    }
}