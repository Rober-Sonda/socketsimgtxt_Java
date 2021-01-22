package com.rober.imagesockects;

import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.chrono.MinguoChronology;

public class Servidor implements Runnable {
//    EditText _editText;
    private String _ServerUser;
    private ServerSocket _Server;
    private Socket _Client;
    private BufferedReader _Input;
    private String _IPAddress;
    private int _PORT;
    public LinearLayout _Chat;
    private ObjectInputStream _Entrada;
    MainActivity _main;
    private String mensajeRecibido;

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
                    //SI ES UN TEXTO MUESTRO UN TEXTO
                    mostrarInfoenPantalla();
                    //SINO MUESTRO LA IMAGEN ENVIADA POR EL CLIENTE

                    //EL SERVIDOR SOLO ENVIARA MENSAJES DE RECIBIDO AL CLIENTE
                    procesarConexion();
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

    public void mostrarInfoenPantalla(){
        _main.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String message = mensajeRecibido;
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

    public void procesarConexion() throws IOException {
//        InputStream fuente = new BufferedInputStream(new FileInputStream("C:/Users/Enzo/Downloads/fotografo-paisajes.jpg"));
//        OutputStream destino =new FileOutputStream("file.jpg");
        DataInputStream fuente = new DataInputStream(_Client.getInputStream());
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
//        destino.close();
        // destino.write(i);
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