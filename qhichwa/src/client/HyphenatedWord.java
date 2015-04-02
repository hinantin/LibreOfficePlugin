 package qhichwa.client;
 
 import com.sun.star.lang.Locale;
 import com.sun.star.linguistic2.XHyphenatedWord;
 
 public class HyphenatedWord
   implements XHyphenatedWord
 {
   private String word;
   private Locale locale;
   private short pos;
   
   public HyphenatedWord(String word, Locale locale, short pos)
   {
     this.word = word;
     this.locale = locale;
     this.pos = pos;
   }
   
   public short getHyphenPos()
   {
     return this.pos;
   }
   
   public String getHyphenatedWord()
   {
     return getWord();
   }
   
   public short getHyphenationPos()
   {
     return getHyphenPos();
   }
   
   public Locale getLocale()
   {
     return this.locale;
   }
   
   public String getWord()
   {
     return this.word;
   }
   
   public boolean isAlternativeSpelling()
   {
     return false;
   }
 }
