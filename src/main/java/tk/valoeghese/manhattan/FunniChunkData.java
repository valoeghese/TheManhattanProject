package tk.valoeghese.manhattan;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.WorldProperties;
import tk.valoeghese.manhattan.biome.GenBiome;
import tk.valoeghese.manhattan.biome.NoiseProperties;
import tk.valoeghese.manhattan.biome.SurfaceConfigProvider;
import tk.valoeghese.manhattan.utils.FunniMessageCompiler;

public final class FunniChunkData {
	public static void setProgram(String message) {
		System.out.println("Setting program");
		long time = System.currentTimeMillis();

		if (message != currentMessage) {
			currentMessage = message;

			synchronized (currentProgram) {
				currentProgram = FunniMessageCompiler.compile(message);
			}

			genBiomeData();
		}

		System.out.println("Set program in " + (System.currentTimeMillis() - time) + "ms");
	}

	private static void genBiomeData() {
		System.out.println("Generating Manhattan Project Biome Data");

		// write GenBiome stuff
		GenBiome.config = new SurfaceConfigProvider();

		for (int i = 0; i < 5; ++i) {
			int index = i * 4;

			switch (currentProgram[index++]) {
			case 0: // noise gen, done separately
				break;
			case 1: // surface
				byte subType = currentProgram[index++];
				GenBiome.config.add(subType, currentProgram[index++], currentProgram[index++]);
				break;
			case 2:
				break;
			}
		}
	}

	/**
	 * This sits on the (-, -) corner of the chunk.
	 */
	public static NoiseProperties getNoiseProperties(MinecraftServer server, int chunkX, int chunkZ) {
		long mapIndex = key(chunkX, chunkZ);
		NoiseProperties result = NOISE_DATA.get(mapIndex);

		if (result == null) {
			byte[] data = getOrCreateData(server, chunkX, chunkZ);

			float d = 0;
			float scale = 0;
			float t = 0;

			for (int i = 0; i < 5; ++i) {
				int index = i * 4;

				if (data[index++] == 0) {
					d += clampMap((float) currentProgram[index++], -128f, 128f, -0.5f, 1.4f);
					scale += clampMap((float) currentProgram[index++], -128f, 128f, -0.03f, 0.43f);
					t += clampMap((float) currentProgram[index++], -128f, 128f, 0.0f, 1.78f);
				}
			}

			if (d < -1.0f) {
				if (d < -1.8f) {
					d= -1.8f;
				}

				t /= 5;
			}

			if (scale < -0.01f) {
				scale = -0.01f;
			}

			result = new NoiseProperties(d, scale, t);
			NOISE_DATA.put(mapIndex, result);
		}

		return result;
	}

	@Nullable
	public static byte[] getData(MinecraftServer server, int chunkX, int chunkZ) {
		// System.out.println("Getting data");
		load(server);

		synchronized (DATA) {
			long index = key(chunkX, chunkZ);
			return DATA.get(index);
		}
	}

	public static byte[] getOrCreateData(MinecraftServer server, int chunkX, int chunkZ) {
		// System.out.println("Getting or creating data");
		byte[] result = getData(server, chunkX, chunkZ);

		if (result == null) {
			result = new byte[20];

			synchronized (currentProgram) {
				System.arraycopy(currentProgram, 0, result, 0, 20);
			}

			long index = key(chunkX, chunkZ);

			synchronized (DATA) {
				DATA.put(index, result);
			}
		}

		return result;
	}

