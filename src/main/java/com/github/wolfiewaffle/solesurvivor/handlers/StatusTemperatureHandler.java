package com.github.wolfiewaffle.solesurvivor.handlers;

import com.github.wolfiewaffle.solesurvivor.util.BiomeUtil;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class StatusTemperatureHandler {

	public static float[] getSurroundingData(EntityLivingBase entityLiving, int range) {

		float[] data = new float[8];
		float temp = -999F;
		float cooling = 0;

		int x = MathHelper.floor(entityLiving.posX);
		int y = MathHelper.floor(entityLiving.posY);
		int z = MathHelper.floor(entityLiving.posZ);

		// Get world
		World world = entityLiving.getEntityWorld();
		
		// If the world is null, return
		if (world == null) {
			return data;
		}

		// Get the chunk
		Chunk chunk = entityLiving.getEntityWorld().getChunkFromBlockCoords(new BlockPos(x, y, z));

		// Null chunk
		if (chunk == null) {
			return data;
		}

		// Get biome
		Biome biome = world.getBiome(new BlockPos(entityLiving.posX, entityLiving.posY, entityLiving.posZ));

		if (biome == null) {
			return data;
		}

		// TODO: ???
		float surBiomeTemps = 0;
		int biomeTempChecks = 0;
		
		// Is day?
		boolean isDay = world.isDaytime();
		
		//Note: This is offset slightly so that heat peaks after noon.
		float scale = 1.25F; // From Enviromine: Anything above 1 forces the maximum and minimum temperatures to plateau when they're reached
		// TODO: clean up this Enviromine function
		float dayPercent = MathHelper.clamp((float)(Math.sin(Math.toRadians(((world.getWorldTime()%24000L)/24000D)*360F - 30F))*0.5F + 0.5F)*scale, 0F, 1F);
		
		// TODO: ???
		int lightLev = 0;
		int blockLightLev = 0;		
		
		// Biome check
		for(int areaX = -range; areaX <= range; areaX++)
		{
			for(int areaY = -range; areaY <= range; areaY++)
			{
				for(int areaZ = -range; areaZ <= range; areaZ++)
				{
					if(y == 0)
					{
						// This I assume gets the biome, though I don't know why it needs to use getBiome 
						Chunk testChunk = world.getChunkFromBlockCoords(new BlockPos((x + areaX), areaY, (z + areaZ)));
						Biome checkBiome = testChunk.getBiome(new BlockPos((x + areaX), areaY, (z + areaZ)), world.getBiomeProvider());
						
						if(checkBiome != null)
						{}
							else
							{
								surBiomeTemps += BiomeUtil.getBiomeTemperature(checkBiome, (x + areaX), (y + areaY), (z + areaZ));
							}
							
							biomeTempChecks += 1;
						}
					}
				}
			}
		
		return null;
	}

	private static void getSurroundingBiomeData(BlockPos pos, World world, int range) {
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
	}

}
