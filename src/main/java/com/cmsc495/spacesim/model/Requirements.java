/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cmsc495.spacesim.model;

/**
 *
 * @author Megan
 */


import java.util.*;
import java.time.*;


public class Requirements {
    
    public HashMap<Resource, int> resources;
    public HashMap<Skill, int> skills;
    public Progress progressContribution;
    public Duration duration;
    
    
    public Requirements(){
        
        resources = new HashMap();
        skills = new HashMap();
        
    }
    
    
        
    
    // complete the given requirement, removing the needed resources from the planet supply and incrementing the progress
    public void completeRequirement(){
    } //End completeRequirement()
    
    
    
    
    
} //End Requirements class
