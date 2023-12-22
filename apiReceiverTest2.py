# -*- coding: utf-8 -*-
"""
Created on Fri Dec 22 01:06:13 2023

@author: ranuj
"""

from zeroconf import Zeroconf, ServiceBrowser
from flask import Flask, request, jsonify
import requests
import socket

class ServiceListener:
    def remove_service(self, zeroconf, type, name):
        print("Service {} removed".format(name))

    def add_service(self, zeroconf, type, name):
        info = zeroconf.get_service_info(type, name)
        print("Service {} added, service info: {}".format(name, info))

        if info:
            host_address = socket.inet_ntoa(info.address)
            port = info.port
            service_endpoint = "http://{}:{}/receive_message".format(host_address, port)
            print("Service endpoint: {}".format(service_endpoint))

            # Now you can use service_endpoint to interact with the Android service
            # (e.g., send a message using requests)
            

            # Replace this with the actual service endpoint obtained from the script's output
            #service_endpoint = "http://192.168.2.43:5000/receive_message"
            
            # Send a GET request to the discovered service endpoint
            response = requests.get(f"{service_endpoint}")
            
            print(f"Response from Android service: {response.text}")
    

# Initialize zeroconf for service discovery
zeroconf = Zeroconf()
listener = ServiceListener()
browser = ServiceBrowser(zeroconf, "_http._tcp.local.", listener)
