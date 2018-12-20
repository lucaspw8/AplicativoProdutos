package com.lucaspw8.gproductfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lucaspw8.gproductfirebase.Classes.Empresa;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;
import com.lucaspw8.gproductfirebase.Helper.UsuarioPreferencias;

public class CadastroEmpresaActivity extends AppCompatActivity {

    private BootstrapEditText nomeEmpresa;
    private BootstrapEditText telefoneEmpresa;
    private BootstrapEditText ruaEmpresa;
    private BootstrapEditText bairroEmpresa;
    private BootstrapEditText numeroEmpresa;
    private BootstrapEditText complementoEmpresa;
    private BootstrapButton btnCadastrarEmpresa;
    private Menu menu1;

    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference referenceFirebase;

    private Empresa empresa;
    UsuarioPreferencias usuPref;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_empresa);
        usuPref = new UsuarioPreferencias(CadastroEmpresaActivity.this);
        autenticacao = FirebaseAuth.getInstance();
        referenceFirebase = FirebaseDatabase.getInstance().getReference();

        nomeEmpresa =  findViewById(R.id.edtNomeEmpresa);
        telefoneEmpresa =  findViewById(R.id.edtTelefoneEmpresa);
        ruaEmpresa =  findViewById(R.id.edtRua);
        bairroEmpresa =  findViewById(R.id.edtBairro);
        numeroEmpresa =  findViewById(R.id.edtNumero);
        complementoEmpresa =  findViewById(R.id.edtComplemento);
        btnCadastrarEmpresa =  findViewById(R.id.btnCadastrarEmpresa);

        //Criando mascara para o campo de telefone
        SimpleMaskFormatter mask = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(telefoneEmpresa,mask);
        telefoneEmpresa.addTextChangedListener(mtw);
        //Fim da mascara
        Log.d("IDUsu",""+usuPref.getUidUsu());
        btnCadastrarEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(CadastroEmpresaActivity.this, "Aguarde.",
                        "Cadastrando Empresa..!", true);
                btnCadastrarEmpresa.setClickable(false);


                empresa = new Empresa();
                empresa.setUidUsuario(usuPref.getUidUsu());
                empresa.setNome(nomeEmpresa.getText().toString());

                empresa.setRua(ruaEmpresa.getText().toString());
                empresa.setBairro(bairroEmpresa.getText().toString());

                empresa.setNumero(numeroEmpresa.getText().toString());
                empresa.setTelefone(telefoneEmpresa.getText().toString());

                empresa.setComplemento(complementoEmpresa.getText().toString());
                if(!empresa.getNome().equals("")){
                    cadastarEmpresa(empresa);
                }else{
                    progressDialog.dismiss();
                    btnCadastrarEmpresa.setClickable(true);
                    Toast.makeText(CadastroEmpresaActivity.this,"Imforme ao menos o nome para a empresa",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    /**
     * Cadastra a empresa passada por parametro
     * @param empresa
     */
    public void cadastarEmpresa(Empresa empresa){
        try {
            referenceFirebase = ConfiguracaoFirebase.getFirebase().child("empresa");
            String key = referenceFirebase.push().getKey();
            empresa.setKeyEmpresa(key);
            referenceFirebase.child(key).setValue(empresa);
            Toast.makeText( CadastroEmpresaActivity.this,"Empresa cadastrada com sucesso",Toast.LENGTH_LONG).show();
            finish();


        }catch (Exception e){
            progressDialog.dismiss();
            btnCadastrarEmpresa.setClickable(true);
            Toast.makeText( CadastroEmpresaActivity.this,"Erro ao cadastrar "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu1 = menu;
        getMenuInflater().inflate(R.menu.menu_comprador,menu1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.sair_consumidor) {
            deslogar();
        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogar() {
        autenticacao.signOut();
        finish();
    }
}
