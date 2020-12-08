package com.example.hampo.presentacion;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.hampo.Aplicacion;
import com.example.hampo.R;
import com.example.hampo.ServicioMusica;
import com.example.hampo.casos_uso.CasosUsoActividades;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private AppBarConfiguration mAppBarConfiguration;

    MediaPlayer mp;

    private CasosUsoActividades usoActividades;

    private SharedPreferences pref;

    NfcAdapter nfcAdapter;
    public String mensaje;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
         db = FirebaseFirestore.getInstance();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //preferencias

        if(pref.getBoolean("tema", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else if(!pref.getBoolean("tema", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Musica

        if(pref.getBoolean("musica", true))
            startService(new Intent(MainActivity.this,
                    ServicioMusica.class));
        else if(!pref.getBoolean("musica", true))
            stopService(new Intent(MainActivity.this,
                    ServicioMusica.class));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_mis_hampos, R.id.nav_mi_perfil, R.id.nav_config, R.id.nav_adiestramiento, R.id.nav_FAQ)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            lanzarPreferencias(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void lanzarPreferencias(View v){
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivity(i);
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void cerrarSesion(View v) {
        if (v.getId() == R.id.btn_cerrar_sesion) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, CustomLoginActivity.class));
                            finish();
                        }
                    });
        }
    }

    @Override protected void onStart(){
        super.onStart();
    }
    @Override
    protected  void onResume(){
        super.onResume();


       enableForegroundDispatchSystem();
    }

    @Override
    protected  void onPause(){
        super.onPause();

        disableForegroundDispatchSystem();
    }
    @Override protected void onStop() {
        super.onStop();
    }
    @Override protected void onRestart() {
        super.onRestart();
    }
    @Override protected void onDestroy() {
        super.onDestroy();
    }

   @Override
    protected  void onNewIntent(final Intent intent){
        super.onNewIntent(intent);

        if(intent.hasExtra(nfcAdapter.EXTRA_TAG))
        {



            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);


            if(parcelables != null && parcelables.length > 0)
            {
                readTextFromMessage((NdefMessage) parcelables[0]);
                Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                DocumentReference existe = db.collection(Aplicacion.getId()).document(mensaje);
                existe.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                Intent i = new Intent(MainActivity.this, MiHampo.class);
                                i.putExtra("id", mensaje);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(MainActivity.this, CreateHampoActivity.class);
                                i.putExtra("id", mensaje);
                                startActivity(i);
                            }
                        } else {

                        }
                    }
                });


            }else{
                Toast.makeText(MainActivity.this, "No NDEF messages found!", Toast.LENGTH_SHORT).show();
            }





        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {

        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length>0){

            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getTextFromNdefRecord(ndefRecord);


            mensaje = tagContent;


        }else
        {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }

    }

    private void enableForegroundDispatchSystem(){

        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);


    }

    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage){
        try{

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if(ndefFormatable == null){
                Toast.makeText(this, "Tag is not ndef formatable", Toast.LENGTH_SHORT).show();
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
        }catch (Exception e){
            Log.e("FormatTag", e.getMessage());

        }

    }






    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);

        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

}