package com.example.gmap;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mDatabase = FirebaseDatabase.getInstance().getReference("locations");
        mDatabase1 = FirebaseDatabase.getInstance().getReference("current_location");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize the handler
        mHandler = new Handler();
        // Start location updates
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Update the location
                updateLocation();
                // Schedule the next update after 30 seconds
                mHandler.postDelayed(this, 30000); // 30 seconds in milliseconds
            }
        }, 0); // Start immediately
    }

    private void updateLocation() {
        // Fetch the current location from the database and update the map
        mDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    LatLng currentLocation = new LatLng(latitude, longitude);

                    // Update the current location marker
                    mMap.clear(); // Clear existing markers
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));



                    // Add markers for warehouses
                    mDatabase.child("warehouses").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot warehouseSnapshot : dataSnapshot.getChildren()) {
                                // Retrieve latitude and longitude for each warehouse
                                double warehouseLatitude = warehouseSnapshot.child("latitude").getValue(Double.class);
                                double warehouseLongitude = warehouseSnapshot.child("longitude").getValue(Double.class);
                                LatLng warehouseLocation = new LatLng(warehouseLatitude, warehouseLongitude);
                                mMap.addMarker(new MarkerOptions().position(warehouseLocation).title("Warehouse1"));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error
                            Log.e("MapsActivity", "Failed to retrieve warehouses: " + databaseError.getMessage());
                            Toast.makeText(MapsActivity.this, "Failed to retrieve warehouses", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Show a success toast message
                    Toast.makeText(MapsActivity.this, "Location updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("MapsActivity", "Current location data does not exist");
                    // Show a failure toast message
                    Toast.makeText(MapsActivity.this, "Failed to update location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MapsActivity", "Error retrieving location: " + databaseError.getMessage());
                // Show a failure toast message
                Toast.makeText(MapsActivity.this, "Failed to update location", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       mDatabase1.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               mMap.clear();
               if (dataSnapshot.exists()) {
                   double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                   double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                   LatLng currentLocation = new LatLng(latitude, longitude);

                   mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
               } else {
                   Log.d("MapsActivity", "Current location data does not exist");
               }
           }


           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

        // Add markers for vehicles
        mDatabase.child("vehicles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    double latitude = snapshot.child("latitude").getValue(Double.class);
                    double longitude = snapshot.child("longitude").getValue(Double.class);
                    LatLng vehicleLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(vehicleLocation).title("Vehicle"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

        // Add markers for warehouses
        mDatabase.child("warehouses2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot warehouseSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve latitude and longitude for each warehouse
                    double latitude = warehouseSnapshot.child("latitude").getValue(Double.class);
                    double longitude = warehouseSnapshot.child("longitude").getValue(Double.class);
                    LatLng warehouseLocation = new LatLng(latitude, longitude);



                    // Add marker for each warehouse to the map
                    mMap.addMarker(new MarkerOptions().position(warehouseLocation).title("Warehouse2"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.e("MapsActivity", "Failed to retrieve warehouses: " + databaseError.getMessage());
                Toast.makeText(MapsActivity.this, "Failed to retrieve warehouses", Toast.LENGTH_SHORT).show();
            }
        });

        // Zoom to a specific location with a desired zoom level
        LatLng center = new LatLng(23.7956, 90.3537); // Example location
        float zoomLevel = 15.0f; // Desired zoom level
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to prevent memory leaks
        mHandler.removeCallbacksAndMessages(null);
    }
}

