package com.fatec.tcc.findfm.Views;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Controller.Midia.RadioController;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Request.Coordenada;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.MidiaUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.databinding.ActivityTelaPrincipalBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class TelaPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Observer{

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ProgressDialog dialog;
    private FragmentManager fragmentManager;
    private MenuItem radioMenu;
    private boolean tocandoRadio = false;
    private RadioController radioController;
    private boolean radioIniciando = false;
    private ActivityTelaPrincipalBinding binding;
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = DataBindingUtil.setContentView(this, R.layout.activity_tela_principal);
        setSupportActionBar(binding.appBarInclude.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarInclude.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        radioMenu = binding.navView.getMenu().findItem(R.id.playRadio);

        TextView textView = binding.navView.getHeaderView(0).findViewById(R.id.txtUsuarioHeader);
        textView.setText(FindFM.getUsuario().getNomeCompleto());

        ImageView imageView = binding.navView.getHeaderView(0).findViewById(R.id.imageViewHeader);
        MidiaUtils.setImagemPerfilToImageView(imageView, this);

        binding.navView.setNavigationItemSelectedListener(this);
        binding.navView.getMenu().getItem(0).setChecked(true);

        init();

        Bundle action = getIntent().getExtras();
        if(action != null && action.getString("DO") != null) {
            if (action.getString("DO").equals("volume")) {
                Log.i("NotificationReturnSlot", "volume");
                //Your code
            } else if (action.getString("DO").equals("stopNotification")) {
                //Your code
                Log.i("NotificationReturnSlot", "stopNotification");
            }
        }
    }

    private void init(){
        this.dialog = new ProgressDialog(this);
        this.dialog.setMessage("Carregando...");
        this.dialog.setCancelable(false);

        this.radioController = new RadioController(this);
        this.radioController.addObserver(this);

        Bundle bundle = getIntent().getBundleExtra("CriarPost");
        fragmentManager = getFragmentManager();

        if(bundle != null) {
            if (bundle.getString("id_usuario") != null){
                if(fragmentManager.findFragmentById(R.id.frame_content) == null){
                    fragmentManager.beginTransaction().add(R.id.frame_content,
                            new Perfil_Fragment(this, HttpUtils.buildUrl(getResources(),"account", bundle.getString("id_usuario"))))
                            .commit();
                } else {
                    fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.frame_content)).commit();
                    fragmentManager.beginTransaction().replace(R.id.frame_content,
                            new Perfil_Fragment(this, HttpUtils.buildUrl(getResources(), "account", bundle.getString("id_usuario"))))
                            .commit();
                }
                return;
            } else if (bundle.getBoolean("euMesmo") == true){
                if(fragmentManager.findFragmentById(R.id.frame_content) == null){
                    fragmentManager.beginTransaction().add(R.id.frame_content,
                            new Perfil_Fragment(this, HttpUtils.buildUrl(getResources(),"account", "me")))
                            .commit();
                } else {
                    fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.frame_content)).commit();
                    fragmentManager.beginTransaction().replace(R.id.frame_content,
                            new Perfil_Fragment(this, HttpUtils.buildUrl(getResources(),"account", "me")))
                            .commit();
                }

                return;
            }
        } else {
            if(!FindFM.isUsuarioAceitouTermos(this) && !TiposUsuario.VISITANTE.equals(FindFM.getTipoUsuario(this))) {
                AlertDialogUtils.newSimpleDialog__TwoButtons(this, "Termos de uso - FindFM", R.drawable.ic_error, R.string.termos,
                        "Aceito", "Não aceito",
                        (dialog, which) -> FindFM.usuarioAceitaTermos(this),
                        (dialog, which) -> onBackPressed()).show();
            } else if (TiposUsuario.VISITANTE.equals(FindFM.getTipoUsuario(this))){
                AlertDialogUtils.newSimpleDialog__TwoButtons(this, "Termos de uso - FindFM", R.drawable.ic_error, R.string.termos,
                        "Aceito", "Não aceito",
                        (dialog, which) -> {},
                        (dialog, which) -> onBackPressed()).show();
            }
        }

        if(fragmentManager.findFragmentById(R.id.frame_content) == null){
            fragmentManager.beginTransaction().add(R.id.frame_content, new Feed_Fragment(this, TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario())))
                    .commit();
        } else {
            fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.frame_content)).commit();
            fragmentManager.beginTransaction().replace(R.id.frame_content, new Feed_Fragment(this, TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario())))
                    .commit();
        }
        getCidade();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(FindFM.getTelaAtual().equals("HOME")) {
                dialog.dismiss();
                finishAffinity();
                System.exit(0);
            } else {
                binding.navView.getMenu().getItem(0).setChecked(true);
                fragmentManager = getFragmentManager();

                if(fragmentManager.findFragmentById(R.id.frame_content) == null){
                    fragmentManager.beginTransaction().add(R.id.frame_content, new Feed_Fragment(this, TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario())))
                            .commit();
                } else {
                    fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.frame_content)).commit();
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new Feed_Fragment(this, TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario())))
                            .commit();
                }


            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(FindFM.getUsuario().getTipoUsuario().equals(TiposUsuario.VISITANTE)) {
            NavigationView navigationView = findViewById(R.id.nav_view);
            Menu menuNav = navigationView.getMenu();

            MenuItem meu_perfil = menuNav.findItem(R.id.meu_perfil);
            meu_perfil.setEnabled(false);

            MenuItem meus_posts = menuNav.findItem(R.id.meus_posts);
            meus_posts.setEnabled(false);

            MenuItem trabalhos = menuNav.findItem(R.id.trabalhos);
            trabalhos.setEnabled(false);
        }

        if(FindFM.getUsuario().getTipoUsuario().equals(TiposUsuario.CONTRATANTE)) {
            NavigationView navigationView = findViewById(R.id.nav_view);
            Menu menuNav = navigationView.getMenu();

            MenuItem meus_posts = menuNav.findItem(R.id.meus_posts);
            meus_posts.setTitle("Meus Anúncios");

            MenuItem trabalhos = menuNav.findItem(R.id.trabalhos);
            trabalhos.setVisible(false);

            MenuItem anuncios_sugeridos = menuNav.findItem(R.id.anuncio_sugerido);
            anuncios_sugeridos.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        this.optionsMenu = menu;
        try {
            optionsMenu.getItem(2).setVisible(true);
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment f = getFragmentManager().findFragmentById(R.id.frame_content);
        return f.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String tela = FindFM.getTelaAtual();

        switch (item.getItemId()) {
            case R.id.inicio:
                if(!tela.equals("HOME")) {
                    fragmentManager = getFragmentManager();
                    if(fragmentManager.findFragmentById(R.id.frame_content) == null){
                        fragmentManager.beginTransaction().add(R.id.frame_content, new Feed_Fragment(this, TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario())))
                                .commit();
                    } else {
                        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.frame_content)).commit();
                        fragmentManager.beginTransaction().replace(R.id.frame_content, new Feed_Fragment(this, TiposUsuario.VISITANTE.equals(FindFM.getUsuario().getTipoUsuario())))
                                .commit();
                    }
                }
                break;
            case R.id.meu_perfil:
                if(!tela.equals("MEU_PERFIL")) {
                    if(fragmentManager.findFragmentById(R.id.frame_content) == null){
                        fragmentManager.beginTransaction().add(R.id.frame_content, new Perfil_Fragment(this, HttpUtils.buildUrl(getResources(),"account", "me")))
                                .commit();
                    } else {
                        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.frame_content)).commit();
                        fragmentManager.beginTransaction().replace(R.id.frame_content, new Perfil_Fragment(this, HttpUtils.buildUrl(getResources(),"account", "me")))
                                .commit();
                    }
                }
                break;
            case R.id.meus_posts:
                if(!tela.equals("MEUS_POSTS")) {
                    if(fragmentManager.findFragmentById(R.id.frame_content) == null){
                        fragmentManager.beginTransaction().add(R.id.frame_content, new MeusPosts_Fragment(this))
                                .commit();
                    } else {
                        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.frame_content)).commit();
                        fragmentManager.beginTransaction().replace(R.id.frame_content, new MeusPosts_Fragment(this))
                                .commit();
                    }
                }
                break;
            case R.id.anuncio_sugerido:
                if(!tela.equals("ANUNCIOS_SUGERIDOS")) {
                    if(fragmentManager.findFragmentById(R.id.frame_content) == null){
                        fragmentManager.beginTransaction().add(R.id.frame_content, new AnunciosSugeridos_Fragment(this))
                                .commit();
                    } else {
                        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.frame_content)).commit();
                        fragmentManager.beginTransaction().replace(R.id.frame_content, new AnunciosSugeridos_Fragment(this))
                                .commit();
                    }
                }
                break;
            case R.id.trabalhos:
                if(!tela.equals("TRABALHOS")) {
                    if(fragmentManager.findFragmentById(R.id.frame_content) == null){
                        fragmentManager.beginTransaction().add(R.id.frame_content, new Trabalhos_Fragment(this, HttpUtils.buildUrl(getResources(),"account", "me")))
                                .commit();
                    } else {
                        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.frame_content)).commit();
                        fragmentManager.beginTransaction().replace(R.id.frame_content, new Trabalhos_Fragment(this, HttpUtils.buildUrl(getResources(),"account", "me")))
                                .commit();
                    }
                }
                break;
            case R.id.playRadio:
                if( !this.radioController.isPreparado() ){
                    Toast.makeText(this, "Carregando rádio...", Toast.LENGTH_LONG).show();
                    if(!radioIniciando) {
                        this.radioController.prepare();
                        item.setTitle(R.string.radio_carregando);
                        radioIniciando = true;
                    }
                }
                else if(!this.tocandoRadio){
                    item.setTitle(R.string.radio_pausar);
                    item.setIcon(R.drawable.ic_pause);
                    this.tocandoRadio = true;
                    this.radioController.play();
                }
                else{
                    item.setTitle(R.string.radio_tocar);
                    item.setIcon(R.drawable.ic_play);
                    this.tocandoRadio = false;
                    this.radioController.pause();
                }
                break;
            case R.id.infoRadio:
                radioInfo();
                break;
            case R.id.sair:
                dialog.show();
                FindFM.logoutUsuario(this);
                dialog.dismiss();
                Util.open_form__no_return(this, Login.class);
                break;
        }
        item.setChecked(true);
        DrawerLayout drawerLayout = this.findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawers();

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(tocandoRadio){
            radioController.toggle();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(tocandoRadio){
            radioController.toggle();
        }
    }

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        radioController.dismiss();
        super.onDestroy();
    }

    @Override
    public void update(Observable radioController, Object arg) {
        if(radioController instanceof RadioController){
            runOnUiThread(() -> {
                final Toast toast = Toast.makeText(getApplicationContext(), "Rádio carregada!", Toast.LENGTH_SHORT);
                toast.show();
                radioMenu.setTitle(R.string.radio_tocar);
            });
        }
    }

    public void atualizarBarraLateral(){
        ImageView imageView = binding.navView.getHeaderView(0).findViewById(R.id.imageViewHeader);
        MidiaUtils.setImagemPerfilToImageView(imageView, this);

        TextView textView = binding.navView.getHeaderView(0).findViewById(R.id.txtUsuarioHeader);
        textView.setText(FindFM.getUsuario().getNomeCompleto());
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public Menu getOptionsMenu() {
        return optionsMenu;
    }

    public boolean getCidade() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialogUtils.newSimpleDialog__OneButton(this, "Atenção!", R.drawable.ic_error,
                        R.string.texto_localizacao_permissao, "OK", (dialogInterface, i) -> {
                            ActivityCompat.requestPermissions(TelaPrincipal.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        });
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            Activity activity = this;
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            Util.getLocalizacao(activity, location.getLatitude(), location.getLongitude());
                        } else {
                            Util.requestLocalizacao(this);
                        }

                        Coordenada coordenada = (Coordenada) FindFM.getMap().get("coordenadas");
                        if(coordenada == null){
                            Util.requestLocalizacao(this);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Util.requestLocalizacao(this);
                    });
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                        Activity activity = this;
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(this, location -> {
                                    if (location != null) {
                                        Util.getLocalizacao(activity, location.getLatitude(), location.getLongitude());
                                    } else {
                                        Util.requestLocalizacao(this);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Util.requestLocalizacao(this);
                                });
                    }
                } else {
                    AlertDialogUtils.newSimpleDialog__OneButton(this, "Atenção!", R.drawable.ic_error,
                            R.string.texto_localizacao_sem_permissao, "OK", (dialogInterface, i) -> {
                                ActivityCompat.requestPermissions(TelaPrincipal.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            });
                }
                return;
            }

        }
    }

    public void goToPerfil() {
        if(FindFM.getMap().containsKey("USUARIO_BUSCA")){
            Usuario usuario = (Usuario) FindFM.getMap().get("USUARIO_BUSCA");
            fragmentManager.beginTransaction().replace(R.id.frame_content, new Perfil_Fragment(this, HttpUtils.buildUrl(getResources(),"account", usuario.getId())))
                    .commit();
            FindFM.getMap().remove("USUARIO_BUSCA");
        }
    }

    private void radioInfo(){
        JsonTypedRequest<Usuario, ResponseBody, ErrorResponse> radioInfos = new JsonTypedRequest<>
                (       this,
                        Request.Method.GET,
                        Usuario.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        HttpUtils.buildUrl(getResources(),"radioInfo"),
                        null,
                        (ResponseBody response) ->
                        {
                            getDialog().hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                try {
                                    String nome = ((Map<String, Object>) ((Map<String, Object>) response.getData()).get("song")).get("name").toString();
                                    AlertDialogUtils.newSimpleDialog__OneButton(this, "Tocando agora!", R.drawable.ic_audio,
                                            "Nome da Música: " + nome, "OK", null).create().show();
                                } catch ( Exception e ){
                                    String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                                    AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                            mensagem, "OK", (dialog, id) -> { }).create().show();
                                }
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            getDialog().hide();
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Response]GetRadio", errorResponse.getMessage());
                                mensagem = errorResponse.getMessage();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        },
                        (VolleyError errorResponse) ->
                        {
                            getDialog().hide();
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Volley]GetRadio", errorResponse.getMessage());
                                errorResponse.printStackTrace();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(this, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        }
                );

        radioInfos.execute();
        getDialog().show();
    }
}
