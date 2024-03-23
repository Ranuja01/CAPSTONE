
import PlugCommands as plug
from time import sleep

def readCurrents(hostname):
    # Array of current values and kepeing track of the end 
    currVals = 10*[0.0]
    end = 0
    initSize = 10
    avg = 0
    currsum = 0
    
    # Get the first few values to fill the array
    while end < initSize:
        voltage, current = plug.getVoltCurr(hostname)
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
        voltage, current = plug.getVoltCurr(hostname)
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