package com.github.irvisozuna.zbtprinter;

import java.io.IOException;
import android.os.Bundle;
import android.os.Looper;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;
import com.zebra.android.discovery.*;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

public class ZebraBluetoothPrinter extends CordovaPlugin {

    private static final String LOG_TAG = "ZebraBluetoothPrinter";
    private Connection printerConnection;
    private ZebraPrinter printer;
    //String mac = "AC:3F:A4:1D:7A:5C";

    public ZebraBluetoothPrinter() {
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("print")) {
            try {
                String mac = args.getString(0);
                String msg = args.getString(1);
                sendData(callbackContext, mac, msg);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        if (action.equals("find")) {
            try {
                findPrinter(callbackContext);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    
    public void findPrinter(final CallbackContext callbackContext) {
      try {
          BluetoothDiscoverer.findPrinters(this.cordova.getActivity().getApplicationContext(), new DiscoveryHandler() {

              public void foundPrinter(DiscoveredPrinter printer) {
                  String macAddress = printer.address;
                  //I found a printer! I can use the properties of a Discovered printer (address) to make a Bluetooth Connection
                  callbackContext.success(macAddress);
              }

              public void discoveryFinished() {
                  //Discovery is done
              }

              public void discoveryError(String message) {
                  //Error during discovery
                  callbackContext.error(message);
              }
          });
      } catch (Exception e) {
          e.printStackTrace();
      }      
    }

    /*
     * This will send data to be printed by the bluetooth printer
     */
    void sendData(final CallbackContext callbackContext, final String mac, final String msg) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
              doConnection(mac);
          }
        }).start();
    }
    private void doConnection(String mac) {
        printer = connect(mac);
        if (printer != null) {
            sendTestLabel();
        } else {
            disconnect();
        }
    }

    public ZebraPrinter connect(String mac) {
        printerConnection = null;
        if (isBluetoothSelected()) {
            printerConnection = new BluetoothConnection(mac);
        } 

        try {
            printerConnection.open();
        } catch (ConnectionException e) {
            sleep(1000);
            disconnect();
        }

        ZebraPrinter printer = null;

        if (printerConnection.isConnected()) {
            try {
                printer = ZebraPrinterFactory.getInstance(printerConnection);
                PrinterLanguage pl = printer.getPrinterControlLanguage();
            } catch (ConnectionException e) {
                printer = null;
                sleep(1000);
                disconnect();
            } catch (ZebraPrinterLanguageUnknownException e) {
                printer = null;
                sleep(1000);
                disconnect();
            }
        }

        return printer;
    }
    public Boolean isBluetoothSelected(){
      return true;
    }
    public void disconnect() {
        try {
            if (printerConnection != null) {
                printerConnection.close();
            }
        } catch (ConnectionException e) {
        } finally {
        }
    }
    private void sendTestLabel() {
        try {
            byte[] configLabel = getConfigLabel();
            printerConnection.write(configLabel);
            sleep(1500);
            if (printerConnection instanceof BluetoothConnection) {
                String friendlyName = ((BluetoothConnection) printerConnection).getFriendlyName();
                sleep(500);
            }
        } catch (ConnectionException e) {

        } finally {
            disconnect();
        }
    }

    private byte[] getConfigLabel() {
        PrinterLanguage printerLanguage = printer.getPrinterControlLanguage();

        byte[] configLabel = null;
        if (printerLanguage == PrinterLanguage.ZPL) {
            configLabel = "Hola Mundo".getBytes();
        } else if (printerLanguage == PrinterLanguage.CPCL) {
            String cpclConfigLabel = "! 0 200 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 TEST\r\n" + "PRINT\r\n";
            configLabel = cpclConfigLabel.getBytes();
        }
        return configLabel;
    }
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}