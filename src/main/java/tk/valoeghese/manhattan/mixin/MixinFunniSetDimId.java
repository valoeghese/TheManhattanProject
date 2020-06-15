package tk.valoeghese.manhattan.mixin;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import tk.valoeghese.manhattan.utils.DimIDManager;

@Mixin(ServerWorld.class)
public abstract class MixinFunniSetDimId extends World {
	protected MixinFunniSetDimId(MutableWorldProperties mutableWorldProperties, RegistryKey<World> registryKey,
			RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, Supplier<Profiler> profiler,
			boolean bl, boolean bl2, long l) {
		super(mutableWorldProperties, registryKey, registryKey2, dimensionType, profiler, bl, bl2, l);
	}

	@Inject(at = @At("RETURN"), method = "<init>")
	private void onCreate(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, WorldGenerationProgressListener generationProgressListener, ChunkGenerator chunkGenerator, boolean bl, long l, List<Spawner> list, boolean bl2, CallbackInfo info) {
		if (chunkGenerator instanceof SurfaceChunkGenerator) {
			((DimIDManager) chunkGenerator).manhattan_setDimID(this.getDimensionRegistryKey().getValue());
		}
	}
}
