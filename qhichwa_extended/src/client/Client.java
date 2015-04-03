package qhichwa.client;

import com.sun.star.lang.Locale;
import com.sun.star.linguistic2.ProofreadingResult;
import com.sun.star.linguistic2.SingleProofreadingError;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

public class Client {

    protected Process flookup = null;
    protected OutputStream fl_wr = null;
    protected InputStream fl_rd = null;
    protected Process p_tcpServer = null;
    public File analyzeUnificado_bin = null;
    public File chain_bin = null;
    public File spellcheckUnificado_bin = null;
    protected File flookup_bin = null;
    protected File tcpServer = null;
    protected File tcpClient = null;
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
            throws Exception {
        System.out.println("Initializing client");

        Client temp = new Client();
        temp.debug = true;
        Locale lt = new Locale();
        lt.Language = "quh";
        lt.Country = "BO";
        lt.Variant = "UTF-8";

        System.out.println("Checking validity of ñuuqa: " + temp.isValidWord("ñuuqa"));
        String[] alt = temp.getAlternatives("ñuuqa");
        for (int i = 0; i < alt.length; i++) {
            System.out.println("Checking alternatives of ñuuqa: " + alt[i]);
        }
        ProofreadingResult error = temp.proofreadText("PACHANTIN LLAQTAKUNAPA RUNAP ALLIN KANANPAQ HATUN KAMACHIY.", lt, new ProofreadingResult());
        for (int x = 0; x < error.aErrors.length; x++) {
            System.out.println(error.aErrors[x].nErrorStart + ", " + error.aErrors[x].nErrorLength + ", " + error.aErrors[x].nErrorType + ", " + error.aErrors[x].aRuleIdentifier + ", " + error.aErrors[x].aShortComment + ", " + error.aErrors[x].aFullComment);
            for (int j = 0; j < error.aErrors[x].aSuggestions.length; j++) {
                System.out.println("\t" + error.aErrors[x].aSuggestions[j]);
            }
        }
    }

