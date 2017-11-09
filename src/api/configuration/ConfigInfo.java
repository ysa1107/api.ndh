package api.configuration;

import com.kyt.framework.config.Config;
import com.kyt.framework.util.ConvertUtils;


public class ConfigInfo {

    private static final String SERVER_SHOPINESS = "shopiness_server";
    public static final String PORT_LISTEN = Config.getParam(SERVER_SHOPINESS, "port_listen");
    public static final String HOST_LISTEN = Config.getParam(SERVER_SHOPINESS, "host_listen");
    public static final int MIN_THREAD = ConvertUtils.toInt(Config.getParam(SERVER_SHOPINESS, "min_pool"), 128);
    public static final int MAX_THREAD = ConvertUtils.toInt(Config.getParam(SERVER_SHOPINESS, "max_pool"), 256);
    public static String STATIC_URL = ConvertUtils.toString(Config.getParam(SERVER_SHOPINESS, "static_url"), "");
    public static String API_URL = ConvertUtils.toString(Config.getParam(SERVER_SHOPINESS, "api_url"), "");
    public static final String REWRITE_PATH = Config.getParam("rewrite-path", "path");
    public static final String REWRITE_ENTITY_MEM_KEY = "rewrite_entity_key";


//    public static final String ROOT_IMAGE_URL = Config.getParam(SERVER_SHOPINESS, "url_image");
//    public static final String SERVER_UPLOAD_IMAGE = Config.getParam(SERVER_SHOPINESS, "url_upload_image");
//    public static final String SERVER_UPLOAD_IMAGE_PNG = Config.getParam(SERVER_SHOPINESS, "url_upload_image_png");
//    public static final String SERVER_DOWNLOAD_AVATAR = Config.getParam(SERVER_SHOPINESS, "url_download_avatar");
//    public static final String UPLOAD_APPID = Config.getParam(SERVER_SHOPINESS, "upload_id");
//    public static final String UPLOAD_SECRECT_KEY = Config.getParam(SERVER_SHOPINESS, "upload_secrect_key");
    

    
    //huanlh
//    public static final boolean IS_CHECK_TOKEN = ConvertUtils.toBoolean(Config.getParam(SERVER_SHOPINESS, "is_check_token"), false);  
//    public static final boolean IS_GET_USERID_FROM_TOKEN = ConvertUtils.toBoolean(Config.getParam(SERVER_SHOPINESS, "is_get_userid_from_token"), true); 
//    public static final boolean IS_TEST_NOTIFICATION = ConvertUtils.toBoolean(Config.getParam(SERVER_SHOPINESS, "is_test_notification"), false);  
//    public static final String DEBUG_TOKEN = ConvertUtils.toString(Config.getParam(SERVER_SHOPINESS, "debug_token"), "");      
//    public static final String SERVER_USER_DEAL = "deal_user_server";    
//    public static final String SERVER_BRAND_THRIFT = "thrift_brand_server";    
//    public static final String SERVER_CASHBACK_THRIFT = "shop_cashback_server";
//    public static final String SERVER_SEARCH_THRIFT = "shop_search_server";
//    public static final String URL_INSERT_NOTIF = "http://notify.nhaccuatui.com/api/device-insert";

}

