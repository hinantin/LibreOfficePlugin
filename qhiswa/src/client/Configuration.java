package qhichwa.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Configuration {

    protected Properties config;
    protected File file;
    protected Set<String> categories;
    protected Set<String> phrases;
    protected List<WeakReference<ChangeListener>> listeners = new LinkedList();
    protected static Configuration singleton = null;

    public void fireChange() {
        Iterator i = this.listeners.iterator();
        while (i.hasNext()) {
            WeakReference ref = (WeakReference) i.next();
            Object o = ref.get();
            if (o == null) {
                i.remove();
            } else {
                ((ChangeListener) o).settingsChanged();
            }
        }
    }

    public void addChangeListener(ChangeListener l) {
        this.listeners.add(new WeakReference(l));
    }

    public static synchronized Configuration getConfiguration() {
        if (singleton == null) {
            singleton = new Configuration(new File(System.getProperty("user.home"), ".SC-OpenOffice.org"));
            singleton.load();
        }
        return singleton;
    }

    public Configuration(File _file) {
        this.config = new Properties();
        this.file = _file;
    }

    public void load() {
        try {
            this.config.load(new FileInputStream(this.file));
            this.phrases = getIgnoredPhrases();
            this.categories = getCategories();
        } catch (Exception ex) {
            this.phrases = new HashSet();
            this.categories = new HashSet();
        }
    }

    private Set<String> createSet(String[] strings) {
        Set<String> temp = new HashSet();
        for (int x = 0; x < strings.length; x++) {
            temp.add(strings[x]);
        }
        return temp;
    }

    public synchronized boolean isIgnored(String phrase) {
        return this.phrases.contains(phrase);
    }

    public synchronized boolean isEnabled(String category) {
        return this.categories.contains(category);
    }

    public synchronized void ignorePhrase(String phrase) {
        this.phrases.add(phrase);
        this.config.setProperty("ignoredPhrases", createString(this.phrases));
    }

    public synchronized void removePhrase(String phrase) {
        this.phrases.remove(phrase);
        this.config.setProperty("ignoredPhrases", createString(this.phrases));
    }

    public synchronized void showCategory(String category) {
        this.categories.add(category);
        this.config.setProperty("categories", createString(this.categories));
    }

    public synchronized void hideCategory(String category) {
        this.categories.remove(category);
        this.config.setProperty("categories", createString(this.categories));
    }

    private String createString(Set<String> strings) {
        StringBuffer temp = new StringBuffer();
        Iterator<String> i = strings.iterator();
        while (i.hasNext()) {
            String value = (String) i.next();
            temp.append(value);
            if (i.hasNext()) {
                temp.append(", ");
            }
        }
        return temp.toString();
    }

    public synchronized Set<String> getIgnoredPhrases() {
        return createSet(this.config.getProperty("ignoredPhrases", "").split(",\\s+"));
    }

    public synchronized Set<String> getCategories() {
        return createSet(this.config.getProperty("categories", "").split(",\\s+"));
    }

    public synchronized String getServiceHost() {
        return "http://sc.hinantin:80/tools/office/";
    }

    public synchronized void setServiceHost(String name) {
        this.config.setProperty("host", name);
    }

    public synchronized String getLogin() {
        return this.config.getProperty("login", "").trim();
    }

    public synchronized void setLogin(String name) {
        this.config.setProperty("login", name);
    }

    public synchronized String getPassword() {
        return this.config.getProperty("password", "").trim();
    }

    public synchronized void setPassword(String name) {
        this.config.setProperty("password", name);
    }

    public void save() {
        try {
            this.config.store(new FileOutputStream(this.file), "SQUOIA-OpenOffice Properties");
            fireChange();
        } catch (Exception ex) {
            throw new RuntimeException("Could not save properties\nLocation:" + this.file + "\n" + ex.getMessage());
        }
    }
}
