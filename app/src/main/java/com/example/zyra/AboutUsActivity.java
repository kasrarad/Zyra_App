package com.example.zyra;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    protected ImageView imageGarden;
    protected TextView textAboutText;
    protected TextView textAboutUs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        getSupportActionBar().setTitle("To Settings");

        setupUI();

        String text = "About Us";
        SpannableString ss = new SpannableString(text);

        SpannableString content = new SpannableString("About Us");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textAboutUs.setText(content);

    }

    public void setupUI() {
        imageGarden = findViewById(R.id.imageViewGarden);
        textAboutText = findViewById(R.id.textViewAboutText);
        textAboutUs = findViewById(R.id.textViewAboutUs);
    }
}
