/*
 * Created by jiahong on 19/01/17.
 * lz78::futil.compression
 */
package futil.compression;

public class Token {

    private char ch;
    private long index;

    public Token(char ch, long index) {
        this.ch = ch;
        this.index = index;
    }


    public long getIndex() {
        return index;
    }

    public char getChar() {
        return ch;
    }
}

