package futil.compression;

import struct.trie.Trie;

import java.io.*;

public class LZ78 {


    public static void main(String[] args) {
        // Step 1: deal with IO
        File fin = new File(args[0]);
        if (!fin.exists()) {
            System.err.println(fin.getName() + " cannot be found at " + fin.getAbsolutePath());
            System.exit(-1);
        }

        PrintWriter out = new PrintWriter(System.out);
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(fin));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Step 1 complete


        // Step 2: start reading from input stream
        StringBuilder stringBuffer = new StringBuilder();
        Trie<Integer> trie = new Trie<>(Trie.KEY);
        int ch;
        int index = 0;
        int lastIndex = 0;
        try {
            while ((ch = in.read()) != -1) {
                if (ch > 255) continue;
                // start building up the characters read
                stringBuffer.append((char)ch);

                if (!trie.search(stringBuffer.toString())) {
                    // well, we gotta save this into our dictionary first
                    trie.add(stringBuffer.toString(), ++index);

                    // get the last index of the known string, and print it to out-stream,
                    // along with this unknown character(ch)
                    out.println((char)ch + "" + lastIndex);

                    // clear the contents of stringBuffer
                    stringBuffer.setLength(0);

                    // always reset lastIndex to 0
                    lastIndex = 0;
                } else {
                    lastIndex = trie.get(stringBuffer.toString());
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
                    out.println(lastCharacter + "" + trie.get(stringBuffer.toString()));
                }
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            System.err.println(fin.getName() + " contains ascii encoding that is not supported");
            System.exit(-1);
        }
        // Step 2: Complete
    }


}
