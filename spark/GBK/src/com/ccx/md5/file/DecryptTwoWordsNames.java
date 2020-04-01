package com.ccx.md5.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DecryptTwoWordsNames {
	private static int threads = 200;
	public static void main(String[] args) {
		String twnPath = "D:/md5/twonameall.txt";//姓+名
		String hanziPath = "D:/md5/hanzi.txt";//所有汉字
		String nameMd5Path = "D:/md5/name_md5_all.txt";//姓名MD5值
		int size = 50000;
		int from = 1;
		if(args.length>1) {
			if(args[0]!=null) {
				twnPath = args[0];
			}
			if(args[1]!=null) {
				hanziPath = args[1];
			}
			
			if(args[2]!=null) {
				nameMd5Path = args[2];
			}
			if(args[3]!=null) {
				from = Integer.valueOf(args[3]);
			}
			
			if(args[4]!=null) {
				threads = Integer.valueOf(args[4]);
			}
		}
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threads);
		List<String> twoWordsNames = readFile(twnPath);
		System.out.println(twoWordsNames.size());
		List<String> hanziS = readFile(hanziPath);
		Map<String,String> md5Names = readFile2Map(nameMd5Path);
		for(int i=0;i<threads;i++) {
			int start = from + i*size -1;
			int end = from + (i+1)*size -1;
			if(end>twoWordsNames.size()) {
				end = twoWordsNames.size();
			}
			if(start<end) {
				List<String> subedTwoWordsNames = twoWordsNames.subList(start, end);
				fixedThreadPool.submit(new DectyptTwoWordsNamesMd5Runner(subedTwoWordsNames, hanziS, md5Names));
			}
		}
	}
	
	
	public static List<String> readFile(String filePath){
		InputStreamReader  in = null;
        BufferedReader reader = null;
        List<String> a = new ArrayList<String>();
        try {
            //得到输入流
            FileInputStream filestream=new FileInputStream(filePath);
            String ecode="utf-8";
            //得到输出流
            InputStreamReader readStream=new InputStreamReader(filestream,ecode);
            reader=new BufferedReader(readStream);
            
            String temp=null;
            int line=0;//行号
            while((temp=reader.readLine())!=null){
                line++;
                a.add(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (reader != null) {
                	reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return a;
	}
	
	public static Map<String,String> readFile2Map(String filePath){
		InputStreamReader  in = null;
        BufferedReader reader = null;
        Map<String,String> m = new HashMap<String,String>();
        try {
            //得到输入流
            FileInputStream filestream=new FileInputStream(filePath);
            String ecode="utf-8";
            //得到输出流
            InputStreamReader readStream=new InputStreamReader(filestream,ecode);
            reader=new BufferedReader(readStream);
            
            String temp=null;
            int line=0;//行号
            while((temp=reader.readLine())!=null){
                line++;
                m.put(temp, temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (reader != null) {
                	reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return m;
	}
}