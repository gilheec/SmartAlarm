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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BrlistActivity extends AppCompatActivity {
    // ListView listView = null;

    private static final String BRLIST_URL = "http://172.16.1.252:3000/branch/list";

    class Item {
        String branch_name;
        String address;
        String position;
        String branch_no;

        Item(String branch_name, String address, String position, String branch_no) {
            this.branch_name = branch_name;
            this.address = address;
            this.position = position;
            this.branch_no = branch_no;
        }

        public String getAddress() {
            return address;
        }
        public String getBranch_name() {
            return branch_name;
        }
        public String getPosition() {
            return position;
        }
        public String getBranch_no() {
            return branch_no;
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
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }

            TextView text1View = (TextView)convertView.findViewById(R.id.branch_name);
            TextView text2View = (TextView)convertView.findViewById(R.id.address);
            TextView text3View = (TextView)convertView.findViewById(R.id.position);
            TextView text4View = (TextView)convertView.findViewById(R.id.branch_no);

            Item item = itemList.get(position);

            text1View.setText(item.branch_name);
            text2View.setText(item.address);
            text3View.setText(item.position);
            text4View.setText(item.branch_no);

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
        setContentView(R.layout.activity_brlist);

        ListView listView = (ListView)findViewById(R.id.listview);

        /* 통신로직 쪽으로 이동        */
        /*
        itemList.add(new Item("광교영업부", "서울 중구 청계천로 54", "12m", "3001"));
        itemList.add(new Item("서울롯데", "서울특별시 중구 을지로 30, (롯데호텔)", "272m", "3002"));
        itemList.add(new Item("종각역", "서울특별시 종로구 종로 33", "301m", "3003"));
        itemList.add(new Item("파이낸스센터", "서울특별시 중구 세종대로 136, (파이낸스빌딩)", "312m", "3004"));
        itemList.add(new Item("광화문", "서울특별시 중구 세종대로 135-5", "452m", "3005"));
        */

        itemAdpater = new ItemAdapter(BrlistActivity.this, R.layout.list_item, itemList);
        listView.setAdapter(itemAdpater);

        Log.d("debug", "aaaaaaaaaaaaaaaaa");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Item item = itemList.get(position);

                Intent intent = new Intent();
                intent.putExtra("branch_name", item.getBranch_name());
                intent.putExtra("address", item.getAddress());
                intent.putExtra("position", item.getPosition());
                intent.putExtra("branch_no", item.getBranch_no());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // 통신하여 영업점 리스트를 가지고 온다.
        new ConnBrlist().execute(BRLIST_URL);
    }

    // ConnBrlist  에 대한 Async 통신 처리
    class ConnBrlist extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(BrlistActivity.this);
        @Override
        protected String doInBackground(String... params) {
            StringBuilder output = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    //conn.setDoInput(true); conn.setDoOutput(true);
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
            dialog.setMessage("가까운 영업점 확인중..");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                JSONObject json = new JSONObject(s);

                if (json.getBoolean("result") == true) {//통신 성공

                    Log.d("debug", "bbbbbb");
                    JSONArray ny_array = json.getJSONArray("ny");

                    for (int ix = 0; ix < ny_array.length(); ix++)
                    {
                        JSONObject obj = ny_array.getJSONObject(ix);

                        String branch_name = obj.getString("branch_name");
                        String address = obj.getString("address");
                        String position = obj.getString("position_x") + "m";
                        String branch_no = obj.getString("branch_no");

                        itemList.add(new Item(branch_name, address, position, branch_no));
                    }
                    itemAdpater.notifyDataSetChanged();

                } else {//통신 실패

                    Log.d("debug", "ccccccc");
                    Toast.makeText(BrlistActivity.this,
                            json.getString("err"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

}
