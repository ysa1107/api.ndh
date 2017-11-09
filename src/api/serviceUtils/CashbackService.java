/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.util.DateTimeUtils;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.log4j.Logger;
import shopiness.cashback.client.TCashbackClient;
import shopiness.cashback.thrift.TBank;
import shopiness.cashback.thrift.TBankFilter;
import shopiness.cashback.thrift.TBankListResult;
import shopiness.cashback.thrift.TBankResult;
import shopiness.cashback.thrift.THistory;
import shopiness.cashback.thrift.THistoryFilter;
import shopiness.cashback.thrift.THistoryListResult;
import shopiness.cashback.thrift.THistoryResult;
import shopiness.cashback.thrift.TLink;
import shopiness.cashback.thrift.TLinkListResult;
import shopiness.cashback.thrift.TLinkResult;
import shopiness.cashback.thrift.TTransaction;
import shopiness.cashback.thrift.TTransactionFilter;
import shopiness.cashback.thrift.TTransactionListResult;
import shopiness.cashback.thrift.TTransactionResult;

/**
 *
 * @author nghiapht
 */
public class CashbackService {

    private static TCashbackClient client = TCashbackClient.getInstance(ConfigInfo.SERVER_CASHBACK_THRIFT);
    private static final Logger logger = LogUtil.getLogger(CashbackService.class);
    private static final long ONE_DAY_IN_MILLISECONDS = 86400*1000;

    public static List<TLink> getLinkOfDeal(long dealId) {
        try {
            TLinkListResult res = client.getLinksOfDeal(dealId);
            if (res != null && res.getListData() != null) {
                return res.getListData();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return Collections.emptyList();
    }

    public static TLink getLink(long linkId) {
        try {
            TLinkResult res = client.getLink(linkId);
            if (res != null) {
                return res.getValue();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return null;
    }

    public static THistoryListResult getHistory(THistoryFilter filter) {
        try {
            THistoryListResult res = client.getHistoriesFilter(filter);
            if (res != null) {
                return res;
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return new THistoryListResult();
    }

    public static THistoryResult insertHistory(THistory val) {
        try {
            THistoryResult res = client.insertHistory(val);
            if (res != null) {
                return res;
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return new THistoryResult();
    }

    public static TTransactionListResult getTransactionsOfUser(long uid) {
        try {
            TTransactionListResult res = client.getTransactionsOfUser(uid);
            if (res != null) {
                return res;
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return new TTransactionListResult();
    }
    
    public static TTransactionListResult getSavedTransactionsOfUser(long uid) {
        try {
            TTransactionListResult res = client.getSavedTransactionsOfUser(uid);
            if (res != null) {
                return res;
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return new TTransactionListResult();
    }

    public static int getTotalTransactionToday(long uid) {
        int ret = 0;
        try {
            Date date = DateTimeUtils.getDateTime(DateTimeUtils.getNow("dd/MM/yyyy"), "dd/MM/yyyy", TimeZone.getDefault().getID());
            long fromTime = date.getTime();
            long toTime = date.getTime() + ONE_DAY_IN_MILLISECONDS;

            TTransactionFilter filer = new TTransactionFilter();
            filer.setUserId(uid);
            filer.setFromDate(fromTime);
            filer.setToDate(toTime);

            TTransactionListResult res = client.getTransactionsFilter(filer);
            if (res != null && res.getListData() != null) {
                for (TTransaction tranT : res.getListData()) {
                    if(tranT.getValue() < 0){
                        ret -= tranT.getValue();
                    }
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return ret;
    }

    public static TTransactionResult insertTransaction(TTransaction val) {
        try {
            TTransactionResult res = client.insertTransaction(val);
            if (res != null) {
                return res;
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return new TTransactionResult();
    }
    
    public static boolean saveTransaction(long uid, long transId) {
        try {
            if(uid > 0 && transId > 0){
                return client.saveTransaction(uid, transId);
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return false;
    }
    
    public static TTransaction getTransaction(long transId) {
        try {
            TTransactionResult res = client.getTransaction(transId);
            if (res != null) {
                return res.getValue();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return null;
    }

    public static TBankListResult getBankFilter(TBankFilter filter) {
        try {
            TBankListResult res = client.getBanksFilter(filter);
            if (res != null) {
                return res;
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return new TBankListResult();
    }

    public static TBank getBank(long id) {
        try {
            TBankResult res = client.getBank(id);
            if (res != null) {
                return res.getValue();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return null;
    }
}
