/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.JsonResultEnt;

/**
 *
 * @author huanlh
 */
public interface DealUserIface {
    
    public JsonResultEnt updateDealUserTable();
    
    public JsonResultEnt saveDeal(long dealProductId, long userId);
    
    public JsonResultEnt unSaveDeal(long dealProductId, long userId);
    
}
