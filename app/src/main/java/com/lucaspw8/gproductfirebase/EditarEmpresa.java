package com.lucaspw8.gproductfirebase;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.lucaspw8.gproductfirebase.Classes.Empresa;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;
import com.lucaspw8.gproductfirebase.Helper.EmpresaPreferencias;

public class EditarEmpresa extends AppCompatActivity {
    private ActionBar actionBar;
    //Campos
    private EditText edtNomeEmpresa;
    private EditText edtTelefoneEmpresa;
    private EditText edtRuaEmpresa;
    private EditText edtBairroEmpresa;
    private EditText edtNumeroEmpresa;
    private EditText edtComplementoEmpresa;
    //Botões
    private BootstrapButton btSalvarEmpresa;
    private BootstrapButton btCancelarEmpresa;

    private EmpresaPreferencias empresaPref;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_empresa);

        empresaPref = new EmpresaPreferencias(EditarEmpresa.this);

        edtNomeEmpresa = findViewById(R.id.edtEditarNomeEmpresa);
        edtTelefoneEmpresa = findViewById(R.id.edtEditarTelefoneEmpresa);
        edtRuaEmpresa = findViewById(R.id.edtEditarRuaEmpresa);
        edtBairroEmpresa = findViewById(R.id.edtEditarBairroEmpresa);
        edtNumeroEmpresa = findViewById(R.id.edtEditarNumeroEmpresa);
        edtComplementoEmpresa = findViewById(R.id.edtEditarComplementoEmpresa);

        btSalvarEmpresa = findViewById(R.id.btnSalvarEmpresa);
        btCancelarEmpresa = findViewById(R.id.btnEditarEmpresaCancelar);

        //Criando mascara para o campo de telefone
        SimpleMaskFormatter mask = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(edtTelefoneEmpresa,mask);
        edtTelefoneEmpresa.addTextChangedListener(mtw);
        //Fim da mascara

        edtNomeEmpresa.setText(empresaPref.getEmpresa().getNome());
        edtTelefoneEmpresa.setText(empresaPref.getEmpresa().getTelefone());
        edtRuaEmpresa.setText(empresaPref.getEmpresa().getRua());
        edtBairroEmpresa.setText(empresaPref.getEmpresa().getBairro());
        edtNumeroEmpresa.setText(empresaPref.getEmpresa().getNumero());
        edtComplementoEmpresa.setText(empresaPref.getEmpresa().getComplemento());

        btSalvarEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btSalvarEmpresa.setEnabled(false);
                if(edtNomeEmpresa.getText().length() > 0) {
                    Empresa empresa = new Empresa();
                    empresa.setKeyEmpresa(empresaPref.getEmpresa().getKeyEmpresa());
                    empresa.setUidUsuario(empresaPref.getEmpresa().getUidUsuario());
                    empresa.setNome(edtNomeEmpresa.getText().toString());
                    empresa.setTelefone(edtTelefoneEmpresa.getText().toString());
                    empresa.setRua(edtRuaEmpresa.getText().toString());
                    empresa.setBairro(edtBairroEmpresa.getText().toString());
                    empresa.setNumero(edtNumeroEmpresa.getText().toString());
                    empresa.setComplemento(edtComplementoEmpresa.getText().toString());
                    salvar(empresa);
                }else{
                    btSalvarEmpresa.setEnabled(true);
                    Toast.makeText(EditarEmpresa.this,"Nome não pode estar vazio!",Toast.LENGTH_LONG).show();
                }
            }
        });

        btCancelarEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
     * Salva os dados da empresa
     * @param empresa
     */
    private void salvar(final Empresa empresa){
        reference = ConfiguracaoFirebase.getFirebase().child("empresa");
        reference.child(empresa.getKeyEmpresa()).setValue(empresa)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            btSalvarEmpresa.setEnabled(true);
                            empresaPref.salvarEmpresa(empresa);
                            Toast.makeText(EditarEmpresa.this,"Dados salvos!",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(EditarEmpresa.this,"Erro ao salvar! "+task.getException().toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
}
