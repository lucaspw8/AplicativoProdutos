package com.lucaspw8.gproductfirebase.Adapter;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
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
import com.lucaspw8.gproductfirebase.CadastroUsuario;
import com.lucaspw8.gproductfirebase.Classes.Empresa;
import com.lucaspw8.gproductfirebase.Classes.Produto;
import com.lucaspw8.gproductfirebase.InformacaoProduto;
import com.lucaspw8.gproductfirebase.InformacaoProdutoEmpresa;
import com.lucaspw8.gproductfirebase.MeuPerfilActivity;
import com.lucaspw8.gproductfirebase.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ViewHolder>{

    private List<Produto> mprodutoList;
    private Context context;
    private DatabaseReference referenciaFirebase;
    private Produto todosProdutos;
    private Empresa empresa;
    private String direcao;

    public ProdutoAdapter(List<Produto> l, Context c,String direcao){
        this.context = c;
        mprodutoList = l;
        this.direcao = direcao;

    }
    public void pesquisar(List<Produto> listaProd){
        this.mprodutoList = listaProd;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProdutoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        empresa = new Empresa();
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.modelolistprod,viewGroup,false);
        return new ProdutoAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProdutoAdapter.ViewHolder holder, final int position) {
        final Produto item = mprodutoList.get(position);

        if (position >= mprodutoList.size()-1 && mprodutoList.size() >=4){
            holder.view.setVisibility(View.VISIBLE);
        }else {
            holder.view.setVisibility(View.GONE);
        }

        //formata o valor para ficar mais amigavel
        DecimalFormat df = new DecimalFormat("0.00");
        //Define o texto da lista de prod
        holder.txtNomeProdLista.setText(item.getNome());
        holder.txtValorProdLista.setText("R$ "+df.format(item.getValor()));
        holder.txtDescriProdLista.setText(item.getDescricao());

        //Colocando o nome da empresa na lista
        if(direcao.equals("usuComum")){
            //volta a visibilidade do item
            holder.txtEmpresaProdLista.setVisibility(View.VISIBLE);
            //Se empresa atual tem o mesmo uid do produto atual
            if(item.getUidUsuario().equals(empresa.getUidUsuario())){
                holder.txtEmpresaProdLista.setText("Vendido por: "+empresa.getNome());

            }else {
                referenciaFirebase = FirebaseDatabase.getInstance().getReference();
                referenciaFirebase.child("empresa").orderByChild("uidUsuario").equalTo(item.getUidUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            empresa = postSnapshot.getValue(Empresa.class);
                            holder.txtEmpresaProdLista.setText("Vendido por: "+empresa.getNome());

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }


        if(item.getImagemUrl().equals("")){
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/gproduct-3086b.appspot.com/o/imgprod.png?alt=media&token=cdd47373-d625-4c04-b64b-0f372d811281")
                    .resize(300, 300).centerCrop()
                    .into(holder.fotoProdutoLista);
        }else {
            Picasso.get().load(item.getImagemUrl()).resize(300, 300).centerCrop()
                    .into(holder.fotoProdutoLista);
        }

        //Clica sobre o produto para expandir
        holder.linearLayoutProdutosLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Produto","nome "+item.getNome()+" uidUsu: "+item.getUidUsuario());
                Bundle bundle = new Bundle();
                bundle.putString("nomeProd",item.getNome());
                bundle.putString("valor",String.valueOf(item.getValor()));
                bundle.putString("descricao",item.getDescricao());
                bundle.putString("keyProduto",item.getKeyProduto());
                bundle.putString("uidUsuario",item.getUidUsuario());
                bundle.putString("imagemUrl",item.getImagemUrl());

                if(direcao.equals("usuComum")) {
                    Intent intent = new Intent(context, InformacaoProduto.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }else if(direcao.equals("usuVendedor")){
                    Intent intent = new Intent(context, InformacaoProdutoEmpresa.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
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
        protected TextView txtEmpresaProdLista;
        protected ImageView fotoProdutoLista;
        protected LinearLayout linearLayoutProdutosLista;
        protected View view;

        public ViewHolder (View itemView){
            super(itemView);

            txtNomeProdLista = itemView.findViewById(R.id.txtNomeProdLista);
            txtValorProdLista = itemView.findViewById(R.id.txtValorProdLista);
            txtDescriProdLista = itemView.findViewById(R.id.txtDescriProdLista);
            txtEmpresaProdLista = itemView.findViewById(R.id.txtEmpresaProdLista);

            fotoProdutoLista = itemView.findViewById(R.id.fotoProdutoLista);
            linearLayoutProdutosLista = itemView.findViewById(R.id.linearLayoutProdutosLista);
            view = itemView.findViewById(R.id.viewLista);
        }
    }
}
