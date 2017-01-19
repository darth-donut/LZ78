/*
 * Created by jiahong on 15/01/17.
 * lz78::futil.compression.texts
 */
package futil.compression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;

public class LZ78Hash {

    private static CommandLine parseArgs(String[] args) {
        DefaultParser parser = new DefaultParser();
        Options opts = new Options();
/*        Option comp = new Option("c", "--compress", false, "set to compression mode");
        comp.setRequired(false);*/
        opts.addOption("c", "compress", false, "set to compression mode");
        opts.addOption("x", "decompress", false, "set to decompression mode");
        HelpFormatter help = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(opts, args);
            if (cmd.hasOption("c") && cmd.hasOption("x") ||
                    !cmd.hasOption("c") && !cmd.hasOption("x")) {
                throw new ParseException("Specify at least one argument for compression / decompression");
            }
            if (cmd.getArgs().length != 1)
                throw new ParseException("Input file missing, only accepts one file at a time");
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            help.printHelp("java -jar LZ78x.x.x.jar -c/-x inputFile", opts);
            System.err.println(" inputFile\t   Input file to compress/decompress");
        }
        return cmd;
    }

    public static void main(String[] args) {
        CommandLine cmd = parseArgs(args);
        // we can be sure cmd.getArgs()[0] has a file
        if (cmd.hasOption("c")) {
            File fin = new File(cmd.getArgs()[0]);
            File fout = new File(cmd.getArgs()[0]+ ".lz");
            compress(fin, fout);
        } else if (cmd.hasOption("x")) {
            uncompress(new File(cmd.getArgs()[0]),
                    new File(FilenameUtils.removeExtension(cmd.getArgs()[0])+".txt"));
        }
    }

    public static void uncompress(File fin, File fout) {
        // Step 5: Decode bitcode to ascii again
        File dumpFile = null;
        try {
            BitDecoder bitDecoder = new BitDecoder(fin);
            dumpFile = File.createTempFile("dumpFile", ".txt");
            dumpFile.deleteOnExit();
            bitDecoder.decode(dumpFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Step 5: complete

        // Step 6: Recombine all the tokens

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(fout));
            BufferedReader in = new BufferedReader(new FileReader(dumpFile));

            long index;
            long insertIndex = 0;
            StringBuilder buffer = new StringBuilder();
            int ch;
            HashMap<Long, Token> map = new HashMap<>();

            while ((ch = in.read()) != -1) {
                buffer.append((char)ch);
                index = Long.parseLong(in.readLine());
                map.put(++insertIndex, new Token((char)ch, index));

                while (index > 0) {
                    Token tk = map.get(index);
                    index = tk.getIndex();
                    buffer.append(tk.getChar());
                }
                // assuming the nested while loop takes awhile, using
                // append + 1 call to reverse() should be more efficient
                pw.print(buffer.reverse().toString());
                buffer.setLength(0);
            }
            pw.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Step 6: complete
    }

    public static void compress(File fin, File fout) {

        // Step 1: deal with IO
        File tmp = null;

        try {
            tmp = File.createTempFile("tmp", ".txt");
            tmp.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        if (!fin.exists()) {
            System.err.println(fin.getName() + " cannot be found at " + fin.getAbsolutePath());
            System.exit(-1);
        }

        PrintWriter out = null;

        try {
            out = new PrintWriter(tmp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(fin));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Step 1 complete


        // Step 1.5

        char[] fill = new char[80];
        Arrays.fill(fill, 'x');
        out.println(fill);

        // Step 1.5 complete

        // Step 2: start reading from input stream
        StringBuilder stringBuffer = new StringBuilder();
        HashMap<String, Integer> map = new HashMap<>();
        int maxIndex = 0;
        int ch;
        int index = 0;
        int lastIndex = 0;
        try {
            while ((ch = in.read()) != -1) {
                // start building up the characters read
                stringBuffer.append((char)ch);

                if (!map.containsKey(stringBuffer.toString())) {
                    // well, we gotta save this into our dictionary first
                    map.put(stringBuffer.toString(), ++index);

                    // get the last index of the known string, and print it to out-stream,
                    // along with this unknown character(ch)
                    out.println((char)ch + "" + lastIndex);

                    // clear the contents of stringBuffer
                    stringBuffer.setLength(0);

                    // always reset lastIndex to 0
                    lastIndex = 0;
                } else {
                    lastIndex = map.get(stringBuffer.toString());
                    maxIndex = lastIndex > maxIndex ? lastIndex : maxIndex;
                }
            }

            // stringBuffer might still be waiting for an unknown character to come by
            /* We deal with this by deleting the last character, getting the index of that string,
            * and appending that index after this current 'ch' which we pretend to be unknown */
            if (stringBuffer.length() > 0) {
                char lastCharacter=  stringBuffer.charAt(stringBuffer.length()-1);
                // special note to take care of, if the buffer length is only one, we can
                // immediately print this character and append a 0
                if (stringBuffer.length() == 1) {
                    out.println(lastCharacter + "0");
                    // stick to plan and deal with the waiting character
                } else {
                    stringBuffer.deleteCharAt(stringBuffer.length()-1);
                    lastIndex = map.get(stringBuffer.toString());
                    out.println(lastCharacter + "" + lastIndex);
                    maxIndex = lastIndex > maxIndex ? lastIndex : maxIndex;
                }
            }
            out.close();
            in.close();
            // Step 2: Complete

            // Step 3: update XXX on first line

            RandomAccessFile ra = new RandomAccessFile(tmp, "rw");
            ra.seek(0);
            ra.writeBytes(Integer.toString(maxIndex));
            // Step 3 complete


            // Step 4: translate ascii to bitcode

            BitEncoder bitEncoder = new BitEncoder(tmp);
            bitEncoder.printBytes(fout);

            // Step 4 complete

        } catch (IOException | InvalidHeaderException e) {
            e.printStackTrace();
        }
    }
}
