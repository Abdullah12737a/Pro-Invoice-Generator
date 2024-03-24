package com.work.proinvoicemaker.activity;

import static com.work.proinvoicemaker.Uitilty.Util.ChangeStatusBarColor;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.work.proinvoicemaker.R;
import com.work.proinvoicemaker.adapter.RvAdapterMyCustomers;
import com.work.proinvoicemaker.databaseHelper.MyDatabaseHelperCustomers;
import com.work.proinvoicemaker.databinding.ActivityMyFavCustomersBinding;
import com.work.proinvoicemaker.model.MyCustomersList;

import java.util.ArrayList;

public class MyFavCustomersActivity extends AppCompatActivity {
    private ArrayList<String> arrayListId;
    public ArrayList<MyCustomersList> arrayList;
    private ArrayList<String> arrayListMyCustomerAddress;
    private ArrayList<String> arrayListMyCustomerName;
    private ArrayList<String> arrayListMyCustomerPhone;
    private MyDatabaseHelperCustomers myDBCustomer;
    public RvAdapterMyCustomers rvadapterMyCustomers;
    private ActivityMyFavCustomersBinding binding;
    private InterstitialAd interstitialAd;
    String TAG = "Tag";

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_fav_customers);

        ChangeStatusBarColor(this);
        initView();
        storeDataInArrays();
        initClickListener();
        setData();

    }

    private void initView() {
        myDBCustomer = new MyDatabaseHelperCustomers(this);
        myDBCustomer = new MyDatabaseHelperCustomers(this);
        arrayList = new ArrayList<>();
        arrayListId = new ArrayList<>();
        arrayListMyCustomerName = new ArrayList<>();
        arrayListMyCustomerAddress = new ArrayList<>();
        arrayListMyCustomerPhone = new ArrayList<>();

        binding.setTitle("Customers");
        binding.setOnBackClick(v -> {
            onBackPressed();
            finish();
        });
        binding.toolbar.ivAdd.setVisibility(View.VISIBLE);
        binding.toolbar.ivBack.setVisibility(View.VISIBLE);
    }

    private void initClickListener() {

        binding.setOnAddClick(v -> {
            final Dialog dialog = new Dialog(MyFavCustomersActivity.this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialogaddnewcustomer);
            final EditText editText = dialog.findViewById(R.id.et_dialog_customer_name);
            final EditText editText2 = dialog.findViewById(R.id.et_dialog_customer_address);
            final EditText editText3 = dialog.findViewById(R.id.et_dialog_customer_phone);
            dialog.show();
            dialog.setCancelable(false);
            dialog.findViewById(R.id.bt_delete_customer).setVisibility(View.GONE);
            dialog.findViewById(R.id.btn_dialog_add_customer_cancel).setOnClickListener(view -> {
                dialog.dismiss();
                setAds();
            });
            dialog.findViewById(R.id.btn_dialog_add_customer).setOnClickListener(view -> {
                if (editText.getText().toString().length() <= 0 || editText2.getText().toString().length() <= 0 || editText3.getText().toString().length() <= 0) {
                    Toast.makeText(MyFavCustomersActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                myDBCustomer.addMyItem(editText.getText().toString(), editText2.getText().toString(), editText3.getText().toString());
                dialog.dismiss();
                recreate();
                setAds();
            });
        });
    }

    private void setData() {
        rvadapterMyCustomers = new RvAdapterMyCustomers(this, this, arrayListId, arrayListMyCustomerName, arrayListMyCustomerAddress, arrayListMyCustomerPhone);
        binding.rvListCustomers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvListCustomers.setAdapter(rvadapterMyCustomers);
    }

    public void storeDataInArrays() {
        Cursor readAllData = myDBCustomer.readAllData();
        if (readAllData.getCount() == 0) {
            binding.tvNoDataCustomer.setVisibility(View.VISIBLE);
            binding.rvListCustomers.setVisibility(View.GONE);
            return;
        }
        while (readAllData.moveToNext()) {
            arrayListId.add(readAllData.getString(0));
            arrayListMyCustomerName.add(readAllData.getString(1));
            arrayListMyCustomerAddress.add(readAllData.getString(2));
            arrayListMyCustomerPhone.add(readAllData.getString(3));
        }
        binding.tvNoDataCustomer.setVisibility(View.GONE);
        binding.rvListCustomers.setVisibility(View.VISIBLE);
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
