package com.rober.DAutosocketsUDP;
import android.inputmethodservice.ExtractEditText;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import static com.rober.DAutosocketsUDP.GlobalInfo.*;

public class ServidorUDP implements Runnable{
    private DatagramSocket ServerSocketUDP;
    private MainActivity _main;
    private int _PORT;
    private String _Texto;
    byte[] buffer = new byte[1500];
    private Uri Url_Fuente_Gallery;
    public ServidorUDP(int PORT, MainActivity main) throws SocketException {
        _main = main;
        _PORT = PORT;
    }

    @Override
    public void run(){
        try{
            ServerSocketUDP = new DatagramSocket(PORT);
            //do {
            //   indicador = fuente.read(typeFile);
            //} while (indicador == 0);

            //el buffer lo creamos dentro de cada clase de archivo
            boolean primeraLectura = false;

            while(true){
                try{
                    //Preparo la respuesta
//                    DatagramPacket peticion = new DatagramPacket(barray,0,barray.length);
                    // Recibo y leo una petición del DatagramSocket Cliente
                    DatagramPacket Datos = new DatagramPacket(buffer,0,buffer.length);
                    ServerSocketUDP.receive(Datos);
                    buffer = Datos.getData();
                    if (primeraLectura == false){
                        if (buffer[0] == 1){
                            System.out.println("Es texto");
                            mostrarInfoTxtenPantalla(Datos.getData(),Datos.getLength());
                        }else if(buffer[0] == 2){
                            System.out.println("Es Imagen");

                        }
                    }
                    System.out.print("Datagrama recibido desde el host: " + Datos.getAddress()+":" + Datos.getPort());





//creo el datagrama de tipo DatagramPacket para enviar la respuesta
//                    DatagramPacket respuesta =  new DatagramPacket (Datos.getData(),
//                                                                    Datos.getLength(),
//                                                                    Datos.getAddress(),
//                                                                    Datos.getPort());
//                    ServerSocketUDP.send(respuesta); //en caso de querer reenviar al cliente una respuesta
                    //la respuesta enviada es un eco
                }catch (SocketException  e){
                    System.out.println("Socket: " + e.getMessage());
                }
            }
        }catch (IOException e){
            System.out.println("IO: " + e.getMessage());
        }
    }
    public void mostrarInfoTxtenPantalla(byte[] buffer, int punteroPpal) throws IOException {
        String message = "";
        message += new String(buffer).substring(1, punteroPpal); //salteamos el dato de archivo
        set_Texto(message);
        Log.i("Server QNTT", message + " soy el servidor");
//        if (fuente != null) {
//            fuente.close();
//        }
        //Views
        _main.cargartxtChatFrag(R.id.frg_Content);
        String messageNuevo = get_Texto();
        TextView TxtMensaje = new TextView(_main);
        _main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ExtractEditText msg = _main.findViewById(R.id.msg_id); //R.id.mnimg_id);
                TextView TxtIndicadorRol = _main.findViewById(R.id.txtvcliente_id); //R.id.mnimg_id);
                Button btnEnviarTxt = _main.findViewById(R.id.btnSendTxt); //R.id.mnimg_id);
                TextView TxtDescripcion = _main.findViewById(R.id.txtv_subtitulo); //R.id.mnimg_id);
                TxtIndicadorRol.setText("SERVIDOR");
                TxtDescripcion.setText("RECIBISTE UN TEXTO DESDE EL CLIENTE: " + ServerSocketUDP.getInetAddress());
                if(GlobalInfo.Rol == 1) {
                    msg.setVisibility(View.INVISIBLE);
                    btnEnviarTxt.setVisibility(View.INVISIBLE);
                }

                TxtMensaje.setText("Servidor: " + getIP() +" "+ messageNuevo);
                TxtMensaje.setTextSize(15);
                TxtMensaje.setGravity(Gravity.LEFT);
                LinearLayout chat = _main.findViewById(R.id.chat_id);
                chat.addView(TxtMensaje);
            }
        });
    }


    public String get_Texto() {
        return _Texto;
    }

    public void set_Texto(String _Texto) {
        this._Texto = _Texto;
    }

}