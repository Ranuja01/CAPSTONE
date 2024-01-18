import network
from time import sleep
from picozero import pico_led
import machine

CONN_RESET_COUNT = 10
TIMEOUT_COUNT = 4

def led_blink():
    pico_led.on()
    sleep(0.1)
    pico_led.off()
    sleep(0.1)

def SetupWifiInfo():
    f = open('WifiInfo.txt', 'w')
    # Used for testing, this should be taken from the app
    print("Enter SSID and Password:")
    ssid = input("SSID: ")
    password = input("Password: ")
    # Store the wifi login information locally to be used later
    f.write(f"{ssid}, {password}")
    f.close()
    return ssid, password


def APMode():
    # Turns on AP mode in powersaving for the phone to access
    ap = network.WLAN(network.AP_IF)
    ap.config(ssid = "PlugPico", password = "Capstone", pm = network.WLAN.PM_POWERSAVE)
    ap.active(True)
    return

def connect(ssid, password):
    wlan = network.WLAN(network.STA_IF)
    timecount = 0
    # Retry connection until timeout reached
    while wlan.isconnected() == False and timecount < TIMEOUT_COUNT:
        wlan.active(True)
        wlan.connect(ssid,password)
        attemptcount = 0
        while wlan.isconnected() == False and attemptcount < 10:
              print('Waiting for connection...')
              led_blink() # Shows user device is not connected
              sleep(1)
              attemptcount += 1
        timecount += 1
    # Device was unable to connect to the given wifi signal
    if(timecount == TIMEOUT_COUNT):
        print("Unable to connect.")
        return -1
    # Double blink to show device is connected
    led_blink()
    led_blink()
    ip = wlan.ifconfig()[0]
    print(f'Connected on {ip}')
    return ip
