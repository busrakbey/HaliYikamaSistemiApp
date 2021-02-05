package com.example.haliyikamaapp.UI;

import android.animation.Animator;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity {

    private ImageView bookIconImageView, ilk_logo;
    private TextView bookITextView;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView, afterAnimationView;
    Button loginButton;
    EditText username_edittext, password_edittext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_activity);
        initViews();
        ilk_logo.setVisibility(VISIBLE);


        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                new CountDownTimer(6000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        bookITextView.setVisibility(GONE);
                        loadingProgressBar.setVisibility(GONE);
                        rootView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorSplashText));
                        bookIconImageView.setImageResource(R.drawable.logo_hali);

                        startAnimation();
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            }
        };
        handler.postDelayed(r, 1500);


    }

    private void initViews() {
        bookIconImageView = (ImageView) findViewById(R.id.bookIconImageView);
        bookITextView = (TextView) findViewById(R.id.bookITextView);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        afterAnimationView = (RelativeLayout) findViewById(R.id.afterAnimationView);
        ilk_logo = (ImageView) findViewById(R.id.ilk_logo);
        loginButton = (Button) findViewById(R.id.loginButton);
        username_edittext = (EditText) findViewById(R.id.usernameEditText);
        password_edittext = (EditText) findViewById(R.id.passwordEditText);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  OrtakFunction.tokenControl(Log.this);

                if (username_edittext.getText().toString().trim().equalsIgnoreCase("") ||
                        password_edittext.getText().toString().trim().equalsIgnoreCase(""))
                    MessageBox.showAlert(LoginActivity.this, "Lütfen kullanıcı adı ve parolayı eksiksiz bir şekilde giriniz.", false);
                else

                    OrtakFunction.getTtoken(LoginActivity.this, username_edittext.getText().toString(), password_edittext.getText().toString() );
            }
        });
    }

    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = bookIconImageView.animate();
        viewPropertyAnimator.x(50f);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ilk_logo.setVisibility(GONE);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                afterAnimationView.setVisibility(VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}