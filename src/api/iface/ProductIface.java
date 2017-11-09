/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.JsonResultEnt;
import com.nct.shop.thrift.brand.models.TProductFilter;
import com.nct.shop.thrift.deal.models.TDealProductFilter;

/**
 *
 * @author trailn
 */
public interface ProductIface {

    JsonResultEnt getProductsOfDeal(TProductFilter filter, boolean isFull);
    
    JsonResultEnt getProductsOfDeal(TDealProductFilter filter, boolean isFull, long userId);
    
    JsonResultEnt getProducts(TDealProductFilter filter, boolean isFull);
    
    JsonResultEnt addOrRemovedFavoriteProduct(long dealId, long productId, long userId, boolean isFavorite);
}
