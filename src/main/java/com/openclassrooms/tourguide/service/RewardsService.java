package com.openclassrooms.tourguide.service;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

@Service
public class RewardsService {
	private final ExecutorService executor = Executors.newCachedThreadPool();
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	//Done
	public void calculateRewards(User user) {
		List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());
		List<Attraction> attractions = gpsUtil.getAttractions();

		userLocations.parallelStream().forEach(l -> {
			attractions.parallelStream().forEach(a -> {
				if(nearAttraction(l, a)) {
					user.addUserReward(new UserReward(l, a, getRewardPoints(a, user)));
				}
			});
		});
	}

	public void calculateRewardsAsync(User user) {
		List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());
		List<Attraction> attractions = gpsUtil.getAttractions();

		List<CompletableFuture> futures = userLocations.parallelStream().map(l -> {
			return CompletableFuture.runAsync(() -> {
				attractions.parallelStream().forEach(a -> {
					if(nearAttraction(l, a)) {
						user.addUserReward(new UserReward(l, a, getRewardPoints(a, user)));
					}
				});
			}, executor);
		}).collect(Collectors.toList());
		//System.out.println("longitude: " + userLocations.get(0).location.longitude+ " latitude: " + userLocations.get(0).location.latitude+ " user: " + user.getUserName()+"thread: "+Thread.currentThread().getName());

		if(user.getUserRewards().size()>0) {
			System.out.println( " user: " + user.getUserName()+"reward:"+user.getUserRewards().get(0).attraction.attractionName);
		} else {
			System.out.println( " user: " + user.getUserName()+"reward: no reward");
		}
		/*
		System.out.println("Number of threads: " + ManagementFactory.getThreadMXBean().getThreadCount());
		System.out.println("Number of cores: " + Runtime.getRuntime().availableProcessors());
		System.out.println("Number of futures: " + futures.size());
		*/
		futures.forEach(CompletableFuture::join);
	}
	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