    public Client() {
        System.err.println("os.name\t" + System.getProperty("os.name"));
        System.err.println("os.arch\t" + System.getProperty("os.arch"));
        try {
            URL tcps_url = null;
            URL tcpc_url = null;
            URL url = null;
            this.command = new String[]{"/bin/bash", "-c", ""};
            if (System.getProperty("os.name").startsWith("Windows")) {
                tcps_url = getClass().getResource("../../lib/foma/win32/tcpServer.exe");
                tcpc_url = getClass().getResource("../../lib/foma/win32/tcpClient.exe");
                url = getClass().getResource("../../lib/foma/win32/flookup.exe");
                this.command = new String[]{"CMD", "/C", ""};
            } else if (System.getProperty("os.name").startsWith("Mac")) {
                tcps_url = getClass().getResource("../../lib/foma/mac/tcpServer");
                tcpc_url = getClass().getResource("../../lib/foma/mac/tcpClient");
                url = getClass().getResource("../../lib/foma/mac/flookup");
            } else if (System.getProperty("os.name").startsWith("Linux")) {
                if ((System.getProperty("os.arch").startsWith("x86_64")) || (System.getProperty("os.arch").startsWith("amd64"))) {
                    tcps_url = getClass().getResource("../../lib/foma/linux64/tcpServer");
                    tcpc_url = getClass().getResource("../../lib/foma/linux64/tcpClient");
                    url = getClass().getResource("../../lib/foma/linux64/flookup");
                } else {
                    tcps_url = getClass().getResource("../../lib/foma/linux32/tcpServer");
                    tcpc_url = getClass().getResource("../../lib/foma/linux32/tcpClient");
                    url = getClass().getResource("../../lib/foma/linux32/flookup");
                }
            }

            this.flookup_bin = new File(url.toURI());
            this.tcpServer = new File(tcps_url.toURI());
            this.tcpClient = new File(tcpc_url.toURI());

            if ((!this.flookup_bin.canExecute()) && (!this.flookup_bin.setExecutable(true))) {
                throw new Exception("Foma's flookup is not executable and could not be made executable!\nTried to execute " + this.flookup_bin.getCanonicalPath());
            }

            if ((!this.tcpServer.canExecute()) && (!this.tcpServer.setExecutable(true))) {
                throw new Exception("Foma's tcpServer is not executable and could not be made executable!\nTried to execute " + this.tcpServer.getCanonicalPath());
            }

            if ((!this.tcpClient.canExecute()) && (!this.tcpClient.setExecutable(true))) {
                throw new Exception("Foma's tcpClient is not executable and could not be made executable!\nTried to execute " + this.tcpClient.getCanonicalPath());
            }

            this.foma_file = new File(getClass().getResource("../../lib/foma/analyzeUnificado.bin").toURI());
            this.analyzeUnificado_bin = new File(getClass().getResource("../../lib/foma/analyzeUnificado.bin").toURI());
            this.chain_bin = new File(getClass().getResource("../../lib/foma/chain.bin").toURI());
            this.spellcheckUnificado_bin = new File(getClass().getResource("../../lib/foma/spellcheckUnificado.bin").toURI());
            if (!this.foma_file.canRead()) {
                throw new Exception("qhichwa.fst is not readable!");
            }

            /* ======================================================
             INICIATE / INSTATIATE THE flookup
             ====================================================== */

            ProcessBuilder pb = new ProcessBuilder(new String[]{flookup_bin.getAbsolutePath(), "-b", "-x", foma_file.getAbsolutePath()});
            Map<String, String> env = pb.environment();
            env.put("CYGWIN", "nodosfilewarning");


            this.flookup = pb.start();

            this.fl_wr = this.flookup.getOutputStream();
            this.fl_rd = this.flookup.getInputStream();

            /* ======================================================
             INICIATE / INSTANTIATE THE TCP SERVER PROGRAM
             ====================================================== */

            ProcessBuilder pb_tcps = new ProcessBuilder(new String[]{this.tcpServer.getAbsolutePath(), "-P", "8888", this.analyzeUnificado_bin.getAbsolutePath(), this.chain_bin.getAbsolutePath(), this.spellcheckUnificado_bin.getAbsolutePath()});
            Map<String, String> env_tcps = pb_tcps.environment();
            env_tcps.put("CYGWIN", "nodosfilewarning");

            this.p_tcpServer = pb_tcps.start();

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public synchronized ProofreadingResult proofreadText(String paraText, Locale locale, ProofreadingResult paRes) {
        try {
            paRes.nStartOfSentencePosition = this.position;
            paRes.nStartOfNextSentencePosition = (this.position + paraText.length());
            paRes.nBehindEndOfSentencePosition = paRes.nStartOfNextSentencePosition;

            ArrayList<SingleProofreadingError> errors = new ArrayList();

            this.rx_m.reset(paraText);
            while (this.rx_m.find()) {
                SingleProofreadingError err = processWord(this.rx_m.group(), this.rx_m.start());
                if (err != null) {
                    errors.add(err);
                }
            }
            paRes.aErrors = ((SingleProofreadingError[]) errors.toArray(paRes.aErrors));
        } catch (Throwable t) {
            showError(t);
            paRes.nBehindEndOfSentencePosition = paraText.length();
        }
        return paRes;
    }

    public synchronized boolean isValid(String word) {
        if ((this.p_tcpServer == null) || (this.flookup == null) || (this.fl_wr == null) || (this.fl_rd == null)) {
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
        if (this.rx_pe_m.matches()) {
            if (isValidWord(this.rx_pe_m.group(1))) {
                return true;
            }
            if (isValidWord(this.rx_pe_m.group(1).toLowerCase())) {
                return true;
            }
        }
        this.rx_pb_m.reset(word);
        if (this.rx_pb_m.matches()) {
            if (isValidWord(this.rx_pb_m.group(1))) {
                return true;
            }
            if (isValidWord(this.rx_pb_m.group(1).toLowerCase())) {
                return true;
            }
        }
        this.rx_pbe_m.reset(word);
        if (this.rx_pbe_m.matches()) {
            if (isValidWord(this.rx_pbe_m.group(1))) {
                return true;
            }
            if (isValidWord(this.rx_pbe_m.group(1).toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public synchronized String[] getAlternatives(String word) {
        String[] rv = new String[0];
        try {
            if ((this.p_tcpServer == null) || (this.tcpClient == null) || (this.tcpServer == null) || (this.foma_file == null)) {
                return rv;
            }
            rv = alternatives(word);
        } catch (Exception ex) {
            showError(ex);
            return rv;
        }
        return rv;
    }
    // Getting the alternatives from the JSON like format output stream

    public String[] alternatives(String word) {
        String[] rv = new String[0];
        word = word.trim().toLowerCase();
        try {
            String ret = "";
            ProcessBuilder pb = new ProcessBuilder(new String[]{this.tcpClient.getAbsolutePath(), "-P", "8888"});
            pb.redirectErrorStream(true);
            Map<String, String> env = pb.environment();
            env.put("CYGWIN", "nodosfilewarning");
            Process process = null;
            try {
                process = pb.start();
            } catch (IOException e) {
                System.out.println("Couldn't start the process.");
                e.printStackTrace();
            }

            try {
                if (process != null) {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), "UTF-8"));
                    bw.write(word);
                    bw.newLine();
                    bw.close();
                }
            } catch (IOException e) {
                System.out.println("Either couldn't read from the template file or couldn't write to the OutputStream.");
                e.printStackTrace();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String currLine = null;
            try {
                while ((currLine = br.readLine()) != null) {
                    System.out.println("Result:\t" + currLine);
                    String[] line = currLine.split("[:|]");
                    ret = line[1];
                    System.out.println(ret);
                }
            } catch (IOException e) {
                System.out.println("Couldn't read the output.");
                e.printStackTrace();
            }
            String delimiter = ",";
            if (ret.contains(delimiter)) {
                rv = ret.split(delimiter);
            } else {
                rv = new String[1];
                rv[0] = ret;
            }
        } catch (Exception ex) {
            showError(ex);
            return rv;
        }
        return rv;
    }

    protected SingleProofreadingError processWord(String word, int start) {
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

    public boolean isValidWord(String word) {
        word = word + "\n";
        byte[] res = new byte[4];
        try {
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
            } else {
                //String s = new String(res);
                //System.out.println("RES: " + s + "\n");
            }
        } catch (Exception ex) {
            showError(ex);
            return false;
        }
        return (res[0] != 43) || (res[1] != 63) || (res[2] != 10);
    }

    static void showError(Throwable e) {
        Main.showError(e);
    }

    public static String makeHash(byte[] convertme) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (Throwable t) {
        }
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Throwable t) {
        }
        return byteArray2Hex(md.digest(convertme));
    }

    private static String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", new Object[]{Byte.valueOf(b)});
        }
        return formatter.toString();
    }
}
