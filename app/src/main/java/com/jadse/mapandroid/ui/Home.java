package com.jadse.mapandroid.ui;

import static java.util.stream.Collectors.toList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.jadse.mapandroid.R;
import com.jadse.mapandroid.dao.LugarDAO;
import com.jadse.mapandroid.databinding.FragmentHomeBinding;
import com.jadse.mapandroid.model.Lugar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Home extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener {

    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;

    FragmentHomeBinding binding;
    Context context;
    View view;
    NavController navController;
    GoogleMap googleMap;
    MarkerOptions markerOptions01;
    String apikey;

    List<Lugar> lugares = new ArrayList<Lugar>();
    Dialog dialog;

    TextView textViewTitulo, textViewDescripcion, textViewLongitud, textViewLatitud;
    ImageView imageViewImagen, imageViewCerrar;

    Spinner spinnerLugares;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*try {
            apikey = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA
            ).metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }*/
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);

        dialog = new Dialog(context);
        Objects.requireNonNull( dialog.getWindow() ).setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.setContentView(R.layout.dlg_lugar);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        imageViewImagen = dialog.findViewById( R.id.ivImagen);
        textViewTitulo = dialog.findViewById( R.id.tvTitulo );
        textViewDescripcion = dialog.findViewById( R.id.tvDescripcion );
        textViewLongitud = dialog.findViewById( R.id.tvLongitud );
        textViewLatitud = dialog.findViewById( R.id.tvLatitud );
        imageViewCerrar = dialog.findViewById(R.id.ivCerrar);
        spinnerLugares = view.findViewById(R.id.spLugares);
        imageViewCerrar.setOnClickListener( v-> dialog.dismiss() );

        // Mapa
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fcvMap);
        if ( supportMapFragment != null )
            supportMapFragment.getMapAsync(this);

        if (lugares.isEmpty()) {
            Lugar destino1 = new Lugar(1, R.drawable.jpg_huacachina, "Huacachina", "Huacachina es un oasis desértico y una pequeña villa justo al oeste de la ciudad de Ica, en el suroeste de Perú.",  -14.0875, -75.763333333333);
            Lugar destino2 = new Lugar(2, R.drawable.webp_playa_de_la_mina, "Playa La Mina Pisco", " Llegada a Playa La Mina, un lugar precioso de agua cristalina y limpia, podrás disfrutar de esta linda playa junto a tus amigos y familiares.",  -13.910546, -76.317833);
            Lugar destino3 = new Lugar(3, R.drawable.jpg_playa_roja, "Playa Roja", "Desierto con playa roja por los minerales volcánicos, fósiles marinos y avistamiento de focas y pingüinos.", -13.892518, -76.301654);
            Lugar destino4 = new Lugar(3, R.drawable.jpg_paracas_e_islas_ballestas, "Paracas Paseos", "Este es el clásico muelle de pesca y embarque de turistas hacia las islas ballestas.", -13.832764, -76.248503);
            Lugar destino5 = new Lugar(3, R.drawable.jpg_desierto_ica, "Desierto de Ica", "El desierto de Ica es una región peruana con un clima cálido y seco, dunas, oasis y pampas. Se encuentra a unos 300 kilómetros al sur de Lima. ", -14.095757, -75.771284);
            lugares.add(destino1);
            lugares.add(destino2);
            lugares.add(destino3);
            lugares.add(destino4);
            lugares.add(destino5);
        }

        //Adaptador y evento de selección de spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                lugares.stream().map(Lugar::getTítulo).collect(Collectors.toList()));
        spinnerLugares.setAdapter(adapter);

        spinnerLugares.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Lugar lugarSeleccionado = lugares.get(position);
                moverCamara(lugarSeleccionado);
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void moverCamara(Lugar lugarSeleccionado) {
        LatLng gps = new LatLng(lugarSeleccionado.getLatitud(), lugarSeleccionado.getLongitud());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 15));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if ( ActivityCompat.checkSelfPermission( context, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_DENIED )
            if ( !ActivityCompat.shouldShowRequestPermissionRationale( requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) )
                ActivityCompat.requestPermissions( requireActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_PERMISSION_ACCESS_FINE_LOCATION );

        this.googleMap.setOnMarkerClickListener(this);
        this.googleMap.setOnMapClickListener(this);
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setOnMyLocationClickListener(this);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);

        verUbicacion();
    }

    private void verUbicacion() {
        googleMap.clear();
        /*LugarDAO lugarDAO = new LugarDAO(context);
        lugarDAO.open();
        lugares = lugarDAO.obtenerLugares();
        lugarDAO.close();*/

        LatLng gps = new LatLng(0,0);
        for ( Lugar lugar : lugares ) {
            gps = new LatLng(lugar.getLatitud(), lugar.getLongitud());
            googleMap.addMarker( new MarkerOptions().position(gps).title(lugar.getTítulo()) );
        }
        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(gps, 15) );
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
            int posicion = -1;
            for (int i = 0; i < lugares.size(); i++) {
                if (lugares.get(i).getTítulo().equals(marker.getTitle())) {
                    posicion = i;
                    break;
                }
            }

            if (posicion != -1){
                imageViewImagen.setImageResource(lugares.get(posicion).getImagen());
                textViewTitulo.setText(lugares.get(posicion).getTítulo());
                textViewDescripcion.setText(lugares.get(posicion).getDescripcion());
                textViewLatitud.setText("Latitud: " + String.valueOf(lugares.get(posicion).getLatitud()));
                textViewLongitud.setText("Longitud: " + String.valueOf(lugares.get(posicion).getLongitud()));
                dialog.show();
            }

        return false;
    }

}