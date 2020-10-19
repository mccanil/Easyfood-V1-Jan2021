package com.easyfoodvone.menu.view.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.menu.adapter.AdapterMenuItems;
import com.easyfoodvone.menu.presenter.MenuPresenter;
import com.easyfoodvone.menu.view.MenuView;
import com.easyfoodvone.orders.view.impl.OrdersActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MenuActivity extends AppCompatActivity implements MenuView, NavigationView.OnNavigationItemSelectedListener {
    EditText input;
    Button action;
    MenuPresenter presenter;
    @BindView(R.id.close)
    LinearLayout close;
    @BindView(R.id.open)
    LinearLayout open;
    @BindView(R.id.home_delevery)
    LinearLayout homeDelevery;
    @BindView(R.id.home_delevery_off)
    LinearLayout homeDeleveryOff;
    private AdapterMenuItems mAdapter;
    private RecyclerView itemsList;
    private ArrayList<Integer> imageListSmall = new ArrayList<>();
    private ArrayList<String> imageListTitle = new ArrayList<>();
    private boolean isOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_menu);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();


        open.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                open.setVisibility(View.GONE);
                close.setVisibility(View.VISIBLE);
                homeDelevery.setVisibility(View.GONE);
                homeDeleveryOff.setVisibility(View.VISIBLE);
                isOpen = false;
            }
        });
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                open.setVisibility(View.VISIBLE);
                close.setVisibility(View.GONE);
                isOpen = true;
            }
        });

        homeDelevery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                homeDelevery.setVisibility(View.GONE);
                homeDeleveryOff.setVisibility(View.VISIBLE);
            }
        });
        homeDeleveryOff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(isOpen) {
                    homeDelevery.setVisibility(View.VISIBLE);
                    homeDeleveryOff.setVisibility(View.GONE);
                }
            }
        });

        setMenuItems();
    }

    @Override
    public void setInputError() {
        input.setError("Username Empty");
    }

    @Override
    public void setNavigation() {
        // startActivity(new Intent(MenuActivity.this, MainActivity.class));
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            finish();
            startActivity(new Intent(MenuActivity.this, OrdersActivity.class));
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_delivery_settings) {

        } else if (id == R.id.nav_restaurant_timings) {

        } else if (id == R.id.nav_orders_report) {

        } else if (id == R.id.nav_revenue_report) {

        } else if (id == R.id.nav_offers) {

        } else if (id == R.id.nav_review_ratings) {

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setMenuItems() {
//        imageListSmall.add(R.drawable.breakfast);
//        imageListSmall.add(R.drawable.burger);
//        imageListSmall.add(R.drawable.chicken);
//        imageListSmall.add(R.drawable.hotbox);
//        imageListSmall.add(R.drawable.sides);
//        imageListSmall.add(R.drawable.sauces_dips);
//        imageListSmall.add(R.drawable.desserts);
//        imageListTitle.add("Breakfast Snacks");
//        imageListTitle.add("Burgers");
//        imageListTitle.add("Chicken");
//        imageListTitle.add("HotBox");
//        imageListTitle.add("Sides");
//        imageListTitle.add("Sauces & Dips");
//        imageListTitle.add("Desserts");
//        itemsList = (RecyclerView) findViewById(R.id.recycler_items);
//        mAdapter = new AdapterMenuItems(getApplicationContext(), imageListSmall, imageListTitle);
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
//        itemsList.setLayoutManager(mLayoutManager);
//        itemsList.setItemAnimator(new DefaultItemAnimator());
//        itemsList.setAdapter(mAdapter);
    }
}
