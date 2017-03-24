package com.nonobank.architecture.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by geyingchao on 16/10/31.
 */
public class Test {

    public static final int LENGTH_MAX=1000;

    public static void main(String[] args) {
        collectData("172.16.3.67", 6479, 1000, "/Users/geyingchao/pre-forever-key.txt");
    }

    public static void collectData(String host, int port, int count, String path) {
        System.out.println("start to collect host:" + host + " port:" + port);
        System.out.println("every scan count:" + count);
        long start = System.currentTimeMillis();
        Jedis jedis = new Jedis(host, port);
        ArrayList<String> list = new ArrayList<>();
        ScanParams params = new ScanParams();
        params.match("*");
        params.count(count);
        ScanResult<String> scanResult = jedis.scan("0", params);
        List<String> keys = scanResult.getResult();
        String nextCursor = scanResult.getStringCursor();
        int counter = 0;
        while (true) {
            System.out.println("第" + (++counter) + "次扫描,扫描到:" + keys.size() + "个");
            int size = 0;
            for (String key : keys) {
                if (jedis.ttl(key) == -1) {
                    size++;
                    list.add(key);
                }
            }
            System.out.println("第" + (counter) + "次添加完毕,添加了:" + size + "个");
            if (nextCursor.equals("0")) {
                break;
            }
            if(list.size()>LENGTH_MAX){
                printToTxt(list,path);
                list.clear();
            }
            scanResult = jedis.scan(nextCursor, params);
            nextCursor = scanResult.getStringCursor();
            keys = scanResult.getResult();
        }
        long startPath = System.currentTimeMillis();
        System.out.println("collect end spend:" + (startPath - start));
        printToTxt(list, path);
        System.out.println("success! total spend:" + (System.currentTimeMillis() - start));
    }


    public static void printToTxt(ArrayList<String> outList, String path) {
        System.out.println("start to save to file path:" + path);
        long start = System.currentTimeMillis();
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(path, true);
            for (String Str : outList) {
                writer.write((Str + "\n"));

            }
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("collect to file:" + path +
                    "   spend:" + (System.currentTimeMillis() - start) +
                    "   keys:" + outList.size()
            );
        }


    }
