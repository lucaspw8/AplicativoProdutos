package com.lucaspw8.gproductfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lucaspw8.gproductfirebase.Classes.Usuario;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private EditText email;
    private EditText senha;
    private Button btnLogin;
    private Usuario usuario;
    private TextView novaConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.edtEmailLogin);
        senha = (EditText) findViewById(R.id.edtSenhaLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        novaConta = (TextView)findViewById(R.id.txtCrieConta);


        if(usuarioLogado()){
            Intent intent = new Intent(this,TelaPrincipal.class);
            abrirTela(intent);
        }else {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!email.getText().toString().equals("") && !senha.getText().toString().equals("")) {
                        usuario = new Usuario();
                        usuario.setEmail(email.getText().toString());
                        usuario.setSenha(senha.getText().toString());

                        validarlogin();
                    } else {
                        Toast.makeText(MainActivity.this, "Preencha os campos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //Chamar tela de cadastro de usuario
        novaConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CadastroUsuario.class);
                startActivity(intent);
            }
        });

    }


    private void validarlogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail().toString(),usuario.getSenha().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    AbrirTelaCadEmpresa();
                    Toast.makeText(MainActivity.this,"Login feito com sucesso!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"Usuário ou senha invalidos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public Boolean usuarioLogado(){
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if(usuario !=null){
            return true;
        }else{
            return false;
        }
    }

    private void AbrirTelaCadEmpresa() {
        Intent intent = new Intent(MainActivity.this,TelaPrincipal.class);
        startActivity(intent);
    }

    public void abrirTela(Intent intent){
        startActivity(intent);
    }
}
