package com.ccx.md5.file;

import java.util.List;
import java.util.Map;

import com.ccx.md5.MD5Util;

public class Md5Runner implements Runnable{
	private List<String> subedTwoWordsNames;
	private List<String> hanziS;
	private Map<String,String> md5Names;
	public  Md5Runner(List<String> subedTwoWordsNames,List<String> hanziS,Map<String,String> md5Names) {
		this.subedTwoWordsNames = subedTwoWordsNames;
		this.hanziS = hanziS;
		this.md5Names = md5Names;
	}
	@Override
	public void run() {
		for(String twn:subedTwoWordsNames) {
			for(String hanzi:hanziS) {
				String tmp = MD5Util.MD5(twn+hanzi).toUpperCase();
				if(md5Names.containsKey(tmp)) {
					System.out.println(tmp+"@"+twn+hanzi);
				}else {
//					System.out.println(tmp+"#"+twn+hanzi);
				}
			}
		}
	}
	
}