import socket

def openSocket(ip, isBlocking):
    ''' Opens a TCP socket on port 80 to listen for http requests '''
    # Open an IPv4 TCP Socket
    address = (ip, 50000) # 80 <- Port Number
    connection = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # Address can be reused without waiting for timeout
    connection.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) 
    connection.setblocking(isBlocking)   # Set to Non-blocking 
    connection.bind(address)
    connection.listen(1)            # Max. 1 connection at a time
    return connection

def receiveTCPConn(connection):
    ''' Process the incoming TCP connection, extracts the client and text information '''
    try:
        client, addr = connection.accept()
        print(f"Connected to {addr}")
        request = client.recv(1024)
        request = request.decode('utf-8')
        print(request)
    except OSError:
        pass
    return client, request


    

