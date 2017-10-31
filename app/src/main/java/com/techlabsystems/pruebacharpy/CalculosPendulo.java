package com.techlabsystems.pruebacharpy;

import android.content.Context;
import android.content.SharedPreferences;

import com.techlabsystems.drivers.DriverCharpyArduino;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;

/**
 * Created by juanjo on 04/10/2017.
 */


public class CalculosPendulo {
    private static final String TAG = DriverCharpyArduino.class.getSimpleName();

    private static Context ct;
    private static SharedPreferences sh;
    private static Aplicacion mApplication;





    private CalculosPendulo()
    {
        super();
    }

    public   static CalculosPendulo  newInstance(Aplicacion _app)
    {
        mApplication = _app;

        return new CalculosPendulo();
    }


    // Cal. de la energia en funcion de la masa y el angulo
    public  double CalEnergiaPotencialInic()
    {
        double alfa1, alfa1r;
        double Epinc, h1;

        alfa1 = abs(mApplication.vg.angCaidaInicial);
        alfa1r = alfa1 * 2.0f * PI / 360.0f;

        Epinc =  mApplication.vg.mMaza.getFMazaCal() *   mApplication.vg.mMaza.getLongPendulo() * ( 1 - cos (alfa1r));
        mApplication.vgram.Epinc = Epinc;

        return Epinc;
    }

    // Cal. de la energia en funcion de la masa y el angulo
    public double CalEnergiaMaza()
    {
        double alfa2, alfa2r;
        double h2, EnergiaMaza;

        alfa2 = abs(mApplication.vg.angCaidaInicial) ; //- fabs(vg.angCaidaInicial);

        alfa2r = alfa2 * 2 * PI / 360.0;


        h2 =  mApplication.vg.mMaza.getLongPendulo() * (1 - cos (alfa2r));
        EnergiaMaza =  mApplication.vg.mMaza.getFMazaCal() * h2;


        return EnergiaMaza;
    }
    // Cal. de la energia en TEST
    public double CalEnergia()
    {
        double alfa2, alfa2r;
        double h2, EfinalTorica, Epinc, Energia;

        alfa2 = mApplication.vgram.angulo_maximo_pendulo ; //- fabs(vg.angCaidaInicial);

        alfa2r = alfa2 * 2 * PI / 360.0;


        h2 =  mApplication.vg.mMaza.getLongPendulo() * (1 - cos (alfa2r));
        EfinalTorica =  mApplication.vg.mMaza.getFMazaCal() * h2;

        Epinc = CalEnergiaPotencialInic();

        Energia = Epinc - EfinalTorica;

        return Energia;
    }

    public double CalvelocidadImpacto(){
        double rta = Math.sqrt(mApplication.vg.mMaza.getEMazaCal() * 2.0/(mApplication.vg.mMaza.getFMazaCal()/9.81));
        return rta;
    }

    public double CalEnergiaPerdidas()
    {
        double EPerdidas;
        EPerdidas = CalEnergia();
        mApplication.vg.mMaza.setEPerdidas((float)EPerdidas);
        return EPerdidas;
    }

    public double CalEnergiaImpacto()
    {
        double alfa2, alfa2r;
        double EImpacto, EfinalTorica;
        double Epinc;
        double h2;
        double seccion;

        Epinc = CalEnergiaPotencialInic();


        alfa2 = mApplication.vgram.angulo_maximo_pendulo; //- abs(vg.angCaidaInicial);
        alfa2r = alfa2 * 2 * PI / 360.0;

        h2 = mApplication.vg.mMaza.getLongPendulo() * (1 - cos (alfa2r));
        EfinalTorica = mApplication.vg.mMaza.getFMazaCal() * h2;
        // anlizamos el caso particular para el angulo cero

        if ((mApplication.vgram.angulo_maximo_pendulo > -0.5) && (mApplication.vgram.angulo_maximo_pendulo < 0.5)) {
            EImpacto = Epinc - mApplication.vg.mMaza.getEPerdidas(); //se ha quedado atascado
        } else {
            EImpacto = Epinc - (EfinalTorica + mApplication.vg.mMaza.getEPerdidas());
        }
        mApplication.vgram.EImpacto = EImpacto;


        seccion = mApplication.vg.anchoprobeta * mApplication.vg.espesorprobeta;

        if (seccion > 0.1) {
            mApplication.vgram.EKJm2 = 1000.0 * mApplication.vgram.EImpacto / seccion;

        }
        else {
            mApplication.vgram.EKJm2 = 0.0;
        }

        return EImpacto;

    }

}
