/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.DeviceInfoEnt;

/**
 *
 * @author huanlh
 */
public interface UserDeviceIface {
    
    boolean updateDeviceInfo(DeviceInfoEnt info, String keyNotify);
    
}
