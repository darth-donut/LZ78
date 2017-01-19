/*
 * Created by jiahong on 18/01/17.
 * lz78::futil.compression
 */
package futil.compression;

public class InvalidHeaderException extends Exception {

    public InvalidHeaderException(String msg) {
        super(msg);
    }

    public InvalidHeaderException() {
        super();
    }
}
