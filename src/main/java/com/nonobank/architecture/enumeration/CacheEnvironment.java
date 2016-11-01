package com.nonobank.architecture.enumeration;

public enum CacheEnvironment {
	
	USER("user"),PROD("prod"),TRD("trd"),INVT("invt"),
	PAY("pay"),ACC("acc"),MKT("mkt"),LOAN("loan"),INFRA("infra"),DEFAULT("default");
	
	private String value;
	
	private CacheEnvironment(String value){
		this.value=value;
	}
	
	public String value(){
		return value;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	public String encode(String key){
		return value+":"+key;
	}
	
	public String decode(String key){
		return key.substring(key.indexOf(":")+1);
	}
	
	public static CacheEnvironment env(String value){
		value=value.toLowerCase();
		switch(value){
		case "user":
			return CacheEnvironment.USER;
		case "prod":
			return CacheEnvironment.PROD;
		case "trd":
			return CacheEnvironment.TRD;
		case "invt":
			return CacheEnvironment.INVT;
		case "pay":
			return CacheEnvironment.PAY;
		case "acc":
			return CacheEnvironment.ACC;
		case "mkt":
			return CacheEnvironment.MKT;
		case "loan":
			return CacheEnvironment.LOAN;
		case "infra":
			return CacheEnvironment.INFRA;
		default :
			 return CacheEnvironment.DEFAULT;
		}
	}
}
