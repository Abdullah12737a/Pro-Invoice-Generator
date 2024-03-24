package com.work.proinvoicemaker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.work.proinvoicemaker.R;
import com.work.proinvoicemaker.activity.SeePrintedInvoiceActivity;
import com.work.proinvoicemaker.databaseHelper.MyDatabaseHelper;
import com.work.proinvoicemaker.databinding.CardlayoutprintedinvoicesBinding;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class RvAdapterPrinted extends RecyclerView.Adapter<RvAdapterPrinted.MyViewHolder> {
    private final Activity activity;
    private String advance;
    private final ArrayList arrayListAdvancePrinted;
    private final ArrayList arrayListCustomerAddressPrinted;
    private final ArrayList arrayListCustomerNamePrinted;
    private final ArrayList arrayListCustomerPhonePrinted;
    private final ArrayList arrayListDeliFeePrinted;
    private final ArrayList arrayListDiscountPrinted;
    private final ArrayList arrayListIdToSee;
    private final ArrayList arrayListItemCountPrinted;
    private final ArrayList arrayListItemNamePrinted;
    private final ArrayList arrayListItemPricePrinted;
    private final ArrayList totalPriceList;
    private final ArrayList arrayListMark;
    private final ArrayList arrayListOrderDatePrinted;
    private final ArrayList arrayListOrderIdPrinted;
    private final ArrayList arrayListPaidStatus;
    private final ArrayList arrayListTaxPrinted;
    private final Context context;
    private String customerAddress;
    private String customerName;
    private String customerPhone;
    private String deliFee;
    private String discount;
    private String totalPrice;
    private String idToSee;
    private String itemCount;
    private String itemName;
    private String itemPrice;
    public String mark;
    private String orderDate;
    private String orderId;
    private String paidStatus;
    private String tax;
    private CardlayoutprintedinvoicesBinding binding;
    private LinearLayout adView;
    private NativeAd nativeAd;
    String TAG = "Tag";
    View containerView;

    public RvAdapterPrinted(Activity activity2, Context context2, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, ArrayList arrayList6, ArrayList arrayList7, ArrayList arrayList8, ArrayList arrayList9, ArrayList arrayList10, ArrayList arrayList11, ArrayList arrayList12, ArrayList arrayList13, ArrayList arrayList14, ArrayList arrayList15, ArrayList totalPriceList) {
        this.activity = activity2;
        this.context = context2;
        this.arrayListIdToSee = arrayList;
        this.arrayListOrderIdPrinted = arrayList2;
        this.arrayListOrderDatePrinted = arrayList3;
        this.arrayListCustomerNamePrinted = arrayList4;
        this.arrayListCustomerAddressPrinted = arrayList5;
        this.arrayListCustomerPhonePrinted = arrayList6;
        this.arrayListDeliFeePrinted = arrayList7;
        this.arrayListAdvancePrinted = arrayList8;
        this.arrayListTaxPrinted = arrayList9;
        this.arrayListDiscountPrinted = arrayList10;
        this.arrayListItemNamePrinted = arrayList11;
        this.arrayListItemCountPrinted = arrayList12;
        this.arrayListItemPricePrinted = arrayList13;
        this.arrayListMark = arrayList14;
        this.arrayListPaidStatus = arrayList15;
        this.totalPriceList = totalPriceList;
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        containerView = LayoutInflater.from(context).inflate(R.layout.native_ad_layout, viewGroup, false);

        View view = LayoutInflater.from(context).inflate(R.layout.cardlayoutprintedinvoices, viewGroup, false);
        binding = DataBindingUtil.bind(view);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {
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

        orderId = String.valueOf(arrayListOrderIdPrinted.get(i));
        orderDate = String.valueOf(arrayListOrderDatePrinted.get(i));
        customerName = String.valueOf(arrayListCustomerNamePrinted.get(i));
        customerAddress = String.valueOf(arrayListCustomerAddressPrinted.get(i));
        customerPhone = String.valueOf(arrayListCustomerPhonePrinted.get(i));
        deliFee = String.valueOf(arrayListDeliFeePrinted.get(i));
        advance = String.valueOf(arrayListAdvancePrinted.get(i));
        tax = String.valueOf(arrayListTaxPrinted.get(i));
        discount = String.valueOf(arrayListDiscountPrinted.get(i));
        itemName = String.valueOf(arrayListItemNamePrinted.get(i));
        itemCount = String.valueOf(arrayListItemCountPrinted.get(i));
        itemPrice = String.valueOf(arrayListItemPricePrinted.get(i));
        mark = String.valueOf(arrayListMark.get(i));
        totalPrice = String.valueOf(totalPriceList.get(i));
        paidStatus = String.valueOf(arrayListPaidStatus.get(i));
        String replace = itemName.replace("--%%0&%%--\n", "\n");
        binding.tvCustomerNamePrinted.setText(String.valueOf(arrayListCustomerNamePrinted.get(i)));
        binding.tvPhoneNumberPrinted.setText(String.valueOf(arrayListCustomerPhonePrinted.get(i)));
        binding.tvItemPrinted.setText(totalPrice);
        TextView textView = binding.tvIdPrinted;
        textView.setText((i + 1) + ".");
        binding.btnGotoEditInvoice.setOnClickListener(view -> {
            RvAdapterPrinted rvadapterPrinted = RvAdapterPrinted.this;
            rvadapterPrinted.idToSee = String.valueOf(rvadapterPrinted.arrayListIdToSee.get(i));
            RvAdapterPrinted rvAdapterPrinted2 = RvAdapterPrinted.this;
            rvAdapterPrinted2.orderId = String.valueOf(rvAdapterPrinted2.arrayListOrderIdPrinted.get(i));
            RvAdapterPrinted rvAdapterPrinted3 = RvAdapterPrinted.this;
            rvAdapterPrinted3.orderDate = String.valueOf(rvAdapterPrinted3.arrayListOrderDatePrinted.get(i));
            RvAdapterPrinted rvAdapterPrinted4 = RvAdapterPrinted.this;
            rvAdapterPrinted4.customerName = String.valueOf(rvAdapterPrinted4.arrayListCustomerNamePrinted.get(i));
            RvAdapterPrinted rvAdapterPrinted5 = RvAdapterPrinted.this;
            rvAdapterPrinted5.customerAddress = String.valueOf(rvAdapterPrinted5.arrayListCustomerAddressPrinted.get(i));
            RvAdapterPrinted rvAdapterPrinted6 = RvAdapterPrinted.this;
            rvAdapterPrinted6.customerPhone = String.valueOf(rvAdapterPrinted6.arrayListCustomerPhonePrinted.get(i));
            RvAdapterPrinted rvAdapterPrinted7 = RvAdapterPrinted.this;
            rvAdapterPrinted7.deliFee = String.valueOf(rvAdapterPrinted7.arrayListDeliFeePrinted.get(i));
            RvAdapterPrinted rvAdapterPrinted8 = RvAdapterPrinted.this;
            rvAdapterPrinted8.advance = String.valueOf(rvAdapterPrinted8.arrayListAdvancePrinted.get(i));
            RvAdapterPrinted rvAdapterPrinted9 = RvAdapterPrinted.this;
            rvAdapterPrinted9.tax = String.valueOf(rvAdapterPrinted9.arrayListTaxPrinted.get(i));
            RvAdapterPrinted rvAdapterPrinted10 = RvAdapterPrinted.this;
            rvAdapterPrinted10.discount = String.valueOf(rvAdapterPrinted10.arrayListDiscountPrinted.get(i));
            RvAdapterPrinted rvAdapterPrinted11 = RvAdapterPrinted.this;
            rvAdapterPrinted11.itemName = String.valueOf(rvAdapterPrinted11.arrayListItemNamePrinted.get(i));
            RvAdapterPrinted rvAdapterPrinted12 = RvAdapterPrinted.this;
            rvAdapterPrinted12.itemCount = String.valueOf(rvAdapterPrinted12.arrayListItemCountPrinted.get(i));
            RvAdapterPrinted rvAdapterPrinted13 = RvAdapterPrinted.this;
            rvAdapterPrinted13.itemPrice = String.valueOf(rvAdapterPrinted13.arrayListItemPricePrinted.get(i));
            RvAdapterPrinted rvAdapterPrinted14 = RvAdapterPrinted.this;
            rvAdapterPrinted14.paidStatus = String.valueOf(rvAdapterPrinted14.arrayListPaidStatus.get(i));

            Intent intent = new Intent(RvAdapterPrinted.this.activity, SeePrintedInvoiceActivity.class);
            intent.putExtra("invoiceid", RvAdapterPrinted.this.idToSee);
            intent.putExtra("orderid", RvAdapterPrinted.this.orderId);
            intent.putExtra("orderdate", RvAdapterPrinted.this.orderDate);
            intent.putExtra("customername", RvAdapterPrinted.this.customerName);
            intent.putExtra("customeraddress", RvAdapterPrinted.this.customerAddress);
            intent.putExtra("customerphone", RvAdapterPrinted.this.customerPhone);
            intent.putExtra("delifee", RvAdapterPrinted.this.deliFee);
            intent.putExtra("advance", RvAdapterPrinted.this.advance);
            intent.putExtra(FirebaseAnalytics.Param.TAX, RvAdapterPrinted.this.tax);
            intent.putExtra("discount", RvAdapterPrinted.this.discount);
            intent.putExtra("itemname", RvAdapterPrinted.this.itemName);
            intent.putExtra("itemcount", RvAdapterPrinted.this.itemCount);
            intent.putExtra("itemprice", RvAdapterPrinted.this.itemPrice);
            intent.putExtra("paidstatus", RvAdapterPrinted.this.paidStatus);
            RvAdapterPrinted.this.activity.startActivityForResult(intent, 1);
        });
        binding.btnDeleteFromList.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(RvAdapterPrinted.this.context);
            builder.setTitle("Delete invoice?");
            builder.setMessage("Are you sure you want to delete this invoice? \nThis action cannot be undone.");
            builder.setPositiveButton("Delete", (dialogInterface, i1) -> {
                try {
                    new MyDatabaseHelper(RvAdapterPrinted.this.context).deleteOneRow(String.valueOf(RvAdapterPrinted.this.arrayListIdToSee.get(i)));
                    Toast.makeText(RvAdapterPrinted.this.context, "Deleted the invoice!", Toast.LENGTH_SHORT).show();
                    RvAdapterPrinted.this.notifyDataSetChanged();
                    RvAdapterPrinted.this.activity.recreate();
                } catch (Exception e) {
                    Log.e("TAG_aara", "onBindViewHolder: " + e.getMessage());
                }
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i1) -> {
            });
            builder.show();
        });
        binding.btCall.setOnClickListener(view -> {
            Intent intent = new Intent("android.intent.action.DIAL");
            intent.setData(Uri.parse("tel:" + binding.tvPhoneNumberPrinted.getText().toString().trim()));
            RvAdapterPrinted.this.activity.startActivity(intent);
        });
    }

    public int getItemCount() {
        return this.arrayListCustomerNamePrinted.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        NativeAdLayout nativeAdLayout;

        public MyViewHolder(View view) {
            super(view);
            nativeAdLayout = view.findViewById(R.id.native_ad_container);
        }
    }
}
