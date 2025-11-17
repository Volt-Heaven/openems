## Setup openEMS server

[Guide](https://openems.github.io/openems.io/openems/latest/gettingstarted.html#download-the-source-code): steps 2,3,4 with this branch (andy)
In EdgeApp.bndrun -> Source: update config and data paths

If error:  
- get main branch running
- add my commit 
- check if you're missing anything from [implement guide](https://openems.github.io/openems.io/openems/latest/edge/implement.html)

## Setup openEMS UI

[Guide](https://openems.github.io/openems.io/openems/latest/ui/setup-ide.html)
with this branch (andy)
- Open edge page:
  - http://localhost:8080/system/console/configMgr
- enable controller api websocket

## Run

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
6. Run:
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
5. Enable in openEMS

## Modbus registers

[`andy/`](/andy)