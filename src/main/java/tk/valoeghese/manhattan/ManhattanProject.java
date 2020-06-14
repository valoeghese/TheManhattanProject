package tk.valoeghese.manhattan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Pair;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

@SuppressWarnings("rawtypes")
public class ManhattanProject implements ModInitializer {

	@SuppressWarnings("unchecked")
	@Override
	public void onInitialize() {
		// tags don't exist until world load
		// hardcode go brrrrrrrrrrr
		TOP_SURFACE_BLOCKS.add(Blocks.GRASS_BLOCK.getDefaultState());
		TOP_SURFACE_BLOCKS.add(Blocks.PODZOL.getDefaultState());
		TOP_SURFACE_BLOCKS.add(Blocks.COARSE_DIRT.getDefaultState());
		TOP_SURFACE_BLOCKS.add(Blocks.STONE.getDefaultState());
		TOP_SURFACE_BLOCKS.add(Blocks.BASALT.getDefaultState());

		TOP_SURFACE_BLOCKS.add(Blocks.GRAVEL.getDefaultState());

		// -----------------------

		TOP_SURFACE_BLOCKS_COLD.add(Blocks.PACKED_ICE.getDefaultState());
		TOP_SURFACE_BLOCKS_COLD.add(Blocks.SNOW_BLOCK.getDefaultState());

		// ----------------------

		TOP_SURFACE_BLOCKS_SANDY.add(Blocks.RED_SAND.getDefaultState());
		TOP_SURFACE_BLOCKS_SANDY.add(Blocks.RED_SAND.getDefaultState());
		TOP_SURFACE_BLOCKS_SANDY.add(Blocks.SAND.getDefaultState());
		TOP_SURFACE_BLOCKS_SANDY.add(Blocks.SAND.getDefaultState());

		TOP_SURFACE_BLOCKS_SANDY.add(Blocks.GRASS_BLOCK.getDefaultState());
		TOP_SURFACE_BLOCKS_SANDY.add(Blocks.GRAVEL.getDefaultState());

		// ----------------------

		TOP_SURFACE_BLOCKS_NETHER.add(Blocks.NETHERRACK.getDefaultState());
		TOP_SURFACE_BLOCKS_NETHER.add(Blocks.CRIMSON_NYLIUM.getDefaultState());
		TOP_SURFACE_BLOCKS_NETHER.add(Blocks.WARPED_NYLIUM.getDefaultState());
		TOP_SURFACE_BLOCKS_NETHER.add(Blocks.SOUL_SAND.getDefaultState());
		TOP_SURFACE_BLOCKS_NETHER.add(Blocks.SOUL_SOIL.getDefaultState());
		TOP_SURFACE_BLOCKS_NETHER.add(Blocks.MAGMA_BLOCK.getDefaultState());
		TOP_SURFACE_BLOCKS_NETHER.add(Blocks.GRAVEL.getDefaultState());

		//------------------------

		UNDER_SURFACE_BLOCKS.add(Blocks.DIRT.getDefaultState());
		UNDER_SURFACE_BLOCKS.add(Blocks.BASALT.getDefaultState());
		UNDER_SURFACE_BLOCKS.add(Blocks.GRAVEL.getDefaultState());
		UNDER_SURFACE_BLOCKS.add(Blocks.STONE.getDefaultState());

		UNDER_SURFACE_BLOCKS.add(Blocks.COARSE_DIRT.getDefaultState());

		// ---------------------

		UNDER_SURFACE_BLOCKS_COLD.add(Blocks.SNOW_BLOCK.getDefaultState());
		UNDER_SURFACE_BLOCKS_COLD.add(Blocks.BLUE_ICE.getDefaultState());
		UNDER_SURFACE_BLOCKS_COLD.add(Blocks.PACKED_ICE.getDefaultState());
		UNDER_SURFACE_BLOCKS_COLD.add(Blocks.STONE.getDefaultState());

		//-----------------------

		UNDER_SURFACE_BLOCKS_SANDY.add(Blocks.RED_SAND.getDefaultState());
		UNDER_SURFACE_BLOCKS_SANDY.add(Blocks.SAND.getDefaultState());
		UNDER_SURFACE_BLOCKS_SANDY.add(Blocks.GRAVEL.getDefaultState());

		// -----------------------

		UNDER_SURFACE_BLOCKS_NETHER.add(Blocks.SOUL_SAND.getDefaultState());
		UNDER_SURFACE_BLOCKS_NETHER.add(Blocks.NETHERRACK.getDefaultState());
		UNDER_SURFACE_BLOCKS_NETHER.add(Blocks.SOUL_SOIL.getDefaultState());
		UNDER_SURFACE_BLOCKS_NETHER.add(Blocks.MAGMA_BLOCK.getDefaultState());

		// And next up, vegetation!
		TREES.add(new Pair(TreeFeatureConfig.class, (BiFunction<FeatureConfig, Random, ConfiguredFeature>)
				(fc, rand) -> Feature.TREE.configure((TreeFeatureConfig) fc).createDecoratedFeature(createCountExtraHeightmap(rand))));
		TREES.add(new Pair(HugeFungusFeatureConfig.class, (BiFunction<FeatureConfig, Random, ConfiguredFeature>)
				(fc, rand) -> Feature.HUGE_FUNGUS.configure((HugeFungusFeatureConfig) fc).createDecoratedFeature(createCountHeightmap(rand, 1))));

		TREE_TYPES.add(new Pair<>(Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.BIRCH_LOG.getDefaultState(), Blocks.BIRCH_LEAVES.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.JUNGLE_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState())); // palm
		TREE_TYPES.add(new Pair<>(Blocks.ACACIA_LOG.getDefaultState(), Blocks.ACACIA_LEAVES.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.DARK_OAK_LOG.getDefaultState(), Blocks.DARK_OAK_LEAVES.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.CRIMSON_STEM.getDefaultState(), Blocks.CRIMSON_NYLIUM.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.WARPED_STEM.getDefaultState(), Blocks.WARPED_NYLIUM.getDefaultState()));

		FEATURE_CONFIG_FACTORIES.put(TreeFeatureConfig.class, rand -> {
			Pair<BlockState, BlockState> type = TREE_TYPES.get(rand.nextInt(TREE_TYPES.size()));
			new TreeFeatureConfig.Builder(
					new SimpleBlockStateProvider(type.getLeft()),
					new SimpleBlockStateProvider(type.getRight()), , trunkPlacer, minimumSize);
		});
	}

