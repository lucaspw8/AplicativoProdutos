package com.lucaspw8.gproductfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lucaspw8.gproductfirebase.Helper.EmpresaPreferencias;
import com.lucaspw8.gproductfirebase.Helper.Preferencias;

public class MenuLateral extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth autenticacao;
    private EmpresaPreferencias empresaPreferencias;

    Preferencias userPref;
    //Itens do menu lateral
    private Menu menuNav;
    private MenuItem menuLoginItem;
    private MenuItem menuLogout;
    private MenuItem menuTopoPesquisar;
    private MenuItem menuContausu;
    private MenuItem menuCadastrarProdutos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_lateral);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        autenticacao = FirebaseAuth.getInstance();




        //Preferencias de usuario
        userPref = new Preferencias(this);
        empresaPreferencias = new EmpresaPreferencias(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Atribuindo os itens do menu pelo id
        menuNav = navigationView.getMenu();
        menuLoginItem = menuNav.findItem(R.id.nav_login);
        menuLogout = menuNav.findItem(R.id.nav_logout);
        menuContausu = menuNav.findItem(R.id.nav_contausu);
        menuCadastrarProdutos = menuNav.findItem(R.id.nav_produtos);
        if(savedInstanceState == null){


        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        menuCadastrarProdutos.setVisible(false);
        //Se user logado
        if (userPref.getTipoUsu() != null) {
            //Oculta opc login
            menuLoginItem.setVisible(false);
            //Exibe opc logout
            menuLogout.setVisible(true);
            //Exibe opc Conta usuario
            menuContausu.setVisible(true);

            if (userPref.getTipoUsu().equals("VENDEDOR")) {
                //Exibe a opc de cadastro de produto
                menuCadastrarProdutos.setVisible(true);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_conteiner, new EmpresaPrincipalActivity()).commit();
            } else if (userPref.getTipoUsu().equals("CONSUMIDOR")) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_conteiner, new ListarProdutos()).commit();
            }
            //Se user não logado
        }else{
            //Exibe opc login
            menuLoginItem.setVisible(true);
            //Oculta opc logout
            menuLogout.setVisible(false);
            //Oculta opc Conta usuario
            menuContausu.setVisible(false);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_conteiner, new ListarProdutos()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lateral, menu);
        // SearchView
        menuTopoPesquisar = menu.findItem(R.id.action_pesquisar);
        SearchView searchView =(SearchView) menuTopoPesquisar.getActionView();
        searchView.setOnQueryTextListener(onSearch());
        return true;
    }

    private SearchView.OnQueryTextListener onSearch(){
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Usuario fez a busca
                Toast.makeText(MenuLateral.this,"Procure: "+query,Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MenuLateral.this,CadastroProduto.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_conteiner,new Login()).commit();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_produtos) {
            Intent intent = new Intent(this,CadastroProduto.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_contausu) {
            perfilUsu();
        }
        else if (id == R.id.nav_login) {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }else if(id == R.id.nav_logout){
            deslogar();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void perfilUsu() {
        Intent intent = new Intent(this,MeuPerfilActivity.class);
        startActivity(intent);
    }

    private void deslogar() {
        autenticacao.signOut();
        userPref.limparDados();
        empresaPreferencias.limparDados();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
