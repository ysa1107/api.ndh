/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
//import api.entities.DealProductViewEnt;
import api.entities.JsonResultEnt;
import api.entities.ProductViewEnt;
import api.iface.ProductIface;
import api.utils.ErrorCode;
import api.utils.FunctionUtils;
import com.nct.shop.thrift.brand.client.TBrandClient;
import com.nct.shop.thrift.brand.models.TProduct;
import com.nct.shop.thrift.brand.models.TProductFilter;
import com.nct.shop.thrift.brand.models.TProductListResult;
import com.nct.shop.thrift.deal.client.TDealProductClient;
import com.nct.shop.thrift.deal.models.TDealProduct;
import com.nct.shop.thrift.deal.models.TDealProductFilter;
import com.nct.shop.thrift.deal.models.TDealProductListResult;
import com.nct.shop.thrift.deal.models.TDealProductResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author liemtpt
 */
public class ProductService implements ProductIface {

    public static ProductService instance = null;

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    @Override
    public JsonResultEnt getProductsOfDeal(TProductFilter filter, boolean isFull) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TProductListResult rs = client.getProducts(filter);
            List<ProductViewEnt> lstProducts = new ArrayList<>();
            long totalRecords = 0;
            boolean loadMore = false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TProduct prd = rs.getListData().get(i);
                    ProductViewEnt vo = new ProductViewEnt(prd, isFull, filter.getCategoryId(), filter.getDealId(), filter.getUserId());
                    lstProducts.add(vo);
                }
                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadMore = true;
                }
            }
            result.setData(lstProducts);
            result.setTotal(totalRecords);
            result.setLoadMore(loadMore);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception ex) {
            return JsonResultEnt.getJsonDataNotExist();
        }
        return result;
    }

    @Override
    public JsonResultEnt getProductsOfDeal(TDealProductFilter filter, boolean isFull, long userId) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TDealProductClient client = TDealProductClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            TDealProductListResult rs = client.getDealProducts(filter);
            List<ProductViewEnt> lstProducts = new ArrayList<>();
            long totalRecords = 0;
            boolean loadMore = false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TDealProduct prd = rs.getListData().get(i);
                    ProductViewEnt vo = new ProductViewEnt(prd, isFull, filter.getCategoryId(), filter.getDealId(), userId);
//                    ProductViewEnt vo = new ProductViewEnt(prd, isFull, filter.getUserId());
                    lstProducts.add(vo);
                }
                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadMore = true;
                }
            }
            result.setData(lstProducts);
            result.setTotal(totalRecords);
            result.setLoadMore(loadMore);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception ex) {
            return JsonResultEnt.getJsonDataNotExist();
        }
        return result;
    }
    
    @Override
    public JsonResultEnt getProducts(TDealProductFilter filter, boolean isFull) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TDealProductClient client = TDealProductClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            int cPageIndex = filter.getPageIndex();
            int cPageSize = filter.getPageSize();
            // lay 1000 record in db
            filter.setPageIndex(1);
            filter.setPageSize(1000);
            TDealProductListResult rs = client.getDealProducts(filter);
            List<ProductViewEnt> lstProducts = new ArrayList<>();
            List<ProductViewEnt> lstTmp = new ArrayList<>();
            long totalRecords = 0;
            boolean loadMore = false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TDealProduct prd = rs.getListData().get(i);
                    ProductViewEnt vo = new ProductViewEnt(prd, isFull, filter.getUserId());
                    lstProducts.add(vo);
                }
                int iStart = (cPageIndex - 1) * cPageSize;
                int iEnd = (cPageIndex * cPageSize);
                if (iEnd >= lstProducts.size()) {
                    iEnd = lstProducts.size();
                }
                if (iStart >= lstProducts.size()) {
                    iStart = lstProducts.size();
                }
                Collections.sort(lstProducts, new Comparator<ProductViewEnt>() {
                    @Override
                    public int compare(ProductViewEnt o1, ProductViewEnt o2) {
                        if (o1.brandId == o2.brandId) {
                            return 0;
                        }
                        //this product is greater than ent
                        if (o1.brandId > o2.brandId) {
                            return 1;
                        }
                        //this product is less than ent
                        return -1;
                    }
                });
                lstTmp = lstProducts.subList(iStart, iEnd);
                totalRecords = rs.getTotalRecord();
                if (cPageSize * cPageIndex < totalRecords) {
                    loadMore = true;
                }
            }
            result.setData(lstTmp);
            result.setTotal(totalRecords);
            result.setLoadMore(loadMore);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception ex) {
            return JsonResultEnt.getJsonDataNotExist();
        }
        return result;
    }
    
    
//    @Override
//    public JsonResultEnt getProducts(TDealProductFilter filter, boolean isFull) {
//        JsonResultEnt result = new JsonResultEnt();
//        try {
//            TDealProductClient client = TDealProductClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
//            int cPageIndex = filter.getPageIndex();
//            int cPageSize = filter.getPageSize();
//            // lay 1000 record in db
//            filter.setPageIndex(1);
//            filter.setPageSize(1000);
//            TDealProductListResult rs = client.getDealProducts(filter);
//            List<DealProductViewEnt> lstProducts = new ArrayList<>();
//            List<DealProductViewEnt> lstTmp = new ArrayList<>();
//            long totalRecords = 0;
//            boolean loadMore = false;
//            if (rs.getListData() != null && rs.getListData().size() > 0) {
//                for (int i = 0; i < rs.getListData().size(); i++) {
//                    TDealProduct prd = rs.getListData().get(i);
//                    DealProductViewEnt vo = new DealProductViewEnt(prd, isFull, filter.getUserId());
//                    lstProducts.add(vo);
//                }
//                int iStart = (cPageIndex - 1) * cPageSize;
//                int iEnd = (cPageIndex * cPageSize);
//                if (iEnd >= lstProducts.size()) {
//                    iEnd = lstProducts.size();
//                }
//                if (iStart >= lstProducts.size()) {
//                    iStart = lstProducts.size();
//                }
//                Collections.sort(lstProducts);
//                lstTmp = lstProducts.subList(iStart, iEnd);
//                totalRecords = rs.getTotalRecord();
//                if (cPageSize * cPageIndex < totalRecords) {
//                    loadMore = true;
//                }
//            }
//            result.setData(lstTmp);
//            result.setTotal(totalRecords);
//            result.setLoadMore(loadMore);
//            result.setCode(ErrorCode.SUCCESS.getValue());
//        } catch (Exception ex) {
//            return JsonResultEnt.getJsonDataNotExist();
//        }
//        return result;
//    }

    @Override
    public JsonResultEnt addOrRemovedFavoriteProduct(long dealId, long productId, long userId, boolean isFavorite) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            String userIDs;
            TDealProductClient client = TDealProductClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            TDealProductResult rs = client.getDealProduct(dealId, productId);
            boolean kq;
            if (rs.value == null) {
                return JsonResultEnt.getJsonDataNotExist();
            }
            TDealProduct product = rs.value;
            if (isFavorite) {
                userIDs = FunctionUtils.addStringIDs(product.getUserIDs(), userId);
            } else {
                userIDs = FunctionUtils.removedStringIDs(product.getUserIDs(), userId);
            }
            product.setUserIDs(userIDs);
            kq = client.updateDealProduct(product);
            if (kq) {
                result = JsonResultEnt.getJsonSuccess();
            } else {
                result = JsonResultEnt.getJsonSystemError();
            }
        } catch (Exception ex) {
            return JsonResultEnt.getJsonDataNotExist();
        }
        return result;
    }
}
