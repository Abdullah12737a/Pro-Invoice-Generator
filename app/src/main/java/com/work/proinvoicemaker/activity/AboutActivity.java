package com.work.proinvoicemaker.activity;

import static com.work.proinvoicemaker.Uitilty.Util.ChangeStatusBarColor;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.work.proinvoicemaker.BuildConfig;
import com.work.proinvoicemaker.R;
import com.work.proinvoicemaker.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding binding;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about);

        ChangeStatusBarColor(this);

        binding.tvVersionName.setText("version : " + BuildConfig.VERSION_NAME);

        binding.tvDevName.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
