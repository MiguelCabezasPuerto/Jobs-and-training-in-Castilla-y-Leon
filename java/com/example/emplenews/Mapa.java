package com.example.emplenews;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
/**
 * Clase Mapa
 *
 * Actividad que muestra las ofertas de empleo filtradas en la búsqueda sobre un mapa de Google a partir de sus coordenadas. Esta clase obedece las directrices de la API de Google en cuanto a implementación de métodos.
 *
 * @author Miguel Cabezas Puerto
 * @version 1.0
 */
public class Mapa extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener,LocationListener{

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=0;
    private static  final int MIN_TIME=10000;
    private static final int MIN_DISTANCE=1;
    /**
     * Variable necesaria para implementar los métodos de la API de Google como añadir marcadores al mapa, mover la cámara o reaccionar a la pulsación de un marcador.
     */
    private GoogleMap mMap;
    ArrayList<String>latitudes;
    ArrayList<String>longitudes;
    /**
     * Lista de ofertas a mostrar sobre el mapa
     */
    ArrayList<OfertasEmpleo> ofers;
    ContentResolver cr;
    /**
     * Listado de marcadores a situar sobre el mapa
     */
    private ArrayList<Marker>marcadores;
    /**
     * Subconjunto de las ofertas que disponen de coordenadas
     */
    private ArrayList<OfertasEmpleo>ofertasConUbicacion;
    /**
     * Permite gestionar el movimiento sobre el mapa
     */
    LocationManager locationManager;
    String provider;
    boolean permiso_ubicacion;
    /**
     * Gestiona los movimientos en el mapa
     */
    CameraUpdate cameraUpdate = null;
    /**
     * Provincia filtrada por el usuario en la búsqueda
     */
    String provincia_seleccionada;
    /**
     * Filtro libre introducido por el usuario en la búsqueda
     */
    String busquedaLibre;
    String[]selectionArgs;
    /**
     * Coordenadas de la oferta sobre el mapa, compuesto por latitud y longitud
     */
    LatLng oferta;
    double latitud,longitud;
    Marker m;


    @Override
    /**
     * Recibe parámetros de la actividad de la que se procede y calcula las coordenadas de cada oferta recibida. Inicializa la vista
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        provincia_seleccionada=getIntent().getStringExtra("selection");
        busquedaLibre=getIntent().getStringExtra("busquedalibre");

        ControladorBD managerBD=new ControladorBD();
        ControladorOfertasEmpleo managerEmpleos=new ControladorOfertasEmpleo();

        ofers=managerBD.buscarEmpleos(provincia_seleccionada,busquedaLibre,getApplicationContext());
        if(!(managerEmpleos.listaEmpleosNula(ofers)))
            ofertasConUbicacion=managerEmpleos.asignarCoordenadas(ofers);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
        marcadores=new ArrayList<>();

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else{
            mMap.setMyLocationEnabled(true);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);





    int i=0;


    ControladorOfertasEmpleo managerEmpleos=new ControladorOfertasEmpleo();
    if(managerEmpleos.listaEmpleosNula(ofertasConUbicacion)){
        return;
    }

    ControladorFormateador formateadorCadenas=new ControladorFormateador();

        for(OfertasEmpleo o:ofertasConUbicacion){
                oferta=null;
                 m=null;
                 String latitude=formateadorCadenas.limpiarCadenaExtremo(managerEmpleos.leerLatitudEmpleo(null,-1,o,1));
                 String longitude=formateadorCadenas.limpiarCadenaExtremo(managerEmpleos.leerLongitudEmpleo(null,-1,o,1));
                latitud=formateadorCadenas.deCadenaADouble(latitude);
                longitud=formateadorCadenas.deCadenaADouble(longitude);
                oferta = new LatLng(latitud, longitud);
                //String titulo=ofertasConUbicacion.get(i).getTitulo();
                m=mMap.addMarker(new MarkerOptions().position(oferta).title("+info").icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                Log.e(i+"Marcador ",""+m.getPosition());
                marcadores.add(m);
                i+=1;
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oferta,7));

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){
            @Override
            public boolean onMyLocationButtonClick()
            {
                ControladorConexionesFicheros managerConexiones=new ControladorConexionesFicheros();
                boolean ubicacion=managerConexiones.localizacionActiva(getApplicationContext());

                if(ubicacion){
                    Location loc = mMap.getMyLocation();
                    if (loc != null) {
                        LatLng latLng = new LatLng(loc.getLatitude(), loc
                                .getLongitude());
                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 7);
                        mMap.animateCamera(cameraUpdate);

                    }
                }
                else{
                    ControladorAvisos managerAvisos=new ControladorAvisos();
                    managerAvisos.avisosToast(getString(R.string.activarubicacion),getApplicationContext());
                }


                return false;
            }
        });



    }


    private void setUpMap(){

    }


    @Override
    /**
     * Cuando se pulsa sobre una oferta en el mapa llama a la clase necesaria para desplegar un cuadro de diálogo con la información de dicha oferta.
     * @param marker marcador pulsado
     */
    public void onInfoWindowClick(Marker marker) {
        ControladorOfertasEmpleo managerEmpleos=new ControladorOfertasEmpleo();
        ControladorFormateador formateadorCadenas=new ControladorFormateador();
        for(int i=0;i<marcadores.size();i++){
            if(marker.equals(marcadores.get(i))){
                String place=formateadorCadenas.concatenarCadenas(managerEmpleos.leerCiudadEmpleo(ofertasConUbicacion,i,null,2),managerEmpleos.leerProvinciaEmpleo(ofertasConUbicacion,i,null,2),"#");
                OfertaDialogFragment.nuevaInstancia(managerEmpleos.leerTituloEmpleo(ofertasConUbicacion,i,null,2),place,managerEmpleos.leerFechaEmpleo(ofertasConUbicacion,i,null,2),managerEmpleos.leerDescripcionEmpleo(ofertasConUbicacion,i,null,2),managerEmpleos.leerFuenteEmpleo(ofertasConUbicacion,i,null,2),managerEmpleos.leerEnlaceEmpleo(ofertasConUbicacion,i,null,2)).show(getSupportFragmentManager(),null);
                return;
            }
        }
    }

    @Override
    /**
     * Comprueba si el marcador pulsado se corresponde con alguno de los dibujados
     * @param marker marcador pulsado
     *  @return <ul>
     *          <li>true: se corresponde/li>
     *           <li>false: no se corresponde</li>
     *          </ul>
     */
    public boolean onMarkerClick(Marker marker) {
        ControladorOfertasEmpleo managerEmpleos=new ControladorOfertasEmpleo();
        return managerEmpleos.coincideConMarcadorEmpleo(marker,marcadores,ofertasConUbicacion,getApplicationContext());
    }


    @Override
    /**
     * Actualiza el foco de visión cuando se produce un movimiento o ampliación en el mapa
     * @param location nueva localizacion
     */
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public interface OnClickButtonListener {

        void onClickButton();
    }


    /**
     * Comprueba si se tiene la capacidad para la localización de coordenadas
     * @param requestCode codigo de peticion
     * @param permissions permisos que posee la aplicacion
     * @param grantResults resultados de la comprobacion de permisos
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mMap.setMyLocationEnabled(true);
                } else {
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }



}
