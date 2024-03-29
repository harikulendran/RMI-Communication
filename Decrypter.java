public class Decrypter {
	private char[][] bytes;
	private char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	public Decrypter(String input) {
		char[] string = input.toCharArray();
		bytes = new char[input.length()/8][8];
		for (int i=0; i<bytes.length; i++)
			for (int j=0; j<8; j++) {
				bytes[i][j] = string[i*8+j];
			}
	}

	public String decrypt(int key) {
		deSubstitute(key);
		deTranspose(key);
		deSubstitute(key);
		deTranspose(key);
		String out = "";
		for (int i=0; i<bytes.length; i++)
			for (int j=0; j<8; j++)
				out += bytes[i][j];
		return out;
	}

	public void deTranspose(int key) {
		for (int i=0; i<bytes.length; i++) {
			char[] shuffled = new char[8];
			for (int j=0; j<8; j++)
				shuffled[(j-(key%8) + 8)%8] = bytes[i][j];
			bytes[i] = shuffled;
		}
	}

	public void deSubstitute(int key) {
		for (int i=0; i<bytes.length; i++)
			for (int j=0; j<8; j++)
				bytes[i][j] = shift(bytes[i][j], key%26);
	}

	private char shift(char c, int o) {
		int val = 0;
		for (int i=0; i<alphabet.length; i++)
			if (alphabet[(val = i)] == c)
				break;
		val = (val - o + 26)%26;
		return alphabet[val];
	}
}
