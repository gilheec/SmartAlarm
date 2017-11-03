package org.gilheec.smartalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class EnterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
    }
    public void onBrlistScr(View view)
    {
        Intent intent = new Intent(EnterActivity.this, BrlistActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK){
                    String branchNo = data.getStringExtra("branch_no");
                    String branchName = data.getStringExtra("branch_name");
                    String address = data.getStringExtra("address");

                    TextView branchNoText = (TextView)findViewById(R.id.branch_no);
                    TextView branchNameText = (TextView)findViewById(R.id.branch_name);
                    TextView addressText = (TextView)findViewById(R.id.address);

                    branchNoText.setText(branchNo);
                    branchNameText.setText(branchName);
                    addressText.setText(address);
                }
                break;
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }
}
