package com.ccx.spark.demo;

import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.storage.StorageLevel;

import scala.Tuple2;

public class NameHitMD5ToolsThreeName {

	
	private static  String xingPath = "file:///data/ccxNameMD5/surname.txt";// "D:/data/name/1124/surname.txt";
	private static  String HanziPath = "file:///data/ccxNameMD5/allchar.txt";// "D:/data/name/1124/allchar.txt";
	private static  String twoNamePath = "file:///data/ccxNameMD5/part-00000";
	private static  String nameMd5Path = "file:///data/ccxNameMD5/S4_custname_md5.txt";// "D:/data/name/1124/S4_custname_md5.txt";
	private static  String nameTargetPath = "file:///data/ccxNameMD5/nameMd5Result/threeName/result";// "D:/data/name/1124/nameMd5Aim";
	private static Integer unitPartNum = 48;

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
		conf.set("spark.cores.max", "48");
		conf.set("spark.executor.memory", "50g");
		conf.set("spark.sql.codegen.wholeStage", "false");
		conf.set("spark.sql.shuffle.partitions", "400");

		SparkContext sc = new SparkContext(conf);
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(sc);
		System.out.println("************************************************************");
		System.out.println("**********  Success Init Spark JavaSparkContext ************");
		System.out.println("*************************************************************");
		return jsc;
	}

	private static void hitMd5(JavaSparkContext jsc) {

		JavaRDD<String> twoNameCharRdd = jsc.textFile(twoNamePath, unitPartNum);
		JavaRDD<String> hanCharRdd = jsc.textFile(HanziPath, unitPartNum);
		System.out.println("twoNameCharRdd partitions: " + twoNameCharRdd.getNumPartitions());
		System.out.println("sedhanCharRdd partitions: " + hanCharRdd.getNumPartitions());
		
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
		nameMD5DbPairRdd.collect();
		nameMD5DbPairRdd.persist(StorageLevel.MEMORY_AND_DISK_SER());

		twoNameCharRdd.take(10);
		twoNameCharRdd.persist(StorageLevel.MEMORY_AND_DISK_SER());
		
		List<String> allHanCharList=hanCharRdd.collect();
		int size=allHanCharList.size();//total 8416
		int ceil=16;
		int fromIndex=0;
		int turns =526;
		System.out.println("total : {}"+size);
		for(int i=1;i<=turns-1;i++){
			System.out.println("====== index: "+i);
			int toIndex=fromIndex+ceil;
			List<String> subList=allHanCharList.subList(fromIndex, toIndex);
			JavaRDD<String> subCharRdd = jsc.parallelize(subList, unitPartNum);
			
			
//			final String thrChar="鹏";
//			//姓名名 RDD			
//			JavaPairRDD<String, String>  threeNameMd5PairRdd=twoNameCharRdd.mapToPair(new PairFunction<String, String, String>(){
//				
//				private static final long serialVersionUID = 1L;
//				public Tuple2<String, String> call(String twoname) throws Exception {
//					String str=twoname+thrChar;
//					return new Tuple2<String, String>(MD5.encryptionGbk(str).toUpperCase(), str);
//				}});
//			threeNameMd5PairRdd.take(10);
//			System.out.println("Turn: "+i+"threeNameMd5PairRdd partitions: " + threeNameMd5PairRdd.getNumPartitions());
			
			JavaPairRDD<String, String>  threeNamePairRdd=twoNameCharRdd.cartesian(subCharRdd);
			JavaPairRDD<String, String>  threeNameMd5PairRdd=threeNamePairRdd.mapToPair(new PairFunction<Tuple2<String, String>, String, String>(){

				private static final long serialVersionUID = -5105871242287300927L;

				public Tuple2<String, String> call(Tuple2<String, String> t) throws Exception {
					String str=t._1+t._2;
					return new Tuple2<String, String>(MD5.encryptionGbk(str).toUpperCase(), str);
				}
				
			});
			
			JavaPairRDD<String, Tuple2<String, String>> threenameMd5AimPairRdd = threeNameMd5PairRdd.join(nameMD5DbPairRdd);
			
			JavaRDD<String> threenameMd5AimRdd = threenameMd5AimPairRdd
					.map(new Function<Tuple2<String, Tuple2<String, String>>, String>() {
						private static final long serialVersionUID = 4867781188309274280L;

						public String call(Tuple2<String, Tuple2<String, String>> tuple) throws Exception {
							StringBuffer sb = new StringBuffer();
							sb.append(tuple._1).append(tuple._2._2).append(tuple._2._1);
							return sb.toString();
						}
					});
			// 姓名名 RDD 与 全量MD5 RDD 取相同 后存 文件
			
			threenameMd5AimRdd.repartition(1).saveAsTextFile(nameTargetPath + "-three-"+i );
		}
		
		
		nameMD5DbPairRdd.unpersist();
		twoNameCharRdd.unpersist();
	}

}
