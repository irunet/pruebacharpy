package com.techlabsystems.pruebacharpy;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.techlabsystems.drivers.DriverCharpyArduino;
import com.techlabsystems.utilidades.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by juanjo on 28/09/2017.
 */

public class CalibracionFragment extends Fragment {

    private static final String TAG = EnsayoFragment.class.getSimpleName();

    private static final String ARG_CONTEXT = "ARG_CONTEXT";
    private static final String ARG_TIPO_ENSAYO = "ARG_TIPO_ENSAYO";
    public static final String ARG_ESCALA_MAZA = "ARG_ESCALA_MAZA";

    public static final int MESSAGE_DISPLAY_DATA_ANGULO = 1;
    public static final int MESSAGE_DISPLAY_DATA_CICLOS = 2;
    public static final int MESSAGE_DISPLAY_DATA_LONG_PENDULO = 3;
    public static final int MESSAGE_DISPLAY_DATA_ENERGIA_PERDIDAS = 4;
    public static final int MESSAGE_DISPLAY_DATA_ANG_DISPARO = 5;
    public static final int MESSAGE_DISPLAY_DATA_ANGULO_CERO = 6;

    public static final int MESSAGE_DISPLAY_DATA_FMAZACAL = 7;
    public static final int MESSAGE_DISPLAY_DATA_LONGBRAZOCAL = 8;
    public static final int MESSAGE_DISPLAY_DATA_EMAZACAL = 9;
    public static final int MESSAGE_DISPLAY_DATA_ESPESOR = 10;
    public static final int MESSAGE_DISPLAY_DATA_ANCHURA = 11;
    public static final int MESSAGE_DISPLAY_DATA_AVISO = 12;

    public static final String PROMPT_ANCHURA = "PROMPT_ANCHURA";

    public static String DIR_CONFIG = "/Android/data/com.techlabsystems.charpy/files/";
    public static String FILE_CONFIG = "file_config.txt";


    //CListaMazas mCListaMazas;


    volatile boolean haciendoTest;
    Integer mTipoEnsayo, mEscalaMaza;

    protected Aplicacion mApplication;
    DriverCharpyArduino conexionPlaca;

    LecturasThread mLecturasThread;

    private FragmentActivity myContext;

    public static CalibracionFragment newInstance(Integer TipoEnsayo, Integer EscalaMaza) {

        Bundle args = new Bundle();

        args.putSerializable(ARG_TIPO_ENSAYO, TipoEnsayo);
        args.putSerializable(ARG_ESCALA_MAZA, EscalaMaza);
        CalibracionFragment fragment = new CalibracionFragment();
        fragment.setArguments(args);
        return fragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mTipoEnsayo = (Integer) getArguments().getSerializable(ARG_TIPO_ENSAYO);
        mEscalaMaza = (Integer) getArguments().getSerializable(ARG_ESCALA_MAZA);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calibracion, container, false);

        //--- Conexion con el equipo
        mApplication = (Aplicacion) (getActivity().getApplication());

        if (mApplication.GetSerialPort() == null) {
            mApplication.openSerialPort();

        }
        if (mApplication.GetSerialPort() != null) {
            conexionPlaca = mApplication.getDriver();
        }

        final Button fragment_calibracion_btn_resetciclos = (Button) view.findViewById(R.id.fragment_calibracion_btn_resetciclos);
        final Button fragment_calibracion_btn_angcero = (Button) view.findViewById(R.id.fragment_calibracion_btn_angcero);
        final Button fragment_calibracion_btn_angdisparo = (Button) view.findViewById(R.id.fragment_calibracion_btn_angdisparo);
        final Button fragment_calibracion_btn_calperdidas = (Button) view.findViewById(R.id.fragment_calibracion_btn_calperdidas);
        final Button fragment_calibracion_btn_CalAngMax = (Button) view.findViewById(R.id.fragment_calibracion_btn_CalAngMax);

        final Button fragment_calibracion_btn_setEspesor = (Button) view.findViewById(R.id.fragment_calibracion_btn_setEspesor);
        final Button fragment_calibracion_btn_setAnchura = (Button) view.findViewById(R.id.fragment_calibracion_btn_setAnchura);

