package com.se421.pointsto.algorithms;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasHashSet;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;

public class AndersenPointsTo {

	/**
	 * The main entry point to run the points-to analysis
	 */
	public static void run() {
		// a worklist of nodes to propagate information from
		Queue<Node> worklist = new LinkedList<Node>();
		
		// get a set of every "new" memory instantiation
		AtlasSet<Node> instantiations = Common.universe().nodes(XCSG.Instantiation, XCSG.ArrayInstantiation).eval().nodes();
		
		// a graph of assignment relationships
	    Q dataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
		
		// assign each new instantiation a unique "address"
		for(Node instantiation : instantiations) {
			Set<Integer> pointsToSet = getPointsToSet(instantiation);
			Integer address = addressFactory.getNewAddress();
			pointsToSet.add(address);
			
			// initialize the worklist with each new instantiation
			worklist.add(instantiation);
		}
		
		// TODO: remove an item from the worklist
		//       get the work item's points-to addresses
		//       for each assignment from the work item to another reference (ex: b=a;)
		//       propagate addresses from a to b
		//       if a received new addresses then a may have new addresses to propagate
		//       to it's data flow successors, so add a to the worklist
		//       repeat until worklist is empty...
	    
 		// this helper method converts your points-to set addresses
 		// to Atlas tags with the prefix ALIAS_<address>
		saveResults();
	}
	
	/**
	 * A helper class to create unique object addresses
	 */
	private static AddressFactory addressFactory = new AddressFactory();
	
	/**
	 * Just a temporary attribute to hold points-to results as a set before converting to tags
	 */
	private static final String POINTS_TO_SET = "POINTS_TO_SET";
	
	/**
	 * Gets or creates the points to set for a graph element.
	 * Returns a reference to the points to set so that updates to the 
	 * set will also update the set on the graph element.
	 * @param node
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	private static Set<Integer> getPointsToSet(Node node){
		if(node.hasAttr(POINTS_TO_SET)){
			return (Set<Integer>) node.getAttr(POINTS_TO_SET);
		} else {
			Set<Integer> pointsToAddresses = new HashSet<Integer>();
			node.putAttr(POINTS_TO_SET, pointsToAddresses);
			return pointsToAddresses;
		}
	}
	
	/**
	 * A helper method for saving the results as tags
	 * 
	 * 1) (REQUIRED) This converts points-to address sets to ALIAS_<address> tags
	 * 2) (EXTRA CREDIT) This creates and tags edges with INFERRED_TYPE_OF  
	 *    corresponding to the type that is actually feasible according to 
	 *    the points-to analysis
	 */
	private static void saveResults() {
		AtlasSet<Node> pointsToSets = new AtlasHashSet<Node>(Common.universe().selectNode(POINTS_TO_SET).eval().nodes());
		
		// for each addressed node in the data flow graph
		// convert the points-to addresses to alias tags
		while(!pointsToSets.isEmpty()) {
			Node variable = pointsToSets.one();
			for(Integer address : getPointsToSet(variable)){
				// convert the address to an alias tag
				variable.tag(PointsToAnalysis.ALIAS_PREFIX + address);
				
				// tag the inferred type of edges
				Node runtimeType = getInstantiationType(address);
				if(runtimeType != null) {
					Edge inferredTypeOfEdge = Common.universe().edges(PointsToAnalysis.INFERRED_TYPE_OF)
							.betweenStep(Common.toQ(variable), Common.toQ(runtimeType)).eval().edges().one();
					if(inferredTypeOfEdge == null){
						inferredTypeOfEdge = Graph.U.createEdge(variable, runtimeType);
						inferredTypeOfEdge.tag(PointsToAnalysis.INFERRED_TYPE_OF);
					}
				}
			}
			
			// purge the old address sets
			variable.removeAttr(POINTS_TO_SET);
			pointsToSets.remove(variable);
		}
	}

	/**
	 * For a given node, return the type of the instantiation
	 * Note: You may change this suggested method however you want
	 * @param address
	 * @return
	 */
	private static Node getInstantiationType(Integer address) {
		return null;
	}
	
}
