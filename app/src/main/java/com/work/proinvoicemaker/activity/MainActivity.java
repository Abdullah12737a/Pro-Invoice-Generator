package com.work.proinvoicemaker.activity;

import static com.work.proinvoicemaker.Uitilty.Util.ChangeStatusBarColor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.work.proinvoicemaker.R;
import com.work.proinvoicemaker.adapter.RvAdapter;
import com.work.proinvoicemaker.databaseHelper.MyDatabaseHelper;
import com.work.proinvoicemaker.databaseHelper.MyDatabaseHelperCustomers;
import com.work.proinvoicemaker.databaseHelper.MyDatabaseHelperItems;
import com.work.proinvoicemaker.databinding.ActivityGenerateReceiptBinding;
import com.work.proinvoicemaker.model.ItemList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private double advanceLong;
    private ArrayList<ItemList> arrayList;
    private double balanceLong;
    private Bitmap bitmap;
    private double calculateDiscount;
    double calculateDiscount2;
    private double calculateTax;
    private String checkString;
    public int color = 1;
    public String countListString;
    private String currencyString;
    public double deliFeeLong;
    public double discountLong;
    public DrawerLayout drawerLayout;
    private int fontInt = 0;
    private int imageQuality = 70;
    public String itemListString;
    private int listArray;
    private final MyDatabaseHelper myDB = new MyDatabaseHelper(this);
    private MyDatabaseHelperCustomers myDBCustomer;
    private NavigationView navigationView;
    private String paidStatus;
    public String priceListString;
    public float scalingFactor;
    private SharedPreferences share;
    private SharedPreferences share2;
    public SharedPreferences share3;
    public double taxLong;
    public ActionBarDrawerToggle toggle;
    public double total;
    private ActivityGenerateReceiptBinding binding;
    String TAG = "Tag";
    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout ladView;
    private com.facebook.ads.InterstitialAd interstitialAd;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_generate_receipt);

        ChangeStatusBarColor(this);

        if (getSharedPreferences("PREFERENCE", 0).getBoolean("isFirstRun", true)) {
            finish();
            startActivity(new Intent(this, AddShopInfoActivity.class));
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        }

        MobileAds.initialize(this, initializationStatus -> {
        });
        AudienceNetworkAds.initialize(this);
        AdView adView = new AdView(this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);

        LinearLayout adContainer = findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();

        AudienceNetworkAds.initialize(this);

        setAds();
        initView();
        setData();
        initClickListener();
        loadNativeAd();
    }

    private void initView() {

    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        myDBCustomer = new MyDatabaseHelperCustomers(this);
        paidStatus = "unpaid";
        binding.rvList.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<ItemList> arrayList = new ArrayList<>();
        this.arrayList = arrayList;
        arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        float f = (float) point.x;
        if (f > 1000.0f) {
            scalingFactor = 0.87f;
        } else if (f < 1000.0f && f > 700.0f) {
            scalingFactor = 0.8f;
        } else if (f < 700.0f && f > 400.0f) {
            scalingFactor = 0.67f;
        } else if (f >= 400.0f || f <= 300.0f) {
            scalingFactor = 0.57f;
        } else {
            scalingFactor = 0.67f;
        }
        binding.constraintLayoutPrint.setScaleX(scalingFactor);
        binding.constraintLayoutPrint.setScaleY(scalingFactor);
        setSupportActionBar(binding.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        //Navigation header
        share3 = getSharedPreferences("shopinfo", 0);
        View headerView = navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.tv_nameMenu);
        TextView navPhone = headerView.findViewById(R.id.tv_phoneMenu);
        if (share3.getString("shopname", "Business Name").isEmpty()) {
            navName.setText("Business Name");
        } else {
            navName.setText(share3.getString("shopname", "Business Name"));
        }

        if (share3.getString("shopphone", "+123456789").isEmpty()) {
            navPhone.setText("+123456789");
        } else {
            navPhone.setText(share3.getString("shopphone", "+123456789"));
        }


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.open, R.string.close);
        toggle = actionBarDrawerToggle;
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        binding.toolbar.setNavigationIcon(R.drawable.ic_iv_nav_icon);
        setInvoiceBackground();
        setShopInfo();
        binding.etDiscount.setKeyListener(DigitsKeyListener.getInstance(true, true));
        binding.etAdvance.setKeyListener(DigitsKeyListener.getInstance(true, true));
        binding.etDeliFees.setKeyListener(DigitsKeyListener.getInstance(true, true));
        binding.etTax.setKeyListener(DigitsKeyListener.getInstance(true, true));
        SharedPreferences sharedPreferences = getSharedPreferences("colorinfo", 0);
        share = sharedPreferences;
        changeInvoiceColor();
        if (share.contains("imagequality")) {
            imageQuality = share.getInt("imagequality", 70);
        }
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private void initClickListener() {
        RvAdapter rvadapter = new RvAdapter(this, this.arrayList);
        binding.rvList.setAdapter(rvadapter);
        binding.checkBoxPaid.setOnCheckedChangeListener((compoundButton, z) -> {
            if (binding.checkBoxPaid.isChecked()) {
                paidStatus = "paid";
                binding.ivPaid.setVisibility(View.VISIBLE);
                return;
            }
            paidStatus = "unpaid";
            binding.ivPaid.setVisibility(View.GONE);
        });
        binding.ivAddBackground.setOnClickListener(view -> startActivityForResult(new Intent(this, ChangeInvoiceBackgroundActivity.class), 222));
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_item_about:
                    startActivity(new Intent(this, AboutActivity.class));
                    break;
                case R.id.menu_item_fav_customers :
                    startActivity(new Intent(this, MyFavCustomersActivity.class));
                    break;
                case R.id.menu_item_help :
                    startActivity(new Intent(this, HelpActivity.class));
                    break;
                case R.id.menu_item_image_quality :
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.setContentView(R.layout.outputqualitychoose);
                    Button button = dialog.findViewById(R.id.btn_dialog_ok_quality);
                    Button button2 = dialog.findViewById(R.id.btn_dialog_cancel_quality);
                    final RadioButton radioButton = dialog.findViewById(R.id.radioButton_high);
                    final RadioButton radioButton2 = dialog.findViewById(R.id.radioButton_medium);
                    final RadioButton radioButton3 = dialog.findViewById(R.id.radioButton_low);
                    dialog.setCancelable(false);
                    dialog.show();
                    radioButton2.setChecked(true);
                    if (imageQuality == 90) {
                        radioButton.setChecked(true);
                    } else if (imageQuality == 70) {
                        radioButton2.setChecked(true);
                    } else if (imageQuality == 40) {
                        radioButton3.setChecked(true);
                    }
                    button2.setOnClickListener(view -> dialog.dismiss());
                    button.setOnClickListener(view -> {
                        if (radioButton.isChecked()) {
                            SharedPreferences.Editor edit = share.edit();
                            edit.putInt("imagequality", 90);
                            edit.apply();
                            Toast.makeText(this, "Quality changed to high!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else if (radioButton2.isChecked()) {
                            SharedPreferences.Editor edit2 = share.edit();
                            edit2.putInt("imagequality", 70);
                            edit2.apply();
                            Toast.makeText(this, "Quality changed to medium!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else if (radioButton3.isChecked()) {
                            SharedPreferences.Editor edit3 = share.edit();
                            edit3.putInt("imagequality", 40);
                            edit3.apply();
                            Toast.makeText(this, "Quality changed to low!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(this, "Please choose a quality", Toast.LENGTH_SHORT).show();
                        }
                        imageQuality = share.getInt("imagequality", 70);
                    });
                    break;
                case R.id.menu_item_my_items :
                    startActivity(new Intent(this, MyItemsActivity.class));
                    break;
                case R.id.menu_item_printed_invoices :
                    startActivity(new Intent(this, PrintedReceiptsActivity.class));
                    break;
                case R.id.menu_item_rate_us:
                    try {
                        MainActivity mainActivity = MainActivity.this;
                        mainActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getPackageName())));
                        break;
                    } catch (ActivityNotFoundException unused) {
                        MainActivity mainActivity2 = MainActivity.this;
                        mainActivity2.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                        break;
                    }
                case R.id.menu_item_setting :
                    startActivity(new Intent(this, EditBusinessInfoActivity.class));
                    break;
                case R.id.menu_item_share_app :
                    String message = getString(R.string.app_name) + " is generate various types of invoice with different different background as per you choose. You can download it from google play store via link : http://play.google.com/store/apps/details?id=" + getPackageName();
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    intent.putExtra("android.intent.extra.TEXT", message);
                    startActivity(Intent.createChooser(intent, "Share via"));
                    break;
            }
            return true;
        });
        binding.btnAddFavCustomer.setOnClickListener(view -> {
            if (binding.etCustomerName.getText().toString().length() <= 0 || binding.etCustomerAddress.getText().toString().length() <= 0 || binding.etCustomerPhone.getText().toString().length() <= 0) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }
            myDBCustomer.addMyItem(binding.etCustomerName.getText().toString(), binding.etCustomerAddress.getText().toString(), binding.etCustomerPhone.getText().toString());
            binding.btnAddFavCustomer.setVisibility(View.GONE);
        });
        binding.btnShowDeco.setOnClickListener(view -> {
            if (binding.cardViewDeco.getVisibility() == View.VISIBLE) {
                binding.cardViewDeco.setVisibility(View.GONE);
            } else {
                binding.cardViewDeco.setVisibility(View.VISIBLE);
                binding.btnShowDeco.getResources().getDrawable(R.drawable.ic_iv_arrow_down);
            }
        });
        Date time = Calendar.getInstance().getTime();
        PrintStream printStream = System.out;
        printStream.println("Current time => " + time);
        binding.btnBlue.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyInvoiceColor", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("invoiceColor", "1");
            myEdit.apply();

            SharedPreferences.Editor edit = share.edit();
            edit.putString("colorChange", "1");
            edit.apply();
            changeInvoiceColor();
            MainActivity mainActivity = MainActivity.this;
            binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
        });
        binding.btnGreen.setOnClickListener(view -> {
            SharedPreferences.Editor edit = share.edit();
            edit.putString("colorChange", "2");
            edit.apply();
            changeInvoiceColor();
            MainActivity mainActivity = MainActivity.this;
            binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
        });
        binding.btnRed.setOnClickListener(view -> {
            SharedPreferences.Editor edit = share.edit();
            edit.putString("colorChange", "3");
            edit.apply();
            changeInvoiceColor();
            MainActivity mainActivity = MainActivity.this;
            binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
        });
        binding.btnPink.setOnClickListener(view -> {
            SharedPreferences.Editor edit = share.edit();
            edit.putString("colorChange", "4");
            edit.apply();
            changeInvoiceColor();
            MainActivity mainActivity = MainActivity.this;
            binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
        });
        binding.btnMainBlue.setOnClickListener(view -> {
            SharedPreferences.Editor edit = share.edit();
            edit.putString("colorChange", "5");
            edit.apply();
            changeInvoiceColor();
            MainActivity mainActivity = MainActivity.this;
            binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
        });
        binding.btnGrey.setOnClickListener(view -> {
            SharedPreferences.Editor edit = share.edit();
            edit.putString("colorChange", "6");
            edit.apply();
            changeInvoiceColor();
            MainActivity mainActivity = MainActivity.this;
            binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
        });
        binding.btnOrange.setOnClickListener(view -> {
            SharedPreferences.Editor edit = share.edit();
            edit.putString("colorChange", "7");
            edit.apply();
            changeInvoiceColor();
            MainActivity mainActivity = MainActivity.this;
            binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
        });
        binding.btnYellow.setOnClickListener(view -> {
            SharedPreferences.Editor edit = share.edit();
            edit.putString("colorChange", "8");
            edit.apply();
            changeInvoiceColor();
            MainActivity mainActivity = MainActivity.this;
            binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
        });
        binding.btnViolet.setOnClickListener(view -> {
            SharedPreferences.Editor edit = share.edit();
            edit.putString("colorChange", "9");
            edit.apply();
            changeInvoiceColor();
            MainActivity mainActivity = MainActivity.this;
            binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
        });
        binding.btnBrown.setOnClickListener(view -> {
            SharedPreferences.Editor edit = share.edit();
            edit.putString("colorChange", "10");
            edit.apply();
            changeInvoiceColor();
            MainActivity mainActivity = MainActivity.this;
            binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
        });
        binding.btnViolet11.setOnClickListener(view -> {
            SharedPreferences.Editor edit = share.edit();
            edit.putString("colorChange", "11");
            edit.apply();
            changeInvoiceColor();
            MainActivity mainActivity = MainActivity.this;
            binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
        });
        binding.btnGreen11.setOnClickListener(view -> {
            SharedPreferences.Editor edit = share.edit();
            edit.putString("colorChange", "12");
            edit.apply();
            changeInvoiceColor();
            MainActivity mainActivity = MainActivity.this;
            binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
        });
        binding.etAdvance.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @SuppressLint("SetTextI18n")
            public void afterTextChanged(Editable editable) {
                Editable text = binding.etAdvance.getText();
                Objects.requireNonNull(text);
                if (text.toString().length() > 0) {
                    Editable text2 = binding.etAdvance.getText();
                    Objects.requireNonNull(text2);
                    if (!text2.toString().equals(".")) {
                        TextView textView = binding.tvAdvance;
                        textView.setText("-" + binding.etAdvance.getText().toString());
                        updateTotalPrice();
                        return;
                    }
                    binding.etAdvance.setText("0.");
                    EditText textInputEditText = binding.etAdvance;
                    Editable text3 = binding.etAdvance.getText();
                    Objects.requireNonNull(text3);
                    textInputEditText.setSelection(text3.length());
                    return;
                }
                updateTotalPrice();
            }
        });
        binding.etTax.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @SuppressLint("SetTextI18n")
            public void afterTextChanged(Editable editable) {
                Editable text = binding.etTax.getText();
                Objects.requireNonNull(text);
                if (text.toString().length() > 0) {
                    Editable text2 = binding.etTax.getText();
                    Objects.requireNonNull(text2);
                    if (text2.toString().equals(".")) {
                        binding.etTax.setText("0.");
                        EditText textInputEditText = binding.etTax;
                        Editable text3 = binding.etTax.getText();
                        Objects.requireNonNull(text3);
                        textInputEditText.setSelection(text3.length());
                    } else if (Double.parseDouble(binding.etTax.getText().toString()) > 100.0d) {
                        binding.etTax.setText("100");
                        EditText textInputEditText2 = binding.etTax;
                        Editable text4 = binding.etTax.getText();
                        Objects.requireNonNull(text4);
                        textInputEditText2.setSelection(text4.length());
                    }
                }
                TextView textView = binding.tvTax;
                textView.setText("+" + binding.etTax.getText().toString());
                updateTotalPrice();
            }
        });
        binding.etDiscount.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @SuppressLint("SetTextI18n")
            public void afterTextChanged(Editable editable) {
                Editable text = binding.etDiscount.getText();
                Objects.requireNonNull(text);
                if (text.toString().length() > 0) {
                    Editable text2 = binding.etDiscount.getText();
                    Objects.requireNonNull(text2);
                    if (text2.toString().equals(".")) {
                        binding.etDiscount.setText("0.");
                        binding.etDiscount.setSelection(binding.etDiscount.getText().length());
                    } else if (Double.parseDouble(binding.etDiscount.getText().toString()) > 100.0d) {
                        binding.etDiscount.setText("100");
                        binding.etDiscount.setSelection(binding.etDiscount.getText().length());
                    }
                }
                TextView textView = binding.tvDiscount;
                textView.setText("-" + binding.etDiscount.getText().toString());
                updateTotalPrice();
            }
        });
        binding.etOrderId.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (binding.etOrderId.getText().toString().length() > 0) {
                    binding.tvPrintOrderId.setText(binding.etOrderId.getText().toString());
                }
            }
        });
        binding.etOrderDate.setOnClickListener(new View.OnClickListener() {
            final Calendar instance = Calendar.getInstance();
            final int i = instance.get(1);
            final int i2 = instance.get(2);
            final int i3 = instance.get(5);

            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, R.style.DatePickerDialog, (datePicker, i, i2, i3) -> {
                    String str = i3 + "/" + (i2 + 1) + "/" + i;
                    binding.etOrderDate.setText(str);
                    binding.tvPrintOrderDate.setText(str);
                }, i, i2, i3).show();
            }
        });
        binding.etCustomerName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                binding.tvPrintCustomerName.setText(binding.etCustomerName.getText().toString());
                if (binding.etCustomerName.getText().toString().length() <= 0 || binding.etCustomerAddress.getText().toString().length() <= 0 || binding.etCustomerPhone.getText().toString().length() <= 0) {
                    binding.btnAddFavCustomer.setVisibility(View.GONE);
                } else {
                    binding.btnAddFavCustomer.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.etCustomerAddress.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                binding.tvPrintCustomerAddress.setText(binding.etCustomerAddress.getText().toString());
                if (binding.etCustomerName.getText().toString().length() <= 0 || binding.etCustomerAddress.getText().toString().length() <= 0 || binding.etCustomerPhone.getText().toString().length() <= 0) {
                    binding.btnAddFavCustomer.setVisibility(View.GONE);
                } else {
                    binding.btnAddFavCustomer.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.etCustomerPhone.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                binding.tvPrintCustomerPhone.setText(binding.etCustomerPhone.getText().toString());
                if (binding.etCustomerName.getText().toString().length() <= 0 || binding.etCustomerAddress.getText().toString().length() <= 0 || binding.etCustomerPhone.getText().toString().length() <= 0) {
                    binding.btnAddFavCustomer.setVisibility(View.GONE);
                } else {
                    myDBCustomer.compareCustomerData(binding.etCustomerPhone.getText().toString(), binding.btnAddFavCustomer, binding.etCustomerName.getText().toString());
                }
            }
        });
        binding.etDeliFees.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @SuppressLint("SetTextI18n")
            public void afterTextChanged(Editable editable) {
                Editable text = binding.etDeliFees.getText();
                Objects.requireNonNull(text);
                if (text.toString().length() > 0) {
                    Editable text2 = binding.etDeliFees.getText();
                    Objects.requireNonNull(text2);
                    if (!text2.toString().equals(".")) {
                        TextView textView = binding.tvPrintDeliFeeFinal;
                        textView.setText("+" + binding.etDeliFees.getText().toString());
                        updateTotalPrice();
                        return;
                    }
                    binding.etDeliFees.setText("0.");
                    EditText textInputEditText = binding.etDeliFees;
                    Editable text3 = binding.etDeliFees.getText();
                    Objects.requireNonNull(text3);
                    textInputEditText.setSelection(text3.length());
                    return;
                }
                updateTotalPrice();
            }
        });
        binding.btnClearAll.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialogdelete);
            Button button = dialog.findViewById(R.id.btn_dialog_delete);
            dialog.setCancelable(false);
            dialog.show();
            button.setText("Clear");
            ((TextView) dialog.findViewById(R.id.tv_dialog_delete_caption)).setText("Clear All?");
            ((TextView) dialog.findViewById(R.id.tv_dialog_delete_text)).setText("Are you sure you want to clear all input information?");
            dialog.findViewById(R.id.btn_dialog_cancel_delete).setOnClickListener(view12 -> dialog.dismiss());
            button.setOnClickListener(view13 -> {
                clearAll();
                dialog.dismiss();
            });
            setAds();
        });
        binding.btnChangeTitleFont.setOnClickListener(view -> changeFont());
        binding.btnAddItems.setOnClickListener(view -> addData());
        binding.btSpinnerCustomer.setOnClickListener(view -> {
            String[] strArr = myDBCustomer.getAllCustomers().toArray(new String[0]);
            if (strArr.length > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MaterialThemeDialog);
                builder.setTitle("Choose from favourites");
                builder.setSingleChoiceItems(strArr, -1, (dialogInterface, i) -> {
                    Cursor fetchAllNames = myDBCustomer.fetchAllNames();
                    if (fetchAllNames != null && fetchAllNames.moveToFirst()) {
                        fetchAllNames.moveToPosition(i);
                        binding.etCustomerName.setText(fetchAllNames.getString(1));
                        binding.etCustomerAddress.setText(fetchAllNames.getString(2));
                        binding.etCustomerPhone.setText(fetchAllNames.getString(3));
                    }
                });
                builder.setPositiveButton("OK", (dialog, which) -> {
                });

                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(arg0 -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue4)));
                dialog.show();

            } else {
                Toast.makeText(this, "No favourite Customers!\nPlease add favourite customers first!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvList.setAdapter(rvadapter);
        binding.rvList.addOnItemTouchListener(new RvAdapter.RecyclerTouchListener(getApplicationContext(), this.binding.rvList, new RvAdapter.ClickListener() {
            public void onClick(View view, int i) {
                MainActivity mainActivity = MainActivity.this;
                mainActivity.checkString = mainActivity.arrayList.get(i).getItemName();
                if (checkString.length() > 0) {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.dialogaddnew);
                    final EditText editText = dialog.findViewById(R.id.et_update_dialog_item);
                    final EditText editText2 = dialog.findViewById(R.id.et_dialog_update_count);
                    final EditText editText3 = dialog.findViewById(R.id.et_dialog_update_price);
                    Button button = dialog.findViewById(R.id.btn_dialog_delete);
                    dialog.setCancelable(false);
                    dialog.show();
                    final String[] strArr = new MyDatabaseHelperItems(getApplicationContext()).getAllNames().toArray(new String[0]);
                    dialog.findViewById(R.id.btn_show_spinner_item).setOnClickListener(view1 -> new AlertDialog.Builder(MainActivity.this).setSingleChoiceItems(strArr, -1, (dialogInterface, i1) -> {
                        String[] split = strArr[i1].split("\nPrice: ");
                        String str = split[0];
                        editText3.setText(split[1].replaceAll("\\D+", ""));
                        editText.setText(str);
                    }).setPositiveButton("Ok", (dialogInterface, i1) -> {
                    }).setTitle("Choose from my items").show());
                    ((TextView) dialog.findViewById(R.id.tv_dialog_delete_caption)).setText("Edit Item");
                    editText.setText(arrayList.get(i).getItemName());
                    editText2.setText(arrayList.get(i).getItemCount());
                    editText3.setText(arrayList.get(i).getItemPrice());
                    button.setText("Update");
                    editText2.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void afterTextChanged(Editable editable) {
                            if (editText2.getText().toString().length() < 1 || Integer.parseInt(editText2.getText().toString()) < 1) {
                                editText2.setText("1");
                            }
                        }
                    });
                    dialog.findViewById(R.id.btn_dialog_count_down).setOnClickListener(view14 -> {
                        if (Integer.parseInt(editText2.getText().toString()) > 1) {
                            editText2.setText(String.valueOf(Integer.parseInt(editText2.getText().toString()) - 1));
                        }
                    });
                    dialog.findViewById(R.id.btn_dialog_count_up).setOnClickListener(view15 -> editText2.setText(String.valueOf(Integer.parseInt(editText2.getText().toString()) + 1)));
                    dialog.findViewById(R.id.btn_dialog_cancel_delete).setOnClickListener(view16 -> dialog.dismiss());
                    final int i2 = i;
                    button.setOnClickListener(view17 -> {
                        if (editText.getText().toString().length() == 0 || editText2.getText().toString().length() == 0 || editText3.getText().toString().length() == 0) {
                            Toast.makeText(MainActivity.this, "Enter all the fields", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String obj = editText.getText().toString();
                        String obj2 = editText2.getText().toString();
                        String obj3 = editText3.getText().toString();
                        arrayList.set(i2, new ItemList(obj, obj2, obj3, Double.parseDouble(obj2) * Double.parseDouble(obj3)));
                        binding.rvList.setAdapter(new RvAdapter(MainActivity.this, arrayList));
                        updateTotalPrice();
                        dialog.dismiss();
                    });
                }
            }

            public void onLongClick(View view, final int i) {
                MainActivity mainActivity = MainActivity.this;
                mainActivity.checkString = mainActivity.arrayList.get(i).getItemName();
                if (checkString.length() > 0) {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.setContentView(R.layout.dialogdelete);
                    dialog.setCancelable(false);
                    dialog.show();
                    dialog.findViewById(R.id.btn_dialog_cancel_delete).setOnClickListener(view18 -> dialog.dismiss());
                    dialog.findViewById(R.id.btn_dialog_delete).setOnClickListener(view19 -> {
                        if (arrayList.size() < 11) {
                            arrayList.remove(i);
                            arrayList.add(new ItemList("", "", "", 0.0d));
                            MainActivity mainActivity1 = MainActivity.this;
                            mainActivity1.listArray--;
                        } else {
                            arrayList.remove(i);
                        }
                        dialog.dismiss();
                        binding.rvList.setAdapter(new RvAdapter(MainActivity.this, arrayList));
                        updateTotalPrice();
                    });
                }
            }
        }));
        binding.rvList.setAdapter(rvadapter);
        binding.btnPrint.setOnClickListener(view -> {
            if (arrayList.get(0).getItemName().length() == 0) {
                Toast.makeText(MainActivity.this, "Add items before print!", Toast.LENGTH_SHORT).show();
                return;
            }
            printReceipt();
            setAds();
            startActivity(new Intent(MainActivity.this, PrintSuccessActivity.class));
        });
    }

    public void setAds() {
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

    private void loadNativeAd() {
        nativeAd = new NativeAd(this, getResources().getString(R.string.Facebook_Native_placement));
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                inflateAd(nativeAd);
                inflateAd1(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(TAG, "Native ad impression logged!");
            }
        };
        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
    }

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        nativeAdLayout = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        ladView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(ladView);

        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);
        MediaView nativeAdIcon = ladView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = ladView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = ladView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = ladView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = ladView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = ladView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = ladView.findViewById(R.id.native_ad_call_to_action);

        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeAd.registerViewForInteraction(
                ladView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }

    private void inflateAd1(NativeAd nativeAd) {

        nativeAd.unregisterView();

        nativeAdLayout = findViewById(R.id.native_ad_container1);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        ladView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(ladView);

        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);
        MediaView nativeAdIcon = ladView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = ladView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = ladView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = ladView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = ladView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = ladView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = ladView.findViewById(R.id.native_ad_call_to_action);

        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeAd.registerViewForInteraction(
                ladView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }

    @SuppressLint("SetTextI18n")
    public void printReceipt() {
        if (balanceLong <= 0.0d || !binding.checkBoxPaid.isChecked()) {
            bitmap = createBitmapFromLayout(binding.constraintLayoutPrint);
            String str = "invoice" + System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 29) {
                ContentResolver contentResolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", str + ".jpg");
                contentValues.put("mime_type", "image/jpg");
                contentValues.put("relative_path", Environment.DIRECTORY_PICTURES + "/Instant Invoice");
                Uri insert = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                try {
                    Objects.requireNonNull(insert);
                    OutputStream openOutputStream = contentResolver.openOutputStream(insert);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, openOutputStream);
                    addToSQLite();
                    clearAll();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byteArrayOutputStream.toByteArray();
                    try {
                        Objects.requireNonNull(openOutputStream);
                        openOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e2) {
                    e2.printStackTrace();
                }
            } else {
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Instant Invoice");
                file.mkdirs();
                File file2 = new File(file, str);
                if (file2.exists()) {
                    file2.delete();
                }
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    addToSQLite();
                    clearAll();
                    ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2);
                    byteArrayOutputStream2.toByteArray();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                MediaScannerConnection.scanFile(this, new String[]{file2.getPath()}, new String[]{"image/jpeg"}, null);
            }
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialogdelete);
            Button button = dialog.findViewById(R.id.btn_dialog_delete);
            dialog.setCancelable(false);
            dialog.show();
            button.setText("Print");
            ((TextView) dialog.findViewById(R.id.tv_dialog_delete_caption)).setText("⚠️ Warning for PAID STAMP!");
            ((TextView) dialog.findViewById(R.id.tv_dialog_delete_text)).setText("The total amount is: " + binding.tvGrandTotals.getText().toString() + this.currencyString + " and the advance paid is only: " + this.advanceLong + this.currencyString + "\n\nThe amount customer left to pay is: " + this.binding.tvBalance.getText().toString() + this.currencyString + "\n\n⚠️ You should use Paid STAMP in fully paid conditions only! \n\nAre you sure you want to proceed to print the invoice with the PAID STAMP included?");
            dialog.findViewById(R.id.btn_dialog_cancel_delete).setOnClickListener(view -> dialog.dismiss());
            button.setOnClickListener(view -> {
                ConstraintLayout constraintLayout = binding.constraintLayoutPrint;
                MainActivity mainActivity = MainActivity.this;
                mainActivity.bitmap = mainActivity.createBitmapFromLayout(constraintLayout);
                String str = "invoice" + System.currentTimeMillis();
                if (Build.VERSION.SDK_INT >= 29) {
                    ContentResolver contentResolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_display_name", str + ".jpg");
                    contentValues.put("mime_type", "image/jpg");
                    contentValues.put("relative_path", Environment.DIRECTORY_PICTURES + "/Instant Invoice");
                    Uri insert = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    try {
                        Objects.requireNonNull(insert);
                        OutputStream openOutputStream = contentResolver.openOutputStream(insert);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, openOutputStream);
                        addToSQLite();
                        clearAll();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byteArrayOutputStream.toByteArray();
                        try {
                            Objects.requireNonNull(openOutputStream);
                            openOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e2) {
                        e2.printStackTrace();
                    }
                } else {
                    File file = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Instant Invoice");
                    file.mkdirs();
                    File file2 = new File(file, str);
                    if (file2.exists()) {
                        file2.delete();
                    }
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file2);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        addToSQLite();
                        clearAll();
                        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2);
                        byteArrayOutputStream2.toByteArray();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                    MediaScannerConnection.scanFile(this, new String[]{file2.getPath()}, new String[]{"image/jpeg"}, null);
                }
                dialog.dismiss();
            });
        }
    }

    public Bitmap createBitmapFromLayout(View view) {
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(makeMeasureSpec, makeMeasureSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap createBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.translate((float) (-view.getScrollX()), (float) (-view.getScrollY()));
        view.draw(canvas);
        return createBitmap;
    }

    public void clearAll() {
        binding.etOrderId.setText("");
        binding.etOrderDate.setText("");
        binding.etCustomerName.setText("");
        binding.etCustomerAddress.setText("");
        binding.etCustomerPhone.setText("");
        binding.etDeliFees.setText("");
        binding.etTax.setText("");
        binding.etAdvance.setText("");
        binding.etDiscount.setText("");
        binding.checkBoxPaid.setChecked(false);
        this.listArray = 0;
        binding.tvPrintOrderId.setText(binding.etOrderId.getText().toString());
        binding.tvPrintOrderDate.setText(binding.etOrderDate.getText().toString());
        this.arrayList = null;
        ArrayList<ItemList> arrayList = new ArrayList<>();
        this.arrayList = arrayList;
        arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        this.arrayList.add(new ItemList("", "", "", 0.0d));
        binding.rvList.setAdapter(new RvAdapter(this, this.arrayList));
        updateTotalPrice();
        binding.etOrderId.requestFocus();
    }

    public void changeFont() {
        Button button;
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogchoosefont);
        Button button2 = dialog.findViewById(R.id.btn_dialog_cancel_set_font);
        final RadioButton radioButton = dialog.findViewById(R.id.btn_default_font);
        final RadioButton radioButton2 = dialog.findViewById(R.id.btn_poppins);
        final RadioButton radioButton3 = dialog.findViewById(R.id.btn_montserrat);
        final RadioButton radioButton4 = dialog.findViewById(R.id.btn_inter);
        final RadioButton radioButton5 = dialog.findViewById(R.id.btn_roboto);
        final RadioButton radioButton6 = dialog.findViewById(R.id.btn_moulpali);
        final RadioButton radioButton7 = dialog.findViewById(R.id.btn_murecho);
        final RadioButton radioButton8 = dialog.findViewById(R.id.btn_mplus1p);
        final RadioButton radioButton9 = dialog.findViewById(R.id.btn_port_lligat_sans);
        final RadioButton radioButton10 = dialog.findViewById(R.id.btn_pontano_sans);
        final RadioButton radioButton11 = dialog.findViewById(R.id.btn_poly);
        Button button3 = dialog.findViewById(R.id.btn_dialog_set_font);
        radioButton2.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_normal));
        radioButton3.setTypeface(ResourcesCompat.getFont(this, R.font.montserrat_regular));
        radioButton4.setTypeface(ResourcesCompat.getFont(this, R.font.inter_regular));
        radioButton5.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_regular));
        radioButton6.setTypeface(ResourcesCompat.getFont(this, R.font.moulpali_regular));
        radioButton7.setTypeface(ResourcesCompat.getFont(this, R.font.murecho_regular));
        radioButton8.setTypeface(ResourcesCompat.getFont(this, R.font.mplus1p_regular));
        radioButton9.setTypeface(ResourcesCompat.getFont(this, R.font.port_lligat_sans_regular));
        radioButton10.setTypeface(ResourcesCompat.getFont(this, R.font.pontano_sans_regular));
        radioButton11.setTypeface(ResourcesCompat.getFont(this, R.font.poly_regular));
        dialog.setCancelable(false);
        dialog.show();
        if (share2.contains("titlefontinfo")) {
            button = button2;
            String string = share2.getString("titlefontinfo", "0");
            Objects.requireNonNull(string);
            fontInt = Integer.parseInt(string);
        } else {
            button = button2;
        }
        radioButton.setChecked(true);
        int i = fontInt;
        if (i == 0) {
            radioButton.setChecked(true);
        } else if (i == 1) {
            radioButton2.setChecked(true);
        } else if (i == 2) {
            radioButton3.setChecked(true);
        } else if (i == 3) {
            radioButton4.setChecked(true);
        } else if (i == 4) {
            radioButton5.setChecked(true);
        } else if (i == 5) {
            radioButton6.setChecked(true);
        } else if (i == 6) {
            radioButton7.setChecked(true);
        } else if (i == 7) {
            radioButton8.setChecked(true);
        } else if (i == 8) {
            radioButton9.setChecked(true);
        } else if (i == 9) {
            radioButton10.setChecked(true);
        } else if (i == 10) {
            radioButton11.setChecked(true);
        }
        button.setOnClickListener(view -> dialog.dismiss());
        button3.setOnClickListener(view -> {
            if (radioButton2.isChecked()) {
                SharedPreferences.Editor edit = share2.edit();
                edit.putString("titlefontinfo", "1");
                edit.apply();
            } else if (radioButton3.isChecked()) {
                SharedPreferences.Editor edit2 = share2.edit();
                edit2.putString("titlefontinfo", ExifInterface.GPS_MEASUREMENT_2D);
                edit2.apply();
            } else if (radioButton4.isChecked()) {
                SharedPreferences.Editor edit3 = share2.edit();
                edit3.putString("titlefontinfo", ExifInterface.GPS_MEASUREMENT_3D);
                edit3.apply();
            } else if (radioButton5.isChecked()) {
                SharedPreferences.Editor edit4 = share2.edit();
                edit4.putString("titlefontinfo", "4");
                edit4.apply();
            } else if (radioButton6.isChecked()) {
                SharedPreferences.Editor edit5 = share2.edit();
                edit5.putString("titlefontinfo", "5");
                edit5.apply();
            } else if (radioButton7.isChecked()) {
                SharedPreferences.Editor edit6 = share2.edit();
                edit6.putString("titlefontinfo", "6");
                edit6.apply();
            } else if (radioButton8.isChecked()) {
                SharedPreferences.Editor edit7 = share2.edit();
                edit7.putString("titlefontinfo", "7");
                edit7.apply();
            } else if (radioButton9.isChecked()) {
                SharedPreferences.Editor edit8 = share2.edit();
                edit8.putString("titlefontinfo", "8");
                edit8.apply();
            } else if (radioButton10.isChecked()) {
                SharedPreferences.Editor edit9 = share2.edit();
                edit9.putString("titlefontinfo", "9");
                edit9.apply();
            } else if (radioButton11.isChecked()) {
                SharedPreferences.Editor edit10 = share2.edit();
                edit10.putString("titlefontinfo", "10");
                edit10.apply();
            } else if (radioButton.isChecked()) {
                SharedPreferences.Editor edit11 = share2.edit();
                edit11.remove("titlefontinfo");
                edit11.apply();
            }
            setShopInfo();
            dialog.dismiss();
        });
    }

    private void setFontInvoice(int fontInvoice) {
        binding.tvShopNameTitle.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvCaptionOrder.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvOrderId.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvPrintOrderId.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvOrderDate.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvPrintOrderDate.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvCaptionDeliInfo.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvCustomerName4.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvPrintCustomerName.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvCustomerAddress3.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvPrintCustomerAddress.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvCustomerPhone4.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvPrintCustomerPhone.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvCaptionNumber.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvCaptionItem.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvCaptionCount.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvCaptionPrice.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvCaptionTotal.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvShopNameSmall.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvShopAddress.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvShopPhone.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvSubTotalCaption.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvSubTotal.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvDiscountCaption.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvDiscount.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvTaxCaption.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvTax.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvDeliFeeCurrency.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvPrintDeliFeeFinal.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvTotalCurrency.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvGrandTotals.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvAdvanceCurrency.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvAdvance.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvBalanceCurrency.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvBalance.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
        binding.tvBottomMessage.setTypeface(ResourcesCompat.getFont(this, fontInvoice));
    }

    @SuppressLint("SetTextI18n")
    public void setShopInfo() {
        binding.tvShopNameTitle.setText("Instant Invoice");
        SharedPreferences sharedPreferences = getSharedPreferences("shopinfo", 0);
        share2 = sharedPreferences;
        if (sharedPreferences.contains("imagePreferance")) {
            byte[] decode = Base64.decode(share2.getString("imagePreferance", "default"), 0);
            Bitmap decodeByteArray = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            binding.ivShopLogo.setBackgroundColor(0);
            binding.ivShopLogo.setVisibility(View.VISIBLE);
            binding.ivShopLogo.setImageBitmap(decodeByteArray);
        }
        if (share2.contains("titlefontinfo")) {
            String string = share2.getString("titlefontinfo", "0");
            switch (string) {
                case "1":
                    setFontInvoice(R.font.poppins_normal);
                    break;
                case ExifInterface.GPS_MEASUREMENT_2D:
                    setFontInvoice(R.font.montserrat_regular);
                    break;
                case ExifInterface.GPS_MEASUREMENT_3D:
                    setFontInvoice(R.font.inter_regular);
                    break;
                case "4":
                    setFontInvoice(R.font.roboto_regular);
                    break;
                case "5":
                    setFontInvoice(R.font.moulpali_regular);
                    break;
                case "6":
                    setFontInvoice(R.font.murecho_regular);
                    break;
                case "7":
                    setFontInvoice(R.font.mplus1p_regular);
                    break;
                case "8":
                    setFontInvoice(R.font.port_lligat_sans_regular);
                    break;
                case "9":
                    setFontInvoice(R.font.pontano_sans_regular);
                    break;
                case "10":
                    setFontInvoice(R.font.poly_regular);
                    break;
                default:
                    binding.tvShopNameTitle.setTypeface(null, Typeface.BOLD);
                    break;
            }
        } else {
            binding.tvShopNameTitle.setTypeface(null, Typeface.BOLD);
        }
        if (share2.contains("shopname")) {
            binding.tvShopNameTitle.setText(share2.getString("shopname", "Instant Invoice"));
            binding.tvShopNameSmall.setText(share2.getString("shopname", " "));
        } else {
            binding.tvShopNameTitle.setText("Instant Invoice");
            binding.tvShopNameSmall.setText(" ");
        }
        if (share2.contains("shopaddress")) {
            binding.tvShopAddress.setText(share2.getString("shopaddress", " "));
        } else {
            binding.tvShopAddress.setText(" ");
        }
        if (share2.contains("shopphone")) {
            binding.tvShopPhone.setText(share2.getString("shopphone", " "));
        } else {
            binding.tvShopPhone.setText(" ");
        }
        if (share2.contains("bottommessage")) {
            binding.tvBottomMessage.setText(share2.getString("bottommessage", "Thanks for shopping with us!"));
        } else {
            binding.tvBottomMessage.setText(R.string.thanks);
        }
        if (share2.contains(FirebaseAnalytics.Param.CURRENCY)) {
            String string2 = share2.getString(FirebaseAnalytics.Param.CURRENCY, "");
            currencyString = string2;
            if (string2.equals("No")) {
                currencyString = "";
                binding.tvTotalCurrency.setText(R.string.total);
                binding.tvAdvanceCurrency.setText(R.string.advance);
                binding.tvBalanceCurrency.setText(R.string.balance);
                return;
            }
            TextView textView = binding.tvTotalCurrency;
            textView.setText("Total " + currencyString + "):");
            TextView textView2 = binding.tvAdvanceCurrency;
            textView2.setText("Advance " + currencyString + "):");
            TextView textView3 = binding.tvBalanceCurrency;
            textView3.setText("Balance " + currencyString + "):");
            return;
        }
        currencyString = "";
        binding.tvTotalCurrency.setText("Total:");
        binding.tvAdvanceCurrency.setText("Advance:");
        binding.tvBalanceCurrency.setText("Balance:");
    }

    public void addData() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialogaddnew);
        final EditText EditText = dialog.findViewById(R.id.et_update_dialog_item);
        final EditText EditText2 = dialog.findViewById(R.id.et_dialog_update_count);
        final EditText EditText3 = dialog.findViewById(R.id.et_dialog_update_price);
        dialog.setCancelable(false);
        dialog.show();
        final String[] strArr = new MyDatabaseHelperItems(getApplicationContext()).getAllNames().toArray(new String[0]);
        dialog.findViewById(R.id.btn_show_spinner_item).setOnClickListener(view -> {
            if (strArr.length > 0) {
                new AlertDialog.Builder(MainActivity.this).setSingleChoiceItems(strArr, -1, (dialogInterface, i) -> {
                    String[] split = strArr[i].split("\nPrice: ");
                    String str = split[0];
                    EditText3.setText(split[1].replaceAll("[^0-9.]+", "").trim());
                    EditText.setText(str);
                }).setPositiveButton("Ok", (dialogInterface, i) -> {
                }).setTitle("Choose from my items").show();
            } else {
                Toast.makeText(this, "No items added yet!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.findViewById(R.id.btn_dialog_count_down).setOnClickListener(view -> {
            Editable text = EditText2.getText();
            Objects.requireNonNull(text);
            if (Integer.parseInt(text.toString()) > 1) {
                EditText2.setText(String.valueOf(Integer.parseInt(EditText2.getText().toString()) - 1));
                TextInputEditText textInputEditText21 = null;
                textInputEditText21.setSelection(textInputEditText21.getText().length());
            }
        });
        dialog.findViewById(R.id.btn_dialog_count_up).setOnClickListener(view -> {
            EditText2.setText(String.valueOf(Integer.parseInt(EditText2.getText().toString()) + 1));
            TextInputEditText textInputEditText212 = null;
            textInputEditText212.setSelection(Objects.requireNonNull(textInputEditText212.getText()).length());
        });
        dialog.findViewById(R.id.btn_dialog_cancel_delete).setOnClickListener(v -> {
            dialog.dismiss();
            setAds();
        });
        dialog.findViewById(R.id.btn_dialog_delete).setOnClickListener(view -> {
            if (Objects.requireNonNull(EditText.getText()).toString().length() == 0 || Objects.requireNonNull(EditText2.getText()).toString().length() == 0 || Objects.requireNonNull(EditText3.getText()).toString().length() == 0) {
                Toast.makeText(this, "Enter all the fields", Toast.LENGTH_SHORT).show();
            } else if (EditText3.getText().toString().equals(".")) {
                Toast.makeText(this, "Enter a valid price!", Toast.LENGTH_SHORT).show();
            } else {
                String obj = EditText.getText().toString();
                String obj2 = EditText2.getText().toString();
                String obj3 = EditText3.getText().toString();
                double parseDouble = Double.parseDouble(obj2) * Double.parseDouble(obj3);
                ItemList itemList = new ItemList(obj, obj2, obj3, parseDouble);
                if (listArray < 10) {
                    arrayList.set(listArray, itemList);
                    MainActivity mainActivity = MainActivity.this;
                    binding.rvList.setAdapter(new RvAdapter(mainActivity, mainActivity.arrayList));
                    listArray++;
                } else {
                    arrayList.add(new ItemList(obj, obj2, obj3, parseDouble));
                    MainActivity mainActivity2 = MainActivity.this;
                    binding.rvList.setAdapter(new RvAdapter(mainActivity2, mainActivity2.arrayList));
                }
                updateTotalPrice();
                dialog.dismiss();
                setAds();
            }
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void updateTotalPrice() {
        total = 0.0d;
        for (int i = 0; i < this.arrayList.size(); i++) {
            total = Double.parseDouble(String.valueOf(total + this.arrayList.get(i).getItemTotal()));
        }
        if (binding.etDiscount.getText().toString().length() == 0) {
            calculateDiscount = 0.0d;
        }
        if (binding.etTax.getText().toString().length() == 0) {
            calculateTax = 0.0d;
        }
        if (binding.etDeliFees.getText().toString().length() == 0) {
            deliFeeLong = 0.0d;
        }
        if (binding.etAdvance.getText().toString().length() == 0) {
            advanceLong = 0.0d;
        }
        if (binding.etDeliFees.getText().toString().length() == 0 && binding.etTax.getText().toString().length() > 0) {
            binding.tvGrandTotals.setVisibility(View.GONE);
        }
        if (binding.etDeliFees.getText().toString().length() == 0 && binding.etDiscount.getText().toString().length() > 0) {
            binding.tvGrandTotals.setVisibility(View.GONE);
        }
        if (binding.etDeliFees.getText().toString().length() == 0 && binding.etTax.getText().toString().length() == 0 && binding.etDiscount.getText().toString().length() == 0) {
            binding.tvGrandTotals.setVisibility(View.VISIBLE);
        }
        if (binding.etDeliFees.getText().toString().length() == 0) {
            binding.tvPrintDeliFeeFinal.setText("0");
            deliFeeLong = 0.0d;
            binding.tvPrintDeliFeeFinal.setVisibility(View.GONE);
            binding.tvDeliFeeCurrency.setVisibility(View.GONE);
        } else {
            deliFeeLong = Double.parseDouble(binding.etDeliFees.getText().toString().trim());
            binding.tvPrintDeliFeeFinal.setVisibility(View.VISIBLE);
            binding.tvDeliFeeCurrency.setVisibility(View.VISIBLE);
            String format = String.format(Locale.getDefault(), "%.2f", deliFeeLong);
            binding.tvPrintDeliFeeFinal.setText("+" + format);
        }
        if (binding.etTax.getText().toString().length() == 0 || binding.etDiscount.getText().toString().length() == 0) {
            binding.tvSubTotal.setVisibility(View.VISIBLE);
            binding.tvSubTotalCaption.setVisibility(View.VISIBLE);
            if (binding.etTax.getText().toString().length() == 0) {
                taxLong = 0.0d;
                binding.tvTax.setVisibility(View.GONE);
                binding.tvTaxCaption.setVisibility(View.GONE);
            }
            if (binding.etDiscount.getText().toString().length() == 0) {
                discountLong = 0.0d;
                binding.tvDiscount.setVisibility(View.GONE);
                binding.tvDiscountCaption.setVisibility(View.GONE);
                calculateDiscount2 = 0.0d;
            }
        }
        if (binding.etDiscount.getText().toString().length() > 0) {
            binding.tvSubTotal.setVisibility(View.VISIBLE);
            binding.tvDiscountCaption.setVisibility(View.VISIBLE);
            binding.tvSubTotal.setText(String.format("%.2f", total));
            discountLong = Double.parseDouble(binding.etDiscount.getText().toString());
            binding.tvDiscount.setVisibility(View.VISIBLE);
            binding.tvDiscountCaption.setVisibility(View.VISIBLE);
            binding.tvDiscount.setText(String.valueOf(discountLong));
            double d = (discountLong * total) / 100.0d;
            calculateDiscount = d;
            String format2 = String.format("%.2f", d);
            binding.tvDiscountCaption.setText("Discount -" + discountLong + "%(" + format2 + "):");
            binding.tvDiscount.setText(String.format("%.2f", total - calculateDiscount));
        }
        if (binding.etTax.getText().toString().length() > 0) {
            binding.tvSubTotal.setVisibility(View.VISIBLE);
            binding.tvDiscountCaption.setVisibility(View.VISIBLE);
            binding.tvSubTotal.setText(String.format("%.2f", total));
            taxLong = Double.parseDouble(binding.etTax.getText().toString());
            if (binding.etDiscount.getText().toString().length() == 0) {
                double d2 = (taxLong * total) / 100.0d;
                calculateTax = d2;
                String format3 = String.format("%.2f", d2);
                binding.tvTaxCaption.setText("Tax +" + taxLong + "%(" + format3 + "):");
                binding.tvTax.setText(String.format("%.2f", total + calculateTax));
            }
            if (binding.etDiscount.getText().toString().length() > 0) {
                double parseDouble = Double.parseDouble(binding.etDiscount.getText().toString());
                discountLong = parseDouble;
                double d3 = (parseDouble * total) / 100.0d;
                calculateDiscount = d3;
                String format4 = String.format("%.2f", d3);
                binding.tvDiscountCaption.setText("Discount -" + discountLong + "%(" + format4 + ")");
                binding.tvDiscount.setText(String.format("%.2f", total - calculateDiscount));
                double d4 = total - calculateDiscount;
                calculateDiscount2 = d4;
                double d5 = (taxLong * d4) / 100.0d;
                calculateTax = d5;
                String format5 = String.format("%.2f", d5);
                binding.tvTaxCaption.setText("Tax +" + taxLong + "%(" + format5 + ")");
                binding.tvTax.setText(String.format("%.2f", calculateDiscount2 + calculateTax));
            }
            binding.tvTax.setVisibility(View.VISIBLE);
            binding.tvTaxCaption.setVisibility(View.VISIBLE);
        }
        if (binding.etAdvance.getText().toString().length() == 0) {
            advanceLong = 0.0d;
            binding.tvAdvance.setText("-");
            binding.tvBalance.setText(binding.tvGrandTotals.getText().toString());
        } else {
            double parseDouble2 = Double.parseDouble(binding.etAdvance.getText().toString());
            advanceLong = parseDouble2;
            String format6 = String.format("%.2f", parseDouble2);
            binding.tvAdvance.setText("-" + format6);
        }
        binding.tvGrandTotals.setText(String.format("%.2f", (total - calculateDiscount) + calculateTax + deliFeeLong));
        double parseDouble3 = Double.parseDouble(binding.tvGrandTotals.getText().toString()) - advanceLong;
        balanceLong = parseDouble3;
        binding.tvBalance.setText(String.format("%.2f", parseDouble3));
    }

    public void addToSQLite() {
        itemListString = this.arrayList.get(0).getItemName();
        countListString = this.arrayList.get(0).getItemCount();
        priceListString = this.arrayList.get(0).getItemPrice();
        for (int i = 1; i < this.arrayList.size(); i++) {
            itemListString += "--%%0&%%--\n" + this.arrayList.get(i).getItemName();
            countListString += "--%%0&%%--\n" + this.arrayList.get(i).getItemCount();
            priceListString += "--%%0&%%--\n" + this.arrayList.get(i).getItemPrice();
        }
        myDB.addOrder(itemListString.split("--%%0&%%--\n--%%0&%%--\n")[0], countListString.split("--%%0&%%--\n--%%0&%%--\n")[0], priceListString.split("--%%0&%%--\n--%%0&%%--\n")[0], binding.etOrderId.getText().toString().trim(), binding.etAdvance.getText().toString().trim(), binding.etTax.getText().toString().trim(), binding.etDiscount.getText().toString().trim(), binding.etDeliFees.getText().toString().trim(), binding.etCustomerName.getText().toString().trim(), binding.etCustomerAddress.getText().toString().trim(), binding.etCustomerPhone.getText().toString(), binding.etOrderDate.getText().toString().trim(), "0", paidStatus, String.valueOf(balanceLong));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setInvoiceBackground() {
        SharedPreferences share;
        share = getSharedPreferences("backgroundinfo", 0);
        String backgroundType = share.getString("lastBackgroundClick", "bgColor");
        String backgroundImageCode = share.getString("backgroundmyimgdefault", "0");

        if (backgroundType.equals("bgColor")) {
            switch (share.getString("backgroundchange", "cwhite")) {
                case "cblue":
                    binding.constraintLayoutPrint.setBackgroundColor(getResources().getColor(R.color.color_bg_blue));
                    break;
                case "cgreen":
                    binding.constraintLayoutPrint.setBackgroundColor(getResources().getColor(R.color.color_background_green));
                    break;
                case "cyellow":
                    binding.constraintLayoutPrint.setBackgroundColor(getResources().getColor(R.color.color_background_yellow));
                    break;
                case "cpink":
                    binding.constraintLayoutPrint.setBackgroundColor(getResources().getColor(R.color.color_background_pink));
                    break;
                case "cviolet":
                    binding.constraintLayoutPrint.setBackgroundColor(getResources().getColor(R.color.color_background_violet));
                    break;
                case "corange":
                    binding.constraintLayoutPrint.setBackgroundColor(getResources().getColor(R.color.color_background_orange));
                    break;
                case "cgreen2":
                    binding.constraintLayoutPrint.setBackgroundColor(getResources().getColor(R.color.color_background_green2));
                    break;
                case "cviolet2":
                    binding.constraintLayoutPrint.setBackgroundColor(getResources().getColor(R.color.color_background_violet2));
                    break;
                case "cblue2":
                    binding.constraintLayoutPrint.setBackgroundColor(getResources().getColor(R.color.color_background_blue2));
                    break;
                case "cgrey":
                    binding.constraintLayoutPrint.setBackgroundColor(getResources().getColor(R.color.color_background_grey));
                    break;
                case "cwhite":
                    binding.constraintLayoutPrint.setBackgroundColor(getResources().getColor(R.color.white));
                    break;
            }
        } else if (backgroundType.equals("bgImage")) {
            switch (backgroundImageCode) {
                case "wp1":
                    binding.constraintLayoutPrint.setBackground(getResources().getDrawable(R.drawable.wp1));
                    break;
                case "wp2":
                    binding.constraintLayoutPrint.setBackground(getResources().getDrawable(R.drawable.wp2));
                    break;
                case "wp3":
                    binding.constraintLayoutPrint.setBackground(getResources().getDrawable(R.drawable.wp3));
                    break;
                case "wp4":
                    binding.constraintLayoutPrint.setBackground(getResources().getDrawable(R.drawable.wp4));
                    break;
            }

        } else {
            String encodedString = share.getString("backgroundmyimg", "");
            if (!encodedString.isEmpty()) {
                byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                Drawable dr = new BitmapDrawable(bitmap);
                binding.constraintLayoutPrint.setBackgroundDrawable(dr);
            }


        }
    }

    public void changeInvoiceColor() {
        if (share.contains("colorChange")) {
            String string = share.getString("colorChange", "1");
            Objects.requireNonNull(string);
            color = Integer.parseInt(string);
            if (color == 1) {
                binding.btnBlue.setText("✓");
                binding.btnGreen.setText("");
                binding.btnRed.setText("");
                binding.btnPink.setText("");
                binding.btnMainBlue.setText("");
                binding.btnGrey.setText("");
                binding.btnOrange.setText("");
                binding.btnYellow.setText("");
                binding.btnViolet.setText("");
                binding.btnBrown.setText("");
                binding.btnGreen11.setText("");
                binding.btnViolet11.setText("");
                binding.tvShopNameTitle.setTextColor(getResources().getColor(R.color.color_blue1));
                binding.tvCaptionOrder.setBackgroundResource(R.color.color_blue1);
                binding.tvCaptionDeliInfo.setBackgroundResource(R.color.color_blue1);
                binding.tvCaptionNumber.setBackgroundResource(R.color.color_blue1);
                binding.tvCaptionItem.setBackgroundResource(R.color.color_blue1);
                binding.tvCaptionCount.setBackgroundResource(R.color.color_blue1);
                binding.tvCaptionPrice.setBackgroundResource(R.color.color_blue1);
                binding.tvCaptionTotal.setBackgroundResource(R.color.color_blue1);
                binding.rvList.setBackgroundResource(R.drawable.framervlistdarkblue);
                binding.tvBottomMessage.setTextColor(getResources().getColor(R.color.color_blue1));
            }
            if (color == 2) {
                binding.btnBlue.setText("");
                binding.btnGreen.setText("✓");
                binding.btnRed.setText("");
                binding.btnPink.setText("");
                binding.btnMainBlue.setText("");
                binding.btnGrey.setText("");
                binding.btnOrange.setText("");
                binding.btnYellow.setText("");
                binding.btnViolet.setText("");
                binding.btnBrown.setText("");
                binding.btnGreen11.setText("");
                binding.btnViolet11.setText("");
                binding.tvShopNameTitle.setTextColor(getResources().getColor(R.color.color_green1));
                binding.tvCaptionOrder.setBackgroundResource(R.color.color_green1);
                binding.tvCaptionDeliInfo.setBackgroundResource(R.color.color_green1);
                binding.tvCaptionNumber.setBackgroundResource(R.color.color_green1);
                binding.tvCaptionItem.setBackgroundResource(R.color.color_green1);
                binding.tvCaptionCount.setBackgroundResource(R.color.color_green1);
                binding.tvCaptionPrice.setBackgroundResource(R.color.color_green1);
                binding.tvCaptionTotal.setBackgroundResource(R.color.color_green1);
                binding.rvList.setBackgroundResource(R.drawable.framervlistgreen);
                binding.tvBottomMessage.setTextColor(getResources().getColor(R.color.color_green1));
            }
            if (color == 3) {
                binding.btnBlue.setText("");
                binding.btnGreen.setText("");
                binding.btnRed.setText("✓");
                binding.btnPink.setText("");
                binding.btnMainBlue.setText("");
                binding.btnGrey.setText("");
                binding.btnOrange.setText("");
                binding.btnYellow.setText("");
                binding.btnViolet.setText("");
                binding.btnBrown.setText("");
                binding.btnGreen11.setText("");
                binding.btnViolet11.setText("");
                binding.tvShopNameTitle.setTextColor(getResources().getColor(R.color.colorRed1));
                binding.tvCaptionOrder.setBackgroundResource(R.color.colorRed1);
                binding.tvCaptionDeliInfo.setBackgroundResource(R.color.colorRed1);
                binding.tvCaptionNumber.setBackgroundResource(R.color.colorRed1);
                binding.tvCaptionItem.setBackgroundResource(R.color.colorRed1);
                binding.tvCaptionCount.setBackgroundResource(R.color.colorRed1);
                binding.tvCaptionPrice.setBackgroundResource(R.color.colorRed1);
                binding.tvCaptionTotal.setBackgroundResource(R.color.colorRed1);
                binding.rvList.setBackgroundResource(R.drawable.framervlistred);
                binding.tvBottomMessage.setTextColor(getResources().getColor(R.color.colorRed1));
            }
            if (color == 4) {
                binding.btnBlue.setText("");
                binding.btnGreen.setText("");
                binding.btnRed.setText("");
                binding.btnPink.setText("✓");
                binding.btnMainBlue.setText("");
                binding.btnGrey.setText("");
                binding.btnOrange.setText("");
                binding.btnYellow.setText("");
                binding.btnViolet.setText("");
                binding.btnBrown.setText("");
                binding.btnGreen11.setText("");
                binding.btnViolet11.setText("");
                binding.tvShopNameTitle.setTextColor(getResources().getColor(R.color.colorPink1));
                binding.tvCaptionOrder.setBackgroundResource(R.color.colorPink1);
                binding.tvCaptionDeliInfo.setBackgroundResource(R.color.colorPink1);
                binding.tvCaptionNumber.setBackgroundResource(R.color.colorPink1);
                binding.tvCaptionItem.setBackgroundResource(R.color.colorPink1);
                binding.tvCaptionCount.setBackgroundResource(R.color.colorPink1);
                binding.tvCaptionPrice.setBackgroundResource(R.color.colorPink1);
                binding.tvCaptionTotal.setBackgroundResource(R.color.colorPink1);
                binding.rvList.setBackgroundResource(R.drawable.frame_rv_list_pink);
                binding.tvBottomMessage.setTextColor(getResources().getColor(R.color.colorPink1));
            }
            if (color == 5) {
                binding.btnBlue.setText("");
                binding.btnGreen.setText("");
                binding.btnRed.setText("");
                binding.btnPink.setText("");
                binding.btnMainBlue.setText("✓");
                binding.btnGrey.setText("");
                binding.btnOrange.setText("");
                binding.btnYellow.setText("");
                binding.btnViolet.setText("");
                binding.btnBrown.setText("");
                binding.btnGreen11.setText("");
                binding.btnViolet11.setText("");
                binding.tvShopNameTitle.setTextColor(getResources().getColor(R.color.color_main1));
                binding.tvCaptionOrder.setBackgroundResource(R.color.color_main1);
                binding.tvCaptionDeliInfo.setBackgroundResource(R.color.color_main1);
                binding.tvCaptionNumber.setBackgroundResource(R.color.color_main1);
                binding.tvCaptionItem.setBackgroundResource(R.color.color_main1);
                binding.tvCaptionCount.setBackgroundResource(R.color.color_main1);
                binding.tvCaptionPrice.setBackgroundResource(R.color.color_main1);
                binding.tvCaptionTotal.setBackgroundResource(R.color.color_main1);
                binding.rvList.setBackgroundResource(R.drawable.frame_rv_list_blue);
                binding.tvBottomMessage.setTextColor(getResources().getColor(R.color.color_main1));
            }
            if (color == 6) {
                binding.btnBlue.setText("");
                binding.btnGreen.setText("");
                binding.btnRed.setText("");
                binding.btnPink.setText("");
                binding.btnMainBlue.setText("");
                binding.btnGrey.setText("✓");
                binding.btnOrange.setText("");
                binding.btnYellow.setText("");
                binding.btnViolet.setText("");
                binding.btnBrown.setText("");
                binding.btnGreen11.setText("");
                binding.btnViolet11.setText("");
                binding.tvShopNameTitle.setTextColor(getResources().getColor(R.color.color_grey1));
                binding.tvCaptionOrder.setBackgroundResource(R.color.color_grey1);
                binding.tvCaptionDeliInfo.setBackgroundResource(R.color.color_grey1);
                binding.tvCaptionNumber.setBackgroundResource(R.color.color_grey1);
                binding.tvCaptionItem.setBackgroundResource(R.color.color_grey1);
                binding.tvCaptionCount.setBackgroundResource(R.color.color_grey1);
                binding.tvCaptionPrice.setBackgroundResource(R.color.color_grey1);
                binding.tvCaptionTotal.setBackgroundResource(R.color.color_grey1);
                binding.tvBottomMessage.setTextColor(getResources().getColor(R.color.color_grey1));
                binding.rvList.setBackgroundResource(R.drawable.framervlistgrey);
            }
            if (color == 7) {
                binding.btnBlue.setText("");
                binding.btnGreen.setText("");
                binding.btnRed.setText("");
                binding.btnPink.setText("");
                binding.btnMainBlue.setText("");
                binding.btnGrey.setText("");
                binding.btnOrange.setText("✓");
                binding.btnYellow.setText("");
                binding.btnViolet.setText("");
                binding.btnBrown.setText("");
                binding.btnGreen11.setText("");
                binding.btnViolet11.setText("");
                binding.tvShopNameTitle.setTextColor(getResources().getColor(R.color.color_orange1));
                binding.tvCaptionOrder.setBackgroundResource(R.color.color_orange1);
                binding.tvCaptionDeliInfo.setBackgroundResource(R.color.color_orange1);
                binding.tvCaptionNumber.setBackgroundResource(R.color.color_orange1);
                binding.tvCaptionItem.setBackgroundResource(R.color.color_orange1);
                binding.tvCaptionCount.setBackgroundResource(R.color.color_orange1);
                binding.tvCaptionPrice.setBackgroundResource(R.color.color_orange1);
                binding.tvCaptionTotal.setBackgroundResource(R.color.color_orange1);
                binding.tvBottomMessage.setTextColor(getResources().getColor(R.color.color_orange1));
                binding.rvList.setBackgroundResource(R.drawable.framervlistorange);
            }
            if (color == 8) {
                binding.btnBlue.setText("");
                binding.btnGreen.setText("");
                binding.btnRed.setText("");
                binding.btnPink.setText("");
                binding.btnMainBlue.setText("");
                binding.btnGrey.setText("");
                binding.btnOrange.setText("");
                binding.btnYellow.setText("✓");
                binding.btnViolet.setText("");
                binding.btnBrown.setText("");
                binding.btnGreen11.setText("");
                binding.btnViolet11.setText("");
                binding.tvShopNameTitle.setTextColor(getResources().getColor(R.color.color_yellow1));
                binding.tvCaptionOrder.setBackgroundResource(R.color.color_yellow1);
                binding.tvCaptionDeliInfo.setBackgroundResource(R.color.color_yellow1);
                binding.tvCaptionNumber.setBackgroundResource(R.color.color_yellow1);
                binding.tvCaptionItem.setBackgroundResource(R.color.color_yellow1);
                binding.tvCaptionCount.setBackgroundResource(R.color.color_yellow1);
                binding.tvCaptionPrice.setBackgroundResource(R.color.color_yellow1);
                binding.tvCaptionTotal.setBackgroundResource(R.color.color_yellow1);
                binding.tvBottomMessage.setTextColor(getResources().getColor(R.color.color_yellow1));
                binding.rvList.setBackgroundResource(R.drawable.framervlistyellow);
            }
            if (color == 9) {
                binding.btnBlue.setText("");
                binding.btnGreen.setText("");
                binding.btnRed.setText("");
                binding.btnPink.setText("");
                binding.btnMainBlue.setText("");
                binding.btnGrey.setText("");
                binding.btnOrange.setText("");
                binding.btnYellow.setText("");
                binding.btnViolet.setText("✓");
                binding.btnBrown.setText("");
                binding.btnGreen11.setText("");
                binding.btnViolet11.setText("");
                binding.tvShopNameTitle.setTextColor(getResources().getColor(R.color.colorViolet1));
                binding.tvCaptionOrder.setBackgroundResource(R.color.colorViolet1);
                binding.tvCaptionDeliInfo.setBackgroundResource(R.color.colorViolet1);
                binding.tvCaptionNumber.setBackgroundResource(R.color.colorViolet1);
                binding.tvCaptionItem.setBackgroundResource(R.color.colorViolet1);
                binding.tvCaptionCount.setBackgroundResource(R.color.colorViolet1);
                binding.tvCaptionPrice.setBackgroundResource(R.color.colorViolet1);
                binding.tvCaptionTotal.setBackgroundResource(R.color.colorViolet1);
                binding.tvBottomMessage.setTextColor(getResources().getColor(R.color.colorViolet1));
                binding.rvList.setBackgroundResource(R.drawable.framervlistviolet);
            }
            if (color == 10) {
                binding.btnBlue.setText("");
                binding.btnGreen.setText("");
                binding.btnRed.setText("");
                binding.btnPink.setText("");
                binding.btnMainBlue.setText("");
                binding.btnGrey.setText("");
                binding.btnOrange.setText("");
                binding.btnYellow.setText("");
                binding.btnViolet.setText("");
                binding.btnBrown.setText("✓");
                binding.btnGreen11.setText("");
                binding.btnViolet11.setText("");
                binding.tvShopNameTitle.setTextColor(getResources().getColor(R.color.color_brown1));
                binding.tvCaptionOrder.setBackgroundResource(R.color.color_brown1);
                binding.tvCaptionDeliInfo.setBackgroundResource(R.color.color_brown1);
                binding.tvCaptionNumber.setBackgroundResource(R.color.color_brown1);
                binding.tvCaptionItem.setBackgroundResource(R.color.color_brown1);
                binding.tvCaptionCount.setBackgroundResource(R.color.color_brown1);
                binding.tvCaptionPrice.setBackgroundResource(R.color.color_brown1);
                binding.tvCaptionTotal.setBackgroundResource(R.color.color_brown1);
                binding.tvBottomMessage.setTextColor(getResources().getColor(R.color.color_brown1));
                binding.rvList.setBackgroundResource(R.drawable.framervlistbrown);
            }
            if (color == 11) {
                binding.btnBlue.setText("");
                binding.btnGreen.setText("");
                binding.btnRed.setText("");
                binding.btnPink.setText("");
                binding.btnMainBlue.setText("");
                binding.btnGrey.setText("");
                binding.btnOrange.setText("");
                binding.btnYellow.setText("");
                binding.btnViolet.setText("");
                binding.btnBrown.setText("");
                binding.btnViolet11.setText("✓");
                binding.btnGreen11.setText("");
                binding.tvShopNameTitle.setTextColor(getResources().getColor(R.color.colorViolet11));
                binding.tvCaptionOrder.setBackgroundResource(R.color.colorViolet11);
                binding.tvCaptionDeliInfo.setBackgroundResource(R.color.colorViolet11);
                binding.tvCaptionNumber.setBackgroundResource(R.color.colorViolet11);
                binding.tvCaptionItem.setBackgroundResource(R.color.colorViolet11);
                binding.tvCaptionCount.setBackgroundResource(R.color.colorViolet11);
                binding.tvCaptionPrice.setBackgroundResource(R.color.colorViolet11);
                binding.tvCaptionTotal.setBackgroundResource(R.color.colorViolet11);
                binding.tvBottomMessage.setTextColor(getResources().getColor(R.color.colorViolet11));
                binding.rvList.setBackgroundResource(R.drawable.framervlistviolet11);
            }
            if (color == 12) {
                binding.btnBlue.setText("");
                binding.btnGreen.setText("");
                binding.btnRed.setText("");
                binding.btnPink.setText("");
                binding.btnMainBlue.setText("");
                binding.btnGrey.setText("");
                binding.btnOrange.setText("");
                binding.btnYellow.setText("");
                binding.btnViolet.setText("");
                binding.btnBrown.setText("");
                binding.btnViolet11.setText("");
                binding.btnGreen11.setText("✓");
                binding.tvShopNameTitle.setTextColor(getResources().getColor(R.color.color_green11));
                binding.tvCaptionOrder.setBackgroundResource(R.color.color_green11);
                binding.tvCaptionDeliInfo.setBackgroundResource(R.color.color_green11);
                binding.tvCaptionNumber.setBackgroundResource(R.color.color_green11);
                binding.tvCaptionItem.setBackgroundResource(R.color.color_green11);
                binding.tvCaptionCount.setBackgroundResource(R.color.color_green11);
                binding.tvCaptionPrice.setBackgroundResource(R.color.color_green11);
                binding.tvCaptionTotal.setBackgroundResource(R.color.color_green11);
                binding.tvBottomMessage.setTextColor(getResources().getColor(R.color.color_green11));
                binding.rvList.setBackgroundResource(R.drawable.framervlistgreen11);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void onBackPressed() {
        if (getIntent().getExtras() != null) {
            finish();
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialogdelete);
            Button button = dialog.findViewById(R.id.btn_dialog_delete);
            dialog.setCancelable(false);
            dialog.show();
            button.setText("Exit");
            ((TextView) dialog.findViewById(R.id.tv_dialog_delete_caption)).setText("Exit from the App?");
            ((TextView) dialog.findViewById(R.id.tv_dialog_delete_text)).setText("Are you sure you want to exit from the App?");
            dialog.findViewById(R.id.btn_dialog_cancel_delete).setOnClickListener(view -> dialog.dismiss());
            button.setOnClickListener(view -> {
                finishAffinity();
                dialog.dismiss();
            });
        }
    }


    public void onResume() {
        super.onResume();
    }

    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        binding.rvList.setAdapter(new RvAdapter(this, this.arrayList));
    }


    public void onStop() {
        super.onStop();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 222) {
            setInvoiceBackground();
            binding.rvList.setAdapter(new RvAdapter(this, this.arrayList));
        }
        if (i == 100 && i2 != -1) {
//            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(i, i2, intent);
    }
}
