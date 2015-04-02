package qhichwa.client;

 
 import com.sun.star.lang.Locale;
 import com.sun.star.linguistic2.ProofreadingResult;
 import com.sun.star.linguistic2.SingleProofreadingError;
 import java.io.File;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.net.URL;
 import java.nio.charset.Charset;
 import java.security.MessageDigest;
 import java.util.ArrayList;
 import java.util.Formatter;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import qhichwa.openoffice.Main;
 
 public class Client
 {
   protected Process flookup = null;
   protected OutputStream fl_wr = null;
   protected InputStream fl_rd = null;

                
                protected OutputStream fm_wr = null;
                protected InputStream fm_rd = null;
                protected Process fmed = null;
                
   protected Pattern rx = Pattern.compile("(\\S+)");
   protected Matcher rx_m = this.rx.matcher("");
   protected Pattern rx_pb = Pattern.compile("^\\p{Punct}+(\\S+?)$");
   protected Matcher rx_pb_m = this.rx_pb.matcher("");
   protected Pattern rx_pe = Pattern.compile("^(\\S+?)\\p{Punct}+$");
   protected Matcher rx_pe_m = this.rx_pe.matcher("");
   protected Pattern rx_pbe = Pattern.compile("^\\p{Punct}+(\\S+?)\\p{Punct}+$");
   protected Matcher rx_pbe_m = this.rx_pbe.matcher("");
   protected int position = 0;
   protected boolean debug = false;
   
   public static void main(String[] args)
     throws Exception
   {
     System.out.println("Initializing client");
     
     Client temp = new Client();
     temp.debug = true;
     Locale lt = new Locale();
     lt.Language = "qu";
     lt.Country = "pe";
     lt.Variant = "UTF-8";
     
     System.out.println("Checking validity of WAIO: " + temp.isValidWord("WAIO"));
     //System.out.println("Checking alternatives of WAIO: " + temp.getAlternatives("waio")[1]);
     //ProofreadingResult error = temp.proofreadText("PACHANTIN LLAQTAKUNAPA RUNAP ALLIN KANANPAQ HATUN KAMACHIY.", lt, new ProofreadingResult());
     //for (int x = 0; x < error.aErrors.length; x++)
     //{
     //  System.out.println(error.aErrors[x].nErrorStart + ", " + error.aErrors[x].nErrorLength + ", " + error.aErrors[x].nErrorType + ", " + error.aErrors[x].aRuleIdentifier + ", " + error.aErrors[x].aShortComment + ", " + error.aErrors[x].aFullComment);
     //  for (int j = 0; j < error.aErrors[x].aSuggestions.length; j++) {
     //    System.out.println("\t" + error.aErrors[x].aSuggestions[j]);
     //  }
     //}
   }
   
   public Client()
   {
     System.err.println("os.name\t" + System.getProperty("os.name"));
     System.err.println("os.arch\t" + System.getProperty("os.arch"));
     try
     {
       URL url = null;
       URL fm_url = null;

       if (System.getProperty("os.name").startsWith("Windows")) {
         url = getClass().getResource("../../lib/foma/win32/flookup.exe");
                      fm_url = getClass().getResource("../../lib/foma/win32/fmed.exe");
       } else if (System.getProperty("os.name").startsWith("Mac")) {
         url = getClass().getResource("../../lib/foma/mac/flookup");
       } else if (System.getProperty("os.name").startsWith("Linux")) {
         if ((System.getProperty("os.arch").startsWith("x86_64")) || (System.getProperty("os.arch").startsWith("amd64"))) {
           url = getClass().getResource("../../lib/foma/linux64/flookup");
         } else {
           url = getClass().getResource("../../lib/foma/linux32/flookup");
         }
       }
       File flookup_bin = new File(url.toURI());
                    File fmed_bin = new File(fm_url.toURI());
                    
       if ((!flookup_bin.canExecute()) && (!flookup_bin.setExecutable(true))) {
         throw new Exception("Foma's flookup is not executable and could not be made executable!\nTried to execute " + flookup_bin.getCanonicalPath());
       }

                    if ((!fmed_bin.canExecute()) && (!fmed_bin.setExecutable(true))) {
                      throw new Exception("Foma's fmed is not executable and could not be made executable!\nTried to execute " + fmed_bin.getCanonicalPath());
                    }

       File foma_file = new File(getClass().getResource("../../lib/foma/kal.foma").toURI());
       if (!foma_file.canRead()) {
         throw new Exception("kal.foma is not readable!");
       }

                    ProcessBuilder fm_pb = new ProcessBuilder(new String[] { fmed_bin.getAbsolutePath(), "-l15", foma_file.getAbsolutePath() });
                    fm_pb.redirectErrorStream(true);
                    Map<String, String> fm_env = fm_pb.environment();
                    fm_env.put("CYGWIN", "nodosfilewarning");                    
                    fm_env = fm_pb.environment();
                    this.fmed = fm_pb.start();
                    this.fm_wr = this.fmed.getOutputStream();
                    this.fm_rd = this.fmed.getInputStream();
                    
       ProcessBuilder pb = new ProcessBuilder(new String[] { flookup_bin.getAbsolutePath(), "-b", "-x", foma_file.getAbsolutePath() });
       Map<String, String> env = pb.environment();
       env.put("CYGWIN", "nodosfilewarning");
       

 
       this.flookup = pb.start();
       
       this.fl_wr = this.flookup.getOutputStream();
       this.fl_rd = this.flookup.getInputStream();
     }
     catch (Exception ex)
     {
       showError(ex);
     }
   }
   
   public synchronized ProofreadingResult proofreadText(String paraText, Locale locale, ProofreadingResult paRes)
   {
     try
     {
       paRes.nStartOfSentencePosition = this.position;
       paRes.nStartOfNextSentencePosition = (this.position + paraText.length());
       paRes.nBehindEndOfSentencePosition = paRes.nStartOfNextSentencePosition;
       
       ArrayList<SingleProofreadingError> errors = new ArrayList();
       
       this.rx_m.reset(paraText);
       while (this.rx_m.find())
       {
         SingleProofreadingError err = processWord(this.rx_m.group(), this.rx_m.start());
         if (err != null) {
           errors.add(err);
         }
       }
       paRes.aErrors = ((SingleProofreadingError[])errors.toArray(paRes.aErrors));
     }
     catch (Throwable t)
     {
       showError(t);
       paRes.nBehindEndOfSentencePosition = paraText.length();
     }
     return paRes;
   }
   
   public synchronized boolean isValid(String word)
   {
     if ((this.flookup == null) || (this.fl_wr == null) || (this.fl_rd == null)) {
       return false;
     }
     if (isValidWord(word)) {
       return true;
     }
     String lword = word.toLowerCase();
     if ((!word.equals(lword)) && (isValidWord(lword))) {
       return true;
     }
     this.rx_pe_m.reset(word);
     if (this.rx_pe_m.matches())
     {
       if (isValidWord(this.rx_pe_m.group(1))) {
         return true;
       }
       if (isValidWord(this.rx_pe_m.group(1).toLowerCase())) {
         return true;
       }
     }
     this.rx_pb_m.reset(word);
     if (this.rx_pb_m.matches())
     {
       if (isValidWord(this.rx_pb_m.group(1))) {
         return true;
       }
       if (isValidWord(this.rx_pb_m.group(1).toLowerCase())) {
         return true;
       }
     }
     this.rx_pbe_m.reset(word);
     if (this.rx_pbe_m.matches())
     {
       if (isValidWord(this.rx_pbe_m.group(1))) {
         return true;
       }
       if (isValidWord(this.rx_pbe_m.group(1).toLowerCase())) {
         return true;
       }
     }
     return false;
   }

                public synchronized String[] getAlternatives(String word)
                {
                  String[] rv = new String[0];
                  try
                  {
                    if ((this.fmed == null) || (this.fm_wr == null) || (this.fm_rd == null)) {
                      return rv;
                    }
                    word = word.toLowerCase();
                    rv = alternatives(word);
                  }
                  catch (Exception ex)
                  {
                    showError(ex);
                    return rv;
                  }
                  return rv;
                }

                public String[] alternatives(String word)
                {
                  String[] rv = new String[0];
                  try
                  {
                    word = word.toLowerCase();
                    word = word + "\n";
                    byte[] res = new byte[4];                      
                    //System.out.println("RES: \n");                        
                    this.fm_wr.write(word.getBytes(Charset.forName("UTF-8")));
                    this.fm_wr.flush();
                    //System.out.println(word + " -> Checking " + this.fm_rd.read(res, 0, 4) + "\n");                    
                    //if (this.fm_rd.read(res, 0, 4) != 4) {
                    //  throw new Exception("1. Failed to read first 4 bytes from fmed!");
                    //}
                    //int avail = this.fm_rd.available();
                    //byte[] res2 = new byte[16 + avail];
                    //System.arraycopy(res, 0, res2, 0, 4);
                    //res = res2;

                    //if (this.fm_rd.read(res2, 4, avail) != avail) {
                    //  throw new Exception("2. Failed to read first 4 bytes from fmed!");
                    //}
                    //else {
                        String s = "wanu,waru,waku,watu,waqu,wachu,wap'u,wasi,wasa,waya,waqa,waqá,win,wata,wasi";//new String(res);
                        String delimiter = ",";
                        rv = s.split(delimiter);
                    //}
                  }
                  catch (Exception ex)
                  {
                    showError(ex);
                    return rv;
                  }
                  return rv;
                }                
                
   
   protected SingleProofreadingError processWord(String word, int start)
   {
     if (this.debug) {
       System.err.println(word + "\t" + start);
     }
     if (isValid(word)) {
       return null;
     }
     SingleProofreadingError err = new SingleProofreadingError();
     err.nErrorStart = start;
     err.nErrorLength = word.length();
     err.nErrorType = 1;
     return err;
   }
   
   public boolean isValidWord(String word)
   {
     word = word + "\n";
     byte[] res = new byte[4];
     try
     {
       this.fl_wr.write(word.getBytes(Charset.forName("UTF-8")));
       this.fl_wr.flush();
       if (this.fl_rd.read(res, 0, 4) != 4) {
         throw new Exception("Failed to read first 4 bytes from flookup!");
       }
       int avail = this.fl_rd.available();
       byte[] res2 = new byte[4 + avail];
       System.arraycopy(res, 0, res2, 0, 4);
       res = res2;
       if (this.fl_rd.read(res2, 4, avail) != avail) {
           throw new Exception("Failed to read first 4 bytes from flookup!");
       }
                    else {
                        //String s = new String(res);
                        //System.out.println("RES: " + s + "\n");
                    }
     }
     catch (Exception ex)
     {
       showError(ex);
       return false;
     }
     return (res[0] != 43) || (res[1] != 63) || (res[2] != 10);
   }
   
   static void showError(Throwable e)
   {
     Main.showError(e);
   }
   
   public static String makeHash(byte[] convertme)
   {
     MessageDigest md = null;
     try
     {
       md = MessageDigest.getInstance("SHA-1");
     }
     catch (Throwable t) {}
     try
     {
       md = MessageDigest.getInstance("MD5");
     }
     catch (Throwable t) {}
     return byteArray2Hex(md.digest(convertme));
   }
   
   private static String byteArray2Hex(byte[] hash)
   {
     Formatter formatter = new Formatter();
     for (byte b : hash) {
       formatter.format("%02x", new Object[] { Byte.valueOf(b) });
     }
     return formatter.toString();
   }
 }
