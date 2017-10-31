package com.techlabsystems.pruebacharpy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.techlabsystems.utilidades.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by juanjo on 09/10/2017.
 */

public class ConfigWindowFragment extends Fragment {
    private static final String TAG = ConfigWindowFragment.class.getSimpleName();

    TextView fragment_config_window_textView1;
    FloatingActionButton fragment_config_window_ButtonEmail;


    public static ConfigWindowFragment newInstance() {

        ConfigWindowFragment fragment = new ConfigWindowFragment();

        return fragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config_window, container, false);

        fragment_config_window_textView1 = (TextView) view.findViewById(R.id.fragment_config_window_textView1);
        fragment_config_window_ButtonEmail= (FloatingActionButton ) view.findViewById(R.id.fragment_config_window_ButtonEmail);
        // Displaying the user details on the screen

        fragment_config_window_ButtonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendFileConfigByEmail();
            }
        });
        ShowFicheroConfiguracionMaquina(CalibracionFragment.FILE_CONFIG);

        return view;

    }

    public  void SendFileConfigByEmail()
    {

        String email = Utils.getPref(getContext(),"EMAIL");
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {email}); //{"email@example.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "File cong charpy");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Fichero de configuración del péndulo charpy");
        //File root = Environment.getExternalStorageDirectory();
        File mFileDir = new File(Environment.getExternalStorageDirectory(), CalibracionFragment.DIR_CONFIG);
        String pathToMyAttachedFile = CalibracionFragment.FILE_CONFIG;
        File file = new File(mFileDir, pathToMyAttachedFile);
        if (!file.exists() || !file.canRead()) {
            Toast.makeText(getContext(), "No puedo acceder al fichero de configuración", Toast.LENGTH_LONG).show();
            return;
        }
        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }

    void ShowFicheroConfiguracionMaquina(String fichero) {
        //Generar un txt con la configuración
        String stadoSD = Environment.getExternalStorageState();
        /*
        if (!stadoSD.equals(Environment.MEDIA_MOUNTED) &&
                !stadoSD.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(getContext(), "No puedo leer en la memoria externa", Toast.LENGTH_LONG).show();
            return;

        }
*/
        File mFileDir = new File(Environment.getExternalStorageDirectory(), CalibracionFragment.DIR_CONFIG);

        if (!mFileDir.exists()) {
                Log.d(TAG, "No se encuentra el directorio");
                return;
        }
        File mFile = new File(mFileDir, CalibracionFragment.FILE_CONFIG);
        if (!mFile.exists()) {
                Log.d(TAG, "No se encuentra el fichero config");
                return;
        }




        FileReader fr = null;
        try {
            fr = new FileReader(mFile);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (null != line) {
                fragment_config_window_textView1.append(line);
                fragment_config_window_textView1.append("\n");
                line = br.readLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (null != fr) {
                try {
                    fr.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

    }
}
