package com.example.roomradar.CustomComponents;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.roomradar.R;

public class CustomPasswordTextInputEditText extends AppCompatEditText {
    private Drawable endDrawable;
    private boolean passwordVisible = false;

    public CustomPasswordTextInputEditText(Context context) {
        super(context);
        init();
    }

    public CustomPasswordTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomPasswordTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        endDrawable = ContextCompat.getDrawable(getContext(), R.drawable.password_visible_icon);
        System.out.println("hi");
        setEndDrawableVisible();
    }

    private void setEndDrawableVisible() {
        Drawable drawable = passwordVisible ? endDrawable : null;
        Drawable[] drawables = getCompoundDrawablesRelative();
        setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], drawables[1], drawable, drawables[3]);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && endDrawable != null) {
            Rect bounds = endDrawable.getBounds();
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (x >= (getWidth() - bounds.width()) && x <= (getWidth() - getPaddingEnd())
                    && y >= (getPaddingTop()) && y <= (getHeight() - getPaddingBottom())) {
                togglePasswordVisibility();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        setInputType(passwordVisible ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
        setSelection(getText().length());
        setEndDrawableVisible();
    }
}
