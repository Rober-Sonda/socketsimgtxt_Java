package com.rober.imagesockects;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.Buffer;
import java.time.chrono.MinguoChronology;

public class Servidor implements Runnable {
    private ServerSocket _Server;
    EditText msj;
    private String _ServerUser;
    private Socket _Client;
    private BufferedReader _Input;
    private String _IPAddress;
    private int _PORT;
    public LinearLayout _Chat;
    private ObjectInputStream _Entrada;
    MainActivity _main;
    private String mensajeRecibido;
    DataInputStream fuente;
    public Servidor(String IPAddress, int PORT, MainActivity main){
        _PORT = PORT;
        _IPAddress = IPAddress;
//        _editText = editText;
        _main = main;
        _Chat = _main.findViewById(R.id.chat_id);
    }
    @Override
    public void run() {
        try {
            _Server = new ServerSocket(_PORT);

            while ( true ) {
                try {

                    esperarConexion();
//                    System.out.println( "Conexion aceptada\n" );
                    System.out.println( "\nSe recibieron los flujos de entrada\n" );
                    if (GlobalInfo.queEnvio == 1) {
                        //SI ES UN TEXTO MUESTRO UN TEXTO
                        mostrarInfoTxtenPantalla(); // del lado del servidor
                    }else if(GlobalInfo.queEnvio == 2){
                        //SINO MUESTRO LA IMAGEN ENVIADA POR EL CLIENTE
                        mostrarInfoImgenPantalla(); // del lado del servidor
                    }else {
                        //queEnvio == 0
                        //Envie un mensaje sin formato
                        AlertDialog.Builder builder = new AlertDialog.Builder(_main);
                        builder.setMessage("El mensaje del cliente llego null");
                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }

                    //EL SERVIDOR SOLO ENVIARA MENSAJES DE RECIBIDO AL CLIENTE
                    //en formato texto
                } catch (Exception ex) {
                    System.err.println(ex.toString());
                    ex.printStackTrace();
                } finally {
                    cerrarConexion();   // Paso 5: cerrar la conexión.
                }
            }
        } catch (IOException IOEx) {
            IOEx.printStackTrace();
        }


    }

    public void mostrarInfoTxtenPantalla() throws IOException{
        fuente = new DataInputStream(_Client.getInputStream());
        String message = "";
        int charsRead = 0;
        byte[] buffer = new byte[1024];
        while ((charsRead = fuente.read(buffer)) != -1) {
            message += new String(buffer).substring(0, charsRead);
        }
        setMensajeRecibido(message);
        Log.i("Server QNTT",message + " soy el servidor");
        if (fuente != null) {
            fuente.close();
        }
        _main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = getMensajeRecibido();
                TextView TxtMensaje = new TextView(_main);
                TxtMensaje.setText("Servidor: " + _IPAddress + message);
                TxtMensaje.setTextSize(15);
                TxtMensaje.setGravity(Gravity.RIGHT);
                LinearLayout chat = _main.findViewById(R.id.chat_id);
                chat.addView(TxtMensaje);
                System.out.println("Servidor: " + message);
            }
        });
    }

    // esperar que la conexión llegue, después mostrar información de la conexión

    public void mostrarInfoImgenPantalla() throws IOException {
//        InputStream fuente = new BufferedInputStream(new FileInputStream("C:/Users/Enzo/Downloads/fotografo-paisajes.jpg"));
//        OutputStream destino =new FileOutputStream("file.jpg");
            ImageView imageView = _main.findViewById(R.id.imgchatid2);
            fuente = new DataInputStream(_Client.getInputStream());

            int charsRead, i = 0;
            byte[] buffer = new byte[1024];
            byte[] buffer2 = new byte[5*1024*1024];
        int punteroPpal = 0, punteroAux = 0;
        while ((charsRead = fuente.read(buffer)) != -1) {

            for (int n=0;n<buffer.length;n++) {
                buffer2[punteroPpal] = buffer[n]; //1024
                punteroPpal++;
            }
            punteroPpal=punteroPpal;
        }

            byte [] buf = buffer2;
            String s = new String(buf, "UTF-8");
            Uri uri = Uri.parse(s);
            _main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageURI(uri);
                }
            });

        if (fuente != null) {
                fuente.close();
            }
    }

    private void esperarConexion() throws IOException {
        Log.i("Servidor","Esperando una conexión\n" );
//        try {/
            _Client = _Server.accept(); // permitir al servidor aceptar la conexión Y ESPERA
//        } catch (IOException e) {/
//            e.printStackTrace();
//        }
        Log.i("Servidor","Conexión recibida de: " + _Client.getInetAddress().getHostName() );
    }

    public void cerrarConexion() throws IOException {
//        try{
//            Log.d("aviso de cierre", "Finalizando la conexión\n");
//        }catch (Exception e){
//
//        }
        _Client.close();

    }
    public String getMensajeRecibido() {
        return mensajeRecibido;
    }

    public void setMensajeRecibido(String mensajeRecibido) {
        this.mensajeRecibido = mensajeRecibido;
    }
}




// CODIGO DE RECICLAJE


//            while ((charsRead = fuente.read(buffer)) != -1) {
//                imageView = new String(buffer).substring(0, charsRead);
////                System.out.println(message);
////                System.out.println("aca llego la imagen");
//            }
//            setMensajeRecibido(message);
//            Log.i("Server QNTT",message + " soy el servidor");

//                    Bitmap bitmap = BitmapFactory.decodeByteArray(buffer2, 0, buffer2.length);
//                    imageView.setImageBitmap(bitmap);
//                    ImageView imageView = imageView; //OBTENER BUFFER
//                    TextView TxtMensaje = new TextView(_main);
//                    TxtMensaje.setText("Servidor: " + _IPAddress + message);
//                    TxtMensaje.setTextSize(15);
//                    TxtMensaje.setGravity(Gravity.RIGHT);
//                    LinearLayout chat = _main.findViewById(R.id.chat_id);
//                    chat.addView(TxtMensaje);
//                    System.out.println("Servidor: " + message);


//                for (int n=j; n < buffer.length; n++){
//                    buffer2[i] = buffer[i];
//                }

//        while ((charsRead = fuente.read(buffer)) != -1) {
////            i = fuente.read(buffer);
////                imageView = new String(buffer).substring(0, charsRead);
////                System.out.println(message);
////                System.out.println("aca llego la imagen");
//            buffer2[i]= (byte) charsRead;
//        }
//            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer2, 0, buffer2.length);

// procesar conexion leyendo byte a byte
//        do { // procesar los mensajes enviados por el cliente
//
//            // leer el mensaje y mostrarlo en pantalla
//            try {
//                byte[] bytesImagen = new byte[0];
//                try {
//                    bytesImagen = (byte[]) _Entrada.readObject();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                ByteArrayInputStream entradaImagen = new ByteArrayInputStream(bytesImagen);
//                BufferedImage bufferedImage = ImageIO.read(entradaImagen);
//
//                String nombreFichero=System.getProperty("user.home")+File.separatorChar+"captura.jpg";
//                System.out.println("Generando el fichero: "+nombreFichero );
//                FileOutputStream out = new FileOutputStream(nombreFichero);
//                // esbribe la imagen a fichero
//                ImageIO.write(bufferedImage, "jpg", out);
//            }
//
//            // atrapar problemas que pueden ocurrir al tratar de leer del cliente
//            catch () {
//                System.out.println( "\nSe recibió un tipo de objeto desconocido" );
//            }
//
//        } while ( true );