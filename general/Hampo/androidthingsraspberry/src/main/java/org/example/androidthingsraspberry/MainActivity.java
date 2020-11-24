package org.example.androidthingsraspberry;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.example.comun.MQTT;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.example.comun.MQTT.topicRoot;

public class MainActivity extends Activity implements MqttCallback {
    // UART Device Name
    private static final String UART_DEVICE_NAME = "UART0";
    public UartDevice mDevice;
    public static MqttClient client = null;
    ArduinoUART UART;
    String s;
    TextView textViewUart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crearConexionMQTT();
        escucharDeTopicMQTT("luz/casa");
        //UART = new ArduinoUART("UART0", 9600);
        textViewUart = findViewById(R.id.textView1);
        PeripheralManager manager = PeripheralManager.getInstance();
        try {
            mDevice = manager.openUartDevice(UART_DEVICE_NAME);
            configureUartFrame(mDevice);
            writeUartData(mDevice);
        } catch (IOException e) {
            Log.w("UART", "Error en openUartDevice: ", e);
        }
    }

    public void writeUartData(UartDevice uart) throws IOException {
        String str = "gg";
        byte[] buffer = str.getBytes();
        int count = uart.write(buffer, buffer.length);
        Log.d("UARt", "Wrote " + count + " bytes to peripheral");
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            // Begin listening for interrupt events
            mDevice.registerUartDeviceCallback(uartCallback);
        } catch (IOException e) {
            Log.w("UART", "Unable to access UART device", e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Interrupt events no longer necessary
        mDevice.unregisterUartDeviceCallback(uartCallback);
    }

    //Crear conexion con el broker MQTT
    public void crearConexionMQTT() {
        try {
            Log.i(MQTT.TAG, "Conectando al broker " + MQTT.broker);
            client = new MqttClient(MQTT.broker, MQTT.clientId,
                    new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill(topicRoot + "WillTopic", "App desconectada".getBytes(), MQTT.qos, false);
            client.connect(connOpts);
        } catch (MqttException e) {
            Log.e(MQTT.TAG, "Error al conectar.", e);
        }
    }

    //Publicar mensaje en un topic
    public void enviarMensajeMQTT(String data, String subTopic) {
        try {
            Log.i(MQTT.TAG, "Publicando mensaje: " + data);
            MqttMessage message = new MqttMessage(data.getBytes());
            message.setQos(MQTT.qos);
            message.setRetained(false);
            client.publish(topicRoot + subTopic, message);
        } catch (
                MqttException e) {
            Log.e(MQTT.TAG, "Error al publicar.", e);
        }
    }

    //Suscribirse a un topic
    public void escucharDeTopicMQTT(String subTopic) {
        try {
            Log.i(MQTT.TAG, "Suscrito a " + topicRoot + subTopic);
            client.subscribe(topicRoot + subTopic, MQTT.qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(MQTT.TAG, "Error al suscribir.", e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(MQTT.TAG, "Conexión perdida");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            Log.d(MQTT.TAG, "Recibiendo: " + topic + "->" + payload);
            if (payload.equalsIgnoreCase("ON")){
                Log.d(MQTT.TAG, "funcion callback");
                //UART.escribir("hh");
            }
            //s = UART.leer();
            //Log.d(MQTT.TAG, "Recibido en uart: "+s);

            //textViewUart.setText(s);
        } catch (Exception e){
            Log.d(MQTT.TAG, "Error en message arrived callback: "+e.getMessage());
        }
    }
    public void configureUartFrame(UartDevice uart) throws IOException {
        // Configure the UART port
        uart.setBaudrate(9600);
        uart.setDataSize(8);
        uart.setParity(UartDevice.PARITY_NONE);
        uart.setStopBits(1);
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(MQTT.TAG, "Entrega completa");
    }

    private UartDeviceCallback uartCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            // Read available data from the UART device
            try {
                readUartBuffer(uart);
            } catch (IOException e) {
                Log.w("UART", "Unable to access UART device", e);
            }

            // Continue listening for more interrupts
            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w("UART", uart + ": Error event " + error);
        }
    };
    public void readUartBuffer(UartDevice uart) throws IOException {
        // Maximum amount of data to read at one time
        final int maxCount = 8;
        byte[] buffer = new byte[maxCount];

        int count;
        while ((count = uart.read(buffer, buffer.length)) > 0) {
            Log.d("UART", "Read " + count + " bytes from peripheral");
        }
        String str = new String(buffer, StandardCharsets.UTF_8);

        Log.d("UART", "String converted from buffer " + str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                Log.w("UART", "Unable to close UART device", e);
            }
        }
    }
}