package com.easyfoodvone.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easyfoodvone.R;
import com.easyfoodvone.adapters.AdapterProfileImage;
import com.easyfoodvone.login.models.LoginResponse;
import com.easyfoodvone.utility.Constants;
import com.easyfoodvone.utility.SlideshowDialogFragment;

import java.io.File;
import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    EditText postcode, restname, serve_style, about, web, landline, phone, email, address;
    @BindView(R.id.image_list_view)
    RecyclerView imageListView;
    @BindView(R.id.add_more_image)
    Button addMoreImage;
    Unbinder unbinder;
    @BindView(R.id.btn_save)
    Button btnSave;
    LoginResponse.Data data;
    private Context mContext;
    private Activity mActivity;
    private View view;
    private AdapterProfileImage imageAdapter;
    SlideshowDialogFragment newFragment;
    String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE};
    private final int PICK_IMAGE_CAMERA = 101, PICK_IMAGE_GALLERY = 102;

    public ProfileFragment() {

    }


    @SuppressLint("ValidFragment")
    public ProfileFragment(Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        postcode = view.findViewById(R.id.postcode);
        restname = view.findViewById(R.id.restname);
        serve_style = view.findViewById(R.id.serve_style);
        about = view.findViewById(R.id.about);
        web = view.findViewById(R.id.web);
        phone = view.findViewById(R.id.phone);
        landline = view.findViewById(R.id.landline);
        address = view.findViewById(R.id.address);
        email = view.findViewById(R.id.email);

        data = Constants.getStoredData(getActivity());


        setData();


        unbinder = ButterKnife.bind(this, view);
        setRecyclerView();
        addMoreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog("Profile details are saved successfully.");
            }
        });
        return view;
    }

    private void setData() {
        postcode.setText(data.getPost_code());
        restname.setText(data.getRestaurant_name());
        serve_style.setText(data.getServe_style());
        about.setText(data.getAbout());
        web.setText(data.getWebsite_url());
        phone.setText(data.getPhone_number());
        landline.setText(data.getLandline_number());
        address.setText(data.getAddress());
        email.setText(data.getEmail());
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void setRecyclerView() {


        imageAdapter = new AdapterProfileImage(mContext, data.getRestaurant_images());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        imageListView.setLayoutManager(linearLayoutManager);
        imageListView.setItemAnimator(new DefaultItemAnimator());
        imageListView.setAdapter(imageAdapter);

        imageListView.addOnItemTouchListener(new AdapterProfileImage.RecyclerTouchListener(mContext, imageListView, new AdapterProfileImage.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", (Serializable) data.getRestaurant_images());
                bundle.putInt("position", position);

                FragmentTransaction ft = getFragmentManager().beginTransaction().addToBackStack(null);
                newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void selectImage() {
        try {
            PackageManager pm = mContext.getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, mContext.getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();

                            Intent m_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                            Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                            m_intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(m_intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else {
                EasyPermissions.requestPermissions(this, "All permissions are required in oder to run this application", 101, permissions);
                Toast.makeText(mContext, "Camera Permission error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                if (requestCode == 101 && resultCode == RESULT_OK) {


                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (requestCode == PICK_IMAGE_GALLERY) {

            try {
                Uri contentURI = data.getData();

            } catch (Exception e) {

            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void alertDialog(String msg) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View mDialogView = factory.inflate(R.layout.alert_dialog, null);
        final android.app.AlertDialog mDialog = new android.app.AlertDialog.Builder(mActivity).create();
        mDialog.setView(mDialogView);
        TextView msgText = mDialogView.findViewById(R.id.txt_message);
        msgText.setText(msg);
        mDialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });

        mDialog.show();
    }
}
