package com.rober.imagesockects;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.w3c.dom.Text;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente implements Runnable {
    public String IPServidor = GlobalInfo.IPServidor;
    public int _PORT = GlobalInfo.PORT;
    public MainActivity _main;
    private String _Chat;


    private Socket _SocketCliente;
    public Cliente(MainActivity main, String chat)  {

//        MainActivity main
        _main = main;
        _Chat = chat;
    }
    @Override
    public void run() {
//        DataInputStream in;

        DataOutputStream out;
        try {
            String ipdemiequipo = GlobalInfo.getIP();
            Log.d("Cliente", "Mensaje desde el cliente" + " " + ipdemiequipo + " " + String.valueOf(_PORT));

            _SocketCliente = new Socket(IPServidor, _PORT);
            out = new DataOutputStream(_SocketCliente.getOutputStream());
            String message = _Chat;
            out.writeBytes("Cliente: " + message);
            out.flush(); //envia mensaje al servidor
            // cierra la conexion con el servidor
            cerrarConexion(_SocketCliente);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }
    }


    public void cerrarConexion(Socket Cliente) throws IOException {
        Log.i("Cliente","Cierro conexion");
        Cliente.close();
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

    public void setMensaje(String mensaje) {
        this._Chat = mensaje;
    }

}






















































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
