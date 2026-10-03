package org.jsoup.nodes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommentDefects4JTest {

    @Test
    public void preservesCommentDataAndSerializesAsComment() {
        Comment comment = new Comment("some comment text");

        assertEquals("some comment text", comment.getData());
        assertEquals("#comment", comment.nodeName());
        assertEquals("<!--some comment text-->", comment.toString());
    }

    @Test
    public void recognizesDeclarationLikeCommentData() {
        Comment processingInstruction = new Comment("?xml version=\"1.0\"?");
        Comment declaration = new Comment("!DOCTYPE html!");

        assertTrue(processingInstruction.isXmlDeclaration());
        assertTrue(declaration.isXmlDeclaration());
    }

    @Test
    public void normalCommentIsNotAnXmlDeclaration() {
        Comment comment = new Comment("ordinary comment");

        assertFalse(comment.isXmlDeclaration());
    }

    @Test
    public void emptyCommentIsNotAnXmlDeclaration() {
        Comment comment = new Comment("");

        assertFalse(comment.isXmlDeclaration());
    }

    @Test
    public void singleDeclarationPrefixIsRecognized() {
        Comment comment = new Comment("?");

        assertTrue(comment.isXmlDeclaration());
    }

    @Test
    public void lessThanCommentDataIsNotAnXmlDeclaration() {
        Comment comment = new Comment("<");

        assertFalse(comment.isXmlDeclaration());
    }
}