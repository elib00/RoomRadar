package com.example.roomradar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterTenantActivity extends AppCompatActivity {
    private Button signUpTenantButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_tenant);
    }

    private void initializeView(){
        signUpTenantButton = (Button) findViewById(R.id.signUpButton);

        signUpTenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
}