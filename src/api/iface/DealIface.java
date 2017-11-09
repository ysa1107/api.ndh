/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.DealFavoriteListEnt;
import api.entities.DealListEnt;
import api.entities.DealViewEnt;
import api.entities.JsonResultEnt;
import com.nct.shop.thrift.deal.models.TDeal;
import com.nct.shop.thrift.deal.models.TDealFilter;
import com.nct.shop.thrift.deal.models.TDealListResult;
import com.nct.shop.thrift.deal.models.TDealResult;
import com.nct.shop.thrift.deal.models.TEventDealFilter;
import java.util.List;
import java.util.Map;

/**
 *
 * @author huanlh
 */
public interface DealIface {
    
    JsonResultEnt getDeals(TDealFilter filter, boolean isFull, long userId);
    
//    JsonResultEnt searchDeals(TDealFilter filter, boolean isFull, long userId);
    
    JsonResultEnt getDeal(long dealId, long userId);
    
    JsonResultEnt getDealOfBrand(long brandId, long userId);
    
    JsonResultEnt saveDeal(long dealId, long userId, boolean isSave);
    
    JsonResultEnt getVoucherCode(long dealId, String username, String type);
    
    JsonResultEnt viewDeal(long dealId);
    
    JsonResultEnt getDealByKeyCode(String keyCode); 
    
    List<Long> getDealAreas(long dealId); 
    
    List<Long> getDealsOfUser(long userId); 
    
    Map<String, List<Long>> getMapAreaOfDeal(List<Long> lstDealId); 
    
    TDealListResult getDeals(TDealFilter filter);
    
    TDealResult getDeal(long dealId);
    
    DealFavoriteListEnt getListFavoriteDeal(TDealFilter filter, boolean isFull);
    
    DealListEnt getListOtherDeal(TDealFilter filter, boolean isFull, int pageIndex, int pageSize);
    
    List<TDeal> getMulti(List<Long> ids);
    
}
