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
    private long tokens;
    private int index;
    private int bufferIndex;
    private DataOutputStream out;
    private short accumulator;
    private byte[] buffer;

    public BitBufferOutput(File outFile, int charBitSize, long indexBitLength, long tokens, int maxBufferSize)
            throws IOException {
        this.indexBitLength = indexBitLength;
        this.maxBufferSize = maxBufferSize;
        this.buffer =  new byte[maxBufferSize];
        this.index = resetByte();
        this.tokens = tokens;
        this.accumulator = 0;
        this.bufferIndex = 0;
        // save to current directory (i.e. just use filename), omitting .getName() saves it to whichever
        // path you use in the prompt, eg: java ... -c ../../test.txt will cause the output to be in ../../
        this.out = new DataOutputStream(new FileOutputStream(outFile.getName()));
        this.charBitSize = charBitSize;
        writeMeta();
    }

    public BitBufferOutput(File outFile, int charBitSize, long indexBitLength, long tokens) throws IOException {
        this(outFile, charBitSize, indexBitLength, tokens, MAX_BUFF_DEFAULT);
    }

    /**
     * Record how many bits we are encoding our data in, so our decoder knows how to decode them
     * @throws IOException
     */
    private void writeMeta() throws IOException {
        out.writeInt(charBitSize);
        out.writeLong(indexBitLength);
        out.writeLong(tokens);
    }


    public void close() throws IOException {
        forceFlush();
        out.close();
    }

    /**
     * From left to right, we deduce the bits of tk's char and index by shifting the bits.
     * We then accumulate those bits in accumulator(short instead of byte because byte is actually
     * signed and only takes -128 ... 127 instead of 255).
     * The variable index keeps track of our current position in the byte element in the byte array,
     * because we are never just only writing a perfect 8 bit, hence, we need to keep track of the last
     * "wrote" position.
     * The shifting starts from the left because we will eventually meet a char or index that doesn't
     * fit perfectly in a byte. Doing so allows us to use index(the instance variable here) to keep track of
     * counting(powering 2)
     * Further, by counting from the left, we do not have to concern ourselves with trailing 0s when the buffer
     * ends before we fill up the last byte element in the byte array(they are already automatically 0)
     * @param tk Token to be written into data stream
     * @throws IOException
     */
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

    /**
     * When index reaches -1, it means we have accumulated enough bits to store in one byte
     * within our byte[] array buffer. Increases the buffer index by 1 and reset the index
     * to resume accumulating bits
     * @throws IOException
     */
    private void checkIndex() throws IOException {
        if (index == -1) {
            buffer[bufferIndex++] = (byte)(accumulator);
            index = resetByte();
            accumulator = 0;
            checkBuffer();
        }
    }


    /**
     * When bufferIndex == maxBufferSize, our array is full, and we should write the contents
     * to our data stream now. We reset the bufferIndex to 0 so we can override the elements in the
     * byte array (can optionally empty array, but redundant)
     * @throws IOException
     */
    private void checkBuffer() throws IOException {
        if (bufferIndex == maxBufferSize) {
            bufferIndex = 0;
            out.write(buffer);
        }
    }

    private void forceFlush() throws IOException {
        buffer[bufferIndex] = (byte)(accumulator);
        out.write(Arrays.copyOf(buffer, bufferIndex+1));
        bufferIndex = 0;
        index = resetByte();
        out.flush();
    }
}
