
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
    
    # Find the average current from the testing period
    for ele in currVals:
            currsum += ele
    baseavg = currsum / len(currVals)
    print("Base Average =", baseavg)
    end = 0
    # Compute a rolling average of the current
    while(True):
        voltage, current = plug.getVoltCurr(hostname)
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
            plug.setPower("OFF", hostname)
            print("Current exceeded 10A, plug shut-off")
            break
        if(current >= 1.5*baseavg):
            plug.setPower("OFF", hostname)
            print("Current exceeded base average, plug shut-off")
            break
        sleep(0.5)
    
    return