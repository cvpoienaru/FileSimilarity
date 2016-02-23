#!/bin/bash

NTHREADS=4
IO_PATH=$HADOOP_HOME/io
JAR_PATH=$HADOOP_HOME/share/hadoop/mapreduce

mkdir -p ./tmp
rm -rf $IO_PATH/inputs/*
rm -rf $IO_PATH/out*
cp $1 $IO_PATH/inputs
time hadoop jar $JAR_PATH/hadoop-mapreduce-examples-2.6.2.jar \
	wordcount $IO_PATH/inputs/* $IO_PATH/output 2> /dev/null
cat $IO_PATH/output/* > ./tmp/result1.txt

rm -rf $IO_PATH/inputs/*
rm -rf $IO_PATH/out*
cp $2 $IO_PATH/inputs
time hadoop jar $JAR_PATH/hadoop-mapreduce-examples-2.6.2.jar \
	wordcount $IO_PATH/inputs/* $IO_PATH/output 2> /dev/null
cat $IO_PATH/output/* > ./tmp/result2.txt

#Execute comparer here
time java -cp ./bin SimilarityPercentage.SimilarityPercentage \
	./tmp/result1.txt ./tmp/result2.txt $NTHREADS

rm -rf ./tmp
