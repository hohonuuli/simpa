package simpa.map.ui;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Jun 15, 2009
 * Time: 3:34:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class TException extends RuntimeException {
    public TException() {
    }

    public TException(String s) {
        super(s);
    }

    public TException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TException(Throwable throwable) {
        super(throwable);
    }
}
