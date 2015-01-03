package com.life.util;

import java.lang.reflect.Array;

import android.R.bool;
import android.R.string;

public class RSAhelper {

	final static int biRadix = 65536;
	final int biRadixBase = 2;
	final int biRadixBits = 16;
	final static int bitsPerDigit = 16;
	final int biHalfRadix = 32768;
	final static int maxDigitVal = 65535;

	final static int key_chunkSize = 126;
	final int key_radix = 16;
	static BigInt key_e = new BigInt();
	final static int this_k = 64;
	static BigInt this_bkplus1 = new BigInt();
	static BigInt this_modulus = new BigInt();
	static BigInt this_mu = new BigInt();

	// static const int biRadixSquared = 16;

	public static String helper(String password) {
		init();
		String temp = encryptedString(password).toLowerCase();
		return temp;

	}

	static void init() {
		key_e.digits[0] = 1;
		key_e.digits[1] = 1;
		int[] temp1 = { 13381, 11781, 7242, 62287, 38208, 17148, 36682, 28526,
				54813, 37809, 12443, 12925, 27820, 1386, 48192, 27208, 27855,
				59049, 15631, 22431, 16025, 50103, 14892, 62276, 15625, 2557,
				36289, 11124, 13811, 61295, 13727, 46513, 31398, 35195, 60201,
				63116, 46142, 15347, 18170, 44287, 40478, 56849, 61724, 30076,
				33833, 20967, 42674, 19595, 37418, 23451, 46539, 6410, 33740,
				39386, 9150, 17236, 19320, 30791, 25100, 51856, 54224, 7506,
				11138, 43168 };
		for (int i = 0; i < 64; i++) {
			this_modulus.digits[i] = temp1[i];
		}
		int[] temp2 = { 64960, 5794, 58342, 4906, 21255, 5449, 37131, 1520,
				25147, 48127, 15949, 8219, 32169, 50821, 10190, 27537, 14250,
				44660, 20599, 33058, 37701, 20652, 45979, 39567, 20286, 30758,
				17065, 20293, 58918, 47896, 54663, 31950, 53677, 57426, 6549,
				19452, 14672, 16668, 37652, 22641, 44716, 5187, 8214, 50719,
				46975, 33250, 60475, 56069, 45892, 11879, 33516, 19745, 41990,
				3787, 2709, 62927, 59461, 1988, 64302, 21380, 25436, 62171,
				55507, 33957, 1 };
		for (int i = 0; i < 65; i++) {
			this_mu.digits[i] = temp2[i];
		}

	}

	static String reverseStr(String original) {
		char[] value = original.toCharArray();
		for (int i = (value.length - 1) >> 1; i >= 0; i--) {
			char temp = value[i];
			value[i] = value[value.length - 1 - i];
			value[value.length - 1 - i] = temp;
		}
		return new String(value);
	}

	static String digitToHex(int x) {
		// String temp = String.valueOf(x);
		String temp = "";
		int res;
		for (; x > 0;) {

			res = x % 16;
			x = x / 16;
			switch (res) {
			case 15:
				temp += "f";
				break;
			case 14:
				temp += "e";
				break;
			case 13:
				temp += "d";
				break;
			case 12:
				temp += "c";
				break;
			case 11:
				temp += "b";
				break;
			case 10:
				temp += "a";
				break;
			default:
				temp += String.valueOf(res);
				break;

			}

		}

		temp = reverseStr(temp);

		if (temp.length() == 3) {
			temp = "0" + temp;
		}
		if (temp.length() == 2) {
			temp = "00" + temp;
		}
		if (temp.length() == 1) {
			temp = "000" + temp;
		}
		if (temp.length() == 1) {
			temp = "0000";
		}
		return temp;
	}

	static String biToHex(BigInt x) {

		String result = "";
		int n = biHighIndex(x);
		for (int i = biHighIndex(x); i > -1; --i) {
			result += digitToHex(x.digits[i]);
		}
		return result;
	}

	static int biHighIndex(BigInt x) {

		int result = x.digits.length - 1;
		while (result > 0 && x.digits[result] == 0)
			--result;
		return result;

	}

