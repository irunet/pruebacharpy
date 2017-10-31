package com.techlabsystems.pruebacharpy;

import android.support.v4.app.Fragment;

/**
 * Created by juanjo on 26/09/2017.
 */

public class TipoEnsayoActivity extends  SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        setTitle(getString(R.string.TipoEnsayoActivity_title));
        Integer FuncionRealizar = (Integer)getIntent().getSerializableExtra(MainFragment.EXTRA_FUNCION_ENSAYO);

        return TipoEnsayoFragment.newInstance(FuncionRealizar);
    }
}
