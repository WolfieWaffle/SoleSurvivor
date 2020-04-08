package com.github.wolfiewaffle.solesurvivor.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class SoleSurvivorPacketHandler {
	private static int discriminator = 0;

	public static final SimpleNetworkWrapper CHANNEL_INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("solesurvivor");

	public static void init() {
		CHANNEL_INSTANCE.registerMessage(SoleSurvivorMessage.Handler.class, SoleSurvivorMessage.class, discriminator++, Side.CLIENT);
	}

}
