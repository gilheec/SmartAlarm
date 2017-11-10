package org.gilheec.smartalarm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    private static final String CHECK_URL = "http://172.16.1.252:3000/orderno/check";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("debug", "yyyyyyyyyyyyyyyyyyyyyyyy");

        // 통신하여 대기상황을 가지고 온다
        String regId = FirebaseInstanceId.getInstance().getToken();
        Log.i("debug", "device create " + regId);
        new ConnCheck().execute(CHECK_URL, regId);
    }

    // ConnStatus  에 대한 Async 통신 처리
    class ConnCheck extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected String doInBackground(String... params) {
            StringBuilder output = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("device_token", params[1]);

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
            dialog.setMessage("대기순번 등록여부 확인중..");
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

                    Log.d("debug", "bbbbbb");

                    int cnt = json.getInt("cnt");

                    if (cnt > 0) {
                        Intent intent = new Intent(MainActivity.this, StatusActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, EnterActivity.class);
                        startActivity(intent);
                    }

                } else { //통신 실패

                    Log.d("debug", "fffffffffffffffffffffff");

                    Toast.makeText(MainActivity.this,
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

    public void onEnterScr(View view)
    {
        Intent intent = new Intent(MainActivity.this, EnterActivity.class);
        startActivity(intent);
    }
    public void onStatusScr(View view)
    {
        Intent intent = new Intent(MainActivity.this, StatusActivity.class);
        startActivity(intent);
    }

}


