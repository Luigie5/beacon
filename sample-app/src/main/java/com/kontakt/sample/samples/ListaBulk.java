package com.kontakt.sample.samples;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kontakt.sample.R;

import java.util.ArrayList;
import java.util.List;

public class ListaBulk extends AppCompatActivity {
    ListView listView ;
    Button editar;
    List rows;
    String res;
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, Lista.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ista_checkboxes);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        editar= (Button) findViewById(R.id.btn);
        // Defined Array values to show in ListView

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        rows= new ArrayList<Row>(30);
        Row row = null;
        for (String str:Support.lista) {
            row = new Row();
            row.setTitle(str);
            rows.add(row);
        }
        //rows.get(0).setChecked(true);
        // Assign adapter to ListView
        final BulkArrayAdapter adapt=new BulkArrayAdapter(this, rows);
        listView.setAdapter(adapt);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Support.bulkList.isEmpty()) {
                    res="";
                    for (int pos : Support.bulkList) {
                        System.out.println(pos + "\n");
                        res+=","+pos;
                    }
                    Support.bulkList.clear();
                    Intent intent= new Intent(ListaBulk.this, BulkEditConfig.class);
                    intent.putExtra("DEVICES", res.substring(1));
                    startActivity(intent);
                }

            }
        });
        setupToolbar();
    }
    private void setupToolbar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        for(String str:res.substring(1).split(",")){
            Support.bulkList.add(Integer.parseInt(str));
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
class BulkArrayAdapter extends ArrayAdapter<Row>
{
    private LayoutInflater layoutInflater;

    public BulkArrayAdapter(Context context, List<Row> rows)
    {
        super(context, 0, rows);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // holder pattern
        Holder holder = null;
        if (convertView == null)
        {
            holder = new Holder();

            convertView = layoutInflater.inflate(R.layout.list_and_checkbox, null);
            holder.setTextViewTitle((TextView) convertView.findViewById(R.id.list_view_item_text));
            holder.setCheckBox((CheckBox) convertView.findViewById(R.id.list_view_item_checkbox));
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        Row row = getItem(position);
        holder.getTextViewTitle().setText(row.getTitle());
        holder.getCheckBox().setTag(position);
        holder.getCheckBox().setChecked(row.isChecked());
        holder.getCheckBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                int position = (Integer) v.getTag();
                getItem(position).setChecked(checkBox.isChecked());
                if(Support.bulkList.indexOf(position)==-1){
                    Support.bulkList.add(position);
                }else{
                    Support.bulkList.remove(Support.bulkList.indexOf(position));
                }
            }
        });
        return convertView;
    }

    static class Holder
    {
        TextView textViewTitle;
        CheckBox checkBox;

        public TextView getTextViewTitle()
        {
            return textViewTitle;
        }

        public void setTextViewTitle(TextView textViewTitle)
        {
            this.textViewTitle = textViewTitle;
        }
        public CheckBox getCheckBox()
        {
            return checkBox;
        }
        public void setCheckBox(final CheckBox checkBox)
        {
            this.checkBox = checkBox;
        }

    }
}