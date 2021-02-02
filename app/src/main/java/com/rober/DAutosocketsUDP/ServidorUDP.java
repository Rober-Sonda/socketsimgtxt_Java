package com.rober.DAutosocketsUDP;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.ExtractEditText;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.regex.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.rober.DAutosocketsUDP.GlobalInfo.*;

public class ServidorUDP implements Runnable{
    private DatagramSocket ServerSocketUDP;
    private MainActivity _main;
    private int _PORT;
    private String _Texto;

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

            int i = 0, j = 0;
            while(true){
                try{
                    byte[] buffer = new byte[1024];
                    DatagramPacket Datos = new DatagramPacket(buffer,buffer.length);
                    // Recibo y leo una petición del DatagramSocket Cliente
                    ServerSocketUDP.receive(Datos);
                    byte[] bufferImgRec = new byte[5*1024*1024];
                    byte[] buffer2;
                    buffer2 = Datos.getData();

                    if (buffer2[0] == 1){
                        System.out.println("Es texto");
                        mostrarInfoTxtenPantalla(Datos.getData(), Datos.getLength());
                    }else{
                        System.out.println("Es Imagen");
                        String encabezado= "";
                        while (buffer2[i]!=0 && i < buffer2.length){
                            encabezado += (char)(buffer2[i]);
                            i++;
                        }
                        i++;
                        int valorPTR = encontrarTexto(encabezado,"PTR=");
                        int valorSIZE = encontrarTexto(encabezado, "SIZE=");
                        int lengthArray = Datos.getLength();
                        System.arraycopy(buffer2, i, bufferImgRec, valorPTR, lengthArray - i);

                        Log.i("buf", "bufrecibido " + bufferImgRec[valorPTR]);
                        Log.i("buf", "bufrecibido buffer2 " + buffer2[i]);

                        //byte[] byteEncabezado;
                        //byteEncabezado = encabezado.getBytes();
                        Log.i("RECIBIDOS", encabezado);
                        i=0;
                        if(valorPTR+1024>valorSIZE) {
                            mostrarInfoImgenPantalla(bufferImgRec,valorSIZE);
                        }
                    }

                    System.out.print("Datagrama recibido desde el host: " + Datos.getAddress()+":" + Datos.getPort());
//                  creo el datagrama de tipo DatagramPacket para enviar la respuesta
//                  DatagramPacket respuesta =  new DatagramPacket (Datos.getData(),
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
    //        int valorSIZE = 0;
//        char[] ArrayCaracteres = cadena.toCharArray();
//        for(valor : ArrayCaracteres) {
//
//        }

    public int encontrarTexto(String Texto, String palabra){
        int pos = 0;
        Pattern regex = Pattern.compile("\\b" + Pattern.quote(palabra) + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher match = regex.matcher(Texto);
        if (match.find()) {  //si se quiere encontrar todas las ocurrencias: cambiar el if por while
            pos = match.end();
        }
        pos=valorCadena(Texto, pos);
        return pos;
    }

    public int valorCadena(@NotNull String cadena, int posInicialValor){
        int valor = 0;
        String strValor="";
        char[] ArrayCaracteres = cadena.toCharArray();
        while(posInicialValor <= ArrayCaracteres.length - 1){
            if(ArrayCaracteres[posInicialValor] != ' '){
                strValor+=ArrayCaracteres[posInicialValor];
                posInicialValor++;
            }else{
                break;
            }
        }
        valor = Integer.valueOf(strValor);
        return valor;
    }




    public void mostrarInfoTxtenPantalla(byte[] buffer, int punteroPpal) throws IOException {
        String message = "";
        message += new String(buffer).substring(1, punteroPpal); //salteamos el dato de archivo
        set_Texto(message);
        Log.i("Server QNTT", message + " soy el servidor UDP");

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

    public void mostrarInfoImgenPantalla(byte[] buffer2, int punteroPpal) throws IOException {
        _main.cargarimgChatFrag(R.id.frg_Content);
        _main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout BtnsImg = _main.findViewById(R.id.linearLButtons); //R.id.mnimg_id);
                TextView TxtDescripcion = _main.findViewById(R.id.txtv_subtitulo); //R.id.mnimg_id);
                TxtDescripcion.setText("IMAGEN RECIBIDA DESDE EL CLIENTE: " + ServerSocketUDP.getInetAddress());
                if (GlobalInfo.Rol == 1) {
                    BtnsImg.setVisibility(View.INVISIBLE);
                } else {
                    BtnsImg.setVisibility(View.VISIBLE);
                }
                ImageView imageView = _main.findViewById(R.id.imgchatid2);
                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer2, 0, punteroPpal);
                imageView.setImageBitmap(bitmap);
            }
        });
    }
}