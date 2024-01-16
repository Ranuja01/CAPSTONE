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
    # Double blink to show device is connected
    led_blink()
    led_blink()
    ip = wlan.ifconfig()[0]
    print(f'Connected on {ip}')
    return ip