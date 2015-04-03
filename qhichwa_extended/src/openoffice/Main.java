package qhichwa.openoffice;

import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.XDesktop;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.Locale;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XServiceDisplayName;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.linguistic2.LinguServiceEvent;
import com.sun.star.linguistic2.XHyphenatedWord;
import com.sun.star.linguistic2.XHyphenator;
import com.sun.star.linguistic2.XLinguServiceEventBroadcaster;
import com.sun.star.linguistic2.XLinguServiceEventListener;
import com.sun.star.linguistic2.XPossibleHyphens;
import com.sun.star.linguistic2.XSpellAlternatives;
import com.sun.star.linguistic2.XSpellChecker;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.task.XJobExecutor;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import qhichwa.client.ChangeListener;
import qhichwa.client.Client;
import qhichwa.client.Configuration;
import qhichwa.client.Hyphenator;
import qhichwa.client.PossibleHyphens;
import qhichwa.client.SpellAlternatives;

public class Main
        extends WeakBase
        implements XJobExecutor, XServiceDisplayName, XServiceInfo, XSpellChecker, XHyphenator, XLinguServiceEventBroadcaster, ChangeListener {

    private Set<String> disabledRules = new HashSet();
    private List<XLinguServiceEventListener> xEventListeners;
    private static final String[] SERVICE_NAMES = {"com.sun.star.linguistic2.SpellChecker", "com.sun.star.linguistic2.Hyphenator", "qhichwa.openoffice.Main"};
    private XComponentContext xContext;
    private Client qhichwaClient = null;

    public Main(XComponentContext xCompContext) {
        try {
            changeContext(xCompContext);
            this.disabledRules = new HashSet();
            this.xEventListeners = new ArrayList();
            this.qhichwaClient = new Client();
            Configuration.getConfiguration().addChangeListener(this);
        } catch (Throwable t) {
            showError(t);
        }
    }

    public final void changeContext(XComponentContext xCompContext) {
        this.xContext = xCompContext;
    }

    private XComponent getxComponent() {
        try {
            XMultiComponentFactory xMCF = this.xContext.getServiceManager();
            Object desktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", this.xContext);
            XDesktop xDesktop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class, desktop);
            return xDesktop.getCurrentComponent();
        } catch (Throwable t) {
            showError(t);
        }
        return null;
    }

    public boolean isValid(String aWord, Locale aLocale, PropertyValue[] aProperties) {
        return this.qhichwaClient.isValid(aWord);
    }

    public XSpellAlternatives spell(String aWord, Locale aLocale, PropertyValue[] aProperties) {
        XSpellAlternatives rv = null;
        try {
            rv = new SpellAlternatives(aWord, aLocale, this.qhichwaClient);
        } catch (Throwable t) {
            showError(t);
        }
        return rv;
    }

    public XHyphenatedWord hyphenate(String aWord, Locale aLocale, short nMaxLeading, PropertyValue[] aProperties) {
        XHyphenatedWord rv = null;
        try {
            rv = Hyphenator.hyphenate(aWord, aLocale, nMaxLeading);
        } catch (Throwable t) {
            showError(t);
        }
        return rv;
    }

    public XHyphenatedWord queryAlternativeSpelling(String aWord, Locale aLocale, short nIndex, PropertyValue[] aProperties) {
        XHyphenatedWord rv = null;

        return rv;
    }

    public XPossibleHyphens createPossibleHyphens(String aWord, Locale aLocale, PropertyValue[] aProperties) {
        return new PossibleHyphens(aWord, aLocale);
    }

    public boolean hasLocale(Locale locale) {
        return "quh".equals(locale.Language);
    }

    public final Locale[] getLocales() {
        return new Locale[]{new Locale("quh", "BO", "quh_BO")};
    }

    public final boolean isSpellChecker() {
        return true;
    }

    public final boolean hasOptionsDialog() {
        return false;
    }

    public final boolean addLinguServiceEventListener(XLinguServiceEventListener xLinEvLis) {
        if (xLinEvLis == null) {
            return false;
        }
        this.xEventListeners.add(xLinEvLis);
        return true;
    }

    public final boolean removeLinguServiceEventListener(XLinguServiceEventListener xLinEvLis) {
        if (xLinEvLis == null) {
            return false;
        }
        if (this.xEventListeners.contains(xLinEvLis)) {
            this.xEventListeners.remove(xLinEvLis);
            return true;
        }
        return false;
    }

    public final void recheckDocument() {
        if (!this.xEventListeners.isEmpty()) {
            for (XLinguServiceEventListener xEvLis : this.xEventListeners) {
                if (xEvLis != null) {
                    LinguServiceEvent xEvent = new LinguServiceEvent();
                    xEvent.nEvent = 8;
                    xEvLis.processLinguServiceEvent(xEvent);
                }
            }
        }
    }

    public final void resetDocument() {
        this.disabledRules = new HashSet();
        recheckDocument();
    }

    public String[] getSupportedServiceNames() {
        return getServiceNames();
    }

    public static String[] getServiceNames() {
        return SERVICE_NAMES;
    }

    public boolean supportsService(String sServiceName) {
        for (String sName : SERVICE_NAMES) {
            if (sServiceName.equals(sName)) {
                return true;
            }
        }
        return false;
    }

    public String getImplementationName() {
        return Main.class.getName();
    }

    public static XSingleComponentFactory __getComponentFactory(String sImplName) {
        SingletonFactory xFactory = null;
        if (sImplName.equals(Main.class.getName())) {
            xFactory = new SingletonFactory();
        }
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo(XRegistryKey regKey) {
        return Factory.writeRegistryServiceInfo(Main.class.getName(), getServiceNames(), regKey);
    }

    public void trigger(String sEvent) {
        if (!javaVersionOkay()) {
            return;
        }
        try {
            if (sEvent.equals("reset")) {
                resetDocument();
            } else {
                System.err.println("Sorry, don't know what to do, sEvent = " + sEvent);
            }
        } catch (Throwable e) {
            showError(e);
        }
    }

    public void settingsChanged() {
        resetDocument();
    }

    private boolean javaVersionOkay() {
        String version = System.getProperty("java.version");
        if ((version != null) && ((version.startsWith("1.0")) || (version.startsWith("1.1")) || (version.startsWith("1.2")) || (version.startsWith("1.3")) || (version.startsWith("1.4")) || (version.startsWith("1.5")))) {
            DialogThread dt = new DialogThread("Error: Qhichwa Spell Checker requires Java 1.6 or later. Current version: " + version);
            dt.start();
            return false;
        }
        return true;
    }

    public static void showError(Throwable e) {
        e.printStackTrace();
        try {
            String metaInfo = "OS: " + System.getProperty("os.name") + " on " + System.getProperty("os.arch") + ", Java version " + System.getProperty("java.vm.version") + " from " + System.getProperty("java.vm.vendor");
            String msg = "An error has occurred in Qhichwa Spell Checker:\n" + e.toString() + "\nStacktrace:\n";

            StackTraceElement[] elem = e.getStackTrace();
            for (StackTraceElement element : elem) {
                msg = msg + element.toString() + "\n";
            }
            msg = msg + metaInfo;
            DialogThread dt = new DialogThread(msg);
            dt.start();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void showMessage(String msg) {
        DialogThread dt = new DialogThread(msg);
        dt.start();
    }

    public void ignoreRule(String ruleId, Locale locale)
            throws IllegalArgumentException {
        try {
            this.disabledRules.add(ruleId);
            recheckDocument();
        } catch (Throwable t) {
            showError(t);
        }
    }

    public void resetIgnoreRules() {
        try {
            this.disabledRules = new HashSet();
        } catch (Throwable t) {
            showError(t);
        }
    }

    public String getServiceDisplayName(Locale locale) {
        return "Qhichwa Spell Checker";
    }
}
