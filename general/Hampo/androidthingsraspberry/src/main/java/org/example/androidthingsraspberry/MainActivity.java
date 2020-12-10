package org.example.androidthingsraspberry;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.bilal.androidthingscameralib.InitializeCamera;
import com.bilal.androidthingscameralib.OnPictureAvailableListener;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class MainActivity extends Activity implements MqttCallback, OnPictureAvailableListener {
    // UART Device Name
    private static final String UART_DEVICE_NAME = "UART0";
    public UartDevice mDevice;
    public static MqttClient client = null;
    ArduinoUART UART;
    String s;
    TextView textViewUart;
    int dataSize = 8;
    String dataRAW = "";
    boolean fullDataReaded;
    private FirebaseDataController firebaseDb;
    private InitializeCamera mInitializeCamera;


    /// ==================================== ACTIVITY ==============================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /// ====================================
        /// ====================================
        firebaseDb = new FirebaseDataController();
        UART = new ArduinoUART("UART0",9600);
        Log.e("TEST","ANTES DE crearConexionMQTT()");
        crearConexionMQTT();
        //escucharDeTopicMQTT("luzhampo/on");
        escucharDeTopicMQTT("luzhampo/off");
        escucharDeTopicMQTT("luzhampo/top/on");
        escucharDeTopicMQTT("luzhampo/down/on");
        escucharDeTopicMQTT("luzhampo/both/on");
        escucharDeTopicMQTT("luzhampo/both/off");
        escucharDeTopicMQTT("luzhampo/alimentar");
        escucharDeTopicMQTT("luzhampo/uv/on");
        escucharDeTopicMQTT("luzhampo/uv/off");
        escucharDeTopicMQTT("luzhampo/adiestramiento");

        //UART = new ArduinoUART("UART0", 9600);
        textViewUart = findViewById(R.id.textView1);
        try {
            //mDevice = PeripheralManager.getInstance().openUartDevice(UART_DEVICE_NAME);
            mDevice = PeripheralManager.getInstance().openUartDevice(UART_DEVICE_NAME);
            configureUartFrame(mDevice);
            uartCallback.onUartDeviceDataAvailable(mDevice);
            writeUartData(mDevice);
            //Log.e("UART", "DataRAW = " + dataRAW);
        } catch (IOException e) {
            Log.w("UART", "Error en openUartDevice: ", e);
        }
    }

    public void writeUartData(UartDevice uart) throws IOException {
        String str = "k";
        byte[] buffer = str.getBytes();
        int count = uart.write(buffer, buffer.length);
        Log.d("UART", "Wrote " + count + " bytes to peripheral");
    }
