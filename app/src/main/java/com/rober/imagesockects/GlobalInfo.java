package com.rober.imagesockects;

import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GlobalInfo {
        public static byte Rol = 1; // 0:Cancelado | 1:Servidor | 2:Cliente
        public static final int PORT = 50000;
        public static final String IPServidor = "192.168.88.42";
        public static final String IPAddress = getIP();

//        public static MaterialAlertDialogBuilder alert = null;
        public static String getIP(){
                List<InetAddress> addrs;
                String address = "";
                try{
                        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                        for(NetworkInterface intf : interfaces){
                                addrs = Collections.list(intf.getInetAddresses());
                                for(InetAddress addr : addrs){
                                        if(!addr.isLoopbackAddress() && addr instanceof Inet4Address){
                                                address = addr.getHostAddress().toUpperCase(new Locale("es", "MX"));
                                        }
                                }
                        }
                }catch (Exception e){
                        Log.w("TAG", "Ex getting IP value " + e.getMessage());
                }
                return address;
        }
}
