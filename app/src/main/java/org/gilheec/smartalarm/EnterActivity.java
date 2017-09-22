package org.gilheec.smartalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class EnterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
    }
    public void onBrlistScr(View view)
    {
        Intent intent = new Intent(EnterActivity.this, BrlistActivity.class);
        startActivity(intent);
    }
}
