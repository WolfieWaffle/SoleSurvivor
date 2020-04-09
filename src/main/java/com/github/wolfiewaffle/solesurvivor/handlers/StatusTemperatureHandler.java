package com.github.wolfiewaffle.solesurvivor.handlers;

import com.github.wolfiewaffle.solesurvivor.util.BiomeUtil;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class StatusTemperatureHandler {

	public static void getTargetTemp(BlockPos pos, EntityLiving entity, int range) {
		double finalTargetTemp = 0;
		World world = entity.world;

		finalTargetTemp = getSurroundingBiomeData(pos, world, range);
		finalTargetTemp = addAmplitudeModifier(entity, finalTargetTemp);
	}

	private static double getSurroundingBiomeData(BlockPos pos, World world, int range) {
		double totalBiomeTemps = 0;

		for (int Xoffset = -range; Xoffset <= range; Xoffset++) {
			for (int Yoffset = -range; Yoffset <= range; Yoffset++) {
				for (int Zoffset = -range; Zoffset <= range; Zoffset++) {
					int Xmodified = pos.getX() + Xoffset;
					int Ymodified = pos.getY() + Yoffset;
					int Zmodified = pos.getZ() + Zoffset;

					// This I assume gets the biome, though I don't know why it
					// needs to use getBiome
					Chunk testChunk = world.getChunkFromBlockCoords(new BlockPos(Xmodified, Ymodified, Zmodified));
					Biome checkBiome = testChunk.getBiome(new BlockPos(Xmodified, Ymodified, Zmodified), world.getBiomeProvider());

					if (checkBiome != null) {
						totalBiomeTemps += BiomeUtil.getBiomeTemperature(checkBiome, Xmodified, Ymodified, Zmodified);
					}
				}
			}
		}

		return totalBiomeTemps;
	}

	private static double addAmplitudeModifier(EntityLiving entity, double baseTemp) {
		double highTemp = -30F; // Max temp at high altitude
		double lowTemp = 30F; // Min temp at low altitude (Geothermal Heating)

		// TODO: document this Eviromine code
		if (entity.posY < 48) {
			if (lowTemp - baseTemp > 0) {
				baseTemp += (lowTemp - baseTemp) * (1F - (entity.posY / 48F));
			}
		} else if (entity.posY > 90 && entity.posY < 256) {
			if (highTemp - baseTemp < 0) {
				baseTemp -= MathHelper.abs((float) (highTemp - baseTemp)) * ((entity.posY - 90F) / 166F);
			}
		} else if (entity.posY >= 256) {
			baseTemp = baseTemp < highTemp ? baseTemp : highTemp;
		}

		return baseTemp;
	}

}
