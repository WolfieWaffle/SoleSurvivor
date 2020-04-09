package com.github.wolfiewaffle.solesurvivor.handlers;

import com.github.wolfiewaffle.solesurvivor.SoleSurvivor;
import com.github.wolfiewaffle.solesurvivor.capability.ITemperature;
import com.github.wolfiewaffle.solesurvivor.capability.Temperature;
import com.github.wolfiewaffle.solesurvivor.capability.TemperatureProvider;
import com.github.wolfiewaffle.solesurvivor.network.SoleSurvivorTempMessage;
import com.github.wolfiewaffle.solesurvivor.network.SoleSurvivorPacketHandler;
import com.github.wolfiewaffle.solesurvivor.util.BiomeUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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
	public static void capabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(SoleSurvivor.MODID, "TEMPERATURE"), new TemperatureProvider(new Temperature()));
		}
	}

	@SubscribeEvent
	public static void breakBlock(BreakEvent event) {
		if (event.getPlayer() != null) {

			// Send a packet to the client
			if (!event.getPlayer().world.isRemote) {
				EntityPlayer player = event.getPlayer();
				ITemperature cap = player.getCapability(TEMPERATURE, null);
				double newTemp = cap.getTemperature() - 1000;

				cap.setTemperature(newTemp);

				SoleSurvivorPacketHandler.CHANNEL_INSTANCE.sendTo(new SoleSurvivorTempMessage(newTemp, cap.getTargetTemperature()), ((EntityPlayerMP) event.getPlayer()));
			}
		}
	}

	// This is when the player joins the game
	@SubscribeEvent
	public static void onJoin(PlayerLoggedInEvent event) {
		ITemperature temperatureCap = event.player.getCapability(TemperatureProvider.TEMPERATURE, null);

		// Send a packet to the client
		if (!event.player.world.isRemote) {
			SoleSurvivorPacketHandler.CHANNEL_INSTANCE.sendTo(new SoleSurvivorTempMessage(temperatureCap.getTemperature(), temperatureCap.getTargetTemperature()), ((EntityPlayerMP) event.player));
		}
	}

	@SubscribeEvent
	public static void onWorldChange(PlayerChangedDimensionEvent event) {
		ITemperature newTemperatureCap = event.player.getCapability(TemperatureProvider.TEMPERATURE, null);
		double newTemp = newTemperatureCap.getTemperature();

		// Send a packet to the client
		if (!event.player.world.isRemote) {
			SoleSurvivorPacketHandler.CHANNEL_INSTANCE.sendTo(new SoleSurvivorTempMessage(newTemp, newTemperatureCap.getTargetTemperature()), ((EntityPlayerMP) event.player));
		}
	}

	@SubscribeEvent
	public static void playerClone(PlayerEvent.Clone event) {

		// Player is the new player (in the dimension being traveled to)
		EntityPlayer player = event.getEntityPlayer();
		ITemperature newTemperatureCap = player.getCapability(TemperatureProvider.TEMPERATURE, null);
		double newTemp = newTemperatureCap.getTemperature();
		double newTTemp = newTemperatureCap.getTargetTemperature();

		// This is the player in the from-dimension
		ITemperature oldTemperatureCap = event.getOriginal().getCapability(TemperatureProvider.TEMPERATURE, null);

		// Set new cap to old cap data
		newTemperatureCap.setTemperature(oldTemperatureCap.getTemperature());

		// This event only fires server-side. So we send a packet to the client
		if (!player.world.isRemote) {
			SoleSurvivorPacketHandler.CHANNEL_INSTANCE.sendTo(new SoleSurvivorTempMessage(newTemp, newTTemp), ((EntityPlayerMP) player));
		}
	}

	@SubscribeEvent
	public static void renderText(RenderGameOverlayEvent.Text event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		ITemperature cap = player.getCapability(TEMPERATURE, null);

		event.getLeft().add("TEMP: " + cap.getTemperature());
		event.getLeft().add("TARGET: " + cap.getTargetTemperature());
	}

	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event) {
		ITemperature cap = event.player.getCapability(TEMPERATURE, null);
		EntityPlayer player = event.player;
		double newTemp = cap.getTemperature();
		double newTTemp = cap.getTargetTemperature();

		if (player != null && !player.getEntityWorld().isRemote) {
			newTTemp = BiomeUtil.getPlayerBiomeTemp(new BlockPos(player.posX, player.posY, player.posZ), player.world, 5);

			cap.setTargetTemperature(newTTemp);
			cap.setTemperature(newTemp);
		}

		// Send packet
		if (!event.player.world.isRemote) {
			SoleSurvivorPacketHandler.CHANNEL_INSTANCE.sendTo(new SoleSurvivorTempMessage(newTemp, newTTemp), ((EntityPlayerMP) event.player));
		}
	}

}
