package com.github.wolfiewaffle.solesurvivor.network;

import com.github.wolfiewaffle.solesurvivor.capability.ITemperature;
import com.github.wolfiewaffle.solesurvivor.handlers.SoleSurvivorEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class contains all the client-side packet logic.
 */
public class ClientPacketHandlers {

	public static void handleSoleSurvivor(SoleSurvivorTempMessage message, MessageContext ctx) {

		// The client player
		EntityPlayer clientPlayer = Minecraft.getMinecraft().player;

		// The value that was sent
		double temp = message.getTemperature();
		double tTemp = message.getTargetTemperature();

		Minecraft.getMinecraft().addScheduledTask(() -> {

			// We know this is client capability instance because this method is
			// not called on the server because
			// the packet is not sent to the server, therefore no isRemote check
			ITemperature cap = clientPlayer.getCapability(SoleSurvivorEventHandler.TEMPERATURE, null);
			cap.setTemperature(temp);
			cap.setTargetTemperature(tTemp);
		});
	}

}
