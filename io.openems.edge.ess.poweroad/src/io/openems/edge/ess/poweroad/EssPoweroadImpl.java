package io.openems.edge.ess.poweroad;

import static io.openems.edge.bridge.modbus.api.ElementToChannelConverter.DIRECT_1_TO_1;
import static io.openems.edge.bridge.modbus.api.ElementToChannelConverter.SCALE_FACTOR_2;
import static io.openems.edge.bridge.modbus.api.ElementToChannelConverter.SCALE_FACTOR_3;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;

import io.openems.common.channel.AccessMode;
import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.bridge.modbus.api.AbstractOpenemsModbusComponent;
import io.openems.edge.bridge.modbus.api.BridgeModbus;
import io.openems.edge.bridge.modbus.api.ElementToChannelConverter;
import io.openems.edge.bridge.modbus.api.ModbusComponent;
import io.openems.edge.bridge.modbus.api.ModbusProtocol;
import io.openems.edge.bridge.modbus.api.element.SignedWordElement;
import io.openems.edge.bridge.modbus.api.element.UnsignedDoublewordElement;
import io.openems.edge.bridge.modbus.api.element.UnsignedWordElement;
import io.openems.edge.bridge.modbus.api.task.FC4ReadInputRegistersTask;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.modbusslave.ModbusSlave;
import io.openems.edge.common.modbusslave.ModbusSlaveNatureTable;
import io.openems.edge.common.modbusslave.ModbusSlaveTable;
import io.openems.edge.common.sum.GridMode;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.ess.api.AsymmetricEss;
import io.openems.edge.ess.api.SymmetricEss;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Ess.Poweroad", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class EssPoweroadImpl extends AbstractOpenemsModbusComponent
		implements EssPoweroad, ModbusComponent, ModbusSlave, OpenemsComponent, SymmetricEss {

	@Reference
	private ConfigurationAdmin cm;

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	protected void setModbus(BridgeModbus modbus) {
		super.setModbus(modbus);
	}

	public EssPoweroadImpl() {
		super(//
				EssPoweroad.ChannelId.values(), //
				ModbusComponent.ChannelId.values(), //
				OpenemsComponent.ChannelId.values(), //
				SymmetricEss.ChannelId.values() //
		);
	}

	@Activate
	private void activate(ComponentContext context, Config config) throws OpenemsException {
		if(super.activate(context, config.id(), config.alias(), config.enabled(), config.modbusUnitId(), this.cm, 
				"Modbus", config.modbus_id())) {
			return;
		}
		this._setCapacity(config.capacity());
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	protected ModbusProtocol defineModbusProtocol() {
		// TODO implement ModbusProtocol TODO_ANDY

		final var offset = -1; // ?
		
		// FC6WriteRegisterTask
		
		return new ModbusProtocol(this,
				new FC4ReadInputRegistersTask(2502 + offset, Priority.LOW,
						m(SymmetricEss.ChannelId.ACTIVE_POWER, new SignedWordElement(2502 + offset), SCALE_FACTOR_2)
				),
				new FC4ReadInputRegistersTask(125 + offset, Priority.LOW, 
						m(SymmetricEss.ChannelId.SOC, new UnsignedWordElement(126 + offset), DIRECT_1_TO_1)
				)
				
		);
	}

	@Override
	public String debugLog() {
		return "Hello World"; //TOOD_ANDY print values
	}

	@Override
	public ModbusSlaveTable getModbusSlaveTable(AccessMode accessMode) {
		return new ModbusSlaveTable(//
				OpenemsComponent.getModbusSlaveNatureTable(accessMode), //
				SymmetricEss.getModbusSlaveNatureTable(accessMode), //
				AsymmetricEss.getModbusSlaveNatureTable(accessMode), //
				ModbusSlaveNatureTable.of(EssPoweroad.class, accessMode, 300) //
						.build());	//TODO_ANDY don't know what this is
	}

	private static final ElementToChannelConverter GRID_MODE_CONVERTER = new ElementToChannelConverter(value -> {
		switch ((Integer) value) {
		case 1:
			return GridMode.OFF_GRID;
		case 2:
			return GridMode.ON_GRID;
		default:
			return GridMode.UNDEFINED;
		}
	});
}










































