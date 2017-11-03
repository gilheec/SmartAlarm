package org.gilheec.smartalarm;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static org.gilheec.smartalarm.R.id.status_ctnt;

public class StatusActivity extends AppCompatActivity {
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
            text1View.setText(item.status_ctnt);

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

        itemList.add(new Item("0012 - 호출 (대기시간 25분)"));
        itemList.add(new Item("0013 - 호출 (대기시간 24분)"));
        itemList.add(new Item("0014 - 대기 (대기시간 23분)"));
        itemList.add(new Item("0015 - 대기 (대기시간 20분)"));
        itemList.add(new Item("0016 - 대기 (대기시간 18분)"));

        itemAdpater = new ItemAdapter(StatusActivity.this, R.layout.list_item, itemList);
        listView.setAdapter(itemAdpater);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Item item = itemList.get(position);

                // item.getStatus_ctnt() 사용...
            }
        });
    }

    public void onBtnCancel(View view) {
        ;
    }

    public void onBtnCall(View view) {
        ;
    }
}
