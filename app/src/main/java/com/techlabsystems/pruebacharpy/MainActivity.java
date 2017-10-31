package com.techlabsystems.pruebacharpy;


import android.support.v4.app.Fragment;
import android.content.Intent;
import android.view.MenuItem;


public class MainActivity extends SingleFragmentActivity implements MainFragment.Callbacks {


    @Override
    protected Fragment createFragment() {

        //return TablaResultadosFragment.newInstance();
        return MainFragment.newInstance();
    }



/*
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        if (item.getItemId() == R.id.menu_setup_preferencias)
        {
            Intent intent = new Intent()
                    .setClass(this, PreferenceActivityEnsayo.class);
            this.startActivityForResult(intent, 0);
        }
        return true;
    }

*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_setup_preferencias:

                intent = new Intent()
                       .setClass(this, PreferenceActivityEnsayo.class);
                startActivity(intent);
                return true;
            case R.id.menu_ficheros:

                intent = new Intent()
                        .setClass(this, ExplorerActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data)
    {
        super.onActivityResult(reqCode, resCode, data);
        setOptionText();
    }

    private void setOptionText()
    {

    }




    @Override
    public void onBtnSelected(int btn) {
/*
        if (btn == R.id.fragment_main_btn_ensayo) {
            Fragment newFragmentTipoEnsayo = TipoEnsayoFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, newFragmentTipoEnsayo)
                    .commit();
        }
        */
/*
        if (btn == R.id.fragment_tipo_ensayo_btn_iso) {
            Fragment newFragmentEnsayo = EnsayoFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, newFragmentEnsayo)
                    .commit();
        }
*/


    }
}
