/*
 * Created by jiahong on 15/01/17.
 * lz78::futil.compression
 */
package futil.compression;



import java.io.*;

public class BitEncoder {
    private static final int BYTE = 8;
    private File ofile;
    private File ifile;
    private int maxIndex;
    private int charBitSize;
    private long indexBitCount;

    public BitEncoder(File ifile, File ofile, int encoding, int maxIndex) throws FileNotFoundException {
        if (!ifile.exists())
            throw new FileNotFoundException();
        this.ifile = ifile;
        this.ofile = ofile;
        this.maxIndex = maxIndex;
        this.charBitSize = (int)log2(encoding);
        this.indexBitCount = log2(maxIndex);
    }


    public long dump() throws IOException, InvalidHeaderException {
        System.err.println("Charset encoding: " + this.charBitSize+
                " Largest int " + maxIndex + " require " + indexBitCount +" to encode");
        // setup files
        BufferedReader sc = new BufferedReader(new FileReader(ifile));


        // build token
        int ch;
        StringBuilder buffer = new StringBuilder();

        // BitBuffer setup
        BitBufferOutput bitBufferOutput = new BitBufferOutput(ofile, charBitSize, indexBitCount);

        while ((ch = sc.read()) != -1) {
            // read index
            buffer.append(sc.readLine());
            Token tk = new Token((char)ch, Long.parseLong(buffer.toString()));

            // write to bitBuffer (do NOT flush in a loop)
            bitBufferOutput.write(tk);

            // clear buffer
            buffer.setLength(0);
        }

        bitBufferOutput.close();
        sc.close();

        return indexBitCount;
    }

    private long log2(long x) {
        return (long)(Math.ceil(Math.log(x+1)/Math.log(2)));
    }


}
