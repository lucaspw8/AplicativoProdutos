package com.lucaspw8.gproductfirebase;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaspw8.gproductfirebase.Adapter.ProdutoAdapter;
import com.lucaspw8.gproductfirebase.Classes.Produto;

import java.util.ArrayList;
import java.util.List;


public class ListarProdutos extends Fragment {
    private RecyclerView mrecyclerView;
    private  ProdutoAdapter adapter;

    private List<Produto> listaProdutos;

    private DatabaseReference referenciaFirebase;

    private Produto produto;

    private LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Obter a view do fragmento
        View view = inflater.inflate(R.layout.activity_listar_produtos, container, false);

        mrecyclerView = view.findViewById(R.id.listaProd);
        carregarTodosProdutos();

        return view;
    }

    private void carregarTodosProdutos(){
        mrecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(
                getActivity(),LinearLayoutManager.VERTICAL,false);

        mrecyclerView.setLayoutManager(linearLayoutManager);
        mrecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        listaProdutos = new ArrayList<>();

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        referenciaFirebase.child("produto").orderByChild("nome").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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

        adapter = new ProdutoAdapter(listaProdutos,getActivity(),"usuComum");
        mrecyclerView.setAdapter(adapter);
    }
}