	private FoliagePlacer createFoliagePlacer(Random rand) {
		switch (rand.nextInt(3)) {
		case 0:
			break;//return new BlobFoliagePlacer(i, j, k, l, m)
		case 1:
			break;
		case 2:
		default:
			break;
		}
		return null;
	}

	private ConfiguredDecorator<?> createCountExtraHeightmap(Random rand) {
		int count = rand.nextInt(5) + rand.nextInt(4) - rand.nextInt(4);

		if (count < 0) {
			count = 0;
		}

		int extraCount = 1;

		if (rand.nextInt(3) == 0) {
			extraCount += 2;

			if (rand.nextInt(3) == 0) {
				extraCount += 2;
			}
		}

		return Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(count, 0.1f, extraCount));
	}

	private ConfiguredDecorator<?> createCountHeightmap(Random rand, int multiplier) {
		int count = rand.nextInt(4 * multiplier) + rand.nextInt(3 * multiplier) - rand.nextInt(3 * multiplier);

		if (count < 0) {
			count = 0;
		}

		return Decorator.COUNT_HEIGHTMAP.configure(new CountDecoratorConfig(count));
	}

	public static final Map<Class, Function<Random, FeatureConfig>> FEATURE_CONFIG_FACTORIES = new HashMap<>();
	public static final List<Pair<Class, BiFunction<FeatureConfig, Random, ConfiguredFeature>>> TREES = new ArrayList<>();
	public static final List<Pair<Class, BiFunction<FeatureConfig, Random, ConfiguredFeature>>> OTHER_VEGETATION = new ArrayList<>();

	public static final List<BlockState> TOP_SURFACE_BLOCKS = new ArrayList<>();
	public static final List<BlockState> UNDER_SURFACE_BLOCKS = new ArrayList<>();

	public static final List<BlockState> TOP_SURFACE_BLOCKS_NETHER = new ArrayList<>();
	public static final List<BlockState> UNDER_SURFACE_BLOCKS_NETHER = new ArrayList<>();

	public static final List<BlockState> TOP_SURFACE_BLOCKS_COLD = new ArrayList<>();
	public static final List<BlockState> UNDER_SURFACE_BLOCKS_COLD = new ArrayList<>();

	public static final List<BlockState> TOP_SURFACE_BLOCKS_SANDY = new ArrayList<>();
	public static final List<BlockState> UNDER_SURFACE_BLOCKS_SANDY = new ArrayList<>();

	private static List<Pair<BlockState, BlockState>> TREE_TYPES = new ArrayList<>();
}
