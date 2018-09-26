package com.fatec.tcc.findfm.Views.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Spinner;

import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.NivelHabilidade;
import com.fatec.tcc.findfm.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdapterInstrumentos extends RecyclerView.Adapter<AdapterInstrumentos.ViewHolder> {

    private List<Instrumento> instrumentos = new ArrayList<>();
    private Set<Instrumento> instrumentosUsuario = new HashSet<>();

    private SparseBooleanArray itemStateArray= new SparseBooleanArray();
    private Context context;

    public AdapterInstrumentos() {

    }

    public AdapterInstrumentos(List<Instrumento> instrumentos, Context context){
        this.instrumentos = instrumentos;
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

    public void setInstrumentos(List<Instrumento> instrumentos, Context context){
        this.instrumentos = instrumentos;
        this.context = context;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        CheckedTextView checkInstrumento;
        Spinner cb_nivelHabilidade;

        ViewHolder(View itemView) {
            super(itemView);
            checkInstrumento = itemView.findViewById( R.id.checkInstumento );
            cb_nivelHabilidade = itemView.findViewById( R.id.cb_nivelHabilidade );

            itemView.setOnClickListener(v -> {
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

            Instrumento instrumento = instrumentos.get(position);

            checkInstrumento.setText(instrumento.getNome());

            cb_nivelHabilidade.setAdapter(
                    new ArrayAdapter<>(context, R.layout.simple_custom_list, NivelHabilidade.values()));

            cb_nivelHabilidade.setSelection(instrumento.getNivelHabilidade().getCodigo() -1);

            for (Instrumento inst : instrumentosUsuario) {
                if (inst.getNome().equals(instrumento.getNome())) {
                    checkInstrumento.setChecked(true);
                    cb_nivelHabilidade.setSelection(inst.getNivelHabilidade().getCodigo() -1);
                }
            }

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

        }
    }

    public AdapterInstrumentos setInstrumentosUsuario(List<Instrumento> instrumentosUsuario) {
        Set<Instrumento> instrumentosSet = new HashSet<>();
        instrumentosSet.addAll(instrumentosUsuario);
        this.instrumentosUsuario = instrumentosSet;
        return this;
    }
}