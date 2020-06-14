package tk.valoeghese.manhattan.biome;

import java.util.Random;
import java.util.function.Consumer;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.BambooJungleBiome;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public final class GenBiome extends BambooJungleBiome {
	public static final GenBiome INSTANCE = new GenBiome(); // not registered, since this is a dummy class for altering worldgen

	public static int xCache;
	public static int zCache;

	public static Biome original;
	public static boolean nether;
	public static SurfaceConfigProvider config;

	public static MinecraftServer server;

	public void setVegetalFeatures(Consumer<Consumer<ConfiguredFeature<?, ?>>> vegetalFeatureProvider) {
		this.features.get(GenerationStep.Feature.VEGETAL_DECORATION).clear();

		vegetalFeatureProvider.accept(cf -> this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, cf));
	}

	@Override
	public void buildSurface(Random random, Chunk chunk, int x, int z, int worldHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed) {
		SurfaceBuilder<TernarySurfaceConfig> surfaceBuilder = nether ? SurfaceBuilder.NETHER : SurfaceBuilder.DEFAULT;

		if (original.getSurfaceBuilder().surfaceBuilder == SurfaceBuilder.BASALT_DELTAS) {
			surfaceBuilder = SurfaceBuilder.BASALT_DELTAS;
		}

		/*int configx = x + (int) (7 * FunniMessageCompiler.NOISE.sample((double) z * 0.12f));
		int configz = z + (int) (7 * FunniMessageCompiler.NOISE.sample((double) (x + 9) * 0.12f))*/;

		surfaceBuilder.initSeed(seed);
		surfaceBuilder.generate(random, chunk, original, x, z, worldHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config.getSurface(random, x, z, /*configx, configz,*/ nether));
	}

	@Override
	public void generateFeatureStep(GenerationStep.Feature step, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, ServerWorldAccess serverWorldAccess, long populationSeed, ChunkRandom chunkRandom, BlockPos pos) {
		if (step == GenerationStep.Feature.VEGETAL_DECORATION) {
			super.generateFeatureStep(step, structureAccessor, chunkGenerator, serverWorldAccess, populationSeed, chunkRandom, pos);
		} else {
			original.generateFeatureStep(step, structureAccessor, chunkGenerator, serverWorldAccess, populationSeed, chunkRandom, pos);
		}
	}
}
