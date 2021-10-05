import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;



/* 
Automaton is represented by two arrays numofStates*numOfletters
First array indicate the destination of the arrow from state with given input letter 
Second array indicate the output letter from state with given input letter 
All words are decoded.
*/

public class Automaton {

	byte[][] arrows;
	byte[][] permutation;
	byte numOfStates;
	byte numOfLetters;

	public Automaton(int numOfStates, int numOfLetters) {
		arrows = new byte[numOfStates][numOfLetters];
		permutation = new byte[numOfStates][numOfLetters];
		this.numOfStates = (byte) numOfStates;
		this.numOfLetters = (byte) numOfLetters;
	}

	public ResultingSides findImage(byte el, byte[] word) {
		ResultingSides result = new ResultingSides(1, word.length);
		System.arraycopy(word, 0, result.word, 0, word.length);
		result.groupEl[0] = el;
		for (int j = 0; j < word.length; j++) {
			byte t = result.word[j];
			result.word[j] = permutation[result.groupEl[0]][t];
			result.groupEl[0] = arrows[result.groupEl[0]][t];
		}
		return result;
	}

	public ResultingSides findImage(byte[] groupEl, byte[] word) {
		ResultingSides result = new ResultingSides(groupEl.length, word.length);
		System.arraycopy(word, 0, result.word, 0, word.length);
		for (int i = 0; i < groupEl.length; i++) {
			ResultingSides preResult = findImage(groupEl[i], result.word);
			result.word = preResult.word;
			result.groupEl[i] = preResult.groupEl[0];
		}
		return result;

	}

	public void print(String name, PrintWriter out) {
		out.println(name);
		out.print("Arrows: {");
		for (int i = 0; i < numOfStates; i++) {
			out.print("{");
			for (int j = 0; j < numOfLetters - 1; j++) {
				out.print(arrows[i][j] + ", ");
			}
			out.print(arrows[i][numOfLetters - 1] + "}");
			if (i < numOfStates - 1) {
				out.print(", ");
			} else {
				out.println("}}");
			}
		}
		out.print("Permutations: {");
		for (int i = 0; i < numOfStates; i++) {
			out.print("{");
			for (int j = 0; j < numOfLetters - 1; j++) {
				out.print(permutation[i][j] + ", ");
			}
			out.print(permutation[i][numOfLetters - 1] + "}");
			if (i < numOfStates - 1) {
				out.print(", ");
			} else {
				out.println("}}");
			}
		}
	}

	public int encoder(byte[] word, int numOfLetters) {
		int result = 0;
		int len = word.length;
		int pow = 1;
		for (int j = 0; j < len; j++) {
			result += pow * word[j];
			pow = pow * numOfLetters;
		}
		return result;
	}

	public byte[] decoder(long cipher, int numOfLetters, int length) {
		// int len = (int) ((Math.log(cipher) / Math.log(numOfLetters)) + 0.9999);
		byte[] result = new byte[length];
		int i = 0;
		while (cipher > 0) {
			result[i] = (byte) (cipher % numOfLetters);
			cipher = cipher / numOfLetters;
			i += 1;
		}
		return result;
	}

	private class CurrentState {

		public int numOfExaminedLetters;
		public int wordCipher;
		public byte groupEl;
	}

