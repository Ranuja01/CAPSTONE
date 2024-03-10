# Andrew Gurges Capstone Code
import WifiConnect as wc
import PlugCommands as plug
import dataProcessing as dataproc
import HTTPReq as httpr
import TCPServ as server
import network
import machine
from time import sleep

# ssid = 'ssid'
# password = 'password'
# plugIP = '192.168.1.102'
hostname = 'PicoPlug'

try:
    
    ip = -1
    while(ip == -1):
        # Start AP Mode to receive ssid and password
        ap_ip, ap = wc.APModeSetup()
        
        connection = server.openSocket(ap_ip, isBlocking = True)
        print(f"Socket Created on IP {ap_ip}")
        ssid = 0
        password = 0
        while (not ssid) or (not password):
            client, request = server.receiveTCPConn(connection)
            # Takes SSID and password in the form "SSID ____" and "PASS ____"
            if "SSID" in request:
                ssid = request.split(' ')[1]
                print(ssid)
            elif "PASS" in request:
                password = request.split(' ')[1]
                print(password)
        
        wc.APModeDisconnect(ap)
        '''
        # Get the ssid and password and store it locally
        inp = input("Use existing info? (Y/N)")
        ssid, password = wc.setupWifiInfo(inp=='Y')
        # Connect to wifi given the ssid and password
        '''
        
        wlan, ip = wc.connectWifi(ssid, password)
    
    '''
    # Do Plug Setup
    inp = input("Perform Plug Setup? (Y/N)")
    # Find Plug SSID
    plug_ssid = wc.findSSID(wlan, "Sha")
    if plug_ssid == 0:
        print("Plug not found.")
    else:
        print("Connecting to: " plug_ssid)
        # Connect to wifi given the ssid and password
        wlan, ip = wc.connectWifi(plug_ssid, "")
    '''
    
    # Open a socket for http requests
    #connection = httpr.open_socket(ip)
    connection = server.openSocket(ip, isBlocking = True)
    state = 'OFF'
    
    #while(True):
    #   dataproc.readCurrents()
    
    while(True):
        # Connect to the Phone's IP
        # res = requests.get(url=f'http://{phoneIP}:8080/Send')
        # client, request = httpr.parseRequest(connection)
        client, request = server.receiveTCPConn(connection)
        print(request)
        if request == 'toggle':
            plug.setPower('TOGGLE')
        '''
        if request == '/poweron?':
            plug.setPower('ON')
            state = 'ON'
        elif request == '/poweroff?':
            plug.setPower('ON')
            state = 'OFF'
        elif request == '/powertog?':
            plug.setPower('TOGGLE')
            if(state == 'OFF'):
                state == 'ON'
            else:
                state == 'OFF'
        '''
        voltage, current = plug.getVoltCurr(hostname)
        client.sendall(f"Voltage: {voltage}\n".encode('utf-8'))
        client.sendall(f"Current: {current}\n".encode('utf-8'))
        print(f"Voltage = {voltage}")
        print(f"Current = {current}")
        # html = httpr.webpage(voltage, current, state)
        # client.send(html)
        client.close()


except KeyboardInterrupt:
    machine.reset()