	public static void load(MinecraftServer server) {
		try {
			WorldProperties wp = server.getSaveProperties().getMainWorldProperties();

			if (wp != properties) {
				File nextFile = new File(server.getSavePath(WorldSavePath.ROOT).toFile(), "manhattan_project.yes");

				if (currentFile != null && currentFile != nextFile) { // to stop it saving empty or a prior load time's stuff to the one it's trying to load's file when it should just load existing data
					saveFile(server);
				}

				properties = wp;
				currentFile = nextFile;

				if (!currentFile.exists()) {
					return; // nothing to load
				}

				System.out.println("Loading Manhattan Project World Data");

				synchronized (DATA) {
					try (DataInputStream src = new DataInputStream(new GZIPInputStream(new FileInputStream(currentFile)))) {
						for (int j = 0; j < 20; ++j) {
							currentProgram[j] = src.readByte();
						}

						int size = src.readInt();
						DATA = new Long2ObjectArrayMap<>();

						for (int i = 0; i < size; ++i) {
							long key = src.readLong();
							byte[] arr = new byte[20];

							for (int j = 0; j < 20; ++j) {
								arr[j] = src.readByte();
							}

							DATA.put(key, arr);
						}

						size = src.readInt();
						NOISE_DATA = new Long2ObjectArrayMap<>();

						for (int i = 0; i < size; ++i) {
							long key = src.readLong();
							float depth = src.readFloat();
							float scale = src.readFloat();
							float thicc = src.readFloat();
							NOISE_DATA.put(key, new NoiseProperties(depth, scale, thicc));
						}
					} catch (IOException e) {
						e.printStackTrace();
						throw new UncheckedIOException(e);
					}

					genBiomeData();
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
			throw t instanceof RuntimeException ? ((RuntimeException) t) : new RuntimeException(t);
		}
	}

	public static void saveFile(MinecraftServer server) {
		File file = currentFile == null ? new File(server.getSavePath(WorldSavePath.ROOT).toFile(), "manhattan_project.yes") : currentFile;
		currentFile = file;

		System.out.println("Saving Manhattan Project World Data");

		synchronized (DATA) {
			try (DataOutputStream dest = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
				for (int j = 0; j < 20; ++j) {
					dest.writeByte(currentProgram[j]);
				}

				dest.writeInt(DATA.size());

				for (Entry<byte[]> entry : DATA.long2ObjectEntrySet()) {
					dest.writeLong(entry.getLongKey());
					byte[] arr = entry.getValue();

					for (int j = 0; j < 20; ++j) {
						dest.writeByte(arr[j]);
					}
				}

				dest.writeInt(DATA.size());

				for (Entry<NoiseProperties> entry : NOISE_DATA.long2ObjectEntrySet()) {
					dest.writeLong(entry.getLongKey());

					NoiseProperties properties = entry.getValue();
					dest.writeFloat(properties.depth);
					dest.writeFloat(properties.scale);
					dest.writeFloat(properties.thicknessVariation);
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	private static float clampMap(float value, float min, float max, float newmin, float newmax) {
		value -= min;
		value /= (max - min);
		value = newmin + value * (newmax - newmin);

		if (value > newmax) {
			return newmax;
		} else if (value < newmin) {
			return newmin;
		} else {
			return value;
		}
	}

	public static long key(int x, int z) {
		// superchunk coords
		x = (x >> 4); // eclipse acts weird with bitshifts and thinks there's indentation, so () prevents it from indenting the rest of the program
		z = (z >> 4); // I would use >>= but then it's weird because I have to use {} instead
		return (((long) x & 0x7FFFFFFF) << 32L) | ((long) z & 0x7FFFFFFF);
	}

	private static Long2ObjectMap<byte[]> DATA = new Long2ObjectArrayMap<>();
	private static Long2ObjectMap<NoiseProperties> NOISE_DATA = new Long2ObjectArrayMap<>();
	private static WorldProperties properties;
	private static File currentFile;
	private static byte[] currentProgram = new byte[20];
	private static String currentMessage = null;

	static {
		// TODO start w/ random prog.
		currentProgram[2 * 4] = 2;
		currentProgram[3 * 4] = 2;
		currentProgram[4 * 4 + 2] = 1;
		currentProgram[4 * 4] = 2;
		currentProgram[4 * 4 + 1] = 1;
		genBiomeData();
	}
}
