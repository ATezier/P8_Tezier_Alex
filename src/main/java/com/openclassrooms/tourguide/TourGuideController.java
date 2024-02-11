package com.openclassrooms.tourguide;

import java.util.List;
import java.util.SortedMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import rewardCentral.RewardCentral;
import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;

    @Autowired
    RewardCentral rewardCentral;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String userName) {
    	return tourGuideService.getUserLocation(getUser(userName));
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
 	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	//  Return a new JSON object that contains:
    	// Name of Tourist attraction, 
        // Tourist attractions lat/long, 
        // The user's location lat/long, 
        // The distance in miles between the user's location and each of the attractions.
        // The reward points for visiting each Attraction.
        //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions") 
    public JSONArray getNearbyAttractions(@RequestParam String userName) throws JSONException {
        User user = getUser(userName);
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        SortedMap<Double, Attraction> mappedAttractions = tourGuideService.getNearByAttractions(visitedLocation);
        Attraction current;
        JSONArray array = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("userLatitude", visitedLocation.location.latitude);
        item.put("userLongitude", visitedLocation.location.longitude);
        array.put(item);
        for(Double distance : mappedAttractions.keySet()) {
            item = new JSONObject();
            current = mappedAttractions.get(distance);
            item.put("name", current.attractionName);
            item.put("latitude", current.latitude);
            item.put("longitude", current.longitude);
            item.put("distance", distance);
            item.put("rewardPoints", rewardCentral.getAttractionRewardPoints(current.attractionId, user.getUserId()));
            array.put(item);
        }
    	return array;
    }
    
    @RequestMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }
       
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	return tourGuideService.getTripDeals(getUser(userName));
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}