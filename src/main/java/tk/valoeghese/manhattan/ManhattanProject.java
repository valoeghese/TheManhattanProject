package tk.valoeghese.manhattan;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class ManhattanProject implements ModInitializer {
	@Override
	public void onInitialize() {
		// tags don't exist until world load
		// hardcode go brrrrrrrrrrr
		TOP_SURFACE_BLOCKS.add(Blocks.GRASS.getDefaultState());
		TOP_SURFACE_BLOCKS.add(Blocks.GRASS.getDefaultState());

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
		TOP_SURFACE_BLOCKS_SANDY.add(Blocks.SAND.getDefaultState());

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
	}

	public static final List<BlockState> TOP_SURFACE_BLOCKS = new ArrayList<>();
	public static final List<BlockState> UNDER_SURFACE_BLOCKS = new ArrayList<>();

	public static final List<BlockState> TOP_SURFACE_BLOCKS_NETHER = new ArrayList<>();
	public static final List<BlockState> UNDER_SURFACE_BLOCKS_NETHER = new ArrayList<>();

	public static final List<BlockState> TOP_SURFACE_BLOCKS_COLD = new ArrayList<>();
	public static final List<BlockState> UNDER_SURFACE_BLOCKS_COLD = new ArrayList<>();

	public static final List<BlockState> TOP_SURFACE_BLOCKS_SANDY = new ArrayList<>();
	public static final List<BlockState> UNDER_SURFACE_BLOCKS_SANDY = new ArrayList<>();
}
