/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.BrandListEnt;
import api.entities.BrandViewEnt;
import api.entities.DealListEnt;
import api.entities.DealViewEnt;
import api.entities.JsonResultEnt;
import api.entities.StoreListEnt;
import api.entities.StoreViewEnt;
import api.iface.BrandIface;
import api.utils.ErrorCode;
import com.nct.shop.thrift.brand.client.TBrandClient;
import com.nct.shop.thrift.brand.models.TBrand;
import com.nct.shop.thrift.brand.models.TBrandFilter;
import com.nct.shop.thrift.brand.models.TBrandListResult;
import com.nct.shop.thrift.brand.models.TBrandResult;
import com.nct.shop.thrift.brand.models.TBrandUser;
import com.nct.shop.thrift.brand.models.TBrandUserResult;
import com.nct.shop.thrift.brand.models.TStore;
import com.nct.shop.thrift.brand.models.TStoreFilter;
import com.nct.shop.thrift.brand.models.TStoreListResult;
import com.nct.shop.thrift.deal.client.TDealClient;
import com.nct.shop.thrift.deal.models.TDeal;
import com.nct.shop.thrift.deal.models.TDealFilter;
import com.nct.shop.thrift.deal.models.TDealListResult;
import com.shopiness.framework.common.LogUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author liemtpt
 */
public class BrandService implements BrandIface {

    private static final Logger logger = LogUtil.getLogger(BrandService.class);
    public static BrandService instance = null;

    public static BrandService getInstance() {
        if (instance == null) {
            instance = new BrandService();
        }
        return instance;
    }

    @Override
    public JsonResultEnt getBrands(TBrandFilter filter, boolean isFull, boolean isSearch) {
        JsonResultEnt result = new JsonResultEnt();
        TBrandListResult rs;
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            if (isSearch) {
                rs = client.getBrands(filter, true);
            } else {
                rs = client.getBrands(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            }

            List<BrandViewEnt> lstBrands = new ArrayList<>();
            long totalRecords = 0;
            boolean loadMore = false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TBrand brand = rs.getListData().get(i);
                    BrandViewEnt vo = new BrandViewEnt(brand, isFull, filter.getUserId(), filter.getAreaId());
                    lstBrands.add(vo);
                }
                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadMore = true;
                }
            }
            BrandListEnt listBrandEnt = new BrandListEnt();
            listBrandEnt.setListBrand(lstBrands, filter);

            result.setData(listBrandEnt.getListBrand());
            result.setTotal(totalRecords);
            result.setLoadMore(loadMore);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

