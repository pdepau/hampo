package org.example.androidthingsraspberry;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.comun.MQTT;

import java.io.IOException;

import static org.example.comun.MQTT.topicRoot;


public class MainActivity extends AppCompatActivity implements MqttCallback {
    private static final String TAG = "coso";
    private UartDevice uart;
    private TextView texto;
    public static MqttClient client = null;
    String aux="";
    Lectura lectura;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        texto = findViewById(R.id.textView);

        try {
            uart = PeripheralManager.getInstance().openUartDevice("UART0");
            uart.setBaudrate(9600);
            uart.setDataSize(8);
            uart.setParity(UartDevice.PARITY_NONE);
            uart.setStopBits(1);
        } catch (IOException e) {
            Log.w(TAG, "Error iniciando UART", e);
        }

        UartDeviceCallback uartCallback = new UartDeviceCallback() {
            @Override
            public boolean onUartDeviceDataAvailable(UartDevice uart) {
                // Read available data from the UART device
                String mensaje = leer();

                //Log.d(TAG, "Read " + mensaje);
                //texto.setText(mensaje);
                // Continue listening for more interrupts
                if(mensaje.contains("#")){
                    aux+=mensaje;
                    String aux2 = aux.split("#")[0];
                    texto.setText(aux2);
                    lectura = new Lectura(aux2.split("\"")[3],aux2.split("\"")[7],aux2.split("\"")[11],aux2.split("\"")[15],System.currentTimeMillis());

                    Log.w(TAG, "Lectura : " + lectura.toString());
                    aux="";
                }else{
                    aux+=mensaje;
                }

                return true;
            }

            @Override
            public void onUartDeviceError(UartDevice uart, int error) {
                Log.w(TAG, uart + ": Error event " + error);
            }
        };

        try {
            uart.registerUartDeviceCallback(uartCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
        crearConexionMQTT();
        escucharDeTopicMQTT("hampo/texto");
    }

    public String leer() {
        String s = "";
        int len;
        final int maxCount = 16; // Máximo de datos leídos cada vez
        byte[] buffer = new byte[maxCount];
        try {
            do {
                len = uart.read(buffer, buffer.length);
                for (int i = 0; i < len; i++) {
                    s += (char) buffer[i];
                }
            } while (len > 0);

        } catch (IOException e) {
            Log.w(TAG, "Error al leer de UART", e);
        }
        return s;
    }

    public void escribir(String s) {
        try {
            int escritos = uart.write(s.getBytes(), s.length());
            Log.d(TAG, escritos + " bytes escritos en UART");
        } catch (IOException e) {
            Log.w(TAG, "Error al escribir en UART", e);
        }
    }

    public void boton(View view) {
        escribir("H");
        Log.i(TAG, leer());
    }

    public void boton2(View view) {
        escribir("L");
        Log.i(TAG, leer());
    }

    public void crearConexionMQTT() {
        try {
            Log.i(TAG, "Conectando al broker " + MQTT.broker);
            client = new MqttClient(MQTT.broker, MQTT.clientId,
                    new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill(topicRoot + "WillTopic", "App desconectada".getBytes(), MQTT.qos, false);
            client.connect(connOpts);
        } catch (MqttException e) {
            Log.e(TAG, "Error al conectar.", e);
        }
    }

    public void escucharDeTopicMQTT(String subTopic) {
        try {
            Log.e(MQTT.TAG, "Suscrito a " + subTopic);
            client.subscribe(subTopic, MQTT.qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(MQTT.TAG, "Error al suscribir.", e);
        }
    }




    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.e(TAG, "message arrived");
        try {
            String payload = new String(message.getPayload());
            Log.d(TAG, "Recibiendo: " + topic + "->" + payload);
            if (topic.equalsIgnoreCase("hampo/luz/on")) {
                Log.d(TAG, "dentro luz");
                escribir("H");
            } else  if (topic.equalsIgnoreCase("hampo/luz/off")){
                Log.d(TAG, "fuera luz");
                escribir("L");
            }

        } catch (Exception e) {
            Log.d(MQTT.TAG, "Error en message arrived callback: " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}