/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.CategoryViewEnt;
import api.entities.JsonResultEnt;
import api.iface.CategoryIface;
import api.utils.ErrorCode;
import com.nct.shop.thrift.brand.client.TBrandClient;
import com.nct.shop.thrift.brand.models.TCategory;
import com.nct.shop.thrift.brand.models.TCategoryFilter;
import com.nct.shop.thrift.brand.models.TCategoryListResult;
import com.shopiness.framework.common.LogUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author liemtpt
 */
public class CategoryService implements CategoryIface {

    private static final Logger logger = LogUtil.getLogger(CategoryService.class);
    public static CategoryService instance = null;

    public static CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
        }
        return instance;
    }

    @Override
    public List<CategoryViewEnt> getCategoriesByIds(String categoryIDs, boolean isFull) {
        List<CategoryViewEnt> lstCate = new ArrayList<>();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TCategoryListResult rs = client.getCategoriesByCategoryIDs(categoryIDs);
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TCategory category = rs.getListData().get(i);
                    CategoryViewEnt vo = new CategoryViewEnt(category, isFull);
                    lstCate.add(vo);
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return lstCate;
    }

    @Override
    public JsonResultEnt getCategories(TCategoryFilter filter, boolean isFull) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TCategoryListResult rs = client.getCategories(filter);
            List<CategoryViewEnt> lstCates = new ArrayList<>();
            long totalRecords = 0;
            boolean loadMore= false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TCategory category = rs.getListData().get(i);
                    CategoryViewEnt vo = new CategoryViewEnt(category, isFull);
                    lstCates.add(vo);
                }
                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadMore = true;
                }
            }
            result.setData(lstCates);
            result.setTotal(totalRecords);
            result.setLoadMore(loadMore);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public List<CategoryViewEnt> getCategoriesByProductIDs(TCategoryFilter filter, boolean isFull) {
        List<CategoryViewEnt> lstCate = new ArrayList<>();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TCategoryListResult rs = client.getCategories(filter);
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TCategory category = rs.getListData().get(i);
                    CategoryViewEnt vo = new CategoryViewEnt(category, isFull);
                    lstCate.add(vo);
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return lstCate;
    }

}
