package za.co.atpsa.atp.fragments.donation;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.adapter.CountryItemsAdapter;
import za.co.atpsa.atp.entities.Country;
import za.co.atpsa.atp.utils.MyLocale;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.gateway.Donations;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.SEND_SMS;


public class RepeatPayments  extends Fragment implements View.OnClickListener, OnServiceResponseListener {
    private final int REQUEST_READ_CONTACTS = 0;
    public final int PICK_CONTACT = 2015;
    View mView;
    List<Country> countryList;
    Spinner country_code,run_date,repeat_times;
    Country selected_country;

    EditText description, name, email, telephone_number,surname,amount;
    Button send_bill;
    LinearLayout parent;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.repeat_payments, container, false);
        countryList = Country.getList(getActivity());
        country_code = mView.findViewById(R.id.country_code);
        description = mView.findViewById(R.id.description);
        name = mView.findViewById(R.id.name);
        email = mView.findViewById(R.id.email);
        telephone_number = mView.findViewById(R.id.telephone_number);
        send_bill = mView.findViewById(R.id.send_bill);
        parent = mView.findViewById(R.id.parent);
        surname = mView.findViewById(R.id.surname);
        run_date =  mView.findViewById(R.id.run_date);
        repeat_times =  mView.findViewById(R.id.repeat_times);
        amount  = mView.findViewById(R.id.amount);

        ArrayList<Country> countries = new ArrayList<>();
        countries.addAll(countryList);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.run_date));
        run_date.setAdapter(adapter2);

        adapter2 = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.repeat_times));
        repeat_times.setAdapter(adapter2);





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

        send_bill.setOnClickListener(this);
        setHasOptionsMenu(true);
        return  mView;
    }
    @Override
    public void onClick(View v) {
        if(TextUtils.isEmpty(name.getText())){
            name.setError("Name is required");
            name.requestFocus();
        } else if (TextUtils.isEmpty(surname.getText())){
            surname.setError("Surname is required");
            surname.requestFocus();
        } else if (TextUtils.isEmpty(email.getText())){
            email.setError("Name is required");
            email.requestFocus();
        } else if (TextUtils.isEmpty(telephone_number.getText())){
            telephone_number.setError("Name is required");
            telephone_number.requestFocus();
        }  else if (TextUtils.isEmpty(description.getText())){
            description.setError("Description is required");
            description.requestFocus();
        }   else if (TextUtils.isEmpty(amount.getText())){
            amount.setError("Amount is required");
            amount.requestFocus();
        } else {
            JSONObject object = new JSONObject();
            try {
                String country_code = selected_country.getDial_code().replace("+", "").replaceAll("\\s+", "");
                String cell_no = telephone_number.getText().toString().replaceAll("\\s+", "");

                String dial_code = selected_country.getDial_code().replaceAll("\\s+", "");

                if (cell_no.startsWith(dial_code)) {

                    cell_no = cell_no.substring(dial_code.length(), cell_no.length());
                    cell_no = "0".concat(cell_no);
                }

                object.put("name", name.getText());
                object.put("surname", surname.getText());
                object.put("email", email.getText());
                object.put("cell_no", cell_no);
                object.put("country_code",country_code);
                object.put("description", description.getText());
                object.put("run_date", run_date.getSelectedItem().toString());
                object.put("repeat_times", repeat_times.getSelectedItem().toString());
                object.put("amount", Double.parseDouble(amount.getText().toString()));

                new Donations(getContext(),RepeatPayments.this, true).send_request(object, Donations.Module.PAYMENTS);

            } catch (JSONException e) {
                e.printStackTrace();
            }


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

    private void clear(){
        name.setText("");
        surname.setText("");
        description.setText("");
        email.setText("");
        telephone_number.setText("");
        amount.setText("");
        run_date.setSelection(0, true);
        repeat_times.setSelection(0, true);


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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.collections, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void openContactPicker() {
        if (!mayRequestContacts()) {
            return;
        }
        contactIntent();

        //contact_query();

        // getLoaderManager().initLoader(0, null, this);
    }






    public boolean onOptionsItemSelected(MenuItem item) {
        //respond to menu item selection
        switch (item.getItemId()) {
            case R.id.action_import:
                openContactPicker();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void contactIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT);

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
        } else {

            // We were not granted permission this time, so don't try to show the contact picker
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
                String str_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                name.setText(str_name);

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
    }
}
