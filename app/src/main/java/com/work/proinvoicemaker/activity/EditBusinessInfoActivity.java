package com.work.proinvoicemaker.activity;

import static com.work.proinvoicemaker.Uitilty.Util.ChangeStatusBarColor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.work.proinvoicemaker.R;
import com.work.proinvoicemaker.databinding.ActivityEditBusinessInfoBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class EditBusinessInfoActivity extends AppCompatActivity {

    public String currencyString;
    public String itemName;
    public SharedPreferences share;
    public int spinnerPosition;
    private ActivityEditBusinessInfoBinding binding;
    private InterstitialAd interstitialAd;
    private final String TAG = EditBusinessInfoActivity.class.getSimpleName();

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_business_info);

        ChangeStatusBarColor(this);
        initView();
        setData();
        initClickListener();
    }

    private void initView() {
        binding.setTitle("Business Details");
        binding.toolbar.ivBack.setVisibility(View.VISIBLE);
        share = getSharedPreferences("shopinfo", 0);
        binding.setOnBackClick(view -> onBackPressed());
    }

    private void setData() {
        if (share.contains("shopname")) {
            binding.etShopNameEdit.setText(share.getString("shopname", "Instant Invoice"));
        } else {
            binding.etShopNameEdit.setText("");
        }
        if (share.contains("shopaddress")) {
            binding.etShopAddressEdit.setText(share.getString("shopaddress", ""));
        } else {
            binding.etShopAddressEdit.setText("");
        }
        if (share.contains("shopphone")) {
            binding.etShopPhoneEdit.setText(share.getString("shopphone", ""));
        } else {
            binding.etShopPhoneEdit.setText("");
        }
        if (share.contains("bottommessage")) {
            binding.etBottomMessageEdit.setText(share.getString("bottommessage", "Thanks for shopping with us!"));
        } else {
            binding.etBottomMessageEdit.setText("");
        }
        if (share.contains("imagePreferance")) {
            byte[] decode = Base64.decode(share.getString("imagePreferance", "default"), 0);
            binding.ivCompanyLogo.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
            binding.btnRemoveIcon.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        binding.spinnerCurrencyEdit.setAdapter(adapter);

        String string1 = share.getString(FirebaseAnalytics.Param.CURRENCY, "0");
        String[] currencyList = getResources().getStringArray(R.array.currencies);
        int position = 0;
        for (int i=0;i<currencyList.length;i++){
            if (currencyList[i].contains(string1)){
                position = i;
            }
        }
        binding.spinnerCurrencyEdit.setSelection(position);
        if (spinnerPosition == 1) {
            binding.etCreateCurrency.setVisibility(View.VISIBLE);
            binding.etCreateCurrency.setText(share.getString(FirebaseAnalytics.Param.CURRENCY, ""));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initClickListener() {

        binding.btnCancelInfo.setOnClickListener(v -> {
            finish();
            setAds();
        });
        binding.btnRemoveIcon.setOnClickListener(view -> {
            binding.ivCompanyLogo.setImageBitmap(null);
            binding.btnRemoveIcon.setVisibility(View.INVISIBLE);
            binding.ivCompanyLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_company_logo));
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
                } else {
                    binding.etCreateCurrency.setVisibility(View.GONE);
                }
            }
        });
        binding.btnUpdateInfo.setOnClickListener(view -> {
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
            } else {
                edit.putString("shopname", "");
            }
            if (binding.btnRemoveIcon.getVisibility() != View.VISIBLE) {
                Log.d("TAG", "NOIMG");
                edit.remove("imagePreferance");
            } else {
                Bitmap bitmap = ((BitmapDrawable) binding.ivCompanyLogo.getDrawable()).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, new ByteArrayOutputStream());
                edit.putString("namePreferance", itemName);
                edit.putString("imagePreferance", encodeTobase64(bitmap));
            }
            edit.putString("shopaddress", binding.etShopAddressEdit.getText().toString().trim());
            edit.putString("shopphone", binding.etShopPhoneEdit.getText().toString().trim());
            edit.putString("bottommessage", binding.etBottomMessageEdit.getText().toString().trim());
            edit.apply();
            setAds();
        });
        binding.ivCompanyLogo.setOnClickListener(view -> {
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
                Uri uri = activityResult.getUri();
                binding.ivCompanyLogo.setBackground(null);
                binding.ivCompanyLogo.setImageURI(uri);
                binding.btnRemoveIcon.setVisibility(View.VISIBLE);
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
