package com.cg.spark.demo;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import scala.Tuple2;

public class WordCountServer {
	public static void main(String[] a){
		// 实际执行步骤：
		// 1、将spark.txt文件上传到hdfs上去
		// 2、使用我们最早在pom.xml里配置的maven插件，对spark工程进行打包
		// 3、将打包后的spark工程jar包，上传到机器上执行
		// 4、编写spark-submit脚本
		// 5、执行spark-submit脚本，提交spark应用到集群执行

		SparkConf conf = new SparkConf()
		        .setAppName("WordCountCluster").setMaster("spark://192.168.2.218:7077")
		        ;//.set("spark.executor.memory","1024m");//.set("spark.cores.max", "4");  

		JavaSparkContext sc = new JavaSparkContext(conf);

		JavaRDD<String> lines = sc.textFile("hdfs://namenode:9000/test/T.txt");

		JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {

		    private static final long serialVersionUID = 1L;

		    public Iterator<String> call(String line) throws Exception {
		        return Arrays.asList(line.split(" ")).iterator();  
		    }

		});

		JavaPairRDD<String, Integer> pairs = words.mapToPair(

		        new PairFunction<String, String, Integer>() {

		            private static final long serialVersionUID = 1L;

		            public Tuple2<String, Integer> call(String word) throws Exception {
		                return new Tuple2<String, Integer>(word, 1);
		            }

		        });

		JavaPairRDD<String, Integer> wordCounts = pairs.reduceByKey(

		        new Function2<Integer, Integer, Integer>() {

		            private static final long serialVersionUID = 1L;

		            public Integer call(Integer v1, Integer v2) throws Exception {
		                return v1 + v2;
		            }

		        });
		System.out.println("===========================================================================================");
		System.out.println("wordcount.count()::::::"+wordCounts.collect());
		System.out.println("===========================================================================================");
		wordCounts.foreach(new VoidFunction<Tuple2<String,Integer>>() {

		    private static final long serialVersionUID = 1L;

		    public void call(Tuple2<String, Integer> wordCount) throws Exception {
		    	System.out.println("*******************************************************************************************");
		        System.out.println(wordCount._1 + " appeared " + wordCount._2 + " times.");    
		        System.out.println("*******************************************************************************************");
		    }

		});

		sc.close();
	}

}
