/*
 * Created by jiahong on 23/01/17.
 * lz78::futil.compression
 */
package futil.compression;

import java.io.*;

public class BitBufferInput {
    private static final int BUFFER_SIZE = (int) 50;
    private static final int BYTE = 8;
    private DataInputStream infile;
    private int charBitSize;
    private long indexBitLength;
    private int bufferIndex;
    private int next;
    private byte[] buffer;
    private boolean done;
    private int lastByte;

    public BitBufferInput(File inFile) throws IOException {
        this.infile = new DataInputStream(new FileInputStream(inFile));
        this.charBitSize = infile.readInt();
        this.indexBitLength = infile.readLong();
        this.bufferIndex = 0;
        this.buffer = new byte[BUFFER_SIZE];
        this.next = 0;
        this.lastByte = this.next;
        this.done = false;
        System.err.println("Charset encoding: " + this.charBitSize+
                " require " + indexBitLength +" to encode");
    }

    public Token getToken() throws IOException {
        // if we're currently looking at the last buffer
        if (shouldStop()) throw new EOFException();

        checkBuffer();
        long index = 0;
        char ch = 0;


        for (int i = 0; i < charBitSize; next++, i++) {
            if (next != 0 && next % BYTE == 0) {
                bufferIndex++;
                checkBuffer();
                if (shouldStop()) throw new EOFException();
            }
            if (((buffer[bufferIndex] >> (7-next%BYTE)) & 1) == 1) {
                ch += (char)Math.pow(2, (charBitSize-1)-i);
            }
        }

        for (int i = 0; i < indexBitLength; next++, i++) {
            if (next != 0 && next % BYTE == 0) {
                bufferIndex++;
                checkBuffer();
                if (shouldStop()) throw new EOFException();
            }
            if (((buffer[bufferIndex] >> (7-next%BYTE)) & 1) == 1) {
                index += (long)Math.pow(2, (indexBitLength-1)-i);
            }
        }
        return new Token(ch, index);
    }

    private boolean shouldStop() {
        // we should stop when:
        //    1) we're done (i.e. finish reading file) AND
        //    2) we don't have enough bits to write another token (which implies we're done;
        //          unless something bad happened in the encoding stage)
        return done && (indexBitLength+charBitSize) > byte2bit(lastByte-bufferIndex);
    }

    private int byte2bit(int n) { return BYTE*n; }

    private void checkBuffer() throws IOException {
        // we fill the buffer when:
        //          1) we don't have enough bits to write a token
        //          2) we have a full buffer
        if (byte2bit(lastByte)  < charBitSize + indexBitLength ||
                bufferIndex == lastByte) {
            fillBuffer();
        }

    }



    private void fillBuffer() throws IOException {
        bufferIndex = 0;
        lastByte = 0;
        while (lastByte < BUFFER_SIZE) {
            try {
                buffer[lastByte++] = infile.readByte();
            } catch (EOFException e) {
                infile.close();
                done = true;
                break;
            }
        }
    }
}

