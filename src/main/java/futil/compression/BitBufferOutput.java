/*
 * Created by jiahong on 23/01/17.
 * lz78::futil.compression
 */
package futil.compression;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class BitBufferOutput {

    private final static int BYTE = 8;
    private long indexBitLength;
    private final static int MAX_BUFF_DEFAULT = (int)50;
    private int charBitSize;
    private int maxBufferSize;
    private int index;
    private int bufferIndex;
    private DataOutputStream out;
    private short accumulator;
    private byte[] buffer;

    public BitBufferOutput(File outFile, int charBitSize, long indexBitLength, int maxBufferSize)
            throws IOException {
        this.indexBitLength = indexBitLength;
        this.maxBufferSize = maxBufferSize;
        this.buffer =  new byte[maxBufferSize];
        this.index = resetByte();
        this.accumulator = 0;
        this.bufferIndex = 0;
        // save to current directory (i.e. just use filename), omitting .getName() saves it to whichever
        // path you use in the prompt, eg: java ... -c ../../test.txt will cause the output to be in ../../
        this.out = new DataOutputStream(new FileOutputStream(outFile.getName()));
        this.charBitSize = charBitSize;
        writeMeta();
    }

    public BitBufferOutput(File outFile, int charBitSize, long indexBitLength) throws IOException {
        this(outFile, charBitSize, indexBitLength, MAX_BUFF_DEFAULT);
    }

    private void writeMeta() throws IOException {
        out.writeInt(charBitSize);
        out.writeLong(indexBitLength);
    }

    public void forceFlush() throws IOException {
        if (bufferIndex != 0) {
            if (accumulator > 0) {
                buffer[bufferIndex] = (byte)(accumulator);
            }
            out.write(Arrays.copyOf(buffer, bufferIndex+1));
            bufferIndex = 0;
            index = resetByte();
        }
        out.flush();
    }

    public void close() throws IOException {
        forceFlush();
        out.close();
    }

    public void write(Token tk) throws IOException {
        // convert token's character
        for (int i = charBitSize - 1; i >= 0; i--) {
            if (((tk.getChar() >> i) & 1) == 1) {
                accumulator += Math.pow(2, index);
            }
            index--;
            checkIndex();
        }

        // convert index to bytes
        for (long i = indexBitLength-1; i >= 0; i--) {
            if (((tk.getIndex() >> i) & 1) == 1) {
                accumulator += Math.pow(2, index);
            }
            index--;
            checkIndex();
        }
    }

    private int resetByte() { return BYTE - 1; }

    private void checkIndex() throws IOException {
        if (index == -1) {
            buffer[bufferIndex++] = (byte)(accumulator);
            index = resetByte();
            accumulator = 0;
            checkBuffer();
        }
    }


    private void checkBuffer() throws IOException {
        if (bufferIndex == maxBufferSize) {
            bufferIndex = 0;
            out.write(buffer);
        }
    }
}
