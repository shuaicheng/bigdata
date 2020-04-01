package com.ccx.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.storage.StorageLevel;

import scala.Tuple2;

public class NameHitMD5ToolsTwoName {

	private static final String xingPath = "file:///data/ccxNameMD5/surname.txt";// "D:/data/name/1124/surname.txt";
	private static final String HanziPath = "file:///data/ccxNameMD5/allchar.txt";// "D:/data/name/1124/allchar.txt";
	private static final String nameMd5Path = "file:///data/ccxNameMD5/S4_custname_md5.txt";// "D:/data/name/1124/S4_custname_md5.txt";
	private static final String nameTargetPath = "file:///data/ccxNameMD5/nameMd5Result";// "D:/data/name/1124/nameMd5Aim";
	private static Integer unitPartNum = 72;

	public static void main(String[] args) {
		JavaSparkContext jsc = genJavaSparkContext();

		hitMd5(jsc);
	}

	private static JavaSparkContext genJavaSparkContext() {
		// System.setProperty("hadoop.home.dir", "D:\\Program
		// Files\\winutils-master\\hadoop-2.7.1");
		SparkConf conf = new SparkConf();
		conf.setAppName("nameMd5Decrypt");
		// conf.setMaster("local[4]");
		conf.setMaster("spark://192.168.70.29:7077");
		conf.set("spark.cores.max", "36");
		conf.set("spark.executor.memory", "30g");
		conf.set("spark.sql.codegen.wholeStage", "false");

		SparkContext sc = new SparkContext(conf);
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(sc);
		System.out.println("************************************************************");
		System.out.println("**********  Success Init Spark JavaSparkContext ************");
		System.out.println("*************************************************************");
		return jsc;
	}

