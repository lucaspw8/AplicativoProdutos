package com.lucaspw8.gproductfirebase;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaspw8.gproductfirebase.Classes.Empresa;
import com.lucaspw8.gproductfirebase.Helper.EmpresaPreferencias;
import com.lucaspw8.gproductfirebase.Helper.UsuarioPreferencias;

import java.text.DecimalFormat;

public class EmpresaPrincipalActivity extends Fragment {
    //Firebase
    private FirebaseAuth autenticacao;
    private DatabaseReference referenceFirebase;
    //Menu de opções
    private Menu menu1;

    //UsuarioPreferencias
    private UsuarioPreferencias usuarioPreferencias;
    private EmpresaPreferencias empresaPreferencias;
    private Empresa empresa;
    private TextView txttituloEmpresa;
    private TextView txtqtdProd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Obter a view do fragmento
        View view = inflater.inflate(R.layout.activity_empresa_principal, container, false);

        usuarioPreferencias = new UsuarioPreferencias(getActivity());
        empresaPreferencias = new EmpresaPreferencias(getActivity());
        //pegando referencia do Firebase
        autenticacao = FirebaseAuth.getInstance();
        referenceFirebase = FirebaseDatabase.getInstance().getReference();
        //Recuperando os dados da empresa


        txttituloEmpresa = view.findViewById(R.id.txtTituloEmpresa);
        txtqtdProd = view.findViewById(R.id.txtqtdProd);

       final DecimalFormat df = new DecimalFormat("0.##");

        referenceFirebase.child("produto").orderByChild("uidUsuario").equalTo(usuarioPreferencias.getUidUsu()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    txtqtdProd.setText(dataSnapshot.getChildrenCount()+" produto(s) cadastrados");
                }else{
                    txtqtdProd.setText("Não há produtos cadastrados");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        referenceFirebase.child("empresa").orderByChild("uidUsuario").equalTo(usuarioPreferencias.getUidUsu()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Verifica se existe alguma empresa
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        //Salvando os dados em um objeto Empresa
                        Empresa empresa =  new Empresa();
                        empresa.setNome(postSnapshot.child("nome").getValue().toString());
                        empresa.setTelefone(postSnapshot.child("telefone").getValue().toString());
                        empresa.setNumero( postSnapshot.child("numero").getValue().toString());
                        empresa.setRua(postSnapshot.child("rua").getValue().toString());
                        empresa.setBairro(postSnapshot.child("bairro").getValue().toString());
                        empresa.setComplemento(postSnapshot.child("complemento").getValue().toString());
                        empresa.setUidUsuario(postSnapshot.child("uidUsuario").getValue().toString());
                        empresa.setKeyEmpresa(postSnapshot.child("keyEmpresa").getValue().toString());
                        txttituloEmpresa.setText(empresa.getNome());
                        //Salvando no SharedPreferencias de Empresa
                        empresaPreferencias.salvarEmpresa(empresa);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        empresa = empresaPreferencias.getEmpresa();
        txttituloEmpresa.setText(empresa.getNome());
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
        usuarioPreferencias.limparDados();
        empresaPreferencias.limparDados();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
*/
}
