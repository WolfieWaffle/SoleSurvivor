package com.github.wolfiewaffle.solesurvivor.util;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TempUtil {

	public static double getPlayerTemp(EntityLiving entityLiving, BlockPos pos, World world, int range) {
		double biomeTemp = 0;
		int cooling = 0;

		biomeTemp = BiomeUtil.getAverageBaseBiomeTemp(pos, world, range);
		biomeTemp = getTempWithAltitude(entityLiving, biomeTemp);
		biomeTemp = getBiomeTempWithWater(biomeTemp, entityLiving);
		// TODO: From enviromine
		biomeTemp -= cooling;
		
		// Add a bit if sleeping
		if (entityLiving instanceof EntityPlayer) {
			if (((EntityPlayer) entityLiving).isPlayerSleeping()) {
				biomeTemp += 10F;
			}
		}

		return biomeTemp;
	}

	private static double getTempWithAltitude(EntityLiving entityLiving, double biomeTemperature) {
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
	public static double getBiomeTempWithWater(double biomeTemp, EntityLiving entityLiving) {
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

}
