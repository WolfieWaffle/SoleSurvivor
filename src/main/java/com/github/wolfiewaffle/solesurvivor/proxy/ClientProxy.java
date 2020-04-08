package com.github.wolfiewaffle.solesurvivor.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

//@Mod.EventBusSubscriber()
public class ClientProxy extends CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);

		//MinecraftForge.EVENT_BUS.register(this); // so forge knows about your
													// modelRegistryEvent that
													// is in this clientproxy
													// class
	}

	//@SubscribeEvent
	//public void modelRegistryEvent(ModelRegistryEvent e) {
	//}

	public void init(FMLInitializationEvent e) {
		super.init(e);

		//ItemRenderRegister.registerItemRenderer();
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySpit.class, new RenderSpit());
	}

	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}

}
