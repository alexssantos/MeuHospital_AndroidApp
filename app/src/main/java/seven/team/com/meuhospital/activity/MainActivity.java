package seven.team.com.meuhospital.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import seven.team.com.meuhospital.R;
import seven.team.com.meuhospital.adapter.PlaceAutocompleteAdapter;
import seven.team.com.meuhospital.model.PlaceInfo;

public class MainActivity extends AppCompatActivity
                            implements OnMapReadyCallback,
                                        GoogleApiClient.OnConnectionFailedListener{

    //  INTERFACE METHODS

    //TAG_Activity
    private static final String TAG = "TAG MainActivity";

    //Constants
    private static final int ERROR_DIALOG_REQUEST = 1001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1002;
    private static final String FINE_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float ZOOM_PADRAO = 15f;
    private static final float INCLINACAO_ANGULO_PADRAO = 45f;
    private static final float ROTACAO_PADRAO = 0f;
    private static final LatLng LOCAL_PADRAO_RJ = new LatLng(-22.9088363,-43.1927289);
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-42,157), new LatLng(-40,120));



    //Var
    private boolean mPermissaoLocalPermitida = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mAutocompleteAdapter;
    private AutoCompleteTextView autoCompleteText;
    private GeoDataClient mGeoDataClient;
    private PlaceInfo mPlaceInfo;
    private GoogleApiClient mGoogleApiClient;

    //Widgets
    private BottomNavigationItemView btnListAllHospitais, btnListByTags, btnListByCloser;
    private FloatingActionButton btnEmergenceCall;
    private AutoCompleteTextView mSearchText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find ID
        btnListAllHospitais = findViewById(R.id.btnListByHospitais);
        btnListByTags =     findViewById(R.id.btnListByTag);
        btnListByCloser=    findViewById(R.id.btnListByCloser);
        btnEmergenceCall =  findViewById(R.id.btnEmergenceCall);
        mSearchText =       findViewById(R.id.imputSearch);



        //Buttons
        btnListAllHospitais.setOnClickListener(listHospitaisActivity);
        btnListByTags.setOnClickListener(listHospitaisActivity);
        btnListByCloser.setOnClickListener(listHospitaisActivity);
        btnEmergenceCall.setOnClickListener(callPhone);


        if (servicoMapOK()) {
            pegarPermissaoDeLocalizacao();
            //Permissao OK -> Inicializa MAPA
            //Permissao NAO OK -> GET Permissao + Inicializar

        }

    }

    public View.OnClickListener callPhone = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:192"));


            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                                                    new String[]{Manifest.permission.CALL_PHONE},
                                                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

            } else {
                try {
                    startActivity(i);
                } catch(SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private View.OnClickListener listHospitaisActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case (R.id.btnListByHospitais):
                    //TODO: Logica de pegar a busca desse metodo e add na Intent
                    Intent intentListByHospitais = new Intent(getApplicationContext(), HospitalsListActivity.class);
                    startActivity(intentListByHospitais);
                    break;
                case (R.id.btnListByTag):
                    //TODO: Logica de pegar a busca desse metodo e add na Intent
                    Intent intentListByTag = new Intent(getApplicationContext(), HospitalsListActivity.class);
                    startActivity(intentListByTag);
                    break;
                case (R.id.btnListByCloser):
                    //TODO: Logica de pegar a busca desse metodo e add na Intent
                    Intent intentListByCloser = new Intent(getApplicationContext(), HospitalsListActivity.class);
                    startActivity(intentListByCloser);
                    break;
            }
        }
    };

    private void init(){
        Log.d(TAG, "init: initializing");

        // Construct a GeoDataClient for the Google Places API for Android.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        // AutoComplete
        mAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, LAT_LNG_BOUNDS, null );
        // Set Adapter
        mSearchText.setAdapter(mAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER){

                    //executar o metodo searching
                    geoLocate();
                }
                return false;
            }
        });

        //Esconder Teclado.
        hideSoftKeyboard();

        // GET DEVICE LOCATON
        //Todo Here:
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();

            //get Place ID
            final AutocompletePrediction item = mAutocompleteAdapter.getItem(position);
            final String placedId = item.getPlaceId();

            //Request
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placedId);
            placeResult.setResultCallback(updatePlaceDetailsCallback);

            // message
            Toast.makeText(getApplicationContext(), "Clicked: " + placedId,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placedId);
        }
    };

    private ResultCallback<PlaceBuffer> updatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (places.getStatus().isSuccess() && places.get(0).getName() != null){
                Log.d(TAG, "updatePlaceDetailsCallback: Place query did not complete successfully: " + places.get(0).getName().toString());
                try {
                    Place place = places.get(0);

                    // Get the Place object from the buffer.
                    mPlaceInfo  = new PlaceInfo();
                    mPlaceInfo.setName(place.getName().toString());
                    mPlaceInfo.setAddress(place.getAddress().toString());
                    mPlaceInfo.setId(place.getId());
                    mPlaceInfo.setPhoneNumber(place.getPhoneNumber().toString());
                    mPlaceInfo.setLatlng(place.getLatLng());


                    moverCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude),
                            ZOOM_PADRAO,
                            INCLINACAO_ANGULO_PADRAO,
                            mPlaceInfo.getName());



                    // Format details of the place for display and show it in a TextView.
                    Log.i(TAG, "Place details received: " + mPlaceInfo.toString());

                    places.release();
                } catch (RuntimeRemoteException e) {
                    Log.e(TAG, "updatePlaceDetailsCallback: Place query did not complete.", e);
                    return;
                } catch (NullPointerException e) {
                    Log.e(TAG, "updatePlaceDetailsCallback: Place query NULL.", e);
                    return;
                }
            }
        }
    };


    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSearchText.setText("");
        
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void geoLocate() {
        Log.d(TAG, "GeoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocationName(searchString, 1);
        }
        catch (IOException e){
            Log.e(TAG, "GeoLocation: IOException");
        }

        if (addressList.size() > 0){
            Address address = addressList.get(0);

            Log.d(TAG, "geoLocatoin: Localização encontrada: " + address.toString());

            moverCamera(new LatLng(address.getLatitude(), address.getLongitude()), ZOOM_PADRAO, INCLINACAO_ANGULO_PADRAO, address.getAddressLine(0));
        }

    }

    private void getuserLocation() {
        Log.d(TAG, "pegarLocalizacaoUsuario: pegando a atual localização do usuario");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mPermissaoLocalPermitida) {
                //ultimalocalizacao
                final Task localizacao = mFusedLocationProviderClient.getLastLocation();
                localizacao.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            //task OK -> Pegar o resultado da localização + Posicionar Camera
                            Log.d(TAG, "onComplete: localização encontrada");

                            Location localizacaoAtual = (Location) task.getResult();
                            moverCamera(new LatLng(localizacaoAtual.getLatitude(), localizacaoAtual.getLongitude()), ZOOM_PADRAO,INCLINACAO_ANGULO_PADRAO, "My Location");

                        } else {
                            Log.d(TAG, "onComplete: localização atual está Null");
                            Toast.makeText(getApplicationContext(), "Localização atual não acessivel", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }
        catch (SecurityException e) {
            Log.d(TAG, "pegarLocalizacaoUsuario: SecurityException: " + e.getMessage());
        }
    }

    //region Camera Config Parameters
    /*  Moves halfway along an arc between straight
        OVERHEAD (0 degrees) and the
        GROUND (90 degrees), to position
    endregion */
    //endregion
    private void moverCamera(LatLng latLng, float zoom, float inclinacao, String title) {
        Log.d(TAG, "moverCamera: movendo a camera para: Lat: " + latLng.latitude + ", Long: " + latLng.longitude);

        //      Position( LatLong latLong, zoom, rotação, inclinação)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition( new CameraPosition(latLng ,zoom, inclinacao, ROTACAO_PADRAO)));

        Marcadores();
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(markerOptions);

        hideSoftKeyboard();
    }

    private void MarcadoresNaUnha (){
        Log.d(TAG, "MarcadorNaUnha: Adding markers");

        //Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_add_location);

        Marker m1 = mMap.addMarker(
                new MarkerOptions()
                .position(new LatLng(-22.9095577,-43.1915863))
                .title("INCA"));

//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location)));
                //.icon(BitmapDescriptorFactory.fromBitmap( bitmap)));

        Marker m2 = mMap.addMarker(
                new MarkerOptions()
                .position(new LatLng(-22.9091885,-43.1887256))
                .title("Policlinica Geral do Rio de Janeiro"));

        Marker m3 = mMap.addMarker(
                new MarkerOptions()
                .position(new LatLng(-22.9146178,-43.2002586))
                .title("HospitalModel Central da Policia Militar"));

    }

    private void Marcadores (){

        MarkerOptions options = new MarkerOptions();
        ArrayList<LatLng> latlngs = new ArrayList<>();

        //You can add to the list of latlngs by,
        latlngs.add(new LatLng(-22.9095577,-43.1915863));
        latlngs.add(new LatLng(-22.9091885,-43.1887256));
        latlngs.add(new LatLng(-22.9146178,-43.2002586));


        //And then, use for loop to set them on the map.
        for (LatLng point : latlngs) {
            options.position(point);
            options.title("someTitle");
            mMap.addMarker(options);
        }
    }

    //Verficação da conexaxao do Google Services
    public boolean servicoMapOK() {
        Log.d(TAG, "requestServiceMapOK: checking google services version");
        int disponivel = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (disponivel == ConnectionResult.SUCCESS) {
            //TUDO OK
            Log.d(TAG, "requestServiceMapOK: Google Play Services está funcionando");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(disponivel)) {
            //Tem um erro porem é consertavel.
            Log.d(TAG, "requestServiceMapOK: Erro na requisição de Mapa ao Google Services (pode ser consertado)");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, disponivel, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Mapa não Autorizado. \n Você não pode fazer uma requisição de Mapa", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void pegarPermissaoDeLocalizacao() {
        Log.d(TAG, "pegarPermissaoDeLocalizacao: verificar permissoes de localização");
        String[] permissoes = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        //Verificar Permissoes de Localização
        if (ContextCompat.checkSelfPermission(getApplicationContext(), FINE_PERMISSION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), COARSE_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "pegarPermissaoLocalizacao: Permissoes Concedidas.");
                mPermissaoLocalPermitida = true;
                inicializarMapa();
            } else {
                //Pedir Permissoes
                ActivityCompat.requestPermissions(this, permissoes, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            //Pedir Permissoes
            ActivityCompat.requestPermissions(this, permissoes, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissaoLocalPermitida = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                //se alguma permissao foi dada
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mPermissaoLocalPermitida = false;
                            Log.d(TAG, "onRequestPermissionResult: permissao negada.");
                            return;
                        }
                    }
                }
                Log.d(TAG, "onRequestPermissionResult: permissao consedida.");
                mPermissaoLocalPermitida = true;
                //Permissoes OK - inicializar o mapa
                inicializarMapa();
                break;

            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the phone call

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    private void inicializarMapa() {
        Log.d(TAG, "inicializarMap: inicializando mapa");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        //Configurar e inicializar o mapa
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Toast.makeText(getApplicationContext(), "Permissao Garantida. \n   Mapa Pronto!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onMapReady: mapa está pronto");
                mMap = googleMap;

                //Pegar Localização Atual do Usuario
//                pegarLocalizacaoUsuario();

                //Iniciar Mapa no RJ - Centro - Hosp. Souza Aguiar
//                moverCamera(LatLong Local , float Zoom);
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        LOCAL_PADRAO_RJ,
                        ZOOM_PADRAO,
                        INCLINACAO_ANGULO_PADRAO,
                        ROTACAO_PADRAO)));

                //Marcar localização Atual Usuario
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);

                MarcadoresNaUnha();

                init();

            }
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