	static BigInt biAdd(BigInt x, BigInt y) {

		BigInt result;

		if (x.isNeg != y.isNeg) {
			y.isNeg = !y.isNeg;
			result = biSubtract(x, y);
			y.isNeg = !y.isNeg;
		} else {
			result = new BigInt();
			int c = 0;
			int n;
			for (int i = 0; i < x.digits.length; ++i) {
				n = x.digits[i] + y.digits[i] + c;
				result.digits[i] = n % biRadix;
				if (n >= biRadix) {
					c = 1;
				} else {
					c = 0;
				}
			}
			result.isNeg = x.isNeg;
		}
		return result;

	}

	static BigInt biSubtract(BigInt x, BigInt y) {

		BigInt result;
		if (x.isNeg != y.isNeg) {
			y.isNeg = !y.isNeg;
			result = biAdd(x, y);
			y.isNeg = !y.isNeg;
		} else {
			result = new BigInt();
			int n, c;
			c = 0;
			for (int i = 0; i < x.digits.length; ++i) {
				n = x.digits[i] - y.digits[i] + c;
				result.digits[i] = n % biRadix;
				// Stupid non-conforming modulus operation.
				if (result.digits[i] < 0)
					result.digits[i] += biRadix;
				if (n < 0) {
					c = 0 - 1;
				} else {
					c = 0 - 0;
				}
			}
			// Fix up the negative sign, if any.
			if (c == -1) {
				c = 0;
				for (int i = 0; i < x.digits.length; ++i) {
					n = 0 - result.digits[i] + c;
					result.digits[i] = n % biRadix;
					// Stupid non-conforming modulus operation.
					if (result.digits[i] < 0)
						result.digits[i] += biRadix;
					if (n < 0) {
						c = 0 - 1;
					} else {
						c = 0 - 0;
					}
				}
				// Result is opposite sign of arguments.
				result.isNeg = !x.isNeg;
			} else {
				// Result is same sign.
				result.isNeg = x.isNeg;
			}
		}
		return result;

	}

	static BigInt biMultiply(BigInt x, BigInt y) {

		BigInt result = new BigInt();
		int c;
		int n = biHighIndex(x);
		int t = biHighIndex(y);
		int u, k, j;
		// int uv;
		long uv;

		for (int i = 0; i <= t; ++i) {
			c = 0;
			k = i;
			for (j = 0; j <= n; ++j, ++k) {
				uv = (long) result.digits[k] + (long) x.digits[j]
						* (long) y.digits[i] + (long) c;
				result.digits[k] = (int) (uv & (long) maxDigitVal);
				result.digits[k] = (int) (uv & (long) maxDigitVal);
				c = (int) (uv / (long) 65536);
				// uv = result.digits[k] + x.digits[j] * y.digits[i] + c;
				// result.digits[k] = uv & maxDigitVal;
				// c = uv >> biRadixBits;

			}
			result.digits[i + n + 1] = c;
		}
		// Someone give me a logical xor, please.
		result.isNeg = x.isNeg != y.isNeg;
		return result;

	}

	static void arrayCopy(int[] src, int srcStart, int[] dest, int destStart,
			int n) {
		int m = Math.min(srcStart + n, src.length);
		for (int i = srcStart, j = destStart; i < m; ++i, ++j) {
			dest[j] = src[i];
		}
	}

	static BigInt biShiftRight(BigInt x, int n) {
		int digitCount = (int) Math.floor(((double) n)
				/ ((double) bitsPerDigit));
		BigInt result = new BigInt();
		arrayCopy(x.digits, digitCount, result.digits, 0, x.digits.length
				- digitCount);
		int bits = n % bitsPerDigit;
		int leftBits = bitsPerDigit - bits;
		int[] lowBitMasks = { 0x0000, 0x0001, 0x0003, 0x0007, 0x000F, 0x001F,
				0x003F, 0x007F, 0x00FF, 0x01FF, 0x03FF, 0x07FF, 0x0FFF, 0x1FFF,
				0x3FFF, 0x7FFF, 0xFFFF };
		for (int i = 0, i1 = i + 1; i < result.digits.length - 1; ++i, ++i1) {
			result.digits[i] = (result.digits[i] >> bits)
					| ((result.digits[i1] & lowBitMasks[bits]) << leftBits);
		}
		result.digits[result.digits.length - 1] >>= bits;
		result.isNeg = x.isNeg;
		return result;
	}

