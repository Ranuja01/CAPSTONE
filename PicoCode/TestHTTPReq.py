# Andrew Gurges Capstone Code
import WifiConnect as wc
import urequests as requests
import network
import machine

# ssid = 'ssid'
# password = 'password'
# plugIP = '192.168.1.102'
hostname = 'PicoPlug'
phoneIP = '192.168.1.103'

def setHostname(ip, hostname):
    res = requests.get(url=f'http://{ip}/cm?cmnd=Hostname%20{hostname}')
    return res.text

def getVoltCurr(ip):
    # Sends a Tasmota command (Status 10) Tasmota which returns the voltage and current readings in a dict
    res = requests.get(url=f'http://{ip}/cm?cmnd=Status%2010')
    # Converts string output to dict
    resdict = eval(res.text)
    # Extracts the voltage and current readings
    voltage = resdict['StatusSNS']['ENERGY']['Voltage']
    current = resdict['StatusSNS']['ENERGY']['Current']
    return voltage, current

def togglePower(ip):
    # Toggle the smart plug power
    response = requests.get(url=f'http://{hostname}/cm?cmnd=Power%20TOGGLE')
    return response.text

try:

    ip = -1
    while(ip == -1):
        # Get the ssid and password and store it locally
        ssid, password = wc.SetupWifiInfo()
        # Connect to wifi given the ssid and password
        ip = wc.connect(ssid, password)
    while(True):
        # Connect to the Phone's IP
        # res = requests.get(url=f'http://{phoneIP}:8080/Send')
        
        voltage, current = getVoltCurr(hostname)
        print(f"Voltage = {voltage}")
        print(f"Current = {current}")
        tog = input("Toggle? Y/N (E to exit): ")
        if (tog == 'Y'):
            print(togglePower(hostname))
        if (tog == 'E'):
            break
            
except KeyboardInterrupt:
    machine.reset()