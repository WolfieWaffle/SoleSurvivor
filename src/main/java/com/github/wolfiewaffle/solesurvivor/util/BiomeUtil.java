package com.github.wolfiewaffle.solesurvivor.util;

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

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

	public static String getBiomeWater(Biome biome) {
		int waterColour = biome.getWaterColorMultiplier();
		boolean looksBad = false;

		if (waterColour != 16777215) {
			Color bColor = new Color(waterColour);

			if (bColor.getRed() < 200 || bColor.getGreen() < 200 || bColor.getBlue() < 200) {
				looksBad = true;
			}
		}

		ArrayList<Type> typeList = new ArrayList<>();
		typeList.addAll(BiomeDictionary.getTypes(biome));

		if (typeList.contains(Type.SWAMP) || typeList.contains(Type.JUNGLE) || typeList.contains(Type.DEAD) || typeList.contains(Type.WASTELAND) || looksBad) {
			return "dirty";
		} else if (typeList.contains(Type.OCEAN) || typeList.contains(Type.BEACH)) {
			return "salty";
		} else if (typeList.contains(Type.SNOWY) || typeList.contains(Type.CONIFEROUS) || biome.getDefaultTemperature() < 0F) {
			return "cold";
		} else {
			return "dirty";
			// removed "clean" as a design choice
		}
	}

}