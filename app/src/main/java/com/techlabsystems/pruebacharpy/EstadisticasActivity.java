package com.techlabsystems.pruebacharpy;

import android.support.v4.app.Fragment;

/**
 * Created by juanjo on 17/10/2017.
 */

public class EstadisticasActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {

        setTitle(getString(R.string.TipoEnsayoActivity_title));
        return EstadisticasFragment.newInstance();

    }
}
