/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.CategoryViewEnt;
import api.entities.JsonResultEnt;
import com.nct.shop.thrift.brand.models.TCategoryFilter;
import java.util.List;

/**
 *
 * @author trailn
 */
public interface CategoryIface {

    JsonResultEnt getCategories(TCategoryFilter filter, boolean isFull);
    
    List<CategoryViewEnt> getCategoriesByIds(String categoryIDs, boolean isFull);
    
    List<CategoryViewEnt> getCategoriesByProductIDs(TCategoryFilter filter, boolean isFull);
}
