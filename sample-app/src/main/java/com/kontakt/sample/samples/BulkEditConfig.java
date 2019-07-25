package com.kontakt.sample.samples;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kontakt.sample.MainActivity;
import com.kontakt.sample.R;
import com.kontakt.sdk.android.common.model.Device;

import java.util.ArrayList;
import java.util.List;

public class BulkEditConfig extends AppCompatActivity implements View.OnClickListener{
    private TextView statusText;
    private EditText intervalText;
    private EditText powerText;
    private EditText minorText;
    private EditText majorText;
    private EditText brilloText;
    Switch brilloSwitch;
    Switch movimientoSwitch;
    List<Device> device= new ArrayList<>();
    Button aplicarButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_edit);
        String[] lista=getIntent().getStringExtra("DEVICES").split(",");
        for (String i : lista){
            device.add(Support.devices.get(Integer.parseInt(i)));
        }
        aplicarButton = (Button) findViewById(R.id.aplicar_button);
        intervalText = (EditText) findViewById(R.id.interval_editText);
        powerText = (EditText) findViewById(R.id.power_editText);
        majorText = (EditText) findViewById(R.id.major_editText);
        minorText = (EditText) findViewById(R.id.minor_editText);
        brilloText = (EditText) findViewById(R.id.brillo_editText);
        brilloSwitch=(Switch) findViewById(R.id.brillo_switch);
        movimientoSwitch=(Switch) findViewById(R.id.movimiento_switch);
        aplicarButton.setOnClickListener(this);
        statusText = (TextView) findViewById(R.id.text);

        setupToolbar();
    }
    private void setupToolbar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        //supportActionBar.setIcon();
    }
    private void setStatus(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(text);
            }
        });
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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.aplicar_button:
                String res="";
                boolean[] aplicarConfig=new boolean[6];
                if(intervalText.length()==0 && powerText.length()==0 && minorText.length()==0 && majorText.length()==0 && !brilloSwitch.isChecked() && !movimientoSwitch.isChecked()){
                    setStatus("Todos los campos no pueden estar vacíos");
                }
                if(intervalText.length()!=0 && (Integer.parseInt(intervalText.getText().toString())<100 || Integer.parseInt(intervalText.getText().toString())>10240)){
                    res+="El intervalo debe de estar comprendido entre 100 y 10240.\n";
                }else{
                    aplicarConfig[0]=true;
                }
                if(powerText.length()!=0 &&(Integer.parseInt(powerText.getText().toString())<1 || Integer.parseInt(powerText.getText().toString())>7)){
                    res+="La potencia debe de estar comprendida entre 1 y 7.\n";
                }else{
                    aplicarConfig[1]=true;
                }
                if(minorText.length()!=0 &&(Integer.parseInt(minorText.getText().toString())<0 || Integer.parseInt(minorText.getText().toString())>65535)){
                    res+="Minor debe de hallarse entre 0 y 65535.\n";
                }else{
                    aplicarConfig[2]=true;
                }
                if(majorText.length()!=0 &&(Integer.parseInt(majorText.getText().toString())<0 || Integer.parseInt(majorText.getText().toString())>65535)){
                    res+="Major debe de hallarse entre 0 y 65535.\n";
                }else{
                    aplicarConfig[3]=true;
                }
                if(brilloSwitch.isChecked() && brilloText.length()==0){
                    res+="Los valores de brillo deben de hallarse entre 0 y 90.\n";
                }else if(brilloSwitch.isChecked() && (brilloText.length()!=0 &&(Integer.parseInt(brilloText.getText().toString())<0 || Integer.parseInt(brilloText.getText().toString())>90))){
                    res+="Los valores de brillo deben de hallarse entre 0 y 90.\n";
                }else{
                    aplicarConfig[4]=true;
                }
                if(brilloSwitch.isChecked() && movimientoSwitch.isChecked()){
                    res+="No pueden estar activos ambos metodos de guardado de batería.\n";
                }else{
                    if(!aplicarConfig[4]) {
                        aplicarConfig[5] = movimientoSwitch.isChecked();
                    }
                }
                setStatus(res);

                break;
        }
    }
}
