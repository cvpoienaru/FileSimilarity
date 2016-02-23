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

public class SimilarityPercentage {
	public static void main(String[] args) {
		if(args.length != 3) {
			System.out.println("Usage: ./SimilarityPercentage <file1> <file2> <nthreads>");
			System.exit(-1);
		}
		
		try {
			SimilarityCalculator calculator = new SimilarityCalculator(args[0], args[1], args[2]);
			System.out.println(calculator.computePercentage());
		} catch (Exception ex) {
			System.out.println("Fatal error occured:");
			ex.printStackTrace();
			System.exit(-1);
		}
		
		System.exit(0);
	}
}