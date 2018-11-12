package com.lucaspw8.gproductfirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaspw8.gproductfirebase.Classes.Empresa;
import com.lucaspw8.gproductfirebase.Helper.EmpresaPreferencias;
import com.lucaspw8.gproductfirebase.Helper.Preferencias;

import java.text.DecimalFormat;

public class EmpresaPrincipalActivity extends AppCompatActivity {
    //Firebase
    private FirebaseAuth autenticacao;
    private DatabaseReference referenceFirebase;
    //Menu de opções
    private Menu menu1;

    //Preferencias
    private Preferencias preferencias;
    private EmpresaPreferencias empresaPreferencias;
    private Empresa empresa;
    private TextView txttituloEmpresa;
    private TextView txtqtdProd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_principal);

        preferencias = new Preferencias(this);
        empresaPreferencias = new EmpresaPreferencias(this);
        //pegando referencia do Firebase
        autenticacao = FirebaseAuth.getInstance();
        referenceFirebase = FirebaseDatabase.getInstance().getReference();
        //Recuperando os dados da empresa
        empresa = empresaPreferencias.getEmpresa();

        txttituloEmpresa = findViewById(R.id.txtTituloEmpresa);
        txttituloEmpresa.setText(empresa.getNome());
        txtqtdProd = findViewById(R.id.txtqtdProd);

       final DecimalFormat df = new DecimalFormat("0.##");

        referenceFirebase.child("produto").orderByChild("emailEmpresa").equalTo(empresa.getEmailDono()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    txtqtdProd.setText(dataSnapshot.getChildrenCount()+" produtos cadastrados");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    private void deslogar() {
        autenticacao.signOut();
        preferencias.limparDados();
        empresaPreferencias.limparDados();

    }

    private void AbrirTelaCadProduto() {
        //Intent intent = new Intent(this,CadastroProduto.class);
        //startActivity(intent);
    }
}
