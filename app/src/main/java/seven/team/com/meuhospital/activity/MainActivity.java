package seven.team.com.meuhospital.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import static android.Manifest.permission.CALL_PHONE;

import java.util.ArrayList;

import seven.team.com.meuhospital.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 1001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1002;
    private static final String FINE_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float ZOOM_PADRAO = 15f;
    private static final float INCLINACAO_ANGULO_PADRAO = 45f;
    private static final float ROTACAO_PADRAO = 0f;
    private static final LatLng LOCAL_PADRAO_RJ = new LatLng(-22.9088363,-43.1927289);
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;


    //variaveis
    private boolean mPermissaoLocalPermitida = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    BottomNavigationItemView btnListAllHospitais, btnListByTags, btnListByCloser;
    FloatingActionButton btnEmergenceCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (servicoMapOK()) {
            pegarPermissaoDeLocalizacao();
            //Permissao OK -> Inicializa MAPA
            //Permissao NAO OK -> GET Permissao + Inicializar MAPA
        }

        //Buttons Find ID
        btnListAllHospitais = findViewById(R.id.btnListByHospitais);
        btnListByTags = findViewById(R.id.btnListByTag);
        btnListByCloser= findViewById(R.id.btnListByCloser);
        btnEmergenceCall = findViewById(R.id.btnEmergenceCall);

        btnListAllHospitais.setOnClickListener(listHospitaisActivity);
        btnListByTags.setOnClickListener(listHospitaisActivity);
        btnListByCloser.setOnClickListener(listHospitaisActivity);
        btnEmergenceCall.setOnClickListener(callPhone);

    }

    public View.OnClickListener callPhone = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:192"));


            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

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


                    Intent intent1 = new Intent(getApplicationContext(), HospitalsListActivity.class);
                    startActivity(intent1);
                    break;
                case (R.id.btnListByTag):
                    //TODO: Logica de pegar a busca desse metodo e add na Intent



                    Intent intent2 = new Intent(getApplicationContext(), HospitalsListActivity.class);
                    startActivity(intent2);
                    break;
                case (R.id.btnListByCloser):
                    //TODO: Logica de pegar a busca desse metodo e add na Intent



                    Intent intent3 = new Intent(getApplicationContext(), HospitalsListActivity.class);
                    startActivity(intent3);
                    break;
            }
        }
    };

    private void pegarLocalizacaoUsuario() {
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
                            moverCamera(new LatLng(localizacaoAtual.getLatitude(), localizacaoAtual.getLongitude()), ZOOM_PADRAO, INCLINACAO_ANGULO_PADRAO);

                        } else {
                            Log.d(TAG, "onComplete: localização atual está Null");
                            Toast.makeText(getApplicationContext(), "Localização atual não acessivel", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.d(TAG, "pegarLocalizacaoUsuario: SecurityException: " + e.getMessage());
        }
    }

/*
        moves halfway along an arc between straight
        OVERHEAD (0 degrees) and the
        GROUND (90 degrees), to position
*/
    private void moverCamera(LatLng latLng, float zoom, float inclinacao) {
        Log.d(TAG, "moverCamera: movendo a camera para: Lat: " + latLng.latitude + ", Long: " + latLng.longitude);

        //      Position( LatLong latLong, zoom, rotação, inclinação)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition( new CameraPosition(latLng ,zoom, inclinacao, ROTACAO_PADRAO)));

        Marcadores();
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
                .title("Hospital Central da Policia Militar"));

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

//    protected Marker createMarker(double latitude, double longitude, String title, String snippet, int iconResID) {
//
//        return mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(latitude, longitude))
//                .anchor(0.5f, 0.5f)
//                .title(title)
//                .snippet(snippet)
//                .icon(BitmapDescriptorFactory.fromResource(iconResID)));
//    }


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

            }
        });
    }


}
