package com.lucaspw8.gproductfirebase;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class EmpresaPrincipalActivity extends Fragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Obter a view do fragmento
        View view = inflater.inflate(R.layout.activity_empresa_principal, container, false);

        preferencias = new Preferencias(getActivity());
        empresaPreferencias = new EmpresaPreferencias(getActivity());
        //pegando referencia do Firebase
        autenticacao = FirebaseAuth.getInstance();
        referenceFirebase = FirebaseDatabase.getInstance().getReference();
        //Recuperando os dados da empresa
        empresa = empresaPreferencias.getEmpresa();

        txttituloEmpresa = view.findViewById(R.id.txtTituloEmpresa);
        txttituloEmpresa.setText(empresa.getNome());
        txtqtdProd = view.findViewById(R.id.txtqtdProd);

       final DecimalFormat df = new DecimalFormat("0.##");

        referenceFirebase.child("produto").orderByChild("emailEmpresa").equalTo(empresa.getEmailDono()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    txtqtdProd.setText(dataSnapshot.getChildrenCount()+" produtos cadastrados");
                }else{
                    txtqtdProd.setText("Não há produtos cadastrados");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        this.menu1 = menu;
        menu1.clear();
        getMenuInflater().inflate(R.menu.menu_vendedor,menu1);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.addProd){
            AbrirTelaCadProduto();
        }else if(id == R.id.sair_vendedor) {
            deslogar();
        }

        return super.onOptionsItemSelected(item);
    }


    private void deslogar() {
        autenticacao.signOut();
        preferencias.limparDados();
        empresaPreferencias.limparDados();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private void AbrirTelaCadProduto() {
        Intent intent = new Intent(this,CadastroProduto.class);
        startActivity(intent);
    }*/
}
