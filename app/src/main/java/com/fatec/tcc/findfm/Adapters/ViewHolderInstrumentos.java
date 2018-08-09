package com.fatec.tcc.findfm.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.fatec.tcc.findfm.R;

public class ViewHolderInstrumentos extends RecyclerView.ViewHolder {

    final CheckBox checkInstrumento;
    final Spinner cb_nivelHabilidade;
    final EditText txtQuantidade;

    public ViewHolderInstrumentos(View view) {
        super( view );
        checkInstrumento = view.findViewById( R.id.checkInstumento );
        cb_nivelHabilidade = view.findViewById( R.id.cb_nivelHabilidade );
        txtQuantidade = view.findViewById(R.id.txtQuantidade);
    }
}