/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.DealFavoriteListEnt;
import api.entities.DealListEnt;
import api.entities.DealViewEnt;
import api.entities.JsonResultEnt;
import api.entities.VoucherViewEnt;
import api.iface.DealIface;
import api.utils.Constant;
import api.utils.ErrorCode;
import com.nct.shop.thrift.brand.client.TBrandClient;
import com.nct.shop.thrift.brand.models.StoreStatus;
import com.nct.shop.thrift.brand.models.TStore;
import com.nct.shop.thrift.brand.models.TStoreFilter;
import com.nct.shop.thrift.brand.models.TStoreListResult;
import com.nct.shop.thrift.deal.client.TDealClient;
import com.nct.shop.thrift.deal.client.TEventUserClient;
import com.nct.shop.thrift.deal.client.TVoucherClient;
import com.nct.shop.thrift.deal.models.CouponType;
import com.nct.shop.thrift.deal.models.DealType;
import com.nct.shop.thrift.deal.models.TDeal;
import com.nct.shop.thrift.deal.models.TDealFilter;
import com.nct.shop.thrift.deal.models.TDealListResult;
import com.nct.shop.thrift.deal.models.TDealResult;
import com.nct.shop.thrift.deal.models.TEventUserResult;
import com.nct.shop.thrift.deal.models.TVoucher;
import com.nct.shop.thrift.deal.models.TVoucherFilter;
import com.nct.shop.thrift.deal.models.TVoucherListResult;
import com.nct.shop.thrift.deal.models.TVoucherResult;
import com.nct.shop.thrift.deal.models.VoucherStatus;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.util.ConvertUtils;
import com.shopiness.framework.util.DateTimeUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author huanlh
 */
public class DealService implements DealIface {

    public static DealService instance = null;
    private static TDealClient dClient;
    private static TEventUserClient euClient;
    private static final Logger logger = LogUtil.getLogger(DealService.class);

    public static DealService getInstance() {
        if (instance == null) {
            instance = new DealService();
        }
        return instance;
    }

    public DealService() {
        dClient = TDealClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
        euClient = TEventUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
    }

