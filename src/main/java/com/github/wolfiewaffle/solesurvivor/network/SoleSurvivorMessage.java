package com.github.wolfiewaffle.solesurvivor.network;

import capability.ITemperature;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SoleSurvivorMessage implements IMessage {

	@CapabilityInject(value = ITemperature.class)
	public static Capability<ITemperature> TEMPERATURE = null;

	// A default constructor is always required
	public SoleSurvivorMessage() {
	}

	private double temperature;

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(int newTemp) {
		temperature = newTemp;
	}

	public SoleSurvivorMessage(double temperature) {
		this.temperature = temperature;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// Writes the int into the buf
		buf.writeDouble(temperature);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// Reads the int back from the buf. Note that if you have multiple
		// values, you must read in the same order you wrote.
		temperature = buf.readDouble();
	}

	// The params of the IMessageHandler are <REQ, REPLY>
	// This means that the first param is the packet you are receiving, and the
	// second is the packet you are returning.
	// The returned packet can be used as a "response" from a sent packet.
	public static class Handler implements IMessageHandler<SoleSurvivorMessage, IMessage> {
		// Do note that the default constructor is required, but implicitly
		// defined in this case
		
		@Override
		public IMessage onMessage(SoleSurvivorMessage message, MessageContext ctx) {
			ClientPacketHandlers.handleSoleSurvivor(message, ctx);

			return null;
		}
	}

}
