package com.example.graduation_project;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener
        {  //추가됨. 5월10일.


    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;
    private Marker anotherMarker = null;

    // 0514 추가
    private LinkedList<String> mapx = new LinkedList<String>();
    private LinkedList<String> mapy = new LinkedList<String>();
    private LinkedList<String> title = new LinkedList<String>();
    private LinkedList<String> firstimage = new LinkedList<String>();
    private LinkedList<String> cat2 = new LinkedList<String>();
    private LinkedList<String> addr = new LinkedList<String>();
    private LinkedList<String> contentTypeId = new LinkedList<String>();

    private ArrayList<String> recommend_id = new ArrayList<String>();

    Double latitude;
    Double longitude;
    //

    private static final String TAG = "MapActivity";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 3000;  // 3초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 2000; // 0.5초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPosition;
    String key = "IkguwPNvNewFWJqbZkLx%2F96u2iRVD59mPrSq8xm0YfbWa7qcWFR155mFpJsoWt9Ql2zR5SwwF1FVLbdSOWWw1A%3D%3D";
    // OpenAPI의 인증 키

    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    List<Marker> previous_marker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        Locale locale = this.getResources().getConfiguration().locale;
        System.out.println("locale : " + locale);

        mActivity = this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //추가. 5월10일. 버튼 리스너
        previous_marker = new ArrayList<Marker>();


        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, RecommendActivity.class);
                MapActivity.this.startActivity(intent);
            }
        });

        String [] recommend_str = {
                "126537", "126512", "126508", "127642", "126510", "125565",
                "126848", "128184", "125555", "125578", "125580", "125579",
                "125906", "125983", "126166", "126207", "126216", "129489"};

        try
        {
            for (int i=0; i < recommend_str.length; i++) {
                recommend_id.add(recommend_str[i]);
            }
        }
        catch (IndexOutOfBoundsException io)
        {
            io.printStackTrace();
            System.out.println("IndexOutOfBoundsException!");
        }
        finally {
            System.out.println(recommend_id);
        }


        // 0515
        final Use_Thread use_thread = new Use_Thread();
        use_thread.setDaemon(true);





        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                use_thread.start();
            }
        };
        TimerTask print = new TimerTask() {
            @Override
            public void run() {
//                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
//                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n");
                int list_size = title.size();
//                System.out.println("사이즈 = " + list_size);
//                for (int i=0; i<list_size; i++) {
//                    System.out.println("인터럽트 이후 " + i + "번째 리스트는 "
//                            + "\naddr = " + addr.get(i)
//                            + "\ncat2 = " + cat2.get(i)
//                            + "\ncontentTypeId = " + contentTypeId.get(i)
//                            + "\nmapx = " + mapx.get(i)
//                            + "\nmapy = " + mapy.get(i)
//                            + "\nfirstimage = " + firstimage.get(i)
//                            + "\ntitle = " + title.get(i)
//                    );
//                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 1000);
        timer.schedule(print, 3000);
    }


    @Override
    public void onResume() {

        super.onResume();

        if (mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }


        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }
    }


    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {


//            System.out.println("startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//                System.out.println("startLocationUpdates : 퍼미션 안가지고 있음");
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }

