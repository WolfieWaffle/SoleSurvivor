package com.github.wolfiewaffle.solesurvivor.util;

import java.util.ArrayList;

import com.github.wolfiewaffle.solesurvivor.capability.ITemperature;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class TempUtil {

	// Capability
	@CapabilityInject(value = ITemperature.class)
	public static Capability<ITemperature> TEMPERATURE = null;

	public static double getPlayerTemp(EntityLivingBase entityLiving, BlockPos pos, World world, int range) {
		ITemperature cap = entityLiving.getCapability(TEMPERATURE, null);
		double oldTemp = cap.getTemperature();
		double newTemp = 0;
		float dropSpeed = 0.001F;
		float riseSpeed = 0.001F;
		int cooling = 0;
		float fireProt = 0;
		Biome biome = BiomeUtil.getBiomeAtLocation(world, pos);
		// [Enviromine]: Note: This is offset slightly so that heat peaks after noon.
		float scale = 1.25F; // [Enviromine]: Anything above 1 forces the maximum and minimum temperatures to plateau when they're reached
		float dayPercent = MathHelper.clamp((float) (Math.sin(Math.toRadians(((world.getWorldTime() % 24000L) / 24000D) * 360F - 30F)) * 0.5F + 0.5F) *scale, 0F, 1F);

		newTemp = BiomeUtil.getAverageBaseBiomeTemp(pos, world, range);
		newTemp = getTempWithAltitude(entityLiving, newTemp);
		newTemp = getBiomeTempWithWater(newTemp, entityLiving);
		// TODO: From enviromine
		newTemp -= cooling;
		
		// Add a bit if sleeping
		if (entityLiving instanceof EntityPlayer) {
			if (((EntityPlayer) entityLiving).isPlayerSleeping()) {
				newTemp += 10F;
			}
		}

		// If raining
		if (world.isRaining() && biome.getRainfall() > 0.0F) {
			newTemp -= 10F;
			// Enviromine
			// animalHostility = -1;

			if (world.canBlockSeeSky(pos)) {
				// Enviromine
				// dropSpeed = 0.01F;
			}
		}

		// If in shade
		if (!world.canBlockSeeSky(pos) && world.isDaytime() && !world.isRaining()) {
			newTemp -= 2.5F;
		}

		// Day modifier
		newTemp -= (1F - dayPercent) * 10F;
		if (biome.getRainfall() <= 0F) {

			// Day is stronger when there is no rain
			newTemp -= (1F - dayPercent) * 30F;
		}

		// Entity modifiers placeholder

		// TODO: figure out what this variable is
		double tempFin = 0F;

		if (oldTemp > newTemp) {
			tempFin = (newTemp + oldTemp) / 2;
			if (oldTemp > (newTemp + 5F)) {
				riseSpeed = 0.005F;
			}
		} else {
			tempFin = newTemp;
		}

		return tempFin;
	}

	private static double getTempWithAltitude(EntityLivingBase entityLiving, double biomeTemperature) {
		double highTemp = -30F; // Max temp at high altitude
		double lowTemp = 30F; // Min temp at low altitude (Geothermal Heating)

		// TODO: Document this from Enviromine
		if (entityLiving.posY < 48) {
			if (lowTemp - biomeTemperature > 0) {
				biomeTemperature += (lowTemp - biomeTemperature) * (1F - (entityLiving.posY / 48F));
			}
		} else if (entityLiving.posY > 90 && entityLiving.posY < 256) {
			if (highTemp - biomeTemperature < 0) {
				biomeTemperature -= MathHelper.abs((float) (highTemp - biomeTemperature)) * ((entityLiving.posY - 90F) / 166F);
			}
		} else if (entityLiving.posY >= 256) {
			biomeTemperature = biomeTemperature < highTemp ? biomeTemperature : highTemp;
		}

		return biomeTemperature;
	}

	/**
	 * Apply to modify temp according to if in water
	 * @param biomeTemp
	 * @param entityLiving
	 * @return
	 */
	private static double getBiomeTempWithWater(double biomeTemp, EntityLivingBase entityLiving) {
		if (entityLiving.isInWater()) {
			if (biomeTemp > 25F) {
				if (biomeTemp > 50F) {
					biomeTemp -= 50F;
				} else {
					biomeTemp = 25F;
				}
			}
			// From enviromine
			// dropSpeed = 0.01F;
		}
		return biomeTemp;
	}

	private static double getFireProt(EntityLivingBase entityLiving) {
		double fireProt = 0;
		Iterable<ItemStack> armor = entityLiving.getArmorInventoryList();

		ItemStack helmet = armor.iterator().next();
		ItemStack plate = armor.iterator().next();
		ItemStack legs = armor.iterator().next();
		ItemStack boots = armor.iterator().next();
		
		float tempMultTotal = 0F;
		float addTemp = 0F;
		
		NBTTagList enchTags = helmet.getEnchantmentTagList();

		if (enchTags != null) {
			for (int index = 0; index < enchTags.tagCount(); index++) {
				int enID = ((NBTTagCompound) enchTags.getCompoundTagAt(index)).getShort("id");
				int enLV = ((NBTTagCompound) enchTags.getCompoundTagAt(index)).getShort("lvl");
				
				System.out.println("ID " + enID);

//				if (enID == Enchantment.respiration.effectId) {
//					leaves += 3F * enLV;
//				} else if (enID == Enchantment.getEnchantmentByLocation("minecraft:fire_resistance")) {
//					fireProt += enLV;
//				}
			}
		}

//		TODO: this
		return 0;

//		System.out.println(helmet + " " + plate + " " + legs + " " + boots);
	}

}
