package com.ccx.md5.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterDecryptedNames {
	public static void main(String[] args) {
		String decrypted_path = "D:/md5/part-00000-twoname.txt";
		String original_path = "D:/md5/encrypted_names.txt";
		String final_dec_file = "D:/md5/decrypted_final.txt";
		Map<String,String> d_names_map = readFile2Map(decrypted_path);
		List<String> original_encrypted_names = ReadTwoWordsName.readFile(original_path);
		
		Map<String,String> am = new HashMap<>();
		
		
		BufferedWriter out = null ;  
        try  {  
        	int u = 0;
        	int a = 0;
            out = new  BufferedWriter( new  OutputStreamWriter(new  FileOutputStream(final_dec_file,  true )));  
            for(String oen:original_encrypted_names) {
    			String final_str = "";
    			if(d_names_map.containsKey(oen)) {
    				if(!am.containsKey(oen)) {
    					a ++;
    					am.put(oen,"");
    				}
    				System.out.println(oen+"\t"+d_names_map.get(oen));
    				final_str = oen+"\t"+d_names_map.get(oen);
    			}else {
    				final_str = oen+"\tNaN";
    				u++;
    			}
    			out.write(final_str+"\r\n");  
    		}
            System.out.println("未解出来的个数："+u);
            System.out.println("解出来的个数："+a);
        } catch  (Exception e) {  
            e.printStackTrace();
        } finally  {  
            try  {  
                out.close();  
            } catch  (IOException e) {  
                e.printStackTrace();  
            }  
        }  
		
		
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
                String[] ts = temp.split("@");
                if(ts.length==2&&!m.containsKey(ts[0])) {
                	m.put(ts[0], ts[1]);
                	line++;
                }else {
//                	m.put(temp, "NAN");
                }
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
