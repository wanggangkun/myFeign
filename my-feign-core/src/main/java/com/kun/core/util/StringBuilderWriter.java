package com.kun.core.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Objects;

/**
 * @author kun
 * @data 2022/1/15 21:21
 */
public class StringBuilderWriter extends Writer implements Serializable {

    private final StringBuilder builder;

    public StringBuilderWriter() {
        this.builder = new StringBuilder();
    }

    @Override
    public Writer append(char c) {
        builder.append(c);
        return this;
    }

    @Override
    public Writer append(CharSequence csq) {
        builder.append(csq);
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        builder.append(csq, start, end);
        return this;
    }

    @Override
    public void write(int c) {
        if (Objects.nonNull(c)) {
            builder.append(c);
        }
    }

    @Override
    public void write(@NotNull char[] cbuf, int off, int len) {
        builder.append(cbuf, off, len);
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
