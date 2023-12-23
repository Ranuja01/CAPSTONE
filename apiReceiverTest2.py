# Python code using zeroconf for service registration
from zeroconf import Zeroconf, ServiceInfo
from flask import Flask, request, jsonify
import socket
app = Flask(__name__)

@app.route('/receive_message', methods=['GET'])
def receive_message():
    # Process the message as needed
    print("RECEIVED!")
    data = request.json
    if data is None:
        return jsonify({'error': 'Invalid JSON data'}), 400

    message = data.get('message')
    if message is None:
        return jsonify({'error': 'Missing "message" in JSON data'}), 400

    print(f'Received message: {message}')
    return jsonify({'status': 'success'})

if __name__ == '__main__':
   # Specify the service information
    service_info = ServiceInfo(
        "_http._tcp.local.",  # service type
        "FlaskService._http._tcp.local.",  # service name
        port=8080,  # port (should be an integer)
        properties={"ip": "192.168.2.54"},  # add IP address as a property
    )
    
    # Create a Zeroconf instance
    zeroconf = Zeroconf()
    
    # Register the service
    zeroconf.register_service(service_info)

    app.run(host='0.0.0.0', port=5000)
