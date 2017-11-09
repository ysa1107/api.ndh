/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.JsonResultEnt;
import api.iface.ProductUserIface;
import com.nct.shop.thrift.deal.client.TDealProductClient;
import com.nct.shop.thrift.deal.client.TProductUserClient;
import com.nct.shop.thrift.deal.models.TDealProductResult;
import com.nct.shop.thrift.deal.models.TProductUser;
import com.nct.shop.thrift.deal.models.TProductUserResult;
import com.shopiness.framework.common.LogUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author huanlh
 */
public class ProductUserService implements ProductUserIface {

    private static final Logger logger = LogUtil.getLogger(ProductUserService.class);
    public static ProductUserService instance = null;

    public static ProductUserService getInstance() {
        if (instance == null) {
            instance = new ProductUserService();
        }
        return instance;
    }

    public ProductUserService() {

    }

    @Override
    public synchronized JsonResultEnt saveProduct(long dealId, long productId, long userId) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        TProductUserClient uClient = TProductUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
        TDealProductClient dClient = TDealProductClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

        try {
            TDealProductResult dprs = dClient.getDealProduct(dealId, productId);
            if (dprs != null && dprs.value != null) {
                TProductUserResult checkRS = uClient.getProductUser(dprs.value.id, userId);
                if (checkRS == null || checkRS.value == null) {
                    TProductUser productUser = new TProductUser();
                    productUser.dealProductId = dprs.value.id;
                    productUser.dealId = dealId;
                    productUser.productId = productId;
                    productUser.userId = userId;
                    
                    uClient.insertProductUser(productUser);
                }
                result = JsonResultEnt.getJsonSuccess();
            } else {
                return JsonResultEnt.getJsonProductNotExists();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    @Override
    public synchronized JsonResultEnt unSaveProduct(long dealId, long productId, long userId) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        TProductUserClient uClient = TProductUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
        TDealProductClient dClient = TDealProductClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
        try {
            TDealProductResult dprs = dClient.getDealProduct(dealId, productId);
            if (dprs != null && dprs.value != null) {
                boolean checkRS = uClient.deleteProductUser(dprs.value.id, userId);
                if (checkRS) {
                    result = JsonResultEnt.getJsonSuccess();
                }
            } else {
                return JsonResultEnt.getJsonProductNotExists();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }

}
