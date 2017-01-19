/*
 * Created by jiahong on 18/01/17.
 * lz78::futil.compression
 */
package futil.compression;

import java.io.*;
import java.util.Arrays;
import java.util.InputMismatchException;

public class BitDecoder {
    private int bytes;
    private File file;
    public BitDecoder(File file) {
        this.file = file;
    }

    public void decode(File ofile) throws IOException {

        DataInputStream datain = new DataInputStream(new FileInputStream(file));
        bytes = datain.readInt();
        System.out.println("Using " + bytes + " bytes to encode large int");
        int readBytes;
        byte[] buffer = new byte[1+bytes];
        PrintWriter pw = new PrintWriter(new FileOutputStream(ofile));

        while ((readBytes = datain.read(buffer)) != -1) {
            if ((1+bytes) != readBytes)
                throw new InputMismatchException("Expected " + (1+bytes) + ", read " + readBytes);
            Byject it = new Byject(buffer[0],
                    Arrays.copyOfRange(buffer, 1, buffer.length));
            pw.println(it.undump());
        }

        pw.close();
        datain.close();
    }
}
