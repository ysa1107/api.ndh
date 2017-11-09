/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.utils;

import com.shopiness.framework.util.ConvertUtils;


/**
 *
 * @author giangnm
 */
public class NumberUtils {

    //private static String str36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String str36 = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    private static final String _str26 = "qwertyuiopasdfghjklzxcvbnm";
    private static int _len = 9;

    public static String IDEncrypt(long id) {
        if (id <= 0) {
            return "";
        }

        String tmp2 = ToString26(Long.toString(id).length());
        String tmp1 = Character.toString(_str26.toCharArray()[tmp2.length()]);
        String tmp3 = ToString26(ConvertUtils.toLong(FormatID(id)));
        return tmp1 + tmp2 + tmp3;
    }

    public static long IDDecrypt(String value) {
        try {
            if (value.length() <= 0) {
                return -1;
            }

            char tmp1 = value.toCharArray()[0];
            int index = _str26.indexOf(tmp1);
            if (index <= 0 || index > value.length()) {
                return -1;
            }

            //long len = FromString26(value.substring(1, index));
            long len = FromString26(value.substring(1, (1 + index)));
            long userID = ToID(Long.toString(FromString26(value.substring(1 + index))));

            if (Long.toString(userID).length() != len) {
                return -1;
            }
            return userID;
        } catch (Exception ex) {
        }
        return -1;
    }

    private static String FormatID(long id) {
        String sid = Long.toString(id);
        String str = "";
        long tmp = (id % 10);
        for (int i = 0; i < _len - sid.length(); ++i) {
            tmp = Math.abs(Math.abs((_len - tmp) - i));
            str += Long.toString(tmp);
        }
        str = Integer.toString(sid.length()) + sid + str;
        return str;
    }

    private static long ToID(String str) {
        if (str.length() < _len + 1) {
            return -1;
        }

        //int l = ConvertUtils.toInt(str.substring(0, 1), -1);
        int l = ConvertUtils.toInt(str.substring(0, 1), -1);
        //long id = ConvertUtils.toLong(str.substring(1, l), -1);
        long id = ConvertUtils.toLong(str.substring(1, (1 + l)), -1);

        long tmp = (id % 10);
        for (int i = l; i < str.length() - 1; ++i) {
            tmp = Math.abs(Math.abs(_len - tmp) - (i - l));
            int cmp = ConvertUtils.toInt(str.toCharArray()[i + 1] + "");
            if (tmp != cmp) {
                id = -1;
                break;
            }
        }
        return id;
    }

    public static String ToString26(long value) {
        String str = "";
        while (value >= _str26.length()) {
            long tmp = value % (_str26.length());
            value = value / (_str26.length());
            str = Character.toString(_str26.toCharArray()[(int) tmp]) + str;
        }
        str = Character.toString(_str26.toCharArray()[(int) value]) + str;
        return str;
    }

    public static long FromString26(String str) {
        //str = str.toUpperCase();
        long value = 0;
        long tmp = 1;
        for (int i = 0; i < str.length(); ++i) {
            long index = _str26.indexOf(Character.toString(str.toCharArray()[str.length() - i - 1]));
            if (index < 0) {
                return -1;
            }
            value += index * tmp;
            tmp = tmp * _str26.length();
        }
        return value;
    }

    public static String ToString36(int value) {
        String str = "";
        while (value >= str36.length()) {
            int tmp = value % (str36.length());
            value = value / (str36.length());
            str = Character.toString(str36.toCharArray()[(int) tmp]) + str;
            //str = str36[(int)tmp].ToString() + str;
        }
        str = Character.toString(str36.toCharArray()[value]) + str;
        return str;
    }

    public static int FromString36(String str) {
        str = str.toUpperCase();
        int value = 0;
        int tmp = 1;
        for (int i = 0; i < str.length(); ++i) {
            int index = str36.indexOf(Character.toString(str.toCharArray()[(str.length() - i - 1)]));
            //String test = Character.toString(str.toCharArray()[0]);
            if (index < 0) {
                return -1;
            }
            value += index * tmp;
            tmp = tmp * str36.length();
        }
        return value;
    }
}
