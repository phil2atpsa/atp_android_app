package za.co.atpsa.atp.fragments;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import za.co.atpsa.atp.R;
import za.co.atpsa.atp.adapter.CountryItemsAdapter;
import za.co.atpsa.atp.entities.Country;
import za.co.atpsa.atp.utils.FileUtils;
import za.co.atpsa.atp.utils.MyLocale;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.gateway.Bills;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.SEND_SMS;

public class MiniBill extends Fragment implements OnServiceResponseListener {
    private final int REQUEST_READ_CONTACTS = 0;
    public final int PICK_CONTACT = 2015;
    public final int PICKFILE_REQUEST_CODE =  3032;
    List<Country> countryList;
    Spinner country_code, currency;
    Country selected_country;
    LinearLayout items;
    ImageView add_item;
    EditText description, total_amount, line_amount, atp_ref, names, email, telephone_number;
    Button send_bill;
    LinearLayout parent;
    TextView file_selected;
    ArrayList<Uri> selected_file = new ArrayList<>();



    double amount = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mini_bill,
                container, false);
        countryList = Country.getList(getActivity());
        country_code = view.findViewById(R.id.country_code);
        currency = view.findViewById(R.id.currency);
        items = view.findViewById(R.id.items);
        add_item = view.findViewById(R.id.add_item);
        description = view.findViewById(R.id.description);
        line_amount = view.findViewById(R.id.line_amount);
        total_amount = view.findViewById(R.id.total_amount);
        atp_ref = view.findViewById(R.id.atp_ref);
        names = view.findViewById(R.id.names);
        email = view.findViewById(R.id.email);
        telephone_number = view.findViewById(R.id.telephone_number);
        send_bill = view.findViewById(R.id.send_bill);
        parent = view.findViewById(R.id.parent);
        file_selected= view.findViewById(R.id.file_selected);


        ArrayList<Country> countries = new ArrayList<>();
        countries.addAll(countryList);


        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.currency_arrays));
        currency.setAdapter(adapter2);


        CountryItemsAdapter adapter = new CountryItemsAdapter(getActivity().getApplicationContext(), countries, true);
        country_code.setAdapter(adapter);
        country_code.setSelection(MyLocale.getLocale(countryList), true);

        selected_country = countryList.get(country_code.getSelectedItemPosition());

        country_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_country = countryList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items.getChildCount() == 0) {
                    if (TextUtils.isEmpty(description.getText()) || TextUtils.isEmpty(line_amount.getText())) {
                        if (TextUtils.isEmpty(description.getText())) {
                            description.setError("Description is required");
                            description.requestFocus();
                        }
                        if (TextUtils.isEmpty(line_amount.getText())) {
                            line_amount.setError("Amount is required");
                            line_amount.requestFocus();
                        }
                    } else {
                        addLineItem();
                    }
                } else {
                    int i = items.getChildCount();

                    LinearLayout ll = (LinearLayout) items.getChildAt(i - 1);
                    EditText description = (EditText) ll.getChildAt(0);
                    EditText amount = (EditText) ll.getChildAt(1);
                    if (TextUtils.isEmpty(description.getText()) || TextUtils.isEmpty(line_amount.getText())) {
                        if (TextUtils.isEmpty(description.getText())) {
                            description.setError("Description is required");
                            description.requestFocus();
                        }
                        if (TextUtils.isEmpty(amount.getText())) {
                            amount.setError("Amount is required");
                            amount.requestFocus();
                        }
                    } else {
                        addLineItem();
                    }

                }
            }
        });

        line_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setHasOptionsMenu(true);

        send_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(atp_ref.getText())) {
                    atp_ref.setError("Reference Required");
                    atp_ref.requestFocus();
                } else if (TextUtils.isEmpty(names.getText())) {
                    names.setError("Name Required");
                    names.requestFocus();
                } else if (TextUtils.isEmpty(email.getText())) {
                    email.setError("Email Required");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(telephone_number.getText())) {
                    telephone_number.setError("Telephone Number Required");
                    telephone_number.requestFocus();
                } else if (TextUtils.isEmpty(description.getText())) {
                    description.setError("Description is required");
                    description.requestFocus();
                } else if (TextUtils.isEmpty(line_amount.getText())) {
                    line_amount.setError("Amount is required");
                    line_amount.requestFocus();
                } else {
                    if (validateItems()) {
                        String country_code = selected_country.getDial_code().replace("+", "").replaceAll("\\s+", "");
                        String cell_no = telephone_number.getText().toString().replaceAll("\\s+", "");

                        String dial_code = selected_country.getDial_code().replaceAll("\\s+", "");

                        if (cell_no.startsWith(dial_code)) {

                            cell_no = cell_no.substring(dial_code.length(), cell_no.length());
                            cell_no = "0".concat(cell_no);
                        }


                        JSONObject bill = new JSONObject();
                        try {
                            bill.put("name", names.getText());
                            bill.put("email", email.getText());
                            bill.put("country_code", country_code);
                            bill.put("cell_no", cell_no);
                            JSONArray line_items = new JSONArray();
                            JSONObject line_item = new JSONObject();

                            line_item.put("description", description.getText());
                            line_item.put("amount", line_amount.getText());
                            line_items.put(0, line_item);

                            for (int i = 0; i < items.getChildCount(); i++) {
                                LinearLayout ll = (LinearLayout) items.getChildAt(i);
                                EditText description = (EditText) ll.getChildAt(0);
                                EditText amount = (EditText) ll.getChildAt(1);
                                line_item = new JSONObject();
                                line_item.put("description", description.getText());
                                line_item.put("amount", amount.getText());
                                line_items.put(i + 1, line_item);
                            }
                            bill.put("items", line_items);
                            bill.put("currency", currency.getSelectedItem().toString());
                            bill.put("ATP_ref", atp_ref.getText());
                            bill.put("notify_url", "https://thevaluegateway.com/notify");
                            bill.put("mode", "bill");

                            new Bills(getActivity(), MiniBill.this, true).send_bill(bill);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        return view;
    }

    private boolean validateItems() {
        //boolean validated = false;
        for (int i = 0; i < items.getChildCount(); i++) {
            LinearLayout ll = (LinearLayout) items.getChildAt(i);
            EditText description = (EditText) ll.getChildAt(0);
            EditText amount = (EditText) ll.getChildAt(1);
            if (TextUtils.isEmpty(description.getText()) || TextUtils.isEmpty(amount.getText())) {
                if (TextUtils.isEmpty(description.getText())) {
                    description.setError("Description is required");
                    description.requestFocus();
                }
                if (TextUtils.isEmpty(amount.getText())) {
                    amount.setError("Amount is required");
                    amount.requestFocus();
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.mini_bill, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        //respond to menu item selection
        switch (item.getItemId()) {
            case R.id.action_import:
                openContactPicker();
                return true;
            case R.id.action_attach:
                if (!requestStorage()) {
                    return false;
                }
                filePickerIntent();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    public void addLineItem() {
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final LinearLayout wrapper = new LinearLayout(getActivity());
        lparams.setMargins(0, 0, 0, 9);
        wrapper.setLayoutParams(lparams);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.setPadding(3, 3, 3, 3);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, .45f
        );
        params.gravity = Gravity.BOTTOM;


        EditText description = new EditText(getActivity());
        description.setInputType(InputType.TYPE_CLASS_TEXT);
        // description.setTextAppearance(R.style.SwingersEditTextStyle);
        description.setLayoutParams(params);
        description.setHint("Description");

        wrapper.addView(description);

        EditText amount = new EditText(getActivity());
        amount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        amount.setLayoutParams(params);
        amount.setHint("Amount");

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        wrapper.addView(amount);

        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.RIGHT;

        ImageView btn_minus = new ImageView(getActivity());
        btn_minus.setLayoutParams(params);
        btn_minus.setImageResource(R.drawable.ic_indeterminate_check_box_red_24dp);

        wrapper.addView(btn_minus);

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.removeView(wrapper);
                doAmount();
            }
        });

        items.addView(wrapper);


    }

    private void openContactPicker() {
        if (!mayRequestContacts()) {
            return;
        }
        contactIntent();

        //contact_query();

        // getLoaderManager().initLoader(0, null, this);
    }
    private void filePickerIntent(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("application/pdf");


        startActivityForResult(intent, PICKFILE_REQUEST_CODE);

    }
    private void contactIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT);

    }

    private boolean requestStorage(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, PICKFILE_REQUEST_CODE);
        }
        return false;
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(getContext(), READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{READ_CONTACTS, SEND_SMS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            contactIntent();
            // getLoaderManager().initLoader(0, null, this);
        } else if(requestCode == PICKFILE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // We were not granted permission this time, so don't try to show the contact picker
            filePickerIntent();

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == getActivity().RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);
            while (cursor.moveToNext()) {
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);


                telephone_number.setText(cursor.getString(column));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                names.setText(name);

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor cur1 = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (cur1.moveToNext()) {

                    String email_address = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    if (email_address != null) {
                        email.setText(email_address);
                    }
                }
                cur1.close();


                // Log.d("phone number", cursor.getString(column));
            }
        }
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {

            if (data != null) {
                selected_file.clear();
                Bundle extras = data.getExtras();


                Log.e("URI", "Uri: " +data.getClipData());
                //showImage(uri);
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {

                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri u = item.getUri();

                            //uris.add(uri);
                            selected_file.add(u);
                          //  Log.e("URIS", "Uri: " + u.toString());
                            //MediaStore.Files.
                        }
                        file_selected.setText(clipData.getItemCount() + " files selected");
                        // Do someting
                    }
                } else {
                    Uri uri  = data.getData();
                    selected_file.add(uri);
                    file_selected.setText("1 file selected");
                }
            }
            }

        }


    private void clear() {
        atp_ref.setText("");
        names.setText("");
        email.setText("");
        telephone_number.setText("");
        description.setText("");
        line_amount.setText("");
        items.removeAllViews();
        doAmount();


    }

    private void doAmount() {
        try {
            double first_line = Double.valueOf(line_amount.getText().toString());

            for (int i = 0; i < items.getChildCount(); i++) {
                LinearLayout ll = (LinearLayout) items.getChildAt(i);
                first_line += Double.valueOf(((EditText) ll.getChildAt(1)).getText().toString());
            }
            amount = first_line;
            total_amount.setText(String.valueOf(amount));
        } catch (NumberFormatException e){
            amount = 0;
            total_amount.setText("");
        }
    }



    @Override
    public void completed(Object object) {
        JSONObject response = (JSONObject) object;
        if(response.optInt("success") == 0){


            Snackbar snackbar = Snackbar.make(parent, response.optString("error"), Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
            } else {
                sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
            }

            snackbar.show();




        } else {
            for(int i = 0; i < selected_file.size(); i++) {
                Uri content_describer = selected_file.get(i);
                try {
                    InputStream in = getActivity().getContentResolver().openInputStream(content_describer);
                    String file_name = getName(getContext(), content_describer);
                    Log.e("file_name", file_name);

                    File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                    storageDir.mkdirs();
                    final File file = new File(storageDir, file_name);

                    byte[] buffer = new byte[in.available()];
                    in.read(buffer);
                    OutputStream outStream = new FileOutputStream(file);
                    outStream.write(buffer);

                    RequestParams params = new RequestParams();
                    params.put("docs", file);
                    params.put("bill_id", response.optInt("bill_id"));
                    AsyncHttpClient client = new AsyncHttpClient();
                    Log.e("response", new String("https://atpevs.co.za/evs/api/v1/mobile/bill/upload"));
                    client.post("https://atpevs.co.za/evs/api/v1/mobile/bill/upload", params, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                          //  Log.e("Response", responseBody.toString());
                            selected_file.clear();
                            file_selected.setText("");
                            file.delete();

                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
                            Log.e("Error",responseString);

                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable,  JSONObject jsonObject){

                            Log.e("Error", jsonObject.toString());

                        }
                    });


                } catch (FileNotFoundException e) {
                   Log.e("FileNotFoundException", e.getMessage());
                } catch (URISyntaxException e) {
                    Log.e("URISyntaxException", e.getMessage());
                } catch (IOException e) {
                    Log.e("IOException", e.getMessage());
                }
            }


            Snackbar snackbar = Snackbar.make(parent, response.optString("message"), Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_success));
            } else {
                sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_success));
            }

            snackbar.show();
            clear();
        }
    }

    @Override
    public void failed(ServiceException e) {
        Snackbar snackbar = Snackbar.make(parent,  e.getMessage(), Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
        } else {
            sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
        }



        snackbar.show();

    }

    public static String getName(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Files.FileColumns.DISPLAY_NAME };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }


        return null;
    }
}
