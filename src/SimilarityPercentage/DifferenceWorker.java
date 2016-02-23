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
import java.util.ArrayList;

public class DifferenceWorker implements Runnable {
	private static long difference = 0;
	private static int nthreads = 0;
	private static HashMap<String, Long> mapA = null;
	private static HashMap<String, Long> mapB = null;
	private static Object syncObject = null;
	private int id;
	
	public DifferenceWorker(int id) {
		if(id < 0) {
			throw new IllegalArgumentException("Thread id must be greater than or equal zero.");
		}
		
		this.id = id;
	}
	
	public static long getDifference() {
		return DifferenceWorker.difference;
	}
	
	public static int getNumberOfThreads() {
		return DifferenceWorker.nthreads;
	}
	
	public static void setNumberOfThreads(int nthreads) {
		if(nthreads <= 0) {
			throw new IllegalArgumentException("Number of threads must be greater than zero.");
		}
		
		DifferenceWorker.nthreads = nthreads;
	}
	
	public static HashMap<String, Long> getMapA() {
		return DifferenceWorker.mapA;
	}
	
	public static void setMapA(HashMap<String, Long> mapA) {
		if(mapA == null) {
			throw new NullPointerException("MapA cannot be null.");
		}
		
		DifferenceWorker.mapA = mapA;
	}
	
	public static HashMap<String, Long> getMapB() {
		return DifferenceWorker.mapB;
	}
	
	public static void setMapB(HashMap<String, Long> mapB) {
		if(mapB == null) {
			throw new NullPointerException("MapB cannot be null.");
		}
		
		DifferenceWorker.mapB = mapB;
	}
	
	public static Object getSynchronizationObject() {
		return DifferenceWorker.syncObject;
	}
	
	public static void setSynchronizationObject(Object syncObject) {
		if(syncObject == null) {
			throw new NullPointerException("Sync object cannot be null.");
		}
		
		DifferenceWorker.syncObject = syncObject;
	}
	
	@Override
	public void run() {
		long difference = 0;
		long count = 0;
		int dimension = 0;
		int low = 0;
		int high = 0;
		ArrayList<Map.Entry<String, Long>> entries = null;
		Map.Entry<String, Long> entry = null;
		
		// Compute the dimension of the subset the current thread is working on, the lower limit
		// and the higher limit of the subset.
		dimension = (int)(DifferenceWorker.mapA.size() / DifferenceWorker.nthreads);
		low = this.id * dimension;
		high = ((this.id + 1) == DifferenceWorker.nthreads)
			? DifferenceWorker.mapA.size()
			: (this.id + 1) * dimension;
		entries = new ArrayList<>(DifferenceWorker.mapA.entrySet());
		for(int i = low; i < high; ++i) {
			entry = entries.get(i);
			count = DifferenceWorker.mapB.containsKey(entry.getKey())
				? DifferenceWorker.mapB.get(entry.getKey())
				: 0;
			
			// The formula used for computing the difference is:
			// sum(abs(appearA(W) - appearB(W))), meaning that for a given word W, we compute the
			// difference between the number of appearances W has made in the first document
			// and the number of appearances in the second document.
			// Here we compute this difference only for pairs found in subset A.
			// For pairs found exclusively in subset B we do the computation below.
			difference += Math.abs(entry.getValue() - count);
		}
		
		// Compute the dimension of the subset the current thread is working on, the lower limit
		// and the higher limit of the subset.
		dimension = (int)(DifferenceWorker.mapB.size() / DifferenceWorker.nthreads);
		low = this.id * dimension;
		high = ((this.id + 1) == DifferenceWorker.nthreads)
			? DifferenceWorker.mapB.size()
			: (this.id + 1) * dimension;
		entries = new ArrayList<>(DifferenceWorker.mapB.entrySet());
		for(int i = low; i < high; ++i) {
			entry = entries.get(i);
			
			// Here we compute the difference for pairs found exclusively in subset B.
			difference += (!DifferenceWorker.mapA.containsKey(entry.getKey()))
				? entry.getValue()
				: 0;
		}
		
		synchronized(DifferenceWorker.syncObject) {
			DifferenceWorker.difference += difference;
		}
	}
}