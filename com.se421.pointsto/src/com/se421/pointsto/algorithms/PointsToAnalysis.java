package com.se421.pointsto.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;

public class PointsToAnalysis {

	/**
	 * The tag prefix of points-to addresses. An "address" is an abstract concept
	 * the corresponds to a unique allocation. References with overlapping
	 * points-to addresses may be aliases of each other. References with only
	 * the same alias must be aliases of each other. References without any
	 * commons points-to address must not be aliases of each other.
	 * 
	 * For convenience this tag is also applied to array components to indicate
	 * the array address. An array component will contain a single address
	 * corresponding to a unique array instantiation.
	 */
	public static final String ALIAS_PREFIX = "ALIAS_";
	
	/**
	 * Applied to edges to indicate that the edge's runtime type of possibility was
	 * verified by the points-to analysis
	 */
	public static final String INFERRED_TYPE_OF = "INFERRED_TYPE_OF";
	
	/**
	 * Returns true if two references not (ever) aliases to the same memory value
	 * @param ref1
	 * @param ref2
	 * @return
	 */
	public static boolean notAliases(Node ref1, Node ref2){
		return !mayAlias(ref1, ref2);
	}
	
	/**
	 * Checks if the two given references may be aliases of each other
	 * @param ref1
	 * @param ref2
	 * @return
	 */
	public static boolean mayAlias(Node ref1, Node ref2){
		HashSet<String> ref1AliasTags = new HashSet<String>();
		for(String aliasTag : getAliasTags(ref1)){
			ref1AliasTags.add(aliasTag);
		}
		for(String aliasTag : getAliasTags(ref2)){
			if(ref1AliasTags.contains(aliasTag)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the two given references must be aliases of each other
	 * @param ref1
	 * @param ref2
	 * @return
	 */
	public static boolean mustAlias(Node ref1, Node ref2){
		String[] ref1AliasTags = getAliasTags(ref1);
		Arrays.sort(ref1AliasTags);
		String[] ref2AliasTags = getAliasTags(ref2);
		Arrays.sort(ref2AliasTags);
		if(ref1AliasTags.length == ref2AliasTags.length){
			for(int i=0; i<ref1AliasTags.length; i++){
				if(!ref1AliasTags[i].equals(ref2AliasTags[i])){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Returns nodes with the same points-to address tags as the given node
	 * @param node
	 * @return
	 */
	public static Q getAliases(Node node){
		String[] tags = getAliasTags(node);
		if(tags.length == 0){
			return Common.empty();
		} else {
			Q aliases = Common.universe().nodesTaggedWithAny(tags);
			return aliases;
		}
	}
	
	/**
	 * Returns an array of points-to tags applied to the given node
	 * @param node
	 * @return
	 */
	public static String[] getAliasTags(Node node){
		ArrayList<String> tags = new ArrayList<String>();
		for(String tag : node.tags()){
			if(tag.startsWith(ALIAS_PREFIX)){
				tags.add(tag);
			}
		}
		String[] result = new String[tags.size()];
		result = tags.toArray(result);
		return result;
	}
	
}