	static int biCompare(BigInt x, BigInt y) {
		if (x.isNeg != y.isNeg) {
			return 1 - 2 * Number(x.isNeg);
		}
		for (int i = x.digits.length - 1; i >= 0; --i) {
			if (x.digits[i] != y.digits[i]) {
				if (x.isNeg) {
					return 1 - 2 * Number(x.digits[i] > y.digits[i]);
				} else {
					return 1 - 2 * Number(x.digits[i] < y.digits[i]);
				}
			}
		}
		return 0;
	}

	static int Number(boolean flag) {
		if (flag) {
			return 1;
		} else {
			return 0;
		}
	}

	static BigInt biMultiplyByRadixPower(BigInt x, int n) {
		BigInt result = new BigInt();
		arrayCopy(x.digits, 0, result.digits, n, result.digits.length - n);
		return result;
	}

	static BigInt biDivideByRadixPower(BigInt x, int n) {
		BigInt result = new BigInt();
		arrayCopy(x.digits, n, result.digits, 0, result.digits.length - n);
		return result;
	}

	static String encryptedString(String password) {
		int[] a = new int[126];
		int sl = password.length();
		int i = 0;
		while (i < sl) {
			// a[i] = (int)Convert.ToChar(password.substring(i, 1));
			a[i] = (int) password.charAt(i);
			i++;
		}
		while (a.length % key_chunkSize != 0) {
			a[i++] = 0;
		}
		int al = a.length;
		String result = "";
		int j, k;
		BigInt block;
		for (i = 0; i < al; i += key_chunkSize) {
			block = new BigInt();
			j = 0;
			for (k = i; k < i + key_chunkSize; ++j) {
				block.digits[j] = a[k++];
				block.digits[j] += a[k++] << 8;
			}
			BigInt crypt = powMod(block, key_e);
			// string text = key_radix == 16 ? biToHex(crypt) :
			// biToString(crypt, key_radix);
			String text = biToHex(crypt);
			result += text + " ";
		}
		return result.substring(0, result.length() - 1);
	}

	static BigInt powMod(BigInt x, BigInt y) {

		BigInt result = new BigInt();
		result.digits[0] = 1;
		BigInt a = x;
		BigInt k = y;
		while (true) {
			if ((k.digits[0] & 1) != 0)
				result = multiplyMod(result, a);
			k = biShiftRight(k, 1);
			if (k.digits[0] == 0 && biHighIndex(k) == 0)
				break;
			a = multiplyMod(a, a);
		}
		return result;
	}

	static BigInt multiplyMod(BigInt x, BigInt y) {
		BigInt xy = biMultiply(x, y);
		return modulo(xy);
	}

	static BigInt modulo(BigInt x) {
		BigInt q1 = biDivideByRadixPower(x, this_k - 1);
		BigInt q2 = biMultiply(q1, this_mu);
		BigInt q3 = biDivideByRadixPower(q2, this_k + 1);
		BigInt r1 = biModuloByRadixPower(x, this_k + 1);
		BigInt r2term = biMultiply(q3, this_modulus);
		BigInt r2 = biModuloByRadixPower(r2term, this_k + 1);
		BigInt r = biSubtract(r1, r2);
		if (r.isNeg) {
			r = biAdd(r, this_bkplus1);
		}
		boolean rgtem = biCompare(r, this_modulus) >= 0;
		while (rgtem) {
			r = biSubtract(r, this_modulus);
			rgtem = biCompare(r, this_modulus) >= 0;
		}
		return r;
	}

	static BigInt biModuloByRadixPower(BigInt x, int n) {
		BigInt result = new BigInt();
		arrayCopy(x.digits, 0, result.digits, 0, n);
		return result;
	}

}