        final Button fragment_calibracion_btn_salvar_config_ram = (Button) view.findViewById(R.id.fragment_calibracion_btn_salvar_config_ram);
        final Button fragment_calibracion_btn_salvar_config_eeprom = (Button) view.findViewById(R.id.fragment_calibracion_btn_salvar_config_eeprom);
        final Button fragment_calibracion_btn_Test = (Button) view.findViewById(R.id.fragment_calibracion_btn_Test);

        final Button fragment_calibracion_btn_seFMazaCal = (Button) view.findViewById(R.id.fragment_calibracion_btn_seFMazaCal);
        final Button fragment_calibracion_btn_seLBrazoCal = (Button) view.findViewById(R.id.fragment_calibracion_btn_seLBrazoCal);
        final Button fragment_calibracion_btn_setEmazaCal = (Button) view.findViewById(R.id.fragment_calibracion_btn_setEmazaCal);


        CharSequence str_ensayo_seleccionado = "";

        if (mTipoEnsayo == CListaMazas.ISO_CHAR) {
            str_ensayo_seleccionado = getResources().getString(R.string.calibracion_ISO_seleccionado, String.valueOf(mEscalaMaza + 1));
        } else if (mTipoEnsayo == CListaMazas.ASTM_CHAR) {
            str_ensayo_seleccionado = getResources().getString(R.string.calibracion_ASTM_seleccionado, String.valueOf(mEscalaMaza + 1));
        } else if (mTipoEnsayo == CListaMazas.IZOD_CHAR) {
            str_ensayo_seleccionado = getResources().getString(R.string.calibracion_IZOD_seleccionado, String.valueOf(mEscalaMaza + 1));
        }


        //mCListaMazas = CListaMazas.newInstance(mApplication.getApplicationContext());
        //List<Maza> ListaMazas = mCListaMazas.LoadListaMazasFromMaquina();
        List<Maza> ListaMazas = mApplication.vg.mCListaMazas.GetMazas();

        // Cargar datos por defecto de la Maq a la tactil

        String strmaza = conexionPlaca.doGetDatosMazaEEPROM(mTipoEnsayo, mEscalaMaza);
        mApplication.vg.mMaza = mApplication.vg.mCListaMazas.DecodeMazaToStrucMaza(strmaza);
        mApplication.vg.mMaza.setCod(mTipoEnsayo);
        mApplication.vg.mMaza.setEscala(mEscalaMaza);
        mApplication.vg.angCaidaInicial = conexionPlaca.doGetAnguloDisparo();
        mApplication.vg.anchoprobeta = conexionPlaca.doLeerAnchoProbeta();
        mApplication.vg.espesorprobeta = conexionPlaca.doLeerEspesorProbeta();

        mApplication.vg.mMaza.setEMazaCal((float) mApplication.calculosPendulo.CalEnergiaMaza());


        //Maza mMaza = mCListaMazas.getMazaFromEEPROM(mTipoEnsayo,mEscalaMaza);
        CharSequence str_capacida_maza_seleccionada = Float.toString(mApplication.vg.mMaza.getEMazaStd());

        getActivity().setTitle(str_ensayo_seleccionado + " (" + str_capacida_maza_seleccionada + " J)");

        CrearFicheroConfiguracionMaquina(FILE_CONFIG);

        if (mApplication.GetSerialPort() != null) {
            conexionPlaca.doBobinaOFF();
            mLecturasThread = new LecturasThread();
            mLecturasThread.start();
        }

