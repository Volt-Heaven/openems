# Client - miniPC

## Setup Operating System
- Enable BIOS settings: auto power-on, boot without keyboard
- Install OS (choose):
    - install Ubuntu Server (headless) for running official OpenEMS release  
    (Docker containers)
    - install Ubuntu Desktop for running latest OpenEMS  
    (run from source code - Eclipse IDE)
- setup auto-login, never sleep, never power-off, auto updates, maybe auto reboot regularly
- setup remote control: ssh or RustDesk (permanent password)
- setup static IP address

## Run from source code

### Setup OpenEMS Edge
1. Follow [official guide](https://openems.github.io/openems.io/openems/latest/gettingstarted.html#download-the-source-code): steps 2,3,4 with this branch (andy)  
2. In EdgeApp.bndrun -> Source: update config and data paths  
Example:
```
felix.cm.dir=/home/lenovo/Work/openems_instance/config,\
openems.data.dir=/home/lenovo/Work/openems_instance/data,\
```
3. Run OSGi
4. Open Edge page:  
http://localhost:8080/system/console/configMgr (admin admin)
5. Enable Controller API Websocket (default settings) for UI

### Setup OpenEMS UI
1. Follow [official guide](https://openems.github.io/openems.io/openems/latest/ui/setup-ide.html) with this branch (andy)
2. Open terminal in project folder
3. `cd ui`
4. `ng serve -c openems-edge-dev`

### Just run

Edge:
- Open Eclipse 
- Run OSGi

UI: 
- Open terminal in repo
- Run `cd ui`
- Run `ng serve -c openems-edge-dev`

## Connect DEYE

1. Clone [pysolarmanv5](https://github.com/jmccrohan/pysolarmanv5) (my changes were accepted)
2. Install python3, python-is-python3, python3.13-venv, pip
3. Create .venv
  - `python -m venv .venv`
4. Activate .venv (every time before running)
  - `source .venv/bin/activate`
5. Install requirements with pip
  - `pip install -r requirements.txt`
6. Install library
  - `pip install pysolarmanv5`
7. Run:
```
python utils/solarman_tcp_proxy.py -l DONGLE_IP -s DONGLE_SERIAL
```
(default port 1502: `-p 1502`)

5. Test with qModMaster / HA modbus integration
6. In openEMS: 
    - login as admin
    - Settings > Install components > 'Bridge Modbus/TCP'
        - Component-ID: modbus0
        - IP: 0.0.0.0 (where proxy is running)
        - Port: 1502 (same port ??)
    - Settings > Install components > 'PV-Inverter Deye'
        - Modbus-ID: modbus0 (same as above)
    - also Deye Grid Power and Deye Batteries

## Enable history

1. [Install Docker ](https://docs.docker.com/engine/install/ubuntu)
2. [Docker next steps](https://docs.docker.com/engine/install/linux-postinstall/): non-root, start on boot
3. Save compose.yaml for influxdb:
```
services:
  influxdb:
    image: influxdb:1.12.2
    container_name: influxdb1
    ports:
      - "8086:8086"
    volumes:
      - ./data:/var/lib/influxdb
    restart: unless-stopped

```
4. Start container: `docker compose up -d`
5. Give permission: `chmod -R 777 ./data`
6. Create db:
```
curl -i -XPOST "http://127.0.0.1:8086/query" \
  --data-urlencode "q=CREATE DATABASE db"
```
```
curl -i -XPOST "http://127.0.0.1:8086/query" \
  --data-urlencode "q=CREATE RETENTION POLICY \"data\" ON \"db\" DURATION 0s REPLICATION 1 DEFAULT"
```
7. Enable in openEMS
  - URL: http://localhost:8086
  - Org: -
  - ApiKey: :
  - Bucket: db/data

## Run from containers

# Server - Cloud

## Backend

Run this compose:
```
https://raw.githubusercontent.com/OpenEMS/openems/refs/heads/main/tools/docker/backend/docker-compose.yml
```
- Maybe change influxdb to 1.12.2 as above.
- Edit ports if already in use:
```
ports:
    - 80:80     # to 89:80
    - 443:443   # to 449:443
```

### Backend config
1. Open Backend page:  
http://localhost:8079/system/console/configMgr  
admin admin 
2. Change Metadata.File path:  
`/var/opt/openems/data/metadata.json`  
3. Create metadata.json
- open terminal
- `cd /var/lib/docker/volumes/vopenems_openems-backend-data/_data`
- `nano metadata.json`
- Choose apikey and password
```
{
	edges: {
		edge0: {
			apikey: "d92IC4eEHyrqmiMab6GX",
			setuppassword: "3rzEK9pAqV8iApe7twvy",
			comment: "OpenEMS Demo Edge"
		}
}
```
4. Enable history here too (same as Edge).
5. Go to Edge page and connect to Backend
- Controller Api Backend
- apikey + ws://ip:8081

# Modbus registers

[`andy/`](/andy)