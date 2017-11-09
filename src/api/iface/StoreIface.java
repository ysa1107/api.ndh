/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.JsonResultEnt;
import com.nct.shop.thrift.brand.models.TStore;
import com.nct.shop.thrift.brand.models.TStoreFilter;
import java.util.List;

/**
 *
 * @author trailn
 */
public interface StoreIface {

    JsonResultEnt getStores(TStoreFilter filter, boolean isFull);
    
    List<TStore> getMulti(List<Long> ids);
    
    TStore getStore(long id);
    
}
