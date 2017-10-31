package com.techlabsystems.pruebacharpy;

import android.support.v4.app.Fragment;

/**
 * Created by juanjo on 28/09/2017.
 */

public class CalibracionActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Integer TipoEnsayo = (Integer)getIntent().getSerializableExtra(SelectMazaFragment.EXTRA_TIPO_ENSAYO);
        Integer EscalaMaza = (Integer)getIntent().getSerializableExtra(SelectMazaFragment.EXTRA_ESCALA_MAZA);
        return CalibracionFragment.newInstance(TipoEnsayo, EscalaMaza);

    }
}
