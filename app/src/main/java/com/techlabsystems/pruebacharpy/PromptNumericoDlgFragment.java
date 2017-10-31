package com.techlabsystems.pruebacharpy;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;


/**
 * Created by juanjo on 26/05/2017.
 */

public class PromptNumericoDlgFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "PromptNumericoDlgFragment";
    Context mContext;


    public static PromptNumericoDlgFragment newInstance(String prompt, String ValorActual) {
        PromptNumericoDlgFragment pdf = new PromptNumericoDlgFragment();
        Bundle bundle = new Bundle();
        bundle.putString("prompt", prompt);
        bundle.putString("ValorActual", ValorActual);
        pdf.setArguments(bundle);

        return pdf;
    }

    @Override
    public void onAttach(Activity act) {
        // If the activity we're being attached to has
        // not implemented the OnDialogDoneListener
        // interface, the following line will throw a
        // ClassCastException. This is the earliest we
        // can test if we have a well-behaved activity.
        try {
            OnDialogDoneListener test = (OnDialogDoneListener) act;

        } catch (ClassCastException cce) {
            // Here is where we fail gracefully.
            Log.e(TAG, "Activity is not listening");
        }
        super.onAttach(act);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.setCancelable(true);
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle icicle) {
        View v = inflater.inflate(R.layout.prompt_dialog, container, false);

        TextView tv = (TextView) v.findViewById(R.id.promptmessage);
        tv.setText(getArguments().getString("prompt"));

        TextView tvValorActual = (TextView) v.findViewById(R.id.tvValorActual);
        tvValorActual.setText("Valor actual: "+getArguments().getString("ValorActual"));

        Button dismissBtn = (Button) v.findViewById(R.id.btn_dismiss);
        dismissBtn.setOnClickListener(this);

        Button saveBtn = (Button) v.findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(this);

        TextView input = (TextView) v.findViewById(R.id.inputtext);

        input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);



        return v;
    }

    @Override
    public void onCancel(DialogInterface di) {
        Log.v(TAG, "in onCancel()");
        super.onCancel(di);
    }

    @Override
    public void onDismiss(DialogInterface di) {
        Log.v(TAG, "in onDismiss()");
        super.onDismiss(di);
    }

    public void onClick(View v) {
        OnDialogDoneListener act = (OnDialogDoneListener) mContext;
        if (v.getId() == R.id.btn_save) {
            TextView tv = (TextView) getView().findViewById(R.id.inputtext);
            act.onDialogDone(this.getTag(), false, tv.getText().toString());
            dismiss();
            return;
        }
        if (v.getId() == R.id.btn_dismiss) {
            act.onDialogDone(this.getTag(), true, null);
            dismiss();
            return;
        }

    }


}
