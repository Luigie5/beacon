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

public class Beacon extends AppCompatActivity implements View.OnClickListener{
    private TextView statusText;
    private EditText intervalText;
    private EditText powerText;
    private EditText minorText;
    private EditText majorText;
    private EditText brilloText;
    Switch brilloSwitch;
    Switch movimientoSwitch;
    Device device;
    Button aplicarButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_info);
        device= Support.devices.get(Integer.parseInt(getIntent().getStringExtra("DEVICE")));
        System.out.println(device.getUniqueId());
        statusText = (TextView) findViewById(R.id.text);

        //setStatus(device.toString());
        setupToolbar();
        intervalText = (EditText) findViewById(R.id.interval_editText);
        powerText = (EditText) findViewById(R.id.power_editText);
        majorText = (EditText) findViewById(R.id.major_editText);
        minorText = (EditText) findViewById(R.id.minor_editText);
        brilloText = (EditText) findViewById(R.id.brillo_editText);
        aplicarButton = (Button) findViewById(R.id.aplicar_button);
        brilloSwitch=(Switch) findViewById(R.id.brillo_switch);
        movimientoSwitch=(Switch) findViewById(R.id.movimiento_switch);
        aplicarButton.setOnClickListener(this);
        setInfo();
    }
    private void setupToolbar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        supportActionBar.setTitle(device.getUniqueId());
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
    private void setInfo() {
        intervalText.setText(device.getConfig().getInterval()+"", TextView.BufferType.EDITABLE);
        powerText.setText(device.getConfig().getTxPower()+"", TextView.BufferType.EDITABLE);
        majorText.setText(device.getConfig().getMajor()+"", TextView.BufferType.EDITABLE);
        minorText.setText(device.getConfig().getMinor()+"", TextView.BufferType.EDITABLE);
        brilloText.setText(device.getConfig().getPowerSaving().getLightSensorThreshold()+"", TextView.BufferType.EDITABLE);
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
                boolean aplicarConfig=true;
                if(Integer.parseInt(intervalText.getText().toString())<100 || Integer.parseInt(intervalText.getText().toString())>10240){
                    res+="El intervalo debe de estar comprendido entre 100 y 10240.\n";
                    aplicarConfig=false;
                }
                if(Integer.parseInt(powerText.getText().toString())<1 || Integer.parseInt(powerText.getText().toString())>7){
                    res+="La potencia debe de estar comprendida entre 1 y 7.\n";
                    aplicarConfig=false;
                }
                if(Integer.parseInt(minorText.getText().toString())<0 || Integer.parseInt(minorText.getText().toString())>65535){
                    res+="Minor debe de hallarse entre 0 y 65535.\n";
                    aplicarConfig=false;
                }
                if(Integer.parseInt(majorText.getText().toString())<0 || Integer.parseInt(majorText.getText().toString())>65535){
                    res+="Major debe de hallarse entre 0 y 65535.\n";
                    aplicarConfig=false;
                }
                if(brilloSwitch.isChecked() && (Integer.parseInt(brilloText.getText().toString())<0 || Integer.parseInt(brilloText.getText().toString())>90)){
                    res+="Los valores de brillo deben de hallarse entre 0 y 90.\n";
                    aplicarConfig=false;
                }
                if(brilloSwitch.isChecked() && movimientoSwitch.isChecked()){
                    res+="No pueden estar activos ambos metodos de guardado de batería.\n";
                    aplicarConfig=false;
                }
                if(aplicarConfig){

                }
                setStatus(res);
                break;
        }
    }
}
