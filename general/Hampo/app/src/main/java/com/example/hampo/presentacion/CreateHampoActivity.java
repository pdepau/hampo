package com.example.hampo.presentacion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.example.hampo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CreateHampoActivity extends AppCompatActivity {

    private ImageView imagenHampo;
    private Uri uriUltimaFoto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_hampo);
        findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
        imagenHampo = findViewById(R.id.imagenHampo);
        //Cuando clica sobre el frameLayout para añadir una imagen
        findViewById(R.id.frameLayout1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.VISIBLE);

            }
        });
        //Cuando clica sobre la imagen de galeria
        findViewById(R.id.cardGaleria).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
                ponerDeGaleria(1);
            }
        });

        //Cuando clica sobre la imagen de la camara
        findViewById(R.id.cardCamara).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.cardSelectPicOptions).setVisibility(View.INVISIBLE);
                uriUltimaFoto = manejadorFotoHampo();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.arrayRazasHamster, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        final TextView textViewComida = findViewById(R.id.textViewComida);
        final TextView textViewBebida = findViewById(R.id.textViewBebida);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                switch (position) {
                    case 0:
                        textViewComida.setText("🍖 400 gr/dia");
                        textViewBebida.setText("💧 350 ml/dia");
                        break;
                    case 1:
                        textViewComida.setText("🍖 200 gr/dia");
                        textViewBebida.setText("💧 50 ml/dia");
                        break;
                    case 2:
                        textViewComida.setText("🍖 300 gr/dia");
                        textViewBebida.setText("💧 150 ml/dia");
                        break;
                    case 3:
                        textViewComida.setText("🍖 450 gr/dia");
                        textViewBebida.setText("💧 350 ml/dia");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        findViewById(R.id.femaleCard).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                femaleOptionSelected(v);
            }
        });
        findViewById(R.id.maleCard).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                maleOptionSelected(v);
            }
        });
        /*mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_mis_hampos, R.id.nav_mi_perfil, R.id.nav_config, R.id.nav_tinder, R.id.nav_FAQ)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
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

    public void femaleOptionSelected(View view) {
        CardView maleCard = findViewById(R.id.maleCard);
        maleCard.setCardBackgroundColor(12);
        CardView femaleCard = findViewById(R.id.femaleCard);
        femaleCard.setCardBackgroundColor(getResources().getColor(R.color.femaleColor));
    }

    public void maleOptionSelected(View view) {
        CardView femaleCard = findViewById(R.id.femaleCard);
        femaleCard.setCardBackgroundColor(12);
        CardView maleCard = findViewById(R.id.maleCard);
        maleCard.setCardBackgroundColor(getResources().getColor(R.color.maleColor));
    }

    public void returnBtnFn(View view) {
        finish();
    }

    public void createBtnFn(View view) {

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
            //Log.e(Mqtt.TAG, "uri: " + uriUltimaFoto.toString());
            visualizarFoto(imagenHampo, uriUltimaFoto.toString());
            /*
            if (uriUltimaFoto != null) {
                visualizarFoto(imagenHampo, uriUltimaFoto.toString());
            } else {
                Toast.makeText(this, "Error en captura", Toast.LENGTH_LONG).show();
            }*/
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
