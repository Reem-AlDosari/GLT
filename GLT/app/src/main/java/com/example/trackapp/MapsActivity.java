package com.example.trackapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST = 100;

    private ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
    private ArrayList<String> mMarkerIDArray = new ArrayList<String>();

    private String myid=new String("");
    private LatLng myloc=new LatLng(24.7136,46.6753);
    private String mygroup=new String("");

    private CheckBox EnableTrackingChk;
    public static boolean my_track_enable=false;

    private ImageButton GroupSettingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        EnableTrackingChk = (CheckBox) findViewById(R.id.EnableTracking);
        EnableTrackingChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    my_track_enable=true;
                    Toast.makeText(buttonView.getContext(), "Tracking Started", Toast.LENGTH_SHORT).show();
                }
                else {
                    my_track_enable=false;
                    Toast.makeText(buttonView.getContext(), "Tracking Stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });


        GroupSettingsBtn = (ImageButton) findViewById(R.id.map_groupsettings);
        GroupSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( v.getContext() ,GroupActivity.class));
            }
        });


        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE); //check GPS enabled
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }

        int permission = ContextCompat.checkSelfPermission(this, //check we have permisiion to access location
                Manifest.permission.ACCESS_FINE_LOCATION);

        myid=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        if (permission == PackageManager.PERMISSION_GRANTED) {
            startService(new Intent(this, LocationUpdate.class)); //start service

            FirebaseDatabase.getInstance().getReference("Users")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Double lat= ((Number)snapshot.child("location").child("latitude").getValue()).doubleValue();
                            Double lng= ((Number)snapshot.child("location").child("longitude").getValue()).doubleValue();
                            Boolean user_track_enable= ((Boolean)snapshot.child("track_enable").getValue());
                            String group= (String)snapshot.child("group").getValue().toString();

                            String name= (String) snapshot.child("name").getValue();
                            String id= snapshot.getKey().toString();

                            LatLng loc= new LatLng(lat, lng);

                            boolean markerexist=false;

                            for (int i=0;i < mMarkerIDArray.size();i++) {
                                if(mMarkerIDArray.toArray()[i].equals(id)){
                                    Marker marker= (Marker) mMarkerArray.get(i);
                                    marker.setPosition(loc);
                                    marker.setVisible(user_track_enable  && mygroup.equals(group));
                                    markerexist=true;
                                }
                            }

                            if(markerexist==false){
                                Marker marker=mMap.addMarker(new MarkerOptions().position(loc).title(name));
                                mMarkerArray.add(marker);
                                mMarkerIDArray.add(id);
                            }

                            if(id.equals(myid) && my_track_enable==true){
                                myloc=loc;
                                mygroup=group;
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                            }

                            if(my_track_enable==true && user_track_enable==true){
                                float [] dist = new float[1];
                                Location.distanceBetween(loc.latitude,loc.longitude,myloc.latitude,myloc.longitude,dist);


                                if(dist[0]>500) {


                                    NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    String CHANNEL_ID="";
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        CHANNEL_ID = "my_channel_01";
                                        CharSequence name1 = "my_channel";
                                        String Description = "This is my channel";
                                        int importance = NotificationManager.IMPORTANCE_HIGH;
                                        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name1, importance);
                                        mChannel.setDescription(Description);
                                        mChannel.enableLights(true);
                                        mChannel.setLightColor(Color.RED);
                                        mChannel.enableVibration(true);
                                        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                                        mChannel.setShowBadge(false);
                                        notificationManager.createNotificationChannel(mChannel);
                                    }
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MapsActivity.this,CHANNEL_ID)
                                            .setSmallIcon(R.drawable.ic_action_alarms)
                                            .setContentTitle("Attention")
                                            .setContentText("500m");



                                    Intent resultIntent = new Intent(MapsActivity.this, MainActivity.class);
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(MapsActivity.this);
                                    stackBuilder.addParentStack(MainActivity.class);
                                    stackBuilder.addNextIntent(resultIntent);
                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    builder.setContentIntent(resultPendingIntent);




                                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    manager.notify(1,builder.build());

                                }
                            }

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        } else {
            ActivityCompat.requestPermissions(this,   // if we have no permission to access location, ask the user to allow
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startService(new Intent(this, LocationUpdate.class)); //start service
        } else {

            Toast.makeText(this, "Please activate GPS", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng dummylocation = new LatLng(151, -34);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dummylocation));
    }

}