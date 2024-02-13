# Andrew Gurges Capstone Code
import WifiConnect as wc
import HTTPReq as httpr
import urequests as requests
import network
import machine
from time import sleep

# ssid = 'ssid'
# password = 'password'
# plugIP = '192.168.1.102'
hostname = 'PicoPlug'
phoneIP = '192.168.1.103'


def setHostname(ip, hostname):
    ''' Sets the hostname of the plug '''
    res = requests.get(url=f'http://{ip}/cm?cmnd=Hostname%20{hostname}')
    return res.text

def getVoltCurr(ip):
    ''' Reads the voltage and current information from the plug '''
    try:
        # Sends a Tasmota command (Status 10) Tasmota which returns the voltage and current readings in a dict
        res = requests.get(url=f'http://{ip}/cm?cmnd=Status%2010')
        # Converts string output to dict
        resdict = eval(res.text)
        # Extracts the voltage and current readings
        voltage = resdict['StatusSNS']['ENERGY']['Voltage']
        current = resdict['StatusSNS']['ENERGY']['Current']
    except OSError:
        print("Unable to read voltage or current.")
        voltage = -1
        current = -1
    return voltage, current

def setPower(command):
    ''' Toggle the smart plug power '''
    try:
        response = requests.get(url=f'http://{hostname}/cm?cmnd=Power%20{command}')
    except:
        print("Could not toggle power")
        return ""
    return response.text

def readCurrents():
    # Array of current values and kepeing track of the end 
    currVals = 10*[0.0]
    end = 0
    initSize = 10
    avg = 0
    currsum = 0
    
    # Get the first few values to fill the array
    while end < initSize:
        voltage, current = getVoltCurr(hostname)
        print("Current = ", current)
        # Only use nonzero current values to fill the array
        if(current != 0):
            currVals[end] = current
            end += 1
        sleep(0.5)
        
    print(currVals)
    
    for ele in currVals:
            currsum += ele
    avg = currsum / len(currVals)
    end = 0
    while(True):
        voltage, current = getVoltCurr(hostname)
        print(f"Current = {current}")
        print(f"Average = {avg}")
        # Remove old value and add new value to sum, update array and average
        currsum -= currVals[end]
        currsum += current
        currVals[end] = current
        avg = currsum / len(currVals)
        end = (end + 1) % initSize # 0-initSize then loops back to 0
        sleep(0.5)
        
    return

try:
    ip = -1
    while(ip == -1):
        # Get the ssid and password and store it locally
        inp = input("Use existing info? (Y/N)")
        ssid, password = wc.setupWifiInfo(inp=='Y')
        # Connect to wifi given the ssid and password
        ip = wc.connectWifi(ssid, password)
        
    # Open a socket for http requests
    connection = httpr.open_socket(ip)
    state = 'OFF'
    
    while(True):
        readCurrents()
    
    while(True):
        # Connect to the Phone's IP
        # res = requests.get(url=f'http://{phoneIP}:8080/Send')
        client, request = httpr.parseRequest(connection)
        print(request)
        
        if request == '/poweron?':
            setPower('ON')
            state = 'ON'
        elif request == '/poweroff?':
            setPower('ON')
            state = 'OFF'
        elif request == '/powertog?':
            setPower('TOGGLE')
            if(state == 'OFF'):
                state == 'ON'
            else:
                state == 'OFF'
        
        voltage, current = getVoltCurr(hostname)
        
        html = httpr.webpage(voltage, current, state)
        client.send(html)
        client.close()
        
        
        print(f"Voltage = {voltage}")
        print(f"Current = {current}")


except KeyboardInterrupt:
    machine.reset()
