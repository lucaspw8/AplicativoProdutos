package com.lucaspw8.gproductfirebase;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.lucaspw8.gproductfirebase.Classes.Produto;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class InformacaoProduto extends AppCompatActivity {
    //Elementos visuais
    private ImageView imagemProd;
    private TextView nomeProd;
    private TextView valorProd;
    private TextView descricaoProd;
    private ActionBar actionBar;

    Produto produto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacao_produto);

        imagemProd = findViewById(R.id.imgInformacaoComumProduto);
        nomeProd = findViewById(R.id.txtNomeInformacaoComumprod);
        valorProd = findViewById(R.id.txtValorInformacaoComumProd);
        descricaoProd = findViewById(R.id.txtDescricaoInformacaoComumProd);


        //Pegando intent anterior
        Intent intent = getIntent();
        //Passando dados para o bundle
        Bundle dadosTela = intent.getExtras();

        produto = new Produto();

        produto.setNome(dadosTela.getString("nomeProd"));
        produto.setValor(Float.parseFloat(dadosTela.getString("valor")));
        produto.setDescricao(dadosTela.getString("descricao"));
        produto.setKeyProduto(dadosTela.getString("keyProduto"));
        produto.setUidUsuario(dadosTela.getString("uidUsuario"));
        produto.setImagemUrl(dadosTela.getString("imagemUrl"));

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
