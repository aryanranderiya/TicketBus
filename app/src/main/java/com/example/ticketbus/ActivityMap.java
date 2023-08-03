package com.example.ticketbus;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

@SuppressWarnings({"deprecation","FieldCanBeLocal","Convert2Lambda"})

public class ActivityMap extends AppCompatActivity implements OnMapReadyCallback {

    // MapBox
    private final String ACCESS_TOKEN = "sk.eyJ1IjoiZGhydXZnb2hpbCIsImEiOiJjbGdnMXRid3gwN2NjM290MW5pb3k4ZjhsIn0.u9BCMmy1CYch4YbMj6VEYw";
    private LocationManager locationManager;
    private MapView mapMain;
    public Style style;
    public MapboxMap mapboxMap;
    private Style.OnStyleLoaded onStyleLoaded;
    private AppCompatImageButton mapFilter,btnMapDirections;
    private LinearLayout mapLinearLayoutBottom;
    private Polyline polyline;
    private List<Marker> busStationMarkersList, busCenterMarkersList;
    private Marker stationMarker;
    private Marker centerMarker;
    private Marker userMarker2;
    private Marker clickedMarker;
    private LatLng clickedPoint, searchedLatLng,currentLatLng;
    private MarkerOptions clickedmarkerOptions;
    Intent i;
    private ProgressDialog progressDialog;
    private Bitmap bitmap2;
    private Icon icon2;


    // Google Location
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;


    // Dialog
    private SwitchCompat toggleStations,toggleCenters;
    private ImageView darkCardImage,lightCardImage,satelliteCardImage,streetsCardImage;
//    private CardView satelliteCard,lightCard,streetsCard,darkCard;


    // BottomSheet
    private BottomSheetBehavior bottomSheetBehavior;
    private AppCompatImageButton navigateCurrentLocation;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetInternal,inflater,bottomSheetView;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private EditText edt_searchLocation;
    private ImageButton searchBtn;
    private Button btnStartDirections;
    private TextView txt_coordinates, txt_address, txt_distance,txt_time;


    // Integers
    private int initialTranslation;
    private int currentSize;
    private final int MIN_SIZE = 250;


