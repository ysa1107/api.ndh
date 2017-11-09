/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.JsonResultEnt;
import api.iface.DealUserIface;
import api.utils.FunctionUtils;
import com.nct.shop.thrift.deal.client.TDealClient;
import com.nct.shop.thrift.deal.client.TDealUserClient;
import com.nct.shop.thrift.deal.models.TDeal;
import com.nct.shop.thrift.deal.models.TDealFilter;
import com.nct.shop.thrift.deal.models.TDealListResult;
import com.nct.shop.thrift.deal.models.TDealResult;
import com.nct.shop.thrift.deal.models.TDealUser;
import com.nct.shop.thrift.deal.models.TDealUserResult;
import com.shopiness.framework.common.LogUtil;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author huanlh
 */
public class DealUserService implements DealUserIface {

    private static final Logger logger = LogUtil.getLogger(DealUserService.class);
    public static DealUserService instance = null;

    public static DealUserService getInstance() {
        if (instance == null) {
            instance = new DealUserService();
        }
        return instance;
    }

    public DealUserService() {

    }

    @Override
    public JsonResultEnt updateDealUserTable() {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();

        TDealUserClient uclient = TDealUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
        TDealClient dClient = TDealClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

        try {
            TDealFilter filter = new TDealFilter();
            TDealListResult drs = dClient.getDeals(filter, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            if (drs != null && drs.listData != null && drs.listData.size() > 0) {
                for (TDeal deal : drs.listData) {
                    if (!FunctionUtils.IsNullOrEmpty(deal.userIDs)) {
                        List<Long> lstUserId = FunctionUtils.ParseStringToListLong(deal.userIDs);
                        TDealUser dealUser = null;
                        if (lstUserId != null && lstUserId.size() > 0) {
                            for (long userId : lstUserId) {
                                TDealUserResult checkRS = uclient.getDealUser(deal.dealId, userId);
                                if (checkRS == null || checkRS.value == null) {
                                    dealUser = new TDealUser();
                                    dealUser.dealId = deal.dealId;
                                    dealUser.userId = userId;

                                    uclient.insertDealUser(dealUser);
                                }
                            }
                        }
                    }
                }
            }
            result = JsonResultEnt.getJsonSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    @Override
    public synchronized JsonResultEnt saveDeal(long dealId, long userId) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        TDealUserClient uClient = TDealUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
        TDealClient dClient = TDealClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

        try {
            TDealUserResult checkRS = uClient.getDealUser(dealId, userId);
            if (checkRS == null || checkRS.value == null) {
                TDealUser dealUser = new TDealUser();
                dealUser.dealId = dealId;
                dealUser.userId = userId;

                uClient.insertDealUser(dealUser);
                
                TDealResult drs = dClient.getDeal(dealId, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
                if(drs!=null && drs.value!=null){
                    drs.value.numlike ++;
                    dClient.updateDeal(drs.value);
                }
            }
            result = JsonResultEnt.getJsonSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    @Override
    public synchronized JsonResultEnt unSaveDeal(long dealId, long userId) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        TDealUserClient uClient = TDealUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
        TDealClient dClient = TDealClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

        try {
            boolean checkRS = uClient.deleteDealUser(dealId, userId);
            if (checkRS) {
                TDealResult drs = dClient.getDeal(dealId, ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
                if(drs!=null && drs.value!=null){
                    drs.value.numlike --;
                    dClient.updateDeal(drs.value);
                }
            }
            result = JsonResultEnt.getJsonSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }

}
