package com.techlabsystems.pruebacharpy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.icu.text.DecimalFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


import static android.app.Activity.RESULT_OK;


/**
 * Created by juanjo on 17/10/2017.
 */

public class EstadisticasFragment  extends Fragment implements MyAdapter.ItemListener {
    private static final String LOG_TAG = EstadisticasFragment.class.getSimpleName();

    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE2 = 2;
    private static final String FILE_REPORT = "FILE_REPORT";
    private static final String FOLDER_REPORT = "FOLDER_REPORT";


    private static final String PATH_USB = "/mnt/usbhost"; //mapeo del USB

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;




    static protected Aplicacion mApplication;
    private FragmentActivity myContext;

    // resultados estadisticos
    float mediaEnergiaImpacto;
    float mediaResilenciaImpacto;
    float stdEnergiaImpacto;
    float stdResilenciaImpacto;

    List<DataModel> input; //datos del Grid
    ArrayList<BarEntry> yVals1 ; //Datos del grafico
    protected BarChart mChart;
    private Typeface mTf;
    private CharSequence str_ensayo_seleccionado ;
    private CalculosEstadisticos mCalculosEstadisticos;

    String strFileReportSource = null;
    String strFolderReportSource = null;

    TextView fragment_estadisticas_media_energia;
    TextView fragment_estadisticas_media_energiam2;
    TextView fragment_estadisticas_std_energia;
    TextView fragment_estadisticas_std_energiam2;




    public static EstadisticasFragment newInstance() {

        EstadisticasFragment fragment = new EstadisticasFragment();
        return fragment;

    }

