package com.suhane.gridbox.common;

/**
 * Created by shashanksuhane on 03/04/18.
 */
public class Constants {

    public static boolean IS_LOCAL = true;  //false when using server api

    public static final String BASE_URL = "http://your.base.url";

    public static final String GET_QUERY = "/api/item/get";
    public static final String POST_QUERY = "/api/item/post";

    public static final int CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB

    public static final String ITEMS_JSON_FILE_NAME = "items.json";

}
