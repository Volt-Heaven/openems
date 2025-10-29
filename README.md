## Setup openEMS server

[Guide](https://openems.github.io/openems.io/openems/latest/gettingstarted.html#download-the-source-code): steps 2,3,4 with this branch (andy)

If error:  
- get main branch running
- add my commit 
- check if you're missing anything from [implement guide](https://openems.github.io/openems.io/openems/latest/edge/implement.html)

## Setup openEMS UI

[Guide](https://openems.github.io/openems.io/openems/latest/ui/setup-ide.html)
with this branch (andy)

## Run

Edge:
- Open Eclipse 
- Run OSGi

UI: 
- Open terminal in repo
- Run `cd ui`
- Run `ng serve -c openems-edge-dev`

## Connect DEYE

1. Clone [pysolarmanv5 - tcp_proxy branch](https://github.com/Volt-Heaven/pysolarmanv5/tree/tcp_proxy)
2. Create & activate .venv
3. Install requirements with pip
4. Run:
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

## Modbus registers

[`andy/`](/andy)