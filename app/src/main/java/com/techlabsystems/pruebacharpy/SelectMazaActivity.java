package com.techlabsystems.pruebacharpy;

import android.support.v4.app.Fragment;

/**
 * Created by juanjo on 26/09/2017.
 */

public class SelectMazaActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        Integer TipoEnsayo = (Integer)getIntent().getSerializableExtra(TipoEnsayoFragment.EXTRA_TIPO_ENSAYO);
        Integer FuncionRealizar = (Integer)getIntent().getSerializableExtra(TipoEnsayoFragment.EXTRA_FUNCION_ENSAYO);
        return SelectMazaFragment.newInstance(FuncionRealizar,TipoEnsayo);
    }
}
