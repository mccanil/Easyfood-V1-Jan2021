package com.lexxdigitals.easyfoodvone.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.adapters.DragAndDropGridAdapter;
import com.lexxdigitals.easyfoodvone.api_handler.ApiClient;
import com.lexxdigitals.easyfoodvone.api_handler.ApiInterface;
import com.lexxdigitals.easyfoodvone.helper.OnStartDragListener;
import com.lexxdigitals.easyfoodvone.helper.SimpleItemTouchHelperCallback;
import com.lexxdigitals.easyfoodvone.login.models.LoginResponse;
import com.lexxdigitals.easyfoodvone.menu.CommonRequest;
import com.lexxdigitals.easyfoodvone.menu.MenuCategoryList;
import com.lexxdigitals.easyfoodvone.new_order.models.CommonResponse;
import com.lexxdigitals.easyfoodvone.utility.Constants;
import com.lexxdigitals.easyfoodvone.utility.LoadingDialog;
import com.lexxdigitals.easyfoodvone.utility.UserPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class MenuFragment extends Fragment implements OnStartDragListener, DragAndDropGridAdapter.OnItemPositionChanged {
    private Context mContext;
    View view;
    @BindView(R.id.recycler_items)
    RecyclerView recyclerItems;
    Unbinder unbinder;
    LoginResponse.Data baseData;
    MenuCategoryList menuCategoryList;
    DragAndDropGridAdapter adapter;
    boolean isFirstTimeOnPage = true;


    private ItemTouchHelper mItemTouchHelper;

    public MenuFragment() {
    }


    @SuppressLint("ValidFragment")
    public MenuFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseData = (LoginResponse.Data) UserPreferences.getUserPreferences().getResponse(mContext, Constants.LOGIN_RESPONSE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        unbinder = ButterKnife.bind(this, view);


        return view;
    }

    private void callMenuList(List<MenuCategoryList.MenuCategories> menu_items) {
        adapter = new DragAndDropGridAdapter(getActivity(), this, menu_items);
        if (recyclerItems != null)
            recyclerItems.setAdapter(adapter);
        final int spanCount = 2;
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
        recyclerItems.setLayoutManager(layoutManager);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerItems);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder, int position) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public void getAllMenuCategories(String restaurantId) {

        final LoadingDialog dialog = new LoadingDialog(getActivity(), "Loading menus...");
        dialog.setCancelable(false);
        if (isFirstTimeOnPage)
            dialog.show();
        isFirstTimeOnPage = false;


        try {
            CommonRequest request = new CommonRequest();
            request.setRestaurant_id(restaurantId);
            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getMenuCategories(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<MenuCategoryList>() {
                        @Override
                        public void onSuccess(MenuCategoryList data) {

                            dialog.dismiss();
                            if (data.isSuccess()) {
                                menuCategoryList = data;
                                callMenuList(data.getMenu_items());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Loading failed ", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("Exception ", e.toString());
            Toast.makeText(getActivity(), "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    @Override
    public void onItemPositionChanged(int currentPosition, int previousPosition, MenuCategoryList.MenuCategories menuCategories) {
        try {
            CommonRequest request = new CommonRequest();
            request.setRestaurant_id(Constants.getStoredData(getActivity()).getRestaurant_id());
            request.setMenu_category_id(menuCategories.getMenu_category_id());
            request.setMenu_category_current_position(previousPosition + "");
            request.setMenu_category_new_position(currentPosition + "");
            ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(apiService.getMenuCategoryItemsPositin(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CommonResponse>() {
                        @Override
                        public void onSuccess(CommonResponse data) {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "Loading failed", Toast.LENGTH_LONG).show();
                            Log.e("onError", "onError: " + e.getMessage());
                        }
                    }));

        } catch (Exception e) {
            Log.e("Exception ", e.toString());
            Toast.makeText(getActivity(), "Server not responding.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllMenuCategories(baseData.getRestaurant_id());

    }


}
