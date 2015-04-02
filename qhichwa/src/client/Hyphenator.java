package qhichwa.client;

import com.sun.star.lang.Locale;
import com.sun.star.linguistic2.XHyphenatedWord;

public class Hyphenator {

    public static final String vowels = "aiueo√�?y√¶√•0AIUEY√Ö√ò√Ü";
    public static final String consonants = "ntsrlmqkgpfvjdhbczwxNKSTXPMGDJLQCHBRFVWZ";

    public static boolean is_vowel(int c) {
        return "aiueo√�?y√¶√•0AIUEY√Ö√ò√Ü".indexOf(c) != -1;
    }

    public static boolean is_consonant(int c) {
        return "ntsrlmqkgpfvjdhbczwxNKSTXPMGDJLQCHBRFVWZ".indexOf(c) != -1;
    }

    public static XHyphenatedWord hyphenate(String word, Locale aLocale, short nMaxLeading) {
        String lcword = word.toLowerCase();
        int pos = -1;
        for (short i = 0; (i < nMaxLeading) && (i < lcword.length() - 2); i = (short) (i + 1)) {
            if ((lcword.charAt(i) == lcword.charAt(i + 1)) && (is_consonant(lcword.charAt(i)))) {
                pos = i;
            } else if ((lcword.charAt(i) != lcword.charAt(i + 1)) && (is_vowel(lcword.charAt(i))) && (is_vowel(lcword.charAt(i + 1)))) {
                pos = i;
            } else if ((is_vowel(lcword.charAt(i))) && (is_consonant(lcword.charAt(i + 1))) && (is_vowel(lcword.charAt(i + 2)))) {
                pos = i;
            } else if ((i < lcword.length() - 3) && (is_vowel(lcword.charAt(i))) && (lcword.charAt(i + 1) == 'n') && (lcword.charAt(i + 2) == 'g') && (is_vowel(lcword.charAt(i + 3)))) {
                pos = i;
            } else if ((lcword.charAt(i) == 'r') && (is_consonant(lcword.charAt(i + 1)))) {
                pos = i;
            } else if ((lcword.charAt(i) == 't') && (lcword.charAt(i + 1) == 's')) {
                pos = i;
            }
        }
        if (pos != -1) {
            return new HyphenatedWord(word, aLocale, (short) pos);
        }
        return null;
    }
}
