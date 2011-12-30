package com.zaubersoftware.labs.twiki2markdown.test;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Assert;

import org.apache.maven.doxia.module.markdown.MarkdownSink;
import org.apache.maven.doxia.module.twiki.TWikiParser;
import org.apache.maven.doxia.parser.ParseException;

/*
 * Copyright (c) 2011 Zauber S.A. -- All rights reserved
 */

/**
 * Tests compatibility between the Twiki parser and the Markdown Sink
 * @author Cristian Pereyra
 * @since Dec 26, 2011
 */
public class TwikiToMarkdownTest {

    /**
     * Parser to test against
     */
    final TWikiParser parser = new TWikiParser();

    /**
     * Checks the parse as expected
     * @param from String to parse
     * @param expected Result desired
     * @throws ParseException
     */
    public final void assertParse(final String from, final String expected) throws ParseException {
        
        StringReader reader = new StringReader(from);
        StringWriter writer = new StringWriter();
        parser.parse(reader, new MarkdownSink(writer));
        Assert.assertEquals(expected, writer.getBuffer().toString());
    }


}
