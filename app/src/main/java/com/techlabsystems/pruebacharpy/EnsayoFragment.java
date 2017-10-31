package com.techlabsystems.pruebacharpy;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.techlabsystems.drivers.DriverCharpyArduino;
import com.techlabsystems.utilidades.Utils;

import java.util.List;
import java.util.regex.Pattern;

import static com.techlabsystems.utilidades.Utils.getPref;


/**
 * Created by Juanjo on 25/09/2017.
 */

public class EnsayoFragment extends Fragment {
    private static final String TAG = EnsayoFragment.class.getSimpleName();
    private static final String ARG_TIPO_ENSAYO = "ARG_TIPO_ENSAYO";
    public static final String ARG_ESCALA_MAZA = "ARG_ESCALA_MAZA";


    public static final int MESSAGE_DISPLAY_DATA_ANGULO = 1;
    public static final int MESSAGE_DISPLAY_DATA_ENERGIA = 2;
    public static final int MESSAGE_DISPLAY_DATA_RESILENCIA = 3;

    public static final int MESSAGE_DISPLAY_DATA_ENERGIA_PERDIDAS = 4;
    public static final int MESSAGE_DISPLAY_DATA_ANG_DISPARO = 5;
    public static final int MESSAGE_DISPLAY_DATA_ANGULO_CERO = 6;
    public static final int MESSAGE_DISPLAY_DATA_ANGULO_MAX = 7;

    public static final int MESSAGE_DISPLAY_DATA_ESPESOR = 10;
    public static final int MESSAGE_DISPLAY_DATA_ANCHURA = 11;
    public static final int MESSAGE_DISPLAY_DATA_AVISO = 12;
    public static final int MESSAGE_DISPLAY_DATA_VELO_IMPACTO = 13;
    public static final int MESSAGE_DISPLAY_DATA_NR_PROBETAS = 14;


    public static final String PROMPT_ANCHURA = "PROMPT_ANCHURA";


    Integer mTipoEnsayo, mEscalaMaza;
    Integer mOldTipoEnsayo;
    Integer mOldEscalaMaza;
    String mOldEstadoMazaCalibrada;
    CharSequence str_ensayo_seleccionado;

    protected Aplicacion mApplication;
    DriverCharpyArduino conexionPlaca;

    LecturasThread mLecturasThread;
    private FragmentActivity myContext;


    CFuncionesEnsayosCharpy.CalPerdidasAsyncTask mCalPerdidas;
    CFuncionesEnsayosCharpy.CalEnergiaAsyncTask mCalEnergia;


    TextView fragment_ensayo_textView1;
    TextView fragment_ensayo_textView2;

