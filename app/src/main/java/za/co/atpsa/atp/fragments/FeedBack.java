package za.co.atpsa.atp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import za.co.atpsa.atp.R;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Notification;

public class FeedBack extends Fragment implements View.OnClickListener, OnServiceResponseListener {
    TextView user_name;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    private Spinner topic;
    private EditText subject,message;
    private Button send_feedback;
    private LinearLayout parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_back,
                container, false);
        user_name = view.findViewById(R.id.user_name);
        spref = getActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        topic = view.findViewById(R.id.topic);
        user_name.setText(spref.getString("username",""));
        subject = view.findViewById(R.id.subject);
        message = view.findViewById(R.id.message);
        send_feedback = view.findViewById(R.id.send_feedback);
        parent = view.findViewById(R.id.parent);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.feedback_cat));
        topic.setAdapter(adapter);
        send_feedback.setOnClickListener(this);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(TextUtils.isEmpty(subject.getText())){
            subject.setError("Subject Required");
            subject.requestFocus();
        } else if (TextUtils.isEmpty(message.getText())){
            message.setError("Message Required");
            message.requestFocus();

        } else {
            JSONObject object = new JSONObject();
            try {
                object.put("access_token", spref.getString("access_token",""));
                object.put("topic", topic.getSelectedItem().toString());
                object.put("subject", subject.getText());
                object.put("message", message.getText());

                new Notification(getActivity(), this, true).message(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void completed(Object object) {
        JSONObject response = (JSONObject) object;
        if(!response.optBoolean("success")){
            Snackbar snackbar = Snackbar.make(parent,"Something went wrong. Please try again later", Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
            } else {
                sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
            }

            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar.make(parent,"Feedback was sent", Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_success));
            } else {
                sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_success));
            }



            snackbar.show();
            topic.setSelection(0, true);
            subject.setText("");
            message.setText("");

        }

    }

    @Override
    public void failed(ServiceException e) {
        Snackbar snackbar = Snackbar.make(parent,"Something went wrong. Please try again later", Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
        } else {
            sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
        }

        snackbar.show();
    }
}