//            System.out.println("startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);

        }

    }


    private void stopLocationUpdates() {


//        System.out.println("stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        Log.d(TAG, "stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

//        System.out.println("onMapReady :");
        Log.d(TAG, "onMapReady :");

        mGoogleMap = googleMap;


        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(9));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {

                Log.d(TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

//                System.out.println("onMapClick :");
                Log.d(TAG, "onMapClick :");
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {

                if (mMoveMapByUser == true && mRequestingLocationUpdates) {

//                    System.out.println("onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    Log.d(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;

            }
        });


        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {


            }
        });


    }


    @Override
    public void onLocationChanged(Location location) {

        currentPosition
                = new LatLng(location.getLatitude(), location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
//        System.out.println(latitude + "   " + longitude);

//        System.out.println("onLocationChanged : ");
        Log.d(TAG, "onLocationChanged : ");

        String markerTitle = getCurrentAddress(currentPosition);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());
        //현재 위치에 마커 생성하고 이동



        setCurrentLocation(location, markerTitle, markerSnippet);
        mCurrentLocatiion = location;
    }


    @Override
    protected void onStart() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() == false) {

//            System.out.println("onStart: mGoogleApiClient connect");
            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    protected void onStop() {

        if (mRequestingLocationUpdates) {
//            System.out.println("onStop : call stopLocationUpdates");
            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if (mGoogleApiClient.isConnected()) {
//            System.out.println("onStop : mGoogleApiClient disconnect");
            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) {


        if (mRequestingLocationUpdates == false) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                } else {
//                    System.out.println("onConnected : ");
                    Log.d(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

            } else {
//                System.out.println("onConnected : call startLocationUpdates");
                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("onConnectionFailed");
        setDefaultLocation();
    }


    @Override
    public void onConnectionSuspended(int cause) {
        System.out.println("onConnectionSuspended");
    }


    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
//            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
//            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        mMoveMapByUser = false;


        if (currentMarker != null) {
            currentMarker.remove();
            System.out.println(currentMarker.getPosition());

        }



        final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        List<MarkerOptions> markers = new ArrayList<MarkerOptions>();


        //0514
        latitude = location.getLatitude();
        longitude = location.getLongitude();


//        MarkerOptions mylocateMarkers = new MarkerOptions();
//        mylocateMarkers.position(currentLatLng);
//        mylocateMarkers.title(markerTitle);
//        mylocateMarkers.snippet(markerSnippet);
//        mylocateMarkers.draggable(true);
//        mylocateMarkers.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        currentMarker = mGoogleMap.addMarker(mylocateMarkers);


        int cat2_size = cat2.size();
        String get = "";
//        System.out.println("cat2_size = " + cat2_size);
        for (int i = 0; i < cat2_size; i++)
        {
//            get = cat2.get(i);
//            System.out.println("if문 A0201");
//            System.out.println(addr.get(i));
//            System.out.println(contentTypeId.get(i));
//            System.out.println(mapx.get(i));
//            System.out.println(mapy.get(i));
//            System.out.println(firstimage.get(i));
//            System.out.println(title.get(i));
            if (cat2.get(i).equals("A0201")) {
                LatLng newLatLng = new LatLng(Double.parseDouble(mapy.get(i)), Double.parseDouble(mapx.get(i)));
                MarkerOptions marker = new MarkerOptions();
                marker.position(newLatLng);
                marker.title(title.get(i));
                marker.snippet(contentTypeId.get(i));

                if (recommend_id.contains(contentTypeId.get(i)))
                {
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.recommend));
                }
                else
                {
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                }
                marker.draggable(true);
                markers.add(marker);

            }

//            System.out.println("cat2.get("+i+") = "+cat2.get(i) + "  get = " + get);
//            if (cat2.get(i).equals("A0201")) {
//                System.out.println("if문 A0201");
//                System.out.println(addr.get(i));
//                System.out.println(contentTypeId.get(i));
//                System.out.println(mapx.get(i));
//                System.out.println(mapy.get(i));
//                System.out.println(firstimage.get(i));
//                System.out.println(title.get(i));
//
//                LatLng newLatLng = new LatLng(Double.parseDouble(mapy.get(i)), Double.parseDouble(mapx.get(i)));
//                MarkerOptions marker = new MarkerOptions();
//                marker.position(newLatLng);
//                marker.title(title.get(i));
////                marker.snippet(contentTypeId.get(i));
//
//                String rating = "5";
//                if (rating.equals("1")) {
//                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                }
//                else if (rating.equals("2")) {
//                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//                }
//                else if (rating.equals("3")) {
//                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
//                }
//                else if (rating.equals("4")) {
//                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                }
//                else if (rating.equals("5")) {
//                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//                }
//                else {
//                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
//                }
//
////                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_phone));
//                marker.draggable(true);
//                markers.add(marker);
//            }
//            else
//            {
////                System.out.println("else");
//            }

        }



        for (MarkerOptions marker:markers) {
            mGoogleMap.addMarker(marker);
        }

        mGoogleMap.setOnMarkerClickListener(this);
        //0514 추가


        if (mMoveMapByAPI) {

            Log.d(TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude());
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }




    public void setDefaultLocation() {

        mMoveMapByUser = false;


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerOptions.draggable(true);
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 9);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {


            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");

            if (mGoogleApiClient.isConnected() == false) {

                Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {


                if (mGoogleApiClient.isConnected() == false) {

                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }


            } else {

                checkPermissions();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");


                        if (mGoogleApiClient.isConnected() == false) {

                            Log.d(TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }

                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14);
        mGoogleMap.moveCamera(cameraUpdate);
        marker.showInfoWindow();
        /// 0723 추가
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("메시지")
                .setMessage("무슨 서비스를 실행시겠습니까?")
                .setPositiveButton("길찾기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 인텐트로 Tmap으로 이동한다.
                        Intent intent = new Intent(MapActivity.this, SKTMap2Activity.class);
                        intent.putExtra("currentlongtitude", currentPosition.longitude);
                        intent.putExtra("currentlatitude", currentPosition.latitude);
                        intent.putExtra("markerlongtitude", marker.getPosition().longitude);
                        intent.putExtra("markerlatitude", marker.getPosition().latitude);
                        System.out.println("cplo : " + currentPosition.longitude);
                        System.out.println("cpla : " + currentPosition.latitude);
                        System.out.println("mplo : " + marker.getPosition().longitude);
                        System.out.println("mpla : " + marker.getPosition().latitude);
                        MapActivity.this.startActivity(intent);
                    }
                })
                .setNegativeButton("상세정보", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MapActivity.this, MapCompareActivity.class);
                        intent.putExtra("id", marker.getSnippet());
                        MapActivity.this.startActivity(intent);
                    }
                })
        .create().show();
        return true;
    }

            //0514
    class Use_Thread extends Thread {
        @Override
        public void run() {
            Main_Method(latitude, longitude);
//            System.out.println("유즈쓰레드 실행");
            TimerTask interrupt_Thread = new TimerTask() {
                @Override
                public void run() {
                    interrupt();
//                    System.out.println("인터럽트 1");
                }
            };
            Timer inter_timer = new Timer();
            inter_timer.schedule(interrupt_Thread, 10000);
        }
    }

    public void Main_Method(Double latitude, Double longitude)
    {
        //0514 추가
        try
        {
            Locale locale = this.getResources().getConfiguration().locale;
            String queryURL = "";
            Resources res = getResources();

            if (locale.getLanguage() == "ko")
            {
                queryURL =
                        "http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey="
                                + key
                                +"&mapX="
                                +longitude
                                +"&mapY="
                                +latitude
                                +"&radius=50000&listYN=Y&arrange=P&MobileOS=ETC&MobileApp="
                                +res.getString(R.string.app_name)
                                +"&numOfRows=80&pageNo=1"
                                +"&contentTypeId=12";
            }
            if (locale.getLanguage() == "en")
            {
                queryURL =
                        "http://api.visitkorea.or.kr/openapi/service/rest/EngService/locationBasedList?ServiceKey="
                                + key
                                +"&mapX="
                                +longitude
                                +"&mapY="
                                +latitude
                                +"&radius=50000&listYN=Y&arrange=P&MobileOS=ETC&MobileApp="
                                +res.getString(R.string.app_name);

            }
            if (locale.getLanguage() == "ja")
            {
                queryURL =
                        "http://api.visitkorea.or.kr/openapi/service/rest/JpnService/locationBasedList?ServiceKey="
                                + key
                                +"&mapX="
                                +longitude
                                +"&mapY="
                                +latitude
                                +"&radius=50000&listYN=Y&arrange=P&MobileOS=ETC&MobileApp="
                                +res.getString(R.string.app_name);

            }
            if (locale.getLanguage() == "zh")
            {
                queryURL =
                        "http://api.visitkorea.or.kr/openapi/service/rest/ChsService/locationBasedList?ServiceKey="
                                + key
                                +"&mapX="
                                +longitude
                                +"&mapY="
                                +latitude
                                +"&radius=50000&listYN=Y&arrange=P&MobileOS=ETC&MobileApp="
                                +res.getString(R.string.app_name);

            }
//            System.out.println("queryURL=" + queryURL);
            URL url = new URL(queryURL);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(is, "UTF-8");

            String tag;
            StringBuffer titleBuf = new StringBuffer();
            StringBuffer firstimageBuf = new StringBuffer();
            StringBuffer mapxBuf = new StringBuffer();
            StringBuffer mapyBuf = new StringBuffer();
            StringBuffer cat2Buf = new StringBuffer();
            StringBuffer addrBuf = new StringBuffer();
            StringBuffer contenttypeidBuf = new StringBuffer();

            title.clear();
            firstimage.clear();
            mapx.clear();
            mapy.clear();
            cat2.clear();
            addr.clear();
            contentTypeId.clear();

            xpp.next();
            int eventType = xpp.getEventType();
//            System.out.println("while 분기 시작  " + eventType);

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
//                        System.out.println("while 분기 시작  " + eventType);
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item"));
                        else if (tag.equals("addr1")) {
                            xpp.next();
                            addrBuf.setLength(0);
                            addrBuf.append(xpp.getText());
                            addr.add(0, addrBuf.toString());
//                            System.out.println("addr = " + addr.get(0));
                        }
                        else if (tag.equals("cat2")) {
                            xpp.next();
                            cat2Buf.setLength(0);
                            cat2Buf.append(xpp.getText());
                            cat2.add(0, cat2Buf.toString());
                            System.out.println("cat2 = " + cat2.get(0));
                        }
                        else if (tag.equals("contentid")) {
                            xpp.next();
                            contenttypeidBuf.setLength(0);
                            contenttypeidBuf.append(xpp.getText());
                            contentTypeId.add(0, contenttypeidBuf.toString());
                            System.out.println("contentTypeId = " + contentTypeId.get(0));
                        }
                        else if (tag.equals("firstimage")) {
                            xpp.next();
                            firstimageBuf.setLength(0);
                            firstimageBuf.append(xpp.getText());
                            firstimage.add(0, firstimageBuf.toString());
//                            System.out.println("firstimage =" + firstimage.get(0));
                        }
                        else if (tag.equals("mapx")) {
                            xpp.next();
                            mapxBuf.setLength(0);
                            mapxBuf.append(xpp.getText());
                            mapx.add(0, mapxBuf.toString());
//                            System.out.println("mapx =" + mapx.get(0));
                        }
                        else if (tag.equals("mapy")) {
                            xpp.next();
                            mapyBuf.setLength(0);
                            mapyBuf.append(xpp.getText());
                            mapy.add(0, mapyBuf.toString());
//                            System.out.println("mapy =" + mapy.get(0));
                        }
                        else if (tag.equals("title")) {
                            xpp.next();
                            titleBuf.setLength(0);
                            titleBuf.append(xpp.getText());
                            title.add(0, titleBuf.toString());
//                            System.out.println("title =" + title.get(0));
                        }
                        else {}
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        tag= xpp.getName();
                        break;
                }
                eventType = xpp.next();

            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



}







