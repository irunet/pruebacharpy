package com.techlabsystems.drivers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.app.Application;

import com.techlabsystems.pruebacharpy.Aplicacion;


/**
 * Created by juanjo on 16/05/2017.
 */

public class DriverRCM3200 {
    //Control de maquina
    private final static String TAG = DriverRCM3200.class.getSimpleName();

    private static Context ct;
    private static SharedPreferences sh;
    private static Aplicacion mApplication;



    private DriverRCM3200()
    {
        super();
    }

    public  static DriverRCM3200  newInstance(Aplicacion _app)
    {
        mApplication = _app;
        return new DriverRCM3200();
    }


    public  void doSubir()
    {
        mApplication.maqw("WF");
    }
    public  void doBajar()
    {
        mApplication.maqw("WR");
    }

    public  void doStop()
    {
        mApplication.maqw("WS");
    }

    public  void Bajar(float Velocidad)
    {
        SetVelocidad(Velocidad);
        doBajar();
    }

    public  void Subir(float Velocidad)
    {
        SetVelocidad(Velocidad);
        doSubir();
    }

    public void SetVelocidad(float veloc)
    {
        int velocidad =Math.round(veloc * 100);
        //Integer.toString(velocidad);
        String cmd = "WV"+Integer.toString(velocidad);
        mApplication.maqw(cmd);

    }

    public  void CeroExtension() {
        mApplication.maqw("WE");
    }

    public  void SetOnControlRemoto() {
        mApplication.maqw("WT1");
    }

    public  void SetOffControlRemoto() {
        mApplication.maqw("WT0");
    }


    public  void CeroFuerza() {
        mApplication.maqw("WZ");
    }






}
