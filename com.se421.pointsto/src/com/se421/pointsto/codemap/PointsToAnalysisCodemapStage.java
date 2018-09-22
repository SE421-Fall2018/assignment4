package com.se421.pointsto.codemap;

import java.text.DecimalFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ensoftcorp.atlas.core.indexing.providers.ToolboxIndexingStage;
import com.se421.pointsto.algorithms.AndersenPointsTo;
import com.se421.pointsto.log.Log;

public class PointsToAnalysisCodemapStage implements ToolboxIndexingStage {

	@Override
	public String displayName() {
		return "Points-to Analysis";
	}

	@Override
	public void performIndexing(IProgressMonitor monitor) {
		try {
			Log.info("Starting points-to analysis");
			long start = System.nanoTime();
			AndersenPointsTo.run();
			long stop = System.nanoTime();
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			double time = (stop - start)/1000.0/1000.0; // ms
			if(time < 100) {
				Log.info("Finished  points-to analysis in " + decimalFormat.format(time) + "ms");
			} else {
				time = (stop - start)/1000.0/1000.0/1000.0; // s
				if(time < 60) {
					Log.info("Finished points-to analysis in " + decimalFormat.format(time) + "s");
				} else {
					if(time < 60) {
						Log.info("Finished points-to analysis in " + decimalFormat.format(time) + "m");
					} else {
						time = (stop - start)/1000.0/1000.0/1000.0/60.0/60.0; // h
						Log.info("Finished points-to analysis in " + decimalFormat.format(time) + "h");
					}
				}
			}
		} catch (Exception e) {
			Log.error("Error running points-to analysis codemap stage", e);
		}
	}
	
}
