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
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.FSEntityResolver;
import org.xml.sax.InputSource;

/**
 * A Filter which converts the filtered document to PDF.  It looks for a special request parameter, outputType.  If outputType is
 * "pdf" then the filtered document is rendered using Flying Saucer and iText.  If outputType is not set then the document is passed
 * through unaltered.
 *
 * @author mcculley
 */
public class PDFRendererFilter implements Filter {

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
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                documentBuilder.setEntityResolver(FSEntityResolver.instance());
                Document xhtmlContent = documentBuilder.parse(source);
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocument(xhtmlContent, "");
                renderer.layout();
                response.setContentType("application/pdf");

                String preferredFilename = request.getParameter("preferredFilename");
                if (preferredFilename != null) {
                    response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"",preferredFilename));
                }

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
