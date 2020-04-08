package com.github.wolfiewaffle.solesurvivor;

import org.apache.logging.log4j.Logger;

import com.github.wolfiewaffle.solesurvivor.network.SoleSurvivorMessage;
import com.github.wolfiewaffle.solesurvivor.network.SoleSurvivorPacketHandler;
import com.github.wolfiewaffle.solesurvivor.proxy.CommonProxy;

import capability.ITemperature;
import capability.Temperature;
import capability.TemperatureProvider;
import capability.TemperatureStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@EventBusSubscriber
@Mod(modid = SoleSurvivor.MODID, name = SoleSurvivor.MODNAME, version = SoleSurvivor.VERSION)
public class SoleSurvivor {
	public static final String MODID = "solesurvivor";
	public static final String MODNAME = "Sole Survivor";
	public static final String VERSION = "1.0";

	private static Logger logger;

	// Proxy
	@SidedProxy(clientSide = "com.github.wolfiewaffle.solesurvivor.proxy.ClientProxy", serverSide = "com.github.wolfiewaffle.solesurvivor.proxy.ServerProxy")
	public static CommonProxy proxy;

	// Configuration
	public static Configuration config;

	// Instance so we can refer to the mod later
	@Instance(MODID)
	public static SoleSurvivor instance = new SoleSurvivor();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CapabilityManager.INSTANCE.register(ITemperature.class, new TemperatureStorage(), Temperature::new);
		SoleSurvivorPacketHandler.init();
		SoleSurvivor.proxy.preInit(event);
	}

	@EventHandler
	public void Init(FMLInitializationEvent event) {
		SoleSurvivor.proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		SoleSurvivor.proxy.postInit(event);
	}

	@SubscribeEvent
	public static void capabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(MODID, "TEMPERATURE"), new TemperatureProvider(new Temperature()));
		}
	}

	@CapabilityInject(value = ITemperature.class)
	public static Capability<ITemperature> TEMPERATURE = null;

	@SubscribeEvent
	public static void breakBlock(BreakEvent event) {
		System.out.println(event.getPlayer());
		if (event.getPlayer() != null) {
			EntityPlayer player = event.getPlayer();
			ITemperature cap = player.getCapability(TEMPERATURE, null);

//			System.out.println("BEFORE " + cap.getTemperature());
			cap.setTemperature(cap.getTemperature() + 1);
//			System.out.println("AFTER " + cap.getTemperature());
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
	public static void playerTick(PlayerTickEvent event) {
		ITemperature cap = event.player.getCapability(TEMPERATURE, null);
		double newTemp = cap.getTemperature() + 1;

		if (event.player != null && !event.player.getEntityWorld().isRemote) {
			System.out.println("TEMP " + cap.getTemperature());
			EntityPlayer player = event.player;

			// System.out.println("BEFORE " + cap.getTemperature());
			cap.setTemperature(newTemp);
			// System.out.println("AFTER " + cap.getTemperature());
		}

		// Send packet
		if (!event.player.world.isRemote) {
			SoleSurvivorPacketHandler.CHANNEL_INSTANCE.sendTo(new SoleSurvivorMessage(newTemp), ((EntityPlayerMP) event.player));
		}
	}

// IGNORE COPIED FROM A PREVIOUS MOD
	@Config(modid = MODID) // TODO: Config
	public static class CONFIG {
		@Comment({ "If this is true, no items in itemList can be used in the campfire. If false, only items listed can be used." })
		public static boolean isItemListBlacklist = true;

		@Comment({ "This is the item list used with isItemBlacklist to determine what items can be placed onto the spit." })
		public static String[] itemList = { "minecraft:example" };
	}
}
