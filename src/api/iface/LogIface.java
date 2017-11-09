/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.JsonResultEnt;
import java.util.List;

/**
 *
 * @author liempt
 */
public interface LogIface {

    JsonResultEnt logCacheSync(String itemKey, String type, String actType);

    JsonResultEnt logViewed(String itemKey, String type, String cate);

    JsonResultEnt logShowcase(String itemKey, String type);

    JsonResultEnt setStreamError(List<String> itemIds, String type, String typeError, String errorText, String clientIP);

    JsonResultEnt logTimeViewed(List<String> itemIds, String type, int time, String cate, String clientIP);

    JsonResultEnt logFirstUse();

    JsonResultEnt PushMessage(String osPush, String dataType, String dataMessage);

    JsonResultEnt logAllMgs(String DeviceID, String OsName, String OsVersion, String AppName, String AppVersion, String DeviceName, String Network, String UserID, String MethodID, String Type, String Result, String AccountType, String UrlStream, String HttpCode, String Key, String Buffer, String CurrentTime, String Linkdown, String Others, String clientIp);
}
