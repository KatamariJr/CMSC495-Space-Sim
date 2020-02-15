/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cmsc495.spacesim.controller;

import java.util.*;
import com.cmsc495.spacesim.model.*;

public class Controller {
    
    private static Planet earth;
    private static ArrayList<Planet> planets;
    
    // InitializeEarth will prepare earth for the simulation.
    public static void InitializeEarth(){
        earth = new Planet();
        for (int i = 0; i < 50; i++) {
            earth.people.add(newPerson());
        }
        
        HashMap<Resource, Integer> r = new HashMap<Resource, Integer>();
        r.put(new Resource("Food"), 999);
        r.put(new Resource("Water"), 999);
        r.put(new Resource("Medicine"), 999);
        
        earth.resources = r;
        
        planets = new ArrayList<Planet>();
        planets.add(earth);
    }
    
    // create a new person.
    private static Person newPerson(){
        Person p = new Person();
        p.name = "Bob";
        p.skill = "Flying";
        return p;
    }
    
    
    // SendShip will load the given resources onto the specified ship (checking capacity) and send it to the target planet.
    public static void SendShip(Ship s, Planet target, ArrayList<Person> people, HashMap<Resource,Integer> resources) throws RuntimeException{
        
        int totalResources = 0;
        for (Map.Entry<Resource, Integer> k : resources.entrySet()){
            totalResources += k.getValue();
        }
  
        //check if we can send the ship
        if (totalResources > s.cargoCapacity){
            throw new RuntimeException("insufficient cargo space");
        }
        float planetDistance  = earth.distanceToPlanet(target);
        if (planetDistance > s.fuelCapacity){
            throw new RuntimeException("not enough fuel");
        }
        
        earth.removePeople(people);
        earth.removeResources(resources);
        s.AddPeople(people);
        s.AddResources(resources);
        earth.UndockShip(s);
        
        //this might be the spot where we should start a timer, but for now we appear at the planet immediately
        target.DockShip(s);
        UnloadShip(s, target);
    }
    
    // GetAllPeople will return all people on the starting planet.
    public static ArrayList<Person>GetAllPeople(){
        return earth.people;
    }
    
    // GetAllShips will return all ships on the starting planet.
    public static ArrayList<Ship>GetAllShips(){
        return earth.dockedShips;
    }
    
    // GetAllPlanets will return all planets.
    public static ArrayList<Planet> GetAllPlanets(){
        return planets;
    }
    
    // GetAllResources will return all resources on the starting planet.
    public static HashMap<Resource, Integer> GetAllResources(){
        return earth.resources;
    }
    
    // AddPlanet will append p to the static planet list.
    public static void AddPlanet(Planet p){
        planets.add(p);
    }
    
    // remove all people and resources from s and add them to target
    public static void unloadShip(Ship s, Planet target){
       target.people.addAll(s.person); 
       s.person.clear();
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
    }

     // In case we want to create a separte log for errors
    public static void logEvent(String event){
       logFile(event, "log.txt");
    }
    // logs an event to a specified file
    public static void logFile(String log, String fileName){
         BufferedWriter writer = null;

         // write the text variable using the bufferedwriter to testing.txt
         try {
             writer = new BufferedWriter(new FileWriter(fileName, true));
             writer.write("---" + log + " at " + new Date());
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
   
    //these are here only for completion, they will be removed when the model stuff comes in

    class Person{

    }

    class Resource{

    }
    
}
