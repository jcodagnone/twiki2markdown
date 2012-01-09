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

import org.apache.maven.doxia.module.twiki.TWikiParser;
import org.apache.maven.doxia.parser.ParseException;
import org.junit.Test;

import com.zaubersoftware.labs.twiki2markdown.FilteredMarkdownSink;

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
        parser.parse(reader, new FilteredMarkdownSink(writer, "/myfolder/myfilename.txt"));
        Assert.assertEquals(expected, writer.getBuffer().toString());
    }
    
    @Test
    public final void complexListingTest() throws ParseException {
        String text = "De estos 3 parametros:\n" + 
            "   * el momento en que se realizen los cambios puede definirse\n" +
            "   * el tiempo de vencimiento de la cache en cada nodo de CloudFront no es determinable\n" +
            "   * el tiempo de vencimiento de la cache en el browser de cada cliente no es determinable";
        
        assertParse(text, "\nDe estos 3 parametros:\n" +
        		"\n" +
        		"* el momento en que se realizen los cambios puede definirse\n" +
        		"* el tiempo de vencimiento de la cache en cada nodo de [[CloudFront]] no es determinable\n" +
                "* el tiempo de vencimiento de la cache en el browser de cada cliente no es determinable\n\n\n");
    }
    
    @Test
    public final void avoidLinkTest() throws ParseException {
        String text = "There is _!SomeRandomWikiWord_ in the text";
        
        assertParse(text,"\nThere is _SomeRandomWikiWord_ in the text\n");
        
        String text2 = "---+++!SomeRandomWikiWord ";
        
        assertParse(text2,"### SomeRandomWikiWord\n");
    }
    
    @Test
    public final void avoidInvalidLinkFormat() throws ParseException {
        String text2 = "Tag 2.0M2 de [[https://git.zaubersoftware.com/intextual/platform.git][Repo GIT]]";
        
        assertParse(text2,"\nTag 2.0M2 de [Repo GIT](https://git.zaubersoftware.com/intextual/platform.git)\n");
    }
    
    @Test
    public final void avoidBadWikiWordTest() throws ParseException {
        String text = "SVN is not a wiki word";
        
        assertParse(text,"\nSVN is not a wiki word\n");
    }
    
    @Test
    public final void removeHTMLFromLinkTest() throws ParseException {
        String text = "WikiPedia is a wiki word";
        
        assertParse(text,"\n [[WikiPedia]] is a wiki word\n");
    }
    
    
    @Test
    public final void replaceImageLinks() throws ParseException {
        String text = "<img src=\"some/uri.jpg\"/>";
        
        assertParse(text,"\n<img src=\"some/uri.jpg\"/>\n");
    }
    
    @Test
    public final void removeUnpercentWords() throws ParseException {
        String text = "%TOC% %ATTACHURLPATH% %ICON{\"searchtopic\"}%%MAKETEXT{\"Search\"}%";
        
        assertParse(text,"\n images/myfilename\n");
    }
    
    
    @Test
    public final void removeDoubleMark() throws ParseException {
        String text = "---++!! Header";
        
        assertParse(text,"## Header\n");
    }

    

}
