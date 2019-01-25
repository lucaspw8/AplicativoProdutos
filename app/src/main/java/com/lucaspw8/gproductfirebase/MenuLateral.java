package com.lucaspw8.gproductfirebase;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lucaspw8.gproductfirebase.Helper.EmpresaPreferencias;
import com.lucaspw8.gproductfirebase.Helper.UsuarioPreferencias;

public class MenuLateral extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth autenticacao;
    private EmpresaPreferencias empresaPreferencias;

    UsuarioPreferencias userPref;
    //Itens do menu lateral
    private Menu menuNav;
    private MenuItem menuLoginItem;
    private MenuItem menuLogout;
    private MenuItem menuTopoPesquisar;
    private MenuItem menuContausu;
    private MenuItem menuCadastrarProdutos;

    private TextView txtNomeMenu;
    private TextView txtEmailMenu;
    private ImageView imgMenu;

    private NavigationView navigationView;

    private boolean pesquisa = false;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_lateral);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        autenticacao = FirebaseAuth.getInstance();


        //UsuarioPreferencias de usuario
        userPref = new UsuarioPreferencias(this);
        empresaPreferencias = new EmpresaPreferencias(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Cabeçalho menu
        View headerView = navigationView.getHeaderView(0);

        txtNomeMenu = headerView.findViewById(R.id.txtNomeMenuLateral);
        txtEmailMenu = headerView.findViewById(R.id.txtEmailMenuLateral);
        imgMenu = headerView.findViewById(R.id.imageViewMenu);

        //Atribuindo os itens do menu pelo id
        menuNav = navigationView.getMenu();
        menuLoginItem = menuNav.findItem(R.id.nav_login);
        menuLogout = menuNav.findItem(R.id.nav_logout);
        menuContausu = menuNav.findItem(R.id.nav_contausu);
        menuCadastrarProdutos = menuNav.findItem(R.id.nav_produtos);

        this.getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        Fragment atual = getCurrentFragment();
                        if(atual instanceof ListarProdutos){
                            navigationView.setCheckedItem(R.id.nav_home);
                        }else if(atual instanceof EmpresaPrincipalActivity){
                            navigationView.setCheckedItem(R.id.nav_home);
                        }
                    }
                }
        );


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!pesquisa) {
            menuCadastrarProdutos.setVisible(false);
            //Se user logado
            if (usuarioLogado()) {
                //Oculta opc login
                menuLoginItem.setVisible(false);
                //Exibe opc logout
                menuLogout.setVisible(true);
                //Exibe opc Conta usuario
                menuContausu.setVisible(true);

                //Atribui o nome e email do usu logado ao menu
                txtNomeMenu.setText(userPref.getNomeUsu());
                txtEmailMenu.setText(userPref.getEmailUsu());

                if (userPref.getTipoUsu().equals("VENDEDOR")) {
                    //Exibe a opc de cadastro de produto
                    menuCadastrarProdutos.setVisible(true);
                    navigationView.setCheckedItem(R.id.nav_home);
                    if(getCurrentFragment() instanceof EmpresaPrincipalActivity){

                    }else {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_conteiner, new EmpresaPrincipalActivity()).commit();
                    }
                } else if (userPref.getTipoUsu().equals("CONSUMIDOR")) {
                    navigationView.setCheckedItem(R.id.nav_home);
                    if(getCurrentFragment() instanceof ListarProdutos){

                    }else {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_conteiner, new ListarProdutos()).commit();
                    }
                }
                //Se user não logado
            } else {
                //Exibe opc login
                menuLoginItem.setVisible(true);
                //Oculta opc logout
                menuLogout.setVisible(false);
                //Oculta opc Conta usuario
                menuContausu.setVisible(false);
                navigationView.setCheckedItem(R.id.nav_home);
                if(getCurrentFragment() instanceof ListarProdutos){

                }else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_conteiner, new ListarProdutos()).commit();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                //super.onBackPressed();
                if(getSupportFragmentManager().getBackStackEntryCount() > 1){
                    getSupportFragmentManager().popBackStack();
                }else{
                    moveTaskToBack(true);
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lateral, menu);
        // SearchView
        menuTopoPesquisar = menu.findItem(R.id.action_pesquisar);
        searchView =(SearchView) menuTopoPesquisar.getActionView();
        searchView.setOnQueryTextListener(onSearch());
        searchView.setOnCloseListener(closeListener());
        return true;
    }

    private SearchView.OnCloseListener closeListener(){
        return new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                pesquisa = false;
                Log.d("Fechado","Close");
                return false;
            }
        };
    }

    private SearchView.OnQueryTextListener onSearch(){
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Usuario fez a busca
                Fragment atual = getCurrentFragment();
                pesquisa = true;
                if(atual instanceof ListarProdutos){
                    ((ListarProdutos) atual).pesquisarProd(query);
                }else if(atual instanceof EmpresaPrincipalActivity){
                    ((EmpresaPrincipalActivity) atual).pesquisarProd(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Fragment atual = getCurrentFragment();
                pesquisa = true;
                if(atual instanceof ListarProdutos){
                   ((ListarProdutos) atual).pesquisarProd(newText);
                }else if(atual instanceof EmpresaPrincipalActivity){
                    ((EmpresaPrincipalActivity) atual).pesquisarProd(newText);
                }
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

        }else if (id == R.id.nav_produtos) {
            Intent intent = new Intent(this,CadastroProduto.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_contausu) {
            perfilUsu();
        }
        else if (id == R.id.nav_login) {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            //finish();
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
    //Desloga o usuario e limpa as preferencias
    private void deslogar() {
        autenticacao.signOut();
        userPref.limparDados();
        empresaPreferencias.limparDados();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /**
     * Retorna o fragmento atual
     * @return Fragment
     */
    public android.support.v4.app.Fragment getCurrentFragment() {
        return this.getSupportFragmentManager().findFragmentById(R.id.frame_conteiner);
    }

    /**
     * Informa se o usuario ja está logado
     * @return Boolean
     */
    public Boolean usuarioLogado(){

        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            return true;
        }else{
            return false;
        }
    }

}
