/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.utils;

import com.shopiness.framework.common.LogUtil;
import java.text.NumberFormat;
import org.apache.log4j.Logger;
import shopiness.cashback.common.Const;

/**
 *
 * @author nghiapht
 */
public class CurrencyUtils {

    private static final Logger logger = LogUtil.getLogger(CurrencyUtils.class);

    public static String formatCurrency(int value, String unit) {
        try {
            return NumberFormat.getInstance().format(value) + unit;
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return "0";
    }

    public static String formatCashback(double cashbackAmount, short cashbackType) {
        try {
            switch (cashbackType) {
                case Const.Cashback.Type.CASH:
                    int amount = (int)cashbackAmount;
                    if (amount > 1000) {
                        return formatCurrency(amount / 1000, "") + "k";
                    } else {
                        return formatCurrency(amount, "đ");
                    }
                case Const.Cashback.Type.PERCENT:
                    return NumberFormat.getInstance().format(cashbackAmount) + "%";
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return "0";
    }
}
