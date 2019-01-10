package org.openmrs.mobile.activities.patientdashboard.contacts;

/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.activities.patientcontacts.DatabaseHelper;
import org.openmrs.mobile.activities.patientcontacts.MainActivity;
import org.openmrs.mobile.activities.patientcontacts.Name;
import org.openmrs.mobile.activities.patientcontacts.NameAdapter;
import org.openmrs.mobile.activities.patientcontacts.NetworkStateChecker;
import org.openmrs.mobile.activities.patientcontacts.VolleySingleton;
import org.openmrs.mobile.activities.patientcontacts.lists;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardFragment;
import org.openmrs.mobile.activities.patientdashboard.details.PatientDashboardDetailsPresenter;
import org.openmrs.mobile.activities.patientdashboard.details.PatientPhotoActivity;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactPatientDetailsFragment extends PatientDashboardFragment implements PatientDashboardContract.ViewPatientDetails {

    private MainActivity mClass;


    private Button buttonOpen;
    private Button buttonSave;
    private View rootView;
    private EditText editTextName;
    private ListView listViewNames;
    private TextView patientDetailsNames;

    private List<Name> names;

    private PatientDashboardActivity mPatientDashboardActivity;


    public static final String URL_SAVE_NAME = "http://192.168.0.101/SyncData/saveName.php";
    private org.openmrs.mobile.activities.patientcontacts.DatabaseHelper db;
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";
    private BroadcastReceiver broadcastReceiver;
    private NameAdapter nameAdapter;



    public static ContactPatientDetailsFragment newInstance() {
        return new org.openmrs.mobile.activities.patientdashboard.contacts.ContactPatientDetailsFragment();
    }

    @Override
    public void attachSnackbarToActivity() {
        Snackbar snackbar = Snackbar
                .make(mPatientDashboardActivity.findViewById(R.id.patientDashboardContentFrame), getString(R.string.snackbar_no_internet_connection), Snackbar.LENGTH_INDEFINITE);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPatientDashboardActivity = (PatientDashboardActivity) context;
    }

   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setPresenter(mPresenter);

        setHasOptionsMenu(true);

        mClass = new MainActivity();


        //    pasted from here

    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.patient_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSynchronize:
                ((PatientDashboardDetailsPresenter) mPresenter).synchronizePatient();
                break;
            case R.id.actionUpdatePatient:
                startPatientUpdateActivity(mPresenter.getPatientId());
                break;
            default:
                // Do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragments_contacts_add, null, false);
//       pasted from here



        FontsUtil.setFont((ViewGroup) rootView);

        super.onCreate(savedInstanceState);
        buttonOpen = (Button) rootView.findViewById(R.id.buttonOpen);

        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), lists.class);
                startActivity(intent);

            }
        });
        buttonSave = (Button) rootView.findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

             EditText  editname  = (EditText) rootView.findViewById(R.id.editTextName);
             TextView  patientDetailsNames  = (TextView) rootView.findViewById(R.id.patientDetailsNames);

             String text = editname.getText().toString();
             String text1 = patientDetailsNames.getText().toString();


               Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("mytext",text);
                intent.putExtra("idnt",text1);

                startActivity(intent);

//
//                MainActivity main = new MainActivity();
//                main.editTextName.setText(text);
//
//                main.saveNameToServer();



            }
        });

        return rootView;
    }



    @Override
    public void resolvePatientDataDisplay(final Patient patient) {

    }

    @Override
    public void showDialog(int resId) {
        mPatientDashboardActivity.showProgressDialog(resId);
    }

    private void showAddressDetailsViewElement(View detailsLayout, int detailsViewId, String detailsText) {
        if (StringUtils.notNull(detailsText) && StringUtils.notEmpty(detailsText)) {
            ((TextView) detailsLayout.findViewById(detailsViewId)).setText(detailsText);
        } else {
            detailsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void dismissDialog() {
        mPatientDashboardActivity.dismissCustomFragmentDialog();
    }

    @Override
    public void showToast(int stringRes, boolean error) {
        ToastUtil.ToastType toastType = error ? ToastUtil.ToastType.ERROR : ToastUtil.ToastType.SUCCESS;
        ToastUtil.showShortToast(mPatientDashboardActivity, toastType, stringRes);
    }

    @Override
    public void setMenuTitle(String nameString, String identifier) {
        mPatientDashboardActivity.getSupportActionBar().setTitle(nameString);
        mPatientDashboardActivity.getSupportActionBar().setSubtitle("#" + identifier);
        ((TextView) rootView.findViewById(R.id.patientDetailsNames)).setText(identifier);
    }


    @Override
    public void startPatientUpdateActivity(long patientId) {
        Intent updatePatient = new Intent(mPatientDashboardActivity, AddEditPatientActivity.class);
        updatePatient.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE,
                String.valueOf(patientId));
        startActivity(updatePatient);
    }

    public void showPatientPhoto(Bitmap photo, String patientName) {
        Intent intent = new Intent(getContext(), PatientPhotoActivity.class);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        intent.putExtra("photo", byteArrayOutputStream.toByteArray());
        intent.putExtra("name", patientName);
        startActivity(intent);
    }

//    pasted from here





}






