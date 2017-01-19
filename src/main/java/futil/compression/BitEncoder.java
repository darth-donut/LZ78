/*
 * Created by jiahong on 15/01/17.
 * lz78::futil.compression
 */
package futil.compression;



import java.io.*;
import java.util.Scanner;

public class BitEncoder {
    private File file;

    public BitEncoder(File file) throws FileNotFoundException {
        if (!file.exists())
            throw new FileNotFoundException();
        this.file = file;
    }


    public long printBytes(File ofile) throws IOException, InvalidHeaderException {
        long largestInt;

        // setup files
        BufferedReader sc = new BufferedReader(new FileReader(file));
        String firstLine = sc.readLine();
        DataOutputStream dataout = new DataOutputStream(new FileOutputStream(ofile));

        int bigIntLength = firstLine.indexOf('x');
        if (bigIntLength == -1)
            throw new InvalidHeaderException("Expected at least one 'x' at header for LZ78 output");
        largestInt = Long.parseLong(firstLine.substring(0, bigIntLength));

        long bits = log2(largestInt);
        System.out.println("Largest int " + largestInt + " require " + bits +" to encode");

        StringBuilder buffer = new StringBuilder();
        int ch;
        boolean writemeta = false;

        while ((ch = sc.read()) != -1) {
            // read character
            buffer.append((char)ch);
            // read index
            buffer.append(sc.readLine());

            Byject it = new Byject(buffer.toString(), bits);

            if (!writemeta) {
                dataout.writeInt(it.metadata());
                writemeta = true;
            }
            dataout.write(it.dump());

            // clear buffer
            buffer.setLength(0);
        }

        dataout.close();
        sc.close();

        return largestInt;
    }

    private long log2(long x) {
        return (long)Math.ceil(Math.log(x)/Math.log(2));
    }


}
