package com.rober.DAutosocketsUDP;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.Buffer;
import java.util.concurrent.CopyOnWriteArrayList;

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

    @SuppressLint("DefaultLocale")
    @Override
    public void run() {
        DatagramSocket SocketUDP = null;
        try {

            SocketUDP = new DatagramSocket();

            if (GlobalInfo.queEnvio == 1) { //indicador global 1: TEXTO 2: IMAGEN
                byte[] buffer = new byte[1500];
                byte[] buffer2 = new byte[1500];
                int i = 0;
                //Si es texto cargo el primer byte en 1
                buffer[0] = 1;
                //obtengo los bytes de la cadena
                buffer2 = _Texto.getBytes();
                //concateno los bytes de la cadena con el primer byte 0 que indica que es un texto
                while (i < buffer2.length) {
                    buffer[i + 1] = buffer2[i];
                    i++;
                }

                DatagramPacket peticion = new DatagramPacket(buffer,                                //preparo el paquete para enviar al servidor
                                                0,                               //con los datos almacenados en el buffer
                                                buffer.length,
                                                GlobalInfo.IPServer,
                                                GlobalInfo.PORT);
                SocketUDP.send(peticion);                                                           //enviamos el datagrama al servidor
            } else if (GlobalInfo.queEnvio == 2) {   //ENVIO UDP DE IMAGENES
                //Si lo que envio es una imagen vuelco los binarios en un buffer
                String send_image = "";
                DatagramPacket peticion = null;
                int BloqueCortePaquete = 1024, PTR = 0 ;
                int charsRead = 0, tamañoBuffer = 0, n = 0, i = 0 ;
                byte[] bufferImg = new byte[10 * 1024 * 1024];
                byte[] buffer2 = new byte[2048];
                byte[] buffer;
                InputStream in = null;
                if (Url_Fuente_Gallery!=null) { //SOLO INGRESA SI HAY UNA IMAGEN

                    in = _main.getContentResolver().openInputStream(Url_Fuente_Gallery);

                    while ((charsRead = in.read(bufferImg, tamañoBuffer, bufferImg.length - tamañoBuffer)) != -1) {
                        //tengo el tamaño del buffer cargado
                        tamañoBuffer += charsRead;
                    }

                }else{
                    finalize();
                }


                while (PTR < tamañoBuffer) {

                    send_image = String.format("IMAGE PTR=%d SIZE=%d\0", PTR, tamañoBuffer);

                    buffer = send_image.getBytes(); //encabezado

                    //copio el encabezado al buffer2
                    System.arraycopy(buffer, 0, buffer2, 0, buffer.length);//agregar encabezado

                    int arraylength = buffer.length;

                    //aca hago el corrimiento para copiar la imagen en caso de que sea la ultima trama
                    if((PTR + BloqueCortePaquete) > tamañoBuffer){
                        BloqueCortePaquete = tamañoBuffer - PTR;
                        //CARGO ENCABEZADO
                    }
                    System.arraycopy(bufferImg, PTR, buffer2, arraylength, BloqueCortePaquete);
                    //largo del datagrama           arrayLength es el largo del encabezado
                    int DatagLength = arraylength + BloqueCortePaquete;
                    Log.i("ENVIADOS", new String(buffer));
                    Log.i("buffer2", "bufenviado  --> " + buffer2[arraylength]); // +1 para ver el primer byte despues del 0
                    Log.i("bufferImg", "bufferImg --> " + bufferImg[PTR]);
                    //BloqueCortePaquete es la cantidad de bytes que cargo en buffer2
                    peticion = new DatagramPacket(  buffer2,//preparo el paquete para enviar al servidor
                                             0,
                                                    DatagLength,
                                                    GlobalInfo.IPServer,
                                                    GlobalInfo.PORT);
                    SocketUDP.send(peticion);
                    //DatagLength es el encabezado y el cuerpo del mensaje
                    PTR+=BloqueCortePaquete;
                }

                // * simplificar el codigo
                // * asignar los binarios
                // * armar el paquete a enviar

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(_main);
                    builder.setMessage("El tipo de datos a enviar no es válido");
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
