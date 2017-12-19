package t27.surreyfooddeliverycompany;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import objectstodb.Account;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private boolean isInitmarker;
    private HashMap<String,Marker> uidTOmaker;
    private  DatabaseReference databaseReference;
    private Query locationRef;
    private String TAG = "MapsActivity";
    private ChildEventListener childListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uidTOmaker = new HashMap<>();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
         LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // Add a marker in Sydney and move the camera
                locationRef = databaseReference.child("driver");


                isInitmarker = false;
                childListener =  locationRef.addChildEventListener(new ChildEventListener() {
                    
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Account one = dataSnapshot.getValue(Account.class);
                        String oldUid = one.getAccountUID();
                        if(!uidTOmaker.containsKey(oldUid)) {
                            return;
                        }
                        Double newLat = one.getLat();
                        Double newLng = one.getLng();
                        Marker marker = uidTOmaker.get(oldUid);
                        if(newLat.compareTo(marker.getPosition().latitude) !=0 ||
                                newLng.compareTo(marker.getPosition().longitude) !=0){
                            marker.setPosition(new LatLng(newLat,newLng));

                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled:  addChildEventListener " + databaseError.getMessage());
                    }
                });
                locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<HashMap<String,Account>> type_drivers_list =
                                new GenericTypeIndicator<HashMap<String,Account>>() {};
                        HashMap<String,Account> driversmap = dataSnapshot.getValue(type_drivers_list);
                        for(Account one:driversmap.values()) {
                            LatLng pos = new LatLng(one.getLat(),one.getLng());
                            if(one.getLat()!=null&&one.getLng()!=null) {
                                MarkerOptions mo = new MarkerOptions().position(pos)
                                        .title(one.getName());
                                Marker oneMarker = mMap.addMarker(mo);
                                uidTOmaker.put(one.getAccountUID(),oneMarker);

                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
                        }
                        isInitmarker = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                    }
                });
            }
        });

        t.start();
    }

    @Override
    protected void onDestroy() {
        locationRef.removeEventListener(childListener);
        Log.d(TAG, "onDestroy: remove");
        super.onDestroy();
    }
}
