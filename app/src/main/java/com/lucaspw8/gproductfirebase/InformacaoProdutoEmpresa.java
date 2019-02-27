package com.lucaspw8.gproductfirebase;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.lucaspw8.gproductfirebase.Classes.Produto;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class InformacaoProdutoEmpresa extends AppCompatActivity {

    ActionBar actionBar;
    //Elementos visuais
    ImageView imagemProd;
    TextView nomeProd;
    TextView valorProd;
    TextView descricaoProd;
    BootstrapButton btEditarProd;
    BootstrapButton btExcluirProd;
    private DatabaseReference referenciaFirebase;

    Produto produto;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacao_produto_empresa);

        imagemProd = findViewById(R.id.imgInformacaoEmpresaProduto);
        nomeProd = findViewById(R.id.txtNomeInformacaoEmpresaprod);
        valorProd = findViewById(R.id.txtValorInformacaoEmpresaProd);
        descricaoProd = findViewById(R.id.txtDescricaoInformacaoEmpresaProd);

        btEditarProd = findViewById(R.id.btnEditarProdInformacao);
        btExcluirProd = findViewById(R.id.btnExcluirProd);

        //Pegando intent anterior
        final Intent intent = getIntent();
        //Passando dados para o bundle
        Bundle dadosTela = intent.getExtras();

        produto = new Produto();

        produto.setNome(dadosTela.getString("nomeProd"));
        produto.setValor(Float.parseFloat(dadosTela.getString("valor")));
        produto.setDescricao(dadosTela.getString("descricao"));
        produto.setKeyProduto(dadosTela.getString("keyProduto"));
        produto.setUidUsuario(dadosTela.getString("uidUsuario"));
        produto.setImagemUrl(dadosTela.getString("imagemUrl"));







        //Clique dos botões
        btEditarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("nomeProd",produto.getNome());
                bundle.putString("valor",String.valueOf(produto.getValor()));
                bundle.putString("descricao",produto.getDescricao());
                bundle.putString("keyProduto",produto.getKeyProduto());
                bundle.putString("uidUsuario",produto.getUidUsuario());
                bundle.putString("imagemUrl",produto.getImagemUrl());

                Intent intent1 = new Intent(InformacaoProdutoEmpresa.this,EditarProduto.class);
                intent1.putExtras(bundle);
                startActivity(intent1);
            }
        });

        btExcluirProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogExcluirProduto();
            }
        });

        //Seta de voltar
        try {
            actionBar = getSupportActionBar();
            //Adiciona o botao de voltar no action bar
            actionBar.setDisplayHomeAsUpEnabled(true); //Mostrar o botão
            actionBar.setTitle("Informação do produto "+produto.getNome());
            //getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
            //getSupportActionBar().setTitle("Seu titulo aqui");
        } catch (NullPointerException e) {
            Log.w("Erro", e.getMessage());
        }

    }



    @Override
    protected void onResume() {
        super.onResume();

        referenciaFirebase = ConfiguracaoFirebase.getFirebase().child("produto");

        referenciaFirebase.orderByChild("keyProduto").equalTo(produto.getKeyProduto()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    produto = postSnapshot.getValue(Produto.class);

                    if(!produto.getImagemUrl().equals("")) {
                        Picasso.get().load(produto.getImagemUrl()).resize(300, 300).centerCrop()
                                .into(imagemProd);
                    }
                    nomeProd.setText(produto.getNome());
                    DecimalFormat df = new DecimalFormat("0.00");
                    valorProd.setText("R$ "+df.format(produto.getValor()));
                    descricaoProd.setText(produto.getDescricao());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void abrirDialogExcluirProduto() {
        final Dialog dialog = new Dialog(InformacaoProdutoEmpresa.this);
        dialog.setContentView(R.layout.alert_excluir_produto);
        final BootstrapButton btConfirmaExcluir = dialog.findViewById(R.id.btnConfirmaExcluirProduto);
        final BootstrapButton btNegaExcluir = dialog.findViewById(R.id.btnNegaExcluirProduto);

        //Ação do botao de comfirmar exclusão
        btConfirmaExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(InformacaoProdutoEmpresa.this,
                        "Aguarde!","Excluindo produto..!",true);
                excluirProd();
                dialog.dismiss();
            }
        });

        //Ação do botao de negar exclusão
        btNegaExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void excluirProd() {
        try {
            referenciaFirebase.orderByChild("keyProduto").equalTo(produto.getKeyProduto()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Produto produto = postSnapshot.getValue(Produto.class);

                        //Apaga imagem do produto
                        if(!produto.getImagemUrl().equals("")) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            storage.getReferenceFromUrl(produto.getImagemUrl()).delete();
                        }
                        //Remove produto
                        referenciaFirebase.child(produto.getKeyProduto()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                 Toast.makeText(InformacaoProdutoEmpresa.this,
                                         "Produto excluido!",Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        finish();
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(InformacaoProdutoEmpresa.this,
                                            "Erro ao excluir!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            Toast.makeText(InformacaoProdutoEmpresa.this,
                    "Erro! "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
