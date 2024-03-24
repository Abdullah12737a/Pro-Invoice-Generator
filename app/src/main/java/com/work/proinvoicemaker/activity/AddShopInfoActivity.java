package com.work.proinvoicemaker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.work.proinvoicemaker.R;
import com.work.proinvoicemaker.databinding.ActivityAddShopInfoBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class AddShopInfoActivity extends AppCompatActivity {
    private String currencyString;
    public String itemNAme;
    private SharedPreferences share;
    private int spinnerPosition;
    private ActivityAddShopInfoBinding binding;
    private InterstitialAd interstitialAd;
    private final String TAG = EditBusinessInfoActivity.class.getSimpleName();

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_shop_info);
        initView();
        initClickListener();
    }

    private void initView() {
        binding.setTitle("Instant Invoice");
        share = getSharedPreferences("shopinfo", 0);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        binding.spinnerCurrencyEdit.setAdapter(adapter);
    }

    private void initClickListener() {
        binding.btnCancelInfo.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            getSharedPreferences("PREFERENCE", 0).edit().putBoolean("isFirstRun", false).apply();
            setAds();
        });
        binding.spinnerCurrencyEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                String[] split = adapterView.getSelectedItem().toString().split(" ");
                currencyString = split[1];
                spinnerPosition = adapterView.getSelectedItemPosition();
                if (currencyString.equals("Own")) {
                    binding.etCreateCurrency.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.btRemoveAddIcon.setOnClickListener(view -> {
            binding.btRemoveAddIcon.setVisibility(View.INVISIBLE);
            binding.ivAddLogo.setImageBitmap(null);
            binding.ivAddLogo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_company_logo));
        });
        binding.btbUpdateInfo.setOnClickListener(view -> {
            SharedPreferences.Editor edit = share.edit();
            if (currencyString.equals("No")) {
                edit.putString(FirebaseAnalytics.Param.CURRENCY, "No");
                edit.putString("currencyposition", String.valueOf(spinnerPosition));
            } else if (!currencyString.equals("Own")) {
                edit.putString(FirebaseAnalytics.Param.CURRENCY, currencyString);
                edit.putString("currencyposition", String.valueOf(spinnerPosition));
            } else if (binding.etCreateCurrency.getText().toString().length() != 0) {
                edit.putString(FirebaseAnalytics.Param.CURRENCY, binding.etCreateCurrency.getText().toString());
                edit.putString("currencyposition", String.valueOf(spinnerPosition));
            } else {
                Toast.makeText(this, "Type your own currency or \nChoose currency from the list", Toast.LENGTH_SHORT).show();
            }
            if (binding.etShopNameEdit.getText().toString().length() > 0) {
                edit.putString("shopname", binding.etShopNameEdit.getText().toString().trim());
            }
            if (binding.btRemoveAddIcon.getVisibility() != View.VISIBLE) {
                Log.d("TAG", "NOIMG");
            } else {
                Bitmap bitmap = ((BitmapDrawable) binding.ivAddLogo.getDrawable()).getBitmap();
                edit.putString("namePreferance", itemNAme);
                edit.putString("imagePreferance", encodeTobase64(bitmap));
            }
            edit.putString("shopaddress", binding.etShopAddressEdit.getText().toString().trim());
            edit.putString("shopphone", binding.etShopPhoneEdit.getText().toString().trim());
            edit.putString("bottommessage", binding.etBottomMessageEdit.getText().toString().trim());
            edit.apply();
            getSharedPreferences("PREFERENCE", 0).edit().putBoolean("isFirstRun", false).apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            setAds();
        });
        binding.ivAddLogo.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction("android.intent.action.GET_CONTENT");
            startActivityForResult(Intent.createChooser(intent, "Pick an image"), 123);
        });
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 123 && i2 == -1 && intent != null) {
            CropImage.activity(intent.getData()).setOutputCompressQuality(70).setOutputCompressFormat(Bitmap.CompressFormat.PNG).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }
        if (i == 203) {
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(intent);
            if (i2 == -1 && activityResult != null) {
                binding.ivAddLogo.setImageURI(activityResult.getUri());
                binding.btRemoveAddIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    public static String encodeTobase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String encodeToString = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
        Log.d("Image Log:", encodeToString);
        return encodeToString;
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void setAds() {
        interstitialAd = new com.facebook.ads.InterstitialAd(this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }
}
