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
			float resultDepth = (float) FunniMessageCompiler.NOISE.sample((double) GenBiome.xCache / 12.0, (double) GenBiome.zCache / 12.0);

			int chunkX = GenBiome.xCache >> 2;
			int chunkZ = GenBiome.zCache >> 2;

			int upperChunkX = ((GenBiome.xCache >> 1) + 1) >> 1;
			int upperChunkZ = ((GenBiome.zCache >> 1) + 1) >> 1;

			float xProgress = (float) (GenBiome.xCache & 2) / 2.0f;
			float zProgress = (float) (GenBiome.zCache & 2) / 2.0f;

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
					resultDepth = resultDepth > 0.12f ? 2.8f : 0;
				} else {
					resultDepth = resultDepth > 0.12f ? 0 : 2.8f;
				}
			}

			resultDepth = (float) (gDepth + gDepthVariation * resultDepth);

			//NoiseProperties
			// oceans are oceans.
			if (GenBiome.original.getCategory() == Biome.Category.OCEAN) {
				if (resultDepth > -0.3) {
					if (resultDepth < 0) {
						resultDepth = -0.3f;
					} else {
						resultDepth = -0.3f * resultDepth;

						if (resultDepth > -0.3f) {
							resultDepth = 0.3f;
						} else {
							while (resultDepth < -1.8) {
								resultDepth /= 2;
							}
						}
					}
				}
			}

			if (GenBiome.original.getDepth() > 0.3) {
				resultDepth += (GenBiome.original.getDepth() / 2);
			}

			// System.out.println(noise);
			info.setReturnValue(resultDepth);
		}
	}

	@Inject(at = @At("HEAD"), method = "getScale", cancellable = true)
	private void genScale(CallbackInfoReturnable<Float> info) {
		if ((Object) this == GenBiome.INSTANCE) {
			int chunkX = (GenBiome.xCache >> 2);
			int chunkZ = GenBiome.zCache >> 2;

			int upperChunkX = ((GenBiome.xCache >> 1) + 1) >> 1;
			int upperChunkZ = ((GenBiome.zCache >> 1) + 1) >> 1;

			float xProgress = (float) (GenBiome.xCache & 2) / 2.0f;
			float zProgress = (float) (GenBiome.zCache & 2) / 2.0f;

			NoiseProperties LOWER_LEFT = FunniChunkData.getNoiseProperties(GenBiome.server, chunkX, chunkZ);
			NoiseProperties UPPER_LEFT = FunniChunkData.getNoiseProperties(GenBiome.server, upperChunkX, chunkZ);
			NoiseProperties LOWER_RIGHT = FunniChunkData.getNoiseProperties(GenBiome.server, chunkX, upperChunkZ);
			NoiseProperties UPPER_RIGHT = FunniChunkData.getNoiseProperties(GenBiome.server, upperChunkX, upperChunkZ);

			float gScale = MathHelper.lerp(xProgress,
					MathHelper.lerp(zProgress, LOWER_LEFT.scale, UPPER_LEFT.scale),
					MathHelper.lerp(zProgress, LOWER_RIGHT.scale, UPPER_RIGHT.scale));

			if (gScale < -0.01f) { // idk if this is neccesary
				gScale = -0.02f;
			}

			if (GenBiome.original.getScale() > 0.45f && gScale < 0.45f) {
				gScale += 0.4f;
			}

			info.setReturnValue(gScale);
		}
	}
}
