package com.techlabsystems.pruebacharpy;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import static com.techlabsystems.pruebacharpy.CFuncEstadisticas.desviacion;
import static com.techlabsystems.pruebacharpy.CFuncEstadisticas.media;



/**
 * Created by juanjo on 18/10/2017.
 */


public class CalculosEstadisticos {



    private static float vmedioEnergiaImpacto, stdEnergiaImpacto, vmedioResilenciaImpacto, stdResilenciaImpacto;

    private static CalculosEstadisticos mCalculosEstadisticos;
    static protected Aplicacion mApplication;
    private static Context mContext;




    public static CalculosEstadisticos newInstance(Context context) {
        if (mCalculosEstadisticos == null) {
            mCalculosEstadisticos = new CalculosEstadisticos(context);
        }

        return mCalculosEstadisticos;
    }

    private CalculosEstadisticos(Context context) {

        mContext = context.getApplicationContext();


        //--- Conexion con el equipo
        mApplication = (Aplicacion) (Aplicacion.getInstance());


    }
    public static float getVmedioEnergiaImpacto(List<Probeta> Probetas) {

        EstadisticaEnergiaImpacto(Probetas);
        return vmedioEnergiaImpacto;
    }

    public static float getStdEnergiaImpacto(List<Probeta> Probetas) {

        EstadisticaEnergiaImpacto(Probetas);
        return stdEnergiaImpacto;
    }

    public static float getVmedioResilenciaImpacto(List<Probeta> Probetas) {
        EstadisticaResilenciaImpacto(Probetas);
        return vmedioResilenciaImpacto;
    }

    public static float getStdResilenciaImpacto(List<Probeta> Probetas) {
        EstadisticaEnergiaImpacto(Probetas);
        return stdResilenciaImpacto;
    }









    public static void EstadisticaEnergiaImpacto (List<Probeta> mProbetas){

        float[] valores = new float[mProbetas.size()];
        int i = 0;
        for (Probeta pr : mProbetas) {
            valores[i]=pr.getEnergiaImpacto();
            i++;
        }
        vmedioEnergiaImpacto = media(valores);
        stdEnergiaImpacto = desviacion(valores);

    }

    public static void EstadisticaResilenciaImpacto (List<Probeta> mProbetas){

        float[] valores = new float[mProbetas.size()];
        int i = 0;
        for (Probeta pr : mProbetas) {
            valores[i]=pr.getResilenciaImpacto();
            i++;
        }
        vmedioResilenciaImpacto = media(valores);
        stdResilenciaImpacto = desviacion(valores);

    }



}
