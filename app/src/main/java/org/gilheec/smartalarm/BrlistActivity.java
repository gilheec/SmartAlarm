package org.gilheec.smartalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class BrlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brlist);

        ListView listView = (ListView)findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("branch_name", "광교영업부");
                intent.putExtra("address", "서울 중구 청계천로 54");
                intent.putExtra("position", "012m");
                intent.putExtra("branch_no", "3011");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void onBrList01 (View view)
    {
        Intent intent = new Intent();
        intent.putExtra("branch_name", "광교영업부");
        intent.putExtra("address", "서울 중구 청계천로 54");
        intent.putExtra("position", "012m");
        intent.putExtra("branch_no", "3011");
        setResult(RESULT_OK, intent);
        finish();


        TextView branch_no_1 = (TextView)findViewById(R.id.branch_no_1);


        //TextView popupText = (TextView)findViewById(R.id.popup_text);

        //Intent intent = new Intent(EnterActivity.this, BrlistActivity.class);
        //startActivity(intent);


    }
}
