package com.doubleteam.healthcare.bluetoothSevice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothTransmitService extends Service {
    public BluetoothTransmitService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
