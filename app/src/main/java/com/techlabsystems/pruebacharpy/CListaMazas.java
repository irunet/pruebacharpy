package com.techlabsystems.pruebacharpy;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import com.techlabsystems.drivers.DriverCharpyArduino;
import com.techlabsystems.utilidades.Utils;

/**
 * Created by juanjo on 15/09/2017.
 */

public class CListaMazas {

    // Limites
    public static int NR_MAZAS_ISO = 8;
    public static int NR_MAZAS_ASTM = 4;
    public static int NR_MAZAS_IZOD = 5;

    private static int NR_NORMAS = 3;

    // Tipos de mazas
    public static int ISO_CHAR = 0;
    public static int ASTM_CHAR = 1;
    public static int IZOD_CHAR = 2;

    /*
    public static int R1 = 0;
    public static int R2 = 1;
    public static int R3 = 2;
    public static int R4 = 3;
    public static int R5 = 4;
    public static int R6 = 5;
    public static int R7 = 6;
    public static int R8 = 7;
*/

    private static CListaMazas mListaMazas;
    static private List<Maza> mMazas;
    private static Context mContext;

    static protected Aplicacion mApplication;
    static DriverCharpyArduino conexionPlaca;

    public static CListaMazas newInstance(Context context) {
        if (mListaMazas == null) {
            mListaMazas = new CListaMazas(context);
        }

        return mListaMazas;
    }

    private CListaMazas(Context context) {
        mMazas = new ArrayList<>();
        mContext = context.getApplicationContext();


        //--- Conexion con el equipo
        mApplication = (Aplicacion) (Aplicacion.getInstance());


        if (mApplication.GetSerialPort() == null) {
            mApplication.openSerialPort();

        }
        if (mApplication.GetSerialPort() != null) {
            conexionPlaca = mApplication.getDriver();
        }

        //mMazas = LoadListaMazasFromMaquina();
        //mMazas = GetMazas();
    }


    public void addMaza(Maza m) {
        mMazas.add(m);
    }

    public List<Maza> GetMazas() {
        mMazas = LoadListaMazasFromMaquina();
        return mMazas;
    }

    public Maza getMaza(int Cod, int Escala) {

        for (Maza maza : mMazas) {
            if ((maza.getCod() == Cod) && (maza.getEscala() == Escala)) {
                return maza;
            }
        }
        return null;
    }

    public Maza getMaza(UUID id) {
        for (Maza maza : mMazas) {
            if (maza.getmId().equals(id)) {
                return maza;
            }
        }
        return null;
    }


    public void UpdateMaza(int Cod, int Escala, float _EMazaStd, float _FMazaCal, float _EMazaCal, float _LongPendulo, float _EPerdidas) {
        int i;
        i = 0;
        for (Maza maza : mMazas) {
            if ((maza.getCod() == Cod) && (maza.getEscala() == Escala)) {
                maza.setEMazaStd(_EMazaStd);
                maza.setFMazaCal(_FMazaCal);
                maza.setEMazaCal(_EMazaCal);
                maza.setLongPendulo(_LongPendulo);
                maza.setEPerdidas(_EPerdidas);

                mMazas.set(i, maza);
                break;
            }
            i++;
        }

    }

    //Transforma un string que representa una maza a una  estructura de tipo Maza
    //RP|20|TIPO_MAZA|ESCALA como entrada a la placa, devuelve
    // FMazaCal | EMazaStd | EMazaCal | LongPendulo | EPerdidas
    // Decode de los Parametros de calibracion de las mazas en EEPROM
    public Maza DecodeMazaToStrucMaza(String strMaza) {
        String[] tokens = strMaza.split(Pattern.quote("|"));
        Maza maza = new Maza();

        String sFMazaCal = tokens[0];
        String sEMazaStd = tokens[1];
        String sEMazaCal = tokens[2];
        String sLongPendulo = tokens[3];
        String sEPerdidas = tokens[4];


        if (tokens.length == 5 && Utils.isFloatNumber(sFMazaCal) && Utils.isFloatNumber(sEMazaStd) &&
                Utils.isFloatNumber(sEMazaCal) && Utils.isFloatNumber(sLongPendulo) &&
                Utils.isFloatNumber(sEPerdidas)) {

            maza.setFMazaCal(Float.parseFloat(tokens[0]));
            maza.setEMazaStd(Float.parseFloat(tokens[1]));
            maza.setEMazaCal(Float.parseFloat(tokens[2]));
            maza.setLongPendulo(Float.parseFloat(tokens[3]));
            maza.setEPerdidas(Float.parseFloat(tokens[4]));

        }
        return maza;
    }


