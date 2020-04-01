package com.ccx.md5.file;

import java.util.List;
import java.util.Map;

import com.ccx.md5.MD5Util;

public class DectyptTwoWordsNamesMd5Runner implements Runnable{
	private List<String> subedTwoWordsNames;
	private Map<String,String> md5Names;
	public  DectyptTwoWordsNamesMd5Runner(List<String> subedTwoWordsNames,List<String> hanziS,Map<String,String> md5Names) {
		this.subedTwoWordsNames = subedTwoWordsNames;
		this.md5Names = md5Names;
	}
	@Override
	public void run() {
//		System.out.println(Thread.currentThread().getId()+"----"+subedTwoWordsNames.size());
		for(String twn:subedTwoWordsNames) {
				String tmp = MD5Util.MD5(twn).toUpperCase();
				if(md5Names.containsKey(tmp)) {
					System.out.println(tmp+"@"+twn);
				}else {
//					System.out.println(tmp+"#"+twn+hanzi);
				}
		}
//		System.out.println(Thread.currentThread().getId()+"-----end");
	}
	
}