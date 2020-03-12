package com.lexxdigitals.easyfoodvone.charity;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.databinding.FragmentSuccessBinding;
import com.lexxdigitals.easyfoodvone.orders.view.impl.OrdersActivity;


import static com.lexxdigitals.easyfoodvone.utility.Helper.setFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessFragment extends Fragment implements View.OnClickListener {
    private FragmentSuccessBinding binding;
    private Context context;
    private boolean isCancel;

    public SuccessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_success, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OrdersActivity.getInstance().setBackAction(2);
        getDataFromIntent();
        init();
        setListeners();


    }


    private void getDataFromIntent() {
        isCancel = getArguments().getBoolean("IS_CANCEL", false);
    }

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

    private void setListeners() {

        binding.cvOk.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_ok:
                CharityFragment.getInstance().getCharityInfo();
                CharityFragment successFragment = new CharityFragment();
                setFragment(successFragment, false, getActivity(), R.id.frameLayout);
                break;
        }
    }


}
