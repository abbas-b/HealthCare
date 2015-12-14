package com.doubleteam.healthcare.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.Window;

import com.doubleteam.healthcare.R;

/**
 * Created by bsh on 12/14/2015.
 */
public class DeviceListActivity extends Activity {
    /**
     * Tag for Log
     */
    private static final String TAG="DeviceListActivity";
    /**
     * Return Intent extra
     */
    public static String EXTERA_DEVICE_ADDRESS="device_address";

    private BluetoothAdapter hBTAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_devicelist_drawer);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);
    }
}
