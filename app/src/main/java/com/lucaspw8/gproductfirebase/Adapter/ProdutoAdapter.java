package com.lucaspw8.gproductfirebase.Adapter;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaspw8.gproductfirebase.Classes.Produto;
import com.lucaspw8.gproductfirebase.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ViewHolder>{

    private List<Produto> mprodutoList;
    private Context context;
    private DatabaseReference referenciaFirebase;
    private List<Produto> produtos;
    private Produto todosProdutos;

    public ProdutoAdapter(List<Produto> l, Context c){
        this.context = c;
        mprodutoList = l;

    }

    @NonNull
    @Override
    public ProdutoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.modelolistprod,viewGroup,false);
        return new ProdutoAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProdutoAdapter.ViewHolder holder, final int position) {
        final Produto item = mprodutoList.get(position);

       produtos = new ArrayList<>();

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        referenciaFirebase.child("produto").orderByChild("nome").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    todosProdutos = postSnapshot.getValue(Produto.class);

                    produtos.add(todosProdutos);

                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DecimalFormat df = new DecimalFormat("0.##");

        holder.txtNomeProdLista.setText(item.getNome());
        holder.txtValorProdLista.setText("R$ "+df.format(item.getValor()));
        holder.txtDescriProdLista.setText(item.getDescricao());
        Picasso.get().load(item.getImagemUrl()).resize(300,300).centerCrop()
                .into(holder.fotoProdutoLista);

        //Clica sobre o produto para expandir
        holder.linearLayoutProdutosLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Produto",""+produtos.get(position).getNome());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mprodutoList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtNomeProdLista;
        protected TextView txtValorProdLista;
        protected TextView txtDescriProdLista;
        protected ImageView fotoProdutoLista;
        protected LinearLayout linearLayoutProdutosLista;

        public ViewHolder (View itemView){
            super(itemView);

            txtNomeProdLista = itemView.findViewById(R.id.txtNomeProdLista);
            txtValorProdLista = itemView.findViewById(R.id.txtValorProdLista);
            txtDescriProdLista = itemView.findViewById(R.id.txtDescriProdLista);

            fotoProdutoLista = itemView.findViewById(R.id.fotoProdutoLista);
            linearLayoutProdutosLista = itemView.findViewById(R.id.linearLayoutProdutosLista);
        }
    }
}
