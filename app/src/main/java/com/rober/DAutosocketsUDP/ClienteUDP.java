package com.rober.DAutosocketsUDP;

import android.net.Uri;
import android.provider.ContactsContract;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClienteUDP implements Runnable{
    private MainActivity _main;
    private String _Texto;
    private Uri Url_Fuente_Gallery;

    public ClienteUDP(MainActivity main, String Texto){
        _main = main;
        _Texto = Texto;
    }
    public ClienteUDP(MainActivity main, Uri Url_Fuente_Gallery) throws IOException {

//        MainActivity main
        _main = main;
        this.Url_Fuente_Gallery = Url_Fuente_Gallery;
//        url_img = _main.getContentResolver().openInputStream(Url_Fuente_Gallery);
    }

    @Override
    public void run() {
        DatagramSocket SocketUDP = null;
        try{

            SocketUDP = new DatagramSocket();

            if (GlobalInfo.queEnvio == 1) { //indicador global
                byte[] buffer = new byte[1500];
                byte[] buffer2 = new byte[1500];
                int i = 0;
                //Si es texto cargo el primer byte en 1
                buffer[0] = 1;
                //obtengo los bytes de la cadena
                buffer2  = _Texto.getBytes();
                //concateno los bytes de la cadena con el primer byte 0 que indica que es un texto
                while(i < buffer2.length){
                    buffer[i+1]=buffer2[i];
                    i++;
                }

                DatagramPacket peticion = new DatagramPacket(buffer,                                //preparo el paquete para enviar al servidor
                                                            0,                               //con los datos almacenados en el buffer
                                                            buffer.length,
                                                            GlobalInfo.IPServer,
                                                            GlobalInfo.PORT);

                SocketUDP.send(peticion);                                                           //enviamos el datagrama al servidor
            }else if (GlobalInfo.queEnvio == 2) {   //ENVIO UDP DE IMAGENES
                //Si lo que envio es una imagen vuelco los binarios en un buffer

                byte[] buffer = new byte[1024];
                String send_image = "IMAGE PTR=0 SIZE=\0";

//                buffer[0] = 2; // 2:IMAGEN
//                out.write(buffer,0,1); //envia el primer byte
//                while ((charsRead = in.read(buffer,0,buffer.length)) != -1) {
//                    out.write(buffer,0,charsRead);  //envia los demas
//                }
//                out.flush();
//                in.close();
//                out.close();
//                cerrarConexion(_SocketCliente); // cierra la conexion con el servidor
            } else{

            }


        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
}
