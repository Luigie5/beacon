package com.kontakt.sample.samples;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kontakt.sample.R;
import com.kontakt.sdk.android.ble.configuration.ScanMode;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.connection.ErrorCause;
import com.kontakt.sdk.android.ble.connection.KontaktDeviceConnection;
import com.kontakt.sdk.android.ble.connection.KontaktDeviceConnectionFactory;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.connection.WriteListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.SecureProfileListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleSecureProfileListener;
import com.kontakt.sdk.android.cloud.KontaktCloud;
import com.kontakt.sdk.android.cloud.KontaktCloudFactory;
import com.kontakt.sdk.android.cloud.response.CloudCallback;
import com.kontakt.sdk.android.cloud.response.CloudError;
import com.kontakt.sdk.android.cloud.response.CloudHeaders;
import com.kontakt.sdk.android.cloud.response.paginated.Configs;
import com.kontakt.sdk.android.cloud.response.paginated.Devices;
import com.kontakt.sdk.android.common.model.Config;
import com.kontakt.sdk.android.common.model.Device;
import com.kontakt.sdk.android.common.model.DeviceType;
import com.kontakt.sdk.android.common.model.PacketType;
import com.kontakt.sdk.android.common.model.PowerSaving;
import com.kontakt.sdk.android.common.model.PowerSavingFeature;
import com.kontakt.sdk.android.common.profile.DeviceProfile;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.ISecureProfile;
import com.kontakt.sdk.android.common.profile.RemoteBluetoothDevice;
import com.kontakt.sdk.android.common.util.SecureProfileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;

/**
 * This is an example of changing beacon's major and minor
 */
