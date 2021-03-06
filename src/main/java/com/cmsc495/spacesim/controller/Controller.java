/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cmsc495.spacesim.controller;

import java.util.*;
import com.cmsc495.spacesim.model.*;
import com.cmsc495.spacesim.view.*;
import java.io.*;

public class Controller {
    final static private int REQUIREMENT_MAXIMUM_SKILL_TOTAL = 5;
    final static private int REQUIREMENT_MAXIMUM_RESOURCE_TOTAL = 15;
    
    private static Planet earth;
    private static ArrayList<Planet> planets;
    private static ArrayList<String> names;
    private static ArrayList<String> allSkills;
    private static ArrayList<String> allResources;    
    public static ViewLog logWindow = new ViewLog();
    
    // InitializeEarth will prepare earth for the simulation.
    public static void InitializeEarth(){
        
        earth = new Planet("Earth", 0);
        names = getAllPossiblePeopleNames();
        allSkills = getAllPossibleSkills();
        allResources =  getAllPossibleResources();
        for (int i = 0; i < 50; i++) {
            earth.people.add(newPerson());
        }
        
        //populate the list of resources
        for(int i = 0; i < allResources.size(); i++){
            earth.resources.put(allResources.get(i), 999);
        }
        earth.dockedShips.addAll(new Connect().getShip("Small' OR shipSize='Medium' OR shipSize='Large"));
        
        planets = new ArrayList<>();
        planets.add(earth);
    }
    
    // create a new person.
    private static Person newPerson(){
        Random r = new Random();
        Person p = new Person(names.get(r.nextInt(names.size())), allSkills.get(r.nextInt(allSkills.size())));
        return p;
    }
    
    // get a new Requirement with randomly populated skill/resource values
    public static Requirement newRequirement(){
        Requirement req = new Requirement();
        Random r = new Random();

        //pick a random number of skills
        for(int i = 0; i < r.nextInt(REQUIREMENT_MAXIMUM_SKILL_TOTAL) + 1; i++){
            //add a random skill
            String skill = allSkills.get(r.nextInt(allSkills.size()));
            
            //make sure key is set
            if (!req.skills.containsKey(skill)){
                req.skills.put(skill, 1);
            } else {
                req.skills.put(skill, req.skills.get(skill));
            }
        }
        
        //pick a random number of resources
        for(int i = 0; i <= r.nextInt(REQUIREMENT_MAXIMUM_RESOURCE_TOTAL) + 1; i++){
            //add a random skill
            String skill = allResources.get(r.nextInt(allResources.size()));
            
            //make sure key is set
            if (!req.resources.containsKey(skill)){
                req.resources.put(skill, 1);
            } else {
                req.resources.put(skill, req.resources.get(skill));
            }
        }
        
        return req;     
    }
    
    private static ArrayList<String> getAllPossibleResources(){
        //String[] str = {"Food", "Water", "Gasoline", "Coal", "Building Materials", "Medical Supplies"};
        //return new ArrayList<String>(Arrays.asList(str));
        return new Connect().getIdentifier("resource");
    }
    
    private static ArrayList<String> getAllPossibleSkills(){
        //String[] str = {"Chemist", "Medic", "Architect", "Explorer", "Engineer"};
        //return new ArrayList<String>(Arrays.asList(str));
        return new Connect().getIdentifier("skill");
    }
    
