package tk.valoeghese.manhattan.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import tk.valoeghese.manhattan.FunniChunkData;
import tk.valoeghese.manhattan.ManhattanProject;
import tk.valoeghese.manhattan.biome.GenBiome;
import tk.valoeghese.manhattan.utils.DimIDManager;

@Mixin(SurfaceChunkGenerator.class)
public abstract class MixinFunniWorldgen implements DimIDManager {
	private Identifier manhattan_Dimension_ID;

	@Shadow
	@Final
	private BlockState defaultBlock;

	@Redirect(method = "Lnet/minecraft/world/gen/chunk/SurfaceChunkGenerator;sampleNoiseColumn([DII)V", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/biome/source/BiomeSource;getBiomeForNoiseGen(III)Lnet/minecraft/world/biome/Biome;"
			))
	private Biome yeet(BiomeSource originalProvider, int gx, int seaLevel, int gz) {
		if (this.manhattan_Dimension_ID == null) {
			throw new IllegalStateException("Stored dimension id is null!");
		}

		if (ManhattanProject.shapeChunk && this.manhattan_Dimension_ID.equals(ManhattanProject.dimensionType)) {
			// haha funni inline
			GenBiome.original = originalProvider.getBiomeForNoiseGen(gx, seaLevel, gz);

			if (ManhattanProject.overwriteModded || ManhattanProject.vanillaBiomes.contains(GenBiome.original)) {
				return FunniChunkData.yeetImpl(gx, gz);
			}
		}

		return originalProvider.getBiomeForNoiseGen(gx, seaLevel, gz);
	}

	@Redirect(method = "buildSurface", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/ChunkRegion;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;"
			))
	private Biome yeet2(ChunkRegion originalProvider, BlockPos pos) {
		if (ManhattanProject.replaceSurfaceBlocks && this.manhattan_Dimension_ID.equals(ManhattanProject.dimensionType)) {
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

	@Override
	public Identifier manhattan_getDimID() {
		return this.manhattan_Dimension_ID;
	}

	@Override
	public void manhattan_setDimID(Identifier id) {
		this.manhattan_Dimension_ID = id;
	}
}
