# -*- coding: utf-8 -*-
"""
Created on Fri Dec 22 19:02:30 2023

@author: ranuj
"""

import requests

# Replace this with the actual IP address and port of your Android device running NanoHTTPD
android_device_ip = "192.168.2.54"
android_device_port = 8080  # Replace with the actual port your NanoHTTPD server is running on

# Construct the URL for the NanoHTTPD endpoint you want to access
nanohttpd_url = f"http://{android_device_ip}:{android_device_port}/Send"

# Make a GET request to the NanoHTTPD server
response = requests.get(nanohttpd_url)

# Check the response
if response.status_code == 200:
    print("GET request successful")
    print("Response content:")
    print(response.text)
else:
    print(f"GET request failed with status code {response.status_code}")


# Construct the URL for the NanoHTTPD endpoint you want to access
nanohttpd_url = f"http://{android_device_ip}:{android_device_port}/Receive"

# Define the data to be sent in the POST request
data = {"key1": "value1", "key2": "value2"}
data = "asdjhgasdg"
# Make a POST request to the NanoHTTPD server
response = requests.post(nanohttpd_url, data)

# Check the response
if response.status_code == 200:
    print("POST request successful")
    print("Response content:")
    print(response.text)
else:
    print(f"POST request failed with status code {response.status_code}")