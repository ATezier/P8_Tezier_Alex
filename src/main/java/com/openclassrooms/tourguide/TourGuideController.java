package com.openclassrooms.tourguide;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.openclassrooms.tourguide.dto.AttractionDto;
import org.json.JSONException;
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

    @RequestMapping("/getNearbyAttractions") 
    public List<AttractionDto> getNearbyAttractions(@RequestParam String userName) throws JSONException {
        User user = getUser(userName);
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        SortedMap<Double, Attraction> mappedAttractions = tourGuideService.getNearByAttractions(visitedLocation);
        Attraction current;
        List<AttractionDto> result = new ArrayList<>();
        AttractionDto currentDto = null;
        for(Double distance : mappedAttractions.keySet()) {
            current = mappedAttractions.get(distance);
            currentDto = new AttractionDto();
            currentDto.setName(current.attractionName);
            currentDto.setAttractionLatitude(current.latitude);
            currentDto.setAttractionLongitude(current.longitude);
            currentDto.setUserLatitude(visitedLocation.location.latitude);
            currentDto.setUserLongitude(visitedLocation.location.longitude);
            currentDto.setDistance(distance);
            currentDto.setRewardPoints(rewardCentral.getAttractionRewardPoints(current.attractionId, user.getUserId()));
            result.add(currentDto);
        }
    	return result;
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