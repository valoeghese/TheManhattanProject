package tk.valoeghese.manhattan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import tk.valoeghese.manhattan.FunniChunkData;

@Mixin(MinecraftServer.class)
public class MixinFunniSaveData {
	@Inject(at = @At("RETURN"), method = "save")
	private void funniSave(boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> data) {
		if (!bl) {
			FunniChunkData.saveFile((MinecraftServer) (Object) this);
		}
	}
}
