package com.techlabsystems.pruebacharpy;

import android.support.v4.app.Fragment;

/**
 * Created by juanjo on 09/10/2017.
 */

public class ConfigWindowActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {

        //return TablaResultadosFragment.newInstance();
        return ConfigWindowFragment.newInstance();
    }

}
