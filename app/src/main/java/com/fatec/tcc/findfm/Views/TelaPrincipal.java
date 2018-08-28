package com.fatec.tcc.findfm.Views;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.Util;

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

        fragmentManager.beginTransaction().replace(R.id.frame_content, new Home_Fragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(FindFM.getTelaAtual().equals("HOME"))
                super.onBackPressed();
            else {
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_content, new Home_Fragment())
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
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String tela = FindFM.getTelaAtual();

        switch (item.getItemId()) {
            case R.id.inicio:
                if(!tela.equals("HOME")) {
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new Home_Fragment(this))
                        .commit();
                }
                break;
            case R.id.meu_perfil:
                if(!tela.equals("MEU_PERFIL")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new Perfil_Fragment(this))
                            .commit();
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
}
