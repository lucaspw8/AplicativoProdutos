package com.lucaspw8.gproductfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaspw8.gproductfirebase.Classes.Empresa;
import com.lucaspw8.gproductfirebase.Classes.Usuario;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;
import com.lucaspw8.gproductfirebase.Helper.EmpresaPreferencias;
import com.lucaspw8.gproductfirebase.Helper.Preferencias;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference referenceFirebase;
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
        usuario = new Usuario();
        referenceFirebase = FirebaseDatabase.getInstance().getReference();


        if(usuarioLogado()){
           tipoUsuario();
        }else {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!email.getText().toString().equals("") && !senha.getText().toString().equals("")) {

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
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    tipoUsuario();
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



    //Responsavel por abrir a telas apos o login dependendo do tipo de usuario
    private void tipoUsuario(){

        //Recebendo email do usuario logado no momento
        usuario.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //Buscando usuario cujo email é igual o email do usuario logado no momento
        referenceFirebase.child("usuarios").orderByChild("email").equalTo(usuario.getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    usuario.setTipoUsuario(postSnapshot.child("tipoUsuario").getValue().toString());
                    usuario.setNome(postSnapshot.child("nome").getValue().toString());
                    //Salvando nas preferencias de usuario
                    Preferencias preferencias = new Preferencias(MainActivity.this);
                    preferencias.salvarUsu(usuario);

                    if (usuario.getTipoUsuario().equals("VENDEDOR")) {
                                            //Tipo Vendedor
                        //Buscando empresa do Vendedor
                        referenceFirebase.child("empresa").orderByChild("emailDono").equalTo(usuario.getEmail()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Verifica se existe alguma empresa
                                if (dataSnapshot.hasChildren()) {
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        //Salvando os dados em um objeto Empresa
                                        Empresa empresa =  new Empresa();
                                        empresa.setNome(postSnapshot.child("nome").getValue().toString());
                                        empresa.setTelefone(Integer.parseInt(postSnapshot.child("telefone").getValue().toString()));
                                        empresa.setNumero(Integer.parseInt( postSnapshot.child("numero").getValue().toString()));
                                        empresa.setRua(postSnapshot.child("rua").getValue().toString());
                                        empresa.setBairro(postSnapshot.child("bairro").getValue().toString());
                                        empresa.setComplemento(postSnapshot.child("complemento").getValue().toString());

                                        //Salvando no SharedPreferencias de Empresa
                                        EmpresaPreferencias empresaPreferencias =  new EmpresaPreferencias(MainActivity.this);
                                        empresaPreferencias.salvarEmpresa(empresa);

                                        //Abrindo tela de empresa
                                        Intent intent = new Intent(MainActivity.this,EmpresaPrincipalActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                    //Não possui empresa
                                } else{
                                    Intent intent = new Intent(MainActivity.this,CadastroEmpresaActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });


                    } else if (usuario.getTipoUsuario().equals("CONSUMIDOR")) {
                        //Tipo Consumidor
                        AbrirTelaPrincipal();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void AbrirTelaPrincipal() {
        Intent intent = new Intent(MainActivity.this,TelaPrincipal.class);
        startActivity(intent);
        finish();
    }


}


