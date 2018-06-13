package com.lucaspw8.gproductfirebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lucaspw8.gproductfirebase.Helper.Preferencias;

public class EmpresaPrincipalActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private DatabaseReference referenceFirebase;
    private Menu menu1;
    private Preferencias preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_principal);

        preferencias = new Preferencias(EmpresaPrincipalActivity.this);

        autenticacao = FirebaseAuth.getInstance();
        referenceFirebase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        this.menu1 = menu;

        //Recebendo email do usuario logado atraves do SharedPreferences
        String email = preferencias.getEmailUsu();
        menu1.clear();


            getMenuInflater().inflate(R.menu.menu_vendedor,menu1);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.addProd){
            AbrirTelaCadProduto();
        }else if(id == R.id.sair_vendedor){
            deslogar();
        }
        else if(id == R.id.sair_consumidor){
            deslogar();
        }else if(id == R.id.empresa){
            Intent intent = new Intent(this, CadastroEmpresaActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogar() {
        autenticacao.signOut();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void AbrirTelaCadProduto() {
        Intent intent = new Intent(this,CadastroProduto.class);
        startActivity(intent);
    }
}
