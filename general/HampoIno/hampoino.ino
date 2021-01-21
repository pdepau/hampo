#include <M5Stack.h>
#include <analogWrite.h>

#include <DHT.h>
#include <DHT_U.h>

#define TIMER_DIVIDER   80
#define TIMER_SCALE     (TIMER_BASE_CLK / TIMER_DIVIDER) // 80e6/TIMER_DIVIDER
#define TIMER0_INTERVAL0_SEC   (1) // segundos
#define ESP_INTR_FLAG_TIMER0
#define BUF_SIZE 1024

#define DHTTYPE DHT11   // DHT 11
#define LedPin  3     // what digital pin we're connected to
#define DHTPin  5     // what digital pin we're connected to
#define LumPin  35     // what digital pin we're connected to

const int Trigger = 25;   //Pin digital 2 para el Trigger del sensor
const int Echo = 26;   //Pin digital 3 para el Echo del sensor
const int HyT = 5;   //Pin digital 3 para el Echo del sensor

//--------------------------------------------------
//luz
float bufferTemp[100], bufferHum[100], bufferLum[100];
int cont;
DHT dht(DHTPin, DHTTYPE);
float mediaTemp = 0, mediaHum = 0, mediaLum = 0;
unsigned int SumatorioLum = 0, maximoLum = 0, minimoLum = 4095, SumatorioTemp = 0, SumatorioHum = 0,  maximoHum = 0,  minimoHum = 4095 ;
float  maximoTemp = 0,  minimoTemp = 4095;

float media_lum() {
  SumatorioLum = 0;

  if (bufferLum[cont] < minimoLum ) minimoLum = bufferLum[cont];  //Da el minimo
  if (bufferLum[cont] > maximoLum ) maximoLum = bufferLum[cont];  //Da el maximo
  for (int i = 0; i < 100; i++) {
    SumatorioLum += bufferLum[i];
  }
  float mediaLum = SumatorioLum / 100;
  return mediaLum;
}

float media_temp() {
  SumatorioTemp = 0;

  if (bufferTemp[cont] < minimoTemp ) minimoTemp = bufferTemp[cont];  //Da el minimo
  if (bufferTemp[cont] > maximoTemp ) maximoTemp = bufferTemp[cont];  //Da el maximo
  for (int i = 0; i < 100; i++) {
    SumatorioTemp += bufferTemp[i];
  }
  float mediaTemp = SumatorioTemp / 100;
  return mediaTemp;
}
float media_hum() {
  SumatorioHum = 0;

  if (bufferHum[cont] > maximoHum ) maximoHum = bufferHum[cont];  // Da el maximo
  if (bufferHum[cont] < minimoHum ) minimoHum = bufferHum[cont];  // Da el minimo
  for (int i = 0; i < 100; i++) {
    SumatorioHum += bufferHum[i];
  }
  int mediaHum = SumatorioHum / 100;
  return mediaHum;
}
//no mas luz
//--------------------------------------------------





volatile int Flag_ISR_Timer0 = 0;
hw_timer_t * timer0 = NULL;

void IRAM_ATTR ISR_Timer0() {
  Flag_ISR_Timer0 = 1; // Activacion de Timer
}

void TimerConfig() {
  // Configuracion Timer0
  timer0 = timerBegin(0, TIMER_DIVIDER, true); // = 1uS
  timerAttachInterrupt(timer0, &ISR_Timer0, true);
  timerAlarmWrite(timer0, 2000000, true); //cada 1s
  timerAlarmEnable(timer0);
}



