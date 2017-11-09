/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.EventDealListEnt;
import api.entities.JsonResultEnt;
import com.nct.shop.thrift.deal.models.TEventDealFilter;
import com.nct.shop.thrift.deal.models.TEventDealListResult;
import com.nct.shop.thrift.deal.models.TEventUserFilter;

/**
 *
 * @author huanlh
 */
public interface EventIface {
    
    JsonResultEnt getEventDeals(TEventDealFilter filter, boolean isFull);
    
    JsonResultEnt getEventDeal(long id,long userId, String deviceId);
    
    JsonResultEnt registrationEventUser(long eventId, long userId, String deviceId);
    
    JsonResultEnt getEventUsers(TEventUserFilter filter, boolean isFull); 
    
    TEventDealListResult getEventDeals(TEventDealFilter filter);
    
    EventDealListEnt getListEventDeal(TEventDealFilter filter, boolean isFull);
//    JsonResultEnt searchDeals(TDealFilter filter, boolean isFull, long userId);
//    JsonResultEnt getDealOfBrand(long brandId, long userId);
//    
//    JsonResultEnt saveDeal(long dealId, long userId, boolean isSave);
//    
//    JsonResultEnt getVoucherCode(long dealId, String username, String type);
//    
//    JsonResultEnt viewDeal(long dealId);
//    
//    JsonResultEnt getDealByKeyCode(String keyCode); 
//    
//    List<Long> getDealAreas(long dealId); 
//    
//    List<Long> getDealsOfUser(long userId); 
//    
//    Map<String, List<Long>> getMapAreaOfDeal(List<Long> lstDealId); 
    
}
