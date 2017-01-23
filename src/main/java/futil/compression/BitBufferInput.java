/*
 * Created by jiahong on 23/01/17.
 * lz78::futil.compression
 */
package futil.compression;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BitBufferInput {
    private DataInputStream infile;
    private int charBitSize;
    private long indexBitLength;

    public BitBufferInput(File inFile) throws IOException {
        this.infile = new DataInputStream(new FileInputStream(inFile));
        this.charBitSize = infile.readInt();
        this.indexBitLength = infile.readLong();
    }

/*    public Token getToken() {

    }*/


}
