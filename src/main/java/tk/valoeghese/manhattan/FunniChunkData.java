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
import net.minecraft.world.level.LevelProperties;

public final class FunniChunkData {
	private static Long2ObjectMap<byte[]> DATA;
	private static LevelProperties properties;
	private static File currentFile;
	private static byte[] currentData = new byte[20];

	public static void update(MinecraftServer server) {
		WorldProperties wp = server.getSaveProperties().getMainWorldProperties();

		if (wp != properties) {
			saveFile(server);

			currentFile = new File(server.getSavePath(WorldSavePath.ROOT).toFile(), "manhattan_project.yes");

			synchronized (DATA) {
				try (DataInputStream src = new DataInputStream(new GZIPInputStream(new FileInputStream(currentFile)))) {
					for (int j = 0; j < 20; ++j) {
						currentData[j] = src.readByte();
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
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		}
	}

	@Nullable
	public static byte[] getData(MinecraftServer server, int chunkX, int chunkZ) {
		update(server);

		synchronized (DATA) {
			long index = key(chunkX, chunkZ);
			return DATA.get(index);
		}
	}

	public static byte[] getOrCreateData(MinecraftServer server, int chunkX, int chunkZ) {
		byte[] result = getData(server, chunkX, chunkZ);

		if (result == null) {
			result = new byte[20];
			System.arraycopy(currentData, 0, result, 0, 20);
			long index = key(chunkX, chunkZ);

			synchronized (DATA) {
				DATA.put(index, result);
			}
		}

		return result;
	}

	public static void saveFile(MinecraftServer server) {
		WorldProperties wp = server.getSaveProperties().getMainWorldProperties();

		File file = currentFile == null ? new File(server.getSavePath(WorldSavePath.ROOT).toFile(), "manhattan_project.yes") : currentFile;

		synchronized (DATA) {
			try (DataOutputStream dest = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
				for (int j = 0; j < 20; ++j) {
					dest.writeByte(currentData[j]);
				}

				dest.writeInt(DATA.size());

				for (Entry<byte[]> entry : DATA.long2ObjectEntrySet()) {
					dest.writeLong(entry.getLongKey());
					byte[] arr = entry.getValue();

					for (int j = 0; j < 20; ++j) {
						dest.writeByte(arr[j]);
					}
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	public static long key(int x, int z) {
		// superchunk coords
		{x >>= 4;} // eclipse acts weird with bitshifts and thinks there's indentation, so {} prevents it from affecting the rest of the program
		{z >>= 4;}
		return (((long) x & 0x7FFFFFFF) << 32L) | ((long) z & 0x7FFFFFFF);
	}
}
