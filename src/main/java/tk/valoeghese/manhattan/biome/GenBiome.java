package tk.valoeghese.manhattan.biome;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.BambooJungleBiome;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public final class GenBiome extends BambooJungleBiome {
	public static final GenBiome INSTANCE = new GenBiome(); // not registered, since this is a dummy class for altering worldgen

	public static int xCache;
	public static int zCache;

	public static float gDepth;
	public static float gDepthVariation;
	public static float gScale;

	public static Biome original;
	public static boolean nether;
	public static SurfaceConfigProvider config;

	@Override
	public void buildSurface(Random random, Chunk chunk, int x, int z, int worldHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed) {
		SurfaceBuilder<TernarySurfaceConfig> surfaceBuilder = nether ? SurfaceBuilder.NETHER : SurfaceBuilder.DEFAULT;

		if (original.getSurfaceBuilder().surfaceBuilder == SurfaceBuilder.BASALT_DELTAS) {
			surfaceBuilder = SurfaceBuilder.BASALT_DELTAS;
		}

		surfaceBuilder.initSeed(seed);
		surfaceBuilder.generate(random, chunk, original, x, z, worldHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config.getSurface(random, x, z, nether));
	}
}
