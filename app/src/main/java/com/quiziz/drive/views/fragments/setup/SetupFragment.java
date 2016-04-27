package com.quiziz.drive.views.fragments.setup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quiziz.drive.R;
import com.quiziz.drive.model.Configuration;
import com.quiziz.drive.util.BillingHelper;
import com.quiziz.drive.views.activities.web.WebActivity;
import com.quiziz.drive.util.QuizizConstants;
import com.quiziz.drive.views.fragments.setup.adapter.SetupArrayAdapter;

/**
 * Created by pototo 28/03/16.
 */
public class SetupFragment extends Fragment
    implements SetupArrayAdapter.OnSetupArrayAdapterListener {

    private static final String TAG = "SetupFragment";
    private ListView mSetupListView;
    private SetupArrayAdapter mSetupArrayAdapter;
    private ImageView mExitImageView;
    private ImageView mBasicImageView;
    private ImageView mPremiumImageView;
    private TextView mPremiumLabelTextView;
    private RelativeLayout mSetupPremiumLogoRelativeLayout;
    private RelativeLayout mPremiumRelativeLayout;
    private View mRootView;
    private BillingHelper mBillingHelper;
    private Configuration mConfiguration;
    private OnSetupFragmentListener mCallback;

    public interface OnSetupFragmentListener {
        void exit();
        void setGoogleAnalytics(String screenName);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (OnSetupFragmentListener)activity;
        }catch(ClassCastException exception){
            throw new ClassCastException(activity.toString() + " must implement OnSetupFragmentListener.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfiguration = new Configuration(getActivity());
        mBillingHelper = BillingHelper.getInstance(getActivity());
        if(mCallback != null)
            mCallback.setGoogleAnalytics(QuizizConstants.GA_SETUP_TRACKER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_setup, container, false);
        setSetupPremiumLogoRelativeLayout();
        setSetupListView();
        setExitImageView();
        setPremiumRelativeLayout();
        setBasicImageView();
        setPremiumImageView();
        setPremiumLabelTextView();
        return mRootView;
    }

    private void setSetupPremiumLogoRelativeLayout() {
        mSetupPremiumLogoRelativeLayout = (RelativeLayout)mRootView.findViewById(R.id.setup_premium_logo);
        if(mSetupPremiumLogoRelativeLayout != null)
            mSetupPremiumLogoRelativeLayout.setBackgroundResource(mConfiguration.isPremium() ? R.color.bg_premium_icon : R.color.bg_basic_icon);
    }

    private void setPremiumRelativeLayout() {
        mPremiumRelativeLayout = (RelativeLayout)mRootView.findViewById(R.id.setup_premium);
        if (mPremiumRelativeLayout != null) {
            mPremiumRelativeLayout.setVisibility(mConfiguration.isPremium() ? View.GONE : View.VISIBLE);
            mPremiumRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mConfiguration.isPremium())
                        Toast.makeText(getActivity(), getString(R.string.setup_premium_message), Toast.LENGTH_SHORT).show();
                    else
                        mBillingHelper.launchSubscriptionMonthPurchaseFlow(getActivity(), BillingHelper.nextPayload());

                }
            });
        }
    }

    private void setExitImageView() {
        mExitImageView = (ImageView)mRootView.findViewById(R.id.setup_exit);
        if(mExitImageView != null){
            mExitImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCallback != null)
                        mCallback.exit();
                }
            });
        }

    }

    private void setPremiumLabelTextView() {
        mPremiumLabelTextView = (TextView)mRootView.findViewById(R.id.setup_premium_label);
        if(mPremiumLabelTextView != null) {
            mPremiumLabelTextView.setText(mConfiguration.isPremium() ? getString(R.string.setup_premium_label) : getString(R.string.setup_basic_label));
            mPremiumLabelTextView.setTextColor(mConfiguration.isPremium() ? getResources().getColor(R.color.font_premium_icon) : getResources().getColor(R.color.font_basic_icon));
        }
    }

    private void setPremiumImageView() {
        mPremiumImageView = (ImageView)mRootView.findViewById(R.id.setup_premium_image);
        if (mPremiumImageView != null)
            mPremiumImageView.setImageResource(mConfiguration.isPremium() ? R.drawable.icon_version_premium_on : R.drawable.icon_version_premium_off);
    }

    private void setBasicImageView() {
        mBasicImageView = (ImageView)mRootView.findViewById(R.id.setup_basic_image);
        if (mBasicImageView != null)
            mBasicImageView.setImageResource(mConfiguration.isPremium() ? R.drawable.icon_version_basic_off : R.drawable.icon_version_basic_on);
    }

    @Override
    public void mailToIntent() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setClassName(QuizizConstants.GMAIL_PACKAGE_NAME, QuizizConstants.GMAIL_CLASS_NAME);
        emailIntent.setData(Uri.parse(QuizizConstants.MAIL_TO));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{QuizizConstants.TO});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, QuizizConstants.SUBJECT);
        emailIntent.putExtra(Intent.EXTRA_TEXT, QuizizConstants.BODY);

        try {
            startActivity(emailIntent);
            Log.i(TAG, "Finished sending email...");
        }catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There is no Gmail client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void playStoreIntent() {
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
        playStoreIntent.setData(Uri.parse(QuizizConstants.PLAY_STORE_URL));
        startActivity(playStoreIntent);
    }

    @Override
    public void webIntent(String url) {
        Intent webIntent = new Intent(getActivity(), WebActivity.class);
        webIntent.putExtra(WebActivity.URL_EXTRA_PARAM, url);
        startActivity(webIntent);
    }

    private void setSetupListView() {
        mSetupListView = (ListView)mRootView.findViewById(R.id.setup_list_view);
        if(mSetupListView != null){
            mSetupArrayAdapter = new SetupArrayAdapter(getActivity(), QuizizConstants.SETUP);
            mSetupArrayAdapter.attach(this);
            mSetupListView.setAdapter(mSetupArrayAdapter);
        }
    }
}
