#include <PHYSIs_Master.h>

PHYSIs_WiFi physisWiFi;

#define RELAY_2 2
#define RELAY_3 3
#define SOUND   4
#define BUZZER  5


const String WIFI_SSID     = "U+Net4C63";               // WiFi 명
const String WIFI_PWD      = "6000214821";          // WiFi 비밀번호

const String SERIAL_NUMBER = "123412341234";      // PHYSIs KIT 시리얼번호
const String PUB_TOPIC     = "SoundState";             // Subscribe Topic


int soundDetect;
long effectTimeout = 10000;
long effectTime;

long publishTime;
long publishInterval = 250;

void setup() {
  Serial.begin(9600);

  pinMode(RELAY_2, OUTPUT);
  pinMode(RELAY_3, OUTPUT);
  pinMode(SOUND, INPUT);
  pinMode(BUZZER, OUTPUT);

  analogWrite(BUZZER, 0);

  physisWiFi.enable();

  physisWiFi.connectWiFi(WIFI_SSID, WIFI_PWD);
  Serial.print("# WiFi Connecting..");
  delay(1000);
  while (!physisWiFi.isWiFiStatus()) {
    Serial.print(".");
    delay(1000);
  }
  Serial.println(F("Connected..."));

  Serial.print("# MQTT Connecting..");
  if (physisWiFi.connectMQTT()) {                  // PHYSIs 플랫폼의 MQTT Broker와 연결
    Serial.println("Success...");
  } else {
    Serial.print("Fail...");
  }
}

void loop() {
  soundDetect = digitalRead(SOUND);;

  if (millis() - publishTime > publishInterval) {
    physisWiFi.startReceiveMsg();
    publishState();
  }
  if (soundDetect) {
    digitalWrite(RELAY_2, LOW);
    digitalWrite(RELAY_3, HIGH);
    publishState();
    outputBuzzer();
  } else {
    digitalWrite(RELAY_3, LOW);
    digitalWrite(RELAY_2, HIGH);
  }
}

void publishState() {
  Serial.print(F("# Sound State : "));
  Serial.println(soundDetect);
  physisWiFi.publish(SERIAL_NUMBER, PUB_TOPIC, String(soundDetect));
  publishTime = millis();
}

void outputBuzzer() {
  effectTime = millis();
  Serial.println(F("# Start Effect."));
  while (millis() - effectTime < effectTimeout) {
    analogWrite(BUZZER, 255);
    delay(250);
    analogWrite(BUZZER, 0);
    delay(250);
  }
  Serial.println(F("# Stop Effect."));
  analogWrite(BUZZER, 0);
}
