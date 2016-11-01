package com.nonobank.architecture.cache;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPoolConfig;

import com.nonobank.architecture.enumeration.CacheEnvironment;
import com.nonobank.architecture.util.PropertiesLoader;

public final class CacheConfig {
	
	private static final String DEFAULT_FILE_PATH="/JAVA_FILES/cacheclient-service/conf/app-codis.properties";
	
	private static final String DEFAULT_CLASS_PATH="conf/app-codis.properties";
	
	private static final  Integer DEFAULT_MAX_TOTAL=1000;
	
	private static final  Integer DEFAULT_MAX_IDLE=1000;
	
	private static final Integer DEFAULT_MIN_IDLE=0;

	private static final Integer DEFAULT_MAXWAIT_MILLIS=20000;
	
	private static final String DEFAULT_ZKADDRESSANDPORT="172.16.4.51:2181,172.16.4.52:2181,172.16.4.53:2181";
	
	private static final String DEFAULT_ZKPROXYDIR="/zk/codis/db_nonobank/proxy";
	
	private static final Integer DEFAULT_ZKSESSIONTIMEOUTMS=30000;
	
	private static final String  DEFAULT_ENVIRONMENT=CacheEnvironment.DEFAULT.value();
	
	private static final boolean DEFAULT_DEBUG=false;
	
	private int maxTotal=DEFAULT_MAX_TOTAL;
	
	private int maxIdle=DEFAULT_MAX_IDLE; 
	
	private int minIdle=DEFAULT_MIN_IDLE;
	
	private int maxWaitMillis=DEFAULT_MAXWAIT_MILLIS;
	
	private String zkAddressAndPort=DEFAULT_ZKADDRESSANDPORT;
	
	private int zkSessionTimeOutMs=DEFAULT_ZKSESSIONTIMEOUTMS;
	
	private String zkProxyDir=DEFAULT_ZKPROXYDIR;
	
	private boolean debug=DEFAULT_DEBUG;
	
	private String envrionment=DEFAULT_ENVIRONMENT;
	
	private static Logger log=LoggerFactory.getLogger(CacheConfig.class);
	
	public CacheConfig(){
		//如果从外部配置加载失败，启用默认配置
//		if(!tryInit()){
//			this.maxIdle=DEFAULT_MAX_IDLE;
//			this.maxTotal=DEFAULT_MAX_TOTAL;
//			this.minIdle=DEFAULT_MIN_IDLE;
//			this.maxWaitMillis=DEFAULT_MAXWAIT_MILLIS;
//			this.zkAddressAndPort=DEFAULT_ZKADDRESSANDPORT;
//			this.zkSessionTimeOutMs=DEFAULT_ZKSESSIONTIMEOUTMS;
//			this.zkProxyDir=DEFAULT_ZKPROXYDIR;
//			this.debug=DEFAULT_DEBUG;
//			this.envrionment=DEFAULT_ENVIRONMENT;
//		}
	}
	
	@SuppressWarnings("unused")
	private boolean tryInit(){
		Properties pros=PropertiesLoader.load(DEFAULT_FILE_PATH, true);
		if(pros==null){
			pros=PropertiesLoader.load(DEFAULT_CLASS_PATH, false);
		}
		if(pros!=null){
			try{
			this.maxIdle=Integer.valueOf(pros.getProperty("codis.pool.maxIdle"));
			this.maxTotal=Integer.valueOf(pros.getProperty("codis.pool.maxTotal"));
			this.minIdle=Integer.valueOf(pros.getProperty("codis.pool.minIdle"));
			this.maxWaitMillis=Integer.valueOf(pros.getProperty("codis.pool.maxWaitMillis"));
			this.zkAddressAndPort=pros.getProperty("codis.zk.zkAddressAndPort");
			this.zkSessionTimeOutMs=Integer.valueOf(pros.getProperty("codis.zk.zkSessionTimeOutMs"));
			this.zkProxyDir=pros.getProperty("codis.zk.zkProxyDir");
			this.debug=Boolean.getBoolean(pros.getProperty("codis.debug"));
			this.envrionment=pros.getProperty("codis.envrionment");
			
			return true;
			}catch(Exception e){
				e.printStackTrace();
				log.info("properties file load error! cann`t init cache client!|properties file path:{} or {}",DEFAULT_CLASS_PATH,DEFAULT_FILE_PATH);
				log.info("trying to init cache client by default values!");
			}
		}
		return false;
	}
	
	protected JedisPoolConfig CacheConfig2JedisPoolConfig(){
		JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
		jedisPoolConfig.setTestWhileIdle(true);                   //jodis default
		jedisPoolConfig.setMinEvictableIdleTimeMillis(60000);	  //jodis default
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);  //jodis default
		jedisPoolConfig.setNumTestsPerEvictionRun(-1);            //jodis default
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMaxTotal(maxTotal);
		jedisPoolConfig.setMinIdle(minIdle);
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		
		return jedisPoolConfig;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(int maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public String getZkAddressAndPort() {
		return zkAddressAndPort;
	}

	public void setZkAddressAndPort(String zkAddressAndPort) {
		this.zkAddressAndPort = zkAddressAndPort;
	}

	public int getZkSessionTimeOutMs() {
		return zkSessionTimeOutMs;
	}

	public void setZkSessionTimeOutMs(int zkSessionTimeOutMs) {
		this.zkSessionTimeOutMs = zkSessionTimeOutMs;
	}

	public String getZkProxyDir() {
		return zkProxyDir;
	}

	public void setZkProxyDir(String zkProxyDir) {
		this.zkProxyDir = zkProxyDir;
	}

	public boolean getDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getEnvrionment() {
		return envrionment;
	}

	public void setEnvrionment(String envrionment) {
		this.envrionment = envrionment;
	}
	
}
