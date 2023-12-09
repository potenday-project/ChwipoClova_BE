package com.chwipoClova.common.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;

public class CachedByteArrayInputStream extends ServletInputStream {
    private ByteArrayInputStream in;

    public CachedByteArrayInputStream(byte[] body) {
        this.in = new ByteArrayInputStream(body);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
    }

    @Override
    public int read() {
        return in.read();
    }
}