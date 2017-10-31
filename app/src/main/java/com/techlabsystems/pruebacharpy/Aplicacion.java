package com.techlabsystems.pruebacharpy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;


import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.techlabsystems.drivers.DriverCharpyArduino;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

/**
 * Created by juanjo on 25/09/2017.
 */

public class Aplicacion extends Application {
// Version de test
    private static Aplicacion instancia;
    private static Context context;

    public static String  APP_VERSION = "1.1 - 2017";


    //public static String SIMBOLO_GRADO = "°";
    //public static String SIMBOLO_M2 = "m²"; //<Alt> 253 = ²
    //public static String SIMBOLO_M3 = "m³"; //<Alt> 252 = ³

    public static String Energia_label = "J";
    public static String Energia_m2_label ="kJ/m²";
    public static String Energia_m2_label_asci ="kJ/m^2";

    public static String Energia_imperial_label = "ft-lbf";
    public static String Energia_m2_imperial_label ="ft·lbf/in²"; //J/m^2
    public static String Energia_m2_imperial_label_asci ="ft-lbf/in^2"; //J/m^2


    public static double J_To_FT_LBS = 0.737571913261543; // Joules to ft-lbs Pie por libra (Foot pound)
    public static double KJM2_FT_LBF_IN2 = 0.475846; // kJ/m^2 a Pie-libra fuerza/pulgada cuadrada

    public static double MM_TO_PULGADA = 0.0393701;
    public static double INCH_TO_MM = 25.4;



    //1ft·lbf/in.2= 2.1 kJ/m2
    //1 ft·lbf/in.2 = 2.1 kJ/m2
    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;


    boolean mByteReceivedBack;
    Object mByteReceivedBackSemaphore = new Object();
    Integer mValueToSend = new Integer(0);

    byte[] readBuffer; /*circular buffer*/
    int readIndex = 0;

    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;


    private  DriverCharpyArduino DriverCarpy;


    public static VarGlobal vg;
    public VarGlobalRam vgram;
    public CalculosPendulo calculosPendulo;


    public class VarGlobal {


        public CListaMazas mCListaMazas;
        public CFuncionesEnsayosCharpy mCFuncionesEnsayosCharpy;

        public Integer SistemaUnidades ; //o = metrico , 1=imperial
        public Integer TipoEnsayo; // 0..3
        public Integer EscalaMaza;// R1..R8
        public Integer FuncionRealizar; //TEST, CALIBRACION, OTRA (1,2,3)

        public Maza mMaza;
        public float angCaidaInicial;
        public float anchoprobeta;
        public float espesorprobeta;
        Integer nrProbeta;
        public List<Probeta> mProbetas ;

        // datos para la grafica
        public String Leyenda;
        public String TituloGrafico;
        public Boolean MostrarLeyenda;


    }

    public class VarGlobalRam {
        // -- Charpy
        public double angulo_maximo_pendulo;
        //public double angulo_maximo_pendulo_g; // grados
        public double EImpacto;
        public double Epinc; // EnergiaPotencialInic
        public double EKJm2; // energia en KJ/m2
        public double EPerdidas;
        public double Lp;  // Log. del pendulo cal por medio del periodo
        public double velocidadImpacto_sof; // Velocidad impacto calculada

        //Lecturas de la placa
        public float tciclo; // tciclo
        public float ciclos ; // ciclos
        public float lp; // log. pendulo
        public float LecturaExtensionCero ;

        Probeta probeta;

        //Control

    }


    public static Aplicacion getInstance(){
        if (instancia == null) {
            instancia = new Aplicacion();
        }
        return instancia;
    }

    public Aplicacion(){
        // constructor oculto
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        vg = new VarGlobal();
        vgram = new VarGlobalRam();
        calculosPendulo =  CalculosPendulo.newInstance(this);

        context = getApplicationContext();
        instancia = this;
        openSerialPort();
        DriverCarpy = DriverCharpyArduino.newInstance(this);

        vg.mCListaMazas = CListaMazas.newInstance(context);
        vg.mCFuncionesEnsayosCharpy = CFuncionesEnsayosCharpy.newInstance(context);
        vg.TipoEnsayo = 0;
        vg.EscalaMaza = 0;
        vg.nrProbeta = 0;
        vg.SistemaUnidades = 0; //metrico;
        vgram.probeta = new Probeta();
        vg.mProbetas = new ArrayList<>();





    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        closeSerialPort();
    }



    public static Context getStaticContext()
    {
        return context;
    }

    public  DriverCharpyArduino getDriver()
    {
        return DriverCarpy;
    }

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
			/* Read serial port parameters */

            SharedPreferences sp = getSharedPreferences("com.techlabsystems.pruebacharpy_preferences", MODE_PRIVATE);
            String path = sp.getString("DEVICE", "");
            int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

            path ="/dev/ttyS5";
            baudrate = 19200;

			/* Check parameters */
            if ( (path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mReadThread != null)
            mReadThread.interrupt();
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        if (mByteReceivedBackSemaphore != null)
            mByteReceivedBackSemaphore = null;
    }



    public void openSerialPort()
    {
        try {
            mSerialPort = getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            readBuffer = new byte[4096];
			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            DisplayError(R.string.error_security);
        } catch (IOException e) {
            DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            DisplayError(R.string.error_configuration);
        }
    }

    private void DisplayError(int resourceId) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //mApplication.getActivity.finish();
            }
        });
        b.show();
    }





/*
    public void vaciarBuffers()
    {
        readIndex = 0;
    }
*/

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void onDataReceived(byte[] buffer, int size) {
        synchronized (mByteReceivedBackSemaphore) {
            int i;
            for (i = 0; i < size; i++) {
                if ((buffer[i] == '\r') && (mByteReceivedBack == false)) {
                    readBuffer[readIndex] = 0;
                    //mValueToSend++;

                    mByteReceivedBack = true;
                    mByteReceivedBackSemaphore.notify();
                } else {
                    readBuffer[readIndex] = buffer[i];
                    readIndex++;
                }
            }
        }
    }

    // Envio con respuesta
    public  synchronized  String maqwr(String comando) {

        readIndex = 0;
        String data = "";


        mByteReceivedBack = false;

        if (mSerialPort != null) {
            mSerialPort.sendCmds(comando);

            //while (!mByteReceivedBack){};

            synchronized (mByteReceivedBackSemaphore) {
                try {
                    mByteReceivedBackSemaphore.wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                byte[] encodeBytes = new byte[readIndex];
                System.arraycopy(readBuffer, 0, encodeBytes, 0, encodeBytes.length);

                try {
                    data = new String(encodeBytes, "US-ASCII");
                } catch (UnsupportedEncodingException e) {
                }


            }
        }
        return data;
    }


    // Envio sin respuesta
    public  synchronized  void maqw(String comando) {

        if (mSerialPort != null) {
            mSerialPort.sendCmds(comando);

        }
    }

    public SerialPort GetSerialPort (){
        return mSerialPort;
    }


    public void xxx(){

    }
}
