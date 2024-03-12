
import socket

def webpage(voltage, current, state):
    # Basic HTML
    html = f"""
    <!DOCTYPE html>
    <html>
    <body>
    <form action="./wifiinfo">
    <label for="SSID">SSID:</label>
    <input type="text" id="ssid" name="ssid"><br><br>
    <label for="Password">Password:</label>
    <input type="text" id="password" name="password"><br><br>
    <input type="submit" value="Submit">
    </form>
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

def open_socket(ip):
    ''' Opens a TCP socket on port 80 to listen for http requests '''
    # Open a socket
    address = (ip, 80) # 80 <- Port Number
    connection = socket.socket()
    # Address can be reused without waiting for timeout
    connection.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) 
    connection.bind(address)
    connection.listen(1)
    return connection

def parseRequest(connection):
    ''' Parses the incoming http request, extracts the client and text information '''
    client = connection.accept()[0]
    request = client.recv(1024)
    request = str(request)
    print(request.split()[1])
    try:
        request = request.split()[1] # Request has format http://{IP}/poweron?
    except IndexError:
        pass
    return client, request


    

