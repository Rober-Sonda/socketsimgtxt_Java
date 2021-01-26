package com.rober.imagesockects;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente implements Runnable {
    public String IPServidor = GlobalInfo.IPServidor;
    public int _PORT = GlobalInfo.PORT;
    public MainActivity _main;
    private String _Chat;
    DataOutputStream out;
    DataInputStream in;
    private InputStream url_img;
    private Uri URL_FUENTE_GALLERY;
    private Socket _SocketCliente;

    public Cliente(MainActivity main, String chat)  {
//        MainActivity main
        _main = main;
        _Chat = chat;
    }
    public Cliente(MainActivity main, Uri URL_FUENTE_GALLERY) throws IOException {

//        MainActivity main
        _main = main;

        this.URL_FUENTE_GALLERY = URL_FUENTE_GALLERY;
//        url_img = _main.getContentResolver().openInputStream(URL_FUENTE_GALLERY);
    }
    @Override
    public void run() {

        try {
            String ipdemiequipo = GlobalInfo.getIP();
            Log.d("Cliente", "Mensaje desde el cliente" + " " + ipdemiequipo + " " + String.valueOf(_PORT));

            _SocketCliente = new Socket(IPServidor, _PORT);

            if (GlobalInfo.queEnvio == 1) {  // 1:TEXTO
                out = new DataOutputStream(_SocketCliente.getOutputStream());
                String message = _Chat;
                String prueba = out.toString();
                Log.i("OUT CLIENTE: ", prueba);
                out.writeBytes("Cliente: " + message);
                out.flush(); //envia mensaje al servidor
                cerrarConexion(_SocketCliente);// cierra la conexion con el servidor

            } else if (GlobalInfo.queEnvio == 2) { // 2:IMAGENES
                //Envio imagen
                out = new DataOutputStream(_SocketCliente.getOutputStream());
                InputStream url_img = _main.getContentResolver().openInputStream(URL_FUENTE_GALLERY);

//                int charsRead = 0;
//                int i = url_img.read();
                byte[] buffer = new byte[5*1024*1024];
                out.writeBytes(url_img.toString());
                out.flush();
                cerrarConexion(_SocketCliente);// cierra la conexion con el servidor

                } else {
                    //Envie un mensaje sin formato  0: NO ESPECIFICADO
                    AlertDialog.Builder builder = new AlertDialog.Builder(_main);
                    builder.setMessage("El mensaje del cliente llego null intenta nuevamente");
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }

            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex.toString());
            }
        }

    public void cerrarConexion(Socket Cliente) throws IOException {
        Log.i("Cliente","Cierro conexion");
        Cliente.close();
    }

    public void setMensaje(String mensaje) {
        this._Chat = mensaje;
    }

}


//    public void set_IPAddress(String _IPAddress) {
//        this._IPAddress = _IPAddress;
//    }
//
//    public void set_PORT(int _PORT) {
//        this._PORT = _PORT;
//    }

//    public String get_IPAddress() {
//        return _IPAddress;
//    }
//
//    public int get_PORT() {
//        return _PORT;
//    }
//
//    public TextView getMensaje() {
//        return _mensaje;
//    }

//                //InputStream fuente = new BufferedInputStream(new FileInputStream("C:/Users/Enzo/Downloads/fotografo-paisajes.jpg"));
//                //OutputStream destino =new FileOutputStream("file.jpg");


//                //BufferedOutputStream fuente = new BufferedOutputStream(_SocketCliente.getOutputStream());
//                out = new DataOutputStream(_SocketCliente.getOutputStream());
//                int charsRead = 0;
//                int i = URL_IMG.read();
//                byte[] buffer = new byte[1024];
//                byte[] buffer2 = new byte[5*1024*1024];
//                while ((charsRead = URL_IMG.read(buffer)) != -1) {
//                    //buffer2 += buffer.substring(0, charsRead);
//                    System.out.println((char)charsRead);
////                    buffer2 =
//                }
//
//                Log.i("Cliente ", "enviando una imagen");
//                if (URL_IMG != null) {
//                    URL_IMG.close();
//                }


















































//    public static void main(String[] args)  throws IOException {
//        Socket socketCliente = null;
//        BufferedReader entrada = null;
//        PrintWriter salida = null;
//
//        // Creamos un socket en el lado cliente, enlazado con un
//        // servidor que escucha en el puerto 4444
//        try {
//            socketCliente = new Socket("localhost", 4444);
//            // Obtenemos el canal de entrada
//            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
//            // Obtenemos el canal de salida
//            salida = new PrintWriter(new BufferedWriter(new
//                    OutputStreamWriter(socketCliente.getOutputStream())),true);
//        } catch (IOException e) {
//            System.err.println("No puede establer canales de E/S para la conexión");
//            System.exit(-1);
//        }
//        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
//
//        String linea;
//        // El programa cliente no analiza los mensajes enviados por el
//        // usario, simplemente los reenvía al servidor hasta que este
//        // se despide con "Adios"
//        try {
//            while (true) {
//                // Leo la entrada del usuario
//                linea = stdIn.readLine();
//                // La envia al servidor
//                salida.println(linea);
//                // Envía a la salida estándar la respuesta del servidor
//                linea = entrada.readLine();
//                System.out.println("Respuesta servidor: " + linea);
//                // Si es "Adios" es que finaliza la comunicación
//                if (linea.equals("Adios")) break;
//            }
//        } catch (IOException e) {
//            System.out.println("IOException: " + e.getMessage());
//        }
//
//        // Libera recursos
//        salida.close();
//        entrada.close();
//        stdIn.close();
//        socketCliente.close();
//    }
//}
