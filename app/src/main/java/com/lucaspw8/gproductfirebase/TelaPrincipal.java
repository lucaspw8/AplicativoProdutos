package com.lucaspw8.gproductfirebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaspw8.gproductfirebase.Classes.Usuario;
import com.lucaspw8.gproductfirebase.Helper.Preferencias;

public class TelaPrincipal extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private DatabaseReference referenceFirebase;
    private TextView tipoUsuario;
    private Usuario usuario;
    private String tipoUsuEmail;
    private Menu menu1;
    private Preferencias preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        tipoUsuario = (TextView)findViewById(R.id.txttipoUsuario) ;
        preferencias = new Preferencias(TelaPrincipal.this);

        autenticacao = FirebaseAuth.getInstance();
        referenceFirebase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        this.menu1 = menu;

        //Recebendo email do usuario logado atraves do SharedPreferences
        String email = preferencias.getEmailUsu();

                    tipoUsuEmail = preferencias.getTipoUsu();
                    tipoUsuario.setText(tipoUsuEmail);
                    menu1.clear();

                    if (tipoUsuEmail.equals("VENDEDOR")) {
                        getMenuInflater().inflate(R.menu.menu_vendedor,menu1);
                    }else if (tipoUsuEmail.equals("CONSUMIDOR")){
                        getMenuInflater().inflate(R.menu.menu_comprador,menu1);
                    }
              ;

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
        finish();
    }
}
