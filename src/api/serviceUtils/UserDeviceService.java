/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.DeviceInfoEnt;
import api.iface.UserDeviceIface;
import api.utils.FunctionUtils;
import com.nct.shop.thrift.deal.client.TUserDeviceClient;
import com.nct.shop.thrift.deal.models.LoginStatus;
import com.nct.shop.thrift.deal.models.TUserDevice;
import com.nct.shop.thrift.deal.models.TUserDeviceResult;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.util.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author huanlh
 */
public class UserDeviceService implements UserDeviceIface{
    private static final Logger logger = LogUtil.getLogger(UserDeviceService.class);
    public static UserDeviceService instance = null;

    public static UserDeviceService getInstance() {
        if (instance == null) {
            instance = new UserDeviceService();
        }
        return instance;
    }

    public UserDeviceService() {
    }

    @Override
    public synchronized boolean updateDeviceInfo(DeviceInfoEnt info, String keyNotify) {
        boolean result = false;

        try{
            TUserDeviceClient client = TUserDeviceClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            TUserDeviceResult dRS = client.getUserDevice(info.getDeviceId());
            TUserDevice device = null;
            if (dRS!=null && dRS.getValue()!=null) {
                device = dRS.getValue();
            }else{
                device = new TUserDevice();
                device.setDeviceId(info.getDeviceId());
            }
            device.setUsername(info.getUsername());
            device.setDeviceName(StringUtils.urlEncode(info.getDeviceName()));
            device.setOsName(info.getOsName());
            device.setOsVersion(info.getOsVersion());
            device.setAppVersion(info.getAppVersion());
            device.setLoginStatus(LoginStatus.LOGGED_IN);

            if(!FunctionUtils.IsNullOrEmpty(keyNotify)){
                device.setKeyNotify(keyNotify);
            }

            if (dRS!=null && dRS.getValue()!=null) {
                result = client.updateUserDevice(device);
            }else{
                TUserDeviceResult insertRS = client.insertUserDevice(device);
                if (insertRS!=null && insertRS.getValue()!=null){
                    result = true;
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        return result;
    }
    
}
