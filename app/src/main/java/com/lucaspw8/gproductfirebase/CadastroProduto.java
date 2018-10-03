package com.lucaspw8.gproductfirebase;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lucaspw8.gproductfirebase.Classes.Produto;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;
import com.lucaspw8.gproductfirebase.Helper.Preferencias;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;


public class CadastroProduto extends AppCompatActivity {

    private BootstrapEditText nomeProd;
    private BootstrapEditText valorProd;
    private BootstrapEditText descricao;
    private ImageView imgProd;
    private BootstrapButton btCadastrarProd;

    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private StorageReference storageReference;
    private DatabaseReference reference;

    Preferencias preferencias;
    private Produto produto;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto);

        nomeProd = findViewById(R.id.edtNomeProd);
        valorProd = findViewById(R.id.edtValorProd);
        descricao = findViewById(R.id.edtDescricao);
        imgProd = findViewById(R.id.imgProduto);
        btCadastrarProd = findViewById(R.id.btnCadastrarProd);

        storageReference = ConfiguracaoFirebase.getStorageReference();
        preferencias = new Preferencias(CadastroProduto.this);
        permissao();







        //Acão do botao de cadastrar
        btCadastrarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                produto = new Produto();
                produto.setNome(nomeProd.getText().toString());
                produto.setValor(Float.parseFloat(valorProd.getText().toString()));
                produto.setDescricao(descricao.getText().toString());
                produto.setEmailEmpresa(preferencias.getEmailUsu());
                btCadastrarProd.setClickable(false);
                progressDialog = ProgressDialog.show(CadastroProduto.this, "Aguarde.",
                        "Cadastrando Produto..!", true);
                cadastrarFotoProd();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Carrega a imagem da galeria
        imgProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,"Complete a ação usando"),123);
            }
        });
    }

    /**
     * Carrega a imagem padrão do produto
     */
    private void carregarFotoPadrao(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Log.d("Foto","chamou foto padrao");
        storageReference =
                storage.getReferenceFromUrl("gs://gproduct-3086b.appspot.com/produto_sem_foto.png");

        final int height = 300;
        final int width = 300;

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).resize(width,height).centerCrop().into(imgProd);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
            }
        }
    }

    private void cadastrarFotoProd(){
        StorageReference montaImagemReferencia = storageReference.child("fotoProduto/"+preferencias.getEmailUsu()+"/"+produto.getNome()+".jpg");
        imgProd.setDrawingCacheEnabled(true);
        Log.d("Diretorio",montaImagemReferencia.toString());
        imgProd.buildDrawingCache();
        Bitmap bitmap = imgProd.getDrawingCache();
        ByteArrayOutputStream byteArray =  new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArray);

        byte [] data = byteArray.toByteArray();

        UploadTask uploadTask = montaImagemReferencia.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( CadastroProduto.this,"Erro na imagem: "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                produto.setImagemUrl(downloadUrl.toString());
                cadastrarProduto(produto);

            }
        });
    }



    public void cadastrarProduto(Produto produto){

        try {
            reference = ConfiguracaoFirebase.getFirebase().child("produto");
            reference.push().setValue(produto);
            Toast.makeText( CadastroProduto.this,"Produto cadastrado com sucesso",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            nomeProd.setText("");
            valorProd.setText("");
            descricao.setText("");
            imgProd.clearColorFilter();

        }catch (Exception e){
            progressDialog.dismiss();
            Toast.makeText( CadastroProduto.this,"Erro: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }



    }

    /**
     * Pede permissão de acesso a galeria do celular
     */
    public void permissao(){
            int PERMISSION_ALL = 1;
            String [] permition = {Manifest.permission.READ_EXTERNAL_STORAGE};

            ActivityCompat.requestPermissions(this,permition,PERMISSION_ALL);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

}
