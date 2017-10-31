package com.techlabsystems.drivers;

import android.content.Context;
import android.content.SharedPreferences;

import com.techlabsystems.pruebacharpy.Aplicacion;

import java.util.regex.Pattern;

/**
 * Created by juanjo on 27/09/2017.
 */

public class DriverCharpyArduino {
    private static final String TAG = DriverCharpyArduino.class.getSimpleName();

    private static Context ct;
    private static SharedPreferences sh;
    private static Aplicacion mApplication;



    private DriverCharpyArduino()
    {
        super();
    }

    public  static DriverCharpyArduino  newInstance(Aplicacion _app)
    {
        mApplication = _app;
        return new DriverCharpyArduino();
    }

    public  void doEnsayo()
    {

        mApplication.maqw("WT");
    }

    public void SeleccionarTipoEnsayo(Integer tipo_ensayo, Integer escala_ensayo){
        String comando = "WP|26|" + Integer.toString(tipo_ensayo) + "|" + Integer.toString(escala_ensayo);
            mApplication.maqw(comando);
    }




    public void doHacerCeroExtension(){
        mApplication.maqw("WE");
        mApplication.vgram.LecturaExtensionCero = 0f;
        mApplication.vgram.LecturaExtensionCero = doLeerExtension();
    }

    public float  doLeerExtension(){
        float valor;
        String comando ="RH";
        String rta = mApplication.maqwr(comando);
        try {
            valor = Float.parseFloat(rta);
        }catch (Exception e){
            valor = 9999;
        }
        valor = valor - mApplication.vgram.LecturaExtensionCero;
        return valor;
    }

    public float doLeerAnchoProbeta(){

        String comando ="RP|31";
        String rta = mApplication.maqwr(comando);
        float valor;
        try {
            valor = Float.parseFloat(rta);
        }catch (Exception e){
            valor = 9999;
        }
        mApplication.vg.anchoprobeta = valor;

        return valor;
    }

    public void doAnchoProbeta(String sesp){
        String comando = "WP|31|"+ sesp;
        mApplication.maqw(comando);
    }

    public float doLeerEspesorProbeta(){

        String comando ="RP|32";
        String rta = mApplication.maqwr(comando);
        float valor;
        try {
            valor = Float.parseFloat(rta);
        }catch (Exception e){
            valor = 9999;
        }
        mApplication.vg.espesorprobeta = valor;

        return valor;
    }

    public void doEspesorProbeta(String sesp){
        String comando = "WP|32|"+ sesp;
        mApplication.maqw(comando);
    }

    public void doSetAnguloDisparo(){
        float extension = doLeerExtension();
        String comando = "WP|21|"+ Float.toString(extension);
        mApplication.maqw(comando);
    }

    public void doSetPerdidasMazaConectada(float perdidas){

        String comando = "WP|24|"+ Float.toString(perdidas);
        mApplication.maqw(comando);
    }

    public float doGetEnergiaPerdidas(){
        float valor;
        String comando ="RP|24"; //actualiza la energia como perdidas en la maza conectada
        String rta = mApplication.maqwr(comando);
        try {
            valor = Float.parseFloat(rta);
        }catch (Exception e){
            valor = 9999;
        }
        return valor;
    }

    public float doGetEnergiaImpacto(){
        float valor;
        String comando ="RP|23";
        String rta = mApplication.maqwr(comando);
        try {
            valor = Float.parseFloat(rta);
        }catch (Exception e){
            valor = 9999;
        }
        return valor;
    }

    public float doGetAnguloDisparo(){

        String comando ="RP|21";
        String rta = mApplication.maqwr(comando);
        float valor;
        try {
            valor = Float.parseFloat(rta);
        }catch (Exception e){
            valor = 9999;
        }
       return valor;
    }


    public float doGetAnguloMaxEnsayo(){

        String comando ="RP|22";
        String rta = mApplication.maqwr(comando);
        float valor;
        try {
            valor = Float.parseFloat(rta);
        }catch (Exception e){
            valor = 9999;
        }
        mApplication.vgram.angulo_maximo_pendulo = valor;
        return valor;
    }

