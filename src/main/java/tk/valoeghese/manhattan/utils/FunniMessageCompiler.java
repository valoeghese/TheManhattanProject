package tk.valoeghese.manhattan.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FunniMessageCompiler {
	public static byte[] compile(String message) {
		byte[] hash = SHA1.digest(message.getBytes());
		byte[] result = new byte[20];

		for (int i = 0; i < 5; ++i) {
			int startIndex = i * 4;

			byte type = (byte) (hash[startIndex] % 3);
			result[startIndex] = type;

			switch (type) {
			case 0: // noisegen
				
				break;
			case 1: // surfacemodifier
				break;
			case 2: // surfacepopulator
				break;
			}
		}
	}

	private static final MessageDigest SHA1;

	static {
		try {
			SHA1 = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
