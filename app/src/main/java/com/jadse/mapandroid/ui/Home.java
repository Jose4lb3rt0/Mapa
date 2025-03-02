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
import android.widget.ImageView;
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

    //Sacar las variables aqui

    List<Lugar> lugares = new ArrayList<Lugar>();
    Dialog dialog;

    TextView textViewTitulo, textViewDescripcion, textViewLongitud, textViewLatitud;
    ImageView imageViewImagen, imageViewCerrar;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fcvMap);
        if ( supportMapFragment != null ) supportMapFragment.getMapAsync(this);

        //crear el dialog aqui y setcontentview con imagen titulo descripcion DIALOG NEW dialog
        //dialog.setcontentview
        //bnombre nombre = dlg.findView(r.id.xxxxx)

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
        imageViewCerrar.setOnClickListener( v-> dialog.dismiss() );
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if ( ActivityCompat.checkSelfPermission( context, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_DENIED )
            if ( !ActivityCompat.shouldShowRequestPermissionRationale( requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) )
                ActivityCompat.requestPermissions( requireActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_PERMISSION_ACCESS_FINE_LOCATION );

        this.googleMap = googleMap;
        this.googleMap.setOnMarkerClickListener(this);
        this.googleMap.setOnMapClickListener(this);
        this.googleMap.clear();
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setOnMyLocationClickListener(this);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        verUbicacion();
    }

    private void verUbicacion() {
        Lugar Ciudad1 = new Lugar(1, 23, "Machu Picchu", "El sitio mas turístico de cusco", -13.163220, -72.545226);
        Lugar Ciudad2 = new Lugar(2, 24, "Huayna Picchu", "El sitio mas turístico de cusco", -13.156220, -72.546448);
        Lugar Ciudad3 = new Lugar(3, 25, "Camino peatonal a Machu Picchu", "El sitio mas turístico de cusco", -13.165897, -72.542732);
        lugares.add(Ciudad1);
        lugares.add(Ciudad2);
        lugares.add(Ciudad3);

        LatLng gps = new LatLng(0,0);
        for ( Lugar lugar : lugares ) {
            gps = new LatLng(lugar.getLatitud(), lugar.getLongitud());
//            googleMap.clear();
            googleMap.addMarker( new MarkerOptions().position(gps).title(lugar.getTítulo()) );
        }
        System.out.println(Arrays.deepToString(lugares.toArray()));
        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(gps, 25) );
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }
    //Clase lugar y hacer arraylist, crear 5 puntos y hacer foreach en verUbicacion, crear variable gps, lugar.getLatitude y getLngt., crear gps en 0,0
    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
//        if (marker.getTitle().equals("CFP Luis Cáceres Graziani") ) {}
//            Snackbar.make(view, marker.getTitle(), Snackbar.LENGTH_LONG).show();

            int posicion = -1;
            for (int i = 0; i < lugares.size(); i++) {
                if (lugares.get(i).getTítulo().equals(marker.getTitle())) {
                    posicion = i;
                    break;
                }
            }

            if (posicion != -1){
                textViewTitulo.setText(lugares.get(posicion).getTítulo());
                textViewDescripcion.setText(lugares.get(posicion).getDescripcion());
                dialog.show();
                textViewLatitud.setText("Latitud: " + String.valueOf(lugares.get(posicion).getLatitud()));
                textViewLongitud.setText("Longitud: " + String.valueOf(lugares.get(posicion).getLongitud()));
            }

            //marker.gettitle (buscar el nombre exacto en la lista arraylist<lugares>, encontrar position) <-----
            //tvNombrese.ttext( lugares.get(position).getNombre)
            //Detalle = lugares.get(position).getNombre
            //iv.setImageResource lugares.get(position).getIamgen
            //Dialog-show


        return false;
    }
}