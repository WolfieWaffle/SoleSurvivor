package capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class TemperatureProvider implements ICapabilitySerializable<NBTTagCompound> {

	// Forge ensures the null will all be the same instance
	@CapabilityInject(value = ITemperature.class)
	public static Capability<ITemperature> TEMPERATURE = null;

	public static final String TEMPERATURE_TAG = "temperature";

	// The actual data
	private final ITemperature temperatureInstance;

	// Needds a provider for some reason
	public TemperatureProvider(ITemperature temperatureInstance) {
		this.temperatureInstance = temperatureInstance;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		final NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble(TEMPERATURE_TAG, temperatureInstance.getTemperature());
		return nbt;
	}

	@Override
	public void deserializeNBT(final NBTTagCompound nbt) {
		temperatureInstance.setTemperature(nbt.getInteger(TEMPERATURE_TAG));
	}

	// This I believe is used to determine what capability something has, so that you know what can be got from it?
	// Specifically using instances as keys
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == TEMPERATURE;
	}

	// Unchecked because forge replaces nulls with the data which eclipse doesn't recognize
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == TEMPERATURE) {
			return (T) temperatureInstance;
		} else {
			return null;
		}
	}

}
