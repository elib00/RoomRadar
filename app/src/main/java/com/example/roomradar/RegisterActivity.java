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
import com.example.roomradar.Entities.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText firstnameInput, lastnameInput, emailInput, passwordInput, contactNumberInput;
    private Button signUpButton;
    private TextView registerTypeIndicator;
    private Boolean isLandlord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        initializeView();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeView(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        assert bundle != null;
        isLandlord = bundle.getBoolean("isLandlord");

        firstnameInput = findViewById(R.id.firstnameInput);
        lastnameInput = findViewById(R.id.lastnameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signUpButton = findViewById(R.id.signUpButton);
        contactNumberInput = findViewById(R.id.contactNumberInput);
        registerTypeIndicator = findViewById(R.id.registerPageIndicator);

        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setHapticFeedbackEnabled(false); // to remove vibration

        passwordInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int drawableWidth = passwordInput.getCompoundDrawables()[2].getBounds().width();
                    if (event.getRawX() >= (passwordInput.getRight() - drawableWidth)) {
                        // Click event occurred on the end drawable
                        togglePasswordVisibility(passwordInput);
                        updateEndDrawable(passwordInput);
                        return true; // Consume the touch event
                    }
                }
                return false;
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = firstnameInput.getText().toString();
                String lastname = lastnameInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String contactNumber = contactNumberInput.getText().toString();

                User registerUser = new User(firstname, lastname, isLandlord, contactNumber);
                DatabaseManager.registerUser(RegisterActivity.this,email, password, registerUser);
            }
        });

        if(isLandlord){
            registerTypeIndicator.setText("Register as Landlord");
        }else{
            registerTypeIndicator.setText("Register as User");
        }
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