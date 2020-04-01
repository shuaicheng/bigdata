package com.ccx.string;

import java.util.ArrayList;
import java.util.List;

public class Test {
	/*static String a = "aaa";
	static {
		System.out.println("Exception OOOOO!!!!");
		if(a.equals("aaa")) {
			System.out.println("Exception OOOOO!!!!");
//			System.exit(0);
		}
	}*/
	public static void main(String ap[]) {
		List<String> a = new ArrayList<String>();
		a.add("111");
		a.add("222");
		a.add("333");
		a.add("444");
		a.add("555");
		System.out.println(a.subList(1, 3));
		System.out.println(a.subList(3, 5));
	}
}
