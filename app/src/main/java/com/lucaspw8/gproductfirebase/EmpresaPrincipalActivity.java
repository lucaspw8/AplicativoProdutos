package com.lucaspw8.gproductfirebase;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.lucaspw8.gproductfirebase.Adapter.ProdutoAdapter;
import com.lucaspw8.gproductfirebase.Classes.Empresa;
import com.lucaspw8.gproductfirebase.Classes.Produto;
import com.lucaspw8.gproductfirebase.Helper.EmpresaPreferencias;
import com.lucaspw8.gproductfirebase.Helper.UsuarioPreferencias;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

    private RecyclerView mrecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<Produto> listaProdutos;
    private Produto produto;
    private  ProdutoAdapter adapter;


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


        mrecyclerView = view.findViewById(R.id.listaProdEmpresa);

        txttituloEmpresa = view.findViewById(R.id.txtTituloEmpresa);
        txtqtdProd = view.findViewById(R.id.txtqtdProd);

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
        carregarTodosProdutos();

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),CadastroProduto.class);
                startActivity(intent);
            }
        });

        getActivity().setTitle("Início vendedor");
        return view;
    }


    private void carregarTodosProdutos(){
        mrecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(
                getActivity(),LinearLayoutManager.VERTICAL,false);

        mrecyclerView.setLayoutManager(linearLayoutManager);
        mrecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        listaProdutos = new ArrayList<>();

        referenceFirebase = FirebaseDatabase.getInstance().getReference();

        referenceFirebase.child("produto").orderByChild("uidUsuario").equalTo(usuarioPreferencias.getUidUsu()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    txtqtdProd.setText(dataSnapshot.getChildrenCount()+" produto(s) cadastrados");
                }else{
                    txtqtdProd.setText("Não há produtos cadastrados");
                }

                listaProdutos.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    produto = postSnapshot.getValue(Produto.class);

                    listaProdutos.add(produto);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new ProdutoAdapter(listaProdutos,getActivity(),"usuVendedor");
        mrecyclerView.setAdapter(adapter);
    }


    public void pesquisarProd(String texto){
        Log.d("Lista Prod", texto);
        List<Produto> newListProd = new ArrayList<Produto>();
        if(adapter !=null){
            for (Produto prodList: listaProdutos) {
                if(prodList.getNome().toLowerCase().contains(texto)){
                    newListProd.add(prodList);
                }
            }
            adapter.pesquisar(newListProd);
        }
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
