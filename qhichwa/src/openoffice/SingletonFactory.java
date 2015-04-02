package qhichwa.openoffice;

import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.XComponentContext;

public class SingletonFactory
        implements XSingleComponentFactory {

    private transient Main instance;

    public final Object createInstanceWithArgumentsAndContext(Object[] arguments, XComponentContext xContext)
            throws Exception {
        return createInstanceWithContext(xContext);
    }

    public final Object createInstanceWithContext(XComponentContext xContext)
            throws Exception {
        if (this.instance == null) {
            this.instance = new Main(xContext);
        } else {
            this.instance.changeContext(xContext);
        }
        return this.instance;
    }
}
