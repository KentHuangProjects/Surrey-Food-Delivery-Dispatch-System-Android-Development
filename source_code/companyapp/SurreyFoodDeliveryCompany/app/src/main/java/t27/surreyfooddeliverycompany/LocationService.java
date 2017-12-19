package t27.surreyfooddeliverycompany;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationService extends Service {
    //60000ms
    private static final long minTime = 2000;

    private static final float minDistance = 2;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private String TAG = "LocationService";
    private String uid;

    //service bind with activity.

    //create
    @Override
    public void onCreate() {
        System.out.println("Create service");
        super.onCreate();
    }

    //start service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Start service");
        uid = intent.getExtras().getString("uid");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GpsLocationListener();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance,
                locationListener);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public class GpsLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

            Log.d(TAG, "onLocationChanged: ");
            // get data and post to the server
            if(null != location) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                ref.child("driver").child(uid).child("lat").setValue(location.getLatitude());
                ref.child("driver").child(uid).child("lng").setValue(location.getLongitude());

                Log.d(TAG, "onLocationChanged:Latitude:" + location.getLatitude());
                Log.d(TAG, "onLocationChanged Longitude:"+ location.getLongitude());
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }
    }
}
