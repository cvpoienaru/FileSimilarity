/*
 *    Copyright (c) 2015, The Hadoop Team [1].
 *    All rights reserved.
 *
 *    Redistribution and use in source and binary forms, with or without modification, are
 *    permitted provided that the following conditions are met:
 *
 *    - Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *
 *    This software is provided by the copyright holders and contributors "as is" and any express
 *    or implied warranties, including, but not limited to, the implied warranties of
 *    merchantability and fitness for a particular purpose are disclaimed. In no event shall the
 *    copyright holder or contributors be liable for any direct, indirect, incidental, special,
 *    exemplary, or consequential damages (including, but not limited to, procurement of substitute
 *    goods or services; loss of use, data, or profits; or business interruption) however caused
 *    and on any theory of liability, whether in contract, strict liability, or tort (including
 *    negligence or otherwise) arising in any way out of the use of this software, even if advised
 *    of the possibility of such damage.
 *
 *    [1] The Hadoop Team (not related to Apache Hadoop):
 *        Codrin-Victor Poienaru  <cvpoienaru@gmail.com>
 *        Geanina Mihalea         <geanina.mihalea@gmail.com>
 *        Robert Ioan Roventa     <rov_93@yahoo.com>
 */

package SimilarityPercentage;

import java.util.Map;
import java.util.HashMap;
import java.io.FileReader;
import java.io.BufferedReader;

public class SimilarityCalculator {
	private String mapAPath;
	private String mapBPath;
	private int nthreads;
	
	public SimilarityCalculator(String mapAPath, String mapBPath, String nthreads) {
		if(mapAPath == null || mapAPath.isEmpty()) {
			throw new IllegalArgumentException("MapAPath is null or empty.");
		}
		if(mapBPath == null || mapBPath.isEmpty()) {
			throw new IllegalArgumentException("MapBPath is null or empty.");
		}
		if(mapBPath == null || mapBPath.isEmpty()) {
			throw new IllegalArgumentException("Number of threads is null or empty.");
		}
		
		try {
			this.nthreads = Integer.parseInt(nthreads);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Number of threads is not parsable.");
		}
		
		if(this.nthreads <= 0) {
			throw new IllegalArgumentException("Number of threads must be greater than zero.");
		}
		
		this.mapAPath = mapAPath;
		this.mapBPath = mapBPath;
	}
	
	private static HashMap<String, Long> buildMap(String filename) {
		String line = null;
		BufferedReader reader = null;
		HashMap<String, Long> map = new HashMap<String, Long>();
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			while ((line = reader.readLine()) != null) {
				String[] splitted = line.split("\\s+");
				map.put(splitted[0], Long.parseLong(splitted[1]));
			}       
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return map;
	}
	
	private static long computeWordCount(HashMap<String, Long> map) {
		long total = 0;
		
		for(Map.Entry<String, Long> entry : map.entrySet()) {
			total += entry.getValue();
		}
		return total;
	}
	
	public double computePercentage() {
		Thread[] threads = new Thread[this.nthreads];
		HashMap<String, Long> mapA = SimilarityCalculator.buildMap(this.mapAPath);
		HashMap<String, Long> mapB = SimilarityCalculator.buildMap(this.mapBPath);
		long totalCountMapA = computeWordCount(mapA);
		long totalCountMapB = computeWordCount(mapB);
		long cardinalAB = totalCountMapA + totalCountMapB;
		
		DifferenceWorker.setMapA(mapA);
		DifferenceWorker.setMapB(mapB);
		DifferenceWorker.setNumberOfThreads(this.nthreads);
		DifferenceWorker.setSynchronizationObject(new Object());
		for(int i = 0; i < this.nthreads; ++i) {
			threads[i] = new Thread(new DifferenceWorker(i));
			threads[i].start();
		}
		
		try {
			for(int i = 0; i < this.nthreads; ++i) {
				threads[i].join();
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		
		return ((double)cardinalAB - DifferenceWorker.getDifference()) * 100 / cardinalAB;
	}
}