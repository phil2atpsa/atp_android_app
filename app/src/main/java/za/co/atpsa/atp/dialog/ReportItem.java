package za.co.atpsa.atp.dialog;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.entities.BillReportItem;

public class ReportItem extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_item);
        BillReportItem billReportItem  = (BillReportItem) getIntent().getExtras().getSerializable("billReportItem");

        ( (TextView) findViewById(R.id.title) ).setText("Bill ref#"+billReportItem.getRef());
        ( (TextView) findViewById(R.id.description) ).setText(Html.fromHtml("<p style='text-align:justify'>"+billReportItem.getDescription()+"</p>"));
        ( (TextView) findViewById(R.id.name) ).setText(billReportItem.getName());
        ( (TextView) findViewById(R.id.email) ).setText(billReportItem.getEmail());
        ( (TextView) findViewById(R.id.date) ).setText(billReportItem.getDate());
        ( (TextView) findViewById(R.id.cell_no) ).setText(billReportItem.getCell_no());


        ( (TextView) findViewById(R.id.amount) ).setText("ZAR "+billReportItem.getAmount());

        ImageView status = findViewById(R.id.status);
        if(billReportItem.getStatus().toLowerCase().equals("success")){
            status.setImageResource(R.drawable.ic_check);
        }
        if(billReportItem.getStatus().toLowerCase().equals("failed")){
            status.setImageResource(R.drawable.ic_payment_failed);
        }
        if(billReportItem.getStatus().toLowerCase().equals("pending")){
            status.setImageResource(R.drawable.ic_pending);
        }

        findViewById(R.id.exit).setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
