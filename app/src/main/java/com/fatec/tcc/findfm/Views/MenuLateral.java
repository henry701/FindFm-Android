package com.fatec.tcc.findfm.Views;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.Util;

public class MenuLateral extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog dialog;
    private NavigationView navigationView = null;
    private View header = null;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_lateral);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);
        fragmentManager = getFragmentManager();
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
        getMenuInflater().inflate(R.menu.home__page, menu);
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
        int id = item.getItemId();
        String tela = FindFM.getInstance().getParams().getString("tela", "");
        switch (id) {
            case R.id.inicio:
                if(!tela.equals("HOME")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new HomePage())
                        .commit();
                }
                break;
            case R.id.meu_perfil:
                if(tela.equals("")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new HomePage())
                            .commit();
                }
                break;
            case R.id.meus_anuncios:
                if(tela.equals("")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new HomePage())
                            .commit();
                }
                break;
            case R.id.notificacoes:
                if(tela.equals("")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new HomePage())
                            .commit();
                }
                break;
            case R.id.configuracoes:
                if(tela.equals("")) {
                    fragmentManager.beginTransaction().replace(R.id.frame_content, new HomePage())
                            .commit();
                }
                break;
            case R.id.sair:
                SharedPreferences.Editor editor = getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("username", null);
                // As chaves precisam ser persistidas
                editor.apply();
                dialog.dismiss();
                Util.open_form__no_return(this, Login.class);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