    @Override
    public JsonResultEnt getDeals(TDealFilter filter, boolean isFull, long userId) {
        JsonResultEnt result = new JsonResultEnt();
        TDeal tDeal;
        DealViewEnt deal;
        try {
            if (filter.isfavorite == Constant.FAVORITE_TRUE) {
                DealFavoriteListEnt dealFavorite = new DealFavoriteListEnt();
                List<DealViewEnt> lstOther = new ArrayList<>();
                List<DealViewEnt> lstNew = new ArrayList<>();

                TDealListResult rsOther = dClient.getDeals(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
                if (rsOther.getListData() != null && rsOther.getListData().size() > 0) {
                    for (int i = 0; i < rsOther.getListData().size(); i++) {
                        tDeal = rsOther.getListData().get(i);
                        deal = new DealViewEnt(tDeal, isFull, userId, true, false);

                        lstOther.add(deal);
                    }
                }
                DealListEnt lstOtherEnt = new DealListEnt();
                lstOtherEnt.setListDeal(lstOther, userId, false);

                //GET LIST NEW
                filter.setIsNew(true);
                TDealListResult rsNew = dClient.getDeals(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
                if (rsNew.getListData() != null && rsNew.getListData().size() > 0) {
                    for (int i = 0; i < rsNew.getListData().size(); i++) {
                        tDeal = rsNew.getListData().get(i);
                        deal = new DealViewEnt(tDeal, isFull, userId, true, false);

                        lstNew.add(deal);
                    }
                }
                DealListEnt lstNewEnt = new DealListEnt();
                lstNewEnt.setListDeal(lstNew, userId, false);

                dealFavorite.setLstNew(lstNewEnt.getListDeal());
                dealFavorite.setLstOther(lstOtherEnt.getListDeal());
                result.setData(dealFavorite);
                result.setCode(ErrorCode.SUCCESS.getValue());

            } else {

                TDealListResult rs = dClient.getDeals(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
                List<DealViewEnt> lstDeal = new ArrayList<>();
                long totalRecords = 0;
                boolean loadmore = false;

                if (rs.getListData() != null && rs.getListData().size() > 0) {
                    for (int i = 0; i < rs.getListData().size(); i++) {
                        tDeal = rs.getListData().get(i);
                        if (filter.userId > 0) {
                            deal = new DealViewEnt(tDeal, isFull, userId, true, true);
                        } else {
                            deal = new DealViewEnt(tDeal, isFull, userId, true, false);
                        }

                        lstDeal.add(deal);
                    }
                    totalRecords = rs.getTotalRecord();
                    if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                        loadmore = true;
                    }
                }
                DealListEnt lstDealEnt = new DealListEnt();
                lstDealEnt.setListDeal(lstDeal, userId, true);

                result.setData(lstDealEnt.getListDeal());
                result.setTotal(totalRecords);
                result.setLoadMore(loadmore);
                result.setCode(ErrorCode.SUCCESS.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return result;
    }

    @Override
    public JsonResultEnt getDeal(long dealId, long userId) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TDealResult rs = dClient.getDeal(dealId, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            DealViewEnt deal = null;

            if (rs.getValue() != null) {
                deal = new DealViewEnt(rs.getValue(), true, userId, true, false);
            }
            result.setData(deal);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public TDealResult getDeal(long dealId) {
        TDealResult result = new TDealResult();
        try {
            result = dClient.getDeal(dealId, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt getDealOfBrand(long brandId, long userId) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TDealListResult rs = dClient.getDealsOfBrand(brandId, false);
            List<DealViewEnt> lstDeal = new ArrayList<>();
            long totalRecords = 0;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TDeal tDeal = rs.getListData().get(i);
                    DealViewEnt dealView = new DealViewEnt(tDeal, true, userId, true, false);
                    lstDeal.add(dealView);
                }
                totalRecords = rs.getTotalRecord();
            }
            DealListEnt dealListEnt = new DealListEnt();
            dealListEnt.setListDeal(lstDeal, userId, false);
            result.setData(dealListEnt.getListDeal());
            result.setTotal(totalRecords);
            result.setLoadMore(false);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt saveDeal(long dealId, long userId, boolean isSave) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            boolean rs = dClient.saveDeal(dealId, userId, isSave);
            if (rs) {
                result = JsonResultEnt.getJsonSuccess();
            } else {
                result = JsonResultEnt.getJsonSystemError();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

//    @Override
//    public JsonResultEnt searchDeals(TDealFilter filter, boolean isFull, long userId) {
//        JsonResultEnt result = new JsonResultEnt();
//        try {
//            TDealListResult rs = dClient.searchDeals(filter);
//            List<DealViewEnt> lstDeal = new ArrayList<>();
//            DealListEnt dealListEnt = new DealListEnt();
//            long totalRecords = 0;
//            boolean loadmore = false;
//            if (rs.getListData() != null && rs.getListData().size() > 0) {
//                for (int i = 0; i < rs.getListData().size(); i++) {
//                    TDeal tDeal = rs.getListData().get(i);
//                    DealViewEnt dealView = new DealViewEnt(tDeal, isFull, userId, true, false);
//                    lstDeal.add(dealView);
//                }
//                dealListEnt.setListDeal(lstDeal, userId, false);
//
//                totalRecords = rs.getTotalRecord();
//                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
//                    loadmore = true;
//                }
//            }
//            result.setData(dealListEnt.getListDeal());
//            result.setTotal(totalRecords);
//            result.setLoadMore(loadmore);
//            result.setCode(ErrorCode.SUCCESS.getValue());
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//        }
//        return result;
//    }

    @Override
    public synchronized JsonResultEnt getVoucherCode(long dealId, String username, String type) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TVoucherClient client = TVoucherClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            long now = DateTimeUtils.getMilisecondsNow();
            TDealResult drs = dClient.getDeal(dealId, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            if (drs == null || drs.getValue() == null) {
                return new JsonResultEnt(ErrorCode.DEAL_NOT_EXISTS.getValue());
            } else if (now > drs.getValue().dateExpired) {
                return new JsonResultEnt(ErrorCode.DEAL_EXPIRED.getValue());
            }
            if (DealType.DEAL_EVENT.equals(drs.value.getType())) {
                TEventUserResult euRs = euClient.getEventUserByParam(dealId, username);
                if (euRs == null || euRs.getValue() == null) {
                    return new JsonResultEnt(ErrorCode.USER_NOT_WINNER_EVENT.getValue());
                }
            }
            if (drs.getValue().couponType.equals(CouponType.UNIQUE_CODE)) {
                TVoucherResult rs = client.getVoucherByParam(dealId, username);
                int availableVoucher = client.countAvailableVouchersOfDeal(dealId);
                if (type.equals("get")) {
                    //Truong hop da tao code
                    if (rs != null && rs.getValue() != null) {
                        VoucherViewEnt voucherEnt = new VoucherViewEnt(rs.getValue(), drs.getValue().getKeyCode());
                        result = new JsonResultEnt();

                        result.setCode(ErrorCode.SUCCESS.getValue());
                        result.setData(voucherEnt);
                        result.setExtend(availableVoucher);
                        //Truong hop chua co code
                    } else {
                        TVoucherFilter filter = new TVoucherFilter();
                        filter.setDealId(dealId);
                        filter.setPageSize(10);
                        filter.setPageIndex(1);
                        filter.setStatus(VoucherStatus.NEW.getValue());
                        TVoucherListResult vrs = client.getVouchers(filter);
                        //Con code
                        if (vrs.getListData() != null && vrs.getListData().size() > 0) {
                            long timestamp = DateTimeUtils.getMilisecondsNow();
                            long timeExpire = ConvertUtils.toLong(drs.getValue().timeExpired) * 60 * 60 * 1000;
                            TVoucher newVoucher = vrs.getListData().get(0);
                            newVoucher.username = username;
                            newVoucher.dateDistributed = timestamp;
                            newVoucher.status = VoucherStatus.DISTRIBUTED;
                            long dateExpired = timestamp + timeExpire;
                            newVoucher.dateExpired = dateExpired;
                            boolean updateRS = client.updateVoucher(newVoucher);
                            if (updateRS) {
                                VoucherViewEnt voucherEnt = new VoucherViewEnt(newVoucher, drs.getValue().getKeyCode());
                                result = new JsonResultEnt();
                                result.setCode(ErrorCode.SUCCESS.getValue());
                                result.setData(voucherEnt);
                                result.setExtend(availableVoucher - 1);
                            } else {
                                result = new JsonResultEnt(ErrorCode.SYSTEM_ERROR.getValue());
                            }
                            //Het code
                        } else {
                            result = new JsonResultEnt(ErrorCode.OUT_OF_VOUCHER_CODE.getValue());
                        }
                    }
                } else if (rs != null && rs.getValue() != null) {
                    VoucherViewEnt voucherEnt = new VoucherViewEnt(rs.getValue(), drs.getValue().getKeyCode());
                    result = new JsonResultEnt();

                    result.setData(voucherEnt);
                    result.setExtend(availableVoucher);
                } else {
                    result.setExtend(availableVoucher);
                }
            } else {
                result = new JsonResultEnt(ErrorCode.DEAL_NOT_HAVE_UNIQUE_CODE.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt viewDeal(long dealId) {
        JsonResultEnt result = null;
        try {
            TDealResult drs = dClient.getDeal(dealId, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            if (drs != null && drs.getValue() != null) {
                TDeal deal = drs.getValue();
                deal.setNumView(deal.getNumView() + 1);
                if (dClient.updateDeal(deal)) {
                    result = JsonResultEnt.getJsonSuccess();
                } else {
                    result = JsonResultEnt.getJsonSystemError();
                }
            } else {
                result = JsonResultEnt.getJsonDealNotExists();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt getDealByKeyCode(String keyCode) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TDealResult rs = dClient.getDealByKeyCode(keyCode);
            DealViewEnt deal = null;
            if (rs.getValue() != null) {
                deal = new DealViewEnt(rs.getValue(), true);
            }
            result.setData(deal);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public List<Long> getDealAreas(long dealId) {
        List<Long> lstAreaId = new ArrayList<>();
        try {
            if (!ConfigInfo.DISABLED_REQUEST_FROM_CACHE) {
                lstAreaId = dClient.getAreasOfDeal(dealId);
            } else {
                TBrandClient brandClient = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
                TStoreFilter sFilter = new TStoreFilter();
                sFilter.status = StoreStatus.ENABLED.getValue();
                sFilter.dealId = dealId;
                TStoreListResult stRS = brandClient.getStores(sFilter, true);
                if (stRS != null && stRS.listData != null && stRS.listData.size() > 0) {
                    for (TStore item : stRS.listData) {
                        String areaId = ConvertUtils.toString(item.areaId);
                        lstAreaId.add(item.areaId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return lstAreaId;
    }

    @Override
    public List<Long> getDealsOfUser(long userId) {
        List<Long> lstDealId = new ArrayList<>();
        try {
            TDealClient client = TDealClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            lstDealId = client.getDealsOfUser(userId);
            if (lstDealId == null) {
                lstDealId = new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return lstDealId;
    }

    @Override
    public Map<String, List<Long>> getMapAreaOfDeal(List<Long> lstDealId) {
        Map<String, List<Long>> map = new HashMap<>();
        try {
            map = dClient.getMapAreaOfDeal(lstDealId);
            if (lstDealId == null) {
                map = new HashMap<>();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return map;
    }

    @Override
    public TDealListResult getDeals(TDealFilter filter) {
        TDealListResult rs = null;
        try {
            TDealClient client = TDealClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            rs = client.getDeals(filter, true);
        } catch (Exception ex) {
            logger.error(LogUtil.stackTrace(ex));
        }
        return rs;
    }

    @Override
    public DealFavoriteListEnt getListFavoriteDeal(TDealFilter filter, boolean isFull) {
        DealFavoriteListEnt listEnt = new DealFavoriteListEnt();
        try {
            TDeal tDeal;
            DealViewEnt deal;
            filter.setIsfavorite(Constant.FAVORITE_TRUE);
            TDealListResult rsOther = dClient.getDeals(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            DealListEnt lstOtherEnt = new DealListEnt();
            List<DealViewEnt> lstOther = new ArrayList<>();
            if (rsOther.getListData() != null && rsOther.getListData().size() > 0) {
                for (int i = 0; i < rsOther.getListData().size(); i++) {
                    tDeal = rsOther.getListData().get(i);
                    deal = new DealViewEnt(tDeal, filter.getUserId());
                    lstOther.add(deal);
                }
                lstOtherEnt.setListDeal(lstOther, filter.getUserId(), false);
            }

            //GET LIST NEW
            DealListEnt lstNewEnt = new DealListEnt();
            List<DealViewEnt> lstNew = new ArrayList<>();
            filter.setIsNew(true);
            TDealListResult rsNew = dClient.getDeals(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            if (rsNew.getListData() != null && rsNew.getListData().size() > 0) {
                for (int i = 0; i < rsNew.getListData().size(); i++) {
                    tDeal = rsNew.getListData().get(i);
                    deal = new DealViewEnt(tDeal, isFull, filter.getUserId(), true, false);
                    lstNew.add(deal);
                }
                lstNewEnt.setListDeal(lstNew, filter.getUserId(), false);
            }
            listEnt.setLstNew(lstNewEnt.getListDeal());
            listEnt.setLstOther(lstOtherEnt.getListDeal());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return listEnt;
    }

    @Override
    public DealListEnt getListOtherDeal(TDealFilter filter, boolean isFull, int pageIndex, int pageSize) {
        DealListEnt listEnt = new DealListEnt();
        try {
            filter.setIsfavorite(Constant.FAVORITE_FALSE);
            filter.setPageIndex(pageIndex);
            filter.setPageSize(pageSize);
            TDealListResult rs = dClient.getDeals(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                long totalRecords = 0;
                boolean loadmore = false;
                TDeal tDeal;
                DealViewEnt deal;
                List<DealViewEnt> lstDeal = new ArrayList<>();
                for (int i = 0; i < rs.getListData().size(); i++) {
                    tDeal = rs.getListData().get(i);
                    if (filter.userId > 0) {
                        deal = new DealViewEnt(tDeal, filter.getUserId());
                    } else {
                        deal = new DealViewEnt(tDeal, filter.getUserId());
                    }
                    lstDeal.add(deal);
                }

                totalRecords = rs.getTotalRecord();
                if (pageSize * pageIndex < totalRecords) {
                    loadmore = true;
                }
                listEnt.setListDeal(lstDeal, filter.getUserId(), true, loadmore, totalRecords);
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return listEnt;
    }

    @Override
    public List<TDeal> getMulti(List<Long> ids) {
        try {
            TDealFilter filter = new TDealFilter();
            filter.setListDealId(ids);
            filter.setPageSize(ids.size());

            TDealListResult res = dClient.getDeals(filter, false);
            if (res != null && res.getListData() != null) {
                return res.getListData();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return Collections.emptyList();
    }
}
