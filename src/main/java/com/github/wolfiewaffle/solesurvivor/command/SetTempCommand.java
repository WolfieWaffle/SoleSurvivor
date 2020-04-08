package com.github.wolfiewaffle.solesurvivor.command;

import com.github.wolfiewaffle.solesurvivor.capability.ITemperature;
import com.github.wolfiewaffle.solesurvivor.network.SoleSurvivorMessage;
import com.github.wolfiewaffle.solesurvivor.network.SoleSurvivorPacketHandler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class SetTempCommand extends CommandBase {

	@CapabilityInject(value = ITemperature.class)
	public static Capability<ITemperature> TEMPERATURE = null;

	@Override
	public String getName() {
		return "settemp";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "test";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World world = sender.getEntityWorld();

		// Get the value
		try {
			double newTemp = Double.parseDouble(args[0]);

			if (!world.isRemote) {
				Entity entity = sender.getCommandSenderEntity();

				if (entity instanceof EntityPlayerMP) {
					EntityPlayerMP player = ((EntityPlayerMP) entity);
					ITemperature cap = player.getCapability(TEMPERATURE, null);

					cap.setTemperature(newTemp);
					SoleSurvivorPacketHandler.CHANNEL_INSTANCE.sendTo(new SoleSurvivorMessage(newTemp), player);
				}
			}
		} catch (Exception e) {
			return;
		}
	}

}
