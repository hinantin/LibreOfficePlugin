 package qhichwa.client;
 
 import com.sun.star.lang.Locale;
 import com.sun.star.linguistic2.XPossibleHyphens;
 
 public class PossibleHyphens
   implements XPossibleHyphens
 {
   private String word;
   private String lcword;
   private Locale locale;
   
   public PossibleHyphens(String word, Locale locale)
   {
     this.word = word;
     this.lcword = word.toLowerCase();
     this.locale = locale;
   }
   
   public short[] getHyphenationPositions()
   {
     int num = 0;
     for (short i = 0; i < this.lcword.length() - 2; i = (short)(i + 1)) {
       if ((this.lcword.charAt(i) == this.lcword.charAt(i + 1)) && (Hyphenator.is_consonant(this.lcword.charAt(i)))) {
         num++;
       } else if ((this.lcword.charAt(i) != this.lcword.charAt(i + 1)) && (Hyphenator.is_vowel(this.lcword.charAt(i))) && (Hyphenator.is_vowel(this.lcword.charAt(i + 1)))) {
         num++;
       } else if ((Hyphenator.is_vowel(this.lcword.charAt(i))) && (Hyphenator.is_consonant(this.lcword.charAt(i + 1))) && (Hyphenator.is_vowel(this.lcword.charAt(i + 2)))) {
         num++;
       } else if ((i < this.lcword.length() - 3) && (Hyphenator.is_vowel(this.lcword.charAt(i))) && (this.lcword.charAt(i + 1) == 'n') && (this.lcword.charAt(i + 2) == 'g') && (Hyphenator.is_vowel(this.lcword.charAt(i + 3)))) {
         num++;
       } else if ((this.lcword.charAt(i) == 'r') && (Hyphenator.is_consonant(this.lcword.charAt(i + 1)))) {
         num++;
       } else if ((this.lcword.charAt(i) == 't') && (this.lcword.charAt(i + 1) == 's')) {
         num++;
       }
     }
     short[] ps = new short[num];
     num = 0;
     for (short i = 0; i < this.lcword.length() - 2; i = (short)(i + 1)) {
       if ((this.lcword.charAt(i) == this.lcword.charAt(i + 1)) && (Hyphenator.is_consonant(this.lcword.charAt(i)))) {
         ps[(num++)] = i;
       } else if ((this.lcword.charAt(i) != this.lcword.charAt(i + 1)) && (Hyphenator.is_vowel(this.lcword.charAt(i))) && (Hyphenator.is_vowel(this.lcword.charAt(i + 1)))) {
         ps[(num++)] = i;
       } else if ((Hyphenator.is_vowel(this.lcword.charAt(i))) && (Hyphenator.is_consonant(this.lcword.charAt(i + 1))) && (Hyphenator.is_vowel(this.lcword.charAt(i + 2)))) {
         ps[(num++)] = i;
       } else if ((i < this.lcword.length() - 3) && (Hyphenator.is_vowel(this.lcword.charAt(i))) && (this.lcword.charAt(i + 1) == 'n') && (this.lcword.charAt(i + 2) == 'g') && (Hyphenator.is_vowel(this.lcword.charAt(i + 3)))) {
         ps[(num++)] = i;
       } else if ((this.lcword.charAt(i) == 'r') && (Hyphenator.is_consonant(this.lcword.charAt(i + 1)))) {
         ps[(num++)] = i;
       } else if ((this.lcword.charAt(i) == 't') && (this.lcword.charAt(i + 1) == 's')) {
         ps[(num++)] = i;
       }
     }
     return ps;
   }
   
   public Locale getLocale()
   {
     return this.locale;
   }
   
   public String getPossibleHyphens()
   {
     short[] ps = getHyphenationPositions();
     StringBuilder sb = new StringBuilder(this.word);
     for (int i = ps.length; i > 0; i--) {
       sb.insert(ps[(i - 1)] + 1, '=');
     }
     return sb.toString();
   }
   
   public String getWord()
   {
     return this.word;
   }
 }