    public static EnsayoFragment newInstance(Integer TipoEnsayo, Integer EscalaMaza) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_TIPO_ENSAYO, TipoEnsayo);
        args.putSerializable(ARG_ESCALA_MAZA, EscalaMaza);
        EnsayoFragment fragment = new EnsayoFragment();
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
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ensayo, container, false);


        //--- Conexion con el equipo
        mApplication = (Aplicacion) (getActivity().getApplication());

        if (mApplication.GetSerialPort() == null) {
            mApplication.openSerialPort();

        }
        if (mApplication.GetSerialPort() != null) {
            conexionPlaca = mApplication.getDriver();
        }

        fragment_ensayo_textView1 = (TextView) view.findViewById(R.id.fragment_ensayo_textView1);
        fragment_ensayo_textView2 = (TextView) view.findViewById(R.id.fragment_ensayo_textView2);

        final Button fragment_ensayo_btn_runtest = (Button) view.findViewById(R.id.fragment_ensayo_btn_runtest);
        final Button fragment_ensayo_btn_validar = (Button) view.findViewById(R.id.fragment_ensayo_btn_validar);
        final Button fragment_ensayo_btn_estadisticas = (Button) view.findViewById(R.id.fragment_ensayo_btn_estadisticas);
        final Button fragment_ensayo_btn_calperdidas = (Button) view.findViewById(R.id.fragment_ensayo_btn_calperdidas);


        final Button fragment_ensayo_btn_angcero = (Button) view.findViewById(R.id.fragment_ensayo_btn_angcero);
        final Button fragment_ensayo_btn_angdisparo = (Button) view.findViewById(R.id.fragment_ensayo_btn_angdisparo);

        final Button fragment_ensayo_btn_calperdidas2 = (Button) view.findViewById(R.id.fragment_ensayo_btn_calperdidas2);

        final Button fragment_ensayo_btn_setEspesor = (Button) view.findViewById(R.id.fragment_ensayo_btn_setEspesor);
        final Button fragment_ensayo_btn_setAnchura = (Button) view.findViewById(R.id.fragment_ensayo_btn_setAnchura);

        final Button fragment_ensayo_btn_calvelocImpacto = (Button) view.findViewById(R.id.fragment_ensayo_btn_calvelocImpacto);
        final Button fragment_ensayo_btn_init_lote = (Button) view.findViewById(R.id.fragment_ensayo_btn_init_lote);


        fragment_ensayo_btn_validar.setVisibility(View.INVISIBLE);
        fragment_ensayo_btn_runtest.setVisibility(View.INVISIBLE);
        fragment_ensayo_btn_estadisticas.setVisibility(View.INVISIBLE);
        fragment_ensayo_btn_calperdidas.setVisibility(View.INVISIBLE);


        mOldTipoEnsayo = Integer.parseInt(CListaMazas.getPref("TIPO_ENSAYO"));
        mOldEscalaMaza = Integer.parseInt(CListaMazas.getPref("ESCALA_MAZA"));
        mOldEstadoMazaCalibrada = CListaMazas.getPref("ESTADO_CALIBRACION_MAZA");

        if (mOldEstadoMazaCalibrada == null) {
            mOldEstadoMazaCalibrada = "0";
        }

        if ((mTipoEnsayo == mOldTipoEnsayo) && (mEscalaMaza == mOldEscalaMaza) && (mOldEstadoMazaCalibrada.equals("1"))) {
            fragment_ensayo_btn_validar.setVisibility(View.INVISIBLE);
            fragment_ensayo_btn_runtest.setVisibility(View.VISIBLE);
            if (Aplicacion.vg.nrProbeta > 0) {
                fragment_ensayo_btn_estadisticas.setVisibility(View.VISIBLE);
            } else {
                fragment_ensayo_btn_estadisticas.setVisibility(View.INVISIBLE);
            }
            fragment_ensayo_btn_calperdidas.setVisibility(View.VISIBLE);
        } else {
            fragment_ensayo_btn_validar.setVisibility(View.INVISIBLE);
            fragment_ensayo_btn_runtest.setVisibility(View.INVISIBLE);
            fragment_ensayo_btn_estadisticas.setVisibility(View.VISIBLE);
            fragment_ensayo_btn_calperdidas.setVisibility(View.VISIBLE);

            //Inicializar el contador  de probetas y los datos
            Aplicacion.vg.mProbetas.clear();
            Aplicacion.vg.nrProbeta = 0;
        }


        String sUnidades = Utils.getPref(myContext, "UNIDADES");

        if (sUnidades.equals("1")) {
            Aplicacion.vg.SistemaUnidades = 1;
        } else {
            Aplicacion.vg.SistemaUnidades = 0;
        }

        str_ensayo_seleccionado = "";

        if (mTipoEnsayo == CListaMazas.ISO_CHAR) {
            str_ensayo_seleccionado = getResources().getString(R.string.ensayo_ISO_seleccionado, String.valueOf(mEscalaMaza + 1));
        } else if (mTipoEnsayo == CListaMazas.ASTM_CHAR) {
            str_ensayo_seleccionado = getResources().getString(R.string.ensayo_ASTM_seleccionado, String.valueOf(mEscalaMaza + 1));
        } else if (mTipoEnsayo == CListaMazas.IZOD_CHAR) {
            str_ensayo_seleccionado = getResources().getString(R.string.ensayo_IZOD_seleccionado, String.valueOf(mEscalaMaza + 1));
        }


        //List<Maza> ListaMazas = Aplicacion.vg.mCListaMazas.GetMazas();

        // Cargar datos por defecto de la Maq a la tactil
        conexionPlaca.SeleccionarTipoEnsayo(Aplicacion.vg.TipoEnsayo, Aplicacion.vg.EscalaMaza);

        String strmaza = conexionPlaca.doGetDatosMazaEEPROM(mTipoEnsayo, mEscalaMaza);
        Aplicacion.vg.mMaza = Aplicacion.vg.mCListaMazas.DecodeMazaToStrucMaza(strmaza);
        Aplicacion.vg.mMaza.setCod(mTipoEnsayo);
        Aplicacion.vg.mMaza.setEscala(mEscalaMaza);
        Aplicacion.vg.angCaidaInicial = conexionPlaca.doGetAnguloDisparo();
        Aplicacion.vg.anchoprobeta = conexionPlaca.doLeerAnchoProbeta();
        Aplicacion.vg.espesorprobeta = conexionPlaca.doLeerEspesorProbeta();

        Aplicacion.vg.mMaza.setEMazaCal((float) mApplication.calculosPendulo.CalEnergiaMaza());


        //Maza mMaza = mCListaMazas.getMazaFromEEPROM(mTipoEnsayo,mEscalaMaza);
        CharSequence str_capacida_maza_seleccionada = Float.toString(Aplicacion.vg.mMaza.getEMazaStd());

        getActivity().setTitle(str_ensayo_seleccionado + " (" + str_capacida_maza_seleccionada + " J)");


        if (mApplication.GetSerialPort() != null) {
            conexionPlaca.doBobinaOFF();
            mLecturasThread = new LecturasThread();
            mLecturasThread.start();
        }


        UpdateDisplayUnidades();

        fragment_ensayo_btn_validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Salvar el ensayo y enviarlo por el canal serie2 del arduino

                fragment_ensayo_btn_validar.setVisibility(View.INVISIBLE);
                fragment_ensayo_btn_estadisticas.setVisibility(View.VISIBLE);
                Probeta mProbeta = new Probeta();
                Aplicacion.vg.nrProbeta = Aplicacion.vg.nrProbeta + 1;
                mProbeta.setPos(Aplicacion.vg.nrProbeta);
                mProbeta.setCodEnsayo(Aplicacion.vg.TipoEnsayo);
                mProbeta.setEscala(Aplicacion.vg.EscalaMaza);
                mProbeta.setEnergiaImpacto((float) mApplication.vgram.EImpacto);
                mProbeta.setResilenciaImpacto((float) mApplication.vgram.EKJm2);
                mProbeta.setVelocImpacto((float) mApplication.vgram.velocidadImpacto_sof);
                mProbeta.setAngMax((float) mApplication.vgram.angulo_maximo_pendulo);
                mProbeta.setAnchura(Aplicacion.vg.anchoprobeta);
                mProbeta.setEspesor(Aplicacion.vg.espesorprobeta);


                Aplicacion.vg.mProbetas.add(mProbeta);
                EnviarResultadoToHost(mProbeta);

            }
        });

        fragment_ensayo_btn_estadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveConfigEnsayo();
                Intent intent = new Intent(getActivity(), EstadisticasActivity.class);

                startActivity(intent);
            }
        });


        fragment_ensayo_btn_angcero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float pos;

                conexionPlaca.doHacerCeroExtension();
                pos = conexionPlaca.doLeerExtension();


                String menssage = String.format("%.3f", pos);

                if (getView() != null) {
                    TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_AngCero);
                    lb.setText(menssage);
                }

            }
        });

        fragment_ensayo_btn_angdisparo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Aplicacion.vg.angCaidaInicial = conexionPlaca.doLeerExtension();
                conexionPlaca.doSetAnguloDisparo();

            }
        });


        fragment_ensayo_btn_runtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conexionPlaca.SeleccionarTipoEnsayo(Aplicacion.vg.TipoEnsayo, Aplicacion.vg.EscalaMaza);
                fragment_ensayo_btn_validar.setVisibility(View.INVISIBLE);

                mCalEnergia = new CFuncionesEnsayosCharpy.CalEnergiaAsyncTask(1);
                mCalEnergia.execute(2000); //tiempo de respuesta = 2 s. para leer el resultado

                fragment_ensayo_btn_validar.setVisibility(View.VISIBLE);

            }
        });

        fragment_ensayo_btn_calperdidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                conexionPlaca.SeleccionarTipoEnsayo(Aplicacion.vg.TipoEnsayo, Aplicacion.vg.EscalaMaza);

                mCalPerdidas = new CFuncionesEnsayosCharpy.CalPerdidasAsyncTask(1);
                mCalPerdidas.execute(2000); //tiempo de respuesta = 2 s. para leer el resultado

                CListaMazas.putPref("ESTADO_CALIBRACION_MAZA", "1");
                fragment_ensayo_btn_validar.setVisibility(View.INVISIBLE);
                fragment_ensayo_btn_runtest.setVisibility(View.VISIBLE);
                fragment_ensayo_btn_estadisticas.setVisibility(View.VISIBLE);
                fragment_ensayo_btn_calperdidas.setVisibility(View.VISIBLE);



            }
        });


        fragment_ensayo_btn_init_lote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
                builder.setTitle(getString(R.string.fragment_ensayo_btn_init_lote_msg));   // Título

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        //Inicializar el contador  de probetas y los datos
                        Aplicacion.vg.mProbetas.clear();
                        Aplicacion.vg.nrProbeta = 0;
                        fragment_ensayo_btn_estadisticas.setVisibility(View.INVISIBLE);


                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for CANCEL button here, or leave in blank
                    }
                });
                builder.create().show();

            }
        });

        fragment_ensayo_btn_calvelocImpacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        //*******************
        fragment_ensayo_btn_setEspesor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetEspesor();

            }
        });

        fragment_ensayo_btn_setAnchura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SetAnchura();
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

                case MESSAGE_DISPLAY_DATA_ENERGIA:

                    strIncom = (String) msg.obj;


                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lbConsola = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Energia);
                            lbConsola.setText(menssage);
                        }

                    }

                    break;

                case MESSAGE_DISPLAY_DATA_RESILENCIA:

                    strIncom = (String) msg.obj;


                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lbConsola = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Energia_m2);
                            lbConsola.setText(menssage);
                        }

                    }

                    break;

                case MESSAGE_DISPLAY_DATA_ANGULO:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lbConsola = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Angulo);
                            lbConsola.setText(menssage);
                        }


                    }

                    break;

                case MESSAGE_DISPLAY_DATA_ANGULO_MAX:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lbConsola = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Angulo_max);
                            lbConsola.setText(menssage);
                        }


                    }

                    break;


                case MESSAGE_DISPLAY_DATA_ENERGIA_PERDIDAS:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Calperdidas);
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
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Angdisparo);
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
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_AngCero);
                            lb.setText(menssage);
                        }
                    }

                    break;

                case MESSAGE_DISPLAY_DATA_VELO_IMPACTO:
                    strIncom = (String) msg.obj;

                    if (Utils.isFloatNumber(strIncom)) {
                        float x = Float.parseFloat(strIncom);

                        menssage = String.format("%.3f", x);

                        if (getView() != null) {
                            TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_calVelocImpacto);
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

                case MESSAGE_DISPLAY_DATA_NR_PROBETAS:
                    menssage = (String) msg.obj;


                    if (getView() != null) {
                        TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_nrprobetas);
                        lb.setText(menssage);
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
                    Thread.sleep(50);

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


    private void Lecturas() {

        Message msg;


        if (mCalEnergia != null) {

            if (mCalEnergia.getStatus() == AsyncTask.Status.FINISHED) {
                //pasa
            } else {
                return;
            }
        }

        if (mCalPerdidas != null) {

            if (mCalPerdidas.getStatus() == AsyncTask.Status.FINISHED) {
                //pasa
            } else {
                return;
            }
        }


        float val = conexionPlaca.doLeerExtension();
        double valor = 0f;


        String rta = String.valueOf(val);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_ANGULO, rta);
        mHandler.sendMessage(msg);

        rta = String.valueOf(mApplication.vgram.angulo_maximo_pendulo);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_ANGULO_MAX, rta);
        mHandler.sendMessage(msg);

        valor = mApplication.vgram.EImpacto;
        if (Aplicacion.vg.SistemaUnidades == 1) {
            valor = valor * Aplicacion.J_To_FT_LBS;
        }
        rta = String.valueOf(valor);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_ENERGIA, rta);
        mHandler.sendMessage(msg);

        valor = mApplication.vgram.EKJm2;
        if (Aplicacion.vg.SistemaUnidades == 1) {
            valor = valor * Aplicacion.KJM2_FT_LBF_IN2;
        }
        rta = String.valueOf(valor);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_RESILENCIA, rta);
        mHandler.sendMessage(msg);

        mApplication.vgram.velocidadImpacto_sof = mApplication.calculosPendulo.CalvelocidadImpacto();
        rta = String.valueOf(mApplication.vgram.velocidadImpacto_sof);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_VELO_IMPACTO, rta);
        mHandler.sendMessage(msg);

        val = conexionPlaca.doGetEnergiaPerdidas();
        val = (float) mApplication.vgram.EPerdidas;
        valor = mApplication.vgram.EPerdidas;
        if (Aplicacion.vg.SistemaUnidades == 1) {
            valor = valor * Aplicacion.J_To_FT_LBS;
        }
        rta = String.valueOf(valor);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_ENERGIA_PERDIDAS, rta);
        mHandler.sendMessage(msg);


        val = conexionPlaca.doGetAnguloDisparo();
        rta = String.valueOf(val);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_ANG_DISPARO, rta);
        mHandler.sendMessage(msg);


        //----

        if (Aplicacion.vg.SistemaUnidades == 1){
            // paso MM_TO_PULGADA
            valor = Aplicacion.vg.anchoprobeta * (float)Aplicacion.MM_TO_PULGADA;
        }else{
            valor = Aplicacion.vg.anchoprobeta;
        }

        rta = String.valueOf(valor);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_ANCHURA, rta);
        mHandler.sendMessage(msg);

        if (Aplicacion.vg.SistemaUnidades == 1){
            // paso MM_TO_PULGADA
            valor = Aplicacion.vg.espesorprobeta * (float)Aplicacion.MM_TO_PULGADA;
        }else{
            valor = Aplicacion.vg.espesorprobeta;
        }
        rta = String.valueOf(valor);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_ESPESOR, rta);
        mHandler.sendMessage(msg);

        rta = String.valueOf(Aplicacion.vg.nrProbeta);
        msg = Message.obtain(mHandler, MESSAGE_DISPLAY_DATA_NR_PROBETAS, rta);
        mHandler.sendMessage(msg);


    }


    private void SetAnchura() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(myContext);

        final EditText texto = new EditText(myContext);
        texto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        texto.setRawInputType(Configuration.KEYBOARD_12KEY);
        if (Aplicacion.vg.SistemaUnidades == 1) {
            builder.setTitle(getString(R.string.EnsayoFragment_SetAnchura_Anchura_in));   // Título
        } else {
            builder.setTitle(getString(R.string.EnsayoFragment_SetAnchura_Anchura_mm));   // Título
        }
        builder.setView(texto);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Log.i("texto escrito por usuario", texto.getText().toString());

                String rta = texto.getText().toString();
                try {
                    float valor = Float.parseFloat(rta);

                    if (Aplicacion.vg.SistemaUnidades ==1) {
                        valor = valor * (float) Aplicacion.INCH_TO_MM;
                    }
                    Aplicacion.vg.anchoprobeta = valor;
                    rta = String.format("%.3f", valor);
                    conexionPlaca.doAnchoProbeta(rta);
                    UpdateDisplayDatosMaza();

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
        if (Aplicacion.vg.SistemaUnidades == 1) {
            builder.setTitle(getString(R.string.EnsayoFragment_SetAnchura_Espesor_in));   // Título
        } else {
            builder.setTitle(getString(R.string.EnsayoFragment_SetAnchura_Espesor_mm));   // Título
        }
        builder.setView(texto);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Log.i("texto escrito por usuario", texto.getText().toString());

                String rta = texto.getText().toString();
                try {
                    float valor = Float.parseFloat(rta);

                    if (Aplicacion.vg.SistemaUnidades ==1) {
                        valor = valor * (float) Aplicacion.INCH_TO_MM;
                    }
                    Aplicacion.vg.espesorprobeta = valor;
                    rta = String.format("%.3f", valor);

                    conexionPlaca.doEspesorProbeta(rta);
                    UpdateDisplayDatosMaza();

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


    // Mostrar valores
    private void showAngulo(final String menssage) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (getView() != null) {
                    TextView lbConsola = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Angulo);
                    lbConsola.setText(menssage);
                }
            }
        });
    }


    void UpdateDisplayDatosMaza() {
        String svalor;
        float valor;


        try {
            valor = Aplicacion.vg.anchoprobeta;
            if (Aplicacion.vg.SistemaUnidades == 1){
                // paso las pulgadas a mm
                valor = Aplicacion.vg.anchoprobeta * (float)Aplicacion.MM_TO_PULGADA;
            }else{
                valor = Aplicacion.vg.anchoprobeta;
            }
            svalor = String.format("%.3f", valor);
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Anchura);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

        try {

            if (Aplicacion.vg.SistemaUnidades == 1){
                // paso las pulgadas a mm
                valor = Aplicacion.vg.espesorprobeta * (float)Aplicacion.MM_TO_PULGADA;
            }else{
                valor = Aplicacion.vg.espesorprobeta;
            }
            svalor = String.format("%.3f", valor);
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Espesor);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }
    }



    void UpdateDisplayUnidades() {
        String svalor;



        try {

            svalor =Aplicacion.Energia_label;
            if (Aplicacion.vg.SistemaUnidades == 1){
                svalor = String.format("%s", Aplicacion.Energia_imperial_label);
            }
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_textView1);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

        try {

            svalor =Aplicacion.Energia_m2_label;
            if (Aplicacion.vg.SistemaUnidades == 1){
                svalor = String.format("%s", Aplicacion.Energia_m2_imperial_label);
            }
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_ensayo_textView2);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

        try {

            svalor =getString(R.string.EnsayoFragment_SetAnchura_Espesor_mm);
            if (Aplicacion.vg.SistemaUnidades == 1){
                svalor = getString(R.string.EnsayoFragment_SetAnchura_Espesor_in);
                svalor = String.format("%s", svalor);
            }
            if (getView() != null) {
                Button lb = (Button) getView().findViewById(R.id.fragment_ensayo_btn_setEspesor);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

        try {

            svalor =getString(R.string.EnsayoFragment_SetAnchura_Anchura_mm);
            if (Aplicacion.vg.SistemaUnidades == 1){
                svalor = getString(R.string.EnsayoFragment_SetAnchura_Anchura_in);
                svalor = String.format("%s", svalor);
            }
            if (getView() != null) {
                Button lb = (Button) getView().findViewById(R.id.fragment_ensayo_btn_setAnchura);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

        try {

            svalor =getString(R.string.EnsayoFragment_Perdidas)+"\n("+Aplicacion.Energia_label+")";
            if (Aplicacion.vg.SistemaUnidades == 1){
                svalor = getString(R.string.EnsayoFragment_Perdidas)+"\n("+Aplicacion.Energia_imperial_label+")";
                svalor = String.format("%s", svalor);
            }
            if (getView() != null) {
                Button lb = (Button) getView().findViewById(R.id.fragment_ensayo_btn_calperdidas2);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

        try {

            svalor =getString(R.string.EnsayoFragment_VelImpacto_mm);
            if (Aplicacion.vg.SistemaUnidades == 1){
                svalor =getString(R.string.EnsayoFragment_VelImpacto_in);
            }
            if (getView() != null) {
                Button lb = (Button) getView().findViewById(R.id.fragment_ensayo_btn_calvelocImpacto);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }


    }


    private void showExtension(final String menssage) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getView() != null) {
                    TextView lbConsola = (TextView) getView().findViewById(R.id.fragment_ensayo_tv_Energia);
                    lbConsola.setText(menssage);
                }
            }
        });
    }


    private void EnviarResultadoToHost(Probeta mProveta) {
        float valor;

        valor = mProveta.getEnergiaImpacto();
        String rta = String.format("%.3f", valor);

        valor = mProveta.getResilenciaImpacto();
        rta = rta + "|"+String.format("%.3f", valor);

        conexionPlaca.doSendResultToHost(rta);

    }


    void SaveConfigEnsayo() {
        // Salvar la config al FS de la tactil
        //CListaMazas.SaveMazasToFile();
        //mApplication.vg.mCListaMazas.SaveMazaToFile(mApplication.vg.mMaza);

        String svalor = String.format("%.3f", Aplicacion.vg.anchoprobeta);
        Aplicacion.vg.mCListaMazas.putPref("ANCHURA", svalor);

        svalor = String.format("%.3f", Aplicacion.vg.espesorprobeta);
        CListaMazas.putPref("ESPESOR", svalor);

        svalor = String.format("%.3f", Aplicacion.vg.angCaidaInicial);
        CListaMazas.putPref("ANG_DISPARO", svalor);

        CListaMazas.putPref("TIPO_ENSAYO", String.valueOf(Aplicacion.vg.TipoEnsayo));
        Aplicacion.vg.mCListaMazas.putPref("ESCALA_MAZA", String.valueOf(Aplicacion.vg.EscalaMaza));


        CListaMazas.putPref("ESTADO_CALIBRACION_MAZA", mOldEstadoMazaCalibrada);

        CListaMazas.putPref("ENSAYO_SELECCIONADO", str_ensayo_seleccionado.toString());


    }


    //Registrarla actividad que contiene al fragment
    @Override
    public void onAttach(Activity activity) {
        myContext = (AppCompatActivity) activity;
        super.onAttach(activity);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mApplication.GetSerialPort() == null) {
            mApplication.openSerialPort();

        }
        UpdateDisplayUnidades();



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SaveConfigEnsayo();
        if (mLecturasThread != null)
            mLecturasThread.setRunning(false);

        mLecturasThread = null;


    }

}