        fragment_calibracion_btn_resetciclos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                conexionPlaca.doResetCiclos();

            }
        });

        fragment_calibracion_btn_angcero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float pos;

                conexionPlaca.doHacerCeroExtension();
                pos = conexionPlaca.doLeerExtension();


                String menssage = String.format("%.3f", pos);

                if (getView() != null) {
                    TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_textViewAngCero);
                    lb.setText(menssage);
                }

            }
        });

        fragment_calibracion_btn_angdisparo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mApplication.vg.angCaidaInicial = conexionPlaca.doLeerExtension();
                conexionPlaca.doSetAnguloDisparo();

                /*
                String menssage = String.format("%.3f", pos);

                if (getView() != null) {
                    TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_textViewAngdisparo);
                    lb.setText(menssage);
                }
                */

            }
        });

        fragment_calibracion_btn_calperdidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                conexionPlaca.SeleccionarTipoEnsayo(mApplication.vg.TipoEnsayo, mApplication.vg.EscalaMaza);


                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());


                builder.setMessage(getString(R.string.fragment_calibracion_btn_calperdidas_dialog_msg))
                        .setTitle(getString(R.string.fragment_calibracion_btn_calperdidas_dialog_title))
                        .setPositiveButton(getString(R.string.fragment_calibracion_btn_calperdidas_dialog_btn_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Log.i("Dialogos", "Confirmacion Aceptada.");
                                fragment_calibracion_btn_calperdidas.setEnabled(false);
                                CalPerdidasAsyncTask mCalPerdidas = new CalPerdidasAsyncTask(1);
                                mCalPerdidas.execute(2000); //tiempo de respuesta = 2 s. para leer el resultado
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(getString(R.string.fragment_calibracion_btn_calperdidas_dialog_btn_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Log.i("Dialogos", "Confirmacion Cancelada.");
                                dialog.cancel();
                            }
                        });

                builder.create().show();



            }
        });

        fragment_calibracion_btn_CalAngMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float pos;

                mApplication.vgram.angulo_maximo_pendulo = conexionPlaca.doGetAnguloMaxEnsayo();
                String menssage = String.format("%.3f", mApplication.vgram.angulo_maximo_pendulo);


                if (getView() != null) {
                    TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_textViewAngMax);
                    lb.setText(menssage);
                }


            }
        });

