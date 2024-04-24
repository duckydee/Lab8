import bridges.base.GraphAdjList;
import bridges.connect.Bridges;
import bridges.validation.RateLimitException;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.regex.*;

import static java.lang.Integer.parseInt;

public class Main {
    public static double[][] distanceMatrix = new double[48][48];
    public static GraphAdjList<String, Integer, Double> graph = new GraphAdjList<>();
    public static String[] cities = new String[distanceMatrix.length];


    public static List<Integer> findIntermediatePoints(int startPoint, int endPoint) {
        List<Integer> intermediatePoints = new ArrayList<>();
        if(graph.getEdgeData(cities[startPoint], cities[endPoint]) == null) {
            return intermediatePoints; // Direct connection exists, no intermediate points needed
        }
        for (int intermediateVertex = 0; intermediateVertex < graph.getVertices().size(); intermediateVertex++) {
            //if (intermediateVertex == startPoint || intermediateVertex == endPoint) {
                //continue; // Skip start and end vertices
            //}
            if (graph.getEdgeData(cities[startPoint], cities[intermediateVertex]) != null && graph.getEdgeData(cities[intermediateVertex], cities[endPoint]) != null) {
                intermediatePoints.add(intermediateVertex);
            }
        }
        return intermediatePoints;
    }

//    public static ArrayList<Integer> findIntermediatePoints(double[][] graph, int x, int y) {
//        double target = graph[x][y];
//        ArrayList<Integer> intermediatePoints = new ArrayList<>();
//        DecimalFormat df = new DecimalFormat("#.####");
//        df.setRoundingMode(RoundingMode.CEILING);
//        int k = x;
//        while (k != y && k > 0) {
//            for (int kMinusOne = k-1; kMinusOne > 0; kMinusOne--) {
//                double result = Double.parseDouble(df.format((graph[kMinusOne][y]-graph[x][kMinusOne])));
//
//                if (result == target) {
//                    intermediatePoints.add(k);
//                    k = kMinusOne;
//                    break;
//                }
//            }
//        }
//        return intermediatePoints;
//    }

    public static double getDist(double lat1, double long1, double lat2, double long2) {
        //uses the haversine formula
        final int R = 6371000; // meters
        final double phi1 = Math.toRadians(lat1);
        final double phi2 = Math.toRadians(lat2);
        final double delPhi = Math.toRadians((lat2 - lat1));
        final double delLambda = Math.toRadians((long2 - long1));

        final double a = Math.sin(delPhi/2) * Math.sin(delPhi/2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(delLambda/2) * Math.sin(delLambda/2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c; //meters
    }

    public static void main(String[] args) throws RateLimitException, IOException {
        Bridges bridges = new Bridges(19,"duckydee","348122572003");
        bridges.setCoordSystemType("albersusa");
        bridges.setMapOverlay(true);


        for (int k=0;k<distanceMatrix.length;k++) {
            for (int i = 0; i < distanceMatrix.length; i++) {
                if (k == i){
                    distanceMatrix[k][i] = 0.0;
                }else{
                    distanceMatrix[k][i] = Double.MAX_VALUE;
                }

            }
        }

        int counter = 0;
        //Step 1: Get the data points from the file
        try{
            File file = new File("src/graph_us_cities.txt");
            Scanner myReader = new Scanner(file);
            Pattern pattern = Pattern.compile("\t(.*?)  (.*?)  (.*?)$");
            while (myReader.hasNextLine()){
                String data = myReader.nextLine();
                Matcher matcher = pattern.matcher(data);
                if (matcher.find()) {
                    try {
                        Double.parseDouble(matcher.group(2));
                        //City
                        cities[counter] = matcher.group(1);
                        graph.addVertex(matcher.group(1), counter);
                        graph.getVertex(matcher.group(1)).setLocation(Double.parseDouble(matcher.group(3)), Double.parseDouble(matcher.group(2)));
                        graph.getVertex(matcher.group(1)).setLabel(matcher.group(1));
                        counter++;
                    } catch (NumberFormatException e) {
                        //Path
                        graph.addEdge(matcher.group(1), matcher.group(2), Double.valueOf(matcher.group(3)));
                        graph.getLinkVisualizer(matcher.group(1),matcher.group(2)).setLabel(String.valueOf(Double.valueOf(matcher.group(3))));
                        distanceMatrix[graph.getVertexData(matcher.group(1))][graph.getVertexData(matcher.group(2))] = Double.parseDouble(matcher.group(3));
                    }
                }
            }
        }catch (FileNotFoundException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        //Task 2: Implement the Algorithm
        DecimalFormat df = new DecimalFormat("#.####");
        for (int k=0;k<distanceMatrix.length;k++) {
            for (int i=0;i<distanceMatrix.length;i++) {
                for (int j=0;j<distanceMatrix.length;j++) {
                    if (distanceMatrix[i][k] + distanceMatrix[k][j] < distanceMatrix[i][j]) {
                        distanceMatrix[i][j] = Double.parseDouble(df.format(distanceMatrix[i][k] + distanceMatrix[k][j]));
                    }
                }
            }
        }


        //Task 3: Get the shortest path

        int startCity = 17;
        int[] searchedCities = new int[]{44,12,26};
        for (int x :searchedCities) {
            List<Integer> path = findIntermediatePoints(startCity,x);
            for (int y=0;y<path.size()-1;y++){
                graph.getVertex(cities[path.get(y)]).setColor("red");
                graph.getVertex(cities[path.get(y)]).setSize(5.0);
                graph.getVertex(cities[path.get(y+1)]).setColor("red");
                graph.getVertex(cities[path.get(y+1)]).setSize(5.0);

                graph.getLinkVisualizer(cities[path.get(y)],cities[path.get(y+1)]).setColor("red");
                graph.getLinkVisualizer(cities[path.get(y)],cities[path.get(y+1)]).setThickness(5.0);
            }
        }


        bridges.setDataStructure(graph);
        bridges.visualize();

    }
}