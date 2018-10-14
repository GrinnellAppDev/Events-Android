package edu.grinnell.appdev.events.Fragments;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.grinnell.appdev.events.R;

import static edu.grinnell.appdev.events.Misc.Constants.ARH;
import static edu.grinnell.appdev.events.Misc.Constants.CARNEGIE;
import static edu.grinnell.appdev.events.Misc.Constants.CLARK;
import static edu.grinnell.appdev.events.Misc.Constants.CLEVE;
import static edu.grinnell.appdev.events.Misc.Constants.COWLES;
import static edu.grinnell.appdev.events.Misc.Constants.DIBBLE;
import static edu.grinnell.appdev.events.Misc.Constants.GATES;
import static edu.grinnell.appdev.events.Misc.Constants.GOODNOW;
import static edu.grinnell.appdev.events.Misc.Constants.GRINNELL;
import static edu.grinnell.appdev.events.Misc.Constants.HAINES;
import static edu.grinnell.appdev.events.Misc.Constants.HARRIS;
import static edu.grinnell.appdev.events.Misc.Constants.JAMES;
import static edu.grinnell.appdev.events.Misc.Constants.KERSHAW;
import static edu.grinnell.appdev.events.Misc.Constants.LANGAN;
import static edu.grinnell.appdev.events.Misc.Constants.LAZIER;
import static edu.grinnell.appdev.events.Misc.Constants.LOOSE;
import static edu.grinnell.appdev.events.Misc.Constants.MAIN;
import static edu.grinnell.appdev.events.Misc.Constants.MEARS;
import static edu.grinnell.appdev.events.Misc.Constants.NORRIS;
import static edu.grinnell.appdev.events.Misc.Constants.PERMISSIONS_REQUEST;
import static edu.grinnell.appdev.events.Misc.Constants.RATHJE;
import static edu.grinnell.appdev.events.Misc.Constants.RAWSON;
import static edu.grinnell.appdev.events.Misc.Constants.READ;
import static edu.grinnell.appdev.events.Misc.Constants.ROSE;
import static edu.grinnell.appdev.events.Misc.Constants.SMITH;
import static edu.grinnell.appdev.events.Misc.Constants.SPANISH_HOUSE;
import static edu.grinnell.appdev.events.Misc.Constants.STEINER;
import static edu.grinnell.appdev.events.Misc.Constants.YOUNKER;
import static edu.grinnell.appdev.events.Misc.Constants.ZOOM_LEVEL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMap extends Fragment {
    MapView mapView;
    GoogleMap gmap;
    boolean mLocationPermissionGranted;
    CameraPosition mCameraPosition;
    private static final String KEY_CAMERA_POSITION = "camera_position";


    public FragmentMap() {
        // Required empty public constructor
    }

    //TODO: Restore the state of the map after switching fragments. Add more Locations.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        else {

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    gmap = mMap;

                    // For dropping a marker at a point on the Map
                    gmap.addMarker(new MarkerOptions().position(GRINNELL).title("Grinnell College")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    gmap.addMarker(new MarkerOptions().position(HARRIS).title("Harris Cinema"));
                    gmap.addMarker(new MarkerOptions().position(COWLES).title("Cowles Hall"));
                    gmap.addMarker(new MarkerOptions().position(NORRIS).title("Norris Hall"));
                    gmap.addMarker(new MarkerOptions().position(DIBBLE).title("Dibble Hall"));
                    gmap.addMarker(new MarkerOptions().position(CLARK).title("Clark Hall"));
                    gmap.addMarker(new MarkerOptions().position(GATES).title("Gates Hall"));
                    gmap.addMarker(new MarkerOptions().position(RAWSON).title("Rawson Hall"));
                    gmap.addMarker(new MarkerOptions().position(LANGAN).title("Langan Hall"));
                    gmap.addMarker(new MarkerOptions().position(SMITH).title("Smith Hall"));
                    gmap.addMarker(new MarkerOptions().position(SPANISH_HOUSE).title("Spanish House"));
                    gmap.addMarker(new MarkerOptions().position(YOUNKER).title("Younker Hall"));
                    gmap.addMarker(new MarkerOptions().position(ARH).title("Alumni Recitation Hall (ARH)"));
                    gmap.addMarker(new MarkerOptions().position(CARNEGIE).title("Carnegie Hall"));
                    gmap.addMarker(new MarkerOptions().position(STEINER).title("Steiner Hall"));
                    gmap.addMarker(new MarkerOptions().position(GOODNOW).title("Goodnow Hall"));
                    gmap.addMarker(new MarkerOptions().position(MEARS).title("Mears Cottage"));
                    gmap.addMarker(new MarkerOptions().position(MAIN).title("Main Hall"));
                    gmap.addMarker(new MarkerOptions().position(CLEVE).title("Cleve Hall"));
                    gmap.addMarker(new MarkerOptions().position(JAMES).title("James Hall"));
                    gmap.addMarker(new MarkerOptions().position(HAINES).title("Haines Hall"));
                    gmap.addMarker(new MarkerOptions().position(READ).title("Read Hall"));
                    gmap.addMarker(new MarkerOptions().position(LOOSE).title("Loose Hall"));
                    gmap.addMarker(new MarkerOptions().position(LAZIER).title("Lazier Hall"));
                    gmap.addMarker(new MarkerOptions().position(KERSHAW).title("Kershaw Hall"));
                    gmap.addMarker(new MarkerOptions().position(ROSE).title("Rose Hall"));
                    gmap.addMarker(new MarkerOptions().position(RATHJE).title("Rathje Hall"));

                    LatLngBounds Bounds = new LatLngBounds(
                            new LatLng(41.5, -92.9), new LatLng(41.9, -92.5));
                    gmap.setLatLngBoundsForCameraTarget(Bounds);

                    // For zooming automatically to the location of the marker
                    mCameraPosition = new CameraPosition.Builder().target(GRINNELL).zoom(ZOOM_LEVEL).build();
                    gmap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
                }
            });
        }
        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        //updateLocationUI();
    }

}


