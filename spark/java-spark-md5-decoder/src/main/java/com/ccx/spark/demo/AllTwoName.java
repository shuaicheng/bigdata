package com.ccx.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

public class AllTwoName {

	private static  String xingPath = "file:///data/ccxNameMD5/surname.txt";// "D:/data/name/1124/surname.txt";
	private static  String HanziPath = "file:///data/ccxNameMD5/allchar.txt";// "D:/data/name/1124/allchar.txt";
	private static  String nameTargetPath = "file:///data/ccxNameMD5/twoNameResult-2";// "D:/data/name/1124/nameMd5Aim";
	private static Integer unitPartNum = 48;
	
	public static void main(String[] args) {
		JavaSparkContext jsc = genJavaSparkContext();

		genAllName(jsc);

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
	
	private static void genAllName(JavaSparkContext jsc){
		JavaRDD<String> xingCharRdd = jsc.textFile(xingPath, unitPartNum);
		JavaRDD<String> sedhanCharRdd = jsc.textFile(HanziPath, unitPartNum);
		System.out.println("xingCharRdd partitions: " + xingCharRdd.getNumPartitions());
		System.out.println("sedhanCharRdd partitions: " + sedhanCharRdd.getNumPartitions());
		
		JavaPairRDD<String, String> twoNamePairRdd = xingCharRdd.cartesian(sedhanCharRdd);
		//姓名
				JavaRDD<String> twoNameRdd =twoNamePairRdd.map(new  Function<Tuple2<String, String>, String>() {
					private static final long serialVersionUID = 4517627142150602130L;

					public String call(Tuple2<String, String> tuple) throws Exception {
						
						return tuple._1+tuple._2;
					}});
				
		twoNameRdd.repartition(1).saveAsTextFile(nameTargetPath);
	}
}
