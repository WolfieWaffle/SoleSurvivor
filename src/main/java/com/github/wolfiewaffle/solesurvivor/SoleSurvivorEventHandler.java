package com.github.wolfiewaffle.solesurvivor;

import com.github.wolfiewaffle.solesurvivor.capability.ITemperature;
import com.github.wolfiewaffle.solesurvivor.capability.TemperatureProvider;
import com.github.wolfiewaffle.solesurvivor.network.SoleSurvivorMessage;
import com.github.wolfiewaffle.solesurvivor.network.SoleSurvivorPacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@EventBusSubscriber
public class SoleSurvivorEventHandler {

	@CapabilityInject(value = ITemperature.class)
	public static Capability<ITemperature> TEMPERATURE = null;

	@SubscribeEvent
	public static void breakBlock(BreakEvent event) {
		if (event.getPlayer() != null) {

			// Send a packet to the client
			if (!event.getPlayer().world.isRemote) {
				EntityPlayer player = event.getPlayer();
				ITemperature cap = player.getCapability(TEMPERATURE, null);
				double newTemp = cap.getTemperature() - 1000;

				cap.setTemperature(newTemp);

				SoleSurvivorPacketHandler.CHANNEL_INSTANCE.sendTo(new SoleSurvivorMessage(newTemp), ((EntityPlayerMP) event.getPlayer()));
			}
		}
	}

	// This is when the player joins the game
	@SubscribeEvent
	public static void onJoin(PlayerLoggedInEvent event) {
		ITemperature temperatureCap = event.player.getCapability(TemperatureProvider.TEMPERATURE, null);

		// Send a packet to the client
		if (!event.player.world.isRemote) {
			SoleSurvivorPacketHandler.CHANNEL_INSTANCE.sendTo(new SoleSurvivorMessage(temperatureCap.getTemperature()), ((EntityPlayerMP) event.player));
		}
	}

	@SubscribeEvent
	public static void onWorldChange(PlayerChangedDimensionEvent event) {
		ITemperature newTemperatureCap = event.player.getCapability(TemperatureProvider.TEMPERATURE, null);
		double newTemp = newTemperatureCap.getTemperature();

		// Send a packet to the client
		if (!event.player.world.isRemote) {
			SoleSurvivorPacketHandler.CHANNEL_INSTANCE.sendTo(new SoleSurvivorMessage(newTemp), ((EntityPlayerMP) event.player));
		}
	}

	@SubscribeEvent
	public static void playerClone(PlayerEvent.Clone event) {

		// Player is the new player (in the dimension being travelled to)
		EntityPlayer player = event.getEntityPlayer();
		ITemperature newTemperatureCap = player.getCapability(TemperatureProvider.TEMPERATURE, null);
		double newTemp = newTemperatureCap.getTemperature();

		// This is the player in the from-dimension
		ITemperature oldTemperatureCap = event.getOriginal().getCapability(TemperatureProvider.TEMPERATURE, null);

		// Set new cap to old cap data
		newTemperatureCap.setTemperature(oldTemperatureCap.getTemperature());

		// This event only fires server-side. So we send a packet to the client
		if (!player.world.isRemote) {
			SoleSurvivorPacketHandler.CHANNEL_INSTANCE.sendTo(new SoleSurvivorMessage(newTemp), ((EntityPlayerMP) player));
		}
	}

	@SubscribeEvent
	public static void renderText(RenderGameOverlayEvent.Text event) {
		ITemperature cap = Minecraft.getMinecraft().player.getCapability(TEMPERATURE, null);

		event.getLeft().add("TEMP: " + cap.getTemperature());
	}

	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event) {
		ITemperature cap = event.player.getCapability(TEMPERATURE, null);
		double newTemp = cap.getTemperature() + 1;

		if (event.player != null && !event.player.getEntityWorld().isRemote) {
			System.out.println("TEMP " + cap.getTemperature());

			cap.setTemperature(newTemp);
		}

		// Send packet
		if (!event.player.world.isRemote) {
			SoleSurvivorPacketHandler.CHANNEL_INSTANCE.sendTo(new SoleSurvivorMessage(newTemp), ((EntityPlayerMP) event.player));
		}
	}

}
