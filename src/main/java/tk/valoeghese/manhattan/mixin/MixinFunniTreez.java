package tk.valoeghese.manhattan.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import tk.valoeghese.manhattan.biome.GenBiome;

@Mixin(ChunkGenerator.class)
public class MixinFunniTreez {
	@Redirect(method = "generateFeatures", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/biome/source/BiomeSource;getBiomeForNoiseGen(III)Lnet/minecraft/world/biome/Biome;"
			))
	private Biome yeet(BiomeSource originalProvider, int gx, int gy, int gz) {
		GenBiome.original = originalProvider.getBiomeForNoiseGen(gx, gy, gz);
		return GenBiome.INSTANCE;
	}
}
