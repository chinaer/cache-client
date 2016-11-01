package com.nonobank.architecture.enumeration;

import redis.clients.jedis.BinaryClient;

/**
 * Created by geyingchao on 16/10/31.
 */
public enum ListPosition {
    BEFORE,AFTER;

    public  BinaryClient.LIST_POSITION warp(){
       if(ListPosition.BEFORE.equals(this)){
          return  BinaryClient.LIST_POSITION.BEFORE;
       }
        return BinaryClient.LIST_POSITION.AFTER;
    }
}