	public ResultingSides findGroupEl(int length) {

		int numWords = (int) Math.pow(numOfLetters, length);
		// if its more then 7-8 states there must be int but not byte
		int[] words = new int[numWords];
		
		int[] degree = new int[1 << numOfStates];
		for (int step = 0; step < numWords; step++) {
			byte[] goodLetters = new byte[numOfStates];
			for (byte i = 0; i < numOfStates; i++) {
				byte[] word = decoder(step, numOfLetters, length);

				if (findImage(i, word).groupEl[0] == i) {
					// Insertion to print all arrows, that saves letter
					/*
					 * for (int t=0; t<word.length; t++){ System.out.printf("%d", word[t]); }
					 * System.out.printf(" -"+i+"-> "); for (int t=0; t<word.length; t++){
					 * System.out.printf("%d", findImage(i, word).word[t]); } System.out.println();
					 */// end of insertion;
					goodLetters[i] = 1;
				}
			}
			words[step] = encoder(goodLetters, 2);

			degree[words[step]]++;
		}

		for (int step = 0; step < numWords; step++) {
			LinkedList<Byte> groupElement;
			groupElement = new LinkedList();
			LinkedList<Integer> visited;
			visited = new LinkedList();
			int elExam = 0;
			LinkedList st = new LinkedList();
			if (words[step] != 0) {
				int currentState = step;
				visited.push(currentState);
				do {
					if ((words[currentState] <= 0) && (words[currentState] != -step - 1)) {
						words[currentState] = 0;
						while (!st.isEmpty()) {
							CurrentState c = (CurrentState) st.pop();
							currentState = c.wordCipher;

							if ((words[c.wordCipher] > 0) || (words[c.wordCipher] == -step - 1)) {
								// review!
								// visited.push(currentState);
								while (elExam > c.numOfExaminedLetters) {
									elExam -= 1;
									groupElement.pop();
									int v = visited.pop();
									words[v] = 0;
								}
								groupElement.push(c.groupEl);
								visited.push(currentState);
								elExam += 1;
								break;
							} else {
								words[currentState] = 0;
							}
						}
					}
					if (words[currentState] > 0) {
						byte[] word = decoder(currentState, numOfLetters, length);
						byte elem = 0;
						int cipher = words[currentState];
						while (cipher % 2 == 0) {
							elem += 1;
							cipher = cipher / 2;
						}
						cipher = cipher / 2;
						ResultingSides s = findImage(elem, word);
						words[currentState] = -step - 1;
						currentState = encoder(s.word, numOfLetters);
						visited.push(currentState);
						groupElement.push(elem);

						while (cipher != 0) {
							elem += 1;
							if (cipher % 2 == 1) {
								s = findImage(elem, word);
								CurrentState c = new CurrentState();
								c.groupEl = elem;
								c.numOfExaminedLetters = elExam;
								c.wordCipher = encoder(s.word, numOfLetters);
								st.push(c);
							}
							cipher = cipher / 2;
						}
						elExam += 1;

					}
					if (((words[currentState] == -step - 1) || (currentState == step)) && (elExam > 0)) {
						byte[] initWord = decoder(step, numOfLetters, length);
						while (encoder(initWord, numOfLetters) != currentState) {
							byte elem = groupElement.removeLast();
							elExam -= 1;
							initWord = findImage(elem, initWord).word;
						}
						ResultingSides result = new ResultingSides(elExam, length);
						result.word = initWord;
						for (int j = 0; j < elExam; j++) {
							result.groupEl[j] = groupElement.removeLast();
						}
						return result;
					}

				} while (!((currentState == step) || ((st.isEmpty()) && (words[currentState] <= 0))));

			}

		}
		return null;

	}

	public Automaton dual() {
		Automaton result = new Automaton(numOfLetters, numOfStates);
		for (int i = 0; i < numOfLetters; i++) {
			for (int j = 0; j < numOfStates; j++) {
				result.arrows[i][j] = permutation[j][i];
				result.permutation[i][j] = arrows[j][i];
			}
		}
		return result;
	}
	
	public static Automaton genRandomAutomaton(int numOfStates, int numOfLabels) {
        Automaton result = new Automaton(numOfStates, numOfLabels);
        for (int i = 0; i < numOfStates; i++) {
            Random rand = new Random();
            for (int j = 0; j < numOfLabels; j++) {
                result.arrows[i][j] = (byte) rand.nextInt(numOfStates);
            }
            ArrayList l = new ArrayList(numOfLabels);
            for (byte j = 0; j < numOfLabels; j++) {
                l.add(j);
            }
            int j = 0;
            while (!l.isEmpty()) {
                int k = rand.nextInt(l.size());
                result.permutation[i][j] = (byte) l.remove(k);
                j++;
            }
        }
        return result;
    }
	
	
	public static Automaton genRandomRevAutomaton(int numOfStates, int numOfLabels) {
        Automaton result = new Automaton(numOfStates, numOfLabels);
        for (int i = 0; i < numOfStates; i++) {
            Random rand = new Random();
            ArrayList l = new ArrayList(numOfLabels);
            for (byte j = 0; j < numOfLabels; j++) {
                l.add(j);
            }
            int j = 0;
            while (!l.isEmpty()) {
                int k = rand.nextInt(l.size());
                result.permutation[i][j] = (byte) l.remove(k);
                j++;
            }
        }
        Random rand = new Random();
        ArrayList l = new ArrayList(numOfLabels);
        for (byte i = 0; i < numOfLabels; i++) {
            for (byte j = 0; j < numOfStates; j++) {
                l.add(j);
            }

            int j = 0;
            while (!l.isEmpty()) {
                int k = rand.nextInt(l.size());
                result.arrows[j][i] = (byte) l.remove(k);
                j++;
            }
        }
        return result;
    }

}