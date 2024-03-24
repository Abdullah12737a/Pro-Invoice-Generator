package com.work.proinvoicemaker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.work.proinvoicemaker.R;
import com.work.proinvoicemaker.Uitilty.OnItemClick;
import com.work.proinvoicemaker.databaseHelper.MyDatabaseHelperItems;
import com.work.proinvoicemaker.databinding.CardlayoutmyitemsBinding;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class RvAdapterMyItems extends RecyclerView.Adapter<RvAdapterMyItems.MyViewHolder> {
    Activity activity;
    private final ArrayList arrayListId;
    private final ArrayList arrayListMyItemInStock;
    private final ArrayList arrayListMyItemName;
    private final ArrayList arrayListMyItemPic;
    private final ArrayList arrayListMyItemPrice;
    private final Context context;
    String currencyString;
    String myItemId;
    String myItemInStock;
    String myItemName;
    String myItemPrice;
    SharedPreferences share2;
    private CardlayoutmyitemsBinding binding;
    MyDatabaseHelperItems myDBItem;
    private final OnItemClick mCallback;
    private LinearLayout adView;
    private NativeAd nativeAd;
    String TAG = "Tag";
    View containerView;

    public RvAdapterMyItems(Activity activity2, Context context2, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, OnItemClick listener) {
        this.activity = activity2;
        this.context = context2;
        this.arrayListId = arrayList;
        this.arrayListMyItemPic = arrayList2;
        this.arrayListMyItemName = arrayList3;
        this.arrayListMyItemPrice = arrayList4;
        this.arrayListMyItemInStock = arrayList5;
        this.mCallback = listener;
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        containerView = LayoutInflater.from(context).inflate(R.layout.native_ad_layout, viewGroup, false);

        View view = LayoutInflater.from(context).inflate(R.layout.cardlayoutmyitems, viewGroup, false);
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
            // Request an ad
            nativeAd.loadAd(
                    nativeAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());

        }
        myDBItem = new MyDatabaseHelperItems(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("shopinfo", 0);
        share2 = sharedPreferences;
        if (sharedPreferences.contains(FirebaseAnalytics.Param.CURRENCY)) {
            String string = share2.getString(FirebaseAnalytics.Param.CURRENCY, "");
            currencyString = string;
            if (string.equals("No")) {
                currencyString = "";
            }
        } else {
            currencyString = "";
        }
        myItemId = String.valueOf(arrayListId.get(i));
        myItemName = String.valueOf(arrayListMyItemName.get(i));
        myItemPrice = arrayListMyItemPrice.get(i) + " " + currencyString;
        myItemInStock = "Instock: " + arrayListMyItemInStock.get(i) + " pcs";
        TextView textView = binding.tvCustomerNamePrinted;
        textView.setText(myItemName);
        binding.tvItemPrinted.setText(myItemPrice+")");
        binding.btnGotoEditInvoice.setOnClickListener(view -> {
            Log.d("ghdjsdd", "onBindViewHolder: " + i);
            mCallback.onClick(i);
        });
    }

    public int getItemCount() {
        return arrayListId.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        NativeAdLayout nativeAdLayout;

        public MyViewHolder(View view) {
            super(view);
            nativeAdLayout = view.findViewById(R.id.native_ad_container);
        }
    }
}
