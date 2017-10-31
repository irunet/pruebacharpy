package com.techlabsystems.pruebacharpy;

/**
 * Created by Juanjo on 14/09/2017.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    Context mContext;
    protected ItemListener mListener;

    private static List<DataModel> values;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView indice;
        public TextView col1;
        public TextView col2;
        public ImageButton imageButton;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            indice = (TextView) v.findViewById(R.id.indice);
            col1 = (TextView) v.findViewById(R.id.col1);
            col2 = (TextView) v.findViewById(R.id.col2);
            imageButton = (ImageButton) v.findViewById(R.id.buttonDelete);
        }
    }

    public void add(int position, DataModel item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public  void remove(int position) {
        values.remove(position);

        ordenaResultados(values);
        renum(values);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public static void renum(List<DataModel>  arreglo) {
        for(int i=0;i< arreglo.size();i++)
        {
            String nr = String.format("%3d", i+1);
            DataModel dm = arreglo.get(i);
            dm.mIndice = nr;
            arreglo.set(i,dm);
        }
    }


    static List<DataModel>  ordenaResultados(List<DataModel>  arreglo)
    {

        DataModel k=null;
        for(int i=1;i<arreglo.size();i++)
        {
            for(int j=0;j<arreglo.size()-i;j++)
            {
                if(  Integer.parseInt( arreglo.get(j).mIndice.trim()) >  Integer.parseInt( (arreglo.get(j+1).mIndice).trim())) {
                    k=arreglo.get(j+1);
                    arreglo.set(j+1,arreglo.get(j));
                    arreglo.set(j,k);
                }
            }
        }


        return arreglo;

    }



    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, List<DataModel> myDataset, ItemListener itemListener) {

        mContext = context;
        values = myDataset;
        mListener=itemListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        DataModel mDato = values.get(position);
        //poner colores alternativos
        if (position%2 > 0) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
        holder.indice.setText(mDato.mIndice);
        holder.col1.setText(mDato.mCol1);
        holder.col2.setText(mDato.mCol2);

        holder.imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final DataModel mDato = values.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);


                builder.setTitle("¿Desea borrar los datos de la probeta: " +  mDato.mIndice +" ?");   // Título

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        remove(position);
                        if (mListener != null) {
                            mListener.onDeleteItem(mDato);
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
        });


    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

    public interface ItemListener {
        void onDeleteItem(DataModel item);
    }

}