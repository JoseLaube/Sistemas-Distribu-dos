import socket
import struct

grupo_ip = '224.1.1.1'
porta = 8888

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)

sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

sock.bind(('', porta))

# Entrar no grupo multicast
mreq = struct.pack('4s4s', socket.inet_aton(grupo_ip), socket.inet_aton('0.0.0.0'))
sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

print("Receiver pronto, escutando...")

try:
    while True:
        data, adr = sock.recvfrom(1024)
        print(f"Mensagem de {adr}: {data.decode('utf-8')}")
except KeyboardInterrupt:
    print("\nEncerrando receiver...")
finally:
    sock.close()