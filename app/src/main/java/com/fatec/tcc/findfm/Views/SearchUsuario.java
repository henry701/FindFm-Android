package com.fatec.tcc.findfm.Views;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.Model.Http.Response.User;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Views.Adapters.AdapterUsuario;
import com.fatec.tcc.findfm.databinding.ActivitySearchUsuarioBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SearchUsuario extends AppCompatActivity {
    
    private ActivitySearchUsuarioBinding binding;
    private ProgressDialog dialog;
    private boolean apenasMusicos = true;
    private Set<Musico> userList = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FindFM.setTelaAtual("BUSCANDO_USER");
        try {
            getSupportActionBar().setTitle("Buscar usuário");
        } catch (Exception e){
            e.printStackTrace();
        }
        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(getDrawable(R.drawable.divider)));
        binding.listaUsuarios.setLayoutManager(new LinearLayoutManager(this));
        binding.listaUsuarios.addItemDecoration(itemDecorator);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_usuario);

        Bundle param = getIntent().getBundleExtra("BuscaUsuario");
        if (param != null) {
            if (!param.isEmpty()) {
                apenasMusicos = !param.getString("origem").equals("perfil");
            }
        }

        dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        binding.btnBusca.setOnClickListener(v -> {
            if(binding.txtBusca.getText() != null && !binding.txtBusca.getText().toString().isEmpty()){
                buscarUser(binding.txtBusca.getText().toString());
            }
        });
    }
    
    private void buscarUser(String query ) {
        JsonTypedRequest<Usuario, ResponseBody, ErrorResponse> buscarUser = new JsonTypedRequest<>
                (       this,
                        Request.Method.GET,
                        Usuario.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        HttpUtils.buildUrl(getResources(),"account/search?search=" + query),
                        null,
                        (ResponseBody response) ->
                        {
                            this.dialog.dismiss();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                if(((ArrayList) response.getData()).size() != 0) {
                                    for (Map<String, Object> retorno : (ArrayList<Map<String, Object>>) response.getData()) {
                                        User user = JsonUtils.jsonConvert(retorno.get("usuario"), User.class);
                                        Musico usuario = new Musico();
                                        usuario.setId(user.getId());
                                        if(user.getAvatar() != null) {
                                            usuario.setFotoID(user.getAvatar().get_id());
                                        }
                                        usuario.setTipoUsuario(TiposUsuario.fromKind(user.getKind()));
                                        usuario.setNomeCompleto(user.getFullName());
                                        usuario.setSobre(user.getSobre());
                                        if(apenasMusicos) {
                                            if (TiposUsuario.MUSICO.equals(usuario.getTipoUsuario())) {
                                                userList.add(usuario);
                                            }
                                        } else {
                                            userList.add(usuario);
                                        }
                                    }

                                    if(userList.isEmpty()){
                                        binding.lbSemUsuario.setVisibility(View.VISIBLE);
                                        binding.listaUsuarios.setVisibility(View.GONE);
                                    } else {
                                        binding.lbSemUsuario.setVisibility(View.GONE);
                                        binding.listaUsuarios.setVisibility(View.VISIBLE);
                                        binding.listaUsuarios.setAdapter(new AdapterUsuario(userList, this, true));
                                    }
                                } else
                                {
                                    binding.lbSemUsuario.setVisibility(View.VISIBLE);
                                    binding.listaUsuarios.setVisibility(View.GONE);
                                }
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            binding.listaUsuarios.setAdapter(new AdapterUsuario(userList, this, true));
                            dialog.hide();
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Response]FindUser", errorResponse.getMessage());
                                mensagem = errorResponse.getMessage();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        },
                        (VolleyError errorResponse) ->
                        {
                            binding.listaUsuarios.setAdapter(new AdapterUsuario(userList, this, true));
                            dialog.hide();
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Volley]FindUser", errorResponse.getMessage());
                                errorResponse.printStackTrace();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        }
                );
        userList = new HashSet<>();

        buscarUser.execute();
        this.dialog.show();
    }

}
