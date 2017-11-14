package api.configuration;

import com.kyt.framework.config.Config;
import com.kyt.framework.util.ConvertUtils;


public class ConfigInfo {

    private static final String API_SERVER = "api_server";
    public static final String PORT_LISTEN = Config.getParam(API_SERVER, "port_listen");
    public static final String HOST_LISTEN = Config.getParam(API_SERVER, "host_listen");
    public static final int MIN_THREAD = ConvertUtils.toInt(Config.getParam(API_SERVER, "min_pool"), 128);
    public static final int MAX_THREAD = ConvertUtils.toInt(Config.getParam(API_SERVER, "max_pool"), 256);
    public static String STATIC_URL = ConvertUtils.toString(Config.getParam(API_SERVER, "static_url"), "");
    public static final String REWRITE_PATH = Config.getParam("rewrite-path", "path");
    public static final String REWRITE_ENTITY_MEM_KEY = "rewrite_entity_key";
    public static final String ROOT_IMAGE_URL = "";
    public static final String PREFIX_USER= "";

    public static final boolean IS_CHECK_TOKEN = ConvertUtils.toBoolean(Config.getParam(API_SERVER,"is_check_token"));
    public static final String DEBUG_TOKEN = ConvertUtils.toString(Config.getParam(API_SERVER,"debug_token"));
    public static final long TIME_JWTTOKEN_EXPIRED = 6000;

    public static final String SECRET_KEY = ConvertUtils.toString(Config.getParam("settings","secrect_key"));

//    public static final String ROOT_IMAGE_URL = Config.getParam(SERVER_SHOPINESS, "url_image");
//    public static final String SERVER_UPLOAD_IMAGE = Config.getParam(SERVER_SHOPINESS, "url_upload_image");
//    public static final String SERVER_UPLOAD_IMAGE_PNG = Config.getParam(SERVER_SHOPINESS, "url_upload_image_png");
//    public static final String SERVER_DOWNLOAD_AVATAR = Config.getParam(SERVER_SHOPINESS, "url_download_avatar");
//    public static final String UPLOAD_APPID = Config.getParam(SERVER_SHOPINESS, "upload_id");
//    public static final String UPLOAD_SECRECT_KEY = Config.getParam(SERVER_SHOPINESS, "upload_secrect_key");

}

