package org.gilheec.smartalarm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.gilheec.smartalarm.R.id.status_ctnt;

public class StatusActivity extends AppCompatActivity {

    private static final String STATUS_URL = "http://172.16.1.252:3000/orderno/status";
    private static final String CANCEL_URL = "http://172.16.1.252:3000/orderno";

    class Item {
        String status_ctnt;

        Item(String status_ctnt) {
            this.status_ctnt = status_ctnt;
        }
        public String getStatus_ctnt() {
            return status_ctnt;
        }
    }

    ArrayList<Item> itemList = new ArrayList<Item>();
    class ItemAdapter extends ArrayAdapter {
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_status, null);
            }

            TextView text1View = (TextView)convertView.findViewById(status_ctnt);
            Item item = itemList.get(position);
            if (position == itemList.size()-1) {
                text1View.setText(Html.fromHtml("<font color='red'><b>"+item.status_ctnt+"</b></font>"));
            } else {
                text1View.setText(item.status_ctnt);
            }

            return convertView;
        }

        public ItemAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
            super(context, resource, objects);
        }
    }

    ItemAdapter itemAdpater = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        ListView listView = (ListView)findViewById(R.id.listview);

        /* 통신로직 쪽으로 이동        */
        /*
        itemList.add(new Item("0012 - 호출 (대기시간 25분)"));
        itemList.add(new Item("0013 - 호출 (대기시간 24분)"));
        itemList.add(new Item("0014 - 대기 (대기시간 23분)"));
        itemList.add(new Item("0015 - 대기 (대기시간 20분)"));
        itemList.add(new Item("0016 - 대기 (대기시간 18분)"));
        */

        itemAdpater = new ItemAdapter(StatusActivity.this, R.layout.list_item, itemList);
        listView.setAdapter(itemAdpater);

        Log.d("debug", "uuuuuuuuuuuuuuuu");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Item item = itemList.get(position);

                // item.getStatus_ctnt() 사용...
            }
        });

        Log.d("debug", "yyyyyyyyyyyyyyyyyyyyyyyy");

        // 통신하여 대기상황을 가지고 온다
        String regId = FirebaseInstanceId.getInstance().getToken();
        Log.i("debug", "device create " + regId);
        new ConnStatus().execute(STATUS_URL, regId);
    }

    // ConnStatus  에 대한 Async 통신 처리
    class ConnStatus extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(StatusActivity.this);

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
            dialog.setMessage("순번 대기상황 조회중..");
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

                    itemList.clear();

                    JSONArray ny_array = json.getJSONArray("ny");

                    for (int ix = 0; ix < ny_array.length(); ix++)
                    {
                        JSONObject obj = ny_array.getJSONObject(ix);

                        Log.d("debug", "iiiiiiiiiiiiiiiiiiiiiiiiiiiii");

                        int order_no = obj.getInt("order_no");
                        int wait_status_cd = obj.getInt("wait_status_cd");
                        String dr_time = obj.getString("dr_time");
                        String wait_mm = obj.getString("wait_mm");
                        String call_time = obj.getString("call_time");

                        String status_ctnt = "";

                        Log.d("debug", "iiiiiiiiiiiiiiiiiiiiiiiiiiiii" + order_no + "|" + wait_status_cd + "|" + dr_time + "|" + wait_mm);

                        if (wait_status_cd == 1) {
                            // "0013 - 대기중 (대기시간 25분)";
                            status_ctnt = order_no + " -  대기중 (대기시간 " + wait_mm + "분)";
                        } else {
                            // "0012 - 호출   (호출시간 00:00:00)";
                            status_ctnt = order_no + " -  호출 (호출시간 " + call_time + ")";
                        }

                        itemList.add(new Item(status_ctnt));
                    }

                    itemAdpater.notifyDataSetChanged();

                } else { //통신 실패

                    Log.d("debug", "fffffffffffffffffffffff");

                    Toast.makeText(StatusActivity.this,
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

    public void onBtnReflesh(View view) {
        Log.d("debug", "eeeeeeeeeeeeeeeeeeeeeeee");

        // 통신하여 대기상황을 가지고 온다
        String regId = FirebaseInstanceId.getInstance().getToken();
        Log.i("debug", "device create " + regId);
        new ConnStatus().execute(STATUS_URL, regId);
    }

    public void onBtnCancel(View view) {
        Log.d("debug", "eeeeeeeeeeeeeeeeeeeeeeee");

        // 취소처리한다
        String regId = FirebaseInstanceId.getInstance().getToken();
        Log.i("debug", "device create " + regId);
        new ConnCancel().execute(CANCEL_URL, regId);
    }

    // ConnEnter  에 대한 Async 통신 처리
    class ConnCancel extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(StatusActivity.this);

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
                    conn.setRequestMethod("DELETE");
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
            dialog.setMessage("순번 대기 취소처리중..");
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
                    Intent intent = new Intent(StatusActivity.this, EnterActivity.class);
                    startActivity(intent);

                    finish();

                } else { //통신 실패

                    Log.d("debug", "fffffffffffffffffffffff");

                    Toast.makeText(StatusActivity.this,
                            json.getString("err"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

}
