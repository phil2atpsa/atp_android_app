package za.co.atpsa.atp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pusher.pushnotifications.PushNotifications;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import za.co.atpsa.atp.helpers.HandlePayment;
import za.co.atpsa.atp.utils.credit_card.CreditCardExpiryTextWatcher;
import za.co.atpsa.atp.utils.credit_card.CreditCardFormattingTextWatcher;
import za.co.atpsa.atp.utils.credit_card.CreditCardUtils;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Access;
import za.co.atpsa.sp_manager.Paygate;
import za.co.atpsa.ui.TransparentProgressDialog;

public class CreditCard extends AppCompatActivity implements OnServiceResponseListener {
    private EditText card_number,expiry_date,card_name,ccv;
    private TextView amount,description;
    private CreditCardFormattingTextWatcher tv;
    private CreditCardExpiryTextWatcher expiryTextWatcher;
    private Bundle bundle;
    private String ref;

    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    private CardView card_view;
    private String currency;
    private ConstraintLayout parent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit_card);
        card_number = findViewById(R.id.card_number);
        expiry_date = findViewById(R.id.expiry_date);
        ccv = findViewById(R.id.ccv);
        card_name  = findViewById(R.id.card_name);
        amount  = findViewById(R.id.amount);
        description = findViewById(R.id.description);
        tv = new CreditCardFormattingTextWatcher(card_number);
        card_number.addTextChangedListener(tv);

        expiryTextWatcher = new CreditCardExpiryTextWatcher(expiry_date);
        expiry_date.addTextChangedListener(expiryTextWatcher);

        spref = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();
        card_view = findViewById(R.id.card_view);
        parent= findViewById(R.id.parent);

        bundle = getIntent().getExtras();

        String desc = bundle.getString("description");
        ref = bundle.getString("ref");
        Double total_amount  = bundle.getDouble("total_amount");
        currency = bundle.getString("currency");
        desc = desc.concat(" Ref# ").concat(ref);
        description.setText(desc);
        amount.setText(currency.concat(" ").concat(String.valueOf(total_amount)));
        setTitle("Card Payment");







    }

    public void pay(View view) {
        if(TextUtils.isEmpty(card_name.getText())){
            card_name.setError("Name is required");
        } else if(TextUtils.isEmpty(card_number.getText())){
            card_number.setError("Card number is required");
        } else if(!CreditCardUtils.isValid(card_number.getText().toString().replaceAll("\\s+",""))){
            card_number.setError("Card number is invalid");
        }  else if(TextUtils.isEmpty(expiry_date.getText())){
            expiry_date.setError("Expiry  date is required");
        } else if(!CreditCardUtils.isValidDate(expiry_date.getText().toString())){
            expiry_date.setError("Expiry date  is invalid");
        } else {

            new Access(CreditCard.this, this, true).authorize(spref.getString("access_token", ""));
            //WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            //String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());


           // Log.e("ip", ip);

        }
    }

    @Override
    public void completed(Object object) {
        final JSONObject sp = ((JSONObject) object).optJSONObject("sp");
        String bilingEmail =  "payments@advancetechnologypartner.com";
        if(!TextUtils.isEmpty(sp.optString("billing_email"))){
            bilingEmail = sp.optString("billing_email");
        }

        String[] expiry =  expiry_date.getText().toString().split("/");
        Calendar c =  Calendar.getInstance();

        String year = String.valueOf(c.get(Calendar.YEAR));
        String exp = expiry[0].concat(year.substring(0,2)).concat(expiry[1]);
        String ip = bundle.getString("ip");

        String __amount = amount.getText().toString().replace(currency, "").trim();
        float d = Float.valueOf(__amount) * 100 ;
        final int amt = (int) d;


        //d =  d  * 100;
        //final int amt = Integer.valueOf(String.valueOf(d));





        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<!DOCTYPE protocol SYSTEM \"https://www.paygate.co.za/payxml/payxml_v4.dtd\">" +
                "<protocol ver=\"4.0\" pgid=\"1019428100016\" pwd=\"AdvancedTech\">" +
                "<authtx " +
                " cref=\""+ref+"\" " +
                " cname=\""+card_name.getText().toString()+"\"" +
                " cc=\""+ card_number.getText().toString().replaceAll("\\s+","")+"\"" +
                " exp=\""+exp+"\"  budp=\"0\" amt=\""+amt+"\"  " +
                " email=\""+bilingEmail+"\" ip=\""+ip+"\"  cur=\""+currency+"\" cvv=\""+ccv.getText().toString()+"\" " +
                " rurl=\"https://atpevs.co.za/evs/api/v1/mobile/notification\"  nurl=\"https://thevaluegateway.com/cc_payments/notify.php\" />" +
                "</protocol>";


        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "text/xml");
        client.setBasicAuth("1019428100016", "AdvancedTech");
        try {
            StringEntity entity = new StringEntity(xml);
            final ProgressDialog progressDialog = TransparentProgressDialog.createProgressDialog(this);

            client.post(this, "https://www.paygate.co.za/payxml/process.trans", entity, "text/xml",
                    new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                            progressDialog.dismiss();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {

                            progressDialog.dismiss();
                            //Log.e("responseString", ""+responseString);

                            //Logging Payment

                            JSONObject params = new JSONObject();
                            try {
                                params.put("sp_id", sp.optInt("id"));
                                params.put("ref", ref);
                                params.put("amount", amount.getText().toString().replace(currency, "").trim());
                                params.put("transaction_details", Base64.getEncoder().encodeToString(xml.getBytes()));
                                params.put("status", "PENDING");
                                params.put("acquirer_response", responseString);
                                params.put("currency", currency);
                                params.put("bill_id", bundle.getInt("bill_id"));

                                new Paygate(CreditCard.this, new OnServiceResponseListener() {
                                    @Override
                                    public void completed(Object object) {

                                    }

                                    @Override
                                    public void failed(ServiceException e) {

                                    }
                                }, false).initiate(params);




                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                            try {

                                DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
                                Document doc = docBuilder.parse(new InputSource(new StringReader(responseString)));
                                NodeList securerx = doc.getElementsByTagName("securerx");
                                NodeList authrx  = doc.getElementsByTagName("authrx");
                                NodeList errorrx =  doc.getElementsByTagName("errorrx");
                                // NodeList authrx  = doc.getElementsByTagName("authrx");

                                // Log.e("securerx", ""+securerx.getLength());
                                //Log.e("authrx", ""+authrx.getLength());
                                //Log Transanction in sp;
                                if(errorrx.getLength() > 0){
                                    Node errorr = errorrx.item(0);
                                    NamedNodeMap attributes = errorr.getAttributes();
                                    String error ="";
                                    for (int a = 0; a < attributes.getLength(); a++) {
                                        Node theAttribute = attributes.item(a);
                                        if(theAttribute.getNodeName().equals("edesc")){
                                            error = theAttribute.getNodeValue();
                                        }
                                    }

                                    Snackbar snackbar = Snackbar.make(parent, error, Snackbar.LENGTH_LONG);

                                    View sbView = snackbar.getView();

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        sbView.setBackground(getDrawable(R.drawable.snackbar_error));
                                    } else {
                                        sbView.setBackground(getResources().getDrawable(R.drawable.snackbar_error));
                                    }

                                    snackbar.show();





                                } else  if(authrx.getLength() > 0) {
                                    Node  authr = authrx.item(0);
                                    NamedNodeMap attributes = authr.getAttributes();

                                    String tid ="";
                                    String sdesc ="";
                                    String res ="";
                                    String rdesc ="";


                                    for (int a = 0; a < attributes.getLength(); a++) {
                                        Node theAttribute = attributes.item(a);
                                        if(theAttribute.getNodeName().equals("tid")){
                                            tid = theAttribute.getNodeValue();
                                        }
                                        if(theAttribute.getNodeName().equals("sdesc")){
                                            sdesc = theAttribute.getNodeValue();
                                        }
                                        if(theAttribute.getNodeName().equals("res")){
                                            res = theAttribute.getNodeValue();
                                        }
                                        if(theAttribute.getNodeName().equals("rdesc")){
                                            rdesc = theAttribute.getNodeValue();
                                        }
                                    }

                                    final boolean approved = rdesc.toLowerCase().contains("approved");
                                    final String title = approved ? "Payment Successful" :"Payment Failed";

                                    HandlePayment handlePayment = new HandlePayment(CreditCard.this, approved,
                                            rdesc,tid, sp.optInt("id"), ref);


                                    handlePayment.complete();
                                    handlePayment.sendnotify();
                                    handlePayment.in_app_notify();

                                    Bundle extras = new Bundle();
                                    extras.putString("title", title);
                                    extras.putString("desc", sdesc);
                                    extras.putString("tid",tid);

                                    Intent intent = new Intent(CreditCard.this, Confirmation.class).putExtras(extras);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                } else  if( securerx.getLength() > 0){
                                    String tid= "";
                                    String chk= "";
                                    String url= "";
                                    Node secure = securerx.item(0);

                                    NamedNodeMap attributes = secure.getAttributes();

                                    for (int a = 0; a < attributes.getLength(); a++) {
                                        Node theAttribute = attributes.item(a);
                                        if(theAttribute.getNodeName().equals("tid")){
                                            tid = theAttribute.getNodeValue();
                                        }
                                        if(theAttribute.getNodeName().equals("chk")){
                                            chk = theAttribute.getNodeValue();
                                        }
                                        if(theAttribute.getNodeName().equals("url")){
                                            url = theAttribute.getNodeValue();
                                        }

                                    }

                                    //  Toast.makeText(CreditCard.this, tid, Toast.LENGTH_LONG).show();

                                    if(!TextUtils.isEmpty(tid) && !TextUtils.isEmpty(chk) &&  !TextUtils.isEmpty(url)){
                                        String payment_url = url+"?PAYGATE_ID=1019428100016&TRANS_ID="+tid+"&CHECKSUM="+chk;
                                        Bundle b = new Bundle();
                                        b.putString("redirect", payment_url);
                                        b.putString("tid", tid);
                                        b.putString("ref", ref);
                                        b.putInt("sp_id", sp.optInt("id"));
                                        startActivity(new Intent(CreditCard.this, SecurePayment.class).putExtras(b));
                                        finish();
                                    }

                                }
                            } catch (ParserConfigurationException e) {
                                e.printStackTrace();
                            } catch (SAXException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }




    }

    @Override
    public void failed(ServiceException e) {

    }
}
