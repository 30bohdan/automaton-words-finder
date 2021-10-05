public class ResultingSides {

	byte[] word;
	byte[] groupEl;
	
	public ResultingSides(int elLength, int wordLength) {
		word = new byte[wordLength];
		groupEl = new byte[elLength];
	}

	public boolean isEqual(ResultingSides s) {
		if ((s.word.length != word.length) || (s.groupEl.length != groupEl.length)) {
			return false;
		} else {
			for (int i = 0; i < word.length; i++) {
				if (word[i] != s.word[i]) {
					return false;
				}
			}
			for (int j = 0; j < groupEl.length; j++) {
				if (groupEl[j] != s.groupEl[j])
					return false;
			}
		}
		return true;
	}
	
}