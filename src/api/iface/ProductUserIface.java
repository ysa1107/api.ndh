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
public interface ProductUserIface {
    
    public JsonResultEnt saveProduct(long dealId, long productId, long userId);
    
    public JsonResultEnt unSaveProduct(long dealId, long productId, long userId);
}
