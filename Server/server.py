import socket
from _thread import *
import sys
import random

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

#Modified from https://youtu.be/-3B1v-K1oXE

server = 'localhost'
port = 5555

server_ip = socket.gethostbyname(server)

try:
    s.bind((server, port))

except socket.error as e:
    print(str(e))

s.listen(2)
print("Waiting for a connection")

#there are two ids, id 0 and id 1, the two players
currentId = "0"
pos = ["1 1", "1 1"] # Replace these positions with the maze positions
def threaded_client(conn):
    global currentId, pos
    conn.send(str.encode(currentId))
    currentId = "1"
    reply = ''
    while True:
        '''try:'''
        data = conn.recv(2048)
        reply = data.decode('utf-8')
        if not data:
            print("Goodbye")
            conn.send(str.encode("Goodbye"))
            break
        else:
            arr = reply.split(":")
            id = int(arr[0])
            pos[id] = arr[1]

            if id == 0: nid = 1
            if id == 1: nid = 0

            reply = pos[nid][:]

            conn.sendall(str.encode(reply))
            conn.sendall(str.encode('\n'))
            
        '''except OSError:
            print('oserror occurred')
            break
        except:
            print('other error occurred')
            break'''

    print("Connection Closed")
    conn.close()

#generate random seed
number = 0
seed = random.randint(0, 100000000)

print('seed', seed)

while True:
    conn, addr = s.accept()
    print("Connected to: ", addr)
    
    conn.sendall(str.encode(str(number)))
    conn.sendall(str.encode(' '))
    conn.sendall(str.encode(str(seed)))
    conn.sendall(str.encode('\n'))
    number = number + 1

    start_new_thread(threaded_client, (conn,))