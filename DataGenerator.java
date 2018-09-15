package edu.jhu.kqian2.offmaps;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Kelvin on 9/15/2018.
 */

public class DataGenerator {

    public static class Coordinate {
        double latitude;
        double longitude;
        double elevation;

        public Coordinate(double latitude, double longitude, double elevation) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.elevation = elevation;
        }
    }

    public ArrayList<Coordinate> generateData(double startLat, double startLong, double endLat, double endLong) {

        ArrayList<Coordinate> coordList = new ArrayList<>();
        Random rand = new Random();

        for (double lat = startLat; lat <= endLat; lat += 0.01) {
            for (double lon = startLong; lon <= endLong; lon += 0.01) {
                // Randomly generate an elevation
                double elevation = rand.nextDouble() * 1000;
                coordList.add(new Coordinate(lat, lon, elevation));
            }
        }

        return coordList;
    }
}
