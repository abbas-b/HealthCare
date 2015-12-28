package com.doubleteam.healthcare.bluetoothSevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.doubleteam.healthcare.activity.Constants;
import com.doubleteam.healthcare.activity.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by bsh on 12/16/2015.
 */
public class BluetoothTransmitService {
    // Debugging
    private static final String TAG="BluetoothTransmitService";

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothTransmitSecure";
    private static final String NAME_INSECURE = "BluetoothTransmitInSecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    // Member fields
    private final BluetoothAdapter hAdapter;
    private final Handler hHandler;
    private AcceptThread hSecureAcceptThread;
    private AcceptThread hInSecureAcceptThread;
    private ConnectThread hConnectThread;
    private ConnectedThread hConnectedThread;
    private int hState;
    private Context context;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device


    public BluetoothTransmitService(Context _context,Handler _hhandler) {
        this.hAdapter = BluetoothAdapter.getDefaultAdapter();
        this.hHandler = _hhandler;
        this.hState = STATE_NONE;
        this.context = _context;

    }

    /**
     * Set the current state of the chat connection
     *
     * @param _state An integer defining the current connection state
     */
    private synchronized void setState(int _state){
        hState = _state;
        Log.d(TAG,"hstate " +hState+ "->" +_state);

        // Give the new state to the Handler so the UI Activity can update
        hHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE,_state,-1).sendToTarget();
    }
    /**
     * Return the current connection state.
     */
    public synchronized int gethstate(){
        return hState;
    }

    public void start(){
        Log.d(TAG, "start");
        // Cancel any thread attempting to make a connection
        if(hConnectThread != null){
            hConnectThread.cancel();
            hConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if(hConnectedThread != null){
            hConnectedThread.cancel();
            hConnectedThread = null;
        }
        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (hSecureAcceptThread == null) {
            hSecureAcceptThread = new AcceptThread(true);
            hSecureAcceptThread.start();
        }
        if(hInSecureAcceptThread == null){
            hInSecureAcceptThread = new AcceptThread(false);
            hInSecureAcceptThread.start();
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param _device The BluetoothDevice to connect
     * @param _secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice _device,boolean _secure){
        Log.d(TAG, "connect to" + _device);

        // Cancel any thread attempting to make a connection
        if(hState == STATE_CONNECTING){
            hConnectThread.cancel();
            hConnectThread = null;
        }
        // Cancel any thread currently running a connection
        if(hConnectedThread != null){
            hConnectedThread.cancel();
            hConnectedThread = null;
        }

        // Start the thread to connect with the given device
        hConnectThread = new ConnectThread(_device,_secure);
        hConnectThread.start();
        setState(STATE_CONNECTING);
        Toast.makeText(context,"successful connection",Toast.LENGTH_LONG).show();
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param _socket The BluetoothSocket on which the connection was made
     * @param _device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket _socket,BluetoothDevice _device, final String _socketType){
        Log.d(TAG,"connected, Socket Type" + _socketType);
        // Cancel any thread attempting to make a connection
        if(hState == STATE_CONNECTING){
            hConnectThread.cancel();
            hConnectThread = null;
        }
        // Cancel any thread currently running a connection
        if(hConnectedThread != null){
            hConnectedThread.cancel();
            hConnectedThread = null;
        }
        // Cancel the accept thread because we only want to connect to one device
        if(hSecureAcceptThread != null){
            hSecureAcceptThread.cancel();
            hSecureAcceptThread = null;
        }
        if(hInSecureAcceptThread != null){
            hInSecureAcceptThread.cancel();
            hInSecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        hConnectedThread = new ConnectedThread(_socket,_socketType);
        hConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = hHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        msg.setData(bundle);
        hHandler.sendMessage(msg);

        setState(STATE_CONNECTED);




    }

    /**
     * Stop all threads
     */
    public synchronized void stop(){
        Log.d(TAG,"stop");

        if(hConnectThread != null){
            hConnectThread.cancel();
            hConnectThread = null;
        }
        // Cancel any thread currently running a connection
        if(hConnectedThread != null){
            hConnectedThread.cancel();
            hConnectedThread = null;
        }
        // Cancel the accept thread because we only want to connect to one device
        if(hSecureAcceptThread != null){
            hSecureAcceptThread.cancel();
            hSecureAcceptThread = null;
        }
        if(hInSecureAcceptThread != null){
            hInSecureAcceptThread.cancel();
            hInSecureAcceptThread = null;
        }

        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out){
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this){
        if(hState != STATE_CONNECTED) return;;
            r = hConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed(){
        // Send a failure message back to the Activity
        Message msg = hHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST,"Unable to connect device");
        msg.setData(bundle);
        hHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothTransmitService.this.start();

    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost(){
        // Send a failure message back to the Activity
        Message msg = hHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST,"Device connection was lost");
        msg.setData(bundle);
        hHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothTransmitService.this.start();

    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread{
        // The local server socket
        private final BluetoothServerSocket hServerSocket;
        private String hSocketType;

       public AcceptThread(boolean secure) {
           BluetoothServerSocket tmp = null;
           hSocketType = secure ? "Secure" : "Insecure";

           // Create a new listening server socket
           try {
               if(secure){
                   tmp = hAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_SECURE,MY_UUID_SECURE);
               }else {
                   tmp = hAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE,MY_UUID_INSECURE);
               }
           } catch (IOException e){
               Log.e(TAG,"Socket Type:" + hSocketType + "listen() failed",e);
           }
           hServerSocket = tmp;
       }

        public void run(){
            Log.d(TAG, "Socket Type" + hSocketType + "Begin hAcceptThread" + this);
           // SetName("AcceptThread" + hSocketType);

            BluetoothSocket socket = null;
            // Listen to the server socket if we're not connected
            while (hState != STATE_CONNECTED){
                try {
                     // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket=hServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "SocketType: " + hSocketType +"accept() failed", e);
                    break;
                }
                // If a connection was accepted
                if(socket != null){
                    synchronized (BluetoothTransmitService.this){
                        switch (hState){
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket,socket.getRemoteDevice(),hSocketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END hAcceptThread, socketType: " + hSocketType);
        }
        public void cancel(){
            Log.d(TAG, "Socket Type" + hSocketType + "cancel " + this);
            try {
                hServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Sockt Type" + hSocketType + "close() of server failed", e);
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread{
        private final BluetoothSocket hSocket;
        private  final BluetoothDevice hDevice;
        private String hSocketType;

        public ConnectThread(BluetoothDevice _device, boolean _secure){
            hDevice = _device;
            BluetoothSocket tmp = null;
            hSocketType = _secure ? "Secure" : "Insecure";
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if(_secure){
                    tmp = _device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                }else {
                    tmp= _device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                }
            }catch (IOException e){
               Log.e(TAG, "Socket Type: " + hSocketType + "create() failed", e);
            }
           hSocket = tmp;
        }
       public void run(){
           Log.i(TAG, "BEGIN hConnectThread SocketType" + hSocketType);
         //  SetName("ConnectThread" + hSocketType);

           // Always cancel discovery because it will slow down a connection
           hAdapter.cancelDiscovery();

           // Make a connection to the BluetoothSocket
           try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
               hSocket.connect();

           }catch (IOException e){
               // Close the socket
               try {
                   hSocket.close();
               }catch (IOException e2){
                   Log.e(TAG,"unable to close() " + hSocketType + "socket during connection failure", e2);
               }
               connectionFailed();
               return;
           }
           // Reset the ConnectThread because we're done
           synchronized (BluetoothTransmitService.this){
               hConnectThread = null;
           }
           // Start the connected thread
           connected(hSocket,hDevice,hSocketType);
       }
        public  void cancel(){
            try {
                hSocket.close();
            }catch (IOException e){
                Log.e(TAG, "close() of connect " + hSocketType + " socket failed", e);
            }
        }

    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread{
        private final BluetoothSocket hSocket;
        private final InputStream hInStream;
        private final OutputStream hOutStream;

        public ConnectedThread(BluetoothSocket _socket, String _socketType){
            hSocket = _socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = _socket.getInputStream();
                tmpOut = _socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG , "temp sockets not created", e);
            }
            hInStream = tmpIn;
            hOutStream = tmpOut;
        }

        public void run(){
            Log.i(TAG, "BEGIN hConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream while connected
            while (true){
                try {
                    // Read from the InputStream
                    bytes = hInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    hHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }catch (IOException e){
                  Log.e(TAG, "disconnected", e);
                    connectionLost();
                    // Start the service over to restart listening mode
                    BluetoothTransmitService.this.start();
                    break;
                }
            }
        }
        /**
         * Write to the connected OutStream.
         *
         * @param _buffer The bytes to write
         */
        public void write(byte[] _buffer){
            try {
                hOutStream.write(_buffer);
                // Share the sent message back to the UI Activity
                hHandler.obtainMessage(Constants.MESSAGE_WRITE,-1,-1,_buffer).sendToTarget();
            }catch (IOException e){
                Log.e(TAG, "Exception during write", e);
            }
        }
        public void cancel(){
            try {
                hSocket.close();
            }catch (IOException e){
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

    }



}
