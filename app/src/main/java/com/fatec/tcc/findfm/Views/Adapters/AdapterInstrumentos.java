package com.fatec.tcc.findfm.Views.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.toolbox.StringRequest;
import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.NivelHabilidade;
import com.fatec.tcc.findfm.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdapterInstrumentos extends RecyclerView.Adapter<AdapterInstrumentos.ViewHolder> {

    private List<Instrumento> instrumentos = new ArrayList<>();
    private Set<Instrumento> instrumentosUsuario = new HashSet<>();
    private String tipoTela;

    SparseBooleanArray itemStateArray= new SparseBooleanArray();
    HashMap<Integer, Integer> itemQtdArray= new HashMap<>();
    Context context;

    public AdapterInstrumentos() {
    }

    public AdapterInstrumentos(List<Instrumento> instrumentos, String tipoTela, Context context){
        this.instrumentos = instrumentos;
        this.tipoTela = tipoTela;
        this.context = context;
    }

    public List<Instrumento> getInstrumentos(){
        return new ArrayList<>(this.instrumentosUsuario);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForItem = R.layout.view_instumentos;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutForItem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (instrumentos == null) {
            return 0;
        }
        return instrumentos.size();
    }

    public void setInstrumentos(List<Instrumento> instrumentos, String tipoTela, Context context){
        this.instrumentos = instrumentos;
        this.tipoTela = tipoTela;
        this.context = context;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        CheckedTextView checkInstrumento;
        Spinner cb_nivelHabilidade;
        EditText txtQuantidade;

        ViewHolder(View itemView) {
            super(itemView);
            checkInstrumento = itemView.findViewById( R.id.checkInstumento );
            cb_nivelHabilidade = itemView.findViewById( R.id.cb_nivelHabilidade );
            txtQuantidade = itemView.findViewById(R.id.txtQuantidade);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    Instrumento instrumento = instrumentos.get(adapterPosition);

                    if (!itemStateArray.get(adapterPosition, false)) {
                        checkInstrumento.setChecked(true);
                        itemStateArray.put(adapterPosition, true);
                        instrumentosUsuario.add(instrumento);
                    }
                    else {
                        checkInstrumento.setChecked(false);
                        itemStateArray.put(adapterPosition, false);
                        instrumentosUsuario.remove(instrumento);
                    }
                }
            });
        }

        void bind(int position) {
            // use the sparse boolean array to check
            checkInstrumento.setCheckMarkTintList(ColorStateList.valueOf(Color.WHITE));
            if (!itemStateArray.get(position, false)) {
                checkInstrumento.setChecked(false);
            }
            else {
                checkInstrumento.setChecked(true);
            }
            checkInstrumento.setText(String.valueOf(instrumentos.get(position).getNome()));

            if(itemQtdArray.get(position) == null)
                txtQuantidade.setText("0");
            else
                txtQuantidade.setText(itemQtdArray.get(position).toString());

            Instrumento instrumento = instrumentos.get(position);

            switch (tipoTela){
                case "MUSICO":
                    txtQuantidade.setVisibility(View.INVISIBLE);
                    break;
                case "BANDA":
                    txtQuantidade.setVisibility(View.VISIBLE);
                    break;
            }

            checkInstrumento.setText(instrumento.getNome());
            cb_nivelHabilidade.setAdapter(
                    new ArrayAdapter<>(context, R.layout.simple_custom_list, NivelHabilidade.values()));
            cb_nivelHabilidade.setSelection(instrumento.getNivelHabilidade().getCodigo() -1);


            cb_nivelHabilidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Set<Instrumento> toRemove = new HashSet<>();
                    Set<Instrumento> toAdd = new HashSet<>();
                    for(Instrumento inst : instrumentosUsuario){
                        if(inst.equals(instrumento)){
                            inst.setNivelHabilidade(NivelHabilidade.from(position + 1));
                            toRemove.add(instrumento);
                            toAdd.add(inst);
                        }
                    }
                    instrumentosUsuario.removeAll(toRemove);
                    instrumentosUsuario.addAll(toAdd);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
            txtQuantidade.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    int adapterPosition = getAdapterPosition();
                    Instrumento instrumento = instrumentos.get(adapterPosition);

                    if(!txtQuantidade.getText().toString().isEmpty()) {
                        Integer qtd = Integer.parseInt(txtQuantidade.getText().toString());
                        instrumentosUsuario.remove(instrumento);
                        instrumento.setQuantidade(qtd);
                        instrumentosUsuario.add(instrumento);
                        itemQtdArray.put(adapterPosition, qtd);
                    }
                    else{
                        instrumentosUsuario.remove(instrumento);
                        instrumento.setQuantidade(0);
                        instrumentosUsuario.add(instrumento);
                        itemQtdArray.put(adapterPosition, 0);
                    }
                }
            });

        }
    }
}