package com.techlabsystems.pruebacharpy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.techlabsystems.drivers.DriverCharpyArduino;

/**
 * Created by juanjo on 26/09/2017.
 */


public class SelectMazaFragment extends Fragment {
    private static final String TAG = SelectMazaFragment.class.getSimpleName();
    private static final String ARG_TIPO_ENSAYO = "ARG_TIPO_ENSAYO";
    public final static String ARG_FUNCION_ENSAYO ="ARG_FUNCION_ENSAYO"; //Ensayo, calibracion, otra
    public static final String EXTRA_TIPO_ENSAYO = "EXTRA_TIPO_ENSAYO";
    public static final String EXTRA_ESCALA_MAZA = "EXTRA_ESCALA_MAZA";
    public static final String EXTRA_FUNCION_ENSAYO = "EXTRA_FUNCION_ENSAYO";



    //Escalas
    public static int R1 = 0;
    public static int R2 = 1;
    public static int R3 = 2;
    public static int R4 = 3;
    public static int R5 = 4;
    public static int R6 = 5;
    public static int R7 = 6;
    public static int R8 = 7;


    protected Aplicacion mApplication;
    DriverCharpyArduino conexionPlaca;

    Integer mTipoEnsayo, mFuncionRealizar;

    public static SelectMazaFragment newInstance(Integer FuncionRealizar,Integer TipoEnsayo) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FUNCION_ENSAYO, FuncionRealizar);
        args.putSerializable(ARG_TIPO_ENSAYO, TipoEnsayo);
        SelectMazaFragment fragment = new SelectMazaFragment();
        fragment.setArguments(args);
        return fragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFuncionRealizar = (Integer)getArguments().getSerializable(ARG_FUNCION_ENSAYO);
        mTipoEnsayo = (Integer)getArguments().getSerializable(ARG_TIPO_ENSAYO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_select_maza, container, false);

        //--- Conexion con el equipo
        mApplication = (Aplicacion) (getActivity().getApplication());

        if (mApplication.GetSerialPort() == null) {
            mApplication.openSerialPort();

        }
        if (mApplication.GetSerialPort() != null) {
            conexionPlaca  = mApplication.getDriver();
            String strmaza_conectada = conexionPlaca.doGetDatosMaza();


        }

        Button fragment_select_maza_button1 = (Button) view.findViewById(R.id.fragment_select_maza_button1);
        Button fragment_select_maza_button2 = (Button) view.findViewById(R.id.fragment_select_maza_button2);
        Button fragment_select_maza_button3 = (Button) view.findViewById(R.id.fragment_select_maza_button3);
        Button fragment_select_maza_button4 = (Button) view.findViewById(R.id.fragment_select_maza_button4);
        Button fragment_select_maza_button5 = (Button) view.findViewById(R.id.fragment_select_maza_button5);
        Button fragment_select_maza_button6 = (Button) view.findViewById(R.id.fragment_select_maza_button6);
        Button fragment_select_maza_button7 = (Button) view.findViewById(R.id.fragment_select_maza_button7);
        Button fragment_select_maza_button8 = (Button) view.findViewById(R.id.fragment_select_maza_button8);
        Button fragment_select_maza_button9 = (Button) view.findViewById(R.id.fragment_select_maza_button9);

        if (mTipoEnsayo == CListaMazas.ISO_CHAR){
            fragment_select_maza_button1.setVisibility(View.VISIBLE);
            fragment_select_maza_button2.setVisibility(View.VISIBLE);
            fragment_select_maza_button3.setVisibility(View.VISIBLE);
            fragment_select_maza_button4.setVisibility(View.VISIBLE);
            fragment_select_maza_button5.setVisibility(View.VISIBLE);
            fragment_select_maza_button6.setVisibility(View.VISIBLE);
            fragment_select_maza_button7.setVisibility(View.VISIBLE);
            fragment_select_maza_button8.setVisibility(View.VISIBLE);
            fragment_select_maza_button9.setVisibility(View.INVISIBLE);

            fragment_select_maza_button1.setText("R1 - 0.5 J");
            fragment_select_maza_button2.setText("R2 - 1   J");
            fragment_select_maza_button3.setText("R3 - 2   J");
            fragment_select_maza_button4.setText("R4 - 4   J");
            fragment_select_maza_button5.setText("R5 - 5   J");
            fragment_select_maza_button6.setText("R6 - 7.5 J");
            fragment_select_maza_button7.setText("R7 - 15  J");
            fragment_select_maza_button8.setText("R8 - 25  J");


        }

        if (mTipoEnsayo == CListaMazas.ASTM_CHAR){
            fragment_select_maza_button1.setVisibility(View.VISIBLE);
            fragment_select_maza_button2.setVisibility(View.VISIBLE);
            fragment_select_maza_button3.setVisibility(View.VISIBLE);
            fragment_select_maza_button4.setVisibility(View.VISIBLE);
            fragment_select_maza_button5.setVisibility(View.INVISIBLE);
            fragment_select_maza_button6.setVisibility(View.INVISIBLE);
            fragment_select_maza_button7.setVisibility(View.INVISIBLE);
            fragment_select_maza_button8.setVisibility(View.INVISIBLE);
            fragment_select_maza_button9.setVisibility(View.INVISIBLE);

            fragment_select_maza_button1.setText("R1 - 2.75  J");
            fragment_select_maza_button2.setText("R2 - 5.45  J");
            fragment_select_maza_button3.setText("R3 - 10.84 J");
            fragment_select_maza_button4.setText("R4 - 21.68 J");
            fragment_select_maza_button5.setText("");
            fragment_select_maza_button6.setText("");
            fragment_select_maza_button7.setText("");
            fragment_select_maza_button8.setText("");

        }

        if (mTipoEnsayo == CListaMazas.IZOD_CHAR){
            fragment_select_maza_button1.setVisibility(View.VISIBLE);
            fragment_select_maza_button2.setVisibility(View.VISIBLE);
            fragment_select_maza_button3.setVisibility(View.VISIBLE);
            fragment_select_maza_button4.setVisibility(View.VISIBLE);
            fragment_select_maza_button5.setVisibility(View.VISIBLE);
            fragment_select_maza_button6.setVisibility(View.INVISIBLE);
            fragment_select_maza_button7.setVisibility(View.INVISIBLE);
            fragment_select_maza_button8.setVisibility(View.INVISIBLE);
            fragment_select_maza_button9.setVisibility(View.INVISIBLE);

            fragment_select_maza_button1.setText("R1 - 1    J");
            fragment_select_maza_button2.setText("R2 - 2.75 J");
            fragment_select_maza_button3.setText("R3 - 5.5  J");
            fragment_select_maza_button4.setText("R4 - 11   J");
            fragment_select_maza_button5.setText("R5 - 22   J");
            fragment_select_maza_button6.setText("");
            fragment_select_maza_button7.setText("");
            fragment_select_maza_button8.setText("");


        }

        CharSequence str_ensayo_seleccionado = "" ;

        if (mFuncionRealizar == 1) {
            if (mTipoEnsayo == CListaMazas.ISO_CHAR) {
                str_ensayo_seleccionado = getResources().getString(R.string.ensayo_ISO);
            } else if (mTipoEnsayo == CListaMazas.ASTM_CHAR) {
                str_ensayo_seleccionado = getResources().getString(R.string.ensayo_ASTM);
            } else if (mTipoEnsayo == CListaMazas.IZOD_CHAR) {
                str_ensayo_seleccionado = getResources().getString(R.string.ensayo_IZOD);
            }
        }

        if (mFuncionRealizar == 2) {
            if (mTipoEnsayo == CListaMazas.ISO_CHAR) {
                str_ensayo_seleccionado = getResources().getString(R.string.calibracion_ISO);
            } else if (mTipoEnsayo == CListaMazas.ASTM_CHAR) {
                str_ensayo_seleccionado = getResources().getString(R.string.calibracion_ASTM);
            } else if (mTipoEnsayo == CListaMazas.IZOD_CHAR) {
                str_ensayo_seleccionado = getResources().getString(R.string.calibracion_IZOD);
            }
        }

        getActivity().setTitle(str_ensayo_seleccionado);

        fragment_select_maza_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = null;
                conexionPlaca.SeleccionarTipoEnsayo(mTipoEnsayo,R1);

                mApplication.vg.FuncionRealizar = mFuncionRealizar;
                mApplication.vg.TipoEnsayo = mTipoEnsayo;
                mApplication.vg.EscalaMaza = R1;
                ShowActivityFunction();

            }
        });

        fragment_select_maza_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                conexionPlaca.SeleccionarTipoEnsayo(mTipoEnsayo,R2);

                mApplication.vg.FuncionRealizar = mFuncionRealizar;
                mApplication.vg.TipoEnsayo = mTipoEnsayo;
                mApplication.vg.EscalaMaza = R2;
                ShowActivityFunction();

            }
        });

        fragment_select_maza_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conexionPlaca.SeleccionarTipoEnsayo(mTipoEnsayo,R3);

                mApplication.vg.FuncionRealizar = mFuncionRealizar;
                mApplication.vg.TipoEnsayo = mTipoEnsayo;
                mApplication.vg.EscalaMaza = R3;
                ShowActivityFunction();

            }
        });

        fragment_select_maza_button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conexionPlaca.SeleccionarTipoEnsayo(mTipoEnsayo,R4);

                mApplication.vg.FuncionRealizar = mFuncionRealizar;
                mApplication.vg.TipoEnsayo = mTipoEnsayo;
                mApplication.vg.EscalaMaza = R4;
                ShowActivityFunction();

            }
        });

        fragment_select_maza_button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conexionPlaca.SeleccionarTipoEnsayo(mTipoEnsayo,R5);

                mApplication.vg.FuncionRealizar = mFuncionRealizar;
                mApplication.vg.TipoEnsayo = mTipoEnsayo;
                mApplication.vg.EscalaMaza = R5;

                ShowActivityFunction();

            }
        });

        fragment_select_maza_button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conexionPlaca.SeleccionarTipoEnsayo(mTipoEnsayo,R6);

                mApplication.vg.FuncionRealizar = mFuncionRealizar;
                mApplication.vg.TipoEnsayo = mTipoEnsayo;
                mApplication.vg.EscalaMaza = R6;

                ShowActivityFunction();

            }
        });


        fragment_select_maza_button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conexionPlaca.SeleccionarTipoEnsayo(mTipoEnsayo,R7);

                mApplication.vg.FuncionRealizar = mFuncionRealizar;
                mApplication.vg.TipoEnsayo = mTipoEnsayo;
                mApplication.vg.EscalaMaza = R7;

                ShowActivityFunction();

            }
        });


        fragment_select_maza_button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conexionPlaca.SeleccionarTipoEnsayo(mTipoEnsayo,R8);

                mApplication.vg.FuncionRealizar = mFuncionRealizar;
                mApplication.vg.TipoEnsayo = mTipoEnsayo;
                mApplication.vg.EscalaMaza = R8;

                ShowActivityFunction();

            }
        });


        return view;
    }

    //Mostrar la actividad de ensayo o Calibración
    private void ShowActivityFunction()
    {
        Intent intent = null;
        if (mFuncionRealizar == 1) {
            intent = new Intent(getActivity(), EnsayoActivity.class);
        }
        if (mFuncionRealizar == 2) {
            conexionPlaca.SeleccionarTipoEnsayo(mApplication.vg.TipoEnsayo,mApplication.vg.EscalaMaza);
            intent = new Intent(getActivity(), CalibracionActivity.class);
        }
        intent.putExtra(EXTRA_FUNCION_ENSAYO,mApplication.vg.FuncionRealizar);
        intent.putExtra(EXTRA_TIPO_ENSAYO, mApplication.vg.TipoEnsayo);
        intent.putExtra(EXTRA_ESCALA_MAZA,mApplication.vg.EscalaMaza );
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mApplication.GetSerialPort() == null) {
            mApplication.openSerialPort();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



}