package com.lucaspw8.gproductfirebase;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lucaspw8.gproductfirebase.Classes.Produto;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;
import com.lucaspw8.gproductfirebase.Helper.UsuarioPreferencias;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class EditarProduto extends AppCompatActivity {
    private EditText nomeProd;
    private EditText valorProd;
    private EditText descricao;
    private ImageView imgProd;
    private BootstrapButton btEditarProd;
    private BootstrapButton btCancelarEdProd;

    private StorageReference storageReference;
    private DatabaseReference reference;

    private Produto produto;
    //Verifica se o usuario selecionou a imagem da galeria
    private boolean imgSelecionada = false;

    UsuarioPreferencias usuarioPreferencias;

    private ProgressDialog progressDialog;
    private android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_produto);

        permissao();
        usuarioPreferencias = new UsuarioPreferencias(this);

        nomeProd = findViewById(R.id.edtNomeEditarProd);
        valorProd = findViewById(R.id.edtValorEditarProd);
        descricao = findViewById(R.id.edtDescricaoEditarProd);
        imgProd = findViewById(R.id.imgEditarProd);
        btEditarProd = findViewById(R.id.btnEditarProd);
        btCancelarEdProd = findViewById(R.id.btnEditarProdCancelar);

        produto = new Produto();
        Intent intent = getIntent();
        //Passando dados para o bundle
        Bundle dadosTela = intent.getExtras();

        produto.setNome(dadosTela.getString("nomeProd"));
        produto.setValor(Float.parseFloat(dadosTela.getString("valor")));
        produto.setDescricao(dadosTela.getString("descricao"));
        produto.setKeyProduto(dadosTela.getString("keyProduto"));
        produto.setUidUsuario(dadosTela.getString("uidUsuario"));
        produto.setImagemUrl(dadosTela.getString("imagemUrl"));

        Log.d("Nome do Prod",""+produto.getImagemUrl());
        nomeProd.setText(produto.getNome());
        valorProd.setText(String.valueOf(produto.getValor()));
        descricao.setText(produto.getDescricao());

        if(!produto.getImagemUrl().equals("")){
            Picasso.get().load(produto.getImagemUrl()).resize(500,500)
                    .into(imgProd);
        }

        //Acão do botao de Editar
        btEditarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btEditarProd.setEnabled(false);
                produto.setNome(nomeProd.getText().toString());
                produto.setValor(Float.parseFloat(valorProd.getText().toString()));
                produto.setDescricao(descricao.getText().toString());
                progressDialog = ProgressDialog.show(EditarProduto.this, "Aguarde.",
                        "Salvando dados!", true);

                //Verifica se uma nova imagem foi selecionada
                if(imgSelecionada){
                    //Verifica se prod ja tinha imagem
                    if(!produto.getImagemUrl().equals("")){
                        //Possui imagem
                        apagarImg();
                    }


                    //Não possui imagem
                    novaImg();

                }else{
                    salvarProd();
                }

            }
        });
        //Acão do botao de Cancelar
        btCancelarEdProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Acão do click da imagem
        imgProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,"Complete a ação usando"),123);
            }
        });

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

    /**
     * Salva a nova imagem do produto
     */
    private void novaImg() {
        storageReference = ConfiguracaoFirebase.getStorageReference();
        StorageReference montaImagemReferencia = storageReference
                .child("fotoProduto/"+usuarioPreferencias.getUidUsu()+"/"
                        +produto.getNome()+".jpg");
        imgProd.setDrawingCacheEnabled(true);
        imgProd.destroyDrawingCache();
        imgProd.buildDrawingCache();
        Bitmap bitmap = imgProd.getDrawingCache();
        ByteArrayOutputStream byteArray =  new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArray);

        byte [] data = byteArray.toByteArray();

        UploadTask uploadTask = montaImagemReferencia.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( EditarProduto.this,"Erro ao salvar imagem: "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                produto.setImagemUrl(downloadUrl.toString());
                salvarProd();

            }
        });
    }

    /**
     * Apaga a imagem atual do produto
     */
    private void apagarImg() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReferenceFromUrl(produto.getImagemUrl()).delete();
    }

    private void salvarProd() {
        reference = ConfiguracaoFirebase.getFirebase();
        reference.child("produto").child(produto.getKeyProduto())
                .setValue(produto).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(EditarProduto.this,"Produto atualizado!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(EditarProduto.this,"Erro ao salvar! "+task.getException().toString(),Toast.LENGTH_LONG).show();
                    }
                    btEditarProd.setEnabled(true);
                    progressDialog.dismiss();
            }
        });
    }


    /**
     * Coloca a imagem escolhida no imageView
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final int height = 500;
        final int width = 500;

        if(resultCode == Activity.RESULT_OK) {

            if (requestCode == 123) {
                Uri imagemSelecionada = data.getData();
                Picasso.get().load(imagemSelecionada).resize(width, height).centerCrop().into(imgProd);
                imgSelecionada = true;
                Log.d("Imagem teste","A imagem foi escolhida "+imagemSelecionada.toString());
            }
        }else{
            imgSelecionada = false;
            Log.d("Imagem teste","A imagem Não foi escolhida");
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Pede permissão de acesso a galeria do celular
     */
    public void permissao(){
        int PERMISSION_ALL = 1;
        String [] permition = {Manifest.permission.READ_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this,permition,PERMISSION_ALL);

    }
}
