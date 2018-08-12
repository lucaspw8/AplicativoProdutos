package com.lucaspw8.gproductfirebase;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

    private EditText nomeProd;
    private EditText valorProd;
    private ImageView imgProd;
    private Button btCadastrarProd;

    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private StorageReference storageReference;
    private DatabaseReference reference;

    Preferencias preferencias;
    private Produto produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto);

        nomeProd = findViewById(R.id.edtNomeProd);
        valorProd = findViewById(R.id.edtValorProd);
        imgProd = findViewById(R.id.imgProduto);
        btCadastrarProd = findViewById(R.id.btnCadastrarProd);

        storageReference = ConfiguracaoFirebase.getStorageReference();
        preferencias = new Preferencias(CadastroProduto.this);
        carregarFotoPadrao();




        //Acão do botao de cadastrar
        btCadastrarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                produto = new Produto();
                produto.setNome(nomeProd.getText().toString());
                produto.setValor(Float.parseFloat(valorProd.getText().toString()));
                produto.setEmailEmpresa(preferencias.getEmailUsu());
                cadastrarProduto(produto);
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
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,"Selecione uma imagem!"),123);
            }
        });
    }

    private void carregarFotoPadrao(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/gproduct-3086b.appspot.com/o/produto_sem_foto.png?alt=media&token=a4443376-8f6d-4250-a4ea-bbfbf6d082a8");

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final int height = 300;
        final int width = 300;

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
                Toast.makeText( CadastroProduto.this,"Imagem cadastrada: "+downloadUrl,Toast.LENGTH_LONG).show();
            }
        });
    }



    public void cadastrarProduto(Produto produto){

        try {
            reference = ConfiguracaoFirebase.getFirebase().child("produto");
            reference.push().setValue(produto);
            Toast.makeText( CadastroProduto.this,"Produto cadastrado com sucesso",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CadastroProduto.this,EmpresaPrincipalActivity.class);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText( CadastroProduto.this,"Erro: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }



    }
}
