/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import com.shopiness.framework.common.LogUtil;
import org.apache.log4j.Logger;
import shopiness.search.client.TSearchClient;
import shopiness.search.thrift.TBrandSearchValue;
import shopiness.search.thrift.TDealSearchValue;
import shopiness.search.thrift.TSearchResult;
import shopiness.search.thrift.TStoreSearchValue;

/**
 *
 * @author nghiapht
 */
public class SearchService {

    private static TSearchClient client = TSearchClient.getInstance(ConfigInfo.SERVER_SEARCH_THRIFT);
    private static final Logger logger = LogUtil.getLogger(SearchService.class);

    public static TSearchResult searchDeal(TDealSearchValue searchVal) {
        try {
            TSearchResult res = client.searchDeal(searchVal);
            if (res != null) {
                return res;
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return new TSearchResult();
    }

    public static TSearchResult searchBrand(TBrandSearchValue searchVal) {
        try {
            TSearchResult res = client.searchBrand(searchVal);
            if (res != null) {
                return res;
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return new TSearchResult();
    }

    public static TSearchResult searchStore(TStoreSearchValue searchVal) {
        try {
            TSearchResult res = client.searchStore(searchVal);
            if (res != null) {
                return res;
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return new TSearchResult();
    }
}
