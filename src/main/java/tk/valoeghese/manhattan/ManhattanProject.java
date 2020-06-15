package tk.valoeghese.manhattan;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.decorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.decorator.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.LeaveVineTreeDecorator;
import net.minecraft.world.gen.decorator.TrunkVineTreeDecorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.DarkOakFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.PineFoliagePlacer;
import net.minecraft.world.gen.foliage.SpruceFoliagePlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import tk.valoeghese.manhattan.biome.GenBiome;
import tk.valoeghese.zoesteriaconfig.api.ZoesteriaConfig;
import tk.valoeghese.zoesteriaconfig.api.container.Container;
import tk.valoeghese.zoesteriaconfig.api.container.WritableConfig;
import tk.valoeghese.zoesteriaconfig.api.deserialiser.Comment;
import tk.valoeghese.zoesteriaconfig.api.template.ConfigTemplate;

@SuppressWarnings("rawtypes")
public class ManhattanProject implements ModInitializer {
	@SuppressWarnings("unchecked")
	@Override
	public void onInitialize() {
		try {
			/*
			if (configFile.createNewFile()) {
				config = ZoesteriaConfig.createWritableConfig(new LinkedHashMap<>());
				config.addComment("== Manhattan Project Config ==");
				config.putStringValue("dimensionType", "minecraft:overworld");
				config.putBooleanValue("treatLikeNether", false);
				config.addComment("Whether to replace the generation of non-vanilla biomes.");
				config.putBooleanValue("overwriteModdedBiomes", false);

				EditableContainer advanced = ZoesteriaConfig.createWritableConfig(new LinkedHashMap<>());
				advanced.addComment("Whether The Manhattan Project will replace the chunk shape stage");
				advanced.putBooleanValue("shapeChunk", true);
				advanced.addComment("Whether The Manhattan Project will replace the 'build surface' stage");
				advanced.putBooleanValue("replaceSurfaceBlocks", true);
				advanced.addComment("Whether The Manhattan Project will replace the vegetal decoration (trees, grasses) stage");
				advanced.putBooleanValue("populateVegetation", true);

				config.putMap("advanced", advanced.asMap());
			} else {
				config = ZoesteriaConfig.loadConfig(configFile);
			}*/

			File configFile = new File(FabricLoader.getInstance().getConfigDirectory(), "manhattan_project.cfg");
			configFile.createNewFile();

			WritableConfig config = ZoesteriaConfig.loadConfigWithDefaults(configFile, ConfigTemplate.builder()
					.addComment(new Comment("== Manhattan Project Config =="))
					.addDataEntry("dimensionType", "minecraft:overworld")
					.addDataEntry("treatLikeNether", false)
					.addComment(new Comment(" Whether to replace the generation of non-vanilla biomes."))
					.addDataEntry("overwriteModdedBiomes", false)
					.addContainer("advanced", container -> container
							.addComment(new Comment(" Whether The Manhattan Project will replace the chunk shape stage"))
							.addDataEntry("shapeChunk", true)
							.addComment(new Comment(" Whether The Manhattan Project will replace the 'build surface' stage"))
							.addDataEntry("replaceSurfaceBlocks", true)
							.addComment(new Comment(" Whether The Manhattan Project will replace the vegetal decoration (trees, grasses) stage"))
							.addDataEntry("populateVegetation", true)
							)
					.build());

			config.writeToFile(configFile);

			dimensionType = new Identifier(config.getStringValue("dimensionType"));
			netherGen = config.getBooleanValue("treatLikeNether");
			overwriteModded = config.getBooleanValue("overwriteModdedBiomes");

			Container advanced = config.getContainer("advanced");
			shapeChunk = advanced.getBooleanValue("shapeChunk");
			replaceSurfaceBlocks = advanced.getBooleanValue("replaceSurfaceBlocks");
			populateVegetation = advanced.getBooleanValue("populateVegetation");
		} catch (IOException e) {
			throw new UncheckedIOException("Error loading config for The Manhattan Project", e);
		}

		// initialize biomes before hacky reflection!
		Biomes.DEFAULT.hashCode();

		try {
			for (Field field : Biomes.class.getFields()) {
				if (Biome.class.isAssignableFrom(field.getType())) {
					vanillaBiomes.add((Biome) field.get(null));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error collecting vanilla biomes", e);
		}

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
		TREE_TYPES.add(new Pair<>(Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.BIRCH_LOG.getDefaultState(), Blocks.BIRCH_LEAVES.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.JUNGLE_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState())); // palm
		TREE_TYPES.add(new Pair<>(Blocks.ACACIA_LOG.getDefaultState(), Blocks.ACACIA_LEAVES.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.DARK_OAK_LOG.getDefaultState(), Blocks.DARK_OAK_LEAVES.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.CRIMSON_STEM.getDefaultState(), Blocks.NETHER_WART_BLOCK.getDefaultState()));
		TREE_TYPES.add(new Pair<>(Blocks.WARPED_STEM.getDefaultState(), Blocks.WARPED_WART_BLOCK.getDefaultState()));

		FEATURE_CONFIG_FACTORIES.put(TreeFeatureConfig.class, rand -> {
			Pair<BlockState, BlockState> type = TREE_TYPES.get(rand.nextInt(TREE_TYPES.size()));
			TreeFeatureConfig.Builder builder = new TreeFeatureConfig.Builder(
					new SimpleBlockStateProvider(type.getLeft()),
					new SimpleBlockStateProvider(type.getRight()),
					createFoliagePlacer(rand),
					createTrunkPlacer(rand),
					new TwoLayersFeatureSize(1, 0, 1));

			if (rand.nextInt(3) != 0) {
				builder.ignoreVines();
			}

			if (type.getRight().getBlock() == Blocks.JUNGLE_LEAVES && rand.nextBoolean()) {
				builder.decorators(ImmutableList.of(new CocoaBeansTreeDecorator(0.2F), TrunkVineTreeDecorator.field_24965, LeaveVineTreeDecorator.field_24961));
			} else if (rand.nextInt(3) == 0) {
				builder.decorators(ImmutableList.of(rand.nextBoolean() ? BUZZ1 : BUZZ2));
			}

			return builder.build();
		});

		FEATURE_CONFIG_FACTORIES.put(HugeFungusFeatureConfig.class, rand -> {
			// yes I know it depends on the order stuff is added as to what block states are available
			BlockState validSurface = GRASS_BLOCK;

			if (!GenBiome.config.validSurfaceStates.isEmpty()) {
				validSurface = GenBiome.config.validSurfaceStates.get(GenBiome.config.validSurfaceStates.size());
			}

			Pair<BlockState, BlockState> type = TREE_TYPES.get(rand.nextInt(TREE_TYPES.size()));
			BlockState hat = type.getRight();

			if (hat.getBlock() instanceof LeavesBlock) {
				hat = hat.with(LeavesBlock.PERSISTENT, true);
			}

			boolean notShroom = hat.getBlock() != Blocks.WARPED_WART_BLOCK && hat.getBlock() != Blocks.NETHER_WART_BLOCK;

			return new HugeFungusFeatureConfig(validSurface, type.getLeft(), hat, notShroom ? hat : Blocks.SHROOMLIGHT.getDefaultState(), false);
		});

		TREES.add(new Pair(TreeFeatureConfig.class, (BiFunction<FeatureConfig, Random, ConfiguredFeature>)
				(fc, rand) -> Feature.TREE.configure((TreeFeatureConfig) fc).createDecoratedFeature(createCountExtraHeightmap(rand))));
		TREES.add(new Pair(HugeFungusFeatureConfig.class, (BiFunction<FeatureConfig, Random, ConfiguredFeature>)
				(fc, rand) -> Feature.HUGE_FUNGUS.configure((HugeFungusFeatureConfig) fc).createDecoratedFeature(createCountHeightmap(rand, 1))));

		GRASSES.add(Blocks.GRASS.getDefaultState());
		GRASSES.add(Blocks.GRASS.getDefaultState());
		GRASSES.add(Blocks.FERN.getDefaultState());
		GRASSES.add(Blocks.WARPED_FUNGUS.getDefaultState());
		GRASSES.add(Blocks.CRIMSON_FUNGUS.getDefaultState());
		GRASSES.add(Blocks.BROWN_MUSHROOM.getDefaultState());
		GRASSES.add(Blocks.RED_MUSHROOM.getDefaultState());
	}

	public static void addFeatures(List<ConfiguredFeature<?,?>> features, Random featureRandom, Random settingRandom, int count) {
		while (count --> 0) {
			if (count == 1 && featureRandom.nextInt(3) > 0) { // tree
				int i = featureRandom.nextInt(4) == 0 ? 0 : 1;
				Pair<Class, BiFunction<FeatureConfig, Random, ConfiguredFeature>> chosen = TREES.get(i);
				features.add(chosen.getRight().apply(FEATURE_CONFIG_FACTORIES.get(chosen.getLeft()).apply(settingRandom), settingRandom));
			} else {
				switch (featureRandom.nextInt(5)) {
				case 0: // grass
					features.add(Feature.RANDOM_PATCH.configure(
							new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(GRASSES.get(settingRandom.nextInt(GRASSES.size()))), SimpleBlockPlacer.field_24871).build()
							).createDecoratedFeature(
									Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(settingRandom.nextInt(19) + 4))
									));
					break;
				case 1: // flower preset or grass preset
					RandomPatchFeatureConfig r = null;

					int i = settingRandom.nextInt(12);

					switch (i & 0b11) {
					case 0:
						r = i == 0 ? DefaultBiomeFeatures.FOREST_FLOWER_CONFIG : DefaultBiomeFeatures.BLUE_ORCHID_CONFIG;
						break;
					case 1:
						r = i == 1 ? DefaultBiomeFeatures.ROSE_BUSH_CONFIG : DefaultBiomeFeatures.DEFAULT_FLOWER_CONFIG;
						break;
					case 2:
						r = i == 2 ? DefaultBiomeFeatures.DEAD_BUSH_CONFIG : DefaultBiomeFeatures.PLAINS_FLOWER_CONFIG;
						break;
					case 3:
						r = i == 3 ? DefaultBiomeFeatures.SUNFLOWER_CONFIG : DefaultBiomeFeatures.TALL_GRASS_CONFIG;
						break;
					}

					features.add(Feature.RANDOM_PATCH.configure(r).createDecoratedFeature(
							Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(settingRandom.nextInt(19) + 4))
							));
					break;
				case 2: // cactus
					features.add(Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.CACTUS_CONFIG).createDecoratedFeature(
							Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(settingRandom.nextInt(12) + 1))
							));
					break;
				case 3: // patch (of some description)
					features.add(Feature.RANDOM_PATCH.configure(
							settingRandom.nextInt(3) == 0 ? DefaultBiomeFeatures.MELON_PATCH_CONFIG : DefaultBiomeFeatures.PUMPKIN_PATCH_CONFIG
							).createDecoratedFeature(
									Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(settingRandom.nextInt(10) + 1))
									));
					break;
				case 4: // lilypad
					features.add(Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.LILY_PAD_CONFIG).createDecoratedFeature(
							Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(settingRandom.nextInt(10) + 1))
							));
					break;
				}
			}
		}
	}

	private static final BeehiveTreeDecorator BUZZ1 = new BeehiveTreeDecorator(0.002f);
	private static final BeehiveTreeDecorator BUZZ2 = new BeehiveTreeDecorator(0.035f);

	private static TrunkPlacer createTrunkPlacer(Random rand) {
		int height = rand.nextInt(11) + 2;
		int randHeight = rand.nextInt(4);

		if (rand.nextInt(3) == 0) {
			return new ForkingTrunkPlacer(height, randHeight, randHeight == 0 ? 0 : randHeight - 1);
		} else {
			return new StraightTrunkPlacer(height, randHeight, randHeight == 0 ? 0 : randHeight - 1);
		}
	}

	private static FoliagePlacer createFoliagePlacer(Random rand) {
		int type = rand.nextInt(5);

		if (type == 4) {
			// it ignores the values from what I can see
			// I do it separately since I don't need to calculate other values otherwise;
			return new DarkOakFoliagePlacer(0, 0, 0, 0);
		}

		int radius;
		int randomRadius;
		int offset;
		int randomOffset;

		radius = rand.nextInt(3) + 1;
		randomRadius = rand.nextInt(4) - 1;

		if (randomRadius < 0) {
			randomRadius = 0;
		}

		offset = rand.nextInt(4) - 2;

		if (offset < 0) {
			offset = 0;
		}

		randomOffset = rand.nextInt(3) - 1;

		if (randomOffset < 0) {
			randomOffset = 0;
		}

		switch (type) {
		case 0:
			int height = rand.nextInt(5);

			return new BlobFoliagePlacer(radius, randomRadius, offset, randomOffset, height);
		case 1:
			return new AcaciaFoliagePlacer(radius, randomRadius, offset, randomOffset);
		case 2:
			// subtracts from the leaves extension
			int trunkHeight = rand.nextInt(4);
			int trunkHeightRandom = rand.nextInt(3);

			return new SpruceFoliagePlacer(radius, randomRadius, offset, randomOffset, trunkHeight, trunkHeightRandom);
		case 3:
			int foliageHeight = rand.nextInt(4) + 1;
			int foliageHeightRandom = rand.nextInt(4) - 1;

			if (foliageHeightRandom < 0) {
				foliageHeightRandom = 0;
			}

			return new PineFoliagePlacer(radius, randomRadius, offset, randomOffset, foliageHeight, foliageHeightRandom);
		default: // 4 is already handled earlier
			return null;
		}
	}

	private static ConfiguredDecorator<?> createCountExtraHeightmap(Random rand) {
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

	private static ConfiguredDecorator<?> createCountHeightmap(Random rand, int multiplier) {
		int count = rand.nextInt(4 * multiplier) + rand.nextInt(3 * multiplier) - rand.nextInt(3 * multiplier);

		if (count < 0) {
			count = 0;
		}

		return Decorator.COUNT_HEIGHTMAP.configure(new CountDecoratorConfig(count));
	}

	@SuppressWarnings("unchecked")
	public static Identifier getDimensionId(DimensionType dim) {
		
		return ((Registry<DimensionType>) Registry.REGISTRIES
				.get(Registry.DIMENSION_TYPE_KEY.getValue()))
		.getId(dim);
	}

	public static Identifier dimensionType;
	public static boolean netherGen;
	public static boolean overwriteModded;
	public static boolean shapeChunk;
	public static boolean replaceSurfaceBlocks;
	public static boolean populateVegetation;

	public static Set<Biome> vanillaBiomes = new HashSet<>();

	private static final BlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.getDefaultState();

	private static final Map<Class, Function<Random, FeatureConfig>> FEATURE_CONFIG_FACTORIES = new HashMap<>();
	private static final List<Pair<Class, BiFunction<FeatureConfig, Random, ConfiguredFeature>>> TREES = new ArrayList<>();
	//	private static final List<Pair<Class, BiFunction<FeatureConfig, Random, ConfiguredFeature>>> OTHER_VEGETATION = new ArrayList<>();
	private static final List<BlockState> GRASSES = new ArrayList<>();

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
