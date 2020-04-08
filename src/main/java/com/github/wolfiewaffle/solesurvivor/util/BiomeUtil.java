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

}