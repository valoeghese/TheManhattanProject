package tk.valoeghese.manhattan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

@Mixin(SurfaceChunkGenerator.class)
public class MixinSurfaceChunkGenerator {
	@Redirect(method = "sampleNoiseColumn", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/biome/source/BiomeAccess$Storage;getBiomeForNoiseGen(III)Lnet/minecraft/world/biome/Biome;"
			))
	private Biome yeet(BiomeAccess.Storage original, int gx, int seaLevel, int gz) {
		return null;
		// TODO everithing
	}
}
