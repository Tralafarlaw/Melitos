package com.amuyu.melitos.Activitis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amuyu.melitos.CONSTANTES;
import com.amuyu.melitos.MainActivity;
import com.amuyu.melitos.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OnSuccessListener<AuthResult>, OnFailureListener {
    SignInButton google_btn;
    Button regular_btn;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth auth;
    String Mail ="", Pass="";
    TextInputEditText fUSer, fPass;
    TextInputLayout lUser, lPass;

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            Intent it = new Intent(this, MainActivity.class);
            startActivity(it);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        adjust();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.signin):{
                RSignin();
            }break;
            case (R.id.google_signin): {
                GsignIn();
            }break;
        }
    }
    private void init(){
        // inicializar campos
        regular_btn = findViewById(R.id.signin);
        google_btn = findViewById(R.id.google_signin);
        fUSer = findViewById(R.id.login_user_f);
        fPass = findViewById(R.id.login_pass_f);
        lUser = findViewById(R.id.login_user_l);
        lPass = findViewById(R.id.login_pass_l);
    }
    private void adjust(){
        //ajustar campos
        regular_btn.setOnClickListener(this);
        google_btn.setOnClickListener(this);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void GsignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, CONSTANTES.RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONSTANTES.RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void RSignin (){

        auth = FirebaseAuth.getInstance();
        Mail = fUSer.getText().toString();
        Pass = fPass.getText().toString();
        lUser.setErrorEnabled(false);
        lPass.setErrorEnabled(false);
        fUSer.setEnabled(false);
        fPass.setEnabled(false);
        if(!Mail.equals("") && !Pass.equals("")) {
            auth.createUserWithEmailAndPassword(Mail, Pass).addOnSuccessListener(this, this).addOnFailureListener(this, this);
        }


    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        //GoogleSignInAccount account = completedTask.getResult(ApiException.class);
        // Signed in successfully, show authenticated UI.
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
    }



    @Override
    public void onFailure(@NonNull Exception e) {
        if(e instanceof FirebaseAuthUserCollisionException){
            auth.signInWithEmailAndPassword(Mail, Pass).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity( it);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    fUSer.setEnabled(true);
                    fPass.setEnabled(true);
                    if(e instanceof FirebaseAuthInvalidUserException){
                        Toast.makeText(LoginActivity.this,R.string.error_suspended_account , Toast.LENGTH_LONG).show();
                    }
                    if(e instanceof FirebaseAuthInvalidCredentialsException){
                        fPass.setError(getText(R.string.error_wrong_pass));
                    }
                }
            });
        }else {
            fUSer.setEnabled(true);
            fPass.setEnabled(true);
            if(e instanceof FirebaseAuthWeakPasswordException){
                fPass.setError(getText(R.string.error_weak_pass));
            }
            if(e instanceof FirebaseAuthInvalidCredentialsException){
                fUSer.setError(getText(R.string.error_mail));
            }

        }
    }

    @Override
    public void onSuccess(AuthResult authResult) {
        Intent it = new Intent(LoginActivity.this, MainActivity.class);
        startActivity( it);
    }
}
