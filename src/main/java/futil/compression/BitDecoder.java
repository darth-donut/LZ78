/*
 * Created by jiahong on 18/01/17.
 * lz78::futil.compression
 */
package futil.compression;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class BitDecoder {
    private File file;
    public BitDecoder(File file) {
        this.file = file;
    }


    public void decode(File ofile) throws IOException {
        BitBufferInput bitIn = new BitBufferInput(file);
        PrintWriter pw = new PrintWriter(ofile);
        while (true) {
            try {
                Token tk = bitIn.getToken();
                pw.println(tk.getChar() + "" + tk.getIndex());
            } catch (EOFException e) {
                break;
            }
        }
        pw.close();
    }
}
