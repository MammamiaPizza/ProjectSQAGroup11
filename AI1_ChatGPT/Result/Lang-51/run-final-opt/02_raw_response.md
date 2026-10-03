package org.apache.commons.lang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BooleanUtilsLang51Test {

    @Test
    public void toBooleanRecognizesAllDocumentedTrueTokensIgnoringCase() {
        assertTrue(BooleanUtils.toBoolean("true"));
        assertTrue(BooleanUtils.toBoolean(new String("TrUe")));
        assertTrue(BooleanUtils.toBoolean("on"));
        assertTrue(BooleanUtils.toBoolean("ON"));
        assertTrue(BooleanUtils.toBoolean("yes"));
        assertTrue(BooleanUtils.toBoolean("YeS"));
        assertTrue(BooleanUtils.toBoolean("y"));
        assertTrue(BooleanUtils.toBoolean("T"));
    }

    @Test
    public void toBooleanReturnsFalseForNullAndFalseTokens() {
        assertFalse(BooleanUtils.toBoolean((String) null));
        assertFalse(BooleanUtils.toBoolean("false"));
        assertFalse(BooleanUtils.toBoolean("off"));
        assertFalse(BooleanUtils.toBoolean("no"));
        assertFalse(BooleanUtils.toBoolean("n"));
        assertFalse(BooleanUtils.toBoolean("f"));
    }

    @Test
    public void toBooleanReturnsFalseForUnrecognizedThreeCharacterInput() {
        assertFalse(BooleanUtils.toBoolean("abc"));
    }

    @Test
    public void toBooleanHandlesBoundaryLengthUnrecognizedInputs() {
        assertFalse(BooleanUtils.toBoolean(""));
        assertFalse(BooleanUtils.toBoolean("x"));
        assertFalse(BooleanUtils.toBoolean("ox"));
        assertFalse(BooleanUtils.toBoolean("abcd"));
        assertFalse(BooleanUtils.toBoolean("not-a-boolean"));
    }
}