	private static void hitMd5(JavaSparkContext jsc) {

		JavaRDD<String> xingCharRdd = jsc.textFile(xingPath, unitPartNum);
		JavaRDD<String> hanCharRdd = jsc.textFile(HanziPath, unitPartNum);
		System.out.println("xingCharRdd partitions: " + xingCharRdd.getNumPartitions());
		System.out.println("hanCharRdd partitions: " + hanCharRdd.getNumPartitions());

		// 已存Name MD5 RDD
		JavaRDD<String> nameMD5DbRdd = jsc.textFile(nameMd5Path, unitPartNum);
		System.out.println("nameMD5DbRdd partitions: " + nameMD5DbRdd.getNumPartitions());
		JavaPairRDD<String, String> nameMD5DbPairRdd = nameMD5DbRdd.distinct()
				.mapToPair(new PairFunction<String, String, String>() {

					private static final long serialVersionUID = -3042591570439009248L;

					public Tuple2<String, String> call(String str) throws Exception {

						return new Tuple2<String, String>(str, "@");
					}
				});

		nameMD5DbPairRdd.persist(StorageLevel.MEMORY_AND_DISK_SER());

		JavaPairRDD<String, String> twoNamePairRdd = xingCharRdd.cartesian(hanCharRdd);
		System.out.println("twoNameRdd partitions: " + twoNamePairRdd.getNumPartitions());

		// 姓名
		JavaRDD<String> twoNameRdd = twoNamePairRdd.map(new Function<Tuple2<String, String>, String>() {
			private static final long serialVersionUID = 4517627142150602130L;

			public String call(Tuple2<String, String> tuple) throws Exception {
				return tuple._1 + tuple._2;
			}
		});
		

		// (MD5,姓名) pairRDD
		JavaPairRDD<String, String> twoNameMd5PairRdd = twoNameRdd
				.mapToPair(new PairFunction<String, String, String>() {

					private static final long serialVersionUID = -3042591570439009248L;

					public Tuple2<String, String> call(String str) throws Exception {

						return new Tuple2<String, String>(MD5.encryptionGbk(str).toUpperCase(), str);
					}
				});
		System.out.println("twoNameMd5PairRdd partitions: " + twoNameMd5PairRdd.getNumPartitions());

		// 姓名 RDD 与 全量MD5 RDD 按Key相同取
		JavaPairRDD<String, Tuple2<String, String>> twonameMd5AimPairRdd = nameMD5DbPairRdd.join(twoNameMd5PairRdd); // 分区太大的话，可以repartition
		System.out.println("nameMd5AimPairRdd partitions: " + twonameMd5AimPairRdd.getNumPartitions());

		JavaRDD<String> twonameMd5AimRdd = twonameMd5AimPairRdd
				.map(new Function<Tuple2<String, Tuple2<String, String>>, String>() {
					private static final long serialVersionUID = 4867781188309274280L;

					public String call(Tuple2<String, Tuple2<String, String>> tuple) throws Exception {
						StringBuffer sb = new StringBuffer();
						sb.append(tuple._1).append(tuple._2._1).append(tuple._2._2);
						return sb.toString();
					}
				});
		
		// 姓名 RDD 与 全量MD5 RDD 取相同 后存 文件
		twonameMd5AimRdd.repartition(1).saveAsTextFile(nameTargetPath + "-two-" + System.currentTimeMillis());

		
//		// hanCharRdd twoNameRdd 持久化
//				hanCharRdd.persist(StorageLevel.MEMORY_AND_DISK_SER());
//				twoNameRdd.persist(StorageLevel.MEMORY_AND_DISK_SER());
//		// 姓名名
//		JavaPairRDD<String, String> threeNamePairRdd = twoNameRdd.repartition(unitPartNum).cartesian(hanCharRdd);// .repartition(unitPartNum);
//		JavaRDD<String> threeNameRdd = threeNamePairRdd.map(new Function<Tuple2<String, String>, String>() {
//			private static final long serialVersionUID = -1669612473048910297L;
//
//			public String call(Tuple2<String, String> tuple) throws Exception {
//
//				return tuple._1 + tuple._2;
//			}
//		});
//
//		// (MD5,姓名名) pairRDD
//		JavaPairRDD<String, String> threeNameMd5PairRdd = threeNameRdd
//				.mapToPair(new PairFunction<String, String, String>() {
//
//					private static final long serialVersionUID = -4039842624262376610L;
//
//					public Tuple2<String, String> call(String str) throws Exception {
//
//						return new Tuple2<String, String>(MD5.encryptionGbk(str).toUpperCase(), str);
//					}
//				});
//		System.out.println("threeNameMd5PairRdd partitions: " + threeNameMd5PairRdd.getNumPartitions());
//
//		// 姓名名 RDD 与 全量MD5 RDD 按Key相同取
//		JavaPairRDD<String, Tuple2<String, String>> threenameMd5AimPairRdd = nameMD5DbPairRdd.join(threeNameMd5PairRdd); // 分区太大的话，可以repartition
//		System.out.println("threenameMd5AimPairRdd partitions: " + threenameMd5AimPairRdd.getNumPartitions());
//
//		JavaRDD<String> threenameMd5AimRdd = threenameMd5AimPairRdd
//				.map(new Function<Tuple2<String, Tuple2<String, String>>, String>() {
//					private static final long serialVersionUID = 4867781188309274280L;
//
//					public String call(Tuple2<String, Tuple2<String, String>> tuple) throws Exception {
//						StringBuffer sb = new StringBuffer();
//						sb.append(tuple._1).append(tuple._2._1).append(tuple._2._2);
//						return sb.toString();
//					}
//				});
//		// 姓名名 RDD 与 全量MD5 RDD 取相同 后存 文件
//		threenameMd5AimRdd.repartition(1).saveAsTextFile(nameTargetPath + "-three-" + System.currentTimeMillis());
//		hanCharRdd.unpersist();
//		twoNameRdd.unpersist();
//		nameMD5DbPairRdd.unpersist();

		// 保留未命中的Md5
		// JavaPairRDD<String, Tuple2<String, Optional<String>>>
		// nameMd5AimPairRdd
		// =nameMD5DbPairRdd.leftOuterJoin(nameMd5PairRdd,unitPartNum);
		// JavaRDD<String> nameMd5AimRdd=nameMd5AimPairRdd.map( new
		// Function<Tuple2<String, Tuple2<String, Optional<String>>>, String>(){
		// public String call(Tuple2<String, Tuple2<String, Optional<String>>>
		// tuple) throws Exception {
		// StringBuffer sb=new StringBuffer();
		// sb.append(tuple._1).append(tuple._2._1);
		// Optional<String> opt=tuple._2._2;
		// if(opt.isPresent()){
		// sb.append(opt.get());
		// }
		// return sb.toString();
		// }});

	}

}
