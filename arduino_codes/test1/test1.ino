#include "EspMQTTClient.h"
#include <ESP8266WiFi.h>
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>  

void onConnectionEstablished();
void toggler(int);

const char* ssid     = "D-Link";
const char* password = "snp8191536";

WiFiServer server(80);
EspMQTTClient mqttClient(
  "Missing Scarlett",                 // Wifi ssid
  "123456789",                 // Wifi password
  onConnectionEstablished,// Connection established callback
  "smarthomeec2.ml",                   // MQTT broker ip
  1883,                   // MQTT broker port
  "ubuntu",              // MQTT username
  "ubuntu",             // MQTT password
  "homef1r0"                 // Client name
  /*true,                   // Enable web updater
  true                    // Enable debug messages*/
);

String outputStateArray[] = {
  "off", 
  "off", 
  "off", 
  "off", 
  "off", 
  "off", 
  "off", 
  "off"};
const int outputArray[] = {16, 5, 4, 0, 2, 14, 12, 13};
String topicArray[] = {
  "mumbai01/002/light00",
  "mumbai01/002/light01",
  "mumbai01/002/light02",
  "mumbai01/002/light03",
  "mumbai01/002/light04",
  "mumbai01/002/fan00",
  "mumbai01/002/fan01",
  "mumbai01/002/fan02",
  "mumbai01/002/ipaddress"
};

String header;
int i = 0;

void setup() {
  Serial.begin(115200);
  
  WiFiManager wifiManager;
  wifiManager.autoConnect("AutoConnectAP");
  Serial.println("connected...hooray :)");
  
  pinMode(16, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(0, OUTPUT);
  pinMode(2, OUTPUT);
  pinMode(14, OUTPUT);
  pinMode(12, OUTPUT);
  pinMode(13, OUTPUT);
  pinMode(15, OUTPUT);
  
  digitalWrite(16, HIGH);
  digitalWrite(5, HIGH);
  digitalWrite(4, HIGH);
  digitalWrite(0, HIGH);
  digitalWrite(2, HIGH);
  digitalWrite(14, HIGH);
  digitalWrite(12, HIGH);
  digitalWrite(13, HIGH);
  digitalWrite(15, HIGH);
  
  Serial.print("Connecting to ");
  Serial.println(ssid);
  if(WiFi.status()!= WL_CONNECTED){
    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.print(".");
    }
  }
  Serial.println("");
  Serial.println("WiFi connected.");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  server.begin();
}

void onConnectionEstablished(){
  mqttClient.subscribe(topicArray[0], [](const String & payload) {
    Serial.println(payload);
    if (payload[0] == '1') {
      digitalWrite(outputArray[0], LOW);
      outputStateArray[0] = "on";
    } else {
      digitalWrite(outputArray[0], HIGH);
      outputStateArray[0] = "off";
    }
    mqttClient.publish(topicArray[8], WiFi.localIP().toString());
  });
  
  mqttClient.subscribe(topicArray[1], [](const String & payload) {
    Serial.println(payload);
    if (payload[0] == '1') {
      digitalWrite(outputArray[1], LOW);
      outputStateArray[1] = "on";
    } else {
      digitalWrite(outputArray[1], HIGH);
      outputStateArray[1] = "off";
    }
    mqttClient.publish(topicArray[8], WiFi.localIP().toString());
  });
  
  mqttClient.subscribe(topicArray[2], [](const String & payload) {
    Serial.println(payload);
    if (payload[0] == '1') {
      digitalWrite(outputArray[2], LOW);
      outputStateArray[2] = "on";
    } else {
      digitalWrite(outputArray[2], HIGH);
      outputStateArray[2] = "off";
    }
    mqttClient.publish(topicArray[8], WiFi.localIP().toString());
  });

  mqttClient.subscribe(topicArray[3], [](const String & payload) {
    Serial.println(payload);
    if (payload[0] == '1') {
      digitalWrite(outputArray[3], LOW);
      outputStateArray[3] = "on";
    } else {
      digitalWrite(outputArray[3], HIGH);
      outputStateArray[3] = "off";
    }
    mqttClient.publish(topicArray[8], WiFi.localIP().toString());
  });

  mqttClient.subscribe(topicArray[4], [](const String & payload) {
    Serial.println(payload);
    if (payload[0] == '1') {
      digitalWrite(outputArray[4], LOW);
      outputStateArray[4] = "on";
    } else {
      digitalWrite(outputArray[4], HIGH);
      outputStateArray[4] = "off";
    }
    mqttClient.publish(topicArray[8], WiFi.localIP().toString());
  });

  mqttClient.subscribe(topicArray[5], [](const String & payload) {
    Serial.println(payload);
    if (payload[0] == '1') {
      digitalWrite(outputArray[5], LOW);
      outputStateArray[5] = "on";
    } else {
      digitalWrite(outputArray[5], HIGH);
      outputStateArray[5] = "off";
    }
    mqttClient.publish(topicArray[8], WiFi.localIP().toString());
  });

  mqttClient.subscribe(topicArray[6], [](const String & payload) {
    Serial.println(payload);
    if (payload[0] == '1') {
      digitalWrite(outputArray[6], LOW);
      outputStateArray[6] = "on";
    } else {
      digitalWrite(outputArray[6], HIGH);
      outputStateArray[6] = "off";
    }
    mqttClient.publish(topicArray[8], WiFi.localIP().toString());
  });
  
  mqttClient.subscribe(topicArray[7], [](const String & payload) {
    Serial.println(payload);
    if (payload[0] == '1') {
      digitalWrite(outputArray[7], LOW);
      outputStateArray[7] = "on";
    } else {
      digitalWrite(outputArray[7], HIGH);
      outputStateArray[7] = "off";
    }
    mqttClient.publish(topicArray[8], WiFi.localIP().toString());
  });
}

