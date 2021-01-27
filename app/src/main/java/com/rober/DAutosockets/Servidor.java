package com.rober.DAutosockets;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
    DataOutputStream out;
    private byte[] typeFile = new byte[1];
    private int charsRead;



    private byte tipoDato; // 1:TEXTO 2:IMAGEN
    public Servidor(String IPAddress, int PORT, MainActivity main) {
        _PORT = PORT;
        _IPAddress = IPAddress;
        _main = main;
        _Chat = _main.findViewById(R.id.chat_id);
        charsRead = 0;
    }

    @Override
    public void run() {
        try {
            _Server = new ServerSocket(_PORT);

            while (true) {
                try {
                    esperarConexion();
                    System.out.println("\nSe recibieron los flujos de entrada\n");
                    int indicador = 0;
                    fuente = new DataInputStream(_Client.getInputStream());
                    do {
                        indicador = fuente.read(typeFile);
                    } while (indicador == 0);
                    out = new DataOutputStream(_Client.getOutputStream());   //Canal de salida para texto e imagen
                    if (typeFile[0] == 1) {   ///////////////////////////////////////////
                        //SI ES UN TEXTO MUESTRO UN TEXTO
                        byte[] buffer = new byte[2048];
                        byte[] buffer2 = new byte[5 * 1024 * 1024];
                        int punteroPpal = 0;
//                        fuente = new DataInputStream(_Client.getInputStream());
                        while ((charsRead = fuente.read(buffer)) != -1) {
                            for (int n = 0; n < charsRead; n++) {
                                buffer2[punteroPpal] = buffer[n]; //1024
                                punteroPpal++;
                            }
                        }
                        mostrarInfoTxtenPantalla(buffer2, punteroPpal); // del lado del servidor
                    } else if (typeFile[0] == 2) {
                        byte[] buffer = new byte[2048];
                        byte[] buffer2 = new byte[5 * 1024 * 1024];
                        int punteroPpal = 0;
//                        fuente = new DataInputStream(_Client.getInputStream());
                        while ((charsRead = fuente.read(buffer)) != -1) {
                            for (int n = 0; n < charsRead; n++) {
                                buffer2[punteroPpal] = buffer[n]; //1024
                                punteroPpal++;
                            }
                        }
                        //MUESTRO LA IMAGEN ENVIADA POR EL CLIENTE
                        mostrarInfoImgenPantalla(buffer2, punteroPpal); // del lado del servidor
                        //, new frg_txtChat(),R.id.frg_Content
                    } else {
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

    public void mostrarInfoTxtenPantalla(byte[] buffer, int punteroPpal) throws IOException {
        String message = "";
        message += new String(buffer).substring(0, punteroPpal); //salteamos el dato de archivo
        setMensajeRecibido(message);
        Log.i("Server QNTT", message + " soy el servidor");
        if (fuente != null) {
            fuente.close();
        }
        _main.cargartxtChatFrag(R.id.frg_Content);
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
    public void mostrarInfoImgenPantalla(byte[] buffer2, int punteroPpal) throws IOException {
        _main.cargarimgChatFrag(R.id.frg_Content);
        _main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView imageView = _main.findViewById(R.id.imgchatid2);
                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer2, 0, punteroPpal);
                imageView.setImageBitmap(bitmap);
            }
        });
        if (fuente != null) {
            fuente.close();
        }

    }

    public void cargarFragment(Fragment frgseleccionado, int frgDestino){
        FragmentManager fragmentManager = _main.getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frgDestino, frgseleccionado);
        transaction.commit();
    }
    public byte getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(byte tipoDato) {
        this.tipoDato = tipoDato;
    }

    private void esperarConexion() throws IOException {
        Log.i("Servidor", "Esperando una conexión\n");
        try {
            _Client = _Server.accept(); // permitir al servidor aceptar la conexión Y ESPERA
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cerrarConexion() throws IOException {
        try {
            Log.d("aviso de cierre", "Finalizando la conexión\n");
            _Client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMensajeRecibido() {
        return mensajeRecibido;
    }

    public void setMensajeRecibido(String mensajeRecibido) {
        this.mensajeRecibido = mensajeRecibido;
    }
}


// CODIGO DE RECICLAJE


//        Log.i("Servidor","Conexión recibida de: " + _Client.getInetAddress().getHostName() );
//        System.out.println(""+uri.getPath());
//        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath()); //decodeByteArray(buffer2, 0, buffer2.length);

//        byte [] buf = buffer2;
//        String s = new String(buffer2, "UTF-8");
//        Uri uri = Uri.;

//        InputStream fuente = new BufferedInputStream(new FileInputStream("C:/Users/Enzo/Downloads/fotografo-paisajes.jpg"));
//        DataOutputStream destino = new DataOutputStream(_Server);

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