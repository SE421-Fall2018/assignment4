/**
 * 
 */
package com.se421.pointsto.regression.test.cases;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.se421.pointsto.algorithms.PointsToAnalysis;
import com.se421.pointsto.regression.Activator;
import com.se421.pointsto.regression.RegressionTest;

/**
 * Checks that Andersen-style Points-to analysis results match expected results
 * 
 * Execute this class with Run As -> JUnit Plug-in Test
 * 
 * @author Ben Holland
 */
public class JavaPointsToTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RegressionTest.setUpBeforeClass(Activator.getDefault().getBundle(), "/projects/JavaPointsToExamples.zip", "JavaPointsToExamples");
	}

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	/**
	 * This is really just a sanity check that the index exists and correct project was loaded
	 */
	@Test
	public void testExpectedFunctionExists() {
		if(Common.functions("test1").eval().nodes().isEmpty()) {
			fail("Unable to locate expected test function.");
		}
	}
	
	/**
	 * Tests that the results are computed in a flow sensitive manner
	 */
	@Test
	public void testFlowSensitive() {
		Q dfg = Common.functions("test1").contained().nodes(XCSG.DataFlow_Node).induce(Common.universe().edges(XCSG.LocalDataFlow));
		Q printStreamParameter = Common.methodSelect("java.io", "PrintStream", "print").children().nodes(XCSG.Parameter);
		Node testPoint = Common.universe().edges(XCSG.DataFlow_Edge).predecessors(printStreamParameter).intersection(dfg).retainNodes().eval().nodes().one();
		Q instantiations = PointsToAnalysis.getAliases(testPoint).nodes(XCSG.Instantiation, XCSG.ArrayInstantiation);
		if(instantiations.eval().nodes().size() != 1) {
			fail("Points-to analysis should be flow sensitive");
		}
	}
	
	/**
	 * Tests that there are two expected reaching definitions through a branch
	 */
	@Test
	public void testTwoReachingDefinitions() {
		Q dfg = Common.functions("test2").contained().nodes(XCSG.DataFlow_Node).induce(Common.universe().edges(XCSG.LocalDataFlow));
		Q printStreamParameter = Common.methodSelect("java.io", "PrintStream", "print").children().nodes(XCSG.Parameter);
		Node testPoint = Common.universe().edges(XCSG.DataFlow_Edge).predecessors(printStreamParameter).intersection(dfg).retainNodes().eval().nodes().one();
		Q instantiations = PointsToAnalysis.getAliases(testPoint).nodes(XCSG.Instantiation, XCSG.ArrayInstantiation);
		if(instantiations.eval().nodes().size() != 2) {
			fail("Expected two reaching definitions");
		}
	}

}
