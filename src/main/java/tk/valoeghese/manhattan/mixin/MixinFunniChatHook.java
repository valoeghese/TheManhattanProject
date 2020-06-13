package tk.valoeghese.manhattan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinFunniChatHook {
	@Inject(method = "onGameMessage", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
	public void onGameMessage(ChatMessageC2SPacket packet) {
		packet.getChatMessage();
	}
}
