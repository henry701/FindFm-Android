package com.fatec.tcc.findfm.Views;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fatec.tcc.findfm.Controller.Midia.RadioController;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.MidiaUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.databinding.ActivityTelaPrincipalBinding;

import java.util.Observable;
import java.util.Observer;

public class TelaPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Observer{

    private ProgressDialog dialog;
    private FragmentManager fragmentManager;
    private MenuItem radioMenu;
    private boolean tocandoRadio = false;
    private RadioController radioController;
    private boolean radioIniciando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTelaPrincipalBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_tela_principal);
        setSupportActionBar(binding.appBarInclude.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarInclude.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        radioMenu = binding.navView.getMenu().findItem(R.id.playRadio);

        TextView textView = binding.navView.getHeaderView(0).findViewById(R.id.txtUsuarioHeader);
        textView.setText(FindFM.getNomeUsuario(this));

        ImageView imageView = binding.navView.getHeaderView(0).findViewById(R.id.imageViewHeader);
        MidiaUtils.setImagemPerfilToImageView(imageView, this);

        binding.navView.setNavigationItemSelectedListener(this);
        binding.navView.getMenu().getItem(0).setChecked(true);

        init();
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
                fragmentManager.beginTransaction().replace(R.id.frame_content,
                        new Perfil_Fragment(this, HttpUtils.buildUrl(getResources(),"account", bundle.getString("id_usuario"))))
                        .commit();
                return;
            } else if (bundle.getBoolean("euMesmo") == true){
                fragmentManager.beginTransaction().replace(R.id.frame_content,
                        new Perfil_Fragment(this, HttpUtils.buildUrl(getResources(),"account", "me")))
                        .commit();
                return;
            }
        }

        fragmentManager.beginTransaction().replace(R.id.frame_content, new Feed_Fragment(this))
                .commit();
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
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_content, new Feed_Fragment(this))
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            return true;
        }
        else{
            Fragment f = getFragmentManager().findFragmentById(R.id.frame_content);
            return f.onOptionsItemSelected(item);

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String tela = FindFM.getTelaAtual();

        switch (item.getItemId()) {
            case R.id.inicio:
                if(!tela.equals("HOME")) {
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new Feed_Fragment(this))
                            .commit();
                }
                break;
            case R.id.meu_perfil:
                if(!tela.equals("MEU_PERFIL")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new Perfil_Fragment(this, HttpUtils.buildUrl(getResources(),"account", "me")))
                            .commit();
                }
                break;
            case R.id.meus_anuncios:
                if(!tela.equals("MEUS_ANUNCIOS")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new MeusPosts_Fragment(this))
                            .commit();
                }
                break;
            case R.id.notificacoes:
                if(!tela.equals("NOTIFICACOES")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new Notificacoes_Fragment(this))
                            .commit();
                }
                break;
            case R.id.configuracoes:
                if(!tela.equals("CONFIGURACOES")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new Configuracoes_Fragment(this))
                            .commit();
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

    public ProgressDialog getDialog() {
        return dialog;
    }
}
