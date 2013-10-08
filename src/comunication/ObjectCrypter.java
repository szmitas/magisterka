package comunication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ObjectCrypter {

	private static Cipher deCipher;
	private static Cipher enCipher;
	private static SecretKeySpec key;
	private static IvParameterSpec ivSpec;

	public ObjectCrypter() {

	}

	public static void init() {
		// wrap key data in Key/IV specs to pass to cipher

		byte[] keyBytes = new BigInteger("56368293412578977").toByteArray();
		ivSpec = new IvParameterSpec(new byte[] { 22, 33, 44, 55, 66, 77, 88,
				99 });
		// create the cipher with the algorithm you choose
		// see javadoc for Cipher class for more info, e.g.
		try {
			DESKeySpec dkey = new DESKeySpec(keyBytes);
			key = new SecretKeySpec(dkey.getKey(), "DES");
			deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String encrypt(Object obj) {

		byte[] input = null;
		try {
			input = convertToByteArray(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			enCipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			return Arrays.toString(enCipher.doFinal(input));
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

		// cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		// byte[] encypted = new byte[cipher.getOutputSize(input.length)];
		// int enc_len = cipher.update(input, 0, input.length, encypted, 0);
		// enc_len += cipher.doFinal(encypted, enc_len);
		// return encypted;

	}

	public static String decrypt(String encrypted) {

		String[] byteValues = encrypted.substring(1, encrypted.length() - 1)
				.split(",");
		byte[] bytes = new byte[byteValues.length];

		for (int i = 0, len = bytes.length; i < len; i++) {
			bytes[i] = Byte.valueOf(byteValues[i].trim());
		}

		try {
			deCipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			return convertFromByteArray(deCipher.doFinal(bytes)).toString();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static Object convertFromByteArray(byte[] byteObject)
			throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais;

		ObjectInputStream in;
		bais = new ByteArrayInputStream(byteObject);
		in = new ObjectInputStream(bais);
		Object o = in.readObject();
		in.close();
		return o;

	}

	private static byte[] convertToByteArray(Object complexObject)
			throws IOException {
		ByteArrayOutputStream baos;

		ObjectOutputStream out;

		baos = new ByteArrayOutputStream();

		out = new ObjectOutputStream(baos);

		out.writeObject(complexObject);

		out.close();

		return baos.toByteArray();

	}

}