    //Transforma un string que representa una maza a una  estructura de tipo Maza
    //RP|27 como entrada a la placa, devuelve la maza conectada
    // TIPO_MAZA|ESCALA | FMazaCal | EMazaStd | EMazaCal | LongPendulo | EPerdidas
    // Decode de los Parametros de calibracion de las mazas en EEPROM
    public Maza DecodeMazaConectadaToStrucMaza(String strMaza) {
        String[] tokens = strMaza.split(Pattern.quote("|"));
        Maza maza = new Maza();

        String sCodEnsayo = tokens[0];
        String sEscala = tokens[1];

        String sFMazaCal = tokens[2];
        String sEMazaStd = tokens[3];
        String sEMazaCal = tokens[4];
        String sLongPendulo = tokens[5];
        String sEPerdidas = tokens[6];


        if (tokens.length == 7 && Utils.isFloatNumber(sFMazaCal) && Utils.isFloatNumber(sEMazaStd) &&
                Utils.isFloatNumber(sEMazaCal) && Utils.isFloatNumber(sLongPendulo) &&
                Utils.isFloatNumber(sEPerdidas)) {

            maza.setCod(Integer.parseInt(tokens[0]));
            maza.setEscala(Integer.parseInt(tokens[1]));

            maza.setFMazaCal(Float.parseFloat(tokens[2]));
            maza.setEMazaStd(Float.parseFloat(tokens[3]));
            maza.setEMazaCal(Float.parseFloat(tokens[4]));
            maza.setLongPendulo(Float.parseFloat(tokens[5]));
            maza.setEPerdidas(Float.parseFloat(tokens[6]));

        }
        return maza;
    }


    //Recarga las mazas y salva al FS
    public static void SaveMazasToFile() {
        mMazas = LoadListaMazasFromMaquina();
        for (Maza maza : mMazas) {
            SaveMazaToFile(maza);

        }
    }

    public static void SaveMazaToFile(Maza mMaza) {

        Integer CodEnsayo = mMaza.getCod();
        Integer Escala = mMaza.getEscala();

        float FMazaCal = mMaza.getFMazaCal();
        float EMazaStd = mMaza.getEMazaStd();
        float EMazaCal = mMaza.getEMazaCal();
        float LongPendulo = mMaza.getLongPendulo();
        float EPerdidas = mMaza.getEPerdidas();

        String key = "MAZA|" + Integer.toString(CodEnsayo) + "|" + Integer.toString(Escala);

        String valor = Integer.toString(CodEnsayo) + "|" + Integer.toString(Escala) + "|" + Float.toString(FMazaCal) + "|" + Float.toString(EMazaStd) + "|" +
                Float.toString(EMazaCal) + "|" + Float.toString(LongPendulo) + "|" + Float.toString(EPerdidas);


        putPref(key, valor);
    }

    public Maza LoadMazaFromFile(Integer CodEnsayo, Integer Escala) {
        String key = "MAZA|" + Integer.toString(CodEnsayo) + "|" + Integer.toString(Escala);
        String sMaza = getPref(key);
        Maza maza = DecodeMazaConectadaToStrucMaza(sMaza);
        return maza;
    }

