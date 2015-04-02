 package qhichwa.client;
 
 import com.sun.star.lang.Locale;
 import com.sun.star.linguistic2.XSpellAlternatives;
 
 public class SpellAlternatives
   implements XSpellAlternatives
 {
   private String word;
   private Locale locale;
              private Client qhichwaClient = null;
              private String[] rv = new String[0];
   
   public SpellAlternatives(String word, Locale locale, Client client)
   {
     this.word = word;
     this.locale = locale;
                this.qhichwaClient = client;
                this.rv = this.qhichwaClient.getAlternatives(this.word);                
   }
   
   public String getWord()
   {
     return this.word;
   }
   
   public Locale getLocale()
   {
     return this.locale;
   }
   
   public String[] getAlternatives()
   {
     //String[] rv = new String[0];
     return this.rv;
   }
   
   public short getAlternativesCount()
   {
     return (short)this.rv.length;
   }
   
   public short getFailureType()
   {
     return 1;
   }
 }
