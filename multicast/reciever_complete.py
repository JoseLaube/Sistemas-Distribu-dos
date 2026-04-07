import socket
import struct
import threading
import time
from datetime import datetime, timedelta

GRUPO_MULTICAST = '224.0.0.2'
PORTA = 8888

DELTA = 30.0           # Janela de análise
INTERVALO_ENVIO = 10.0 # Frequência de envio de cada nó

nome_host = socket.gethostname()
MEU_IP = socket.gethostbyname(nome_host)

lock_mapa = threading.Lock()
mapa_nos = {}

def ouvinte_multicast():
    sock_recv = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
    sock_recv.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock_recv.bind(('', PORTA))
    
    grupo_binario = socket.inet_aton(GRUPO_MULTICAST)
    mreq = struct.pack('4sL', grupo_binario, socket.INADDR_ANY)
    sock_recv.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

    print(f"[RECEIVER] Ouvindo na porta {PORTA}...")

    while True:
        try:
            dados, endereco = sock_recv.recvfrom(2048)
            ip_remetente = endereco[0]
            msg_recebida = dados.decode('utf-8')

            if ip_remetente == MEU_IP:
                continue

            if "suspeito" in msg_recebida:
                continue 

            tempo_chegada = time.time()

            with lock_mapa:
                if ip_remetente not in mapa_nos:
                    mapa_nos[ip_remetente] = []
                mapa_nos[ip_remetente].append(tempo_chegada)

        except Exception as e:
            print(f"[RECEIVER] Erro: {e}")

def gerente_e_sender():
    sock_send = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    ttl_binario = struct.pack('b', 1)
    sock_send.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, ttl_binario)

    print(f"[SENDER] Monitor iniciado. Intervalo: {INTERVALO_ENVIO}s | Janela: {DELTA}s")
    
    inicio_janela = datetime.now()
    esperado = DELTA / INTERVALO_ENVIO

    while True:
        time.sleep(INTERVALO_ENVIO)
        agora = datetime.now()

        heartbeat = f"{agora.strftime('%Y-%m-%d %H:%M:%S')}, {MEU_IP}\n"
        sock_send.sendto(heartbeat.encode('utf-8'), (GRUPO_MULTICAST, PORTA))

        if (agora - inicio_janela).total_seconds() >= DELTA:
            
            with lock_mapa:
                print("\n" + "="*40)
                print(f"--- ANÁLISE DA JANELA ({DELTA}s) ---")
                print(f"Horário: {agora.strftime('%H:%M:%S')} | Esperado: {esperado}")
                print("="*40)

                payload_mapa = ""
                
                items_para_analise = list(mapa_nos.items())

                for ip, timestamps in items_para_analise:
                    recebidas = len(timestamps)
                    
                    suspeito = max(0.0, 1.0 - (recebidas / esperado))

                    print(f"IP: {ip} | Recebidas: {recebidas} | Suspeito: {suspeito:.4f}")
                    
                    linha = f"{agora.strftime('%Y-%m-%d %H:%M:%S')}, {DELTA}, {ip}, {suspeito}\n"
                    payload_mapa += linha

                    mapa_nos[ip].clear() 

                if payload_mapa:
                    sock_send.sendto(payload_mapa.encode('utf-8'), (GRUPO_MULTICAST, PORTA))

                inicio_janela = datetime.now()
                print("="*40 + "\n")

if __name__ == "__main__":
    thread_ouvinte = threading.Thread(target=ouvinte_multicast)
    thread_ouvinte.daemon = True
    thread_ouvinte.start()

    gerente_e_sender()