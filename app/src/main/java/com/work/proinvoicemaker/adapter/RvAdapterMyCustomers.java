package com.work.proinvoicemaker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.work.proinvoicemaker.R;
import com.work.proinvoicemaker.databaseHelper.MyDatabaseHelperCustomers;
import com.work.proinvoicemaker.databinding.CardlayoutmycustomersBinding;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class RvAdapterMyCustomers extends RecyclerView.Adapter<RvAdapterMyCustomers.MyViewHolder> {
    private final Activity activity;
    private final ArrayList arrayListMyCustomerAddress;
    private final ArrayList arrayListMyCustomerId;
    private final ArrayList arrayListMyCustomerName;
    private final ArrayList arrayListMyCustomerPhone;
    private final Context context;
    private MyDatabaseHelperCustomers myDB;
    public String myCustomerAddress;
    private String myCustomerId;
    public String myCustomerName;
    public String myCustomerPhone;
    public int position;
    private CardlayoutmycustomersBinding binding;
    private InterstitialAd interstitialAd;
    private LinearLayout adView;
    private NativeAd nativeAd;
    String TAG = "Tag";
    View containerView;


    public RvAdapterMyCustomers(Activity activity2, Context context2, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
        this.activity = activity2;
        this.context = context2;
        this.arrayListMyCustomerId = arrayList;
        this.arrayListMyCustomerName = arrayList2;
        this.arrayListMyCustomerAddress = arrayList3;
        this.arrayListMyCustomerPhone = arrayList4;
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        containerView = LayoutInflater.from(context).inflate(R.layout.native_ad_layout, viewGroup, false);

        View view = LayoutInflater.from(context).inflate(R.layout.cardlayoutmycustomers, viewGroup, false);
        binding = DataBindingUtil.bind(view);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {
        if ((i + 1) % 3 == 0 && i != 0) {
            AudienceNetworkAds.initialize(activity);
            nativeAd = new NativeAd(activity, context.getString(R.string.Facebook_Native_placement));

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
                    nativeAd.unregisterView();


                    LayoutInflater inflater = LayoutInflater.from(activity);
                    adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, myViewHolder.nativeAdLayout, false);
                    myViewHolder.nativeAdLayout.addView(adView);

                    LinearLayout adChoicesContainer = containerView.findViewById(R.id.ad_choices_container);
                    AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, myViewHolder.nativeAdLayout);
                    adChoicesContainer.removeAllViews();
                    adChoicesContainer.addView(adOptionsView, 0);

                    MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
                    TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                    MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                    TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                    TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                    TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
                    Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

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
                            adView, nativeAdMedia, nativeAdIcon, clickableViews);

                    myViewHolder.nativeAdLayout.setVisibility(View.VISIBLE);
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
            nativeAd.loadAd(
                    nativeAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        }
        position = i;
        myDB = new MyDatabaseHelperCustomers(context);
        myCustomerId = String.valueOf(arrayListMyCustomerId.get(i));
        myCustomerName = String.valueOf(arrayListMyCustomerName.get(i));
        myCustomerAddress = String.valueOf(arrayListMyCustomerAddress.get(i));
        myCustomerPhone = String.valueOf(arrayListMyCustomerPhone.get(i));
        TextView textView = binding.tvFavCustomerId;
        textView.setText((i + 1) + ". ");
        binding.tvFavCustomerName.setText(myCustomerName);
        binding.tvFavCustomerAddress.setText(myCustomerAddress);
        binding.tvFavCustomerPhone.setText(myCustomerPhone);
        binding.btnDelCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RvAdapterMyCustomers.this.myDB.deleteOneRow(RvAdapterMyCustomers.this.myCustomerId);
                RvAdapterMyCustomers.this.activity.recreate();
                setAds();
            }
        });
        binding.btnEditCustomer.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(RvAdapterMyCustomers.this.context);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialogaddnewcustomer);
            final EditText editText = dialog.findViewById(R.id.et_dialog_customer_name);
            final EditText editText2 = dialog.findViewById(R.id.et_dialog_customer_address);
            final EditText editText3 = dialog.findViewById(R.id.et_dialog_customer_phone);
            ((TextView) dialog.findViewById(R.id.tv_dialog_add_customer)).setText("Edit Customer");
            editText.setText(String.valueOf(RvAdapterMyCustomers.this.arrayListMyCustomerName.get(i)));
            editText2.setText(String.valueOf(RvAdapterMyCustomers.this.arrayListMyCustomerAddress.get(i)));
            editText3.setText(String.valueOf(RvAdapterMyCustomers.this.arrayListMyCustomerPhone.get(i)));
            RvAdapterMyCustomers rvadapterMyCustomers = RvAdapterMyCustomers.this;
            rvadapterMyCustomers.myCustomerId = String.valueOf(rvadapterMyCustomers.arrayListMyCustomerId.get(i));
            dialog.setCancelable(false);
            dialog.show();
            dialog.findViewById(R.id.bt_delete_customer).setOnClickListener(view1 -> {
                RvAdapterMyCustomers.this.myDB.deleteOneRow(RvAdapterMyCustomers.this.myCustomerId);
                dialog.dismiss();
                RvAdapterMyCustomers.this.activity.recreate();
                setAds();

            });
            dialog.findViewById(R.id.btn_dialog_add_customer_cancel).setOnClickListener(view12 -> {
                dialog.dismiss();
                setAds();
            });
            dialog.findViewById(R.id.btn_dialog_add_customer).setOnClickListener(view13 -> {
                if (editText.getText().toString().length() <= 0 || editText2.getText().toString().length() <= 0 || editText3.getText().toString().length() <= 0) {
                    Toast.makeText(RvAdapterMyCustomers.this.context, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                RvAdapterMyCustomers.this.myDB.updateData(RvAdapterMyCustomers.this.myCustomerId, editText.getText().toString(), editText2.getText().toString(), editText3.getText().toString());
                dialog.dismiss();
                RvAdapterMyCustomers.this.activity.recreate();
                setAds();
            });
        });
    }

    public int getItemCount() {
        return this.arrayListMyCustomerName.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        NativeAdLayout nativeAdLayout;

        public MyViewHolder(View view) {
            super(view);
            nativeAdLayout = view.findViewById(R.id.native_ad_container);
        }
    }

    private void setAds() {
        interstitialAd = new com.facebook.ads.InterstitialAd(context, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");
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
