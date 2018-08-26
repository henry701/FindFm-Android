package com.fatec.tcc.findfm.Views;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fatec.tcc.findfm.Controller.FindFM;
import com.fatec.tcc.findfm.Controller.Perfil.PerfilViewModel;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.databinding.ActivityPerfilFragmentBinding;

public class TelaPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog dialog;
    private NavigationView navigationView = null;
    private View header = null;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);
        fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.frame_content, new Perfil_Fragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //TODO: erros quando troca de fragment
        int id = item.getItemId();
        String tela = FindFM.getInstance().getParams().getString("tela", "");
        switch (id) {
            case R.id.inicio:
                if(!tela.equals("HOME")) {
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new Home_Fragment())
                        .commit();
                }
                break;
            case R.id.meu_perfil:
                if(!tela.equals("MEU_PERFIL")) {
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new Perfil_Fragment())
                            .commit();
                    ActivityPerfilFragmentBinding bindingmeu_perfil;
                    bindingmeu_perfil = DataBindingUtil.setContentView(this, R.layout.activity_perfil__fragment);
                    bindingmeu_perfil.setViewModel(new PerfilViewModel(this));
                    bindingmeu_perfil.executePendingBindings();
                }
                break;
            case R.id.meus_anuncios:
                if(!tela.equals("MEUS_ANUNCIOS")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new MeusAnuncios_Fragment(this))
                            .commit();
                }
                break;
            case R.id.notificacoes:
                if(!tela.equals("NOTIFICACOES")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new Notificacoes_Fragment())
                            .commit();
                }
                break;
            case R.id.configuracoes:
                if(!tela.equals("CONFIGURACOES")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new Configuracoes_Fragment())
                            .commit();
                }
                break;
            case R.id.sair:
                dialog.show();
                SharedPreferences.Editor editor = getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("username", null);
                editor.apply();
                dialog.dismiss();
                Util.open_form__no_return(this, Login.class);
                break;
        }
        return true;
    }
}
