package org.apache.commons.math.linear;

import java.math.BigDecimal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class Math209LuDecompositionTest {

    @Test
    public void testBigMatrixTallMatrixLuDecompositionThrowsInvalidMatrixException() {
        BigMatrixImpl matrix = new BigMatrixImpl(new BigDecimal[][] {
            { new BigDecimal("1"), new BigDecimal("2") },
            { new BigDecimal("3"), new BigDecimal("4") },
            { new BigDecimal("5"), new BigDecimal("6") }
        });

        try {
            matrix.luDecompose();
            fail("LU decomposition of a non-square matrix must fail");
        } catch (InvalidMatrixException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void testRealMatrixTallMatrixLuDecompositionThrowsInvalidMatrixException() {
        RealMatrixImpl matrix = new RealMatrixImpl(new double[][] {
            { 1.0, 2.0 },
            { 3.0, 4.0 },
            { 5.0, 6.0 }
        });

        try {
            matrix.luDecompose();
            fail("LU decomposition of a non-square matrix must fail");
        } catch (InvalidMatrixException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void testBigMatrixWideMatrixLuDecompositionThrowsInvalidMatrixException() {
        BigMatrixImpl matrix = new BigMatrixImpl(new BigDecimal[][] {
            { new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3") },
            { new BigDecimal("4"), new BigDecimal("5"), new BigDecimal("6") }
        });

        try {
            matrix.luDecompose();
            fail("LU decomposition of a non-square matrix must fail");
        } catch (InvalidMatrixException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void testRealMatrixWideMatrixLuDecompositionThrowsInvalidMatrixException() {
        RealMatrixImpl matrix = new RealMatrixImpl(new double[][] {
            { 1.0, 2.0, 3.0 },
            { 4.0, 5.0, 6.0 }
        });

        try {
            matrix.luDecompose();
            fail("LU decomposition of a non-square matrix must fail");
        } catch (InvalidMatrixException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void testBigMatrixSquareMatrixStillDecomposesAndComputesDeterminant() {
        BigMatrixImpl matrix = new BigMatrixImpl(new BigDecimal[][] {
            { new BigDecimal("2"), new BigDecimal("1") },
            { new BigDecimal("1"), new BigDecimal("2") }
        });

        matrix.luDecompose();

        assertEquals(0, new BigDecimal("3").compareTo(matrix.getDeterminant()));
    }

    @Test
    public void testRealMatrixSquareMatrixStillDecomposesAndComputesDeterminant() {
        RealMatrixImpl matrix = new RealMatrixImpl(new double[][] {
            { 2.0, 1.0 },
            { 1.0, 2.0 }
        });

        matrix.luDecompose();

        assertEquals(3.0, matrix.getDeterminant(), 0.0);
    }
}
