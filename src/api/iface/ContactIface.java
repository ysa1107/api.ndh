/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.JsonResultEnt;

/**
 *
 * @author liemtpt
 */
public interface ContactIface {

    JsonResultEnt suggestBrandNew(long userId, String content);
    
    JsonResultEnt suggestDealForBrand(long userId, long brandId);
    
    JsonResultEnt feedbackCashback(long userId, String code, String email, String content, long historyId);
}
