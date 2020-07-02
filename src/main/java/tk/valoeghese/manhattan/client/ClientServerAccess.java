package tk.valoeghese.manhattan.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

public class ClientServerAccess {
    public static MinecraftServer getServer() {
        return MinecraftClient.getInstance().getServer();
    }
}
