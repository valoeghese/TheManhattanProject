package tk.valoeghese.manhattan.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class FunniMessageCompiler {
	public static byte[] compile(String message) {
		byte[] hash = SHA1.digest(message.getBytes());
		byte[] result = new byte[20];

		// cleanup
		for (int i = 0; i < 5; ++i) {
			int startIndex = i * 4;

			byte type = (byte) (hash[startIndex] % 3);
			result[startIndex] = type;

			switch (type) {
			case 0: // noisegen: 0, DEPTH, SCALE, THICKNESS_SCALE
			case 2: // surfacepopulator: 2, RAND_SEED_1_AND_2 (b1,b2)->seed0 <type>, (b2,b3)->seed1 <settings>
				System.arraycopy(hash, startIndex + 1, result, startIndex + 1, 3);
				break;
			case 1: // surfacemodifier: 1, SUBTYPE, RAND_SEED
				byte subtype = (byte) (hash[startIndex + 1] % 4); // 4 placements
				result[startIndex + 1] = subtype;
				System.arraycopy(hash, startIndex + 2, result, startIndex + 2, 2);
				break;
			}
		}

		return result;
	}

	private static final MessageDigest SHA1;
	public static final OpenSimplexNoise NOISE = new OpenSimplexNoise(new Random(0xCAFEBABE));

	static {
		try {
			SHA1 = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
