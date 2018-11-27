package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.Model.Http.Response.User;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Adapters.AdapterTrabalhos;
import com.fatec.tcc.findfm.databinding.ActivityTrabalhosListaBinding;

import java.util.Map;
import java.util.Objects;

public class Trabalhos_Fragment extends Fragment {

    private TelaPrincipal activity;
    private String URL;
    private Musico usuario;
    private ActivityTrabalhosListaBinding binding;

    public Trabalhos_Fragment(){}

    @SuppressLint("ValidFragment")
    public Trabalhos_Fragment(TelaPrincipal activity, String URL){
        this.activity = activity;
        this.usuario = new Musico();
        this.URL = URL;
        getUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_trabalhos_lista, container, false);
        try {
            activity.getSupportActionBar().setTitle("Meus Trabalhos");
            activity.getOptionsMenu().getItem(1).setVisible(false);
            activity.getOptionsMenu().getItem(2).setVisible(false);
        } catch (Exception e){
            e.printStackTrace();
        }

        binding.adicionarTrabalho.setOnClickListener(this.adicionar_trabalho_click());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.setTelaAtual("TRABALHOS");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.hideSoftKeyboard(activity);
        switch (item.getItemId()){
            case R.id.action_refresh:
                getUser();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public View.OnClickListener adicionar_trabalho_click(){
        return view -> Util.open_form(activity, CriarTrabalho.class);
    }

    public void getUser() {
        JsonTypedRequest<Usuario, ResponseBody, ErrorResponse> registrarRequest = new JsonTypedRequest<>
                (       activity,
                        Request.Method.GET,
                        Usuario.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        URL,
                        null,
                        (ResponseBody response) ->
                        {
                            activity.getDialog().hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                User user= JsonUtils.jsonConvert(((Map<String, Object>) response.getData()).get("usuario"), User.class);
                                this.usuario.setId(user.getId());
                                this.usuario.setTrabalhos(user.getTrabalhos());
                                if(!this.usuario.getTrabalhos().isEmpty()) {
                                    binding.lbSemTrabalho.setVisibility(View.GONE);
                                    binding.listaTrabalhos.setVisibility(View.VISIBLE);
                                    binding.listaTrabalhos.setLayoutManager(new LinearLayoutManager(activity));
                                    binding.listaTrabalhos.setAdapter(new AdapterTrabalhos(this.usuario.getTrabalhos(), activity, (FindFM.getUsuario().getId().equals(this.usuario.getId()))));
                                    DividerItemDecoration itemDecorator = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
                                    itemDecorator.setDrawable(Objects.requireNonNull(activity.getDrawable(R.drawable.divider)));
                                    binding.listaTrabalhos.addItemDecoration(itemDecorator);
                                } else {
                                    binding.lbSemTrabalho.setVisibility(View.VISIBLE);
                                }
                                //binding.executePendingBindings();
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            activity.getDialog().hide();
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Response]GetTrab", errorResponse.getMessage());
                                mensagem = errorResponse.getMessage();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(activity, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        },
                        (VolleyError errorResponse) ->
                        {
                            activity.getDialog().hide();
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Volley]GetTrab", errorResponse.getMessage());
                                errorResponse.printStackTrace();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(activity, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        }
                );
        registrarRequest.execute();
        activity.getDialog().show();
    }
}
