package org.guercifzone.Classes;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;

import java.io.*;
import java.util.concurrent.atomic.AtomicReference;

public class HtmlDecoder {

    public static String decodeHtml(String html) throws IOException {
        AtomicReference<String> result = new AtomicReference<>("");
        Reader reader = new StringReader(html);

        new ParserDelegator().parse(reader, new HTMLEditorKit.ParserCallback() {
            @Override
            public void handleText(char[] data, int pos) {
                result.set(new String(data));
            }
        }, true);

        return result.get();
    }
}

