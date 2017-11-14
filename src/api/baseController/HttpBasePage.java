/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.baseController;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author tr
 * @param <T>
 */
public class HttpBasePage<T> extends HttpServlet {

    private final String packageTmp = "%sv%s";
    private String _package;

    public String getPackageHandler(Class<T> _class, int version) {
        //NEW
        switch (_class.getName()) {
            case "api.servlet.CommonServlet":
                this._package = String.format(packageTmp, "api.controller.common.CommonController", version);
                break;
            case "api.servlet.UserServlet":
                this._package = String.format(packageTmp, "api.controller.user.UserController", version);
                break;
            case "api.servlet.RequestServlet":
                this._package = String.format(packageTmp, "api.controller.request.RequestController", version);
                break;
            case "api.servlet.LogsServlet":
                this._package = String.format(packageTmp, "api.controller.log.LogController", version);
                break;
            default:
                break;
        }
        return this._package;
    }

    private final Integer[] highestVersionModule_Commons = {1};
    private final Integer[] highestVersionModule_Users = {1};
    private final Integer[] highestVersionModule_Request = {1};
    private final Integer[] highestVersionModule_Brands = {1, 2};
    private final Integer[] highestVersionModule_Stores = {1};
    private final Integer[] highestVersionModule_Categories = {1};
    private final Integer[] highestVersionModule_Areas = {1};
    private final Integer[] highestVersionModule_Products = {1};
    private final Integer[] highestVersionModule_Comments = {1};
    private final Integer[] highestVersionModule_Contacts = {1};
    private final Integer[] highestVersionModule_Events = {1};
    private final Integer[] highestVersionModule_Cashbacks = {1,2};

    public int getVersion(String module, int versionRequest) {
        switch (module) {
            case "commons":
                return findVersion(highestVersionModule_Commons, versionRequest);
            case "users":
                return findVersion(highestVersionModule_Users, versionRequest);
            case "deals":
                return findVersion(highestVersionModule_Request, versionRequest);
            case "brands":
                return findVersion(highestVersionModule_Brands, versionRequest);
            case "stores":
                return findVersion(highestVersionModule_Stores, versionRequest);
            case "categories":
                return findVersion(highestVersionModule_Categories, versionRequest);
            case "areas":
                return findVersion(highestVersionModule_Areas, versionRequest);
            case "products":
                return findVersion(highestVersionModule_Products, versionRequest);
            case "comments":
                return findVersion(highestVersionModule_Comments, versionRequest);
            case "contacts":
                return findVersion(highestVersionModule_Contacts, versionRequest);
            case "events":
                return findVersion(highestVersionModule_Events, versionRequest);
            case "cashbacks":
                return findVersion(highestVersionModule_Cashbacks, versionRequest);
            default:
                return 1;
        }
    }

    private int findVersion(Integer[] arr, int item) {
        int result = item;
        List<Integer> lst = Arrays.asList(arr);
        while (result > 1) {
            if (lst.contains(result)) {
                return result;
            }
            result--;
        }
        return result;
    }
}
