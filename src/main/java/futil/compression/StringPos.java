/*
 * Created by jiahong on 14/01/17.
 * lz78::futil.compression
 */
package futil.compression;

import struct.trie.StringPackage;

class StringPos implements StringPackage {
    private String string;
    private int index;

    StringPos(String str, int index) {
        this.string = str;
        this.index = index;
    }

    StringPos() {
        this(null, 0);
    }

    @Override
    public String getString() {
        return string;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() != getClass()) return false;
        return ((StringPos)other).string.equals(string);
    }

}
