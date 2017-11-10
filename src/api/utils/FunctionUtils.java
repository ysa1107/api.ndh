/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.utils;

import api.configuration.ConfigInfo;
import api.serviceUtils.JWTService;
import api.serviceUtils.UserService;
import com.kyt.framework.config.LogUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Y Sa
 */
public class FunctionUtils {

    private static final Logger logger = LogUtil.getLogger(FunctionUtils.class);

    public static boolean checkAccessToken(HttpServletRequest req, HttpServletResponse response, Map<String, String> params) {
        if (!ConfigInfo.IS_CHECK_TOKEN) {
            return true;
        }

        String url = req.getRequestURI();
        if (url.contains("/commons")) {
            return true;
        }

        String accessToken = req.getHeader("Authorization");
        if (StringUtils.isEmpty(accessToken)) {
            return false;
        }

        // for debuging
        if (!StringUtils.isEmpty(accessToken) && accessToken.equalsIgnoreCase(ConfigInfo.DEBUG_TOKEN)) {
            return true;
        }

        if (params.get("clientip").equals("14.169.191.142")) { //27.77.121.164
            logger.info("AccessToken = " + accessToken);
            JwtTokenResult tokenRes = JWTService.checkToken(accessToken);
            if (tokenRes == null || !tokenRes.status.equals(EJwtTokenStatusResult.SUCCESS)) {
                response.addHeader("FailToken", "401");
                return false;
            }

            if (ConfigInfo.IS_GET_USERID_FROM_TOKEN) {
                if (tokenRes.data != null && tokenRes.data.containsKey("deviceinfo")) {
                    String strDeviceInfo = tokenRes.data.get("deviceinfo");
                    DeviceInfoEnt deviceInfo = JSONUtil.DeSerialize(strDeviceInfo, DeviceInfoEnt.class);
                    if (deviceInfo != null) {
                        if (!FunctionUtils.IsNullOrEmpty(deviceInfo.getUsername())) {
                            params.put("username", deviceInfo.getUsername());
                            params.put("deviceId", deviceInfo.getDeviceId());
                            long uid = UserService.getInstance().getUserIdByUsername(deviceInfo.getUsername());
                            params.put("userId", String.valueOf(uid));
                        } else {
                            params.put("username", "");
                            params.put("userId", "0");
                            params.put("deviceId", "");
                        }
                    }
                }
            }
        } else {
            if (!TokenBC.checkAccessToken(accessToken)) {
                response.addHeader("FailToken", "401");
                return false;
            }

            if (ConfigInfo.IS_GET_USERID_FROM_TOKEN) {
                String action = params.get("action");
                if (!action.equals("login") && !action.equals("register")
                        && !action.equals("reset-password")) {
                    String strDeviceInfo = TokenBC.getDeviceInfo(accessToken);
                    DeviceInfoEnt deviceInfo = JSONUtil.DeSerialize(strDeviceInfo, DeviceInfoEnt.class);
                    if (deviceInfo != null) {
                        if (!FunctionUtils.IsNullOrEmpty(deviceInfo.getUsername())) {
                            params.put("username", deviceInfo.getUsername());
                            params.put("deviceId", deviceInfo.getDeviceId());
                            long uid = UserService.getInstance().getUserIdByUsername(deviceInfo.getUsername());
                            params.put("userId", String.valueOf(uid));
                        } else {
                            params.put("username", "");
                            params.put("userId", "0");
                            params.put("deviceId", "");
                        }
                    }
                }
            }           
        }
        return true;
    }

    

    public static List<Long> GetSubListLongIndex(List<Long> list, int from, int to) {
        int iStart = from;
        int iEnd = to;
        if (iEnd >= list.size()) {
            iEnd = list.size();
        }
        if (iStart >= list.size()) {
            iStart = list.size();
        }
        return list.subList(iStart, iEnd);
    }

    public static List<Long> GetSubListLong(List<Long> list, int pageIndex, int pageSize) {
        int iStart = (pageIndex - 1) * pageSize;
        int iEnd = (pageIndex * pageSize);
        if (iEnd >= list.size()) {
            iEnd = list.size();
        }
        if (iStart >= list.size()) {
            iStart = list.size();
        }
        return list.subList(iStart, iEnd);
    }

