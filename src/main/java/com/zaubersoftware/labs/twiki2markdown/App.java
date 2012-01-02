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
package com.zaubersoftware.labs.twiki2markdown;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
    private App() {
    }

    /**
     * @param args
     * @throws IOException
     * @throws ParseException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void main(final String[] args) throws ParseException, IOException {
        System.setOut(new PrintStream(System.out, true, "iso-8859-1"));

        InputStreamReader reader = new InputStreamReader(new FileInputStream(args[0]), "iso-8859-1");

        TWikiParser parser = new TWikiParser();
        StringWriter writer = new StringWriter();
        parser.parse(reader, new FilteredMarkdownSink(writer, args[0]));
        System.out.print(writer.getBuffer().toString());
    }

}
