package com.techlabsystems.pruebacharpy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.techlabsystems.drivers.DriverCharpyArduino;

import java.util.ArrayList;

/**
 * Created by juanjo on 16/10/2017.
 */

public class CFuncionesEnsayosCharpy {

    private static CFuncionesEnsayosCharpy mCFuncionesEnsayosCharpy;
    private static Context mContext;

    static protected Aplicacion mApplication;
    static DriverCharpyArduino conexionPlaca;


    public static CFuncionesEnsayosCharpy newInstance(Context context) {
        if (mCFuncionesEnsayosCharpy == null) {
            mCFuncionesEnsayosCharpy = new CFuncionesEnsayosCharpy(context);
        }

        return mCFuncionesEnsayosCharpy;
    }

    private CFuncionesEnsayosCharpy(Context context) {

        mContext = context.getApplicationContext();


        //--- Conexion con el equipo
        mApplication = (Aplicacion) (Aplicacion.getInstance());


        if (mApplication.GetSerialPort() == null) {
            mApplication.openSerialPort();

        }
        if (mApplication.GetSerialPort() != null) {
            conexionPlaca = mApplication.getDriver();
        }


    }


    public static class CalPerdidasAsyncTask extends AsyncTask<Integer, Void, Void> {

        final int identifier;


        CalPerdidasAsyncTask(final int identifier) {
            this.identifier = identifier;
            conexionPlaca.doSetPerdidasMazaConectada(0); //poner a cero las perdidas

        }


        protected void onPreExecute() {


        }

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                conexionPlaca.doEnsayo();
                Log.i("CalPerdidasAsyncTask", "Job task#" + identifier + " has started");
                Thread.sleep(params[0]);
                Log.i("CalPerdidasAsyncTask", "Job task#" + identifier + " has finished");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            String menssage;


            mApplication.vgram.angulo_maximo_pendulo = conexionPlaca.doGetAnguloMaxEnsayo();
            mApplication.vg.angCaidaInicial = conexionPlaca.doGetAnguloDisparo();

            double ePerdidas_sof = mApplication.calculosPendulo.CalEnergiaPerdidas();
            double ePerdidas = conexionPlaca.doGetEnergiaPerdidas();
            mApplication.vgram.EPerdidas = ePerdidas_sof;
            mApplication.vgram.velocidadImpacto_sof = mApplication.calculosPendulo.CalvelocidadImpacto();




        }
    }

    public static class CalEnergiaAsyncTask extends AsyncTask<Integer, Void, Void> {

        final int identifier;


        CalEnergiaAsyncTask(final int identifier) {
            this.identifier = identifier;
        }


        protected void onPreExecute() {


        }

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                conexionPlaca.doEnsayo();
                Log.i("CalEnergiaAsyncTask", "Job task#" + identifier + " has started");
                Thread.sleep(params[0]);
                Log.i("CalEnergiaAsyncTask", "Job task#" + identifier + " has finished");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            String menssage;


            //leo estos datos de la placa porque son necesarios para los calculos.
            mApplication.vgram.angulo_maximo_pendulo = conexionPlaca.doGetAnguloMaxEnsayo();
            mApplication.vg.angCaidaInicial = conexionPlaca.doGetAnguloDisparo();
            mApplication.vgram.EImpacto = mApplication.calculosPendulo.CalEnergiaImpacto();

        }
    }
}
