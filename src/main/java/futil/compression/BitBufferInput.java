/*
 * Created by jiahong on 23/01/17.
 * lz78::futil.compression
 */
package futil.compression;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BitBufferInput {
    // Hold at least X tokens at once time
    private static final int BUFFER_SIZE = 50;
    private int bufferSize;
    private static final int BYTE = 8;
    private DataInputStream infile;
    private int charBitSize;
    private long indexBitLength;
    private long tokens;
    private int bufferIndex;
    private int next;
    private byte[] buffer;
    private boolean done;
    private int lastByte;

    public BitBufferInput(File inFile) throws IOException {
        this.infile = new DataInputStream(new FileInputStream(inFile));
        this.charBitSize = infile.readInt();
        this.indexBitLength = infile.readLong();
        this.tokens = infile.readLong();
        this.bufferSize = (int)Math.ceil(1.0*(charBitSize+indexBitLength)/8) * BUFFER_SIZE;
        this.bufferIndex = 0;
        this.buffer = new byte[bufferSize];
        this.next = 0;
        this.lastByte = this.next;
        this.done = false;
        System.err.println("Charset encoding: " + this.charBitSize+
                " require " + indexBitLength +" to encode");
    }

    /**
     * For each bit needed by char and index, we read from the buffer (we know which element from the buffer to choose
     * from by using bufferIndex variable (which increases by 1 each time we read > 8 bits) and accumulate the
     * respective char/index. We consciously keep track of next (which tells us which bit we're looking at now, since
     * we've thrown away all concept of byte as a smallest unit here) to shift the bits correctly.
     * We do this by modding next with BYTE, since we're only ever increasing next by one each time we read one BIT.
     * modding next allows us to wrap around 8 and start from 0 again. Doing this also allows us to check whenever a
     * wrap happens and we need to increase bufferIndex by one (i.e. we need to look at the next byte element) because
     * we've just read 8 bits already.
     *
     * Further, to keep track of when to stop, we need to check if:
     *  1) the done boolean is set to true, i.e. the data stream has nothing to offer anymore
     *                  AND
     *  2) we don't have sufficient bits to write another token (which implies that the remaining bits are
     *  trailing 0s from the encoder)
     * @return
     * @throws IOException
     */
    public Token getToken() throws IOException {
        // if we're currently looking at the last buffer
        if (shouldStop()) throw new EOFException();

        checkBuffer();
        long index = 0;
        char ch = 0;

        // we need to at least run for charBitSize to get the char back
        for (int i = 0; i < charBitSize; next++, i++) {
            // if next isn't 0 and we've wrapped 8, we need to increment bufferIndex by 1 (we've read 8 bits)
            if (next != 0 && next % BYTE == 0) {
                bufferIndex++;
                checkBuffer();
            }
            // as always, we are reading from the left, hence we have to do 7-next%byte to get the right bit,
            // similarly, charBitSize-1-i
            if (((buffer[bufferIndex] >> (7-next%BYTE)) & 1) == 1) {
                ch += (char)Math.pow(2, (charBitSize-1)-i);
            }
        }

        for (int i = 0; i < indexBitLength; next++, i++) {
            if (next != 0 && next % BYTE == 0) {
                bufferIndex++;
                checkBuffer();
            }
            if (((buffer[bufferIndex] >> (7-next%BYTE)) & 1) == 1) {
                index += (long)Math.pow(2, (indexBitLength-1)-i);
            }
        }
        // we can cross one token count down
        tokens--;
        return new Token(ch, index);
    }

    private boolean shouldStop() {
        return tokens == 0;
    }


    /**
     * We have to fill the buffer when we have reached the end of the buffer (and need to buffer more bytes)
     * @throws IOException
     */
    private void checkBuffer() throws IOException {
        if (bufferIndex == lastByte) {
            fillBuffer();
        }
    }

    /**
     * We keep track of lastByte, which signifies the last possible byte to write from the current buffer.
     * Firstly, reset lastByte to 0, and while lastByte is < bufferSize , we can keep storing and increasing the current
     * buffer size. If we happen to get an EOFException, this implies that we've reached the end of our data stream, and
     * set the boolean done to true, so we can stop calling fillBuffer later.
     * Finally, set bufferIndex to 0 so we can start reading from the beginning of the buffer
      * @throws IOException
     */
    private void fillBuffer() throws IOException {
        bufferIndex = 0;
        lastByte = 0;
        while (lastByte < bufferSize) {
            try {
                buffer[lastByte++] = infile.readByte();
            } catch (EOFException e) {
                infile.close();
                done = true;
                break;
            }
        }
    }

    private int byte2bit(int n) { return BYTE*n; }
}