//*******************
        fragment_calibracion_btn_setEspesor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetEspesor();

            }
        });

        fragment_calibracion_btn_setAnchura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SetAnchura();
            }
        });

        fragment_calibracion_btn_salvar_config_eeprom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                conexionPlaca.doUpdateDatosMazaToEEPROM(mApplication.vg.TipoEnsayo, mApplication.vg.EscalaMaza, mApplication.vg.mMaza.getFMazaCal(),
                        mApplication.vg.mMaza.getEMazaStd(), mApplication.vg.mMaza.getEMazaCal(),
                        mApplication.vg.mMaza.getLongPendulo(), mApplication.vg.mMaza.getEPerdidas());

                fragment_calibracion_btn_salvar_config_eeprom.setEnabled(false);
                SaveTOEEPROMAsyncTask saveTOEEPROMAsyncTask = new SaveTOEEPROMAsyncTask(1);
                saveTOEEPROMAsyncTask.execute(8000); //tiempo de respuesta = 8 s

                mApplication.vg.mCListaMazas.SaveMazaToFile(mApplication.vg.mMaza); //salvar la maza al fs de la tactil


            }
        });

        fragment_calibracion_btn_salvar_config_ram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                conexionPlaca.doUpdateDatosMazaToRAM(mApplication.vg.TipoEnsayo, mApplication.vg.EscalaMaza, mApplication.vg.mMaza.getFMazaCal(),
                        mApplication.vg.mMaza.getEMazaStd(), mApplication.vg.mMaza.getEMazaCal(),
                        mApplication.vg.mMaza.getLongPendulo(), mApplication.vg.mMaza.getEPerdidas());

                //Recargar en la ram de la placa los parametros de la celula actualizadaos
                //conexionPlaca.SeleccionarTipoEnsayo(mApplication.vg.TipoEnsayo, mApplication.vg.EscalaMaza);


            }
        });
        fragment_calibracion_btn_Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conexionPlaca.SeleccionarTipoEnsayo(mApplication.vg.TipoEnsayo, mApplication.vg.EscalaMaza);


                try {
                    haciendoTest = true;
                    fragment_calibracion_btn_Test.setEnabled(false);
                    CalEnergiaAsyncTask mCalEnergia = new CalEnergiaAsyncTask(1);
                    mCalEnergia.execute(2000); //tiempo de respuesta = 2 s. para leer el resultado
                } catch (Exception e) {

                } finally {
                    fragment_calibracion_btn_Test.setEnabled(true);
                    //conexionPlaca.doBobinaOFF();
                    haciendoTest = false;
                }


            }
        });


        fragment_calibracion_btn_seFMazaCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SetFMazaCal();
            }
        });

        fragment_calibracion_btn_seLBrazoCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SetLogBrazo();
            }
        });

        fragment_calibracion_btn_setEmazaCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SetEMazaCal();
            }
        });


        return view;
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String strIncom;
            String menssage;
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_DISPLAY_DATA_ANGULO:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lbConsolaFuerza = (TextView) getView().findViewById(R.id.fragment_calibracion_Angulo);
                            lbConsolaFuerza.setText(menssage);
                        }


                    }

                    break;

                case MESSAGE_DISPLAY_DATA_CICLOS:

                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.0f", x);

                        if (getView() != null) {
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_textViewNrCiclos);
                            lb.setText(menssage);
                        }
                    }
                    break;

                case MESSAGE_DISPLAY_DATA_LONG_PENDULO:


                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_longpendulo);
                            lb.setText(menssage);
                        }
                    }
                    break;

                case MESSAGE_DISPLAY_DATA_ENERGIA_PERDIDAS:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_textViewCalperdidas);
                            lb.setText(menssage);
                        }


                    }

                    break;

                case MESSAGE_DISPLAY_DATA_ANG_DISPARO:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_textViewAngdisparo);
                            lb.setText(menssage);
                        }

                    }

                    break;
                case MESSAGE_DISPLAY_DATA_ANGULO_CERO:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_textViewAngCero);
                            lb.setText(menssage);
                        }
                    }

                    break;

                case MESSAGE_DISPLAY_DATA_FMAZACAL:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_FMazaCal);
                            lb.setText(menssage);
                        }
                    }

                    break;
                case MESSAGE_DISPLAY_DATA_LONGBRAZOCAL:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_LBrazoCal);
                            lb.setText(menssage);
                        }
                    }

                    break;
                case MESSAGE_DISPLAY_DATA_EMAZACAL:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_EMazaCal);
                            lb.setText(menssage);
                        }
                    }

                    break;
                case MESSAGE_DISPLAY_DATA_ESPESOR:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Espesor);
                            lb.setText(menssage);
                        }
                    }

                    break;

                case MESSAGE_DISPLAY_DATA_ANCHURA:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Anchura);
                            lb.setText(menssage);
                        }
                    }

                    break;
                case MESSAGE_DISPLAY_DATA_AVISO:
                    menssage = (String) msg.obj;


                    if (getView() != null) {
                        TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_aviso);
                        lb.setText(menssage);
                    }


                    break;

            }
        }
    };


    public class LecturasThread extends Thread {
        volatile boolean isRunning = true;//make sure use volatile keyword

        @Override
        public void run() {

            while (isRunning) {
                try {
                    Thread.sleep(100);
                    Lecturas();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    //e.printStackTrace();
                    return;
                }
            }
        }

        public void setRunning(boolean running) {
            this.isRunning = running;
        }
    }

    ;


    private void Lecturas() {

        Message msg;


        float val = conexionPlaca.doLeerExtension();

        if (haciendoTest) return;

        String rta = String.valueOf(val);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_ANGULO, rta);
        mHandler.sendMessage(msg);

        val = conexionPlaca.doGetEnergiaPerdidas();
        rta = String.valueOf(val);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_ENERGIA_PERDIDAS, rta);
        mHandler.sendMessage(msg);


        val = conexionPlaca.doGetAnguloDisparo();
        rta = String.valueOf(val);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_ANG_DISPARO, rta);
        mHandler.sendMessage(msg);

        conexionPlaca.doGetPeriodo();
        rta = String.valueOf(mApplication.vgram.lp);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_LONG_PENDULO, rta);
        mHandler.sendMessage(msg);

        rta = String.valueOf(mApplication.vgram.ciclos);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_CICLOS, rta);
        mHandler.sendMessage(msg);

        //----
        rta = String.valueOf(mApplication.vg.anchoprobeta);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_ANCHURA, rta);
        mHandler.sendMessage(msg);

        rta = String.valueOf(mApplication.vg.espesorprobeta);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_ESPESOR, rta);
        mHandler.sendMessage(msg);

        rta = String.valueOf(mApplication.vg.mMaza.getFMazaCal());
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_FMAZACAL, rta);
        mHandler.sendMessage(msg);

        rta = String.valueOf(mApplication.vg.mMaza.getLongPendulo());
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_LONGBRAZOCAL, rta);
        mHandler.sendMessage(msg);

        rta = String.valueOf(mApplication.vg.mMaza.getEMazaCal());
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_EMAZACAL, rta);
        mHandler.sendMessage(msg);


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
        SaveConfigToFile();

        if (mLecturasThread != null)
            mLecturasThread.setRunning(false);

        mLecturasThread = null;

    }


    public class CalPerdidasAsyncTask extends AsyncTask<Integer, Void, Void> {

        final int identifier;
        TextView lb;

        CalPerdidasAsyncTask(final int identifier) {
            this.identifier = identifier;
            haciendoTest = true;
            lb = (TextView) getView().findViewById(R.id.fragment_calibracion_Angulo);
            lb.setVisibility(View.INVISIBLE);
            conexionPlaca.doSetPerdidasMazaConectada(0); //poner a cero las perdidas
        }


        protected void onPreExecute() {

            //conexionPlaca.doBobinaON();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                conexionPlaca.doEnsayo(); //hacer nsayo
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


            lb.setVisibility(View.VISIBLE);
            try {
                menssage = String.format("%.3f", mApplication.vgram.EPerdidas);
            } catch (Exception e) {
                ePerdidas = 9999;
                menssage = String.format("%.3f", mApplication.vgram.EPerdidas);
            }
            ;

            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_textViewCalperdidas);
                lb.setText(menssage);
            }


            menssage = String.format("%.3f", mApplication.vgram.angulo_maximo_pendulo);

            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_textViewAngMax);
                lb.setText(menssage);
            }

            haciendoTest = false;

        }
    }


    public class CalEnergiaAsyncTask extends AsyncTask<Integer, Void, Void> {

        final int identifier;
        TextView lb;

        CalEnergiaAsyncTask(final int identifier) {
            this.identifier = identifier;
            lb = (TextView) getView().findViewById(R.id.fragment_calibracion_Angulo);
            lb.setVisibility(View.INVISIBLE);
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

            double energia_sof = mApplication.calculosPendulo.CalEnergiaImpacto();
            //double energia = conexionPlaca.doGetEnergiaImpacto(); ??


            lb.setVisibility(View.VISIBLE);
            try {
                menssage = String.format("%.3f J", energia_sof);
            } catch (Exception e) {
                energia_sof = 9999;
                menssage = String.format("%.3f J", energia_sof);
            }


            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_textViewEnergia);
                lb.setText(menssage);
            }

            //float pos = conexionPlaca.doGetAnguloMaxEnsayo();
            menssage = String.format("%.3f", mApplication.vgram.angulo_maximo_pendulo);

            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_textViewAngMax);
                lb.setText(menssage);
            }

            menssage = String.format("%.3f kJ/m²", mApplication.vgram.EKJm2);

            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_calibracion_textViewEnergia_m2);
                lb.setText(menssage);
            }


        }
    }


    public class SaveTOEEPROMAsyncTask extends AsyncTask<Integer, Void, Void> {

        final int identifier;
        TextView lb;
        final Button fragment_calibracion_btn_salvar_config_eeprom;

        SaveTOEEPROMAsyncTask(final int identifier) {
            this.identifier = identifier;
            haciendoTest = true;
            fragment_calibracion_btn_salvar_config_eeprom = (Button) getView().findViewById(R.id.fragment_calibracion_btn_salvar_config_eeprom);
            fragment_calibracion_btn_salvar_config_eeprom.setEnabled(false);

            String menssage = "Salvando datos a la EEPROM";
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_aviso);
                lb.setText(menssage);
            }
        }


        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                conexionPlaca.doSalvarConfigEEPROM();
                Log.i("SalvarConfigEEPROM", "Job task#" + identifier + " has started");
                Thread.sleep(params[0]);
                Log.i("SalvarConfigEEPROM", "Job task#" + identifier + " has finished");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            String menssage = "Datos salvados a la EEPROM";
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_aviso);
                lb.setText(menssage);
            }
            SaveConfigToFile();
            fragment_calibracion_btn_salvar_config_eeprom.setEnabled(true);
            haciendoTest = false;

        }
    }


    //Registrarla actividad que contiene al fragment
    @Override
    public void onAttach(Activity activity) {
        myContext = (AppCompatActivity) activity;
        super.onAttach(activity);
    }

    private void SetAnchura() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);

        final EditText texto = new EditText(myContext);
        texto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        texto.setRawInputType(Configuration.KEYBOARD_12KEY);
        builder.setTitle("Anchura");   // Título
        builder.setView(texto);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Log.i("texto escrito por usuario", texto.getText().toString());

                String rta = texto.getText().toString();
                try {
                    mApplication.vg.anchoprobeta = Float.parseFloat(rta);
                    conexionPlaca.doAnchoProbeta(rta);
                } catch (Exception e) {
                    //mApplication.vg.anchoprobeta = 0;
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for CANCEL button here, or leave in blank
            }
        });
        builder.create().show();
    }


    private void SetEspesor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);

        final EditText texto = new EditText(myContext);
        texto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        texto.setRawInputType(Configuration.KEYBOARD_12KEY);
        builder.setTitle("Espesor");   // Título
        builder.setView(texto);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Log.i("texto escrito por usuario", texto.getText().toString());

                String rta = texto.getText().toString();
                try {
                    mApplication.vg.espesorprobeta = Float.parseFloat(rta);
                    conexionPlaca.doEspesorProbeta(rta);

                } catch (Exception e) {
                    //mApplication.vg.anchoprobeta = 0;
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for CANCEL button here, or leave in blank
            }
        });
        builder.create().show();
    }

    private void SetLogBrazo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);

        final EditText texto = new EditText(myContext);
        texto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        texto.setRawInputType(Configuration.KEYBOARD_12KEY);
        builder.setTitle("Long. del brazo calibrado (m)");   // Título
        builder.setView(texto);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


                String rta = texto.getText().toString();
                try {
                    mApplication.vg.mMaza.setLongPendulo(Float.parseFloat(rta));
                    mApplication.vg.mMaza.setEMazaCal((float) mApplication.calculosPendulo.CalEnergiaMaza());

                } catch (Exception e) {

                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for CANCEL button here, or leave in blank
            }
        });
        builder.create().show();
    }


    private void SetFMazaCal() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(myContext);

        final EditText texto = new EditText(myContext);

        texto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        texto.setRawInputType(Configuration.KEYBOARD_12KEY);
        builder.setTitle("Fuerza. del brazo calibrado (N)");   // Título
        builder.setView(texto);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


                String rta = texto.getText().toString();
                try {
                    //builder.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    mApplication.vg.mMaza.setFMazaCal(Float.parseFloat(rta));
                    mApplication.vg.mMaza.setEMazaCal((float) mApplication.calculosPendulo.CalEnergiaMaza());

                } catch (Exception e) {

                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for CANCEL button here, or leave in blank
            }
        });
        builder.create().show();
    }

    private void SetEMazaCal() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(myContext);

        final EditText texto = new EditText(myContext);

        texto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        texto.setRawInputType(Configuration.KEYBOARD_12KEY);
        String svalor = String.format("%.3f", mApplication.vg.mMaza.getEMazaCal());

        texto.setText(svalor);
        builder.setTitle("Energia. de la maza calibrado (J)");   // Título
        builder.setView(texto);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


                String rta = texto.getText().toString();
                try {
                    //builder.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    mApplication.vg.mMaza.setEMazaCal(Float.parseFloat(rta));


                } catch (Exception e) {

                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for CANCEL button here, or leave in blank
            }
        });
        builder.create().show();
    }


    void UpdateDisplayDatosMaza2() {
        String svalor;
        float valor;


        try {
            valor = mApplication.vg.anchoprobeta;
            svalor = String.format("%.3f", valor);
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Anchura);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

        try {
            valor = mApplication.vg.espesorprobeta;
            svalor = String.format("%.3f", valor);
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Espesor);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

        try {
            valor = mApplication.vg.mMaza.getLongPendulo();
            svalor = String.format("%.3f", valor);
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_LBrazoCal);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

        try {
            valor = mApplication.vg.mMaza.getFMazaCal();
            svalor = String.format("%.3f", valor);
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_FMazaCal);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

        //mApplication.vg.mMaza.setEMazaCal((float)mApplication.calculosPendulo.CalEnergiaMaza());

        try {
            valor = mApplication.vg.mMaza.getEMazaCal();
            svalor = String.format("%.3f", valor);
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_EMazaCal);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

    }


    void SaveConfigToFile() {
        // Salvar la config al FS de la tactil
        CListaMazas.SaveMazasToFile();
        mApplication.vg.mCListaMazas.SaveMazaToFile(mApplication.vg.mMaza);
        String svalor = String.format("%.3f", mApplication.vg.anchoprobeta);
        mApplication.vg.mCListaMazas.putPref("ANCHURA", svalor);

        svalor = String.format("%.3f", mApplication.vg.espesorprobeta);
        mApplication.vg.mCListaMazas.putPref("ESPESOR", svalor);

        svalor = String.format("%.3f", mApplication.vg.angCaidaInicial);
        mApplication.vg.mCListaMazas.putPref("ANG_DISPARO", svalor);

        mApplication.vg.mCListaMazas.putPref("TIPO_ENSAYO", String.valueOf(mApplication.vg.TipoEnsayo));
        mApplication.vg.mCListaMazas.putPref("ESCALA_MAZA", String.valueOf(mApplication.vg.EscalaMaza));

        CrearFicheroConfiguracionMaquina();

    }

    public void CrearFicheroConfiguracionMaquina() {
        CrearFicheroConfiguracionMaquina(FILE_CONFIG);
    }

    void CrearFicheroConfiguracionMaquina(String fichero) {
        //Generar un txt con la configuración
        FileOutputStream fso = null;
        String comando;
        String stadoSD = Environment.getExternalStorageState();
        if (!stadoSD.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getContext(), "No puedo acceder en la memoria externa", Toast.LENGTH_LONG).show();
            return;
        }

        File mFileDir = new File(Environment.getExternalStorageDirectory(), DIR_CONFIG);

        if (!mFileDir.exists()) {
            if (!mFileDir.mkdirs()) {
                Log.d("CrearFicheroConfig", "failed to create directory");
                return;
            }
        }
        File mFile = new File(mFileDir, FILE_CONFIG);
        if (mFile.exists()) {
            if (!mFile.delete()) {
                Log.d("CrearFicheroConfig", "failed to create borrar fichero config");
                return;
            }
        }

        try {


            fso = new FileOutputStream(mFile, false);
            comando = String.format("%10s", "NORMA") + "|" + String.format("%10s", "ESCALA") + "|" + String.format("%10s", "FMazaCal") + "|" + String.format("%10s", "EMazaStd")
                    + "|" + String.format("%10s", "EMazaCal") + "|" + String.format("%10s", "LongPendulo") + "|" + String.format("%10s", "EPerdidas") + "\n";
            fso.write(comando.getBytes());
            comando = "------------------------------------------------------------------------------\n";
            fso.write(comando.getBytes());

            for (int j = 0; j < CListaMazas.NR_MAZAS_ISO; j++) {
                String smaza = String.valueOf(CListaMazas.ISO_CHAR) + "|" + String.valueOf(j) + "|" + conexionPlaca.doGetDatosMazaEEPROM(CListaMazas.ISO_CHAR, j);
                String[] tokens = smaza.split(Pattern.quote("|"));
                int CodEnsayo = Integer.parseInt(tokens[0]);
                int Escala = Integer.parseInt(tokens[1]);
                float FMazaCal = Float.parseFloat(tokens[2]);
                float EMazaStd = Float.parseFloat(tokens[3]);
                float EMazaCal = Float.parseFloat(tokens[4]);
                float LongPendulo = Float.parseFloat(tokens[5]);
                float EPerdidas = Float.parseFloat(tokens[6]);

                comando = String.format("%10s", "ISO_CHAR") + "|" + String.format("%10d", j) + "|" + String.format("%10.3f", FMazaCal) + "|" + String.format("%10.3f", EMazaStd)
                        + "|" + String.format("%10.3f", EMazaCal) + "|" + String.format("%10.3f", LongPendulo) + "|" + String.format("%10.3f", EPerdidas) + "\n";
                fso.write(comando.getBytes());

            }

            comando = "\n";
            fso.write(comando.getBytes());

            fso.write(comando.getBytes());
            for (int j = 0; j < CListaMazas.NR_MAZAS_ASTM; j++) {
                String smaza = String.valueOf(CListaMazas.ASTM_CHAR) + "|" + String.valueOf(j) + "|" + conexionPlaca.doGetDatosMazaEEPROM(CListaMazas.ASTM_CHAR, j);
                String[] tokens = smaza.split(Pattern.quote("|"));
                int CodEnsayo = Integer.parseInt(tokens[0]);
                int Escala = Integer.parseInt(tokens[1]);
                float FMazaCal = Float.parseFloat(tokens[2]);
                float EMazaStd = Float.parseFloat(tokens[3]);
                float EMazaCal = Float.parseFloat(tokens[4]);
                float LongPendulo = Float.parseFloat(tokens[5]);
                float EPerdidas = Float.parseFloat(tokens[6]);

                comando = String.format("%10s", "ASTM_CHAR") + "|" + String.format("%10d", j) + "|" + String.format("%10.3f", FMazaCal) + "|" + String.format("%10.3f", EMazaStd)
                        + "|" + String.format("%10.3f", EMazaCal) + "|" + String.format("%10.3f", LongPendulo) + "|" + String.format("%10.3f", EPerdidas) + "\n";
                fso.write(comando.getBytes());

            }

            comando = "\n";
            fso.write(comando.getBytes());

            fso.write(comando.getBytes());
            for (int j = 0; j < CListaMazas.NR_MAZAS_IZOD; j++) {
                String smaza = String.valueOf(CListaMazas.IZOD_CHAR) + "|" + String.valueOf(j) + "|" + conexionPlaca.doGetDatosMazaEEPROM(CListaMazas.IZOD_CHAR, j);
                String[] tokens = smaza.split(Pattern.quote("|"));
                int CodEnsayo = Integer.parseInt(tokens[0]);
                int Escala = Integer.parseInt(tokens[1]);
                float FMazaCal = Float.parseFloat(tokens[2]);
                float EMazaStd = Float.parseFloat(tokens[3]);
                float EMazaCal = Float.parseFloat(tokens[4]);
                float LongPendulo = Float.parseFloat(tokens[5]);
                float EPerdidas = Float.parseFloat(tokens[6]);

                comando = String.format("%10s", "IZOD_CHAR") + "|" + String.format("%10d", j) + "|" + String.format("%10.3f", FMazaCal) + "|" + String.format("%10.3f", EMazaStd)
                        + "|" + String.format("%10.3f", EMazaCal) + "|" + String.format("%10.3f", LongPendulo) + "|" + String.format("%10.3f", EPerdidas) + "\n";
                fso.write(comando.getBytes());

            }
            comando = "\n";
            fso.write(comando.getBytes());
            comando = "------------------------------------------------------------------------------\n";
            fso.write(comando.getBytes());

            comando = String.format("%10s", "Anchura:") + String.format("%10.3f", mApplication.vg.anchoprobeta) + " mm.\n";
            fso.write(comando.getBytes());

            comando = String.format("%10s", "Espesor:") + String.format("%10.3f", mApplication.vg.espesorprobeta) + " mm.\n";
            fso.write(comando.getBytes());

            comando = String.format("%15s", "Ang. disparo:") + String.format("%10.3f", mApplication.vg.angCaidaInicial) + " deg\n";
            fso.write(comando.getBytes());
            comando = "\n";
            fso.write(comando.getBytes());
            comando = "------------------------------------------------------------------------------\n";
            fso.write(comando.getBytes());

            comando = "Kernel ID = " + conexionPlaca.doGetVersionKernel() + "\n";
            ;
            fso.write(comando.getBytes());
            comando = "Ver Sof. control = " + mApplication.APP_VERSION + "\n";
            fso.write(comando.getBytes());
            comando = "------------------------------------------------------------------------------\n";
            fso.write(comando.getBytes());

        } catch (IOException e) {

        } finally {
            try {
                fso.flush();
                fso.close();
            } catch (IOException e1) {
                //e1.printStackTrace();
            }
        }

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void SendFileConfigByEmail() {
        SaveConfigToFile();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jjgarcia@techlabsystems.com"}); //{"email@example.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "File cong charpy");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Fichero de configuración del pendulo charpy");
        //File root = Environment.getExternalStorageDirectory();
        File mFileDir = new File(Environment.getExternalStorageDirectory(), DIR_CONFIG);
        String pathToMyAttachedFile = FILE_CONFIG;
        File file = new File(mFileDir, pathToMyAttachedFile);
        if (!file.exists() || !file.canRead()) {
            Toast.makeText(getContext(), "No puedo acceder al fichero de configuración", Toast.LENGTH_LONG).show();
            return;
        }
        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }

}