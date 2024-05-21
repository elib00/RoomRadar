package com.example.roomradar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomradar.Database.DatabaseManager;
import com.example.roomradar.Entities.User;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterTenantActivity extends AppCompatActivity {
    private TextInputEditText firstnameSignUpTenantInput, lastnameSignUpTenantInput, emailSignUpTenantInput, passwordSignUpTenantInput;
    private Button signUpButtonTenant;
    private Boolean isLandlord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_tenant);

        initializeView();

    }

    private void initializeView(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        assert bundle != null;
        isLandlord = bundle.getBoolean("isLandlord");

        firstnameSignUpTenantInput = findViewById(R.id.firstnameSignUpTenantInput);
        lastnameSignUpTenantInput = findViewById(R.id.lastnameSignUpTenantInput);
        emailSignUpTenantInput = findViewById(R.id.emailSignUpTenantInput);
        passwordSignUpTenantInput = findViewById(R.id.passwordSignUpTenantInput);
        signUpButtonTenant = findViewById(R.id.signUpButtonTenant);



        signUpButtonTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = firstnameSignUpTenantInput.getText().toString();
                String lastname = lastnameSignUpTenantInput.getText().toString();
                String email = emailSignUpTenantInput.getText().toString();
                String password = passwordSignUpTenantInput.getText().toString();

                User registerUser = new User(firstname, lastname, isLandlord);
                DatabaseManager.registerUser(RegisterTenantActivity.this,email, password, registerUser);
            }
        });
    }
}