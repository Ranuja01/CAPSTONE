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

def webpage(voltage, current, state):
    # Basic HTML
    html = f"""
    <!DOCTYPE html>
    <html>
    <form action="./poweron">
    <input type="submit" value="Power on" />
    </form>
    <form action="./poweroff">
    <input type="submit" value="Power off" />
    </form>
    <p>LED is {state}</p>
    <p>Current is {current}</p>
    <p>Voltage is {voltage}</p>
    </body>
    </html>
    """
    return str(html)

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

def setPower(command):
    # Toggle the smart plug power
    response = requests.get(url=f'http://{hostname}/cm?cmnd=Power%20{command}')
    return response.text

try:

    ip = -1
    while(ip == -1):
        # Get the ssid and password and store it locally
        ssid, password = wc.setupWifiInfo()
        # Connect to wifi given the ssid and password
        ip = wc.connect(ssid, password)
    while(True):
        # Open a socket for http requests
        connection = wc.open_socket(ip)
        # Connect to the Phone's IP
        # res = requests.get(url=f'http://{phoneIP}:8080/Send')
        client, request = wc.parseRequest(connection)
        
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
        
        html = webpage(voltage, current, state)
        client.send(html)
        client.close()
        
        
        print(f"Voltage = {voltage}")
        print(f"Current = {current}")
        '''
        tog = input("Toggle? Y/N (E to exit): ")
        if (tog == 'Y'):
            print(togglePower(hostname))
        if (tog == 'E'):
            break
        '''
            
except KeyboardInterrupt:
    machine.reset()