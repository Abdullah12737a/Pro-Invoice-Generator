package com.work.proinvoicemaker.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.work.proinvoicemaker.R;
import com.work.proinvoicemaker.databinding.ActivityPrintSuccessBinding;

public class PrintSuccessActivity extends AppCompatActivity {
    private ActivityPrintSuccessBinding binding;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_print_success);
        initView();
        initClickListener();

    }

    private void initView() {
    }

    private void initClickListener() {
        binding.btnBackToMain.setOnClickListener(view -> finish());
    }
}
