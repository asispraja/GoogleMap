package com.example.aashish.googlemap;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    EditText search;
    Button go, normal, sat, ter;
    LatLng ll;
    GoogleMap map;
    double lat,lng;
    LatLng  newLatLng;
    String url="http://maps.googleapis.com/maps/api/geocode/json?address=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search = findViewById(R.id.txt1);
        go = findViewById(R.id.go);
        sat = findViewById(R.id.satelite);
        normal = findViewById(R.id.normal);
        ter = findViewById(R.id.terrain);
        //set id to fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps);
        //call on map ready
        supportMapFragment.getMapAsync(this);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locate = search.getText().toString();
                url = url+locate;
                url.replace(" ","-");

                RequestQueue requestqueue= Volley.newRequestQueue(MainActivity.this);
                StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject obj1 = new JSONObject(s);
                            JSONArray arr1 = obj1.getJSONArray("results");
                            JSONObject obj2 = arr1.getJSONObject(0);
                            JSONObject obj3 = obj2.getJSONObject("geometry");
                            JSONObject obj4 = obj3.getJSONObject("location");
                            lat = obj4.getDouble("lat");
                            lng = obj4.getDouble("lng");

                            newLatLng= new LatLng(lat,lng);
                            map.clear();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 15));
                            map.addMarker(new MarkerOptions().position(newLatLng));

                        }catch(Exception e)
                        {
                            Toast.makeText(MainActivity.this, "No Address Found", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();


                    }
                });



                requestqueue.add(sr);
            }

        });

        sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            }
        });
        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            }
        });
        ter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map=googleMap;
        LatLng l = new LatLng(27.6853, 85.3743);
        ll = l;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15));
        googleMap.addMarker(new MarkerOptions().position(l));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                ll= latLng;

            }
        });
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                PolylineOptions options = new PolylineOptions();
                options.add(ll);
                options.add(latLng);
                googleMap.addPolyline(options);
Location loc1=new Location("origin");
loc1.setLatitude(ll.latitude);
loc1.setLongitude(ll.longitude);

Location loc2=new Location("destin");
loc2.setLatitude(latLng.latitude);
loc2.setLongitude(latLng.longitude);

double distance=loc1.distanceTo(loc2);
if(distance<=1000)
                Toast.makeText(MainActivity.this, "distance="+distance+"m", Toast.LENGTH_SHORT).show();
else {
    distance=distance/1000;
    Toast.makeText(MainActivity.this, "distance=" + distance + "km", Toast.LENGTH_SHORT).show();
}
            }
        });
    }

}
