# Andrew Gurges Capstone Code
from WifiConnect import connect
import urequests as requests
import network
import machine

ssid = 'ssid'
password = 'password'
# plugIP = '192.168.1.102'
hostname = 'PicoPlug'
phoneIP = '192.168.1.103'


try:
    # Connect to wifi given the ssid and password
    ip = connect(ssid, password)
    while(True):
        # Connect to the Phone's IP
        # res = requests.get(url=f'http://{phoneIP}:8080/Send')
        
        # Set Hostname
        res = requests.get(url=f'http://192.168.1.102/cm?cmnd=Hostname%20{hostname}')
        print(res.text)
        # Sends a Tasmota command (Status 10) Tasmota which returns the voltage and current readings in a dict
        res = requests.get(url=f'http://{hostname}/cm?cmnd=Status%2010')
        # Converts string output to dict
        resdict = eval(res.text)
        # Extracts the voltage and current readings and prints them
        voltage = resdict['StatusSNS']['ENERGY']['Voltage']
        current = resdict['StatusSNS']['ENERGY']['Current']
        print(f"Voltage = {voltage}")
        print(f"Current = {current}")
        # Toggle the smart plug power
        tog = input("Toggle? Y/N: ")
        if (tog == 'Y'):
            response = requests.get(url=f'http://{hostname}/cm?cmnd=Power%20TOGGLE')
            print(response.text)
            
except KeyboardInterrupt:
    machine.reset()