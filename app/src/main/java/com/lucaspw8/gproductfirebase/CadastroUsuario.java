package com.lucaspw8.gproductfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

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
    private Button btnCadastrar;

    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nome = (EditText) findViewById(R.id.edtNomeUsu);
        email =(EditText) findViewById(R.id.edtEmailUsu);
        senha =(EditText) findViewById(R.id.edtSenhaUsu);
        repetirSenha = (EditText)findViewById(R.id.edtRepetirSenhaUsu);
        rbconsumidor = (RadioButton)findViewById(R.id.radioConsumidor);
        rbvendedor =(RadioButton)findViewById(R.id.radioVendedor);
        btnCadastrar = (Button)findViewById(R.id.btnCadastrarUsu);


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                }
            }
        });
    }


    //Cadastra o usuario para se autenticar
    private void cadastrarUsuario() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
                .addOnCompleteListener(CadastroUsuario.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            insereUsuario(usuario);
                        }else{
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
    //Cadastra os restante dos dados do usuario recem cadastrado
    private boolean insereUsuario(Usuario usuario) {
        try {
            reference = ConfiguracaoFirebase.getFirebase().child("usuarios");
            reference.push().setValue(usuario);
            Toast.makeText( CadastroUsuario.this,"Usuário cadastrado com sucesso",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CadastroUsuario.this,MainActivity.class);
            startActivity(intent);
            return true;
        }catch (Exception e){
            Toast.makeText(CadastroUsuario.this,"Erro ao gravar o usuario",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }
    }


}