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

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.doxia.module.markdown.MarkdownSink;

/*
 * Copyright (c) 2012 Zauber S.A. -- All rights reserved
 */

/**
 * TODO: Description of the class, Comments in english by default
 * 
 * 
 * @author Cristian Pereyra
 * @since Jan 2, 2012
 */
public class FilteredMarkdownSink extends MarkdownSink {

    private boolean makeHtmlLinks = false;
    private String auxString;
    private boolean wikiWord = false;
    private boolean inItalic;
    private boolean inBold;
    private boolean inLink;
    private boolean inMonospaced;
    private String filename;

    /**
     * Creates the MarkdownSinkWrapper.
     * 
     * @param writer
     */
    public FilteredMarkdownSink(Writer writer, String filename) {
        super(writer);
        String[] splitted = filename.split("/");
        
        int lastIndex = splitted[splitted.length - 1].lastIndexOf(".");
        this.filename = splitted[splitted.length - 1].substring(0,(lastIndex == -1) ? splitted[splitted.length - 1].length() : lastIndex);
    }

    private String filteredText(String text) {

        if (!inVerbatim) {
            if (text.matches("^%META.*$") || text.matches("%TOC%.*")) {
                text = text.replaceAll("(^%META.*$|%TOC%)", "");
            }
            if (text.indexOf("%ATTACHURLPATH%") != -1) {
                text = text.replace("%ATTACHURLPATH%", "images/" + filename);
            }
            if (text.indexOf(".html") != -1) {
                text = text.replaceAll("\\.html", "");
            }
            Pattern pat = Pattern.compile("%.+%.*");

            if (pat.matcher(text).find()) {
                text = text.replaceAll("%.+%", "");
            }
            if (text.matches("Main\\..*")) {
                text = text.replaceAll("Main\\.", "");
            }
            if (text.indexOf("!! ") != -1) {
                text = text.replaceAll("!! ", "");
            }

            String[] words = text.split(" ");

            for (int i = 0; i < words.length; i++) {
                if (words[i].startsWith("!")) {
                    words[i] = words[i].substring(1);
                }
            }
            text = StringUtils.join(words, " ");
        }

        return text;
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#text(java.lang.String,
     *      org.apache.maven.doxia.sink.SinkEventAttributes)
     */
    @Override
    public void text(String text) {

        if (wikiWord) {
            return;
        }

        if (inVerbatim) {
            outputWrite(text);
        } else {
            text = filteredText(text);
            super.text(text);
        }

    }

    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#rawText(java.lang.String) */
    @Override
    public void rawText(String text) {
        text = filteredText(text);

        super.rawText(text);
    }

    private void putSafeWhiteSpace() {

        if (!inItalic && !inBold && !inLink && !inMonospaced) {
            outputWrite(" ");
        }

    }

    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#link(java.lang.String) */
    @Override
    public void link(String url) {

        url = filteredText(url);

        putSafeWhiteSpace();
        inLink = true;
        if (url.indexOf("./") != -1) {
            outputWrite("[[" + url.replaceAll("\\./", "").trim() + "]]");
            wikiWord = true;
        } else {
            if (makeHtmlLinks) {
                outputWrite("&nbsp;<a href=\"" + url + "\">");
            } else {
                super.link(url);
            }
        }

    }

    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#italic() */
    @Override
    public void italic() {
        putSafeWhiteSpace();
        inItalic = true;
        super.italic();
    }

    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#bold() */
    @Override
    public void bold() {
        putSafeWhiteSpace();
        inBold = true;
        super.bold();
    }
    
    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#italic_() */
    @Override
    public void italic_() {
        inItalic = false;
        super.italic_();
    }
    
    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#bold_() */
    @Override
    public void bold_() {
        inBold = false;
        super.bold_();
    }

    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#link_() */
    @Override
    public void link_() {

        inLink = false;
        
        if (wikiWord) {
            wikiWord = false;
            return;
        }

        if (makeHtmlLinks) {
            outputWrite("</a>");
        } else {
            super.link_();
        }
    }

    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#monospaced() */
    @Override
    public void monospaced() {
        makeHtmlLinks = true;
        inMonospaced = true;
        putSafeWhiteSpace();
        super.monospaced();
    }

    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#monospaced_() */
    @Override
    public void monospaced_() {
        inMonospaced = false;
        super.monospaced_();
    }

    /** @see org.apache.maven.doxia.sink.SinkAdapter#figure() */
    @Override
    public void figure() {
        outputWrite("<img src=\"");
    }

    /** @see org.apache.maven.doxia.sink.SinkAdapter#figure_() */
    @Override
    public void figure_() {
        outputWrite(auxString.trim() + "\"/>");
    }

    /** @see org.apache.maven.doxia.sink.SinkAdapter#figureCaption() */
    @Override
    public void figureCaption() {

    }

    /** @see org.apache.maven.doxia.sink.SinkAdapter#figureCaption_() */
    @Override
    public void figureCaption_() {

    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#figureGraphics(java.lang.String,
     *      org.apache.maven.doxia.sink.SinkEventAttributes)
     */
    @Override
    public void figureGraphics(String name) {
        name = filteredText(name);
        auxString = name;
    }

    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#verbatim(boolean) */
    @Override
    public void verbatim(boolean boxed) {
        makeHtmlLinks = true;
        inVerbatim = true;
        outputWrite("\n\n```\n");
    }

    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#verbatim_() */
    @Override
    public void verbatim_() {
        makeHtmlLinks = true;
        inVerbatim = false;
        outputWrite("\n```\n");
    }

    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#table() */
    @Override
    public void table() {
        makeHtmlLinks = true;
        super.table();
    }

    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#table_() */
    @Override
    public void table_() {
        makeHtmlLinks = false;
        super.table_();
    }
}