    private static ArrayList<String> getAllPossiblePeopleNames(){
        //String[] str = {"Alice", "Bob", "Charlie", "Darcy", "Edward"};
        //return new ArrayList<String>(Arrays.asList(str));
        return new Connect().getIdentifier("name");
    }
    
    
    // sendShip will load the given resources onto the specified ship (checking capacity) and send it to the target planet.
    public static void sendShip(Ship s, Planet target, ArrayList<Person> people, HashMap<String,Integer> resources) throws RuntimeException{
        
        int totalResources = 0;
        for (Map.Entry<String, Integer> k : resources.entrySet()){
            totalResources += k.getValue();
        }
  
        //check if we can send the ship
        if (totalResources > s.cargoCapacity){
            logEvent(s + " cargo capacity exceeded");
            throw new RuntimeException("insufficient cargo space");
        }
        float planetDistance  = earth.distanceToPlanet(target);
        if (planetDistance > s.fuelCapacity){
            logEvent("Ship: [" + s + "] cannot reach planet: [" + target + "]");
            throw new RuntimeException("not enough fuel");
        }
        if (people.size() > s.peopleCapacity) {
            logEvent(s + " people capacity exceeded");
            throw new RuntimeException("insufficient passenger space");
        }
        
        try{
            earth.removeResources(resources);
            s.addResources(resources);
            logEvent("Resources added to ship: ["+ s + "]");
        } catch(Exception e){
            logEvent("Error adding resources to ship: [" + s + "] (" + e.getMessage() + ")");
        }

        try{
            earth.removePeople(people);
            s.addPeople(people);
            logEvent("Added [" + people + "] to ship: [" + s + "]");
        } catch(Exception e){
            logEvent("Error adding TOO MANY people to ship: [" + s + "] (" + e.getMessage() + ")");
        }
        


        earth.undockShip(s);
        logEvent("Ship: [" + s + "] has left earth for planet: [" + target + "]");
        
        //this might be the spot where we should start a timer, but for now we appear at the planet immediately
        target.dockShip(s);
        logEvent("Ship: [" + s + "] has arrived on planet: [" + target + "]");
        unloadShip(s, target);
        logEvent("People and resources from Ship: [" + s + "] unloaded onto planet: {" + target + "]");
    }
    
    // returns an ArrayList of ships based on the size selected in a list
    public static ArrayList<Ship> searchShipSize(String size) {
        ArrayList<Ship> ships = new ArrayList<>();       
        for(Ship ship : earth.dockedShips)
            if(ship.shipSize.equals(size))
                ships.add(ship);
        
        return ships;
    }
    
    // get planet list based on fuel capacity of a ship
    public static ArrayList<Planet> getPlanetList(float distance) {
        return new Connect().getPlanet(distance);
    }
    
    // GetAllPeople will return all people on the starting planet.
    public static ArrayList<Person>getAllPeople(){
        return earth.people;
    }
    
    // GetAllShips will return all ships on the starting planet.
    public static ArrayList<Ship>getAllShips(){
        return earth.dockedShips;
    }
    
    // GetAllPlanets will return all planets.
    public static ArrayList<Planet> getAllPlanets(){
        return planets;
    }
    
    // GetAllResources will return all resources on the starting planet.
    public static HashMap<String, Integer> getAllResources(){
        return earth.resources;
    }
    
    // AddPlanet will append p to the static planet list.
    public static void addPlanet(Planet p){
        planets.add(p);
    }
    
    // remove all people and resources from s and add them to target
    public static void unloadShip(Ship s, Planet target){
       target.people.addAll(s.people); 
       s.people.clear();
       Set<String> rSet = s.resources.keySet();
       //System.out.println("The planet before unloading: " + target.resources);
       for(String r: rSet){
          target.resources.computeIfPresent(r, (key, oldVal) -> oldVal + s.resources.get(r));
          target.resources.computeIfAbsent(r, key -> s.resources.get(r));
       }
       //System.out.println("The planet after unloading: " + target.resources);
       //System.out.println("The ship list b4 clearing : " + s.resources);
       s.resources.clear();
       //System.out.println("The ship list after clearing : " + s.resources);
       //System.out.println("The planet has the following people: " + target.people);
       logEvent("All people and resources unloaded from ship: {" + s + 
               "], and added to planet: [" + target + "]" );
    }

     // In case we want to create a separte log for errors
    public static  void logEvent(String event){
        event += " at [" + new Date() + "]";
        logWindow.getAllLogEntries(event);
        logFile(event, "log.txt");
       //logWindow.getAllLogEntries(event);
    }
    // logs an event to a specified file
    public static void logFile(String log, String fileName){
         BufferedWriter writer = null;

         // write the text variable using the bufferedwriter to testing.txt
         try {
             writer = new BufferedWriter(new FileWriter(fileName, true));
             writer.write("---" + log);
             writer.newLine();
         }
         // print error message if there is one
         catch (IOException io) {
             System.out.println("File IO Exception" + io.getMessage());
         }
         //close the file
         finally {
             try {
                 if (writer != null) {
                     writer.close();
                 }
             }
             //print error message if there is one
             catch (IOException io) {
                 System.out.println("Issue closing the File." + io.getMessage());
             }
         }
     } 
}
