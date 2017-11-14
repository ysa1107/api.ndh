/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.utils;

import api.configuration.ConfigInfo;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

import com.kyt.framework.config.LogUtil;
import com.kyt.framework.util.DateTimeUtils;
import com.kyt.framework.util.JSONUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Y Sa
 */
public class UploadUtil {

    private static final Logger logger = LogUtil.getLogger(UploadUtil.class);

    public class UploadResultEnt {

        public String message;
        public int status_code;
        public String status;
        public String file_name;
    }

    public static final short SUCCESS = 0;
    /*Media return Error code*/
    public static final short ERROR_MEDIA_UNKNOWN = -1;
    public static final short ERROR_MEDIA_WRONG_EXTENTION = -2;
    public static final short ERROR_MEDIA_EXCEED_SIZE = -3;
    public static final short ERROR_MEDIA_NO_FILE_SUBMITTED = -4;
    public static final short ERROR_MEDIA_NOT_A_MULTIPART_SUBMIT = -5;
    /*Image return Error code*/
    public static final short ERROR_IMAGE_INVALID_PARAM_TYPE = 1;
    public static final short ERROR_IMAGE_UNSUPPORT_TYPE = 2;
    public static final short ERROR_IMAGE_INVALID_MULTIPART = 3;
    public static final short ERROR_IMAGE_FILE_EMPTY = 4;
    public static final short ERROR_IMAGE_FILE_OVER_SIZE = 5;
    public static final short ERROR_IMAGE_SYSTEM_ERROR = 9;
    public static final short ERROR_IMAGE_INVALID_TYPE = 11;
    public static final short ERROR_IMAGE_INVALID_SIZE = 12;
    public static final short ERROR_IMAGE_INVALID_FORMAT = 13;

    public static UploadResultEnt uploadImage(InputStream fileStream, String type, String fileName) {
        try {
            if (fileStream != null && type != null && fileName != null) {
                String responseMsg = UploadImageToServer(fileStream, type, fileName);
                UploadResultEnt uploadResult = JSONUtil.DeSerialize(responseMsg, UploadResultEnt.class);
                if (uploadResult != null) {
                    return uploadResult;
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return null;
    }

    private static String DoMD5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return "";
    }

    private static String UploadImageToServer(InputStream fileInputStream, String type, String fileName) {
        try {
//            String serverUpload = ConfigInfo.SERVER_UPLOAD_IMAGE;
//            if (fileName.toLowerCase().endsWith(".png")) {
//                serverUpload = ConfigInfo.SERVER_UPLOAD_IMAGE_PNG;
//            }
//            Long time = DateTimeUtils.getMilisecondsNow();
//            String token = DoMD5(ConfigInfo.UPLOAD_APPID + ConfigInfo.UPLOAD_SECRECT_KEY + time);
//            String urlUpload = serverUpload + "?type=" + type + "&appId=" + ConfigInfo.UPLOAD_APPID + "&token=" + token + "&time=" + time;

            //return UploadToURL(urlUpload, fileInputStream, fileName);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return "";
    }

    public static UploadResultEnt downloadAvatar(String type, String imageUrl) {
        try {
            if (type != null && imageUrl != null) {
                String responseMsg = downloadAvatarFromImageUrl(type, imageUrl);
                UploadResultEnt uploadResult = JSONUtil.DeSerialize(responseMsg, UploadResultEnt.class);
                if (uploadResult != null) {
                    return uploadResult;
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return null;
    }

    public static String downloadAvatarFromImageUrl(String type, String imageUrl) {
        try {
//            String serverUpload = ConfigInfo.SERVER_DOWNLOAD_AVATAR;
//            Long time = DateTimeUtils.getMilisecondsNow();
//            String token = DoMD5(ConfigInfo.UPLOAD_APPID + ConfigInfo.UPLOAD_SECRECT_KEY + time);
//
//            serverUpload += "?imageUrl=" + URLEncoder.encode(imageUrl, "UTF-8")
//                    + "&type=" + type
//                    + "&appId=" + ConfigInfo.UPLOAD_APPID
//                    + "&time=" + time
//                    + "&token=" + token;
//
//            return UploadToURL(serverUpload);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return "";
    }

    private static String UploadToURL(String httpUrl) {
        String responseMessage = "";
        HttpURLConnection conn;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            URL url = new URL(httpUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("fileupload", "abc.jpg");

//            int bytesAvailable = fileInputStream.available();
//            String message = "Content-Disposition: form-data; name=\"file1\"; filename=\"" + fileName + "\"" + lineEnd;
//            conn.setRequestProperty("Content-Length", String.valueOf((message.length() + bytesAvailable)));
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);

//            dos.writeBytes(message);
            dos.writeBytes(lineEnd);

//            int maxBufferSize = 1 * 1024 * 1024;
//            bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
//            byte[] buffer = new byte[bytesAvailable];
//            int bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
//            while (bytesRead > 0) {
//                dos.write(buffer, 0, bytesAvailable);
//                bytesAvailable = fileInputStream.available();
//                bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
//                bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
//            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

//            fileInputStream.close();
            dos.flush();
            dos.close();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = rd.readLine()) != null) {
                responseMessage += line + lineEnd;
            }
            rd.close();
        } catch (Exception ex) {
            logger.error(LogUtil.stackTrace(ex));
        }
        System.out.println(responseMessage);

        return responseMessage;
    }

    private static String sendPOSTToServer(String url, String urlParameters) throws Exception {

        URL obj = new URL(URLEncoder.encode(url));
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        logger.info("\nSending 'POST' request to URL : " + url);
        logger.info("Post parameters : " + urlParameters);
        logger.info("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        logger.info(response.toString());

        return response.toString();
    }

//    public static String UploadMediaToServer(InputStream fileInputStream, String fileName) {
//        try {
//            Long time = DateTimeUtils.getMilisecondsNow();
//            String token = DoMD5(ConfigInfo.UPLOAD_APPID + ConfigInfo.UPLOAD_SECRECKEY + time);
//            String urlUpload = ConfigInfo.SERVER_UPLOAD_MEDIA + "?appId=" + ConfigInfo.UPLOAD_APPID + "&token=" + token + "&time=" + time;
//
//            return UploadToURL(urlUpload, fileInputStream, fileName);
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//        }
//        return "";
//    }
    private static String UploadToURL(String httpUrl, InputStream fileInputStream, String fileName) {
        String responseMessage = "";
        HttpURLConnection conn;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            URL url = new URL(httpUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("fileupload", fileName);

            int bytesAvailable = fileInputStream.available();
            String message = "Content-Disposition: form-data; name=\"file1\"; filename=\"" + fileName + "\"" + lineEnd;
            conn.setRequestProperty("Content-Length", String.valueOf((message.length() + bytesAvailable)));

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes(message);
            dos.writeBytes(lineEnd);

            int maxBufferSize = 1 * 1024 * 1024;
            bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bytesAvailable];
            int bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bytesAvailable);
                bytesAvailable = fileInputStream.available();
                bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            fileInputStream.close();
            dos.flush();
            dos.close();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = rd.readLine()) != null) {
                responseMessage += line + lineEnd;
            }
            rd.close();
        } catch (Exception ex) {
            logger.error(LogUtil.stackTrace(ex));
        }
        return responseMessage;
    }

    private static InputStream readFileToStream(String fileURL) {
        InputStream is = null;
        if (StringUtils.isEmpty(fileURL)) {
            return is;
        }
        try {
            is = new FileInputStream(fileURL);
            // is.close();
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return is;
    }
}
