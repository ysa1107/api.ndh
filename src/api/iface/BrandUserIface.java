/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.JsonResultEnt;

/**
 *
 * @author liemtpt
 */
public interface BrandUserIface {
    
    public JsonResultEnt updateBrandUserTable();
    
    public JsonResultEnt insertBrandUser(long brandId, long userId);
    
    public JsonResultEnt deleteBrandUser(long brandId, long userId);
    
}
