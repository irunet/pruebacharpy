package com.techlabsystems.pruebacharpy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.techlabsystems.drivers.DriverCharpyArduino;

import java.util.ArrayList;


/**
 * Created by juanjo on 20/09/2017.
 */

public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();
    public static final String EXTRA_FUNCION_ENSAYO ="EXTRA_FUNCION_ENSAYO";

    private Callbacks mCallbacks;

    protected Aplicacion mApplication;
    DriverCharpyArduino conexionPlaca;

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
       void onBtnSelected(int btn);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //Aplicacion global=(Aplicacion) getActivity().getApplicationContext();
        //global.xxx();


        //--- Conexion con el equipo
        mApplication = (Aplicacion) (getActivity().getApplication());

        if (mApplication.GetSerialPort() == null) {
            mApplication.openSerialPort();
            Log.i(TAG,"PUERTO ABIERTO");

        }
        if (mApplication.GetSerialPort() != null) {
            conexionPlaca = mApplication.getDriver();

        }




        if (savedInstanceState != null) {
            //mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        final Button fragment_main_btn_ensayo = (Button) view.findViewById(R.id.fragment_main_btn_ensayo);
        final Button fragment_main_btn_calibracion = (Button) view.findViewById(R.id.fragment_main_btn_calibracion);
        final Button fragment_main_btn_configuracion = (Button) view.findViewById(R.id.fragment_main_btn_configuracion);


        fragment_main_btn_ensayo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),TipoEnsayoActivity.class);
                intent.putExtra(EXTRA_FUNCION_ENSAYO,1);
                startActivity(intent);

            }
        });


        fragment_main_btn_calibracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),TipoEnsayoActivity.class);
                intent.putExtra(EXTRA_FUNCION_ENSAYO,2);
                startActivity(intent);
            }
        });

        fragment_main_btn_configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ConfigWindowActivity.class);
                intent.putExtra(EXTRA_FUNCION_ENSAYO,2);
                startActivity(intent);


            }
        });






        return view;
    }




    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mainmenu, menu);

    }

}
