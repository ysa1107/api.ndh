/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.JsonResultEnt;
import com.nct.shop.thrift.brand.models.TAreaFilter;
import com.nct.shop.thrift.brand.models.TAreaListResult;

/**
 *
 * @author trailn
 */
public interface AreaIface {

//    JsonResultEnt getSongByKey(String songKey);
//
//    JsonResultEnt getSongByKeys(List<String> keys);
    JsonResultEnt getAreas(TAreaFilter filter, boolean isFull);
    
    TAreaListResult getAreas(TAreaFilter filter);
}
