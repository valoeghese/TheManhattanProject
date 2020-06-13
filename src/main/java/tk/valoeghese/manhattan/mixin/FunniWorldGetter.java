package tk.valoeghese.manhattan.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ChunkRegion;

@Mixin(ChunkRegion.class)
public interface FunniWorldGetter {
	@Accessor("world")
	ServerWorld getWorld();
}
