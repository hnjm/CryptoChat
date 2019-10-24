package com.example.CryptoChat.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import com.example.CryptoChat.R;
import com.example.CryptoChat.services.AuthenticationManager;
import com.example.CryptoChat.services.KeyValueStore;
//import UserInformation;

public class Login extends AppCompatActivity implements Authenticatable {

    Button button_login, button_goto_signup;
    ImageButton button_login_bio;
    String str_username, str_password, str_confirm_password;
    private static EditText editText_username;
    private static EditText editText_password;

    public static void open(Context ctx){
        Intent intent = new Intent(ctx, Login.class);
        ctx.startActivity(intent);
    }

    private void succeed(){
        String Uid = KeyValueStore.getInstance().get(getApplicationContext(),KeyValueStore.UID);
        AuthenticationManager.setUid(Uid);
        MainActivity.open(Login.this);
    }

    private void fail(){
        AuthenticationManager.setUid(null);
        AuthenticationManager.lock();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //TODO: If already logged in, directly to main

        button_login = findViewById(R.id.button_login);
        button_login_bio = findViewById(R.id.button_login_fingerprint);
        button_goto_signup = findViewById(R.id.button_register);
        editText_username = (EditText) findViewById(R.id.username);
        editText_password = (EditText) findViewById(R.id.password);


//        click login button:
        button_login.setOnClickListener(new View.OnClickListener() { //为组件设置点击事件
            @Override
            public void onClick(View v) {
                str_username = editText_username.getText().toString();
                str_password = editText_password.getText().toString();

//                mRef.child("users").child(user.getId()).setValue(userMap);


//                todo: checker whether the username is already in the server database
                boolean username_used = false;


//              check whether one of the blank is not typed
                if (str_username.equals("")) {
                    Toast.makeText(Login.this, "Please enter username", Toast.LENGTH_SHORT).show();
                } else if (str_password.equals("")) {
                    Toast.makeText(Login.this, "Please enter password", Toast.LENGTH_SHORT).show();
                }


//              no such username

                else if (!str_username.equals(KeyValueStore.getInstance().get(getApplicationContext(),KeyValueStore.USERNAME))) {
                    Toast.makeText(Login.this, "Username not found.", Toast.LENGTH_SHORT).show();

                } else if (!str_password.equals(KeyValueStore.getInstance().get(getApplicationContext(),KeyValueStore.PASSWORD))) {
                    Toast.makeText(Login.this, "Wrong password.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Login.this, "Successfully log in.", Toast.LENGTH_SHORT).show();


                    //TODO: Set Authentication manager
                    succeed();


                }


            }

        });

        button_login_bio.setOnClickListener(view -> {
            verifyAuth();
        });

        button_goto_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                todo:go to sign in page

                Toast.makeText(Login.this, "go to Sign up page", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }

        });
    }

    /*
    * Implementing Authenticatable
    * */
    @Override
    public BiometricPrompt.AuthenticationCallback getCallback() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.e("Auth", "Auth error");
                //TODO: In prod env, should exit the program
                succeed();

                //System.exit(0);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                AuthenticationManager.unlock();
                succeed();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                fail();
                Log.e("Auth", "Auth failed");
            }
        };
    }

    @Override
    public void verifyAuth() {
        if (!AuthenticationManager.getAuthState()) {
            showBiometricPrompt();
        } else {
            succeed();
        }
    }

    @Override
    public void initUI() {

    }

    @Override
    public FragmentActivity getBioActivity() {
        return Login.this;
    }



}
