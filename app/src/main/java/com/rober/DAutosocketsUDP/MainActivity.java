package com.rober.DAutosocketsUDP;
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
import android.util.AttributeSet;
import android.util.Log;
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
import java.net.SocketException;
import java.text.SimpleDateFormat;

import static com.rober.DAutosocketsUDP.GlobalInfo.IPAddress;
import static com.rober.DAutosocketsUDP.GlobalInfo.PORT;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1777;
    private static final int REQUEST_IMAGE_GALLERY = 2777;
    private View datos;
    ImageView Marco;
    TextView titulo, txtfecha;
    Button btnCliente, btnServidor, btnGallery, btnLimpiarChattxv, btnSendTxt, btnSenImg;
//    Socket SocketCliente = new Socket(IPServidor, PORT);
//    Cliente cliente = new Cliente(SocketCliente);


    public MainActivity() throws IOException {

    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCliente = findViewById(R.id.btnCliente);
        btnServidor = findViewById(R.id.btnServidor);
        txtfecha = findViewById(R.id.txv_fecha_id);

        String strDateFormat = "dd/MM/yyyy"; // Especifico el formato de la fecha
        SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat); // La cadena de formato de fecha se pasa como un argumento al objeto
        txtfecha.setText(GlobalInfo.fechaActual()); // El formato de fecha se aplica a la fecha actual
        btnServidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción de Servidor
                GlobalInfo.Rol = 1;
//                dialogtxtImg();
                System.out.println(IPAddress);
                System.out.println(String.valueOf(PORT));
                titulo = findViewById(R.id.txv_clienteServidor_id);
                titulo.setText("Servidor UDP escuchando... :" + IPAddress);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ServidorUDP servidor = null;
                        try {
                            servidor = new ServidorUDP(PORT, MainActivity.this);
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                        servidor.run();
                    }
                }).start();
                System.out.println("Servidor..: " + IPAddress);
            }
        });
        btnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción de Cliente
                GlobalInfo.Rol = 2;
                titulo = findViewById(R.id.txv_clienteServidor_id);
                titulo.setText("Cliente ... " + IPAddress + " " + PORT);
            }
        });
    }

    @Override
    public boolean onPreparePanel(int featureId, @Nullable View view, @NonNull Menu menu) {
        MenuItem mnmsj = menu.findItem(R.id.mnmsj_id);
        MenuItem mnimg = menu.findItem(R.id.mnimg_id);
        if(GlobalInfo.Rol == 1) {
            mnmsj.setVisible(false);
            mnimg.setVisible(false);
        }
        else {
            mnmsj.setVisible(true);
            mnimg.setVisible(true);
        }
//        return true;
        return super.onPreparePanel(featureId, view, menu);
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
                GlobalInfo.URL_IMAGEN = imagen;
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
//opciones disponibles solo para el cliente excepto la opcion salir
            if (item.getItemId() == R.id.mnimg_id) {
                GlobalInfo.queEnvio = 2; //Bandera para saber que envio una imagen
                cargarimgChatFrag(R.id.frg_Content);
            } else if (item.getItemId() == R.id.mnmsj_id) {
                GlobalInfo.queEnvio = 1; //Bandera para saber que envio una imagen
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
                GlobalInfo.queEnvio = 1;
                cargartxtChatFrag(R.id.frg_Content);
                dialog.dismiss();
            }
        }).setNegativeButton("IMAGENES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GlobalInfo.queEnvio = 2;
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
                        String mensajeCliente = editText.getText().toString();
                        ClienteUDP cliente = new ClienteUDP(MainActivity.this, mensajeCliente);
                        cliente.run();
                    }
                }).start();
                break;
            case R.id.btnlimpiartxv_id:
                btnLimpiarChattxv = findViewById(R.id.btnlimpiartxv_id);
                btnLimpiarChattxv.setOnClickListener(v -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout textView = findViewById(R.id.chat_id);
                            textView.removeAllViewsInLayout();
                        }
                    });
                });
                break;
            default:
                GlobalInfo.Rol = 2;
                break;
        }

    }

    public void ClickBtnGaleria(View view) {
        Marco = findViewById(R.id.imgchatid2);
        btnGallery = findViewById(R.id.btnGallery);
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
    }

    public void EnviarImagen(View view) throws IOException {
        GlobalInfo.queEnvio = 2;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView imageView = findViewById(R.id.imageView2);
                //mando lo escrito a la clase
                Cliente cliente = null;
                try {
                    new Thread(new Cliente(MainActivity.this, GlobalInfo.URL_IMAGEN)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Dibujar la imagen en el imageview", Toast.LENGTH_SHORT).show();
                }
                //luego de salir y enviar los datos al servidor modifico la vista
            }
        });
    }
}


//            case R.id.btnGallery:
//                Marco = findViewById(R.id.imgchatid2);
//                btnGallery = findViewById(R.id.btnSendTxt);
//                btnGallery.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // tu acción
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //pido los permisos en tiempo de ejecucion
//                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) { //aca sabemos si los permisos estan habilitados o no
//                                abrirGaleria();
//                            } else {
//                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
//                            }
//                        } else {
//                            abrirGaleria(); //abrimos la galeria porque damos por sentado que los permisos ya fueron otorgados
//                        }
//                    }
//                });

                //if (GlobalInfo.URL_IMAGEN != null) {
                //Uri URL_FUENTE_GALLERY = GlobalInfo.URL_IMAGEN;
                ////            InputStream url_img = new BufferedInputStream(new FileInputStream(String.valueOf(URL_FUENTE_GALLERY)));
                //InputStream url_img = getContentResolver().openInputStream(URL_FUENTE_GALLERY);
                ////BufferedOutputStream fuente = new BufferedOutputStream(_SocketCliente.getOutputStream());
                ////            InputStream url_img2 = new BufferedInputStream(new FileInputStream(URL_FUENTE_GALLERY.getPath()));
                //int charsRead = 0;
                //int i = url_img.read();
                //
                //byte[] buffer = new byte[5*1024*1024];
                //
                //
                //while ((charsRead = url_img.read(buffer)) != -1) {
                ////buffer2 += buffer.substring(0, charsRead);
                //System.out.println(""+ charsRead);
                //System.out.println(String.valueOf(buffer.length));
                //Log.i("Cliente ", "enviando una imagen");
                //}
                //
                //if (url_img != null) {
                //url_img.close();
                //}
                //} else {
                //System.out.println("Elije una imagen de la galeria");
                //}
