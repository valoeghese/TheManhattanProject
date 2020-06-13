package tk.valoeghese.manhattan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.biome.Biome;
import tk.valoeghese.manhattan.biome.GenBiome;
import tk.valoeghese.manhattan.biome.NoiseProperties;
import tk.valoeghese.manhattan.utils.FunniMessageCompiler;

@Mixin(Biome.class)
public class MixinFunniBiome {
	@Inject(at = @At("HEAD"), method = "getDepth", cancellable = true)
	private void genDepth(CallbackInfoReturnable<Float> info) {
		if ((Object) this == GenBiome.INSTANCE) {
			float noise = (float) FunniMessageCompiler.NOISE.sample((double) GenBiome.xCache / 12.0, (double) GenBiome.zCache / 12.0);

			if (GenBiome.gDepthVariation > 1.3f && GenBiome.gDepthVariation < 1.5f && GenBiome.gDepth > 0.23f) {
				if (GenBiome.gDepthVariation < 1.4f) {
					noise = noise > 0.12f ? 2 : 0;
				} else {
					noise = noise > 0.12f ? 0 : 2;
				}
			}

			noise = (float) (GenBiome.gDepth + GenBiome.gDepthVariation * noise);

			int chunkX = GenBiome.xCache >> 4;
			int chunkZ = GenBiome.zCache >> 4;

			int upperChunkX = chunkX + 1;
			int upperChunkZ = chunkZ + 1;

			double xProgress = (GenBiome.xCache & 4) / 4;
			double zProgress = (GenBiome.zCache & 4) / 4;

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
			// System.out.println("Scale = " + GenBiome.gScale);
			info.setReturnValue(GenBiome.gScale);
		}
	}
}