    @Override
    public void onResume() {
        super.onResume();
        UpdateUI();
        CrearGrafico();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // PARA DEMOS
        CargarConDatosRamdom();
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        mApplication = (Aplicacion) (Aplicacion.getInstance());
        mCalculosEstadisticos = CalculosEstadisticos.newInstance(getContext());

        str_ensayo_seleccionado = mApplication.vg.mCListaMazas.getPref("ENSAYO_SELECCIONADO");

        fragment_estadisticas_media_energia = (TextView) view.findViewById(R.id.fragment_estadisticas_media_energia);
        fragment_estadisticas_media_energiam2 = (TextView) view.findViewById(R.id.fragment_estadisticas_media_energiam2);
        fragment_estadisticas_std_energia = (TextView) view.findViewById(R.id.fragment_estadisticas_std_energia);
        fragment_estadisticas_std_energiam2 = (TextView) view.findViewById(R.id.fragment_estadisticas_std_energiam2);


        Button fragment_estadisticas_button_save = (Button) view.findViewById(R.id.fragment_estadisticas_button_save);
        recyclerView = (RecyclerView) view.findViewById(R.id.recicler_view_resultados);

        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        yVals1 = new ArrayList<>();

        //Cargar datos en la grafica
        input = new ArrayList<>();
        Integer cto = 1;
        for (Probeta probeta : mApplication.vg.mProbetas) {

            DataModel dm;
            if (mApplication.vg.SistemaUnidades == 1) {
                dm = new DataModel(String.format("%3d", cto++), String.format("%4.3f", mApplication.J_To_FT_LBS *  probeta.getEnergiaImpacto()), String.format("%4.3f", mApplication.KJM2_FT_LBF_IN2 *probeta.getResilenciaImpacto()));
            }else{
                dm = new DataModel(String.format("%3d", cto++), String.format("%4.3f", probeta.getEnergiaImpacto()), String.format("%4.3f", probeta.getResilenciaImpacto()));
            }
            input.add(dm);
        }

        //UpdateUI();

        // define an adapter
        mAdapter = new MyAdapter(getContext(), input, this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();



        //Adapta el grafico
        mChart = (BarChart) view.findViewById(R.id.chart1);

        mChart.setMaxVisibleValueCount(200);
        mChart.setPinchZoom(false);
        mChart.setBackgroundColor(Color.WHITE);


        mChart.setExtraTopOffset(-30f);
        mChart.setExtraBottomOffset(10f);
        mChart.setExtraLeftOffset(70f);
        mChart.setExtraRightOffset(70f);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        //CrearGrafico();



        /* reemplazo el grafico por la tabla
        Fragment mTablaResultadosFragment = TablaResultadosFragment.newInstance();

        myContext.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mTablaResultadosFragment)
                .commit();

        */

        fragment_estadisticas_button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //salvar y exportar
                SaveAndExportResultados();
            }
        });




        return view;
    }

    void UpdateUI(){

        //Actualizar textos de los titulos de columnas de la tabla
        TextView fragment_estadisticas_textView2 = (TextView) getView().findViewById(R.id.fragment_estadisticas_textView2); //J
        TextView fragment_estadisticas_textView3 = (TextView) getView().findViewById(R.id.fragment_estadisticas_textView3); // kJ/m2

        String svalor;
        float valor;

        UpdateCalculos();


        try {
            if (mApplication.vg.SistemaUnidades == 1) {
                svalor = mApplication.Energia_imperial_label;
            }else{
                svalor = mApplication.Energia_label;
            }

            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_estadisticas_textView2);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

        try {
            if (mApplication.vg.SistemaUnidades == 1) {
                svalor = mApplication.Energia_m2_imperial_label;
            }else{
                svalor = mApplication.Energia_m2_label;
            }

            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_estadisticas_textView3);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }


        try {
            if (mApplication.vg.SistemaUnidades == 1) {
                valor = mediaEnergiaImpacto * (float)mApplication.J_To_FT_LBS ;
            }else{
                valor = mediaEnergiaImpacto;
            }
            svalor = String.format("%.3f", valor);
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_estadisticas_media_energia);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }

        try {

            if (mApplication.vg.SistemaUnidades == 1) {
                valor = mediaResilenciaImpacto * (float)mApplication.KJM2_FT_LBF_IN2 ;
            }else{
                valor = mediaResilenciaImpacto;
            }
            svalor = String.format("%.3f", valor);

            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_estadisticas_media_energiam2);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }


        try {

            if (mApplication.vg.SistemaUnidades == 1) {
                valor = stdEnergiaImpacto * (float)mApplication.J_To_FT_LBS ;
            }else{
                valor = stdEnergiaImpacto;
            }
            svalor = String.format("%.3f", valor);
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_estadisticas_std_energia);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }


        try {

            if (mApplication.vg.SistemaUnidades == 1) {
                valor = stdResilenciaImpacto * (float)mApplication.KJM2_FT_LBF_IN2 ;
            }else{
                valor = stdResilenciaImpacto;
            }
            svalor = String.format("%.3f", valor);
            if (getView() != null) {
                TextView lb = (TextView) getView().findViewById(R.id.fragment_estadisticas_std_energiam2);
                lb.setText(svalor);
            }

        } catch (Exception e) {

        }


    }

    void UpdateCalculos()
    {
        mediaEnergiaImpacto = mCalculosEstadisticos.getVmedioEnergiaImpacto(mApplication.vg.mProbetas);
        mediaResilenciaImpacto= mCalculosEstadisticos.getVmedioResilenciaImpacto(mApplication.vg.mProbetas);

        stdEnergiaImpacto = mCalculosEstadisticos.getStdEnergiaImpacto(mApplication.vg.mProbetas);
        stdResilenciaImpacto= mCalculosEstadisticos.getStdResilenciaImpacto(mApplication.vg.mProbetas);


    }

    void CrearGrafico(){

        float valor;

        yVals1.clear();

        //LimitLine ll = new LimitLine(mediaEnergiaImpacto, "x: "+ String.format("%.3f", mediaEnergiaImpacto)  + " | std: " +String.format("%.3f", stdEnergiaImpacto)  );
        if (mApplication.vg.SistemaUnidades == 1) {
            valor = mediaEnergiaImpacto * (float)mApplication.J_To_FT_LBS ;
        }else{
            valor = mediaEnergiaImpacto;
        }

        LimitLine ll = new LimitLine(valor, "");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(1f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(16f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setTextSize(13f);

        xAxis.setLabelCount(10); //?
        xAxis.setCenterAxisLabels(false); // si lo pongo a true no lo centra con pantalla apaisada.
        xAxis.setGranularity(1f);

        YAxis left = mChart.getAxisLeft();
        left.setDrawLabels(false);
        left.setSpaceTop(25f);
        left.setSpaceBottom(25f);
        left.setDrawAxisLine(false);
        left.setDrawGridLines(false);
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(Color.GRAY);
        left.setZeroLineWidth(0.7f);
        left.setAxisMinimum(0f);
        left.setCenterAxisLabels(false);
        left.getLimitLines().clear();
        left.addLimitLine(ll);





        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(true);

        int i=1;

        for (Probeta probeta : mApplication.vg.mProbetas) {
            if (mApplication.vg.SistemaUnidades == 1) {
                valor = probeta.getEnergiaImpacto()* (float)mApplication.J_To_FT_LBS ;
            }else{
                valor = probeta.getEnergiaImpacto();
            }
            yVals1.add(new BarEntry(i++,valor));
        }




        BarDataSet dataset1;
        dataset1 = new BarDataSet(yVals1,getString(R.string.EstadisticasActivity_Lote));
        dataset1.setDrawIcons(true);
        dataset1.setDrawValues(false);

        dataset1.setColors(ColorTemplate.MATERIAL_COLORS);
        //dataset1.setColor(Color.rgb(0,155,0));

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(dataset1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(mTf);

        data.setBarWidth(1f);



        //get the height and width of the device
        DisplayMetrics ds = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(ds);
        int width = ds.widthPixels;
        int height = ds.heightPixels;

        //set the custom description postion
        Description description = new Description();
        description.setText(str_ensayo_seleccionado.toString());
        description.setTextSize(13f);
        description.setPosition(width - 500,height - 130);
        mChart.setDescription(description);

        /*
        Description description = new Description();
        description.setText(str_ensayo_seleccionado.toString());
        description.setTextSize(20f);
        description.setPosition(700f,30f);
        */

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);



        //mChart.animateY(300);
        mChart.setDescription(description);


        mChart.setData(data);
        mChart.invalidate();



    }


    void CargarConDatosRamdom(){
        //Cargar de datos la lista de probetas
        mApplication.vg.nrProbeta = 0;
        mApplication.vg.mProbetas.clear();
        for (int i = 0; i < 5; i++) {
            double val1 = Math.random() * 6 + 1;

            Probeta mProbeta = new Probeta();
            mProbeta.setPos(i+1);
            mProbeta.setEnergiaImpacto((float)val1);
            mProbeta.setResilenciaImpacto((float)(val1 * 1.567));
            mProbeta.setAngMax((float)(Math.random() * 150));
            mProbeta.setEscala(0);
            mProbeta.setVelocImpacto(7.89f);


            mApplication.vg.mProbetas.add(mProbeta);
            mApplication.vg.nrProbeta++;

        }



    }


    //Esta funcion se llama cada vez que se aule un dato en el grid
    @Override
    public void onDeleteItem(DataModel item) {
        int i = Integer.parseInt(item.mIndice.trim());

        mApplication.vg.mProbetas.remove(i-1);
        ordenaResultados(mApplication.vg.mProbetas);
        renum(mApplication.vg.mProbetas);

        UpdateUI();
        CrearGrafico();
    }


    static List<Probeta>  ordenaResultados(List<Probeta>  arreglo)
    {

        Probeta k=null;
        for(int i=1;i<arreglo.size();i++)
        {
            for(int j=0;j<arreglo.size()-i;j++)
            {
                if(  arreglo.get(j).getPos() >  arreglo.get(j+1).getPos() ) {
                    k=arreglo.get(j+1);
                    arreglo.set(j+1,arreglo.get(j));
                    arreglo.set(j,k);
                }
            }
        }


        return arreglo;

    }

    public static void renum(List<Probeta>  arreglo) {
        for(int i=0;i< arreglo.size();i++)
        {
            String nr = String.format("%3d", i+1);
            Probeta dm = arreglo.get(i);
            dm.setPos( i+1);
            arreglo.set(i,dm);
        }
    }


    private void SaveAndExportResultados(){

        File charpyFolder = new File(Environment.getExternalStorageDirectory(), "charpy");

        if (!charpyFolder.exists()) {
            charpyFolder.mkdir();
            Log.i(LOG_TAG, "charpy Folder Directory created");
        }



        //Create time stamp
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        if (mApplication.vg.SistemaUnidades == 1) {
            strFileReportSource = timeStamp + "_imp.txt";
        }else{
            strFileReportSource = timeStamp + "_met.txt";
        }
        strFolderReportSource = charpyFolder.toString();

        File fileReport = new File(charpyFolder.getAbsolutePath(), strFileReportSource);

        Boolean err = false;
        String comando;
        try {
            FileOutputStream fOut = new FileOutputStream(fileReport, false);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            //cabecera
            /*
            comando = String.format("%10s", "TEST") + "  " +String.format("%10s", "ENERGIA") + "  " + String.format("%10s", "RESILENCIA") + "" + String.format("%10s", "Ang Max") + " " + String.format("%10s", "ESCALA")
                    + "" + String.format("%10s", "VELOC")  + "\n";
            osw.write(comando);
            comando = String.format("%10s", "    ") + "  " +String.format("%10s", "   J   ") + "  " + String.format("%10s", "   kJ/m2  ") + "" + String.format("%10s", "   g   ") + "  " + String.format("%10s", "      ")
                    + "" + String.format("%10s", "m/s.")  + "\n";
            osw.write(comando);
            */
            //comando = String.format("%10s", "TEST") + "  " +String.format("%10s", "ENERGIA") + "  " + String.format("%10s", "RESILENCIA") + "\n";
            //comando = String.format("%10s", "    ") + "  " + String.format("%10s", "   J   ") + "  " + String.format("%10s", "   kJ/m2  ") + "\n";
            comando = String.format("%10s", "TEST") + "  " +String.format("%10s", getString(R.string.ENERGIA)) + "  " + String.format("%10s", getString(R.string.RESILENCIA)) + "\n";
            osw.write(comando);
            if (mApplication.vg.SistemaUnidades == 1) {
                comando = String.format("%10s", "    ") + "  " + String.format("%10s", mApplication.Energia_imperial_label) + "  " + String.format("%10s", mApplication.Energia_m2_imperial_label_asci) + "\n";
            }else{
                comando = String.format("%10s", " ") + " " + String.format("%10s", mApplication.Energia_label) + " " + String.format("%10s", mApplication.Energia_m2_label_asci) + "\n";
            }
            osw.write(comando);
            comando="----------------------------------\n";
            osw.write(comando);

            Integer j=1;

            /*
            for (Probeta probeta : mApplication.vg.mProbetas) {

                comando =  String.format("%10d", probeta.getPos()) + " " + String.format("%10.3f", probeta.getEnergiaImpacto()) + " " + String.format("%10.3f", probeta.getResilenciaImpacto())
                        + " " + String.format("%10.3f", probeta.getAngMax()) + " " + String.format("%10d", probeta.getEscala()) + " " + String.format("%10.3f", probeta.getVelocImpacto()) + "\n";
                osw.write(comando);
                j++;
            }
            */
            for (Probeta probeta : mApplication.vg.mProbetas) {

                if (mApplication.vg.SistemaUnidades == 1) {
                    comando = String.format("%10d", probeta.getPos()) + " " + String.format("%10.3f", (float)mApplication.J_To_FT_LBS * probeta.getEnergiaImpacto()) + " " + String.format("%10.3f", (float)mApplication.KJM2_FT_LBF_IN2 * probeta.getResilenciaImpacto()) + "\n";
                }else{
                    comando = String.format("%10d", probeta.getPos()) + " " + String.format("%10.3f", probeta.getEnergiaImpacto()) + " " + String.format("%10.3f", probeta.getResilenciaImpacto()) + "\n";
                }
                osw.write(comando);
                j++;
            }
            comando="----------------------------------\n";
            osw.write(comando);

            // resultados estadisticos


            //Valores estadisticos
            if (mApplication.vg.SistemaUnidades == 1) {
                comando = String.format("%10s", "Media") + " " + String.format("%10.3f", (float) mApplication.J_To_FT_LBS * mediaEnergiaImpacto) + " " + String.format("%10.3f", (float) mApplication.KJM2_FT_LBF_IN2 * mediaResilenciaImpacto) + "\n";
                osw.write(comando);
                comando = String.format("%10s", "Stdev") + " " + String.format("%10.3f", (float) mApplication.J_To_FT_LBS * stdEnergiaImpacto) + " " + String.format("%10.3f", (float) mApplication.KJM2_FT_LBF_IN2 * stdResilenciaImpacto) + "\n";
                osw.write(comando);
            }else{
                comando = String.format("%10s", "Media") + " " + String.format("%10.3f",  mediaEnergiaImpacto) + " " + String.format("%10.3f",  mediaResilenciaImpacto) + "\n";
                osw.write(comando);
                comando = String.format("%10s", "Stdev") + " " + String.format("%10.3f", stdEnergiaImpacto) + " " + String.format("%10.3f", stdResilenciaImpacto) + "\n";
                osw.write(comando);
            }

            osw.flush();
            osw.close();

            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "SaveAndExportResultados error", Toast.LENGTH_LONG).show();
            err = true;
        }
        Log.i("File Path:", fileReport.getPath());



        // Seleccionar el destino por parte del usuario
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        //intent.setType("application/pdf");
        //intent.setType("*/*");
        intent.setType("text/plain");

        //intent.setType(DocumentsContract.Document.MIME_TYPE_DIR);//For API 19+
        intent.putExtra(Intent.EXTRA_TITLE, fileReport.getName());
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        startActivityForResult(intent, REQUEST_CODE);



    }


    private void SaveAndExportResultados_cvs(){

        File charpyFolder = new File(Environment.getExternalStorageDirectory(), "charpy");

        if (!charpyFolder.exists()) {
            charpyFolder.mkdir();
            Log.i(LOG_TAG, "charpy Folder Directory created");
        }



        //Create time stamp
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        if (mApplication.vg.SistemaUnidades == 1) {
            strFileReportSource = timeStamp + "_imp.csv";
        }else{
            strFileReportSource = timeStamp + "_met.csv";
        }
        strFolderReportSource = charpyFolder.toString();

        File fileReport = new File(charpyFolder.getAbsolutePath(), strFileReportSource);

        Boolean err = false;
        String comando;
        try {
            FileOutputStream fOut = new FileOutputStream(fileReport, false);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            //cabecera
            comando = String.format("%10s", getString(R.string.ENERGIA)) + ";" + String.format("%10s", getString(R.string.RESILENCIA)) + ";" + String.format("%10s", "ANG_MAX") + ";" + String.format("%10s", "ESCALA")
                    + ";" + String.format("%10s", "VELOC")  + "\n";
            osw.write(comando);

            Integer j=1;

            for (Probeta probeta : mApplication.vg.mProbetas) {

                comando =  String.format("%10d", j) + ";" + String.format("%10.3f", probeta.getEnergiaImpacto()) + ";" + String.format("%10.3f", probeta.getResilenciaImpacto())
                        + ";" + String.format("%10.3f", probeta.getAngMax()) + ";" + String.format("%10d", probeta.getEscala()) + ";" + String.format("%10.3f", probeta.getVelocImpacto()) + "\n";
                osw.write(comando);
                j++;
            }

            osw.flush();
            osw.close();

            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "SaveAndExportResultados error", Toast.LENGTH_LONG).show();
            err = true;
        }
        Log.i("File Path:", fileReport.getPath());



        // Seleccionar el destino por parte del usuario
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        //intent.setType("application/pdf");
        //intent.setType("*/*");
        intent.setType("text/csv");

        //intent.setType(DocumentsContract.Document.MIME_TYPE_DIR);//For API 19+
        intent.putExtra(Intent.EXTRA_TITLE, fileReport.getName());
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        startActivityForResult(intent, REQUEST_CODE2);



    }


    public static void copyFile(File oldLocation, File newLocation) throws IOException {
        if (oldLocation.exists()) {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(oldLocation));
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(newLocation, false));
            try {
                byte[] buff = new byte[8192];
                int numChars;
                while ((numChars = reader.read(buff, 0, buff.length)) != -1) {
                    writer.write(buff, 0, numChars);
                }
            } catch (IOException ex) {
                throw new IOException("IOException when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
            } finally {
                try {
                    if (reader != null) {
                        writer.close();
                        reader.close();
                    }
                } catch (IOException ex) {
                    Log.e(LOG_TAG, "Error closing files when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
                }
            }
        } else {
            throw new IOException("Old location does not exist when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
        }
    }


    private void copyFile(File src, Uri destUri) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(src));
            bos = new BufferedOutputStream(getActivity().getContentResolver().openOutputStream(destUri));

            byte[] buf = new byte[8192];
            int numChars;

            while ((numChars = bis.read(buf, 0, buf.length)) != -1) {
                bos.write(buf, 0, numChars);
            }


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new IOException("IOException when transferring " + src.getPath() + " to " + getActivity().getContentResolver().toString());
        } finally {
            try {

                if (bis != null) bis.close();
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        File fileToCopy = null;

        if (requestCode == REQUEST_CODE || requestCode == REQUEST_CODE2 ) {
            if (resultCode != RESULT_OK) return;

            try {
                fileToCopy = new File(strFolderReportSource,strFileReportSource);
                copyFile(fileToCopy, data.getData());
                Toast.makeText(getContext(), "Fichero copiado con exito", Toast.LENGTH_LONG).show();


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error transferring files " + fileToCopy.getPath() );
                Toast.makeText(getContext(), "Error transferring files", Toast.LENGTH_LONG).show();
            }
        }

    }




}
