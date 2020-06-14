package tk.valoeghese.manhattan.biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import tk.valoeghese.manhattan.ManhattanProject;
import tk.valoeghese.manhattan.utils.FunniMessageCompiler;

public class SurfaceConfigProvider {
	public SurfaceConfigProvider() {
	}

	private ByteList predicateTypes = new ByteArrayList();
	private List<Surface> surfaces = new ArrayList<>();
	private AtomicInteger surfaceCategory = new AtomicInteger(-1);

	public void setNether(boolean nether) {
		for (Surface surface : this.surfaces) {
			surface.setNether(nether);
		}
	}

	/**
	 * Add a surface thing from the program.
	 * @param type byte 1: the subtype
	 * @param b2 byte 2
	 * @param b3 byte 3
	 */
	public void add(byte type, byte b2, byte b3) {
		this.predicateTypes.add(type);
		this.surfaces.add(Surface.create(b2, b3, this.surfaceCategory));
	}

	public TernarySurfaceConfig getSurface(Random rand, /*int ox, int oz,*/ int x, int z, boolean nether) {
		/*int ocx = (ox >> 4);
		int ocz = (oz >> 4);
		int cx = (x >> 4);
		int cz = (z >> 4);

		if (ocx != cx || ocz != cz) {
			return FunniChunkData.getSurfaceProvider(GenBiome.server, this, ocx, ocz).getSurface(rand, ox, oz, ox, oz, nether);
		}*/

		if (this.predicateTypes.isEmpty()) {
			return nether ? SurfaceBuilder.NETHER_CONFIG : SurfaceBuilder.GRASS_CONFIG;
		} else {
			int length = this.predicateTypes.size();

			for (int i = 0; i < length; ++i) {
				if (i == length - 1) {
					return this.surfaces.get(i);
				}

				boolean isResult = false;

				switch (this.predicateTypes.getByte(i)) {
				case 0: // noise small
					isResult = FunniMessageCompiler.NOISE.sample((double) x / 22.0, (double) z / 22.0) > 0.28;
					break;
				case 1: // noise medium
					isResult = FunniMessageCompiler.NOISE.sample((double) x / 36.0, (double) z / 36.0) > 0.28;
					break;
				case 2: // noise large
					isResult = FunniMessageCompiler.NOISE.sample((double) x / 52.0, (double) z / 52.0) > 0.28;
					break;
				case 3: // rand 3
					isResult = rand.nextInt(3) == 0;
					break;
				}

				if (isResult) {
					return this.surfaces.get(i);
				}
			}

			// dead code, should be unreachable
			throw new RuntimeException("Invalid State! Perhaps the surface list was modified while trying to choose a surface?");
		}
	}

	/**
	 * Really cursed class for a surface config that changes in nether vs overworld.
	 */
	public static class Surface extends TernarySurfaceConfig {
		private Surface(BlockState top, BlockState under, BlockState nt, BlockState nu) {
			super(top, under, under);

			this.top = top;
			this.under = under;
			this.netherTop = nt;
			this.netherUnder = nu;
		}

		private static Integer cache = null;
		private static Surface last;

		BlockState top;
		BlockState under;
		final BlockState netherTop;
		final BlockState netherUnder;
		boolean cachedNether;

		public static Surface create(byte upper, byte lower, AtomicInteger category) {
			int combined = ((upper & 0xff) << 8) | ((lower & 0xff));

			if (last == null || combined != cache.intValue()) {
				cache = combined;
				RAND.setSeed(combined);
				return create(category);
			}

			return last;
		}

		private static Surface create(AtomicInteger category) {
			BlockState netherTop = ManhattanProject.TOP_SURFACE_BLOCKS_NETHER.get(RAND.nextInt(ManhattanProject.TOP_SURFACE_BLOCKS_NETHER.size()));;
			BlockState netherUnder = ManhattanProject.UNDER_SURFACE_BLOCKS_NETHER.get(RAND.nextInt(ManhattanProject.UNDER_SURFACE_BLOCKS_NETHER.size()));;
			BlockState top = Blocks.AIR.getDefaultState();
			BlockState under = Blocks.AIR.getDefaultState();

			if (category.get() == -1) {
				category.set(RAND.nextInt(3));
			}

			switch (category.get()) {
			case 0: // normal
				top = ManhattanProject.TOP_SURFACE_BLOCKS.get(RAND.nextInt(ManhattanProject.TOP_SURFACE_BLOCKS.size()));
				under = ManhattanProject.UNDER_SURFACE_BLOCKS.get(RAND.nextInt(ManhattanProject.UNDER_SURFACE_BLOCKS.size()));
				break;
			case 1: // sandy
				top = ManhattanProject.TOP_SURFACE_BLOCKS_SANDY.get(RAND.nextInt(ManhattanProject.TOP_SURFACE_BLOCKS_SANDY.size()));
				under = ManhattanProject.UNDER_SURFACE_BLOCKS_SANDY.get(RAND.nextInt(ManhattanProject.UNDER_SURFACE_BLOCKS_SANDY.size()));
				break;
			case 2: // cold
				top = ManhattanProject.TOP_SURFACE_BLOCKS_COLD.get(RAND.nextInt(ManhattanProject.TOP_SURFACE_BLOCKS_COLD.size()));
				under = ManhattanProject.UNDER_SURFACE_BLOCKS_COLD.get(RAND.nextInt(ManhattanProject.UNDER_SURFACE_BLOCKS_COLD.size()));
				break;
			}

			Surface next = new Surface(top, under, netherTop, netherUnder);
			return last = next;
		}

		public void setNether(boolean nether) {
			if (nether ^ this.cachedNether) { // The cool kidz use this instead of !=
				if (nether) {
					this.top = this.netherTop;
					this.under = this.netherUnder;
				} else {
					this.top = this.getTopMaterial();
					this.under = this.getUnderMaterial();
				}
			}
		}

		@Override
		public BlockState getTopMaterial() {
			return this.top;
		}

		@Override
		public BlockState getUnderMaterial() {
			return this.under;
		}

		@Override
		public BlockState getUnderwaterMaterial() {
			return this.under;
		}

		private static final Random RAND = new Random();
	}
}
