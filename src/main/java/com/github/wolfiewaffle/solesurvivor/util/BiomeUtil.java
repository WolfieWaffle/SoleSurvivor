package com.github.wolfiewaffle.solesurvivor.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class BiomeUtil {

	public static double getBiomeTemperature(Biome biome) {
		return biome.getDefaultTemperature();
	}

	public static double getBiomeTemperature(Biome biome, double x, double y, double z) {
		return biome.getTemperature(new BlockPos(x, y, z));
	}

	public static Biome getBiomeAtLocation(World world, BlockPos pos) {
		Chunk testChunk = world.getChunkFromBlockCoords(pos);
		Biome checkBiome = testChunk.getBiome(pos, world.getBiomeProvider());
		return checkBiome;
	}

	public static double getSurroundingBiomeTempAverage(BlockPos pos, World world, int range) {
		double totalBiomeTemps = 0;
		int count = 0;

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
						totalBiomeTemps += getBiomeTemperature(checkBiome, Xmodified, Ymodified, Zmodified);
						count++;
					}
				}
			}
		}

		return totalBiomeTemps / count;
	}

}