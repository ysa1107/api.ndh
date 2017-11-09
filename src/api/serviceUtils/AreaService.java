/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.AreaViewEnt;
import api.entities.JsonResultEnt;
import api.iface.AreaIface;
import api.utils.ErrorCode;
import com.nct.shop.thrift.brand.client.TBrandClient;
import com.nct.shop.thrift.brand.models.TArea;
import com.nct.shop.thrift.brand.models.TAreaFilter;
import com.nct.shop.thrift.brand.models.TAreaListResult;
import com.shopiness.framework.common.LogUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author liemtpt
 */
public class AreaService implements AreaIface {

    private static final Logger logger = LogUtil.getLogger(AreaService.class);
    public static AreaService instance = null;

    public static AreaService getInstance() {
        if (instance == null) {
            instance = new AreaService();
        }
        return instance;
    }
    
    @Override
    public JsonResultEnt getAreas(TAreaFilter filter, boolean isFull) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TAreaListResult rs = client.getAreas(filter);
            List<AreaViewEnt> lstAreas = new ArrayList<>();
            long totalRecords = 0;
            boolean loadMore= false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TArea area = rs.getListData().get(i);
                    AreaViewEnt vo = new AreaViewEnt(area, isFull);
                    lstAreas.add(vo);
                }
                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadMore = true;
                }
            }
            result.setData(lstAreas);
            result.setTotal(totalRecords);
            result.setLoadMore(loadMore);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }
    
    @Override
    public TAreaListResult getAreas(TAreaFilter filter) {
        TAreaListResult rs = null;
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            rs = client.getAreas(filter);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return rs;
    }
}
