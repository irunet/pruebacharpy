package com.techlabsystems.pruebacharpy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Juanjo on 26/09/2017.
 */

public class TipoEnsayoFragment extends Fragment {
    private static final String TAG = TipoEnsayoFragment.class.getSimpleName();
    public final static String EXTRA_TIPO_ENSAYO ="EXTRA_TIPO_ENSAYO";
    public final static String EXTRA_FUNCION_ENSAYO ="EXTRA_FUNCION_ENSAYO";
    public final static String ARG_FUNCION_ENSAYO ="ARG_FUNCION_ENSAYO"; //Ensayo, calibracion, otra


    Integer mFuncionRealizar;



    public static TipoEnsayoFragment newInstance(Integer FuncionRealizar) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FUNCION_ENSAYO, FuncionRealizar);

        TipoEnsayoFragment fragment = new TipoEnsayoFragment();
        fragment.setArguments(args);
        return fragment;


    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFuncionRealizar = (Integer) getArguments().getSerializable(ARG_FUNCION_ENSAYO);
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tipo_ensayo, container, false);


        Button fragment_tipo_ensayo_btn_iso = (Button) view.findViewById(R.id.fragment_tipo_ensayo_btn_iso);
        Button fragment_tipo_ensayo_btn_astm = (Button) view.findViewById(R.id.fragment_tipo_ensayo_btn_astm);
        Button fragment_tipo_ensayo_btn_izod = (Button) view.findViewById(R.id.fragment_tipo_ensayo_btn_izod);


        fragment_tipo_ensayo_btn_iso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectMazaActivity.class);
                intent.putExtra(EXTRA_TIPO_ENSAYO,CListaMazas.ISO_CHAR);
                intent.putExtra(EXTRA_FUNCION_ENSAYO,mFuncionRealizar);
                startActivity(intent);
            }
        });

        fragment_tipo_ensayo_btn_astm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), SelectMazaActivity.class);
                intent.putExtra(EXTRA_TIPO_ENSAYO,CListaMazas.ASTM_CHAR);
                intent.putExtra(EXTRA_FUNCION_ENSAYO,mFuncionRealizar);
                startActivity(intent);
            }
        });

        fragment_tipo_ensayo_btn_izod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), SelectMazaActivity.class);
                intent.putExtra(EXTRA_TIPO_ENSAYO,CListaMazas.IZOD_CHAR);
                intent.putExtra(EXTRA_FUNCION_ENSAYO,mFuncionRealizar);
                startActivity(intent);
            }
        });

        return view;
    }


}
