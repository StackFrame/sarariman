/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.vcard;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mcculley
 */
public class xCard4Renderer implements xCardRenderer {

    private Element fullName(Document d, vCardSource s) {
        Element fn = d.createElement("fn");

        Element text = d.createElement("text");
        fn.appendChild(text);
        text.appendChild(d.createTextNode(s.getFullName()));

        return fn;
    }

    private Element name(Document d, vCardSource s) {
        Element n = d.createElement("n");

        Element surname = d.createElement("surname");
        n.appendChild(surname);
        surname.appendChild(d.createTextNode(s.getFamilyName()));

        Element given = d.createElement("given");
        n.appendChild(given);
        given.appendChild(d.createTextNode(s.getGivenName()));

        return n;
    }

    private Element type(Document d, String name) {
        Element type = d.createElement("type");
        Element text = d.createElement("text");
        type.appendChild(text);
        text.appendChild(d.createTextNode(name));
        return type;
    }

    private Element email(Document d, vCardSource s) {
        Element email = d.createElement("email");

        Element parameters = d.createElement("parameters");
        email.appendChild(parameters);

        parameters.appendChild(type(d, "work"));

        Element text = d.createElement("text");
        email.appendChild(text);
        text.appendChild(d.createTextNode(s.getEmailAddress()));

        return email;
    }

    private Element tel(Document d, vCardSource s) {
        Element tel = d.createElement("tel");

        Element parameters = d.createElement("parameters");
        tel.appendChild(parameters);

        parameters.appendChild(type(d, "work"));

        parameters.appendChild(type(d, "cell"));

        Element uri = d.createElement("uri");
        tel.appendChild(uri);
        PhoneNumber mobile = s.getMobile();
        String formattedMobile = PhoneNumberUtil.getInstance().format(mobile, PhoneNumberUtil.PhoneNumberFormat.RFC3966);
        uri.appendChild(d.createTextNode(formattedMobile));

        return tel;
    }

    private Element organization(Document d, vCardSource s) {
        Element org = d.createElement("org");

        Element parameters = d.createElement("parameters");
        org.appendChild(parameters);

        parameters.appendChild(type(d, "work"));

        Element text = d.createElement("text");
        org.appendChild(text);
        text.appendChild(d.createTextNode(s.getOrganization()));

        return org;
    }

    private Element vcard(Document d, vCardSource s) {
        Element vcard = d.createElement("vcard");

        vcard.appendChild(fullName(d, s));

        vcard.appendChild(organization(d, s));

        vcard.appendChild(name(d, s));

        vcard.appendChild(email(d, s));

        vcard.appendChild(tel(d, s));

        return vcard;
    }

    private Element vcards(Document d) {
        return d.createElementNS("urn:ietf:params:xml:ns:vcard-4.0", "vcards");
    }

    private Document document() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();
            d.setXmlStandalone(true);
            return d;
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }

    public Document render(vCardSource s) {
        Document d = document();

        Element vcards = vcards(d);
        d.appendChild(vcards);

        vcards.appendChild(vcard(d, s));

        return d;
    }

    public Document render(Iterable<vCardSource> sources) {
        Document d = document();

        Element vcards = vcards(d);
        d.appendChild(vcards);

        for (vCardSource s : sources) {
            vcards.appendChild(vcard(d, s));
        }

        return d;
    }

}
