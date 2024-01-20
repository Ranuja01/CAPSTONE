from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/receive_message', methods=['POST'])
def receive_message():
    print("RECEIVED!")
    data = request.json

    if data is None:
        return jsonify({'error': 'Invalid JSON data'}), 400
    #print("BBB")
    message = data.get('message')

    if message is None:
        return jsonify({'error': 'Missing "message" in JSON data'}), 400
    #print("CCC")
    # Process the message as needed
    print(f'Received message: {message}')
    #print("DDD")
    return jsonify({'status': 'success'})

# GET endpoint for sending information from the server to the Android app
@app.route('/send_info', methods=['GET'])
def send_info():
    # Prepare the information to be sent
    
    info_message = "TESTING 123..."

    # You can customize the response format as needed
    response_data = {'status': 'success', 'info_message': info_message}

    return jsonify(response_data)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
