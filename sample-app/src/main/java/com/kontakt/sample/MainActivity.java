package com.kontakt.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kontakt.sample.samples.BackgroundScanActivity;
import com.kontakt.sample.samples.BeaconConfigurationActivity;
import com.kontakt.sample.samples.BeaconProScanActivity;
import com.kontakt.sample.samples.BeaconProSensorsActivity;
import com.kontakt.sample.samples.ForegroundScanActivity;
import com.kontakt.sample.samples.KontaktCloudActivity;
import com.kontakt.sample.samples.ScanFiltersActivity;
import com.kontakt.sample.samples.ScanRegionsActivity;
import com.kontakt.sample.samples.Lista;
import com.kontakt.sample.samples.Support;
import com.kontakt.sdk.android.cloud.KontaktCloud;
import com.kontakt.sdk.android.cloud.KontaktCloudFactory;
import com.kontakt.sdk.android.cloud.response.CloudCallback;
import com.kontakt.sdk.android.cloud.response.CloudError;
import com.kontakt.sdk.android.cloud.response.CloudHeaders;
import com.kontakt.sdk.android.cloud.response.paginated.Devices;
import com.kontakt.sdk.android.common.model.Device;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  public static final int REQUEST_CODE_PERMISSIONS = 100;

  private LinearLayout buttonsLayout;
  private final KontaktCloud kontaktCloud = KontaktCloudFactory.create();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupButtons();
    checkPermissions();
    fetchDevices();
  }
  private void fetchDevices() {
    //Request list of all devices. Max results are set to 50 by default. If there are more than 50 devices on your account results will be paginated.
    kontaktCloud.devices().fetch().execute(new CloudCallback<Devices>() {
      @Override
      public void onSuccess(Devices response, CloudHeaders headers) {
        if (response != null && response.getContent() != null) {
          //Do something with your devices list
          for (Device device : response.getContent()) {
            Support.mapa.put(device.getSecureProximity().toString(),device.getUniqueId());
            Support.lista.add(device.getUniqueId()+" - "+device.getSecureProximity().toString().substring(33));
            Support.devices.add(device);
          }
          Support.ordenar();
        }
      }

      @Override
      public void onError(CloudError error) {
      }
    });
  }
  //Setting up buttons and listeners.
  private void setupButtons() {
    buttonsLayout = findViewById(R.id.buttons_layout);

    final Button beaconsScanningButton = findViewById(R.id.button_scan_beacons);
    final Button beaconsProScanningButton = findViewById(R.id.button_scan_beacons_pro);
    final Button scanRegionsButton = findViewById(R.id.button_scan_regions);
    final Button scanFiltersButton = findViewById(R.id.button_scan_filters);
    final Button backgroundScanButton = findViewById(R.id.button_scan_background);
    final Button foregroundScanButton = findViewById(R.id.button_scan_foreground);
    final Button configurationButton = findViewById(R.id.button_beacon_config);
    final Button beaconProSensorsButton = findViewById(R.id.button_beacon_pro_sensors);
    final Button kontaktCloudButton = findViewById(R.id.button_kontakt_cloud);

    beaconsScanningButton.setOnClickListener(this);
    beaconsProScanningButton.setOnClickListener(this);
    scanRegionsButton.setOnClickListener(this);
    scanFiltersButton.setOnClickListener(this);
    backgroundScanButton.setOnClickListener(this);
    foregroundScanButton.setOnClickListener(this);
    configurationButton.setOnClickListener(this);
    beaconProSensorsButton.setOnClickListener(this);
    kontaktCloudButton.setOnClickListener(this);
  }

  //Since Android Marshmallow starting a Bluetooth Low Energy scan requires permission from location group.
  private void checkPermissions() {
    int checkSelfPermissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    if (PackageManager.PERMISSION_GRANTED != checkSelfPermissionResult) {
      //Permission not granted so we ask for it. Results are handled in onRequestPermissionsResult() callback.
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      if (REQUEST_CODE_PERMISSIONS == requestCode) {
        Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
      }
    } else {
      disableButtons();
      Toast.makeText(this, "Location permissions are mandatory to use BLE features on Android 6.0 or higher", Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.button_scan_beacons:
        startActivity(new Intent(MainActivity.this, Lista.class));
        break;
      case R.id.button_scan_beacons_pro:
        startActivity(BeaconProScanActivity.createIntent(this));
        break;
      case R.id.button_scan_filters:
        startActivity(ScanFiltersActivity.createIntent(this));
        break;
      case R.id.button_scan_regions:
        startActivity(ScanRegionsActivity.createIntent(this));
        break;
      case R.id.button_scan_background:
        startActivity(BackgroundScanActivity.createIntent(this));
        break;
      case R.id.button_scan_foreground:
        startActivity(ForegroundScanActivity.createIntent(this));
        break;
      case R.id.button_beacon_config:
        startActivity(BeaconConfigurationActivity.createIntent(this));
        break;
      case R.id.button_beacon_pro_sensors:
        startActivity(BeaconProSensorsActivity.createIntent(this));
        break;
      case R.id.button_kontakt_cloud:
        startActivity(KontaktCloudActivity.createIntent(this));
        break;
    }
  }

  private void disableButtons() {
    for (int i = 0; i < buttonsLayout.getChildCount(); i++) {
      buttonsLayout.getChildAt(i).setEnabled(false);
    }
  }

}
