package com.rober.imagesockects;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.rober.imagesockects.GlobalInfo.IPAddress;
import static com.rober.imagesockects.GlobalInfo.PORT;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1777;
    private static final int REQUEST_IMAGE_GALLERY = 2777;
//    Socket SocketCliente = new Socket(IPServidor, PORT);
//    Cliente cliente = new Cliente(SocketCliente);

    public MainActivity() throws IOException {

    }

    private View datos;
    ImageView Marco;
    TextView titulo;
    Button btnCliente, btnServidor, btnGallery, btnLimpiarChattxv;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCliente = findViewById(R.id.btnCliente);
        btnServidor = findViewById(R.id.btnServidor);


        btnServidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción de Servidor
                dialogtxtImg();
                System.out.println(IPAddress);
                System.out.println(String.valueOf(PORT));
                titulo = findViewById(R.id.txv_clienteServidor_id);
                titulo.setText("Servidor escuchando... :" + IPAddress + " " + PORT);
                //OCULTAR MENU PORQUE EL SERVIDOR NO ENVIA NADA AL CLIENTE
//                Servidor servidor = new Servidor(IPAddress, PORT);
//                String msj;
//                Thread hiloServer = new Thread(servidor); //Servidor a la escucha
//                hiloServer.start();
                new Thread(new Runnable() {
                    String msj = "";
                    LinearLayout linearLayout = findViewById(R.id.chat_id);
                    @Override
                    public void run() {
                        Servidor servidor = new Servidor(IPAddress, PORT, MainActivity.this);
                        servidor.run();
                        msj = servidor.getMensajeRecibido();
                        Log.i("Servidor", " " + msj); //Servidor a la escucha
                        // hilserv.start();
                        System.out.println("Servidor escuchando... :" + IPAddress + " " + PORT);
                    }
                }).start();
//                Log.i("Servidor", " " + msj);
                System.out.println("Servidor escuchando... :" + IPAddress + " " + PORT);
            }
        });
        btnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción de Cliente
                titulo = findViewById(R.id.txv_clienteServidor_id);
//                Cliente cliente = new Cliente(); //Cliente se conecta
//                cliente.run();
                titulo.setText("Cliente listo... " + IPAddress + " " + PORT);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            Log.i("RC", "requestCode: " + requestCode);
            Log.i("RC", "permissions.length: " + permissions.length);
            boolean resultado = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
            Log.i("RC", "grantResults[0] == PackageManager.PERMISSION_GRANTED: " + resultado);
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                Toast.makeText(MainActivity.this, "Necesitas habilitar los permisos", Toast.LENGTH_SHORT).show();
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);

    }

    private void abrirGaleria() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
//        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), 10);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Log.i("TAG", "Result" + resultCode);
                Uri imagen = data.getData();
                System.out.println(imagen);
                if (Marco != null) {
                    Marco.setImageURI(imagen);
                } else {
                    Log.i("TAG2", "El marco no existe todavia");
                }
            } else {
                Log.i("TAG", "Result" + resultCode);
                Toast.makeText(this, "No elegiste ninguna foto", Toast.LENGTH_SHORT).show();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

//        if (resultCode == RESULT_OK) {
//            Uri path = data.getData();
//            String url = path.getPath();
//            System.out.println("PATH -> " + path);
//            url = url.substring(6);
//            System.out.println("URL -> " + url);
//
//            byte[] buffer = new byte[1024];
//            try {
//                InputStream fuente = new BufferedInputStream(new FileInputStream("/storage/emulated/0/DCIM/Camera/IMG_20210118_184637.jpg"));
//                OutputStream destino = new FileOutputStream("file.jpg");
//                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
//                (Marco).setImageBitmap(bitmap);
//                // destino.write(i);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // /storage/emulated/0/DCIM/Camera/IMG_20210118_184637.jpg
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnimg_id) {
            cargarBlankFrag(R.id.frg_Content);
            cargarimgChatFrag(R.id.frg_Content);
        } else if (item.getItemId() == R.id.mnmsj_id) {
//            borrarfrag(R.id.frg_Content);
            cargarBlankFrag(R.id.frg_Content);
            cargartxtChatFrag(R.id.frg_Content);
        } else if (item.getItemId() == R.id.mnsalir_id) {
            dialogSalir();
        } else {
            return super.onContextItemSelected(item);
        }
        return true;
    }

    void cargarimgChatFrag(int Frag) {
        Fragment fragmentoSeleccionado = new frg_imgChat();
        cargarfrag(fragmentoSeleccionado, Frag);
    }

    void cargartxtChatFrag(int Frag) {
        Fragment fragmentoSeleccionado = new frg_txtChat();
        cargarfrag(fragmentoSeleccionado, Frag);
    }

    void cargarBlankFrag(int Frag) {
        Fragment fragmentoSeleccionado = new BlankFragment();
        cargarfrag(fragmentoSeleccionado, Frag);
    }

    public void cargarfrag(Fragment frgseleccionado, int frgDestino) {
        //R.id.frg_Content
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frgDestino, frgseleccionado);
        transaction.commit();
    }

    void dialogSalir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea salir de la apliación?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    void dialogtxtImg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hola Servidor ¿Que vas a recibir?");
        builder.setPositiveButton("TEXTO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cargarBlankFrag(R.id.frg_Content);
                cargartxtChatFrag(R.id.frg_Content);
                dialog.dismiss();
            }
        }).setNegativeButton("IMAGENES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cargarBlankFrag(R.id.frg_Content);
                cargarimgChatFrag(R.id.frg_Content);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void onClick(@NotNull View view) throws IOException {

        switch (view.getId()) {
            case R.id.btnSendTxt:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EditText editText = findViewById(R.id.msg_id);
                        //mando lo escrito a la clase
                        System.out.println(editText.getText().toString());
                        Cliente cliente = new Cliente(MainActivity.this, editText.getText().toString());
                        cliente.run();
                        //luego de salir y enviar los datos al servidor
                        //modifico la vista

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String message = editText.getText().toString();
                                TextView TxtMensaje = new TextView(MainActivity.this);
                                TxtMensaje.setText(message);
                                TxtMensaje.setTextSize(15);
                                TxtMensaje.setGravity(Gravity.RIGHT);
                                LinearLayout _Chat = findViewById(R.id.chat_id);
                                _Chat.addView(TxtMensaje);
                                System.out.println("Cliente: " + message);
                            }
                        });
                    }
                }).start();
                break;
            case R.id.btnlimpiartxv_id:
                btnLimpiarChattxv = findViewById(R.id.btnlimpiartxv_id);
                btnLimpiarChattxv.setOnClickListener(v -> {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout textView = findViewById(R.id.chat_id);
                            textView.removeAllViewsInLayout();
                        }
                    }).start();
                });
                break;
            case R.id.btnSendImg:
                Marco = findViewById(R.id.imgchatid2);
                btnGallery = findViewById(R.id.btnSendTxt);
                btnGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // tu acción
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //pido los permisos en tiempo de ejecucion
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) { //aca sabemos si los permisos estan habilitados o no
                                abrirGaleria();
                            } else {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
                            }
                        } else {
                            abrirGaleria(); //abrimos la galeria porque damos por sentado que los permisos ya fueron otorgados
                        }
                    }
                });
            default:
                GlobalInfo.Rol = 2;
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}