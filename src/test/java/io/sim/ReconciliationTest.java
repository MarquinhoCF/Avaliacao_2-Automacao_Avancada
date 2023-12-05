package io.sim;

import static org.junit.Assert.*;
import org.junit.Test;

import io.sim.processing.reconciliation.Reconciliation;

public class ReconciliationTest {

    @Test
    public void testReconciliation() {
        double[] y = new double[] { 110.5, 60.8, 35.0, 68.9, 38.6, 101.4 };
        double[] v = new double[] { 0.6724, 0.2809, 0.2116, 0.5041, 0.2025, 1.44 };
        double[][] A = new double[][] { { 1, -1, -1, 0, 0, 0 }, { 0, 1, 0, -1, 0, 0 }, { 0, 0, 1, 0, -1, 0 },
                { 0, 0, 0, 1, 1, -1 } };

        Reconciliation rec = new Reconciliation(y, v, A);

        double[] reconciledFlow = rec.getReconciledFlow();

        double[] expectedReconciledFlow = { 103.24010824868768, 65.41556048765828, 37.82454776102939,
                65.41556048765828, 37.82454776102939, 103.24010824868766 };

        assertArrayEquals(expectedReconciledFlow, reconciledFlow, 1e-10);
    }

}

