package com.doubleteam.healthcare.activity;


import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.Handler;

import com.doubleteam.healthcare.R;
import com.doubleteam.healthcare.bluetoothSevice.BluetoothTransmitService;




/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {
    private static final String TAG="startFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT=3;

    // Layout Views
    private ImageButton HstartButton;

    /**
     * Name of the connected device
     */
    private String hConnectedDeviceName = null;


    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter HbluetoothAdapter = MainActivity.getHbluetoothAdapter();
    /**
     * Member object for the BluetoothTransmitService
     */
    static BluetoothTransmitService HbtTransmitService = MainActivity.getHbtTransmitService();

    public static Context context;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context=activity.getApplication();
        Toast.makeText(getActivity(),"onAttach()",Toast.LENGTH_LONG).show();


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //if the adapter is null,then BT is not supported
        if(HbluetoothAdapter == null){
            FragmentActivity activity = getActivity();
            Toast.makeText(activity,"Bluetooth is not available",Toast.LENGTH_LONG).show();
            activity.finish();
        }
        Toast.makeText(getActivity(),"onCreate()",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        if (!HbluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        setupTransaction();
        Toast.makeText(getActivity(),"onStart()",Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HstartButton = (ImageButton) view.findViewById(R.id.btn_start);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getActivity(),"onDestroy()",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(HbtTransmitService != null){
            if(HbtTransmitService.gethstate() == BluetoothTransmitService.STATE_NONE){
                HbtTransmitService.start();
                Toast.makeText(context,"onResume",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupTransaction(){
        Log.d(TAG, "setupTransaction");

        // Initialize the send button with a listener that for click events
        HstartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serverIntent = new Intent(context, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            }
        });
        // Get the BluetoothTransmitService to perform bluetooth connections
        if(MainActivity.HstartService && HbtTransmitService == null){
            HbtTransmitService = new BluetoothTransmitService(context,hhandler);
        }
        Toast.makeText(context,"setupTransaction()" + HbtTransmitService.gethstate(),Toast.LENGTH_LONG).show();


    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId){
        FragmentActivity activity = getActivity();
        if (null == activity){
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if(null == actionBar){
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle){
        FragmentActivity activity = getActivity();
        if(null == activity){
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if(null == actionBar){
            return;
        }
        actionBar.setSubtitle(subTitle);
    }


    /**
     * The Handler that gets information back from the BluetoothTransmitService
     */
    public final Handler hhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what){
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1){
                        case BluetoothTransmitService.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to,hConnectedDeviceName));
                            Toast.makeText(activity, R.string.title_connected_to,Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothTransmitService.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            Toast.makeText(activity, R.string.title_connecting,Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothTransmitService.STATE_LISTEN:
                        case BluetoothTransmitService.STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            Toast.makeText(activity, R.string.title_not_connected,Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    break;
                case Constants.MESSAGE_READ:
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    hConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if(null != activity){
                        Toast.makeText(activity, "Connected to " + hConnectedDeviceName,Toast.LENGTH_LONG).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if(null != activity){
                        Toast.makeText(activity,msg.getData().getString(Constants.TOAST),Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }

    };

    public void onActivityResult(int requestCode, int resultCode, Intent data ){
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE_SECURE:
                Toast.makeText(getActivity(),"onActivityResult" ,
                        Toast.LENGTH_SHORT).show();
                // When DeviceListActivity returns with a device to connect
                if(resultCode == Activity.RESULT_OK){
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                Toast.makeText(getActivity(),"onActivityResult" ,
                        Toast.LENGTH_SHORT).show();
                // When DeviceListActivity returns with a device to connect
                if(resultCode == Activity.RESULT_OK){
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                Toast.makeText(getActivity(),"onActivityResult" ,
                        Toast.LENGTH_SHORT).show();
                // When the request to enable Bluetooth returns
                if(resultCode == Activity.RESULT_OK){
                    setupTransaction();
                }else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(),"Bluetooth was not enabled. Leaving app." ,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }

    }

    /**
     * Establish connection with other divice
     *
     * @param _data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param _secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent _data, boolean _secure){
        // Get the device MAC address
        String address = _data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = HbluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        HbtTransmitService.connect(device,_secure);
        Toast.makeText(getActivity(),"connectDevice" ,
                Toast.LENGTH_SHORT).show();
    }





}