void loop(){
  if(!mqttClient.isConnected()){
    //mqttClient.setServer(MQTT_HOST, 1884)
    //mqttClient.setCallback(onConnectionEstablished);
    mqttClient.setOnConnectionEstablishedCallback(onConnectionEstablished);
  }
  mqttClient.loop();
  WiFiClient espClient = server.available(); 
  if (espClient) {                             
    Serial.println("New Client.");         
    String currentLine = "";             
    while (espClient.connected()) {    
      mqttClient.loop();        
      if (espClient.available()) {            
        char c = espClient.read();            
        Serial.write(c);                 
        header += c;
        if (c == '\n') {                   
          if (currentLine.length() == 0) {
            espClient.println("HTTP/1.1 200 OK");
            espClient.println("Content-type:text/html");
            espClient.println("Connection: close");
            espClient.println();

            for(i = 0;i < 8;i++){
              String header1 = String("GET /");
              header1 += i;
              String printer = String("GPIO ");
              printer += i;
              if (header.indexOf(header1 + "/on") >= 0) {
                printer += " on";
                Serial.println(printer);
                outputStateArray[i] = "on";
                digitalWrite(outputArray[i], LOW);
                mqttClient.publish(topicArray[i], "1");
                break;
              } else if (header.indexOf(header1 + "/off") >= 0) {
                printer += " off";
                Serial.println(printer);
                outputStateArray[i] = "off";
                digitalWrite(outputArray[i], HIGH);
                mqttClient.publish(topicArray[i], "0");
                break;
              }
            }
            
            // Display the HTML web page
            espClient.println("<!DOCTYPE html><html>");
            espClient.println("<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
            espClient.println("<link rel=\"icon\" href=\"data:,\">");
            espClient.println("<style>html { font-family: Helvetica; display: inline-block; margin: 0px auto; text-align: center;}");
            espClient.println(".button { background-color: #195B6A; border: none; color: white; padding: 16px 40px;");
            espClient.println("text-decoration: none; font-size: 30px; margin: 2px; cursor: pointer;}");
            espClient.println(".button2 {background-color: #77878A;}</style></head>");
            
            // Web Page Heading
            espClient.println("<body><h1>ESP8266 Web Server</h1>");

            // Display current state, and ON/OFF buttons for GPIOs

            for(i = 0;i < 8;i++){
              String builder = String("<p>GPIO ");
              builder += i;
              espClient.println(builder + " - State " + outputStateArray[i] + "</p>");
              if (outputStateArray[i]=="off") {
                builder = String("<p><a href=\"/");
                builder += i;
                espClient.println(builder + "/on\"><button class=\"button\">ON</button></a></p>");
              } else {
                builder = String("<p><a href=\"/");
                builder += i;
                espClient.println(builder + "/off\"><button class=\"button button2\">OFF</button></a></p>");
              } 
            }
            espClient.println("</body></html>");

            // The HTTP response ends with another blank line
            espClient.println();
            // Break out of the while loop
            break;
          } else { // if you got a newline, then clear currentLine
            currentLine = "";
          }
        } else if (c != '\r') {  // if you got anything else but a carriage return character,
          currentLine += c;      // add it to the end of the currentLine
        }
      }
    }
    // Clear the header variable
    header = "";
    // Close the connection
    espClient.stop();
    Serial.println("Client disconnected.");
    Serial.println("");
  }
}

void toggler(int pin){
  int ioNum = outputArray[pin];
  digitalWrite(ioNum, !digitalRead(ioNum));
  if(outputStateArray[pin] == "on"){
    outputStateArray[pin] = "off";
  }
  else{
    outputStateArray[pin] = "on";
  }
}
