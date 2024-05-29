package com.example.roomradar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.roomradar.Database.DatabaseManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText password;
    private TextInputEditText email;
    private TextView forgotPassword;
    private Button registerAsTenantButton;
    private Button registerAsLandlordButton;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        initializeView();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        moveTaskToBack(true); // Move the task to the background instead of exiting
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save your app state here
        // Example: outState.putString("key", value);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore your app state here
        // Example: String value = savedInstanceState.getString("key");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save any data that needs to be saved
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restore any data that needs to be restored
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Save any data that needs to be saved
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Restore any data that needs to be restored
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeView(){
        email = (TextInputEditText) findViewById(R.id.emailLoginInput);
        password = (TextInputEditText) findViewById(R.id.passwordLoginInput);

        registerAsTenantButton = (Button) findViewById(R.id.registerAsTenantButton);
        registerAsLandlordButton = (Button) findViewById(R.id.registerAsLandlordButton);

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        login = (Button) findViewById(R.id.loginButton);

        // Set default input type for password field
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseManager.validateUser(LoginActivity.this, email.getText().toString(), password.getText().toString());
            }
        });

//        TextInputEditText passwordEditText = findViewById(R.id.passwordLoginInput);
        password.setHapticFeedbackEnabled(false); // to remove vibration
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int drawableWidth = password.getCompoundDrawables()[2].getBounds().width();
                    if (event.getRawX() >= (password.getRight() - drawableWidth)) {
                        // Click event occurred on the end drawable
                        togglePasswordVisibility(password);
                        updateEndDrawable(password);
                        return true; // Consume the touch event
                    }
                }
                return false;
            }
        });

        registerAsTenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("isLandlord", false);
                startActivity(intent);
            }
        });

        registerAsLandlordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("isLandlord", true);
                startActivity(intent);
            }
        });
    }

    private void togglePasswordVisibility(TextInputEditText editText) {
        if (editText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        // Move cursor to the end of the text
        editText.setSelection(Objects.requireNonNull(editText.getText()).length());
    }

    private void updateEndDrawable(TextInputEditText editText) {
        Drawable startDrawable = editText.getCompoundDrawables()[0];
        Drawable drawable;
        if (isPasswordVisible(editText)) {
            drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.password_visible_icon);
        } else {
            drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.password_protected_icon);
        }

        // Set the drawable on the end
        editText.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, drawable, null);
    }

    private boolean isPasswordVisible(TextInputEditText editText) {
        return editText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
    }
}