/**
 * Copyright (c) 2012 Zauber S.A. <http://www.zaubersoftware.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
