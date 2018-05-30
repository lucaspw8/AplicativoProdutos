package com.lucaspw8.gproductfirebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lucaspw8.gproductfirebase.Classes.Empresa;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;

public class CadastroEmpresaActivity extends AppCompatActivity {

    private EditText nomeEmpresa;
    private EditText telefoneEmpresa;
    private EditText ruaEmpresa;
    private EditText bairroEmpresa;
    private EditText numeroEmpresa;
    private EditText complementoEmpresa;
    private Button btnCadastrarEmpresa;


    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference referenceFirebase;

    private Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_empresa);
        autenticacao = FirebaseAuth.getInstance();
        referenceFirebase = FirebaseDatabase.getInstance().getReference();

        nomeEmpresa = (EditText) findViewById(R.id.edtNomeEmpresa);
        telefoneEmpresa = (EditText) findViewById(R.id.edtTelefoneEmpresa);
        ruaEmpresa = (EditText) findViewById(R.id.edtRua);
        bairroEmpresa = (EditText) findViewById(R.id.edtBairro);
        numeroEmpresa = (EditText) findViewById(R.id.edtNumero);
        complementoEmpresa = (EditText) findViewById(R.id.edtComplemento);
        btnCadastrarEmpresa = (Button) findViewById(R.id.btnCadastrarEmpresa);

        btnCadastrarEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                empresa = new Empresa();
                //Atribui o email do usuario logado no momento
                empresa.setEmailDono(autenticacao.getCurrentUser().getEmail());
                empresa.setNome(nomeEmpresa.getText().toString());
                empresa.setTelefone(Integer.parseInt(telefoneEmpresa.getText().toString()));
                empresa.setRua(ruaEmpresa.getText().toString());
                empresa.setBairro(bairroEmpresa.getText().toString());
                empresa.setNumero(Integer.parseInt(numeroEmpresa.getText().toString()));
                empresa.setComplemento(complementoEmpresa.getText().toString());
                cadastarEmpresa(empresa);
            }
        });
    }

    public void cadastarEmpresa(Empresa empresa){
        try {
            referenceFirebase = ConfiguracaoFirebase.getFirebase().child("empresa");
            referenceFirebase.push().setValue(empresa);
            Toast.makeText( CadastroEmpresaActivity.this,"Empresa cadastrada com sucesso",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CadastroEmpresaActivity.this,TelaPrincipal.class);
            startActivity(intent);

        }catch (Exception e){
            Toast.makeText( CadastroEmpresaActivity.this,"Erro ao cadastrar "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
