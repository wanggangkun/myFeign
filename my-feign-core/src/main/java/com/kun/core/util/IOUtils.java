package com.kun.core.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author kun
 * @data 2022/1/15 21:15
 */
public class IOUtils {

    public static final int EOF = -1;

    public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static String toString(final Reader input) throws IOException {
        try (final StringBuilderWriter sw = new StringBuilderWriter()) {
            copy(input, sw);
            return sw.toString();
        }
    }

    private static int copy(final Reader input, final Writer output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    private static long copyLarge(final Reader input, final Writer output) throws IOException {
        return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }

    private static long copyLarge(final Reader input, final Writer output, final char[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
