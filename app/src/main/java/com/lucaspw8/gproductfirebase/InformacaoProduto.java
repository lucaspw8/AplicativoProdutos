package com.lucaspw8.gproductfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaspw8.gproductfirebase.Classes.Empresa;
import com.lucaspw8.gproductfirebase.Classes.Produto;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class InformacaoProduto extends AppCompatActivity {
    //Elementos visuais
    private ImageView imagemProd;
    private TextView nomeProd;
    private TextView valorProd;
    private TextView descricaoProd;
    private TextView nomeVendedorInf;
    private TextView telefoneVendedorInf;
    private TextView enderecoVendedorInf;
    private ActionBar actionBar;

    private Produto produto;
    private Empresa empresa;

    private DatabaseReference referenciaFirebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacao_produto);

        imagemProd = findViewById(R.id.imgInformacaoComumProduto);
        nomeProd = findViewById(R.id.txtNomeInformacaoComumprod);
        valorProd = findViewById(R.id.txtValorInformacaoComumProd);
        nomeVendedorInf = findViewById(R.id.txtNomeVendedorInf);
        telefoneVendedorInf = findViewById(R.id.txtTelefoneVendedorInf);
        enderecoVendedorInf = findViewById(R.id.txtEnderecoVendedorInf);
        descricaoProd = findViewById(R.id.txtDescricaoInformacaoComumProd);


        //Pegando intent anterior
        Intent intent = getIntent();
        //Passando dados para o bundle
        Bundle dadosTela = intent.getExtras();

        empresa = new Empresa();
        produto = new Produto();
        //Passando dados do bundle para o produto
        produto.setNome(dadosTela.getString("nomeProd"));
        produto.setValor(Float.parseFloat(dadosTela.getString("valor")));
        produto.setDescricao(dadosTela.getString("descricao"));
        produto.setKeyProduto(dadosTela.getString("keyProduto"));
        produto.setUidUsuario(dadosTela.getString("uidUsuario"));
        produto.setImagemUrl(dadosTela.getString("imagemUrl"));

        referenciaFirebase = ConfiguracaoFirebase.getFirebase();
        referenciaFirebase.child("empresa").orderByChild("uidUsuario").equalTo(produto.getUidUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    empresa = postSnapshot.getValue(Empresa.class);
                    nomeVendedorInf.setText(empresa.getNome());
                    telefoneVendedorInf.setText("Contato: "+empresa.getTelefone());
                    enderecoVendedorInf.setText("Endereço\n"+empresa.getRua()+" "+empresa.getNumero()+" "+empresa.getBairro());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if(!produto.getImagemUrl().equals("")) {
            Picasso.get().load(produto.getImagemUrl()).resize(300, 300).centerCrop()
                    .into(imagemProd);
        }
        nomeProd.setText(produto.getNome());
        DecimalFormat df = new DecimalFormat("0.00");
        valorProd.setText("R$ "+df.format(produto.getValor()));
        descricaoProd.setText(produto.getDescricao());





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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
