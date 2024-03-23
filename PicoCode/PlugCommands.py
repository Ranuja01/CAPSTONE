import urequests as requests


def setPlugHostname(ip, hostname):
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
    except OSError as e:
        print("Unable to read voltage or current. Error:", e)
        voltage = -1
        current = -1
    return voltage, current

def setPower(command, ip):
    ''' Toggle the smart plug power '''
    try:
        response = requests.get(url=f'http://{ip}/cm?cmnd=Power%20{command}')
    except OSError as e:
        print("Could not change power. Error:", e)
        return ""
    return response.text