void luz() {
  if (cont == 100) {
    cont = 0;
  }
  //Si no realiza bien la lectura
  if (isnan(dht.readTemperature()) or isnan(dht.readHumidity())) {
    Serial.println("ERROR AL LEER SENSOR HUM/TEMP");
  } else {
    bufferTemp[cont] = dht.readTemperature(); // Buffer con datos de lectura Temperatura
    bufferHum[cont] = dht.readHumidity(); // Buffer con datos de lectura Humedad
  }
  bufferLum[cont] = map(analogRead(LumPin), 0, 4095, 100, 0); // Buffer con datos de lectura Humedad
  mediaTemp = media_temp();
  mediaHum = media_hum();
  mediaLum = media_lum();
  Serial.print(analogRead(LumPin));
  Serial.print(" ");
  Serial.print(dht.readHumidity());
  Serial.print(" ");
  Serial.println(dht.readTemperature());
  Serial.print("H Media: ");
  Serial.print(mediaHum);
  Serial.print(" T Media: ");
  Serial.println(mediaTemp);
  cont++;
  delay(100);
}



//JSON
//--------------------------------------------------
float Enviar[7] = {};

String datas[7] = {};
void sendAndroidValues()
{
  datas[0] = "Temperatura";
  datas[1] = "Humedad";
  datas[2] = "Bebedero";
  datas[3] = "Luminosidad";

  Enviar[0] = mediaTemp;
  Enviar[1] = mediaHum;
  Enviar[2] = bebedero();
  Enviar[3] = mediaLum;

  Serial2.print('{');//hay que poner { para el comienzo de los datos, así Android sabe que empieza el String de datos
  for (int k = 0; k < 4; k++)
  {
    Serial2.print("\"");
    Serial2.print(datas[k]);
    Serial2.print("\"");
    Serial2.print(":");
    Serial2.print("\"");
    Serial2.print(Enviar[k]);
    Serial2.print("\"");
    if ( k != 3) {
      Serial2.print(',');            //separamos los datos con la ,
    }
  }
  Serial2.print('}');               //con esto damos a conocer la finalización del String de datos
  Serial2.print("##");               //con esto damos a conocer la finalización del String de datos

  //Serial.print("{\"Temperatura\":\""mediaTemp"\",\"height\":\"4\"}");
  //delay(200);                       //IMPORTANTE CADA x ms envia si es muy bajo no recibe la app
}
//--------------------------------------------------
//no mas JSON


long bebedero() {
  long t; //timepo que demora en llegar el eco
  long d; //distancia en centimetros

  luz();

  digitalWrite(Trigger, HIGH);
  delayMicroseconds(10);          //Enviamos un pulso de 10us
  digitalWrite(Trigger, LOW);

  t = pulseIn(Echo, HIGH); //obtenemos el ancho del pulso
  d = t / 59;           //escalamos el tiempo a una distancia en cm
  Serial.print(d);
  Serial.println();
  return d;
}

void setup () {
  Serial.begin(115200);
  Serial2.begin(9600); //Inicializo el puerto serial a 9600 baudios
  dht.begin();
  pinMode(LedPin, OUTPUT);
  pinMode(HyT, INPUT);
  pinMode(Trigger, OUTPUT); //pin como salida
  pinMode(Echo, INPUT);  //pin como entrada
  digitalWrite(Trigger, LOW);//Inicializamos el pin con 0
  TimerConfig();

  cont = 0;
  for (int i = 0; i < 100; i++) {
    bufferTemp[i] = dht.readTemperature(); // Buffer con datos de lectura Temperatura
    bufferHum[i] = dht.readHumidity(); // Buffer con datos de lectura Humedad
    bufferLum[i] = map(analogRead(LumPin), 0, 4095, 100, 0); // Buffer con datos de lectura Humedad
  }
}
void loop () {

  if (Flag_ISR_Timer0) { // SI ESTA ACTIVADO EL FLAG TIMER
    Flag_ISR_Timer0 = 0; // DESACTIVAR EL FLAG
    luz();
    bebedero();
    sendAndroidValues();
  }

  if (Serial2.available()) { //Si está disponible
    char c = Serial2.read(); //Guardamos la lectura en una variable char
    if (c == 'H') { //Si es una 'H', enciendo el LED
      digitalWrite(LedPin, HIGH);
      Serial.print("on");
    } else if (c == 'L') { //Si es una 'L', apago el LED
      digitalWrite(LedPin, LOW);
      Serial.print("of");
    } else {
      Serial2.println("kk");
      
    }
  }

}
