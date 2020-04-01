package com.ccx.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.storage.StorageLevel;

import scala.Tuple2;

public class NameGenTools {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.setProperty("hadoop.home.dir", "D:\\Program Files\\winutils-master\\hadoop-2.7.1");
		SparkConf conf = new SparkConf();
		conf.setAppName("nameGen");
		conf.setMaster("local[4]");
		//conf.setMaster("spark://192.168.100.247:7077");
		conf.set("spark.cores.max", "4");
		conf.set("spark.executor.memory", "1g");
		conf.set("spark.sql.codegen.wholeStage", "false");

		
		SparkContext sc=new SparkContext(conf);
		System.out.println("********************************************");
		System.out.println("**********  Success Init Spark  ************");
		System.out.println("********************************************");
		
		int unitPartNum=4;
		JavaSparkContext jsc=JavaSparkContext.fromSparkContext(sc);
		JavaRDD<String> firstCharRdd = jsc.textFile("D:/data/name/xing.txt");
		JavaRDD<String> sedCharRdd = jsc.textFile("D:/data/name/han.txt");
		
		//sparkContext.stop();
		System.out.println(firstCharRdd.collect());
		System.out.println(sedCharRdd.collect());
		JavaPairRDD<String, String> twoNamePairRdd=firstCharRdd.cartesian(sedCharRdd).repartition(unitPartNum);
		System.out.println(twoNamePairRdd.collect());
		System.out.println("firstCharRdd partitions: "+firstCharRdd.getNumPartitions());
		System.out.println("sedCharRdd partitions: "+sedCharRdd.getNumPartitions());
		System.out.println("twoNameRdd partitions: "+twoNamePairRdd.getNumPartitions());
		//姓名
		JavaRDD<String> twoNameRdd =twoNamePairRdd.map(new  Function<Tuple2<String, String>, String>() {
			private static final long serialVersionUID = 4517627142150602130L;

			public String call(Tuple2<String, String> tuple) throws Exception {
				
				return tuple._1+tuple._2;
			}});
		
		System.out.println(twoNameRdd.collect());
		sedCharRdd.persist(StorageLevel.MEMORY_AND_DISK_SER());
		twoNameRdd.persist(StorageLevel.MEMORY_AND_DISK_SER());
		//姓名名
		JavaPairRDD<String, String> threeNamePairRdd =twoNameRdd.cartesian(sedCharRdd).repartition(unitPartNum);
		JavaRDD<String> threeNameRdd = threeNamePairRdd.map(new  Function<Tuple2<String, String>, String>() {

			private static final long serialVersionUID = -1669612473048910297L;

			public String call(Tuple2<String, String> tuple) throws Exception {
				
				return tuple._1+tuple._2;
			}});
		
		System.out.println(threeNameRdd.collect());
		
		//(姓名,MD5) pairRDD
		
		JavaPairRDD<String, String> twoNameMd5PairRdd= twoNameRdd.mapToPair(new PairFunction<String, String, String>() {

			private static final long serialVersionUID = -3042591570439009248L;

			public Tuple2<String, String> call(String str) throws Exception {
				
				return new Tuple2<String, String>(MD5.encryptionGbk(str).toUpperCase(),str);
			}});
		
		System.out.println(twoNameMd5PairRdd.collect());
		
		//(姓名名,MD5) pairRDD
		JavaPairRDD<String, String> threeNameMd5PairRdd= threeNameRdd.mapToPair(new PairFunction<String, String, String>() {

			private static final long serialVersionUID = -4039842624262376610L;

			public Tuple2<String, String> call(String str) throws Exception {
				
				return new Tuple2<String, String>( MD5.encryptionGbk(str).toUpperCase(),str);
			}});
		System.out.println(threeNameMd5PairRdd.collect());
		
		//已存Name MD5 RDD
		JavaRDD<String> twoNameMD5DbRdd = jsc.textFile("D:/data/name/twoNameMd5");
		JavaRDD<String> threeNameMD5DbRdd = jsc.textFile("D:/data/name/threeNameMd5");
		
		System.out.println(threeNameMD5DbRdd.collect());
		
		// RDD按條件取交集
		JavaPairRDD<String, String> twoNameMD5DbPairRdd=twoNameMD5DbRdd.distinct().mapToPair(new PairFunction<String, String, String>() {

			private static final long serialVersionUID = -3042591570439009248L;

			public Tuple2<String, String> call(String str) throws Exception {
				
				return new Tuple2<String, String>(str,"@");
			}} );
		
//		 JavaPairRDD<String, Tuple2<String, String>>  twoOtherRdd=twoNameMD5DbPairRdd.join(twoNameMd5PairRdd);//.leftOuterJoin(twoNameMd5PairRdd, unitPartNum);
		 
		 System.out.println("twoNameMd5PairRdd: "+twoNameMd5PairRdd.collect());
		 System.out.println("twoNameMD5DbPairRdd: "+twoNameMD5DbPairRdd.collect());
//		 System.out.println("twoOtherRdd: "+twoOtherRdd.collect());
		 
		 JavaPairRDD<String, Tuple2<String, Optional<String>>> twoOtherleftRdd =twoNameMD5DbPairRdd.leftOuterJoin(twoNameMd5PairRdd,unitPartNum);
		 JavaRDD<String> twoNameAimRdd=twoOtherleftRdd.map( new Function<Tuple2<String, Tuple2<String, Optional<String>>>, String>(){

			public String call(Tuple2<String, Tuple2<String, Optional<String>>> tuple) throws Exception {
				StringBuffer sb=new StringBuffer();
				sb.append(tuple._1).append(tuple._2._1);
				Optional<String> opt=tuple._2._2;
				if(opt.isPresent()){
					sb.append(opt.get());
				}
				return sb.toString();
			}});
		 		 System.out.println(twoNameAimRdd.collect());
		 		twoNameAimRdd.saveAsTextFile("D:/data/name/twoNameMd5Aim");
//		 System.out.println(twoOtherssRdd.count());
		
	}

	
}
