package com.lucaspw8.gproductfirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaspw8.gproductfirebase.Adapter.ProdutoAdapter;
import com.lucaspw8.gproductfirebase.Classes.Produto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ListarProdutos extends AppCompatActivity {
    private RecyclerView mrecyclerView;
    private  ProdutoAdapter adapter;

    private List<Produto> produtos;

    private DatabaseReference referenciaFirebase;

    private Produto todosProdutos;

    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_produtos);

        mrecyclerView = findViewById(R.id.listaProd);

        carregarTodosProdutos();

    }

    private void carregarTodosProdutos(){
        mrecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(
                this,LinearLayoutManager.VERTICAL,false);

        mrecyclerView.setLayoutManager(linearLayoutManager);

        produtos = new ArrayList<>();

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        referenciaFirebase.child("produto").orderByChild("nome").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    todosProdutos = postSnapshot.getValue(Produto.class);

                    produtos.add(todosProdutos);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new ProdutoAdapter(produtos,this);
        mrecyclerView.setAdapter(adapter);
    }
}
