package com.quiziz.drive.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.quiziz.drive.model.Configuration;
import com.quiziz.drive.util.billing.IabHelper;
import com.quiziz.drive.util.billing.IabResult;
import com.quiziz.drive.util.billing.Inventory;
import com.quiziz.drive.util.billing.Purchase;
import com.quiziz.drive.util.billing.SkuDetails;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pototo on 06/03/2015.
 */
public class BillingHelper {
    private static final String SKU_PROD_01 = "com.quiziz.drive_prod_01";

    private static final String TAG = "BillingHelper";
    private static final String BY_MONTH = " por mes";
    private static final int PURCHASED_SUCCESSFULLY = 0;
    private static final int CANCELLED = 1;
    private static final int REFUNDED = 2;
    private static final int SUBSCRIPTION_EXPIRED = 3;
    private static final int RC_REQUEST = 10001;

    private static BillingHelper mInstance;
    private IabHelper mIabHelper;
    private Context mContext;
    private String mSubscriptionMonthPrice;
    private String mSku;
    private boolean mIsPremium;
    private Configuration mConfiguration;
    private static SecureRandom mRandom = new SecureRandom();

    public static BillingHelper getInstance(Context context){
        if(mInstance == null)
            mInstance = new BillingHelper(context);
        return mInstance;
    }

    public static BillingHelper createInstance(Context context){
        mInstance = new BillingHelper(context);
        return mInstance;
    }

    private BillingHelper(Context context){
        mContext = context;
        mConfiguration = new Configuration(context);
    }

    public void setUpInAppBillingToGetProductDetails(){
        mIabHelper = new IabHelper(mContext, Config.getPublicKey());
        mIabHelper.enableDebugLogging(true, TAG);
        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");
                if (result.isFailure()) {
                    Log.d(TAG, "Problem setting up In-app billing: " + result);
                    return;
                }
                Log.d(TAG, "Setup successful. Querying inventory: " + result);
                getProductDetails();
            }
        });
    }

//    public void setUpInAppBillingToGetPurchases(OnPurchaseStateUpdateListener callback){
//        this.mCallback = callback;
//        mIabHelper = new IabHelper(mContext, Config.getPublicKey());
//        mIabHelper.enableDebugLogging(QuizizConstants.DEVELOPMENT_MODE, TAG);
//        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
//            public void onIabSetupFinished(IabResult result) {
//                Log.d(TAG, "Setup finished.");
//                if (result.isFailure()) {
//                    Log.d(TAG, "Problem setting up In-app billing: " + result);
//                    return;
//                }
//                Log.d(TAG, "Setup successful. Querying inventory: " + result);
//                getUsersPurchases();
//            }
//        });
//    }

    private void getProductDetails(){
        if(mIabHelper == null) return;
        List<String> productIDsSkuList = new ArrayList();
        productIDsSkuList.add(SKU_PROD_01);
        mIabHelper.queryInventoryAsync(true, productIDsSkuList, mProductDetailsFinishedListener);
    }

    IabHelper.QueryInventoryFinishedListener mProductDetailsFinishedListener = new IabHelper.QueryInventoryFinishedListener(){
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if(mIabHelper == null) return;
            if(result.isFailure()) {
                Log.d(TAG, "[ProductDetails] Error query inventory: " + result);
                return;
            }
            Log.d(TAG, "[ProductDetails] Query inventory success full: " + result);

            SkuDetails skuDetails = inventory.getSkuDetails(SKU_PROD_01);
            if(skuDetails != null)
                mSubscriptionMonthPrice = skuDetails.getPrice();

            getUsersPurchases();
            Log.d(TAG, "Product details inventory query finished!!!");
        }
    };

    public void launchSubscriptionMonthPurchaseFlow(Activity activity, String developerPayload){
        mIabHelper.launchPurchaseFlow(activity, SKU_PROD_01, RC_REQUEST, mPurchaseFinishedListener, developerPayload);
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener(){
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            if(result.isFailure()){
                Log.d(TAG, "Error purchasing: " + result);
                return;
            }

            Log.d(TAG, "Purchase successful.");
            if(purchase.getSku().equals(SKU_PROD_01)){
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                mIsPremium = true;
                mSku = purchase.getSku();
                becomePremium();
            }

            Log.d(TAG, "Purchase the month subscription finished!!!");
        }
    };

    public void getUsersPurchases(){
        if(mIabHelper != null)
            mIabHelper.queryInventoryAsync(mGotInventorListener);
    }

    IabHelper.QueryInventoryFinishedListener mGotInventorListener = new IabHelper.QueryInventoryFinishedListener(){
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                return;
            }

            if(mIabHelper == null)
                return;

            Log.d(TAG, "Query inventory was successful.");

            String sku = SKU_PROD_01;

            mIsPremium = inventory.hasPurchase(sku);
            if(mIsPremium) {
                Purchase purchase = inventory.getPurchase(sku);
                if(purchase != null) {
                    int purchaseState = purchase.getPurchaseState();
                    Log.d(TAG, "Purchase state: " + IabHelper.getResponseDesc(purchaseState));
                    if (purchaseState == PURCHASED_SUCCESSFULLY) {
                        Log.d(TAG, "The user purchase product.");
                        Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
                        mSku = purchase.getSku();

                        if(QuizizConstants.DEVELOPMENT_MODE) {
                            mIabHelper.consumeAsync(purchase, consumeFinishedListener);
                        }

                        becomePremium();
                    } else
                        keepBasicAccount();
                }else
                    Log.d(TAG, "Purchase is null!!!");
            }else
                keepBasicAccount();

            Log.d(TAG, "Initial inventory query finished!!!");
        }
    };

    IabHelper.OnConsumeFinishedListener consumeFinishedListener = new IabHelper.OnConsumeFinishedListener(){
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            if(result.isSuccess()){
                Log.d(TAG, "Consumption successful.");
                becomePremium();
            }else
                Log.d(TAG, "Error while consuming: " + result);
            Log.d(TAG, "End consumption flow.");
        }
    };

    private void becomePremium(){
        Log.d(TAG, "User is become to premium!!!");
        mConfiguration.setPremium(true);
    }

    private void keepBasicAccount(){
        Log.d(TAG, "Saved data: User hasn't subscription on Google play.");
        mConfiguration.setPremium(false);
    }

    public void destroy(){
        if(mIabHelper != null) {
            mIabHelper.dispose();
            mIabHelper = null;
        }
    }

    public boolean isPremium() {
        return mIsPremium;
    }

    public String getSubscriptionMonthPrice() {
        return mSubscriptionMonthPrice + BY_MONTH;
    }

    public IabHelper getIabHelper() {
        return mIabHelper;
    }

    public static String nextPayload() {
        return new BigInteger(130, mRandom).toString(32);
    }
}
