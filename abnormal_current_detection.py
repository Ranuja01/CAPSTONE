# -*- coding: utf-8 -*-
"""
Created on Sat Mar 23 10:15:42 2024

@author: ranuj
"""
from time import sleep

import requests
#asdasdas
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

buffer = []
buffer_size = 15
spike_flag = False
fault_flag = False
spike_val = 0
host = '192.168.2.121'

try:    
    while(True):    
        while len(buffer) < buffer_size:
            current = getVoltCurr(host)[1]
            buffer.append(current)
            print(buffer)
            
        while True:
            current = getVoltCurr(host)[1]
            avg = sum(buffer) / len(buffer)
            if (not(spike_flag) and current > 1.025 * avg):
                spike_flag = True
                spike_val = current
                print ("FLAGGED", spike_val, avg)
                buffer = []
                break
            
            if (spike_flag):
                #avg = sum(buffer) / len(buffer)
                if (current > 1.025 * spike_val):
                    print("FAULT! STOPPING", current)
                    requests.get(url=f'http://{host}/cm?cmnd=Power%20TOGGLE')
                    fault_flag = True
                    break
                else:
                    spike_flag = False
                    print ("REMOVING FLAGGED", spike_val, current)
                    
            
            buffer.pop(0)  # Remove oldest value
            buffer.append(current)
            print(buffer)
        
        if (fault_flag):
            break
            
except KeyboardInterrupt:
    print("Keyboard Interrupt... Goodbye.")
