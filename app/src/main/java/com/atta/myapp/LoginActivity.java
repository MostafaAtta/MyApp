package com.atta.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView goToRegister;

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportActionBar().setTitle(R.string.login_activity_title);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null){
            //Not signed in, launch the Sign In Activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        goToRegister = findViewById(R.id.btnRegisterScreen);
        goToRegister.setOnClickListener(this);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view == goToRegister){

            Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
            startActivity(intent);
            finish();
        }else if (view == btnLogin){

            String email = inputEmail.getText().toString();
            final String password = inputPassword.getText().toString();

            //authenticate user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.

                            if (!task.isSuccessful()) {

                                Toast.makeText(LoginActivity.this, "auth_failed", Toast.LENGTH_LONG).show();

                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

        }
    }
}
