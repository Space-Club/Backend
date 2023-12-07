package com.spaceclub.notification.mail;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class HtmlConverter {

    public static String markdownToHtml(String filePath) {
        try {
            Path path = Paths.get(filePath);

            return generateHtml(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다.");
        }
    }

    private static String generateHtml(Path path) throws IOException {
        String content = Files.readString(path);
        Parser parser = Parser.builder().build();
        Node document = parser.parse(content);
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        return renderer.render(document);
    }

}
