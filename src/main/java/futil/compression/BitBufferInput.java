/*
 * Created by jiahong on 23/01/17.
 * lz78::futil.compression
 */
package futil.compression;

import java.io.*;

public class BitBufferInput {
    private static final int BUFFER_SIZE = (int) 50;
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
        System.out.println("Char bits used " + charBitSize);
        System.out.println("index bits used " + indexBitLength);
        this.bufferIndex = 0;
        this.buffer = new byte[BUFFER_SIZE];
        this.next = 0;
        this.lastByte = this.next;
        this.done = false;
    }

    public Token getToken() throws IOException {
        if (done && bufferIndex == lastByte-1
                || done && (charBitSize+indexBitLength) > (lastByte-bufferIndex)*8)
            throw new EOFException();
        checkBuffer();
        long index = 0;
        char ch = 0;


        for (int i = 0; i < charBitSize; next++, i++) {
            if (next != 0 && next % 8 == 0) {
                bufferIndex++;
                checkBuffer();
            }
            if (((buffer[bufferIndex] >> (7-next%8)) & 1) == 1) {
                ch += (char)Math.pow(2, (charBitSize-1)-i);
            }
        }

        for (int i = 0; i < indexBitLength; next++, i++) {
            if (next != 0 && next % 8 == 0) {
                bufferIndex++;
                checkBuffer();
            }
            if (((buffer[bufferIndex] >> (7-next%8)) & 1) == 1) {
                index += (long)Math.pow(2, (indexBitLength-1)-i);
            }
        }
        return new Token(ch, index);
    }

    private void checkBuffer() throws IOException {
        // we fill the buffer when:
        //          1) we don't have enough bits to write a token
        //          2) we have a full buffer
        if (lastByte * 8 < charBitSize + indexBitLength ||
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

