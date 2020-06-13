package tk.valoeghese.manhattan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import tk.valoeghese.manhattan.FunniChunkData;
import tk.valoeghese.manhattan.biome.GenBiome;
import tk.valoeghese.manhattan.biome.NoiseProperties;
import tk.valoeghese.manhattan.utils.FunniMessageCompiler;

@Mixin(Biome.class)
public class MixinFunniBiome {
	@Inject(at = @At("HEAD"), method = "getDepth", cancellable = true)
	private void genDepth(CallbackInfoReturnable<Float> info) {
		if ((Object) this == GenBiome.INSTANCE) {
			float noise = (float) FunniMessageCompiler.NOISE.sample((double) GenBiome.xCache / 12.0, (double) GenBiome.zCache / 12.0);

			int chunkX = GenBiome.xCache >> 4;
			int chunkZ = GenBiome.zCache >> 4;

			int upperChunkX = chunkX + 1;
			int upperChunkZ = chunkZ + 1;

			float xProgress = (float) (GenBiome.xCache & 4) / 4.0f;
			float zProgress = (float) (GenBiome.zCache & 4) / 4.0f;

			NoiseProperties LOWER_LEFT = FunniChunkData.getNoiseProperties(GenBiome.server, chunkX, chunkZ);
			NoiseProperties UPPER_LEFT = FunniChunkData.getNoiseProperties(GenBiome.server, upperChunkX, chunkZ);
			NoiseProperties LOWER_RIGHT = FunniChunkData.getNoiseProperties(GenBiome.server, chunkX, upperChunkZ);
			NoiseProperties UPPER_RIGHT = FunniChunkData.getNoiseProperties(GenBiome.server, upperChunkX, upperChunkZ);

			float gDepthVariation = MathHelper.lerp(xProgress,
					MathHelper.lerp(zProgress, LOWER_LEFT.thicknessVariation, UPPER_LEFT.thicknessVariation),
					MathHelper.lerp(zProgress, LOWER_RIGHT.thicknessVariation, UPPER_RIGHT.thicknessVariation));

			float gDepth = MathHelper.lerp(xProgress,
					MathHelper.lerp(zProgress, LOWER_LEFT.depth, UPPER_LEFT.depth),
					MathHelper.lerp(zProgress, LOWER_RIGHT.depth, UPPER_RIGHT.depth));

			if (gDepthVariation > 1.3f && gDepthVariation < 1.5f && gDepth > 0.23f) {
				if (gDepthVariation < 1.4f) {
					noise = noise > 0.12f ? 2 : 0;
				} else {
					noise = noise > 0.12f ? 0 : 2;
				}
			}

			noise = (float) (gDepth + gDepthVariation * noise);

			//NoiseProperties
			// oceans are oceans.
			if (GenBiome.original.getCategory() == Biome.Category.OCEAN) {
				if (noise > -0.2) {
					if (noise < 0) {
						noise = -0.2f;
					} else {
						noise = -0.2f * noise;

						while (noise < -1.8) {
							noise /= 2;
						}
					}
				}
			}

			// System.out.println(noise);
			info.setReturnValue(noise);
		}
	}

	@Inject(at = @At("HEAD"), method = "getScale", cancellable = true)
	private void genScale(CallbackInfoReturnable<Float> info) {
		if ((Object) this == GenBiome.INSTANCE) {
			int chunkX = (GenBiome.xCache >> 4);
			int chunkZ = (GenBiome.zCache >> 4);

			int upperChunkX = chunkX + 1;
			int upperChunkZ = chunkZ + 1;

			float xProgress = (float) (GenBiome.xCache & 4) / 4.0f;
			float zProgress = (float) (GenBiome.zCache & 4) / 4.0f;

			NoiseProperties LOWER_LEFT = FunniChunkData.getNoiseProperties(GenBiome.server, chunkX, chunkZ);
			NoiseProperties UPPER_LEFT = FunniChunkData.getNoiseProperties(GenBiome.server, upperChunkX, chunkZ);
			NoiseProperties LOWER_RIGHT = FunniChunkData.getNoiseProperties(GenBiome.server, chunkX, upperChunkZ);
			NoiseProperties UPPER_RIGHT = FunniChunkData.getNoiseProperties(GenBiome.server, upperChunkX, upperChunkZ);

			float gScale = MathHelper.lerp(xProgress,
					MathHelper.lerp(zProgress, LOWER_LEFT.scale, UPPER_LEFT.scale),
					MathHelper.lerp(zProgress, LOWER_RIGHT.scale, UPPER_RIGHT.scale));
			
			info.setReturnValue(gScale);
		}
	}
}
