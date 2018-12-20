package com.lucaspw8.gproductfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lucaspw8.gproductfirebase.Classes.Usuario;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;

public class CadastroUsuario extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText senha;
    private EditText repetirSenha;
    private RadioButton rbconsumidor;
    private RadioButton rbvendedor;
    private BootstrapButton btnCadastrar;

    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private ActionBar actionBar;

    private Usuario usuario;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nome =  findViewById(R.id.edtNomeUsu);
        email = findViewById(R.id.edtEmailUsu);
        senha = findViewById(R.id.edtSenhaUsu);
        repetirSenha = findViewById(R.id.edtRepetirSenhaUsu);
        rbconsumidor = findViewById(R.id.radioConsumidor);
        rbvendedor = findViewById(R.id.radioVendedor);
        btnCadastrar = findViewById(R.id.btnCadastrarUsu);


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(CadastroUsuario.this, "Aguarde.",
                        "Cadastrando Usuário..!", true);
                //Desabilita o click do botao
                btnCadastrar.setClickable(false);
                //Verifica se as senhas sao iguais
                if(senha.getText().toString().equals(repetirSenha.getText().toString())){
                    usuario = new Usuario();
                    usuario.setEmail(email.getText().toString());
                    usuario.setSenha(senha.getText().toString());
                    usuario.setNome(nome.getText().toString());
                    //Verifica se o radioButton esta marcado
                    if(rbconsumidor.isChecked()){
                        usuario.setTipoUsuario("CONSUMIDOR");
                    }else if(rbvendedor.isChecked()){
                        usuario.setTipoUsuario("VENDEDOR");
                    }
                    //Metodo que cadastra o usuario usando o FireBase
                    cadastrarUsuario();
                }else{
                    //Exibe mensagem de erro caso as senhas não correspondam
                    Toast.makeText(CadastroUsuario.this,"As senhas não correspondem",Toast.LENGTH_LONG).show();
                    //Habilita o click do botao
                    btnCadastrar.setClickable(true);
                    progressDialog.dismiss();
                }
            }
        });

        try {
            actionBar = getSupportActionBar();
            //Adiciona o botao de voltar no action bar
            actionBar.setDisplayHomeAsUpEnabled(true); //Mostrar o botão
            //getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
            //getSupportActionBar().setTitle("Seu titulo aqui");
        }catch (NullPointerException e){
            Log.w("Erro",e.getMessage());
        }
    }


    //Cadastra o usuario para se autenticar
    private void cadastrarUsuario() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
                .addOnCompleteListener(CadastroUsuario.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //retorna o Uid do usuario recem cadastrado
                            usuario.setUidUsuario(autenticacao.getUid());
                            insereUsuario(usuario);
                        }else{
                            //Habilita o click do botao
                            btnCadastrar.setClickable(true);
                            progressDialog.dismiss();
                            String erro = "";
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                erro = "Digite uma senha com no minimo 8 caracteres e que contenha letras e numeros";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erro = "O e-mail informado é invalido digite um novo e-mail";
                            }catch (FirebaseAuthUserCollisionException e){
                                erro = "Esse e-mail já esta cadastrado!";
                            } catch (Exception e) {
                                erro = "Erro ao cadastrar!";
                                e.printStackTrace();
                            }
                            Toast.makeText(CadastroUsuario.this,"Erro: "+erro,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Cadastra os restante dos dados do usuario recem cadastrado
     * @param usuario
     * @return
     */
    private boolean insereUsuario(Usuario usuario) {
        try {
            reference = ConfiguracaoFirebase.getFirebase().child("usuarios");
            String key = reference.push().getKey();
            usuario.setKeyUsuario(key);
            reference.child(key).setValue(usuario);
            Toast.makeText( CadastroUsuario.this,"Usuário cadastrado com sucesso",Toast.LENGTH_LONG).show();
            //Habilita o click do botao
            btnCadastrar.setClickable(true);
            progressDialog.dismiss();
            finish();

            return true;
        }catch (Exception e){
            //Habilita o click do botao
            btnCadastrar.setClickable(true);
            progressDialog.dismiss();
            Toast.makeText(CadastroUsuario.this,"Erro ao gravar o usuario",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
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
