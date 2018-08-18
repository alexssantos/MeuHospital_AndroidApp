package seven.team.com.meuhospital;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 1001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1002;
    private static final String FINE_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float ZOOM_PADRAO = 15f;


    //variaveis
    private boolean mPermissaoLocalPermitida = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (servicoMapOK()) {
            pegarPermissaoDeLocalizacao();
            //Permissao OK -> Inicializa MAPA
            //Permissao NAO OK -> GET Permissao + Inicializar MAPA
        }
    }

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
                            moverCamera(new LatLng(localizacaoAtual.getLatitude(), localizacaoAtual.getLongitude()), ZOOM_PADRAO);

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

    private void moverCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moverCamera: movendo a camera para: Lat: " + latLng.latitude + ", Long: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
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
                pegarLocalizacaoUsuario();

                //Marcar localização Atual Usuario
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                
            }
        });
    }


}
