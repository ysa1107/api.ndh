/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.BrandViewEnt;
import api.entities.DealViewEnt;
import api.entities.JsonResultEnt;
import api.entities.StoreViewEnt;
import com.nct.shop.thrift.brand.models.TBrand;
import com.nct.shop.thrift.brand.models.TBrandFilter;
import com.nct.shop.thrift.brand.models.TBrandListResult;
import com.nct.shop.thrift.brand.models.TStoreFilter;
import com.nct.shop.thrift.deal.models.TDealFilter;
import java.util.List;
import java.util.Map;

/**
 *
 * @author liemtpt
 */
public interface BrandIface {

    JsonResultEnt getBrands(TBrandFilter filter, boolean isFull, boolean isSearch);
    
    JsonResultEnt addOrRemovedFavoriteBrand(long brandId, long userId, boolean isFavorite);
    
    BrandViewEnt getBrandFromDeal(TBrandFilter filter, boolean isFull);
    
    List<StoreViewEnt> getStoresFromDeal(TStoreFilter filter, boolean isFull);
    
    BrandViewEnt getBrandUser(long brandId, long userId, boolean isFull);
    
    List<DealViewEnt> getDealsByBrandUser(long brandId, long userId, boolean isFull);
    
    List<DealViewEnt> getDealsOfBrand(TDealFilter filter, boolean isFull, long userId);
    
    TBrandListResult getBrands(TBrandFilter filter);
    
    boolean checkBrandUser(long brandId, long userId);
    
    Map<String, List<Long>> getMapDealOfBrand(List<Long> lstBrandId);
    
    Map<Long, Integer> getActiveDealOfBrand(List<Long> lstBrandId, TBrandFilter filter);
    
    List<Long> getBrandsOfUser(long userId);
    
    public List<TBrand> getMulti(List<Long> ids);
}
