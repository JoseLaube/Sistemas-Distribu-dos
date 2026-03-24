import socket
import struct

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)

ttl = 1
ttl_binario = struct.pack('b', ttl)
grupo_ip = '224.0.0.2'
mreq = struct.pack('4s4s', socket.inet_aton(grupo_ip), socket.inet_aton('0.0.0.0'))
sock.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, ttl_binario)

porta = 8888

sock.bind(('', porta))

ttl = 1
ttl_binario = struct.pack('b', ttl)
##sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, ttl_binario)

while True:
    data, adr = sock.recvfrom(1024)
    resp = data.decode('utf-8')
    print(resp, adr)