//    @Override
//    public JsonResultEnt addOrRemovedFavoriteBrand(long brandId, long userId, boolean isFavorite) {
//        JsonResultEnt result = new JsonResultEnt();
//        try {
//            String userIDs;
//            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
//            TBrandResult rs = client.getBrand(brandId);
//            boolean kq;
//            if (rs.value == null) {
//                return JsonResultEnt.getJsonDataNotExist();
//            }
//            TBrand brand = rs.value;
//            if (isFavorite) {
//                userIDs = FunctionUtils.addStringIDs(brand.getUserIDs(), userId);
//            } else {
//                userIDs = FunctionUtils.removedStringIDs(brand.getUserIDs(), userId);
//            }
//            brand.setUserIDs(userIDs);
//            kq = client.updateBrand(brand);
//            if (kq) {
//                result = JsonResultEnt.getJsonSuccess();
//            } else {
//                result = JsonResultEnt.getJsonSystemError();
//            }
//        } catch (Exception ex) {
//            return JsonResultEnt.getJsonDataNotExist();
//        }
//        return result;
//    }
    @Override
    public JsonResultEnt addOrRemovedFavoriteBrand(long brandId, long userId, boolean isFavorite) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TBrandUserResult rs = client.getBrandUser(brandId, userId, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            TBrandUser brand;
            boolean kq = false;
            if (isFavorite) {
                if (rs != null && rs.value != null) {
                    return result;
                }
                brand = new TBrandUser();
                brand.setBrandId(brandId);
                brand.setUserId(userId);
                TBrandUserResult rs1 = client.insertBrandUser(brand);
                if (rs1 != null && rs1.value != null) {
                    kq = true;
                }
            } else if (rs != null && rs.value != null && rs.value.getBrandId() > 0 && rs.value.getUserId() > 0) {
                kq = client.deleteBrandUser(rs.value.getBrandId(), rs.value.getUserId());
            }
            if (kq) {
                result = JsonResultEnt.getJsonSuccess();
            } else {
                result = JsonResultEnt.getJsonSystemError();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public BrandViewEnt getBrandFromDeal(TBrandFilter filter, boolean isFull) {
        BrandViewEnt brandEnt = new BrandViewEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TBrandListResult rs = client.getBrands(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            if (rs.getListData() != null && rs.getListData().size() > 0 && rs.getListData().size() == 1) {
                TBrand brand = rs.getListData().get(0);
                brandEnt = new BrandViewEnt(brand, isFull);
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return brandEnt;
    }

    @Override
    public List<StoreViewEnt> getStoresFromDeal(TStoreFilter filter, boolean isFull) {
        StoreListEnt listEnt = new StoreListEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TStoreListResult rs = client.getStores(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            List<StoreViewEnt> lstStores = new ArrayList<>();
            for (int i = 0; i < rs.getListData().size(); i++) {
                TStore store = rs.getListData().get(i);
                StoreViewEnt vo = new StoreViewEnt(store, isFull, filter.getDealId());
                lstStores.add(vo);
            }
            listEnt.setListStore(lstStores);
            if (filter.getLat() <= 0 && filter.getLng() <= 0) {
                Collections.sort(listEnt.getListStore(), new Comparator<StoreViewEnt>() {
                    @Override
                    public int compare(StoreViewEnt o1, StoreViewEnt o2) {
                        return o1.priority.compareTo(o2.priority);
                    }
                });
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return listEnt.getListStore();
    }

    @Override
    public BrandViewEnt getBrandUser(long brandId, long userId, boolean isFull) {
        BrandViewEnt brandEnt = new BrandViewEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TBrandResult rs = client.getBrand(brandId, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            if (rs != null && rs.value != null) {
                TBrand brand = rs.value;
                brandEnt = new BrandViewEnt(brand, isFull, userId);
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return brandEnt;
    }

    @Override
    public boolean checkBrandUser(long brandId, long userId) {
        boolean kq = false;
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            kq = client.checkBrandUser(brandId, userId, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return kq;
    }

    @Override
    public List<DealViewEnt> getDealsByBrandUser(long brandId, long userId, boolean isFull) {
        DealListEnt dealListEnt = new DealListEnt();
        try {
            TDealClient client = TDealClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            TDealListResult rs = client.getDealsOfBrand(brandId, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            List<DealViewEnt> lstDeal = new ArrayList<>();
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TDeal tDeal = rs.getListData().get(i);
                    DealViewEnt dealView = new DealViewEnt(tDeal, true, userId, true, false);
                    lstDeal.add(dealView);
                }
            }
            dealListEnt.setListDeal(lstDeal, userId, false);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return dealListEnt.getListDeal();
    }

    @Override
    public List<DealViewEnt> getDealsOfBrand(TDealFilter filter, boolean isFull, long userId) {
        DealListEnt dealListEnt = new DealListEnt();
        try {
            TDealClient client = TDealClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            TDealListResult rs = client.getDeals(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            List<DealViewEnt> lstDeal = new ArrayList<>();
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TDeal tDeal = rs.getListData().get(i);
                    DealViewEnt dealView = new DealViewEnt(tDeal, true, userId, true, false);
                    lstDeal.add(dealView);
                }
            }
            dealListEnt.setListDeal(lstDeal, userId, true);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return dealListEnt.getListDeal();
    }

    @Override
    public TBrandListResult getBrands(TBrandFilter filter) {
        TBrandListResult rs = null;
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            rs = client.getBrands(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return rs;
    }

    @Override
    public Map<String, List<Long>> getMapDealOfBrand(List<Long> lstBrandId) {
        Map<String, List<Long>> map = new HashMap<>();
        try {
            if (lstBrandId == null || lstBrandId.size() <= 0) {
                return map;
            }
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            map = client.getMapDealOfBrand(lstBrandId);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return map;
    }

    @Override
    public Map<Long, Integer> getActiveDealOfBrand(List<Long> lstBrandId, TBrandFilter filter) {
        Map<Long, Integer> map = new HashMap<>();
        try {
            if (lstBrandId == null || lstBrandId.size() <= 0) {
                return map;
            }
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            map = client.getActiveDealOfBrand(lstBrandId, filter);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return map;
    }

    @Override
    public List<Long> getBrandsOfUser(long userId) {
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            List<Long> ret = client.getBrandsOfUser(userId);
            if (ret != null) {
                return ret;
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return Collections.emptyList();
    }

    @Override
    public List<TBrand> getMulti(List<Long> ids) {
        try {
            TBrandFilter filter = new TBrandFilter();
            filter.setListBrandId(ids);
            filter.setPageSize(ids.size());
            
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TBrandListResult res = client.getBrands(filter, false);
            if (res != null && res.getListData() != null) {
                return res.getListData();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return Collections.emptyList();
    }
}
