#include <PHYSIs_Master.h>

PHYSIs_WiFi physisWiFi;

#define RELAY_2 2
#define RELAY_3 3
#define SOUND   A0
#define BUZZER  5


const String WIFI_SSID     = "U+Net4C63";               // WiFi 명
const String WIFI_PWD      = "6000214821";          // WiFi 비밀번호

const String SERIAL_NUMBER = "123412341234";      // PHYSIs KIT 시리얼번호
const String PUB_TOPIC     = "SoundState";             // Subscribe Topic


int decibel;
int standardDecibel;

long effectTimeout = 10000;
long effectTime;

long beamInterval = 30000;
long beamTime;

long initNoiseTime = 0;
long initNoiseLimit = 60000;

int noiseRange = 500;
int noiseCount = 0;
bool isPushMsg = false;
bool isBeam = false;

void setup() {
  Serial.begin(9600);

  pinMode(RELAY_2, OUTPUT);
  pinMode(RELAY_3, OUTPUT);
  pinMode(SOUND, INPUT);
  pinMode(BUZZER, OUTPUT);

  analogWrite(BUZZER, 0);

  standardDecibel = analogRead(SOUND) + noiseRange;
  Serial.print(F("# Standard Decibel : "));
  Serial.println(standardDecibel);

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
  decibel = analogRead(SOUND);

  if (initNoiseTime != 0 && millis() - initNoiseTime > initNoiseLimit) {
    noiseCount = 0;
  }

  if (decibel > standardDecibel) {
    noiseCount ++;
    Serial.print(F("# Noise Count : "));
    Serial.println(noiseCount);
    if (noiseCount == 1) {
      initNoiseTime = millis();
    } else if (noiseCount == 3) {
      isPushMsg = true;
      initNoiseTime = noiseCount = 0;
    }
    digitalWrite(RELAY_2, LOW);
    digitalWrite(RELAY_3, HIGH);
    publishNoiseInfo();
    outputBuzzer();
  } else {
    if (millis() - beamTime > beamInterval) {
      beamTime = millis();
      digitalWrite(RELAY_3, LOW);
      isBeam = !isBeam;
      if (isBeam) {
        digitalWrite(RELAY_2, HIGH);
      } else {
        digitalWrite(RELAY_2, LOW);
      }
    }
  }

  physisWiFi.startReceiveMsg();
}

void publishNoiseInfo() {
  String msg = String(isPushMsg) + String(decibel);
  Serial.print(F("# Publish Msg : "));
  Serial.println(msg);
  physisWiFi.publish(SERIAL_NUMBER, PUB_TOPIC, msg);
  isPushMsg = false;
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