    // Boolean
    private boolean cameraMovedToInitialPosition = false;
    private boolean flagMapTheme;
    private boolean toggleStationisChecked=true;
    private boolean toggleCentersisChecked=true;
    private boolean markersAdded=false;
    private boolean flagSelectedDefaultTheme;
    private boolean hasDataLoaded=false;
    private boolean styleIsLoaded = false;
    private Marker searchedMarker;
    private TextView txt_busStationCenter;
    private String str_fetched_address_1;
    private boolean isBottomSheetShown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialise();
        fetchLocation();
        displayMap();
        buttons();
    }

    private void initialise() {
        Mapbox.getInstance(this, ACCESS_TOKEN);
        setContentView(R.layout.activity_map);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapMain = findViewById(R.id.mapMain);
        navigateCurrentLocation = findViewById(R.id.navigateCurrentLocation);
        mapFilter = findViewById(R.id.mapFilter);
        btnMapDirections = findViewById(R.id.btnMapDirections);
        mapLinearLayoutBottom = findViewById(R.id.mapLinearLayoutBottom);
        i = getIntent();
        flagMapTheme = i.getBooleanExtra("mapTheme", false);
        builder = new AlertDialog.Builder(ActivityMap.this);
        inflater = getLayoutInflater().inflate(R.layout.layout_mapbox_style_dialog, null);
        builder.setView(inflater);
        dialog = builder.create();
        busCenterMarkersList = new ArrayList<>();
        busStationMarkersList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading Map...");
        progressDialog.setMessage("Fetching Location...");
        progressDialog.show();
    }

    public void buttons() {

        navigateCurrentLocation.setOnClickListener(v ->
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13), 600));

        mapFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });

        btnMapDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View contentView = bottomSheetDialog.findViewById(R.id.parentviewbottomsheetmap);

                Objects.requireNonNull(contentView).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                bottomSheetDialog.show();
                initialTranslation = -250;
                mapLinearLayoutBottom.setTranslationY(initialTranslation);
                searchBtn.setVisibility(View.VISIBLE);
                edt_searchLocation.setVisibility(View.VISIBLE);
                btnStartDirections.setVisibility(View.GONE);
                txt_time.setVisibility(View.GONE);
                txt_distance.setVisibility(View.GONE);
                txt_address.setVisibility(View.GONE);
                txt_coordinates.setVisibility(View.GONE);
                txt_busStationCenter.setVisibility(View.GONE);

                if(clickedMarker != null){
                    mapboxMap.removeMarker(clickedMarker);
                }
                if( searchedMarker != null){
                    mapboxMap.removeMarker(searchedMarker);
                }
                if(polyline != null){
                    mapboxMap.removePolyline(polyline);
                }
            }
        });

    }

    private void searchLocation(String search) {

        if (style != null) {
            if (style.getLayer("search-layer") != null) {
                style.removeLayer("search-layer");
            }

            if (style.getSource("search-source") != null) {
                style.removeSource("search-source");
            }
        }

        if(search==null){
            Toast.makeText(this, "Please enter location!", Toast.LENGTH_SHORT).show();
        }
        else{

            MapboxGeocoding geocoding = MapboxGeocoding.builder()
                    .accessToken(ACCESS_TOKEN)
                    .query(search)
                    .build();

            geocoding.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<GeocodingResponse> call, @NonNull retrofit2.Response<GeocodingResponse> response) {
                    List<CarmenFeature> results = Objects.requireNonNull(response.body()).features();
                    if (results.size() > 0) {
                        CarmenFeature feature = results.get(0);
                        searchedLatLng = new LatLng(Objects.requireNonNull(feature.center()).latitude(), feature.center().longitude());

                        MapboxDirections directions = MapboxDirections.builder()
                                .accessToken(ACCESS_TOKEN)
                                .origin(Point.fromLngLat(currentLatLng.getLongitude(), currentLatLng.getLatitude()))
                                .destination(Point.fromLngLat(searchedLatLng.getLongitude(), searchedLatLng.getLatitude()))
                                .build();

                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedLatLng, 15), 1500);

                        btnStartDirections.setVisibility(View.VISIBLE);

//                        btnStartNavigation.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                userNavigate(searchedLatLng);
//                            }
//                        });

                        directions.enqueueCall(new Callback<DirectionsResponse>() {

                            @Override
                            public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull retrofit2.Response<DirectionsResponse> response) {
                                List<DirectionsRoute> routes = Objects.requireNonNull(response.body()).routes();
                                if (routes.size() > 0) {
                                    DirectionsRoute route = routes.get(0);

                                    double distance = route.distance();
                                    double duration = route.duration() / 60;

                                    String distanceStr,durationStr;
                                    if (distance >= 1000) {
                                        distance /= 1000;
                                        distanceStr = String.format(Locale.getDefault(),"%.2f Km", distance);
                                    } else {
                                        distanceStr = String.format(Locale.getDefault(), "%.0f m", distance);
                                    }

                                    if (duration >= 60) {
                                        double hours = duration / 60;
                                        if (hours >= 24) {
                                            double days = hours / 24;
                                            durationStr = String.format(Locale.getDefault(), "%.2f days", days);
                                        } else {
                                            durationStr = String.format(Locale.getDefault(), "%.2f hours", hours);
                                        }
                                    } else {
                                        durationStr = String.format(Locale.getDefault(), "%.0f min", duration);
                                    }

                                    txt_distance.setText(distanceStr);
                                    txt_time.setText(durationStr);

                                    List<LatLng> points = new ArrayList<>();
                                    List<Point> coords = LineString.fromPolyline(Objects.requireNonNull(route.geometry()), Constants.PRECISION_6).coordinates();
                                    for (Point coord : coords) {
                                        points.add(new LatLng(coord.latitude(), coord.longitude()));
                                    }

                                    btnStartDirections.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (polyline != null) {
                                                mapboxMap.removePolyline(polyline);
                                            }
                                            polyline = mapboxMap.addPolyline(new PolylineOptions()
                                                    .addAll(points)
                                                    .color(Color.parseColor("#DC0000"))
                                                    .width(5));

                                            btnStartDirections.setVisibility(View.GONE);
                                        }
                                    });

                                    Bitmap bitmap = BitmapUtils.getBitmapFromDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_location_pin));
                                    Icon icon = IconFactory.getInstance(getApplicationContext()).fromBitmap(Objects.requireNonNull(bitmap));

                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(searchedLatLng)
                                            .setIcon(icon);

                                    searchedMarker = mapboxMap.addMarker(markerOptions);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t) {
                                Toast.makeText(ActivityMap.this, "Route is not possible!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<GeocodingResponse> call, @NonNull Throwable t) {
                    Toast.makeText(ActivityMap.this, "Route is not possible!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void filterDialog(){

        darkCardImage = inflater.findViewById(R.id.darkCardImage);
        lightCardImage = inflater.findViewById(R.id.lightCardImage);
        satelliteCardImage = inflater.findViewById(R.id.satelliteCardImage);
        streetsCardImage = inflater.findViewById(R.id.streetsCardImage);

        darkCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapboxMap.setStyle(Style.TRAFFIC_NIGHT, onStyleLoaded);
                hasDataLoaded=false;

                if (toggleStationisChecked){
                    addBusStationsData();
                }
                if (!toggleStationisChecked){
                    removeBusStationsData();
                }

                if (toggleCentersisChecked){
                    addBusCentersData();
                }
                if (!toggleCentersisChecked){
                    removeBusCentersData();
                }
            }
        });

        lightCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapboxMap.setStyle(Style.TRAFFIC_DAY, onStyleLoaded);
                hasDataLoaded=false;

                if (toggleStationisChecked){
                    addBusStationsData();
                }
                if (!toggleStationisChecked){
                    removeBusStationsData();
                }

                if (toggleCentersisChecked){
                    addBusCentersData();
                }
                if (!toggleCentersisChecked){
                    removeBusCentersData();
                }
            }
        });

        satelliteCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapboxMap.setStyle(Style.SATELLITE, onStyleLoaded);
                hasDataLoaded=false;

                if (toggleStationisChecked){
                    addBusStationsData();
                }
                if (!toggleStationisChecked){
                    removeBusStationsData();
                }

                if (toggleCentersisChecked){
                    addBusCentersData();
                }
                if (!toggleCentersisChecked){
                    removeBusCentersData();
                }
            }
        });

        streetsCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, onStyleLoaded);
                hasDataLoaded=false;

                if (toggleStationisChecked){
                    addBusStationsData();
                }
                if (!toggleStationisChecked){
                    removeBusStationsData();
                }

                if (toggleCentersisChecked){
                    addBusCentersData();
                }
                if (!toggleCentersisChecked){
                    removeBusCentersData();
                }
            }
        });

        toggleStations = inflater.findViewById(R.id.toggleStations);
        toggleCenters = inflater.findViewById(R.id.toggleCenters);

        toggleStations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleStationisChecked = isChecked;

                if (toggleStationisChecked){
                    addBusStationsData();
                }
                if (!toggleStationisChecked){
                    removeBusStationsData();
                }
            }
        });

        toggleCenters.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleCentersisChecked = isChecked;

                if (toggleCentersisChecked){
                    addBusCentersData();
                }
                if (!toggleCentersisChecked){
                    removeBusCentersData();
                }
            }
        });

        dialog.show();
    }

    private void displayMap() {
        mapMain.getMapAsync(this);
    }

    private void removeBusStationsData() {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                for (Marker marker : busStationMarkersList) {
                    marker.remove();
                }
                busStationMarkersList.clear();

            }
        });
    }

    private void removeBusCentersData() {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                for (Marker marker : busCenterMarkersList) {
                    marker.remove();
                }
                busCenterMarkersList.clear();
            }
        });
    }

    public void addBusStationsData() {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                addBusStations(21.1921, 72.8232, "BS-01");
                addBusStations(21.1985, 72.8357, "BS-02");
                addBusStations(21.2093, 72.8261, "BS-03");
                addBusStations(21.2067, 72.8201, "BS-04");
                addBusStations(21.2002, 72.7919, "BS-05");
                addBusStations(21.1918, 72.7893, "BS-06");
                addBusStations(21.1810, 72.8014, "BS-07");
                addBusStations(21.1724, 72.8198, "BS-08");
                addBusStations(21.1689, 72.8254, "BS-09");
                addBusStations(21.1673, 72.8152, "BS-10");
                addBusStations(21.1722, 72.8043, "BS-11");
                addBusStations(21.1433, 72.7721, "BS-12");
                addBusStations(21.1488, 72.7719, "BS-13");
                addBusStations(21.1389, 72.7814, "BS-14");
                addBusStations(21.1473, 72.7691, "BS-15");
                addBusStations(21.1455, 72.7729, "BS-16");
                addBusStations(21.1999, 72.7855, "BS-17");
                addBusStations(21.2013, 72.7899, "BS-18");
                addBusStations(21.1913, 72.7894, "BS-19");
                addBusStations(21.1919, 72.7974, "BS-20");
                addBusStations(21.1945, 72.7886, "BS-21");
                addBusStations(21.2018, 72.8644, "BS-22");
                addBusStations(21.1965, 72.8716, "BS-23");
                addBusStations(21.2075, 72.8733, "BS-24");
                addBusStations(21.1729, 72.8132, "BS-25");
                addBusStations(21.1947, 72.8371, "BS-26");
                addBusStations(21.1874, 72.8492, "BS-27");
                addBusStations(21.1609, 72.8327, "BS-28");
                addBusStations(21.1773, 72.8239, "BS-29");
                addBusStations(21.1812, 72.8396, "BS-30");
                addBusStations(21.2056, 72.8541, "BS-31");
                addBusStations(21.1806, 72.8482, "BS-32");
                addBusStations(21.2081, 72.8256, "BS-33");
                addBusStations(21.1549, 72.8217, "BS-34");
                addBusStations(21.2022, 72.8056, "BS-35");
                addBusStations(21.1523, 72.7711, "BS-36");
                addBusStations(21.2211, 72.8597, "BS-37");
                addBusStations(21.1832, 72.7774, "BS-38");
                addBusStations(21.1645, 72.7785, "BS-39");
                addBusStations(21.2149, 72.7951, "BS-40");
                addBusStations(21.1496, 72.7951, "BS-41");
                addBusStations(21.2201, 72.8209, "BS-42");
                addBusStations(21.2004, 72.8553, "BS-43");
                addBusStations(21.1675, 72.8506, "BS-44");
                addBusStations(21.1989, 72.8241, "BS-45");
                addBusStations(21.1865, 72.7957, "BS-46");
                addBusStations(21.1785, 72.7986, "BS-47");
                addBusStations(21.1479, 72.8122, "BS-48");
                addBusStations(21.1598, 72.8574, "BS-49");
                addBusStations(21.2017, 72.8675, "BS-50");
                addBusStations(21.1921, 72.8232, "BS-50");
                addBusStations(21.1892, 72.7807, "BS-51");
                addBusStations(21.2064, 72.7865, "BS-52");
                addBusStations(21.2186, 72.7739, "BS-53");
                addBusStations(21.2030, 72.8041, "BS-54");
                addBusStations(21.2129, 72.8182, "BS-55");
                addBusStations(21.2026, 72.8102, "BS-56");
                addBusStations(21.2173, 72.7875, "BS-57");
                addBusStations(21.2040, 72.7956, "BS-58");
                addBusStations(21.2058, 72.7860, "BS-59");
                addBusStations(21.1644, 72.7526, "BS-71");
                addBusStations(21.1500, 72.7782, "BS-72");
                addBusStations(21.1742, 72.7703, "BS-73");
                addBusStations(21.1445, 72.7568, "BS-74");
                addBusStations(21.1422, 72.7502, "BS-75");
                addBusStations(21.1473, 72.7643, "BS-76");
                addBusStations(21.1649, 72.7571, "BS-77");
                addBusStations(21.1717, 72.7520, "BS-78");
                addBusStations(21.1565, 72.7855, "BS-79");
                addBusStations(21.1684, 72.7629, "BS-80");
                addBusStations(21.1771, 72.7857, "BS-81");

            }
        });
    }

    private void addBusCentersData() {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                addBusCenters(21.1418, 72.7709,"Vesu");
                addBusCenters(21.1959, 72.7933,"Adajan");
                addBusCenters(21.2021, 72.8673,"Varachha");
                addBusCenters(21.2189, 72.7961,"Rander");
                addBusCenters(21.1593, 72.7712,"Piplod");
                addBusCenters(21.1695, 72.7930,"Parle Point");
                addBusCenters(21.1895, 72.8346,"Salabatpura");
            }
        });
    }

    private void addBusStations(double latitude, double longitude,String id) {

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_bus_stops);
        Bitmap bitmap = BitmapUtils.getBitmapFromDrawable(drawable);
        Icon icon = IconFactory.getInstance(getApplicationContext()).fromBitmap(Objects.requireNonNull(bitmap));

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Bus Station")
                .setIcon(icon)
                .snippet(id);

        stationMarker = mapboxMap.addMarker(markerOptions);
        busStationMarkersList.add(stationMarker);
    }

    private void addBusCenters(double latitude, double longitude, String title) {

        Bitmap bitmap = BitmapUtils.getBitmapFromDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_bus_centers));
        Icon icon = IconFactory.getInstance(getApplicationContext()).fromBitmap(Objects.requireNonNull(bitmap));

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Bus Center")
                .setIcon(icon)
                .snippet(title);
        centerMarker = mapboxMap.addMarker(markerOptions);
        busCenterMarkersList.add(centerMarker);
    }

    public void bottomSheetDialogDisplay() {
        bottomSheetDialog = new BottomSheetDialog(ActivityMap.this, R.style.BottomSheetDialogTheme);
        bottomSheetView = getLayoutInflater().inflate(R.layout.layout_map_bottom_sheet_dialog, findViewById(R.id.parentviewbottomsheetmap));
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        Window dialogWindow = bottomSheetDialog.getWindow();
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        bottomSheetBehavior = bottomSheetDialog.getBehavior();
        bottomSheetBehavior.setPeekHeight(260,true);
        edt_searchLocation = bottomSheetView.findViewById(R.id.searchLocation);
        searchBtn = bottomSheetView.findViewById(R.id.searchBtn);
        txt_time = bottomSheetView.findViewById(R.id.txt_time);
        txt_distance = bottomSheetView.findViewById(R.id.txt_distance);
        btnStartDirections = bottomSheetView.findViewById(R.id.btnStartDirections);
        txt_address = bottomSheetView.findViewById(R.id.txt_address);
        txt_coordinates = bottomSheetView.findViewById(R.id.txt_coordinates);
        txt_busStationCenter = bottomSheetView.findViewById(R.id.txt_busStationCenter);
        btnStartDirections.setVisibility(View.GONE);
        txt_time.setVisibility(View.GONE);
        txt_distance.setVisibility(View.GONE);
        txt_address.setVisibility(View.GONE);
        txt_coordinates.setVisibility(View.GONE);
        txt_busStationCenter.setVisibility(View.GONE);
        edt_searchLocation.setFocusable(false);

        edt_searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAutocompleteActivity();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchLoc = edt_searchLocation.getText().toString();

                if(!searchLoc.isEmpty()){
                    searchLocation(searchLoc);
                    txt_distance.setVisibility(View.VISIBLE);
                    txt_time.setVisibility(View.VISIBLE);
                    initialTranslation = -450;
                    mapLinearLayoutBottom.setTranslationY(initialTranslation);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else{
                    Toast.makeText(ActivityMap.this, "Please search a location!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        BottomSheetBehavior.from(Objects.requireNonNull(bottomSheetInternal)).addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {}

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float translationY = initialTranslation + (initialTranslation * slideOffset / 2);
                mapLinearLayoutBottom.setTranslationY(translationY);
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                float currentTranslationY = mapLinearLayoutBottom.getTranslationY();
                ObjectAnimator animator = ObjectAnimator.ofFloat(mapLinearLayoutBottom, "translationY", currentTranslationY, 0);
                animator.setDuration(500);
                animator.start();
            }
        });

    }

    private void openAutocompleteActivity() {

        btnStartDirections.setVisibility(View.GONE);

        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(ACCESS_TOKEN)
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(getResources().getColor(R.color.white))
                        .limit(5)
                        .backgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.main_500))
                        .toolbarColor(ContextCompat.getColor(getApplicationContext(),R.color.main_500))
                        .build(PlaceOptions.MODE_CARDS))
                .build(ActivityMap.this);
        startActivityForResult(intent, 1);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            com.mapbox.api.geocoding.v5.models.CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(Objects.requireNonNull(data));
            if (selectedCarmenFeature != null) {
                String selectedPlace = selectedCarmenFeature.placeName();
                edt_searchLocation.setText(selectedPlace);
                txt_distance.setVisibility(View.GONE);
                txt_time.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Error selecting place", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchLocation() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityMap.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {
                            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                            if(styleIsLoaded){
                                progressDialog.dismiss();
                                moveCameraToInitialPosition();
                            }
                        }
                    }
                };
                fusedLocationClient.requestLocationUpdates(new LocationRequest().setInterval(0), locationCallback, Looper.myLooper());
            }
        }
        displayMap();
    }

    public void onResume() {
        super.onResume();

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (isLocationEnabled) {
                        fusedLocationClient.requestLocationUpdates(new LocationRequest().setInterval(0), locationCallback, null);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please turn on location services!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    ActivityCompat.requestPermissions(ActivityMap.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    Toast.makeText(getApplicationContext(), "Location permission is required for Map!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
        mapMain.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.requestLocationUpdates(new LocationRequest().setInterval(0), locationCallback, null);
                }
            }
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        fusedLocationClient.removeLocationUpdates(locationCallback);
//    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap1) {
        mapboxMap = mapboxMap1;
        mapClickMarker();
        bottomSheetDialogDisplay();

        do {
            if (flagMapTheme) {
                mapboxMap.setStyle(Style.TRAFFIC_NIGHT, onStyleLoaded);
                flagSelectedDefaultTheme = false;
            } else {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, onStyleLoaded);
                flagSelectedDefaultTheme = true;
            }

        } while (0 == 1);

        onStyleLoaded = new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style1) {

                styleIsLoaded = true;
                mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(@NonNull Marker marker) {
                        View infoWindow = getLayoutInflater().inflate(R.layout.custom_infowwindow_layout, null);

                        TextView markerTitle = infoWindow.findViewById(R.id.marker_title);
                        markerTitle.setText(marker.getTitle());

                        TextView markerSnippet = infoWindow.findViewById(R.id.marker_snippet);
                        markerSnippet.setText(marker.getSnippet());

                        return infoWindow;
                    }
                });

                UiSettings uiSettings = mapboxMap.getUiSettings();
                Drawable compassDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_compass);
                Drawable compassDrawableNorth = ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_compass_north);

                uiSettings.setCompassImage(Objects.requireNonNull(compassDrawable));
                uiSettings.setCompassFadeFacingNorth(false);
                uiSettings.setCompassMargins(0, 200, 30, 0);

                mapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {

                        double latitude = mapboxMap.getCameraPosition().target.getLatitude();
                        double longitude = mapboxMap.getCameraPosition().target.getLongitude();

                        double formattedLatitude = Double.parseDouble(String.format(Locale.getDefault(),"%.9f", latitude));
                        double formattedLongitude = Double.parseDouble(String.format(Locale.getDefault(),"%.9f", longitude));


                        LatLng targetLatLng = new LatLng(formattedLatitude,formattedLongitude);

                        if (targetLatLng.equals(currentLatLng)) {
                            navigateCurrentLocation.setImageResource(R.drawable.icon_my_location);
                        } else {
                            navigateCurrentLocation.setImageResource(R.drawable.icon_my_location_v2);
                        }

                        if (mapboxMap.getCameraPosition().bearing == 0) {
                            uiSettings.setCompassImage(Objects.requireNonNull(compassDrawableNorth));
                        } else {
                            uiSettings.setCompassImage(compassDrawable);
                        }
                    }
                });

                style = style1;

                if (!hasDataLoaded) {
                    addBusStationsData();
                    addBusCentersData();
                    hasDataLoaded = true;
                }

            }
        };
    }

    private void userCurrentLocationIcon() {
        Drawable drawable1 = ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_circle_outline);
        Drawable drawable2 = ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_circle_outline2);

        float density = getResources().getDisplayMetrics().density;

        Bitmap bitmap1 = BitmapUtils.getBitmapFromDrawable(drawable1);
        Icon icon1 = IconFactory.getInstance(getApplicationContext()).fromBitmap(Objects.requireNonNull(bitmap1));

        if(currentLatLng == null) {
            currentLatLng = new LatLng(0,0);
        }
        if (!markersAdded) {
            MarkerOptions markerOptions1 = new MarkerOptions().position(currentLatLng).icon(icon1);
            mapboxMap.addMarker(markerOptions1);

            bitmap2 = BitmapUtils.getBitmapFromDrawable(drawable2);
            currentSize = (int) (50 * density * (1 - (mapboxMap.getCameraPosition().zoom - 14) / 4));
            currentSize = Math.max(currentSize, MIN_SIZE);
            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap2, currentSize, currentSize, false);
            icon2 = IconFactory.getInstance(getApplicationContext()).fromBitmap(newBitmap);

            MarkerOptions markerOptions2 = new MarkerOptions().position(currentLatLng).icon(icon2);
            userMarker2 = mapboxMap.addMarker(markerOptions2);
            markersAdded = true;
        }

        mapboxMap.addOnCameraMoveListener(() -> {
            int currentSize1 = (int) (50 * density * (1 - (mapboxMap.getCameraPosition().zoom - 14) / 4));
            currentSize1 = Math.max(currentSize1, MIN_SIZE);

            if (currentSize != currentSize1) {
                currentSize = currentSize1;
                Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap2, currentSize, currentSize, false);
                Icon newIcon = IconFactory.getInstance(getApplicationContext()).fromBitmap(newBitmap);
                userMarker2.setIcon(newIcon);
            }
        });
    }

    private void moveCameraToInitialPosition() {
        if (!cameraMovedToInitialPosition) {
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13), 600, new MapboxMap.CancelableCallback() {
                @Override
                public void onCancel() {}

                @Override
                public void onFinish() {
                    userCurrentLocationIcon();
                }
            });
            cameraMovedToInitialPosition = true;
        }
    }

    public void mapClickMarker(){

        // Click on marker like Bus Center
        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                    if (Objects.equals(marker.getTitle(), "Bus Center") || Objects.equals(marker.getTitle(),"Bus Station")) {

                        edt_searchLocation.setVisibility(View.GONE);
                        searchBtn.setVisibility(View.GONE);
                        txt_coordinates.setVisibility(View.VISIBLE);
                        btnStartDirections.setVisibility(View.VISIBLE);
                        txt_busStationCenter.setVisibility(View.VISIBLE);
                        txt_distance.setVisibility(View.GONE);
                        txt_time.setVisibility(View.GONE);
                        txt_address.setVisibility(View.VISIBLE);
                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition()), 15), 1500);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL url = new URL("https://nominatim.openstreetmap.org/reverse.php?format=json&lat=" + marker.getPosition().getLatitude() + "&lon=" + marker.getPosition().getLongitude());
                                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                    urlConnection.setRequestMethod("GET");

                                    InputStream inputStream = urlConnection.getInputStream();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                    StringBuilder stringBuilder = new StringBuilder();

                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        stringBuilder.append(line);
                                    }

                                    JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                                    str_fetched_address_1 = jsonObject.getString("display_name");

                                    double lat = jsonObject.getDouble("lat");
                                    double lon = jsonObject.getDouble("lon");

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            txt_address.setText(str_fetched_address_1);
                                            txt_coordinates.setText(String.format("%s, %s", lat, lon));
                                        }
                                    });

                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ActivityMap.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        }).start();

                        txt_busStationCenter.setText(String.format("%s - %s", marker.getTitle(), marker.getSnippet()));
                        txt_coordinates.setText(String.format("%s, %s", marker.getPosition().getLatitude(), marker.getPosition().getLongitude()));

                        bottomSheetBehavior.setPeekHeight(700,true);
                        initialTranslation = -450;
                        mapLinearLayoutBottom.setTranslationY(initialTranslation);
                        bottomSheetDialog.show();

                        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                edt_searchLocation.setVisibility(View.VISIBLE);
                                searchBtn.setVisibility(View.VISIBLE);
                                txt_coordinates.setVisibility(View.GONE);
                                txt_distance.setVisibility(View.GONE);
                                txt_time.setVisibility(View.GONE);
                                btnStartDirections.setVisibility(View.GONE);
                                txt_address.setVisibility(View.GONE);
                                txt_busStationCenter.setVisibility(View.GONE);

                                float currentTranslationY = mapLinearLayoutBottom.getTranslationY();
                                ObjectAnimator animator = ObjectAnimator.ofFloat(mapLinearLayoutBottom, "translationY", currentTranslationY, 0);
                                animator.setDuration(500);
                                animator.start();
                            }
                        });

                        btnStartDirections.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                MapboxGeocoding geocoding = MapboxGeocoding.builder()
                                        .accessToken(ACCESS_TOKEN)
                                        .query(str_fetched_address_1)
                                        .build();

                                geocoding.enqueueCall(new Callback<GeocodingResponse>() {
                                    @Override
                                    public void onResponse(@NonNull retrofit2.Call<GeocodingResponse> call, @NonNull retrofit2.Response<GeocodingResponse> response) {

                                        if (response.body() != null) {

                                            btnStartDirections.setVisibility(View.GONE);

                                            List<CarmenFeature> results = response.body().features();
                                            if (results.size() > 0) {

                                                MapboxDirections directions = MapboxDirections.builder()
                                                        .accessToken(ACCESS_TOKEN)
                                                        .origin(Point.fromLngLat(currentLatLng.getLongitude(), currentLatLng.getLatitude()))
                                                        .destination(Point.fromLngLat(marker.getPosition().getLongitude(), marker.getPosition().getLatitude()))
                                                        .build();

                                                directions.enqueueCall(new Callback<DirectionsResponse>() {

                                                    @Override
                                                    public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull retrofit2.Response<DirectionsResponse> response) {
                                                        List<DirectionsRoute> routes = Objects.requireNonNull(response.body()).routes();
                                                        if (routes.size() > 0) {
                                                            DirectionsRoute route = routes.get(0);

                                                            double distance = route.distance();
                                                            double duration = route.duration() / 60;

                                                            String distanceStr, durationStr;

                                                            if (distance >= 1000) {
                                                                distance /= 1000;
                                                                distanceStr = String.format(Locale.getDefault(), "%.2f Km", distance);
                                                            } else {
                                                                distanceStr = String.format(Locale.getDefault(), "%.0f m", distance);
                                                            }

                                                            if (duration >= 60) {
                                                                double hours = duration / 60;
                                                                if (hours >= 24) {
                                                                    double days = hours / 24;
                                                                    durationStr = String.format(Locale.getDefault(), "%.2f days", days);
                                                                } else {
                                                                    durationStr = String.format(Locale.getDefault(), "%.2f hours", hours);
                                                                }
                                                            } else {
                                                                durationStr = String.format(Locale.getDefault(), "%.0f min", duration);
                                                            }
                                                            txt_distance.setVisibility(View.VISIBLE);
                                                            txt_time.setVisibility(View.VISIBLE);

                                                            txt_distance.setText(distanceStr);
                                                            txt_time.setText(durationStr);

                                                            List<LatLng> points = new ArrayList<>();
                                                            List<Point> coords = LineString.fromPolyline(Objects.requireNonNull(route.geometry()), Constants.PRECISION_6).coordinates();
                                                            for (Point coord : coords) {
                                                                points.add(new LatLng(coord.latitude(), coord.longitude()));
                                                            }

                                                            polyline = mapboxMap.addPolyline(new PolylineOptions()
                                                                    .addAll(points)
                                                                    .color(Color.parseColor("#DC0000"))
                                                                    .width(5));
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t) {
                                                        Toast.makeText(ActivityMap.this, "Route is not possible!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                        else{
                                            Toast.makeText(ActivityMap.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(@NonNull retrofit2.Call<GeocodingResponse> call, @NonNull Throwable t) {
                                        Toast.makeText(ActivityMap.this, "Route is not possible!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                        return true;
                    }
                return false;
            }
        });

        // Click on map to add a marker
        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull LatLng point) {
                clickedPoint = point;
                bottomSheetDialog.dismiss();

                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_drop_pin);
                Bitmap bitmap = BitmapUtils.getBitmapFromDrawable(drawable);
                Icon icon = IconFactory.getInstance(getApplicationContext()).fromBitmap(Objects.requireNonNull(bitmap));

                clickedmarkerOptions = new MarkerOptions().position(point).setIcon(icon);

                if (searchedMarker != null) {
                    edt_searchLocation.setVisibility(View.VISIBLE);
                    searchBtn.setVisibility(View.VISIBLE);
                    txt_coordinates.setVisibility(View.GONE);
                    txt_distance.setVisibility(View.GONE);
                    txt_time.setVisibility(View.GONE);
                    btnStartDirections.setVisibility(View.GONE);
                    txt_address.setVisibility(View.GONE);
                    txt_busStationCenter.setVisibility(View.GONE);
                    mapboxMap.removeMarker(searchedMarker);
                    mapClickMarker();
                    searchedMarker = null;
                }

                if (clickedMarker != null) {
                    edt_searchLocation.setVisibility(View.VISIBLE);
                    searchBtn.setVisibility(View.VISIBLE);
                    txt_coordinates.setVisibility(View.GONE);
                    txt_busStationCenter.setVisibility(View.GONE);
                    txt_distance.setVisibility(View.GONE);
                    txt_time.setVisibility(View.GONE);
                    btnStartDirections.setVisibility(View.GONE);
                    txt_address.setVisibility(View.VISIBLE);
                    mapboxMap.removeMarker(clickedMarker);
                    mapClickMarker();
                    clickedMarker = null;
                }
                else{
                    clickedMarker = mapboxMap.addMarker(clickedmarkerOptions);

                    edt_searchLocation.setVisibility(View.GONE);
                    searchBtn.setVisibility(View.GONE);
                    txt_coordinates.setVisibility(View.VISIBLE);
                    btnStartDirections.setVisibility(View.VISIBLE);
                    txt_busStationCenter.setVisibility(View.VISIBLE);
                    txt_distance.setVisibility(View.GONE);
                    txt_time.setVisibility(View.GONE);
                    txt_address.setVisibility(View.VISIBLE);

                    txt_busStationCenter.setText(R.string.clicked_marker);
                    bottomSheetBehavior.setPeekHeight(570,true);
                    initialTranslation = -550;
                    mapLinearLayoutBottom.setTranslationY(initialTranslation);
                    bottomSheetDialog.show();

                    isBottomSheetShown = false;
                    clickedMarker.setTitle("Clicked");
                    clickedMarker.setId("clicked".hashCode());
                }

                if(polyline != null){
                    mapboxMap.removePolyline(polyline);
                    polyline = null;
                }

                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        if (Objects.equals(marker.getTitle(), "Clicked")) {
                            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(clickedPoint.getLatitude(), clickedPoint.getLongitude()), 15), 1500);
                            bottomSheetDialog.show();
                            return true;
                        }
                        return false;
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("https://nominatim.openstreetmap.org/reverse.php?format=json&lat=" + clickedPoint.getLatitude() + "&lon=" + clickedPoint.getLongitude());
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestMethod("GET");

                            InputStream inputStream = urlConnection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            StringBuilder stringBuilder = new StringBuilder();

                            String line;
                            while ((line = reader.readLine()) != null) {
                                stringBuilder.append(line);
                            }

                            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                            str_fetched_address_1 = jsonObject.getString("display_name");
                            double lat = jsonObject.getDouble("lat");
                            double lon = jsonObject.getDouble("lon");

                            if (!isBottomSheetShown) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() { // for clicked marker

                                        txt_coordinates.setText(String.format("%s, %s", lat, lon));
                                        txt_address.setText(str_fetched_address_1);
                                    }
                                });
                                isBottomSheetShown = true;
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActivityMap.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).start();

                btnStartDirections.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MapboxGeocoding geocoding = MapboxGeocoding.builder()
                                .accessToken(ACCESS_TOKEN)
                                .query(str_fetched_address_1)
                                .build();

                        geocoding.enqueueCall(new Callback<GeocodingResponse>() {
                            @Override
                            public void onResponse(@NonNull retrofit2.Call<GeocodingResponse> call, @NonNull retrofit2.Response<GeocodingResponse> response) {

                                if (response.body() != null) {

                                    btnStartDirections.setVisibility(View.GONE);

                                    List<CarmenFeature> results = response.body().features();
                                    if (results.size() > 0) {

                                        MapboxDirections directions = MapboxDirections.builder()
                                                .accessToken(ACCESS_TOKEN)
                                                .origin(Point.fromLngLat(currentLatLng.getLongitude(), currentLatLng.getLatitude()))
                                                .destination(Point.fromLngLat(clickedPoint.getLongitude(), clickedPoint.getLatitude()))
                                                .build();

                                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clickedPoint, 15), 1500);

                                        directions.enqueueCall(new Callback<DirectionsResponse>() {

                                            @Override
                                            public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull retrofit2.Response<DirectionsResponse> response) {
                                                List<DirectionsRoute> routes = Objects.requireNonNull(response.body()).routes();
                                                if (routes.size() > 0) {
                                                    DirectionsRoute route = routes.get(0);

                                                    double distance = route.distance();
                                                    double duration = route.duration() / 60;

                                                    String distanceStr, durationStr;

                                                    if (distance >= 1000) {
                                                        distance /= 1000;
                                                        distanceStr = String.format(Locale.getDefault(), "%.2f Km", distance);
                                                    } else {
                                                        distanceStr = String.format(Locale.getDefault(), "%.0f m", distance);
                                                    }

                                                    if (duration >= 60) {
                                                        double hours = duration / 60;
                                                        if (hours >= 24) {
                                                            double days = hours / 24;
                                                            durationStr = String.format(Locale.getDefault(), "%.2f days", days);
                                                        } else {
                                                            durationStr = String.format(Locale.getDefault(), "%.2f hours", hours);
                                                        }
                                                    } else {
                                                        durationStr = String.format(Locale.getDefault(), "%.0f min", duration);
                                                    }
                                                    txt_distance.setVisibility(View.VISIBLE);
                                                    txt_time.setVisibility(View.VISIBLE);
                                                    txt_distance.setText(distanceStr);
                                                    txt_time.setText(durationStr);

                                                    List<LatLng> points = new ArrayList<>();
                                                    List<Point> coords = LineString.fromPolyline(Objects.requireNonNull(route.geometry()), Constants.PRECISION_6).coordinates();
                                                    for (Point coord : coords) {
                                                        points.add(new LatLng(coord.latitude(), coord.longitude()));
                                                    }

                                                    polyline = mapboxMap.addPolyline(new PolylineOptions()
                                                            .addAll(points)
                                                            .color(Color.parseColor("#DC0000"))
                                                            .width(5));
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t) {
                                                Toast.makeText(ActivityMap.this, "Route is not possible!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                                else{
                                    Toast.makeText(ActivityMap.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(@NonNull retrofit2.Call<GeocodingResponse> call, @NonNull Throwable t) {
                                Toast.makeText(ActivityMap.this, "Route is not possible!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                return true;
            }
        });

    }
}