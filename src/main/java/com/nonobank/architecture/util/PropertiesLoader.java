package com.nonobank.architecture.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class PropertiesLoader {
	
	private static Logger log=LoggerFactory.getLogger(PropertiesLoader.class);
	
	public static Properties load(String path,boolean byPath){
		if(!Strings.isNullOrEmpty(path)){
			if(byPath){
			//load by file path
			File file=new File(path);
			if(file.isFile()){
				try {
					Properties pros=new Properties();
					InputStream in=new FileInputStream(file);
					pros.load(in);
					in.close();
					return pros;
				} catch (FileNotFoundException e) {
					log.error("load file {} error,file is not exit!",path);
				} catch (IOException e) {
					log.error("properties file {} formate error,please check !"+path);
				}
			}
		}else{
			//load by class path
			InputStream in=PropertiesLoader.class.getClassLoader().getResourceAsStream(path);
			Properties pros=new Properties();
			try {
				pros.load(in);
				in.close();
				return pros;
			} catch (Exception e) {
				log.error("properties file {} formate error,please check !",path);
			}
		}
		}
		return null;
	}
	
}
