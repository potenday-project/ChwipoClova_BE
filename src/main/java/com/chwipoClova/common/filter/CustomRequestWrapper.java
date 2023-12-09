package com.chwipoClova.common.filter;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

@Slf4j
public class CustomRequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    public CustomRequestWrapper(HttpServletRequest httpServletRequest) {
        super(httpServletRequest);
        try {
            DataInputStream dis = new DataInputStream(httpServletRequest.getInputStream());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int len = dis.read(buffer); len != -1; len = dis.read(buffer)) {
                os.write(buffer, 0, len);
            }
            os.flush();
            this.body = os.toByteArray();
        } catch (IOException ioe) {
            log.error("IOException {}", ioe);
        }
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedByteArrayInputStream(this.body);
    }

}