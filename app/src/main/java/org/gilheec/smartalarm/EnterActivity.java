package org.gilheec.smartalarm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class EnterActivity extends AppCompatActivity {

    private static final String ENTER_URL = "http://172.16.1.252:3000/orderno";

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
    public void onSerno(View view)
    {
        // 통신하여 영업점 리스트를 가지고 온다.
        String regId = FirebaseInstanceId.getInstance().getToken();
        Log.i("debug ", "device create " + regId);
        new ConnEnter().execute(ENTER_URL, regId);
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

    // ConnEnter  에 대한 Async 통신 처리
    class ConnEnter extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(EnterActivity.this);
        TextView branchNoText = (TextView)findViewById(R.id.branch_no);
        TextView orderNoText = (TextView)findViewById(R.id.orderno);

        @Override
        protected String doInBackground(String... params) {
            StringBuilder output = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("device_token", params[1]);
                postDataParams.put("branch_no", branchNoText.getText());
                postDataParams.put("order_no", orderNoText.getText());

                /// 입력값 추가 postDataParams....

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true); conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));
                    writer.flush();
                    writer.close();
                    os.close();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while(true) {
                        line = reader.readLine();
                        if (line == null) break;
                        output.append(line);
                    }
                    reader.close();
                    conn.disconnect();
                }
            } catch (Exception e) { e.printStackTrace(); }
            return output.toString();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("순번 등록 처리중..");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                JSONObject json = new JSONObject(s);

                Log.d("debug", json.toString());

                Log.d("debug", "ccccccccccccccccccccc");

                if (json.getBoolean("result") == true) { //통신 성공

                    Log.d("debug", "eeeeeeeeeeeeeeeeeeeee");

                    Intent intent = new Intent(EnterActivity.this, StatusActivity.class);
                    startActivityForResult(intent, 1);
                    finish();

                } else { //통신 실패

                    Log.d("debug", "fffffffffffffffffffffff");

                    Toast.makeText(EnterActivity.this,
                            json.getString("err"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }
}
