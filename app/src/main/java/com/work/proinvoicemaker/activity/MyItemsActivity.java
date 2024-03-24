package com.work.proinvoicemaker.activity;

import static com.work.proinvoicemaker.Uitilty.Util.ChangeStatusBarColor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.work.proinvoicemaker.R;
import com.work.proinvoicemaker.Uitilty.Util;
import com.work.proinvoicemaker.adapter.RvAdapterMyItems;
import com.work.proinvoicemaker.databaseHelper.MyDatabaseHelperItems;
import com.work.proinvoicemaker.databaseHelper.dbUtility.DbBitmapUtility;
import com.work.proinvoicemaker.databinding.ActivityMyItemsBinding;
import com.work.proinvoicemaker.model.MyItemsList;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

public class MyItemsActivity extends AppCompatActivity {
    private ArrayList<String> arrayListId;
    public ArrayList<MyItemsList> arrayList;
    private ArrayList<String> arrayListMyItemInStock;
    private ArrayList<String> arrayListMyItemName;
    private ArrayList<byte[]> arrayListMyItemPic;
    private ArrayList<String> arrayListMyItemPrice;
    private MyDatabaseHelperItems myDBItem;
    private ActivityMyItemsBinding binding;

    private EditText etItemName;
    private EditText etItemPrice;
    private TextView tvRemoveAddMyItemPic;
    private ImageView ivAddItemPic;
    private ImageView ivDeleteBtn;
    private Button btnAdd;
    private String itemType = "new";
    private int itemPosition = -1;
    private InterstitialAd interstitialAd;
    Ad adfacebook;
    private final String TAG = MyItemsActivity.class.getSimpleName();
    private final int CAMERA_CAPTURE = 205;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_items);

        ChangeStatusBarColor(this);

        initView();
        initClickListener();
        setData();
    }

    private void initView() {
        myDBItem = new MyDatabaseHelperItems(this);
        arrayList = new ArrayList<>();
        arrayListId = new ArrayList<>();
        arrayListMyItemPic = new ArrayList<>();
        arrayListMyItemName = new ArrayList<>();
        arrayListMyItemPrice = new ArrayList<>();
        arrayListMyItemInStock = new ArrayList<>();
        storeDataInArrays();

        binding.setTitle("My Items");
        binding.toolbar.ivBack.setVisibility(View.VISIBLE);
        binding.setOnBackClick(v -> onBackPressed());
        binding.toolbar.ivAdd.setVisibility(View.VISIBLE);
    }

    private void initClickListener() {
        binding.setOnAddClick(v -> {
            itemType = "new";
            itemPosition = -1;
            addDialog();
            ivDeleteBtn.setVisibility(View.GONE);
        });

    }

    private void setData() {
        RvAdapterMyItems rvadapterMyItems = new RvAdapterMyItems(this, this, arrayListId, arrayListMyItemPic, arrayListMyItemName, arrayListMyItemPrice, arrayListMyItemInStock, value -> {
            itemPosition = value;
            itemType = "update";
            addDialog();
        });
        binding.rvMyItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMyItems.setAdapter(rvadapterMyItems);
    }

    @SuppressLint("SetTextI18n")
    private void addDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialogaddmyitem);

        etItemName = dialog.findViewById(R.id.et_dialog_add_item_name);
        etItemPrice = dialog.findViewById(R.id.et_dialog_add_item_price);
        tvRemoveAddMyItemPic = dialog.findViewById(R.id.bt_remove_add_my_item_pic);
        ivAddItemPic = dialog.findViewById(R.id.iv_dialog_add_item_pic);
        Button btnCancel = dialog.findViewById(R.id.bt_dialog_cancel_add_my_item);
        ivDeleteBtn = dialog.findViewById(R.id.bt_delete_my_item);
        btnAdd = dialog.findViewById(R.id.bt_dialog_add_my_item);
        TextView tvItemTypeName = dialog.findViewById(R.id.tv_dialog_add_item_title);

        if (itemType.equals("update")) {
            tvItemTypeName.setText("Edit Item");
        }

        if (itemType.equals("update")) {
            etItemName.setText(arrayListMyItemName.get(itemPosition));
            etItemPrice.setText(arrayListMyItemPrice.get(itemPosition));
            byte[] byteArrayExtra = arrayListMyItemPic.get(itemPosition);
            Bitmap image = DbBitmapUtility.getImage(byteArrayExtra);
            ivAddItemPic.setImageBitmap(image);

            tvRemoveAddMyItemPic.setVisibility(View.VISIBLE);
            btnAdd.setText("Update");
        }
        dialog.show();
        dialog.setCancelable(false);

        btnAdd.setOnClickListener(view -> {
            String myBtnName = btnAdd.getText().toString();
            if (myBtnName.equals("Update")) {
                if (etItemName.getText().toString().length() <= 0 || etItemPrice.getText().toString().length() <= 0) {
                    Toast.makeText(this, "Item name and Price are required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tvRemoveAddMyItemPic.getVisibility() != View.VISIBLE) {
                    Log.d("TAG", "NOIMG");
                    myDBItem.updateData(arrayListId.get(itemPosition), DbBitmapUtility.getBytes(DbBitmapUtility.getBitmapFromVectorDrawable(this, R.drawable.ic_company_logo)), etItemName.getText().toString(), etItemPrice.getText().toString(), "0");

                } else {
                    Bitmap bitmap = ((BitmapDrawable) ivAddItemPic.getDrawable()).getBitmap();
                    Bitmap.createScaledBitmap(bitmap, 320, 320, true);
                    myDBItem.updateData(arrayListId.get(itemPosition), DbBitmapUtility.getBytes(bitmap), etItemName.getText().toString(), etItemPrice.getText().toString(), "0");
                }
            } else {
                if (etItemName.getText().toString().length() <= 0 || etItemPrice.getText().toString().length() <= 0) {
                    Toast.makeText(this, "Item name and Price are required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tvRemoveAddMyItemPic.getVisibility() != View.VISIBLE) {
                    Log.d("TAG", "NOIMG");

                    myDBItem.addMyItem(DbBitmapUtility.getBytes(DbBitmapUtility.getBitmapFromVectorDrawable(this, R.drawable.ic_company_logo)), etItemName.getText().toString(), etItemPrice.getText().toString(), "0");
                } else {
                    myDBItem.addMyItem(DbBitmapUtility.getBytes(Bitmap.createScaledBitmap(((BitmapDrawable) ivAddItemPic.getDrawable()).getBitmap(), 320, 320, true)), etItemName.getText().toString(), etItemPrice.getText().toString(), "0");
                }
            }
            dialog.dismiss();
            recreate();
            setAds();
        });
        ivAddItemPic.setOnClickListener(view -> pickImage());
        tvRemoveAddMyItemPic.setOnClickListener(view -> {
            tvRemoveAddMyItemPic.setVisibility(View.INVISIBLE);
            ivAddItemPic.setImageBitmap(null);
            ivAddItemPic.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_company_logo));
        });
        ivDeleteBtn.setOnClickListener(v -> {
            myDBItem.deleteOneRow(arrayListId.get(itemPosition));
            dialog.dismiss();
            recreate();
            setAds();
        });
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            setAds();
        });
    }

    @SuppressLint("SetTextI18n")
    private void pickImage() {
        final Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.dialogdelete);
        Button btnCamera = dialog1.findViewById(R.id.btn_dialog_delete);
        Button btnGallery = dialog1.findViewById(R.id.btn_dialog_cancel_delete);
        dialog1.setCancelable(true);
        dialog1.show();
        btnCamera.setText("Camera");
        btnGallery.setText("Gallery");
        TextView textView = dialog1.findViewById(R.id.tv_dialog_delete_caption);
        textView.setText("Choose image source");
        textView.setTextColor(getResources().getColor(R.color.title_field_color_blue));
        ((TextView) dialog1.findViewById(R.id.tv_dialog_delete_text)).setText("Choose an image from gallery or take an image with camera");
        btnGallery.setOnClickListener(view1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction("android.intent.action.GET_CONTENT");
            startActivityForResult(Intent.createChooser(intent, "Pick an image"), 999);
            dialog1.dismiss();
        });
        btnCamera.setOnClickListener(view12 -> {
            try {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureIntent, CAMERA_CAPTURE);
            } catch (ActivityNotFoundException e) {
                Toast toast = Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
                toast.show();
            }
            dialog1.dismiss();
        });
    }

    public void storeDataInArrays() {
        Cursor readAllData = myDBItem.readAllData();
        if (readAllData.getCount() == 0) {
            binding.tvNoDataMyItem.setVisibility(View.VISIBLE);
            binding.rvMyItems.setVisibility(View.GONE);
            return;
        }
        while (readAllData.moveToNext()) {
            arrayListId.add(readAllData.getString(0));
            arrayListMyItemPic.add(readAllData.getBlob(1));
            arrayListMyItemName.add(readAllData.getString(2));
            arrayListMyItemPrice.add(readAllData.getString(3));
            arrayListMyItemInStock.add(readAllData.getString(4));
        }
        Log.d("hhghdc", "onCreate: " + arrayListId.size());
        binding.tvNoDataMyItem.setVisibility(View.GONE);
        binding.rvMyItems.setVisibility(View.VISIBLE);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1) {
            recreate();
        }
        if (i == 999 && i2 == -1 && intent != null) {
            CropImage.activity(intent.getData()).setOutputCompressQuality(5).setOutputCompressFormat(Bitmap.CompressFormat.PNG).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }
        if (i == 203) {
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(intent);
            if (i2 == -1 && activityResult != null) {
                ivAddItemPic.setImageURI(activityResult.getUri());
                Log.d("iv12", "onActivityResult: " + activityResult.getUri());
                tvRemoveAddMyItemPic.setVisibility(View.VISIBLE);
            }
        }
        if (i == CAMERA_CAPTURE) {
            Bitmap image = (Bitmap) intent.getExtras().get("data");
            CropImage.activity(Util.getImageUri(this, image)).setOutputCompressQuality(5).setOutputCompressFormat(Bitmap.CompressFormat.PNG).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }
    }

    private void setAds() {
        interstitialAd = new com.facebook.ads.InterstitialAd(MyItemsActivity.this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");
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
                adfacebook = ad;
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
