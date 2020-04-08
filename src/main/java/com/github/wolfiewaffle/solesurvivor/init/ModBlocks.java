package com.github.wolfiewaffle.solesurvivor.init;

import com.github.wolfiewaffle.solesurvivor.SoleSurvivor;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = SoleSurvivor.MODID)
public final class ModBlocks {

	@SubscribeEvent
	public static void createBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> blockRegistry = event.getRegistry();
	}

}
