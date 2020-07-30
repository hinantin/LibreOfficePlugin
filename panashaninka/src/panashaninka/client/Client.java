package panashaninka.client;

              
              import com.sun.star.lang.Locale;
              import com.sun.star.linguistic2.ProofreadingResult;
              import com.sun.star.linguistic2.SingleProofreadingError;
              import java.io.BufferedReader;
              import java.io.File;
              import java.io.InputStream;
              import java.io.InputStreamReader;
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
              import panashaninka.openoffice.Main;
              
              public class Client
              {
                protected Process flookup = null;
                protected OutputStream fl_wr = null;
                protected InputStream fl_rd = null;

                /* TO OBTAIN THE SUGGESTIONS */
                /*
                protected OutputStream fm_wr = null;
                protected InputStream fm_rd = null;
                protected Process fmed = null;
                */
                protected File flookup_bin = null;
                protected File fmed_bin = null;                
                protected String[] command = null; 
                protected File foma_file = null;
                
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
                  //lt.Language = "qu";
                  //lt.Country = "PE";
                  // SPELL CHECKING CONFIGURATION 
                  lt.Language = "qul";
                  lt.Country = "BO";
                  lt.Variant = "UTF-8";
                  
                  System.out.println("Checking validity of ATIZRI: " + temp.isValidWord("ATIZRI"));
                  String[] alt = temp.getAlternatives("atziriii");
                    for (int i = 0; i < alt.length; i++) {
                        System.out.println("Checking alternatives of ATIZRI: " + alt[i]);        
                    }
/*  50: 41 */     ProofreadingResult error = temp.proofreadText("PONYAA IKANTAVITAKA PASHINI, IROHATSI YATZIRIVITANI SANKATZI, IKANTZI ARI NAAHERI NAAKA.", lt, new ProofreadingResult());
/*  51: 42 */     for (int x = 0; x < error.aErrors.length; x++)
/*  52:    */     {
/*  53: 43 */       System.out.println(error.aErrors[x].nErrorStart + ", " + error.aErrors[x].nErrorLength + ", " + error.aErrors[x].nErrorType + ", " + error.aErrors[x].aRuleIdentifier + ", " + error.aErrors[x].aShortComment + ", " + error.aErrors[x].aFullComment);
/*  54: 44 */       for (int j = 0; j < error.aErrors[x].aSuggestions.length; j++) {
/*  55: 45 */         System.out.println("\t" + error.aErrors[x].aSuggestions[j]);
/*  56:    */       }
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
                public Client()
                {
                  System.err.println("os.name\t" + System.getProperty("os.name"));
                  System.err.println("os.arch\t" + System.getProperty("os.arch"));
                  String foma_file_name = "panashaninka.fst";
                  try
                  {
                    URL url = null;
                    URL fm_url = null;
                    this.command = new String[] { "/bin/bash", "-c", ""};
                    if (System.getProperty("os.name").startsWith("Windows")) {
                      url = getClass().getResource("../../lib/foma/win32/flookup.exe");
                      System.out.println(url);
                      fm_url = getClass().getResource("../../lib/foma/win32/fmed.exe");
                      this.command = new String[] { "CMD", "/C", ""};
                    } else if (System.getProperty("os.name").startsWith("Mac")) {
                      url = getClass().getResource("../../lib/foma/mac/flookup");
                      fm_url = getClass().getResource("../../lib/foma/mac/fmed");
                    } else if (System.getProperty("os.name").startsWith("Linux")) {
                      if ((System.getProperty("os.arch").startsWith("x86_64")) || (System.getProperty("os.arch").startsWith("amd64"))) {
                        url = getClass().getResource("../../lib/foma/linux64/flookup");
                        fm_url = getClass().getResource("../../lib/foma/linux64/fmed");
                      } else {
                        url = getClass().getResource("../../lib/foma/linux32/flookup");
                        fm_url = getClass().getResource("../../lib/foma/linux32/fmed");
                      }
                    }
                    this.flookup_bin = new File(url.toURI());
                    this.fmed_bin = new File(fm_url.toURI());
                    
                    if ((!this.flookup_bin.canExecute()) && (!this.flookup_bin.setExecutable(true))) {
                      throw new Exception("Foma's flookup is not executable and could not be made executable!\nTried to execute " + this.flookup_bin.getCanonicalPath());
                    }

                    if ((!this.fmed_bin.canExecute()) && (!this.fmed_bin.setExecutable(true))) {
                      throw new Exception("Foma's fmed is not executable and could not be made executable!\nTried to execute " + this.fmed_bin.getCanonicalPath());
                    }

                    this.foma_file = new File(getClass().getResource("../../lib/foma/"+foma_file_name).toURI());
                    if (!this.foma_file.canRead()) {
                      throw new Exception(foma_file_name + " is not readable!");
                    }
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
                
/* 102:    */   public synchronized ProofreadingResult proofreadText(String paraText, Locale locale, ProofreadingResult paRes)
/* 103:    */   {
/* 104:    */     try
/* 105:    */     {
/* 106:100 */       paRes.nStartOfSentencePosition = this.position;
/* 107:101 */       paRes.nStartOfNextSentencePosition = (this.position + paraText.length());
/* 108:102 */       paRes.nBehindEndOfSentencePosition = paRes.nStartOfNextSentencePosition;
/* 109:    */       
/* 110:104 */       ArrayList<SingleProofreadingError> errors = new ArrayList();
/* 111:    */       
/* 112:106 */       this.rx_m.reset(paraText);
/* 113:107 */       while (this.rx_m.find())
/* 114:    */       {
/* 115:108 */         SingleProofreadingError err = processWord(this.rx_m.group(), this.rx_m.start());
/* 116:109 */         if (err != null) {
/* 117:110 */           errors.add(err);
/* 118:    */         }
/* 119:    */       }
/* 120:114 */       paRes.aErrors = ((SingleProofreadingError[])errors.toArray(paRes.aErrors));
/* 121:    */     }
/* 122:    */     catch (Throwable t)
/* 123:    */     {
/* 124:117 */       showError(t);
/* 125:118 */       paRes.nBehindEndOfSentencePosition = paraText.length();
/* 126:    */     }
/* 127:120 */     return paRes;
/* 128:    */   }
                  
/* 130:    */   public synchronized boolean isValid(String word)
/* 131:    */   {
/* 132:124 */     if ((this.flookup == null) || (this.fl_wr == null) || (this.fl_rd == null)) {
/* 133:125 */       return false;
/* 134:    */     }
/* 135:128 */     if (isValidWord(word)) {
/* 136:130 */       return true;
/* 137:    */     }
/* 138:133 */     String lword = word.toLowerCase();
/* 139:134 */     if ((!word.equals(lword)) && (isValidWord(lword))) {
/* 140:136 */       return true;
/* 141:    */     }
/* 142:139 */     this.rx_pe_m.reset(word);
/* 143:140 */     if (this.rx_pe_m.matches())
/* 144:    */     {
/* 145:141 */       if (isValidWord(this.rx_pe_m.group(1))) {
/* 146:143 */         return true;
/* 147:    */       }
/* 148:145 */       if (isValidWord(this.rx_pe_m.group(1).toLowerCase())) {
/* 149:147 */         return true;
/* 150:    */       }
/* 151:    */     }
/* 152:151 */     this.rx_pb_m.reset(word);
/* 153:152 */     if (this.rx_pb_m.matches())
/* 154:    */     {
/* 155:153 */       if (isValidWord(this.rx_pb_m.group(1))) {
/* 156:155 */         return true;
/* 157:    */       }
/* 158:157 */       if (isValidWord(this.rx_pb_m.group(1).toLowerCase())) {
/* 159:159 */         return true;
/* 160:    */       }
/* 161:    */     }
/* 162:163 */     this.rx_pbe_m.reset(word);
/* 163:164 */     if (this.rx_pbe_m.matches())
/* 164:    */     {
/* 165:165 */       if (isValidWord(this.rx_pbe_m.group(1))) {
/* 166:167 */         return true;
/* 167:    */       }
/* 168:169 */       if (isValidWord(this.rx_pbe_m.group(1).toLowerCase())) {
/* 169:171 */         return true;
/* 170:    */       }
/* 171:    */     }
/* 172:174 */     return false;
/* 173:    */   }

                public synchronized String[] getAlternatives(String word)
                {
                  String[] rv = new String[0];
                  try
                  {
                    if ((this.fmed_bin == null) || (this.flookup_bin == null) || (this.foma_file == null)) {
                      return rv;
                    }
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
                  word = word.trim().toLowerCase();                  
                  try
                  {
                    String ret = "";      
                    
                    String c = "echo "+ word +"| " + this.fmed_bin.getAbsolutePath() + " -l10 " + this.foma_file.getAbsolutePath();
                    this.command[2] = c;
                    System.out.println(word);
                    Process p = Runtime.getRuntime().exec(this.command);
                    InputStreamReader isr = new InputStreamReader(p.getInputStream(),"UTF8");
                    BufferedReader input = new BufferedReader(isr);
                    String str;
                    while ((str = input.readLine()) != null) {
                        ret = str.trim();
                    }
                    input.close();
                    isr.close();
                    //System.out.println("S: " + ret);                    
                    String delimiter = ",";
                    if (ret.contains(delimiter)) {
                      rv = ret.split(delimiter);
                    }
                    else {
                        rv = new String[1];
                        rv[0] = ret;
                    }                    
                  }
                  catch (Exception ex)
                  {
                    showError(ex);
                    return rv;
                  }
                  return rv;
                }                
                
/* 174:    */   
/* 175:    */   protected SingleProofreadingError processWord(String word, int start)
/* 176:    */   {
/* 177:178 */     if (this.debug) {
/* 178:179 */       System.err.println(word + "\t" + start);
/* 179:    */     }
/* 180:182 */     if (isValid(word)) {
/* 181:183 */       return null;
/* 182:    */     }
/* 183:186 */     SingleProofreadingError err = new SingleProofreadingError();
/* 184:187 */     err.nErrorStart = start;
/* 185:188 */     err.nErrorLength = word.length();
/* 186:189 */     err.nErrorType = 1;
/* 187:190 */     return err;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public boolean isValidWord(String word)
/* 191:    */   {
/* 192:194 */     word = word + "\n";
/* 193:195 */     byte[] res = new byte[4];
/* 194:    */     try
/* 195:    */     {
/* 196:198 */       this.fl_wr.write(word.getBytes(Charset.forName("UTF-8")));
/* 197:199 */       this.fl_wr.flush();
/* 198:201 */       if (this.fl_rd.read(res, 0, 4) != 4) {
/* 199:202 */         throw new Exception("Failed to read first 4 bytes from flookup!");
/* 200:    */       }
/* 201:205 */       int avail = this.fl_rd.available();
/* 202:206 */       byte[] res2 = new byte[4 + avail];
/* 203:207 */       System.arraycopy(res, 0, res2, 0, 4);
/* 204:208 */       res = res2;
/* 205:209 */       if (this.fl_rd.read(res2, 4, avail) != avail) {
/* 206:210 */           throw new Exception("Failed to read first 4 bytes from flookup!");
/* 207:    */       }
                    else {
                        //String s = new String(res);
                        //System.out.println("RES: " + s + "\n");
                    }
/* 208:    */     }
/* 209:    */     catch (Exception ex)
/* 210:    */     {
/* 211:215 */       showError(ex);
/* 212:216 */       return false;
/* 213:    */     }
/* 214:219 */     return (res[0] != 43) || (res[1] != 63) || (res[2] != 10);
/* 215:    */   }
/* 216:    */   
/* 217:    */   static void showError(Throwable e)
/* 218:    */   {
/* 219:223 */     Main.showError(e);
/* 220:    */   }
/* 221:    */   
/* 222:    */   public static String makeHash(byte[] convertme)
/* 223:    */   {
/* 224:227 */     MessageDigest md = null;
/* 225:    */     try
/* 226:    */     {
/* 227:229 */       md = MessageDigest.getInstance("SHA-1");
/* 228:    */     }
/* 229:    */     catch (Throwable t) {}
/* 230:    */     try
/* 231:    */     {
/* 232:234 */       md = MessageDigest.getInstance("MD5");
/* 233:    */     }
/* 234:    */     catch (Throwable t) {}
/* 235:238 */     return byteArray2Hex(md.digest(convertme));
/* 236:    */   }
/* 237:    */   
/* 238:    */   private static String byteArray2Hex(byte[] hash)
/* 239:    */   {
/* 240:242 */     Formatter formatter = new Formatter();
/* 241:243 */     for (byte b : hash) {
/* 242:244 */       formatter.format("%02x", new Object[] { Byte.valueOf(b) });
/* 243:    */     }
/* 244:246 */     return formatter.toString();
/* 245:    */   }
/* 246:    */ }
