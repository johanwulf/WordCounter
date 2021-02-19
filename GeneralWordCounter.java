package WordCounter;

import java.util.*;

public class GeneralWordCounter {
	Map<String, Integer> map = new TreeMap<String, Integer>();
	Set<String> banned;
	
	/**
	 * Creates a set of banned words
	 * @param banned the set of banned words that will not be counted
	 */
	public GeneralWordCounter(Set<String> banned) {
		this.banned = banned;
	}
	
	/**
	 * Processes the word w and skips if the word is banned
	 * @param w the word we are looking for
	 */
	public void process(String w) {
		if(!banned.contains(w)) {
			map.putIfAbsent(w, 0);
			map.put(w, map.get(w)+1);
		}
	}

	/*
	 * Returns wordList
	 */
	public List<Map.Entry<String, Integer>> getWordList() {
		Set<Map.Entry<String, Integer>> wordSet = map.entrySet();
		List<Map.Entry<String, Integer>> wordList = new ArrayList<>(wordSet);

		return wordList;
	}
}
