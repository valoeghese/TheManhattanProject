package tk.valoeghese.manhattan.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import tk.valoeghese.manhattan.FunniChunkData;
import tk.valoeghese.manhattan.biome.GenBiome;

@Mixin(SurfaceChunkGenerator.class)
public abstract class MixinSurfaceChunkGenerator {
	@Shadow
	@Final
	private BlockState defaultBlock;

	@Redirect(method = "Lnet/minecraft/world/gen/chunk/SurfaceChunkGenerator;sampleNoiseColumn([DII)V", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/biome/source/BiomeSource;getBiomeForNoiseGen(III)Lnet/minecraft/world/biome/Biome;"
			))
	private Biome yeet(BiomeSource originalProvider, int gx, int seaLevel, int gz) {
		Object game = FabricLoader.getInstance().getGameInstance();
		MinecraftServer server = null;

		if (game instanceof MinecraftDedicatedServer) {
			server = (MinecraftServer) game;
		} else if (game instanceof MinecraftClient){
			server = ((MinecraftClient) game).getServer();
		}

		FunniChunkData.load(server);
		GenBiome.xCache = gx;
		GenBiome.zCache = gz;
		return GenBiome.INSTANCE;
	}

	@Redirect(method = "buildSurface", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/ChunkRegion;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;"
			))
	private Biome yeet2(ChunkRegion originalProvider, BlockPos pos) {
		FunniChunkData.load(((FunniWorldGetter) originalProvider).getWorld().getServer());
		GenBiome.original = originalProvider.getBiome(pos);
		GenBiome.nether = this.defaultBlock.getBlock() == Blocks.NETHERRACK;
		return GenBiome.INSTANCE;
	}
}
