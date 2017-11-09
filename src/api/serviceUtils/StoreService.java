/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.JsonResultEnt;
import api.entities.StoreViewEnt;
import api.iface.StoreIface;
import api.utils.Constant;
import api.utils.ErrorCode;
import com.nct.shop.thrift.brand.client.TBrandClient;
import com.nct.shop.thrift.brand.models.TBrandFilter;
import com.nct.shop.thrift.brand.models.TStore;
import com.nct.shop.thrift.brand.models.TStoreFilter;
import com.nct.shop.thrift.brand.models.TStoreListResult;
import com.nct.shop.thrift.brand.models.TStoreResult;
import com.shopiness.framework.common.LogUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author liemtpt
 */
public class StoreService implements StoreIface {

    private static final Logger logger = LogUtil.getLogger(StoreService.class);
    public static StoreService instance = null;

    public static StoreService getInstance() {
        if (instance == null) {
            instance = new StoreService();
        }
        return instance;
    }

    @Override
    public JsonResultEnt getStores(TStoreFilter filter, boolean isFull) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TStoreListResult rs = client.getStores(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);

            // prepare map number active deal of a store
            Map<Long, Integer> mapNumDeal = null;
            if (rs != null && rs.getListData() != null) {
                Set<Long> brandIds = new HashSet();
                for (TStore storeT : rs.getListData()) {
                    brandIds.add(storeT.getBrandId());
                }
                TBrandFilter bfilter = new TBrandFilter();
                bfilter.setActiveDeal(Constant.ActiveDeal.TRUE);
                bfilter.setAreaId(filter.getAreaId());
                mapNumDeal = client.getActiveDealOfBrand(new ArrayList(brandIds), bfilter);
            }

            List<StoreViewEnt> lstStores = new ArrayList<>();
            long totalRecords = 0;
            boolean loadMore = false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TStore store = rs.getListData().get(i);
                    StoreViewEnt vo = new StoreViewEnt(store, isFull, filter.getCategoryId(), filter.getUserId(), filter.isPush);

                    int numDeal = (mapNumDeal != null && mapNumDeal.containsKey(store.getBrandId()) ? mapNumDeal.get(store.getBrandId()) : 0);
                    vo.brandViewEnt.numDeal = numDeal;

                    lstStores.add(vo);
                }
                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadMore = true;
                }
            }
            result.setData(lstStores);
            result.setTotal(totalRecords);
            result.setLoadMore(loadMore);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    /**
     * you may have to implement a real getMulti interface for brand service. 
     * Now I have to loop and request n times to thrift service, but no problem
     * since both api and service hosted in the same network
     */   
    @Override
    public List<TStore> getMulti(List<Long> ids) {
        List<TStore> ret = new ArrayList();
        try {
            for (long id : ids) {
                TStore storeT = getStore(id);
                if(storeT != null){
                    ret.add(storeT);
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return ret;
    }

    @Override
    public TStore getStore(long id) {
        try {
            if (id > 0) {
                TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
                TStoreResult res = client.getStore(id, false);
                if (res != null && res.getValue() != null) {
                    return res.getValue();
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return null;
    }
}
