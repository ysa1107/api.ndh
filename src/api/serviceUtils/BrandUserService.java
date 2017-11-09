/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.JsonResultEnt;
import api.iface.BrandUserIface;
import api.utils.FunctionUtils;
import com.nct.shop.thrift.brand.client.TBrandClient;
import com.nct.shop.thrift.brand.models.BrandStatus;
import com.nct.shop.thrift.brand.models.TBrand;
import com.nct.shop.thrift.brand.models.TBrandFilter;
import com.nct.shop.thrift.brand.models.TBrandListResult;
import com.nct.shop.thrift.brand.models.TBrandUser;
import com.nct.shop.thrift.brand.models.TBrandUserResult;
import com.shopiness.framework.common.LogUtil;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author liemtpt
 */
public class BrandUserService implements BrandUserIface {

    private static final Logger logger = LogUtil.getLogger(BrandUserService.class);
    public static BrandUserService instance = null;
    

    public static BrandUserService getInstance() {
        if (instance == null) {
            instance = new BrandUserService();
        }
        return instance;
    }

    public BrandUserService() {

    }

    @Override
    public JsonResultEnt updateBrandUserTable() {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();

        TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);

        try {
            TBrandFilter filter = new TBrandFilter();
            filter.setStatus(BrandStatus.APPROVED.getValue());
            TBrandListResult rs = client.getBrands(filter,true);
            if (rs != null && rs.listData != null && rs.listData.size() > 0) {
                for (TBrand brand : rs.listData) {
                    if (!FunctionUtils.IsNullOrEmpty(brand.userIDs)) {
                        List<Long> lstUserId = FunctionUtils.ParseStringToListLong(brand.userIDs);
                        TBrandUser brandUser;
                        if (lstUserId != null && lstUserId.size() > 0) {
                            for (long userId : lstUserId) {
                                TBrandUserResult checkRS = client.getBrandUser(brand.brandId, userId,true);
                                if (checkRS == null || checkRS.value == null) {
                                    brandUser = new TBrandUser();
                                    brandUser.brandId = brand.brandId;
                                    brandUser.userId = userId;
                                    client.insertBrandUser(brandUser);
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
    public synchronized JsonResultEnt insertBrandUser(long brandId, long userId) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
        try {
            TBrandUserResult rs = client.getBrandUser(brandId, userId,ConfigInfo.DISABLED_REQUEST_FROM_CACHE);
            if (rs == null || rs.value == null) {
                TBrandUser brandUser = new TBrandUser();
                brandUser.brandId = brandId;
                brandUser.userId = userId;
                client.insertBrandUser(brandUser);
            }
            result = JsonResultEnt.getJsonSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    @Override
    public synchronized JsonResultEnt deleteBrandUser(long brandId, long userId) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
        try {
            boolean rs = client.deleteBrandUser(brandId, userId);
            result = JsonResultEnt.getJsonSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }

}
