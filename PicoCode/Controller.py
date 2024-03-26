# Andrew Gurges Capstone Code
import machine
from time import sleep
import select
import _thread
import gc

import WifiConnect as wc
import PlugCommands as plug
import DataProcessing as dataproc
import TCPServ

# ssid = 'ssid'
# password = 'password'
# plugIP = '192.168.1.102'
hostname = 'PicoPlug'
lock = _thread.allocate_lock()

def processData():
    while(True):
        # Array of current values and kepeing track of the end 
        currVals = 10*[0.0]
        end = 0
        initSize = 10
        avg = 0
        currsum = 0
        
        # Get the first few values to fill the array
        while end < initSize:
            lock.acquire()
            voltage, current = plug.getVoltCurr(hostname)
            lock.release()
            print("Current = ", current)
            # Only use nonzero current values to fill the array
            if(current != 0):
                currVals[end] = current
                end += 1
            sleep(0.5)
            
        print(currVals)
        
        # Find the average current from the testing period
        for ele in currVals:
                currsum += ele
        baseavg = currsum / len(currVals)
        print("Base Average =", baseavg)
        end = 0
        # Compute a rolling average of the current
        while(True):
            lock.acquire()
            voltage, current = plug.getVoltCurr(hostname)
            lock.release()
            # Remove old value and add new value to sum, update array and average
            currsum -= currVals[end]
            currsum += current
            currVals[end] = current
            avg = currsum / len(currVals)
            end = (end + 1) % initSize # 0-initSize then loops back to 0
            print(f"Current = {current}")
            print(f"Average = {avg}")
            
            # Check for conditions
            if(current >= 10):
                lock.acquire()
                plug.setPower("OFF", hostname)
                lock.release()
                print("Current exceeded 10A, plug shut-off")
                break
            if(current >= 1.5*baseavg):
                lock.acquire()
                plug.setPower("OFF", hostname)
                lock.release()
                print("Current exceeded base average, plug shut-off")
                break
            sleep(0.5)
    
    return


def processTCPRequests(ip):
    isBlocking = False
    
    # Open the socket that acts as the server
    connection = TCPServ.openSocket(ip, isBlocking)
    
    # Create a poll object to contain the connected clients
    clientlist = select.poll()
    
    state = 'OFF'
    while(True):
        # Add any new clients that would like to connect
        try:
            client, addr = connection.accept()
            client.setblocking(isBlocking)
            clientlist.register(client)
            print(f"Connected to {addr}")
        except OSError as e:
            pass
        
        # Check which clients are ready to be read from
        ready_to_read = clientlist.poll(1)
        for ready_client in ready_to_read:
            client = ready_client[0]
            # Service all available clients
            try:
                request = client.recv(1024)
                if not request:
                    print("Client exited. closing...")
                    clientlist.unregister(client)
                    client.close()
                    continue
                # Check which command was entered
                #lock.acquire()
                request = request.decode('utf-8')
                print("Request:", request)
                if request == 'toggle':
                    plug.setPower('TOGGLE', hostname)
                    if(state == 'ON'):
                        state = 'OFF'
                    else:
                        state = 'ON'
                elif request == 'on':
                    plug.setPower('ON', hostname)
                    state = 'ON'
                elif request == 'off':
                    plug.setPower('OFF', hostname)
                    state = 'OFF'
                elif request == 'getstate':
                    client.sendall(state.encode('utf-8'))
                elif request == 'getcurr':
                    voltage, current = plug.getVoltCurr(hostname)
                    client.sendall(f"{current}".encode('utf-8'))
                elif request == 'getvolt':
                    voltage, current = plug.getVoltCurr(hostname)
                    client.sendall(f"{voltage}".encode('utf-8'))
                elif 'time' in request:
                    times = request.split(' ')
                    start = int(times[1])
                    end = int(times[2])
                    print("Start:", start, "End:", end)
                elif request == 'graph':
                    for i in range(60):
                        #graphclient, graphaddr = connection.accept()
                        current = plug.getVoltCurr(hostname)[1]
                        client.sendall(f"{current}\n".encode('utf-8'))
                        print(current)
                        #graphclient.close()
                #lock.release()
            except OSError as e:
                pass
            
            #print("closing connection... ")
            #client.sendall("FIN\n".encode('utf-8'))
            #client.close()

def wifiSetup():
    ip = -1
    #inp = input("Perform AP Mode Setup? (Y/N)")
    inp = 'N'
    while(ip == -1):
        if inp == 'Y':
            # Start AP Mode to receive ssid and password
            ap_ip, ap = wc.APModeSetup()
            
            connection = TCPServ.openSocket(ap_ip, isBlocking = True)
            print(f"Socket Created on IP {ap_ip}")
            ssid = 0
            password = 0
            while (not ssid) or (not password):
                client, request = TCPServ.receiveTCPConn(connection)
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
        elif inp == 'N':
            ssid, password = wc.setupWifiInfo(True)
            wlan, ip = wc.connectWifi(ssid, password)
        else:
            ssid, password = wc.setupWifiInfo(False)
            wlan, ip = wc.connectWifi(ssid, password)
    
    return ip


if __name__ == '__main__':
    try:
        ip = wifiSetup()
        gc.enable()
        gc.collect()
        #DataProcessingThread = _thread.start_new_thread(processData, ())
        #TCPRequestThread = _thread.start_new_thread(processTCPRequests, (ip,))
        #processData()
        processTCPRequests(ip)

    except KeyboardInterrupt:
        print("Keyboard Interrupt Received. Exiting...")
        machine.reset()
    except OSError as e:
        print("OS Error Reached:", e)
        machine.reset()