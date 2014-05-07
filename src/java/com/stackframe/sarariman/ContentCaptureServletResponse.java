/*
 * Copyright (C) 2010-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author mcculley
 */
public class ContentCaptureServletResponse extends HttpServletResponseWrapper {

    private String contentType;

    private final ByteArrayOutputStream contentBuffer = new ByteArrayOutputStream();

    private final PrintWriter writer = new PrintWriter(contentBuffer);

    public ContentCaptureServletResponse(HttpServletResponse resp) {
        super(resp);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {

            @Override
            public boolean isReady() {
                // We were born ready.
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                try {
                    // It's always possible to write.
                    writeListener.onWritePossible();
                } catch (IOException ioe) {
                    throw new AssertionError();
                }
            }

            @Override
            public void write(int i) {
                contentBuffer.write(i);
            }

        };
    }

    public String getContent() {
        writer.flush();
        String xhtmlContent = new String(contentBuffer.toByteArray());
        return xhtmlContent;
    }

    public byte[] getContentBytes() {
        writer.flush();
        return contentBuffer.toByteArray();
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

}
