/*Created by Omnisttechhub Solution*/
package com.easyfoodvone.charity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.databinding.ActivitySucessBinding;
import com.easyfoodvone.orders.view.impl.OrdersActivity;

public class SucessActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySucessBinding binding;
    private boolean isCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sucess);
        getDataFromIntent();
        init();
        setListeners();

    }

    /**********User Defined Method for getting data through Intent *********/
    private void getDataFromIntent() {
        isCancel = getIntent().getBooleanExtra("IS_CANCEL", false);
    }

    /**********User Defined Method for initializing *********/
    private void init() {
        if (isCancel) {
            binding.tvThank.setVisibility(View.GONE);
            binding.ivBanner.setImageDrawable(getResources().getDrawable(R.drawable.banner_two));
            binding.tvSurity.setText(getResources().getString(R.string.your_donation_cancelled));
            binding.tvSucessMsg.setText(getResources().getString(R.string.cancel_sucees_dialog_msg));
        } else {
            binding.ivBanner.setImageDrawable(getResources().getDrawable(R.drawable.baneer_three));
            binding.tvThank.setVisibility(View.VISIBLE);
            binding.tvSurity.setText(getResources().getString(R.string.we_have_now_alerted_local));
            binding.tvSucessMsg.setText(getResources().getString(R.string.doantion_sucess_msg));
        }
    }
    /**********User Defined Method to setListeners *********/
    private void setListeners() {
        binding.ivLogo.setOnClickListener(this);
        binding.cvOk.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_logo:
                startActivity(new Intent(SucessActivity.this, OrdersActivity.class));
                finishAffinity();
                break;
            case R.id.cv_ok:
                CharityFragment.getInstance().getCharityInfo();
                finish();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CharityFragment.getInstance().getCharityInfo();

    }
}
