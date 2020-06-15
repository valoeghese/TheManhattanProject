package tk.valoeghese.manhattan.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import tk.valoeghese.manhattan.ManhattanProject;
import tk.valoeghese.manhattan.biome.GenBiome;
import tk.valoeghese.manhattan.utils.DimIDManager;

@Mixin(ChunkGenerator.class)
public class MixinFunniTreez {
	@Redirect(method = "generateFeatures", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/biome/source/BiomeSource;getBiomeForNoiseGen(III)Lnet/minecraft/world/biome/Biome;"
			))
	private Biome yeet(BiomeSource originalProvider, int gx, int gy, int gz) {
		if (((Object) this) instanceof SurfaceChunkGenerator) {
			if (ManhattanProject.populateVegetation && ((DimIDManager) this).manhattan_getDimID().equals(ManhattanProject.dimensionType)) {
				GenBiome.original = originalProvider.getBiomeForNoiseGen(gx, gy, gz);

				if (ManhattanProject.overwriteModded || ManhattanProject.vanillaBiomes.contains(GenBiome.original)) {
					return GenBiome.INSTANCE;
				}
			}
		}

		return originalProvider.getBiomeForNoiseGen(gx, gy, gz);
	}
}