public class BeaconConfigurationActivity extends AppCompatActivity implements View.OnClickListener {

  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, BeaconConfigurationActivity.class);
  }

  public static final String TAG = "ProximityManager";
  private static final int TX_POWER_MIN_VALUE = 0;
  private static final int INTERVAL_MAX_VALUE = 10240;
  private static final int TX_POWER_MAX_VALUE = 7;
  private static final int INTERVAL_MIN_VALUE = 100;

  private final KontaktCloud kontaktCloud = KontaktCloudFactory.create();
  private ProximityManager proximityManager;
  private KontaktDeviceConnection deviceConnection;

  private TextView statusText;
  private EditText powerInput;
  private EditText intervalInput;
  private Button startButton;
  private String targetUniqueId;
  private Config targetConfiguration;
  private RemoteBluetoothDevice targetDevice;
  private List<String> ids;
  MiHilo x=new MiHilo();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_beacon_config);

    statusText = (TextView) findViewById(R.id.status_text);
    powerInput = (EditText) findViewById(R.id.power_edit);
    intervalInput = (EditText) findViewById(R.id.interval_edit);
    fetchDevices();

    setupToolbar();
    setupButtons();
    setupProximityManager();
  }

  @Override
  protected void onStop() {
    if (proximityManager != null) {
      proximityManager.disconnect();
    }
    if (deviceConnection != null) {
      deviceConnection.close();
    }
    super.onStop();
  }

  private void setupToolbar() {
    ActionBar supportActionBar = getSupportActionBar();
    if (supportActionBar != null) {
      supportActionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  private void setupButtons() {
    startButton = (Button) findViewById(R.id.start_button);
    startButton.setOnClickListener(this);
  }

  private void setupProximityManager() {
    proximityManager = ProximityManagerFactory.create(this);

    //Configure proximity manager basic options
    proximityManager.configuration()
            //Using ranging for continuous scanning or MONITORING for scanning with intervals
            .scanPeriod(ScanPeriod.RANGING)
            //Using BALANCED for best performance/battery ratio
            .scanMode(ScanMode.BALANCED)
            //OnDeviceUpdate callback will be received with 5 seconds interval
            .deviceUpdateCallbackInterval(TimeUnit.SECONDS.toMillis(5));

    //Setting up iBeacon and Secure Profile listeners
    proximityManager.setIBeaconListener(createIBeaconListener());
    proximityManager.setSecureProfileListener(createSecureProfileListener());
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
          }
          ids=new ArrayList<>();

          for (String e:Support.entrada) {
            ids.add(Support.mapa.get(e));
          }
          String res="";
          for (String id:ids
          ) {
            res=res+"\n"+id;
          }
          setStatus(res);
        }
      }

      @Override
      public void onError(CloudError error) {

      }
    });
  }
  private void startConfiguration() {
    //First validate user's input.
    if (!areInputsValid()) {
      showError("At least one of inserted values is invalid.");
      return;
    }
    //If everything is OK start the scanning.
    x.start();
  }

  private boolean areInputsValid() {
    String powerText = powerInput.getText().toString();
    String intervalText = intervalInput.getText().toString();
    if (TextUtils.isEmpty(powerText) || TextUtils.isEmpty(intervalText)) {
      return false;
    }

    int power = Integer.parseInt(powerText);
    int interval = Integer.parseInt(intervalText);
    if (interval < INTERVAL_MIN_VALUE || interval > INTERVAL_MAX_VALUE || power < TX_POWER_MIN_VALUE || power > TX_POWER_MAX_VALUE) {
      return false;
    }

    return true;
  }

  private void scanForDevice(String uniqueId) {
    targetUniqueId = uniqueId;
    proximityManager.connect(new OnServiceReadyListener() {
      @Override
      public void onServiceReady() {
        proximityManager.startScanning();
        //setStatus("Looking for device...");
      }
    });
    startButton.setEnabled(false);
  }

  private void onDeviceDiscovered(RemoteBluetoothDevice device) {
    //Check if this is our target beacon.
    if (targetUniqueId.equalsIgnoreCase(device.getUniqueId())) {
      proximityManager.disconnect();
      targetDevice = device;
      prepareConfiguration();

      String status = "Device discovered! Unique ID: " + device.getUniqueId();
      setStatus(status);
      Log.i(TAG, status);
    }
  }

  private void prepareConfiguration() {
    //Prepare configuration
    setStatus("Preparing configuration...");
    /*
    Perfiles de ibeacon y eddystone
    List<DeviceProfile> x=new ArrayList<>();
    x.add(DeviceProfile.EDDYSTONE);
    x.add(DeviceProfile.IBEACON);
    List<PacketType> y=new ArrayList<>();
    y.add(PacketType.EDDYSTONE_UID);
    y.add(PacketType.IBEACON);
    y.add(PacketType.KONTAKT);
    Config config = new Config.Builder().powerSaving(new PowerSaving())
            .build();*/
    /* Ahorro de energía
    PowerSaving.Builder xx=new PowerSaving.Builder();
    List<PowerSavingFeature> x=new ArrayList<>();
    x.add(PowerSavingFeature.LIGHT_SENSOR);
    PowerSaving xs= xx.features(x).build();
      Config config = new Config.Builder().powerSaving(xs)
              .build();*/
    Config config = new Config.Builder().txPower(Integer.parseInt(powerInput.getText().toString())).build();
    new Config.Builder().interval(Integer.parseInt(intervalInput.getText().toString()))
            .build();
    //Use KontaktCloud to create config and request encrypted version that will be send to the device.
    kontaktCloud.configs().create(config).forDevices(targetDevice.getUniqueId()).withType(DeviceType.BEACON).execute(new CloudCallback<Config[]>() {
      @Override
      public void onSuccess(Config[] response, CloudHeaders headers) {
        //Config has been successfully created. Now download encrypted version.
        kontaktCloud.configs().secure().withIds(targetDevice.getUniqueId()).execute(new CloudCallback<Configs>() {
          @Override
          public void onSuccess(Configs response, CloudHeaders headers) {
            setStatus("Fetching encrypted configuration...");
            targetConfiguration = response.getContent().get(0);
            onConfigurationReady();
          }

          @Override
          public void onError(CloudError error) {
            showError("Error: " + error.getMessage());
          }
        });
      }

      @Override
      public void onError(CloudError error) {
        showError("Error: " + error.getMessage());
      }
    });
  }

  private void onConfigurationReady() {
    //Initialize connection to the device
    deviceConnection = KontaktDeviceConnectionFactory.create(this, targetDevice, createConnectionListener());
    deviceConnection.connect();
    setStatus("Connecting to device...");
  }

  private void onDeviceConnected() {
    //Device connected. Start configuration...
    setStatus("Applying configuration...");
    deviceConnection.applySecureConfig(targetConfiguration.getSecureRequest(), new WriteListener() {
      @Override
      public void onWriteSuccess(WriteResponse response) {
        //Configuration has been applied. Now we need to send beacon's response back to the cloud to stay synchronized.
        setStatus("Configuration applied in the device.");
        onConfigurationApplied(response);
        deviceConnection.close();
      }

      @Override
      public void onWriteFailure(ErrorCause cause) {
        showError("Configuration error. Cause: " + cause);
        deviceConnection.close();
      }
    });
  }

  private void onConfigurationApplied(WriteListener.WriteResponse response) {
    //Configuration has been applied on the beacon. Now we should inform Cloud about it.
    setStatus("Synchronizing with Cloud...");
    targetConfiguration.applySecureResponse(response.getExtra(), response.getUnixTimestamp());
    kontaktCloud.devices().applySecureConfigs(targetConfiguration).execute(new CloudCallback<Configs>() {
      @Override
      public void onSuccess(Configs response, CloudHeaders headers) {
        //Success!
        setStatus("Configuration completed!");
        startButton.setEnabled(true);
      }

      @Override
      public void onError(CloudError error) {
        showError("Error: " + error.getMessage());
      }
    });
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.start_button:
        startConfiguration();
        break;
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

  private void showError(final String errorMessage) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(BeaconConfigurationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        setStatus(errorMessage);
        startButton.setEnabled(true);
      }
    });
  }

  private void setStatus(final String text) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        statusText.setText(text);
      }
    });
  }

  private IBeaconListener createIBeaconListener() {
    return new SimpleIBeaconListener() {
      @Override
      public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
        onDeviceDiscovered(ibeacon);
      }
    };
  }

  private SecureProfileListener createSecureProfileListener() {
    return new SimpleSecureProfileListener() {
      @Override
      public void onProfileDiscovered(ISecureProfile profile) {
        onDeviceDiscovered(SecureProfileUtils.asRemoteBluetoothDevice(profile));
      }
    };
  }

  private KontaktDeviceConnection.ConnectionListener createConnectionListener() {
    return new KontaktDeviceConnection.ConnectionListener() {
      @Override
      public void onConnectionOpened() {

      }

      @Override
      public void onAuthenticationSuccess(RemoteBluetoothDevice.Characteristics characteristics) {
        onDeviceConnected();
      }

      @Override
      public void onAuthenticationFailure(int failureCode) {
      }

      @Override
      public void onCharacteristicsUpdated(RemoteBluetoothDevice.Characteristics characteristics) {
      }

      @Override
      public void onErrorOccured(int errorCode) {
        showError("Connection error. Code: " + errorCode);
      }

      @Override
      public void onDisconnected() {
        showError("Device disconnected.");
      }
    };
  }
  public class MiHilo extends Thread
  {
    public void run()
    {
      setStatus("hola");
      for (final String e: ids) {
        runOnUiThread(new Runnable() {

          @Override
          public void run() {
            scanForDevice(e);
          }
        });
        try {
          System.out.println(e);
          this.sleep(15000);
        }catch (Exception i){}
      }
    }
  }

}