    public static void putPref(String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPref(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getString(key, null);
    }


    private void LoadListaMazasFromFile() {

    }


// -------------- Interactuan con la maq.


    /*
    private UUID Id;
    private Integer CodEnsayo; //codigo de la maza Nota:de moneto sin usar
    private Integer Escala; //1 .. 16
    private float FMazaCal; // valor estandard
    private float EMazaStd; // valor estandard
    private float EMazaCal; // valor calibrado
    private float LongPendulo; //Long. brazo calibrado
    private float EPerdidas;
*/
    //Carga las mazas desde la maq. y las salva en el FS de la tactil
    // y vuelca el resultado a la variable global de mazas.
    private static List<Maza> LoadListaMazasFromMaquina() {
        ArrayList<Maza> aMazas = new ArrayList<>();

        Integer CodEnsayo;
        Integer Escala;
        float FMazaCal;
        float EMazaStd;
        float EMazaCal;
        float LongPendulo;
        float EPerdidas;
        Maza maza;

        String smaza; //= "0|0|2.45|1.00|1.02|0.230|0.000";
        String[] tokens;



        //Carga mazas ISO
        for (int j = 0; j < NR_MAZAS_ISO; j++) {
            smaza = String.valueOf(ISO_CHAR) + "|" + String.valueOf(j) + "|" + conexionPlaca.doGetDatosMazaEEPROM(ISO_CHAR, j);
            tokens = smaza.split(Pattern.quote("|"));
            CodEnsayo = Integer.parseInt(tokens[0]);
            Escala = Integer.parseInt(tokens[1]);
            FMazaCal = Float.parseFloat(tokens[2]);
            EMazaStd = Float.parseFloat(tokens[3]);
            EMazaCal = Float.parseFloat(tokens[4]);
            LongPendulo = Float.parseFloat(tokens[5]);
            EPerdidas = Float.parseFloat(tokens[6]);

            maza = new Maza();

            maza.setCod(CodEnsayo);
            maza.setEscala(Escala);
            maza.setFMazaCal(FMazaCal);
            maza.setEMazaCal(EMazaStd);
            maza.setEMazaStd(EMazaCal);
            maza.setLongPendulo(LongPendulo);
            maza.setEPerdidas(EPerdidas);
            aMazas.add(maza);
            SaveMazaToFile(maza);
        }
        //Carga mazas ASTM
        for (int j = 0; j < NR_MAZAS_ASTM; j++) {
            smaza = String.valueOf(ASTM_CHAR) + "|" + String.valueOf(j) + "|" + conexionPlaca.doGetDatosMazaEEPROM(ASTM_CHAR, j);
            tokens = smaza.split(Pattern.quote("|"));
            CodEnsayo = Integer.parseInt(tokens[0]);
            Escala = Integer.parseInt(tokens[1]);
            FMazaCal = Float.parseFloat(tokens[2]);
            EMazaStd = Float.parseFloat(tokens[3]);
            EMazaCal = Float.parseFloat(tokens[4]);
            LongPendulo = Float.parseFloat(tokens[5]);
            EPerdidas = Float.parseFloat(tokens[6]);

            maza = new Maza();

            maza.setCod(CodEnsayo);
            maza.setEscala(Escala);
            maza.setFMazaCal(FMazaCal);
            maza.setEMazaCal(EMazaStd);
            maza.setEMazaStd(EMazaCal);
            maza.setLongPendulo(LongPendulo);
            maza.setEPerdidas(EPerdidas);
            aMazas.add(maza);
            SaveMazaToFile(maza);
        }

        //Carga mazas IZOD
        for (int j = 0; j < NR_MAZAS_IZOD; j++) {
            smaza = String.valueOf(IZOD_CHAR) + "|" + String.valueOf(j) + "|" + conexionPlaca.doGetDatosMazaEEPROM(IZOD_CHAR, j);
            tokens = smaza.split(Pattern.quote("|"));
            CodEnsayo = Integer.parseInt(tokens[0]);
            Escala = Integer.parseInt(tokens[1]);
            FMazaCal = Float.parseFloat(tokens[2]);
            EMazaStd = Float.parseFloat(tokens[3]);
            EMazaCal = Float.parseFloat(tokens[4]);
            LongPendulo = Float.parseFloat(tokens[5]);
            EPerdidas = Float.parseFloat(tokens[6]);

            maza = new Maza();

            maza.setCod(CodEnsayo);
            maza.setEscala(Escala);
            maza.setFMazaCal(FMazaCal);
            maza.setEMazaCal(EMazaStd);
            maza.setEMazaStd(EMazaCal);
            maza.setLongPendulo(LongPendulo);
            maza.setEPerdidas(EPerdidas);
            aMazas.add(maza);
            SaveMazaToFile(maza);
        }

        //Salva en la variable global las mazas
        //mApplication.vg.aMazas = (ArrayList<Maza>) aMazas.clone();
        //mMazas = (List<Maza>) aMazas.clone();
        return aMazas;


    }




}