    public String doGetDatosMaza(){

        String comando ="RP|27";
        String rta = mApplication.maqwr(comando);
        return rta;
    }

    public String doGetDatosMazaEEPROM(Integer tipo_ensayo, Integer escala_ensayo){

        String comando ="RP|20|" +  Integer.toString(tipo_ensayo) + "|" + Integer.toString(escala_ensayo);
        String rta = mApplication.maqwr(comando);
        return rta;
    }

    public void doGetPeriodo(){
        String strIncom = mApplication.maqwr("RP|28");
        String[] tokens = strIncom.split(Pattern.quote("|"));

        if (tokens.length == 3) {
            try {
                mApplication.vgram.tciclo = Float.parseFloat(tokens[0]); // tciclo
            }catch (Exception e1){}
            try {
                mApplication.vgram.ciclos = Float.parseFloat(tokens[1]); // ciclos
            }catch (Exception e2){}
            try {
                mApplication.vgram.lp = Float.parseFloat(tokens[2]); // Long. Pendulo
            }catch (Exception e3){}
        }

    }

    public String doGetVersionKernel(){

        String comando ="RI";
        String rta = mApplication.maqwr(comando);
        return rta;
    }

    public void  doUpdateDatosMazaToRAM(Integer tipo_ensayo, Integer escala_ensayo, float FMazacal, float EMazaStd, float EMazaCal, float LongPendulo, float EPerdidas){
       /*
        String comando = "WP|27|" + Integer.toString(tipo_ensayo) + "|" + Integer.toString(escala_ensayo) + "|" + String.valueOf(FMazacal) + "|" + String.valueOf(EMazaStd)
                + "|" + String.valueOf(EMazaCal) + "|" + String.valueOf(LongPendulo) + "|" + String.valueOf(EPerdidas);
        */
        String comando = "WP|27|" + Integer.toString(tipo_ensayo) + "|" + Integer.toString(escala_ensayo) + "|" + String.format("%.3f", FMazacal) + "|" + String.format("%.3f", EMazaStd)
                + "|" + String.format("%.3f", EMazaCal) + "|" + String.format("%.3f", LongPendulo) + "|" + String.format("%.3f", EPerdidas);
        mApplication.maqw(comando);
    }

    public void  doUpdateDatosMazaToEEPROM(Integer tipo_ensayo, Integer escala_ensayo, float FMazacal, float EMazaStd, float EMazaCal, float LongPendulo, float EPerdidas){

        String comando = "WP|20|" + Integer.toString(tipo_ensayo) + "|" + Integer.toString(escala_ensayo) + "|" + String.format("%.3f", FMazacal) + "|" + String.format("%.3f", EMazaStd)
                + "|" + String.format("%.3f", EMazaCal) + "|" + String.format("%.3f", LongPendulo) + "|" + String.format("%.3f", EPerdidas);


        /*
        String comando = "WP|20|" + Integer.toString(tipo_ensayo) + "|" + Integer.toString(escala_ensayo) + "|" + String.valueOf(FMazacal) + "|" + String.valueOf(EMazaStd)
                + "|" + String.valueOf(EMazaCal) + "|" + String.valueOf(LongPendulo) + "|" + String.valueOf(EPerdidas);


         */
        mApplication.maqw(comando);
    }

    public void doSalvarConfigEEPROM(){
        mApplication.maqw("WP|0");
        //Esperar un poco
    }


    public void doBobinaON(){
        //mApplication.maqw("WI|3|1");
    }

    public void doBobinaOFF(){
        //mApplication.maqw("WI|3|0");
    }


    public void doLiberarEjependulo(){

        //mApplication.maqw("WK");
    }

    public  void doFrenarEjePendulo()
    {
       // mApplication.maqw("WI|11|0");
    }

    public  void doResetCiclos()
    {
        mApplication.maqw("WP|30");
    }



    public void SetVelocidad(float veloc)
    {


    }

    public  void doSendResultToHost(String sRta){
        mApplication.maqw("WM|"+sRta);
    }

    public  void doWI(int bit, int valor) {
        String cmd = "WI|"+Integer.toString(bit)+"|"+Integer.toString(valor);
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






}
