/*
 * Created by jiahong on 18/01/17.
 * lz78::futil.compression
 */
package futil.compression;


/**
 * A byject object is an object in a constant transient state
 * between bytes and objects
 * Taking in either a string and long pair, where the string argument
 * represents the raw characters (eg: a10) and the long argument is the
 * minimum number of bits needed to encode (10 in the previous example) the paring index;
 * while the next constructor takes in a byte (character) and array of bytes(the index)
 *
 * <h2>Example </h2>
 *
 * a1
 * b2
 * c0
 * 22
 * b0
 *
 * Needs only 2 bits [0-2] to represent all the possible index, and if we assume UTF-8 characters,
 * then we only need 1 byte to represent each a,b,c,2,b.
 */
public class Byject {

    public static final int UTF8_BIT_SIZE = 8;
    public static final int BYTE = 8;
    private char ch;
    private long index;
    private long bits;

    public Byject(String buffer, long bits) {
        ch = buffer.charAt(0);
        index = Long.parseLong(buffer.substring(1));
        this.bits = bits;
    }

    public Byject(byte ch, byte[] index) {
        this.ch = revert(ch);
        this.bits = index.length;
        this.index = revert(index);
    }


    public byte[] dump() {
        // bytes = number of bytes needed to encode the index
        int bytes = bit2byte(bits);
        // 1(for char) + bytes(for index)
        byte[] out = new byte[1+bytes];
        // converts char into a byte
        out[0] = convert();

        // begin filling in out from the end with the rightmost bits
        for (int i = 1; i < bytes+1; i++)
            // begin appending from rightmost bit
            out[bytes-i+1] = convert((i-1)*BYTE);
        return out;
    }

    public String undump() {
        return Character.toString(ch)+Long.toString(index);
    }

    /**
     * Whenever we dump a byject from one text file, we need to always record
     * the number of bits/bytes that this text file uses for its indices (we use bytes here
     * for simpler array indexing later on)
     * @return int (number of bytes this text file uses for its indices)
     */
    public int metadata() {
        return bit2byte(bits);
    }


    /**
     * Converts from long::index to byte
     * EG:
     * <p>
     * byte[] converted;
     * for (int i = N; i < M; i++)
     *      converted[M-i+1] = convert( ( i ) * 8);
     *
     * for each byte in our resulting bytes(converted::byte[]), the starting index
     * is basically our loop index * BYTE size (i.e. 8)
     *
     * However, because we are storing them in individual byte within a byte array, when
     * powering 2 to the power of the index(start), we must consider offsetting it to the individual
     * byte in the byte array (see below - SEE 1)
     *
     * To visualize it: imagine number in binary      [11001101][01011010][10110101]
     *                                                    z         y         x
     * Every time convert is called, we return x, y, z (in that order) Hence it is important to call
     * convert backwards converted[2] = x, converted[1] = y, converted[0] = z
     *
     * In the above example, converted[2] = x = convert(0) (because x starts from index 0)
     *                       converted[1] = y = convert(1) (an so on ...)
     *
     * Although 110011010101101010110101 is a contiguous number, we're breaking it down into 3 different byte,
     * and each byte, take y for example, 010110101, should be calculated separately (this is the inverse behaviour of
     * revert(byte[] index) below)
     * </p>
     *
     * @param start starting index of byte in a byte array
     * @return byte for an array starting at index start::int
     */
    private byte convert(int start) {
        long sum = 0;
        for (int i = start; i < start + BYTE; i++)
            if (((index >> i) & 1) == 1)
                // instead of Math.pow(2, i), we need to offset it because the returned value(sum)
                // is assigned to a single byte within a byte array (SEE 1)
                sum += Math.pow(2, i-start);
        return (byte)sum;
    }


    private char revert(byte ch) {
        int sum = 0;
        for (int i = 0; i < UTF8_BIT_SIZE; i++)
            if (((ch >> i) & 1) == 1)
                sum += Math.pow(2, i);
        return (char)sum;
    }

    /**
     * Revert an array of bytes back into a single index:long
     *
     * <h3>Method:</h3>
     * starting from the right most cell of the array, start accumulating (converting back to decimal)
     * if index = [101010101][10100010][10100101] = some number N in decimal
     *                 x        y          z
     *    then starting with z, convert that single byte into a number, accumulating along until we reach x
     *    paying close attention to the 2**(index) where index is multiplied by BYTE because
     *    xyz(10101010110100001010100101) is a contiguous number broken into 3 different bytes
     *
     * @param index array of bytes to be converted back into index:long
     * @return long :: index
     */
    private long revert(byte[] index) {
        long sum = 0;
        for (int i = 0; i < index.length; i++) {
            for (int j = 0; j < BYTE; j++) {
                if (((index[index.length-1-i] >> j) & 1) == 1)
                    sum += Math.pow(2, i*BYTE+j);
            }
        }
        return sum;
    }

    private int bit2byte(long bits) {
        return (int)Math.ceil(bits*1.0/8);
    }


    private byte convert() {
        // we assume ch is within UTF8range,
        int sum = 0;
        for (int i = 0; i < UTF8_BIT_SIZE; i++) {
            if (((ch >> i) & 1) == 1)
                sum += Math.pow(2, i);
        }
        return (byte)sum;
    }
}
