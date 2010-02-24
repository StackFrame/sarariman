/*
 * Copyright (C) 2010 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.FSEntityResolver;
import org.xml.sax.InputSource;

/**
 *
 * @author mcculley
 */
public class PDFRendererFilter implements Filter {

    private final DocumentBuilder documentBuilder;

    {
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            documentBuilder.setEntityResolver(FSEntityResolver.instance());
        } catch (ParserConfigurationException pce) {
            throw new RuntimeException(pce);
        }
    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        String outputType = request.getParameter("outputType");

        if (outputType == null) {
            filterChain.doFilter(request, response);
        } else if (outputType.equals("pdf")) {
            // Capture the content for this request
            ContentCaptureServletResponse capContent = new ContentCaptureServletResponse(response);
            filterChain.doFilter(request, capContent);
            // Transform the XHTML content to a document readable by the renderer.
            StringReader contentReader = new StringReader(capContent.getContent());
            InputSource source = new InputSource(contentReader);
            try {
                Document xhtmlContent = documentBuilder.parse(source);
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocument(xhtmlContent, "");
                renderer.layout();
                response.setContentType("application/pdf");
                OutputStream browserStream = response.getOutputStream();
                renderer.createPDF(browserStream);
                return;
            } catch (Exception e) {
                IOException ioe = new IOException();
                ioe.initCause(e);
                throw ioe;
            }
        } else {
            throw new RuntimeException("unknown output type " + outputType);
        }
    }

}
