package com.example.hampo.presentacion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.example.hampo.R;
import com.example.hampo.casos_uso.CasosUsoMQTT;
import com.example.hampo.ui.MiPerfilFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class MiHampo extends AppCompatActivity {

    ImageView imagenHampo;
    Uri uriUltimaFoto;
    Button mqttBtn;
    CasosUsoMQTT mqtt = new CasosUsoMQTT();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mi_hampo);
/*
        mqttBtn = findViewById(R.id.sendMqttBtn);
        mqtt.crearConexionMQTT("123");

        findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);

        imagenHampo = findViewById(R.id.imagenHampo);

        //Cuando clica sobre el frameLayout para añadir una imagen
        findViewById(R.id.frameLayout1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.VISIBLE);
                findViewById(R.id.toggle_layout).setVisibility(View.GONE);
            }
        });

        //Cuando clica sobre la imagen de galeria
        findViewById(R.id.cardGaleria).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
                findViewById(R.id.toggle_layout).setVisibility(View.VISIBLE);
                ponerDeGaleria(1);
            }
        });

        //Cuando clica sobre la imagen de la camara
        findViewById(R.id.cardCamara).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
                findViewById(R.id.toggle_layout).setVisibility(View.VISIBLE);
                uriUltimaFoto = manejadorFotoHampo();
            }
        });

        mqttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqtt.enviarMensajeMQTT("ON", "luz/casa");
            }
        });
*/
    }


    public void visualizarFoto(ImageView imageView, String uri) {
        imageView.setImageBitmap(reduceBitmap(this, uri, 2048, 2048));
    }


    private Uri manejadorFotoHampo() {
        return tomarFoto(2);
    }


    private Bitmap reduceBitmap(Context contexto, String uri,
                                int maxAncho, int maxAlto) {
        try {
            InputStream input = null;
            Uri u = Uri.parse(uri);
            if (u.getScheme().equals("http") || u.getScheme().equals("https")) {
                input = new URL(uri).openStream();
            } else {
                input = contexto.getContentResolver().openInputStream(u);
            }
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = (int) Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(input, null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(contexto, "Fichero/recurso de imagen no encontrado",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Toast.makeText(contexto, "Error accediendo a imagen: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }

    public Uri tomarFoto(int codigoSolicitud) {
        try {
            Uri uriUltimaFoto;
            File file = File.createTempFile(
                    "img_" + (System.currentTimeMillis() / 1000), ".jpg",
                    this.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            if (Build.VERSION.SDK_INT >= 24) {
                uriUltimaFoto = FileProvider.getUriForFile(
                        this, "com.example.hampo", file);
            } else {
                uriUltimaFoto = Uri.fromFile(file);
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriUltimaFoto);
            this.startActivityForResult(intent, codigoSolicitud);
            return uriUltimaFoto;
        } catch (IOException ex) {
            Toast.makeText(this, "Error al crear fichero de imagen",
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    // FOTOGRAFÍAS
    public void ponerDeGaleria(int codigoSolicitud) {
        String action;
        if (Build.VERSION.SDK_INT >= 19) { // API 19 - Kitkat
            action = Intent.ACTION_OPEN_DOCUMENT;
        } else {
            action = Intent.ACTION_PICK;
        }
        Intent intent = new Intent(action,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, codigoSolicitud);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                imagenHampo.setImageURI(Uri.parse(data.getDataString()));
            } else {
                Toast.makeText(this, "Foto no cargada", Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == 2) {

            if (resultCode == Activity.RESULT_OK) {
                visualizarFoto(imagenHampo, uriUltimaFoto.toString());
            } else {
                //visualizarFoto(imagenHampo, lastUri);

                Toast.makeText(this, "Foto no cargada", Toast.LENGTH_LONG).show();
            }

            if (uriUltimaFoto != null) {
                visualizarFoto(imagenHampo, uriUltimaFoto.toString());
            } else {
                Toast.makeText(this, "Error en captura", Toast.LENGTH_LONG).show();
            }
        }

        // para poner galeria imagenHampo.setImageURI(Uri.parse(data.getDataString()));
        //Log.e(Mqtt.TAG, "uri: " + data.toString());
        //Log.e(Mqtt.TAG, "uri: " + data.toString());
        //Log.e(Mqtt.TAG, "uri: " + data.toString());
        //visualizarFoto(imagenHampo,uriUltimaFoto.toString());
        //imagenHampo.setImageURI(uriUltimaFoto);
        /*if (requestCode == RESULTADO_GALERIA) {
            if (resultCode == Activity.RESULT_OK) {
                ponerDeGaleria(pos, data.getDataString(), foto);
            } else {
                Toast.makeText(this, "Foto no cargada",Toast.LENGTH_LONG).show();
            }
        }*/
    }
}
