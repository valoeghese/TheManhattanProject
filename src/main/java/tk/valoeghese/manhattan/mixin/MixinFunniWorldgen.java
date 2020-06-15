package tk.valoeghese.manhattan.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import tk.valoeghese.manhattan.FunniChunkData;
import tk.valoeghese.manhattan.ManhattanProject;
import tk.valoeghese.manhattan.biome.GenBiome;
import tk.valoeghese.manhattan.utils.DimIDGetter;

@Mixin(SurfaceChunkGenerator.class)
public abstract class MixinFunniWorldgen implements DimIDGetter {
	private DimensionType manhattan_Dimension;
	private Identifier manhattan_Dimension_ID;

	@Shadow
	@Final
	private BlockState defaultBlock;

	@Redirect(method = "Lnet/minecraft/world/gen/chunk/SurfaceChunkGenerator;sampleNoiseColumn([DII)V", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/biome/source/BiomeSource;getBiomeForNoiseGen(III)Lnet/minecraft/world/biome/Biome;"
			))
	private Biome yeet(BiomeSource originalProvider, int gx, int seaLevel, int gz) {
		if (ManhattanProject.shapeChunk && manhattan_Dimension_ID.equals(ManhattanProject.dimensionType)) {
			// haha funni inline
			GenBiome.original = originalProvider.getBiomeForNoiseGen(gx, seaLevel, gz);

			if (ManhattanProject.overwriteModded || ManhattanProject.vanillaBiomes.contains(GenBiome.original)) {
				Object game = FabricLoader.getInstance().getGameInstance();
				MinecraftServer server = null;

				if (game instanceof MinecraftDedicatedServer) {
					server = (MinecraftServer) game;
				} else if (game instanceof MinecraftClient) {
					server = ((MinecraftClient) game).getServer();
				}

				if (server == null) {
					System.out.println("[DEBUG] no server object found. Probably saving and exiting world?");
					return GenBiome.INSTANCE;
				}

				GenBiome.server = server;
				FunniChunkData.load(server);
				GenBiome.xCache = gx;
				GenBiome.zCache = gz;
				return GenBiome.INSTANCE;
			}
		}

		return originalProvider.getBiomeForNoiseGen(gx, seaLevel, gz);
	}

	@Redirect(method = "buildSurface", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/ChunkRegion;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;"
			))
	private Biome yeet2(ChunkRegion originalProvider, BlockPos pos) {
		if (ManhattanProject.replaceSurfaceBlocks && manhattan_Dimension_ID.equals(ManhattanProject.dimensionType)) {
			GenBiome.original = originalProvider.getBiome(pos);

			if (ManhattanProject.overwriteModded || ManhattanProject.vanillaBiomes.contains(GenBiome.original)) {
				MinecraftServer server = ((FunniWorldGetter) originalProvider).getWorld().getServer();
				FunniChunkData.load(server);
				GenBiome.server = server;
				return GenBiome.INSTANCE;
			}
		}

		return originalProvider.getBiome(pos);
	}

	@Inject(at = @At("HEAD"), method = "populateNoise")
	private void yeet3(WorldAccess world, StructureAccessor accessor, Chunk chunk, CallbackInfo info) {
		DimensionType t = world.getDimension();

		if (this.manhattan_Dimension != t) {
			this.manhattan_Dimension = t;
			this.manhattan_Dimension_ID = ManhattanProject.getDimensionId(this.manhattan_Dimension);
		}
	}

	@Override
	public Identifier manhattan_getDimID() {
		return this.manhattan_Dimension_ID;
	}
}
