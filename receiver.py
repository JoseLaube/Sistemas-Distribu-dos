import socket
import struct
from datetime import datetime, timedelta

grupo_ip = '224.1.1.1'
porta = 8888

DELTA = 2
JANELA = 10

IP_SENDER = "127.0.1.1"  # <-- coloque o IP do seu sender

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
sock.bind(('', porta))

mreq = struct.pack('4s4s', socket.inet_aton(grupo_ip), socket.inet_aton('0.0.0.0'))
sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

print("Receiver pronto, escutando...")

dados = {}
inicio_janela = datetime.now()

try:
    while True:
        data, adr = sock.recvfrom(1024)
        agora = datetime.now()

        ip = adr[0]

        if ip != IP_SENDER:
            continue

        if ip not in dados:
            dados[ip] = []

        dados[ip].append(agora)

        print(f"[{agora}] Mensagem de {ip}: {data.decode('utf-8')}")

        if agora - inicio_janela >= timedelta(seconds=JANELA):
            print("\n--- Análise da Janela ---")

            esperado = JANELA / DELTA

            for ip, timestamps in dados.items():
                recebidas = len(timestamps)

                suspeito = max(0, 1 - (recebidas / esperado))

                print(f"IP: {ip}")
                print(f"Recebidas: {recebidas}")
                print(f"Esperado: {esperado:.2f}")
                print(f"Índice suspeito: {suspeito:.2f}")
                print("-" * 30)

            dados = {}
            inicio_janela = agora

except KeyboardInterrupt:
    print("\nEncerrando receiver...")
finally:
    sock.close()