/*
 * Copyright (c) 2011 Zauber S.A. -- All rights reserved
 */
package com.zaubersoftware.labs.twiki2markdown;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.apache.maven.doxia.module.markdown.MarkdownSink;
import org.apache.maven.doxia.module.twiki.TWikiParser;
import org.apache.maven.doxia.parser.ParseException;

/**
 * Script for migrating TWiki to Markdown.
 * 
 * @author Cristian Pereyra
 * @since Dec 26, 2011
 */
public final class App {

    /**
     * Creates the App.
     */
    private App() {}

    /**
     * @param args
     * @throws IOException
     * @throws ParseException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void main(final String[] args) throws ParseException, IOException {
        FileReader reader = new FileReader(args[0]);
        TWikiParser parser = new TWikiParser();
        StringWriter writer = new StringWriter();
        parser.parse(reader, new MarkdownSink(writer));
        System.out.print(writer.getBuffer().toString());
    }

}