    public static List<String> GetSubListString(List<String> list, int pageIndex, int pageSize) {
        int iStart = (pageIndex - 1) * pageSize;
        int iEnd = (pageIndex * pageSize);
        if (iEnd >= list.size()) {
            iEnd = list.size();
        }
        if (iStart >= list.size()) {
            iStart = list.size();
        }
        return list.subList(iStart, iEnd);
    }

    public static List<Long> ConvertListStringToLong(List<String> list) {
        if (list != null && list.size() > 0) {
            List<Long> listLong = new ArrayList<Long>();
            for (String tmp : list) {
                long idConvert = ConvertUtils.toLong(tmp);
                if (idConvert > 0 && !listLong.contains(idConvert)) {
                    listLong.add(idConvert);
                }
            }
            return listLong;
        }
        return null;
    }

    public static void warmListRewriteEntity() {
        try {
            List<RewriteEntity> listRewriteEntity = ShopinessRewriteUtils.parseConfigRewrite(ConfigInfo.REWRITE_PATH);
            LocalCached.put(ConfigInfo.REWRITE_ENTITY_MEM_KEY, listRewriteEntity);
        } catch (Exception ex) {
            logger.error(LogUtil.stackTrace(ex));
        }
    }

    public static Map<String, String> collectParams(HttpServletRequest req) {
        Map<String, String> result = new HashMap<>();

        if (req == null || req.getParameterMap() == null || req.getParameterMap().isEmpty()) {
            return result;
        }

        String str = req.getQueryString();

        try {
            for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
                String name = entry.getKey();
                List<String> lstParamAlowGet = FunctionUtils.ParseStringToListString("subId,mainId,action,version");
                String[] values = entry.getValue();
                if (values.length > 0) {
                    if (req.getMethod().equals("POST")) {
                        if (lstParamAlowGet.contains(name)) {
                            result.put(name, values[0]);
                        } else if (!str.contains(name)) {
                            result.put(name, values[0]);
                        }
                    } else {
                        result.put(name, values[0]);
                    }
                }
            }

            if ("upload-avatar".equalsIgnoreCase(result.get("action"))) {
                processUploadFile(req, result, "user");
            }
//            // upload image for comment question (Tam thoi comment lai de xu ly sau -> ko duoc xoa)
//            if ("add-comment-question".equalsIgnoreCase(result.get("action"))) {
//                processUploadFile(req, result, "brand");
//            }
//            // upload image for comment answer
//            if ("add-comment-answer".equalsIgnoreCase(result.get("action"))) {
//                processUploadFile(req, result, "brand");
//            }

            if ("logout".equalsIgnoreCase(result.get("action"))
                    || "notify".equalsIgnoreCase(result.get("action"))) {
                String accessToken = req.getHeader("Authorization");

                if (!FunctionUtils.IsNullOrEmpty(accessToken)) {
                    result.put("accessToken", accessToken);
                }
            }

            result.put("method", req.getMethod());
            result.put("clientip", NetworkUtils.GetClientIp(req));
            result.put("X-Location", req.getHeader("X-Location"));

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    public static String downloadAvatar(String avatarUrl, String type) {
        logger.info("*****DOWNLOAD AVATAR-------avatarURL : " + avatarUrl + "---- type: " + type);
        String result = "";
        try {
            UploadUtil.UploadResultEnt rs = UploadUtil.downloadAvatar(type, avatarUrl);
            logger.info("*****DOWNLOAD AVATAR--UploadResultEnt : " + rs);
            if (rs.status_code == 0) {
                logger.info("*****DOWNLOAD AVATAR--UploadResultEnt--file_name : " + rs.file_name);
                result = rs.file_name;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    public static void processUploadFile(HttpServletRequest req, Map<String, String> result, String type) {
        try {
            logger.info("*****PROCESS UPLOAD FILE------HttpServletRequest: " + req + "--- Map result: " + result + "--- type: " + type);
            boolean isMultipart = ServletFileUpload.isMultipartContent(req);
            if (isMultipart) {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List items = null;
                try {
                    items = upload.parseRequest(req);
                } catch (Exception ex) {
                    System.out.println(LogUtil.stackTrace(ex));
                }
                Iterator iterator = items.iterator();
                while (iterator.hasNext()) {
                    FileItem item = (FileItem) iterator.next();
                    if (!item.isFormField()) {
                        String fieldName = item.getFieldName();
                        if ("fileUpload".equals(fieldName)) {
                            InputStream fileStream = item.getInputStream();
                            UploadUtil.UploadResultEnt rs = UploadUtil.uploadImage(fileStream, type, item.getName());
                            logger.info("**PRO***FileUpload------ UploadResult : " + rs);
                            if (rs.status_code == 0) {
                                logger.info("**PRO***FileUpload-------file_name : " + rs.file_name);
                                result.put("fileName", rs.file_name);
                            }
                        }
                    } else {
                        String fieldName = item.getFieldName();
                        if (fieldName.equals("fbAvatar")) {
                            UploadUtil.UploadResultEnt rs = UploadUtil.downloadAvatar(type, item.getString().replace(";and;", "&"));
                            logger.info("**PRO***fbAvatar------ UploadResult : " + rs);
                            if (rs.status_code == 0) {
                                logger.info("**PRO***fbAvatar-------file_name : " + rs.file_name);
                                result.put("fileName", rs.file_name);
                            }
                        } else {
                            result.put(fieldName, item.getString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static String getValueOfMap(Map<String, String> map, String key) {

        if (map == null || map.isEmpty()) {
            return "";
        }
        return map.containsKey(key) ? ConvertUtils.toString(map.get(key)) : "";
    }

    public static String URLDecodeUTF8(String string) {
        try {
            return URLDecoder.decode(string, "UTF-8");
        } catch (Exception ex) {
            logger.error(String.format("%s : %s", ex.getMessage(), string));
        }
        return null;
    }

    public static String URLEncodeUTF8(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (Exception ex) {
            logger.error(LogUtil.stackTrace(ex));
        }
        return null;
    }

    public static String ConvertToUTF8(String str) throws UnsupportedEncodingException {
        if (IsNullOrEmpty(str)) {
            return "";
        }
        return new String(str.getBytes("ISO-8859-1"), "UTF-8");
    }

    public static String UrlEncode(String url) {
        try {
            if (url == null || url.isEmpty()) {
                return "";
            }
            return URLEncoder.encode(url, "UTF-8");
        } catch (Exception ex) {
            logger.error(LogUtil.stackTrace(ex));
        }
        return "";
    }

    public static String UrlDecode(String url) {
        try {
            if (url == null || url.isEmpty()) {
                return "";
            }
            return URLDecoder.decode(url, "UTF-8");
        } catch (Exception ex) {
            logger.error(LogUtil.stackTrace(ex));
        }
        return "";
    }

    public static boolean IsNullOrEmpty(String str) {
//        return str == null || str.length() == 0 || "null".equalsIgnoreCase(str);
        return str == null || str.length() == 0;
    }

    public static String TimeToStringView(long inTime) {
        String strMinutes = (inTime / 60) >= 10 ? ConvertUtils.toString(inTime / 60) : "0" + ConvertUtils.toString(inTime / 60);
        String strSecond = (inTime % 60) >= 10 ? ConvertUtils.toString(inTime % 60) : "0" + ConvertUtils.toString(inTime % 60);
        return strMinutes + ":" + strSecond;
    }

    public static String DoMD5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return "";
    }

    public static String formatStringDesc(String str, int limitChar, boolean isSubSpace) {
        try {
            if (!str.isEmpty()) {
                str = StringEscapeUtils.unescapeHtml(str);
                str = str.trim();

                str = URLDecoder.decode(str, "UTF-8");

                if (limitChar <= 0) {
                    limitChar = 250;
                }
                if (str.length() > limitChar) {
                    str = str.substring(0, limitChar - 1);

                    if (isSubSpace == true && str.lastIndexOf(" ") != -1) {
                        str = str.substring(0, str.lastIndexOf(" "));
                    }
                }

                str = str.trim().replaceAll("(\\n)(<\\s*br[^>]*>)?{1,}", "<br />");
                str = StringEscapeUtils.unescapeHtml(str);

                return str;
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException ex) {
            logger.error(LogUtil.stackTrace(ex));
            return "";
        }
    }

    public static String toCamelCase(String data) {
        if (StringUtils.isEmpty(data)) {
            return "";
        }
        return WordUtils.capitalizeFully(data, new char[]{' ', '(', '"', '\'', ':', ';', '<', '>', '?', ',', '/', '\\', '&', '%', '$', '#', '@', '!'});
    }

    public static String decodeBase64(String text) {
        byte[] decodedByte = Base64.decodeBase64(text);
        return new String(decodedByte);
    }

    public static String encodeBase64(String text) {
        byte[] encodedByte = Base64.encodeBase64(text.getBytes());
        return new String(encodedByte);
    }

    public static Integer VersionCompare(String ver1, String ver2) {
        /*
         1 : Ver1 > Ver2
         0 : Ver1 = Ver2
         -1 : Ver1 < Ver2
         */
        String[] vals1 = ver1.split("\\.");
        String[] vals2 = ver2.split("\\.");
        int i = 0;
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(ConvertUtils.toInt(vals1[i], 0)).compareTo(Integer.valueOf(ConvertUtils.toInt(vals2[i], 0)));
            return Integer.signum(diff);
        } else {
            return Integer.signum(vals1.length - vals2.length);
        }
    }

    public static List<Long> ParseStringToListLong(String str) {
        List<Long> result = new ArrayList<>();
        if (IsNullOrEmpty(str)) {
            return result;
        }
        String[] arr = str.split(",");
        for (int i = 0; i < arr.length; ++i) {
            if (!IsNullOrEmpty(arr[i])) {
                result.add(ConvertUtils.toLong(arr[i]));
            }
        }
        return result;
    }

    public static String removeUnicodeCharacter(String str) {
        str = str.toLowerCase();
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "àáạảãâầấậẩẫăằắặẳẵ", "aaaaaaaaaaaaaaaaa");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "èéẹẻẽêềếệểễ", "eeeeeeeeeee");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "ùúụủũưừứựửữ", "uuuuuuuuuuu");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "ìíịỉĩ", "iiiii");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "òóọỏõôồốộổỗơờớợởỡ", "ooooooooooooooooo");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "ỳýỵỷỹ", "yyyyy");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "đ", "d");
        str = str.replaceAll("[^a-zA-Z0-9-_ ]*", "");
        str = str.replaceAll("[ ]{1,}", "-").replaceAll("[-]{1,}", "-");
        str = org.apache.commons.lang.StringUtils.remove(str, ",");

        return str;
    }

    public static String formatMetaTagKeyword(String str) {
        str = str.toLowerCase();
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "àáạảãâầấậẩẫăằắặẳẵ", "aaaaaaaaaaaaaaaaa");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "èéẹẻẽêềếệểễ", "eeeeeeeeeee");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "ùúụủũưừứựửữ", "uuuuuuuuuuu");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "ìíịỉĩ", "iiiii");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "òóọỏõôồốộổỗơờớợởỡ", "ooooooooooooooooo");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "ỳýỵỷỹ", "yyyyy");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "đ", "d");
        str = str.replaceAll("[^a-zA-Z0-9-_ ]*", "");
        str = org.apache.commons.lang.StringUtils.capitalize(str);
        return str;
    }

    public static String removeUnicode(String str) {
        str = str.toLowerCase();
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "àáạảãâầấậẩẫăằắặẳẵ", "aaaaaaaaaaaaaaaaa");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "èéẹẻẽêềếệểễ", "eeeeeeeeeee");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "ùúụủũưừứựửữ", "uuuuuuuuuuu");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "ìíịỉĩ", "iiiii");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "òóọỏõôồốộổỗơờớợởỡ", "ooooooooooooooooo");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "ỳýỵỷỹ", "yyyyy");
        str = org.apache.commons.lang.StringUtils.replaceChars(str, "đ", "d");
        return str;
    }

    public static List<String> ParseStringToListString(String str) {
        List<String> result = new ArrayList<>();
        if (IsNullOrEmpty(str)) {
            return result;
        }
        String[] arr = str.split(",");
        for (int i = 0; i < arr.length; ++i) {
            if (!IsNullOrEmpty(arr[i])) {
                result.add(ConvertUtils.toString(arr[i].trim()));
            }
        }
        return result;
    }

    public static List<Short> ParseStringToListShort(String str) {
        List<Short> result = new ArrayList<>();
        if (IsNullOrEmpty(str)) {
            return result;
        }
        String[] arr = str.split(":");
        for (int i = 0; i < arr.length; ++i) {
            if (!IsNullOrEmpty(arr[i])) {
                result.add(ConvertUtils.toShort(arr[i]));
            }
        }
        return result;
    }

    public static String GenStaticLink(String strSuffix, String strMidfix) {
        if (FunctionUtils.IsNullOrEmpty(strSuffix)) {
            return "";
        }
        return ConfigInfo.STATIC_URL + strMidfix + strSuffix;

//        if (isRunning3GViettel) {
//            return ConfigInfo.STATIC_URL_3GVIETTEL + strMidfix + strSuffix;
//        } else {
//            return ConfigInfo.STATIC_URL + strMidfix + strSuffix;
//        }
    }

    public static String ToStringView(long number) {
        return String.format("%,8d", number);
    }

    public static String toTop20Status(short thisPoisition, short lastPoisition) {
        if (lastPoisition == 0) {
            return "NEW";
        }
        if (thisPoisition == lastPoisition) {
            return "NONE";
        }
        if (thisPoisition < lastPoisition) {
            return "UP";
        }
        return "DOWN";
    }

    public static String restFulGetMethod(String _url) {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setConnectTimeout(5000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Get Response	
            InputStream is = connection.getInputStream();
            StringBuilder response;
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
                String line;
                response = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
            }
            return response.toString();
        } catch (IOException ex) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String sendHTTPGet(String url, String USER_AGENT) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            return "{}";
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
        return response.toString();
    }

    //huanlh
    public static String DateToStringView(long date) {
        SimpleDateFormat ad = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return ad.format(date);
    }

    public static String DateToSimpleStringView(long date) {
        SimpleDateFormat ad = new SimpleDateFormat("dd/MM/yyyy");
        return ad.format(date);
    }

    public static boolean checkExistStringIDs(String userIDs, long userId) {
        String uId = String.valueOf(userId);
        if (!FunctionUtils.IsNullOrEmpty(userIDs) && !FunctionUtils.IsNullOrEmpty(uId)) {
            String[] values = userIDs.split(",");

            if (Arrays.asList(values).contains(uId)) {
                return true;
            }
        }
        return false;
    }

    public static String addStringIDs(String IDs, long Id) {
        try {
            String strId = String.valueOf(Id);
            if (!FunctionUtils.IsNullOrEmpty(strId)) {
                if (FunctionUtils.IsNullOrEmpty(IDs)) {
                    return strId;
                }
                boolean check = checkExistStringIDs(IDs, Id);
                if (!check) {
                    if (IDs != null) {
                        IDs += "," + strId;
                    } else {
                        IDs += strId;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
            return IDs;
        }
        return IDs;
    }

    public static String removedStringIDs(String IDs, long Id) {
        String result = "";
        try {
            String strId = String.valueOf(Id);
            String[] values = IDs.split(",");
            if (!checkExistStringIDs(IDs, Id)) {
                if (IDs.charAt(0) == ',') {
                    IDs = IDs.substring(1);
                }
                return IDs;
            } else {
                if (values.length == 1) {
                    return "";
                }
                for (int i = 0; i < values.length; i++) {
                    String item = values[i];
                    if (!"".equals(item) && !strId.equals(item)) {
                        if (i == 0) {
                            result += item;
                        } else {
                            result += "," + item;
                        }
                        if (result.charAt(0) == ',') {
                            result = result.substring(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
            return IDs;
        }

        return result;
    }

    public static int countNumInStringIDs(String IDs) {
        int count = 0;
        if (!FunctionUtils.IsNullOrEmpty(IDs)) {
            String[] values = IDs.split(",");
            for (String value : values) {
                if (value != null && !"".equals(value)) {
                    count++;
                }
            }
        }
        return count;
    }

    public static String generateVoucherCode() {
        Date date = new Date();
        Long miliSec = date.getTime();

        String code = DoMD5(miliSec.toString());
        code = code.substring(code.length() - 8);

        return code.toUpperCase();
    }

    public static List<String> parseStringToListString(String str) {
        List<String> result = new ArrayList<>();
        if (IsNullOrEmpty(str)) {
            return result;
        }
        String[] arr = str.split(";");
        for (int i = 0; i < arr.length; ++i) {
            if (!IsNullOrEmpty(arr[i])) {
                result.add(ConvertUtils.toString(arr[i].trim()));
            }
        }
        return result;
    }

    public static String getNewestNotificationId(String notiIdStr) {
        String result = "";
        List<Long> lstNotiId = ParseStringToListLong(notiIdStr);
        if (lstNotiId != null && lstNotiId.size() > 0 && lstNotiId.size() > 100) {
            List<Long> newList = lstNotiId.subList(lstNotiId.size() - 100, lstNotiId.size());
            for (Long item : newList) {
                result += item + ",";
            }
            if (result.endsWith(",")) {
                result = result.substring(0, result.length() - 1);
            }
        } else {
            result = notiIdStr;
        }
        return result;
    }

    public static void main(String[] args) {

        System.out.println(generateVoucherCode());

    }
}