/*
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
*/
    @Override
    protected void onStop() {
        super.onStop();
        // Interrupt events no longer necessary
        mDevice.unregisterUartDeviceCallback(uartCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Make sure to release the camera resources.
        if (mInitializeCamera != null) {
            mInitializeCamera.releaseCameraResources();
        }
        if (mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                Log.w("UART", "Unable to close UART device", e);
            }
        }
    }

    /// =========================================MQTT==========================================================
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
            Log.e(MQTT.TAG, "Suscrito a " + subTopic);
            client.subscribe(subTopic, MQTT.qos);
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
        Log.e("mqttDebug", "message arrived");
        try {
            String payload = new String(message.getPayload());
            Log.d(MQTT.TAG, "Recibiendo: " + topic + "->" + payload);
            if (topic.equalsIgnoreCase("luzhampo/top/on")) {
                Log.d(MQTT.TAG, "dentro top/on");
                UART.escribir("tt");
                //UART.escribir("hh");
            } else if (topic.equalsIgnoreCase("luzhampo/down/on")) {
                Log.d(MQTT.TAG, "dentro down/on");
                UART.escribir("ss");
                //UART.escribir('d');
            } else if (topic.equalsIgnoreCase("luzhampo/alimentar")) {
                Log.d(MQTT.TAG, "dentro alimentar");
                UART.escribir("hh");
                //UART.escribir('b');
            } else if (topic.equalsIgnoreCase("luzhampo/adiestramiento")) {
                Log.d(MQTT.TAG, "dentro adiestramiento");
                UART.escribir("aa");
                //UART.escribir('b');
            } else if (topic.equalsIgnoreCase("luzhampo/both/on")) {
                Log.d(MQTT.TAG, "dentro both/on");
                UART.escribir("pp");
                //UART.escribir('b');
            } else if (topic.equalsIgnoreCase("luzhampo/both/off")) {
                Log.d(MQTT.TAG, "dentro both/off");
                UART.escribir("qq");
                //UART.escribir('b');
            } else if (topic.equalsIgnoreCase("luzhampo/uv/on")) {
                Log.d(MQTT.TAG, "dentro uv/on");
                UART.escribir("gg");
                //UART.escribir('b');
            } else if (topic.equalsIgnoreCase("luzhampo/uv/off")) {
                Log.d(MQTT.TAG, "dentro uv/off");
                UART.escribir("ff");
                //UART.escribir('b');
            } else if (topic.equalsIgnoreCase("luzhampo/sendnudes")) {
                Log.d(MQTT.TAG, "nudes");
                UART.escribir("kk");
                //UART.escribir('b');
            }

            // Uv g on
             // UV f off
             // Alimentar h
             // Obtener valores sensores k
             // Adistramiento a
             // Ahorro energia m on
             // Ahorro energia n off
            //s = UART.leer();
            //Log.d(MQTT.TAG, "Recibido en uart: "+s);

            //textViewUart.setText(s);
        } catch (Exception e) {
            Log.d(MQTT.TAG, "Error en message arrived callback: " + e.getMessage());
        }
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(MQTT.TAG, "Entrega completa");
    }

    /// ============================ UART =========================================================
    public void configureUartFrame(UartDevice uart) throws IOException {
        // Configure the UART port
        uart.setBaudrate(9600);
        uart.setDataSize(dataSize);
        uart.setParity(UartDevice.PARITY_NONE);
        uart.setStopBits(1);
    }
    public void writeUartData(UartDevice uart) throws IOException {
        String str = "k";
        byte[] buffer = str.getBytes();
        int count = uart.write(buffer, buffer.length);
        Log.d("UART", "Wrote " + count + " bytes to peripheral");
    }
    private UartDeviceCallback uartCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            // Read available data from the UART device
            Log.e("UART", "uart data avaiable broooo");
            try {
                readUartBuffer(uart);
            } catch (IOException e) {
                Log.w("UART", "Unable to access UART device", e);
            }
            /* Despues de entrar all callback lee los datos y
             *  en este scope tenemos el string que recibimos por la uart
             * */
            if (fullDataReaded) {
                Log.e("UART", "DataRAW = " + dataRAW);
                Log.e("UART", "DataFormatted =" + formatToJSON(dataRAW));
                convertToJSON(formatToJSON(dataRAW));

            }
            // Continue listening for more interrupts
            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w("UART", uart + ": Error event " + error);
        }

    };

    public String formatToJSON(String dataToFormat) {
        String res = "";
        String itr;
        for (int i = 0; i < dataToFormat.length(); i++) {
            itr = Character.toString(dataToFormat.charAt(i));
            if (itr.matches("[a-zA-Z{}\":.,0-9]*")) {
                res += dataToFormat.charAt(i);
            }
        }
        return res;
    }

    public boolean checkFinalBit(String str) {
        String itr;
        int contador = 0;
        for (int i = 0; i < str.length(); i++) {
            itr = Character.toString(str.charAt(i));
            if (itr.matches("#")) {
                contador++;
            }
        }
        if (contador == 2) {
            return true;
        } else {
            return false;
        }
    }

    public void readUartBuffer(UartDevice uart) throws IOException {
        // Maximum amount of data to read at one time
        final int maxCount = dataSize;
        byte[] buffer = new byte[maxCount];

        int count;
        while ((count = uart.read(buffer, buffer.length)) > 0) {
            Log.e("UART", "Read " + count + " bytes from peripheral");
            String str = new String(buffer, StandardCharsets.UTF_8);
            if (checkFinalBit(str)) {
                dataRAW += formatToJSON(str);
                fullDataReaded = true;
            } else {
                dataRAW += str;
                fullDataReaded = false;
            }
            Log.e("UART", "String converted from buffer " + str + " buffer length = " + buffer.length);
        }
        //Log.e("UART", "DataRAW = " + dataRAW);
        //int str = uart.read(buffer,maxCount);
    }

    public void convertToJSON(String strToConvert) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SensorsData datos = objectMapper.readValue(strToConvert, SensorsData.class);
            Log.e("UART", "DATOS OBJETO = " + datos.toString());
            firebaseDb.sendSensorsData(datos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /// ================================ CAMARA =====================================================

    public void pruebaCamara(){
        //Recommended configurations IMAGE_WIDTH = 640px, IMAGE_HEIGHT = 480px, MAX_IMAGES = 1;
//initializeCamera(Current Context, Reference Of OnPictureAvailableListener, IMAGE_WIDTH, IMAGE_HEIGHT, MAX_IMAGES)
        mInitializeCamera = new InitializeCamera(this, this, 640, 480, 1);
        mInitializeCamera.captureImage();

    }
    public void onPictureAvailable(byte[] imageBytes) {
         // CUANDO LA IMAGEN ESTA DISPONIBLE....
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        
    }
    /// =====================================================================================

}