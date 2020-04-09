package com.github.wolfiewaffle.solesurvivor;

import org.apache.logging.log4j.Logger;

import com.github.wolfiewaffle.solesurvivor.capability.ITemperature;
import com.github.wolfiewaffle.solesurvivor.capability.Temperature;
import com.github.wolfiewaffle.solesurvivor.capability.TemperatureStorage;
import com.github.wolfiewaffle.solesurvivor.network.SoleSurvivorPacketHandler;
import com.github.wolfiewaffle.solesurvivor.proxy.CommonProxy;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@EventBusSubscriber
@Mod(modid = SoleSurvivor.MODID, name = SoleSurvivor.MODNAME, version = SoleSurvivor.VERSION)
public class SoleSurvivor {
	public static final String MODID = "solesurvivor";
	public static final String MODNAME = "Sole Survivor";
	public static final String VERSION = "1.0";

	@SuppressWarnings("unused")
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

		// No idea what this is I just know I need it
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

	// IGNORE COPIED FROM A PREVIOUS MOD
	@Config(modid = MODID) // TODO: Config
	public static class CONFIG {
		@Comment({ "If this is true, no items in itemList can be used in the campfire. If false, only items listed can be used." })
		public static boolean isItemListBlacklist = true;

		@Comment({ "This is the item list used with isItemBlacklist to determine what items can be placed onto the spit." })
		public static String[] itemList = { "minecraft:example" };
	}

}
