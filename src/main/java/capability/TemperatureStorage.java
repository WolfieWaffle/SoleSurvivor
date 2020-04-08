package capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class TemperatureStorage implements IStorage<ITemperature> {
	private static final String TEMPERATURE = "temperature";

	@Override
	public NBTBase writeNBT(Capability<ITemperature> capability, ITemperature instance, EnumFacing side) {
		double temp = instance.getTemperature();
		NBTTagCompound tag = new NBTTagCompound();
		tag.setDouble(TEMPERATURE, temp);

		return tag;
	}

	@Override
	public void readNBT(Capability<ITemperature> capability, ITemperature instance, EnumFacing side, NBTBase nbt) {
		instance.setTemperature(((NBTTagCompound) nbt).getDouble(TEMPERATURE));
	}

}
