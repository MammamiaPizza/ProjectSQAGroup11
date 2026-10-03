@org.junit.Test
public void testStepSizeControlParametersHaveDocumentedDefaultsAndCanBeUpdated() {
    final org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator integrator =
        new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(
            1.0e-6, 1.0, 1.0e-10, 1.0e-10);

    org.junit.Assert.assertEquals(0.9, integrator.getSafety(), 0.0);
    org.junit.Assert.assertEquals(0.2, integrator.getMinReduction(), 0.0);
    org.junit.Assert.assertEquals(10.0, integrator.getMaxGrowth(), 0.0);

    integrator.setSafety(0.75);
    integrator.setMinReduction(0.35);
    integrator.setMaxGrowth(4.5);

    org.junit.Assert.assertEquals(0.75, integrator.getSafety(), 0.0);
    org.junit.Assert.assertEquals(0.35, integrator.getMinReduction(), 0.0);
    org.junit.Assert.assertEquals(4.5, integrator.getMaxGrowth(), 0.0);
}