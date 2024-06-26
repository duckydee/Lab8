import bridges.base.GraphAdjList;
import bridges.connect.Bridges;
import bridges.validation.RateLimitException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.regex.*;


public class Main {
    public static double[][] distanceMatrix = new double[48][48];
    public static GraphAdjList<String, Integer, Double> graph = new GraphAdjList<>();
    public static String[] cities = new String[distanceMatrix.length];

    public static void main(String[] args) throws RateLimitException, IOException {
        Bridges bridges = new Bridges(19,"duckydee","348122572003");
        bridges.setCoordSystemType("albersusa");
        bridges.setMapOverlay(true);

        //Initalise the Distance Matrix
        for (int k=0;k<distanceMatrix.length;k++) {
            for (int i = 0; i < distanceMatrix.length; i++) {
                if (k == i){
                    distanceMatrix[k][i] = 0.0;
                }else{
                    distanceMatrix[k][i] = Double.MAX_VALUE;
                }
            }
        }

        //Initalise the Pathing Matrix
        int[][] Next = new int[distanceMatrix.length][distanceMatrix.length];
        for (int k=0;k<distanceMatrix.length;k++) {
            for (int i = 0; i < distanceMatrix.length; i++) {
                Next[i][k] = -1;
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
                        graph.getVertex(matcher.group(1)).setSize(1.0f);
                        counter++;
                    } catch (NumberFormatException e) {
                        //Path
                        graph.addEdge(matcher.group(1), matcher.group(2), Double.valueOf(matcher.group(3)));
                        graph.getLinkVisualizer(matcher.group(1),matcher.group(2)).setLabel(String.valueOf(Double.valueOf(matcher.group(3))));
                        graph.getLinkVisualizer(matcher.group(1),matcher.group(2)).setThickness(1.0f);
                        distanceMatrix[graph.getVertexData(matcher.group(1))][graph.getVertexData(matcher.group(2))] = Double.parseDouble(matcher.group(3));
                        Next[graph.getVertexData(matcher.group(1))][graph.getVertexData(matcher.group(2))] = graph.getVertexData(matcher.group(2));

                    }
                }
            }
        }catch (FileNotFoundException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        //Task 2: Implement the Algorithm
        //Use extra matrices, use to plot the paths

        DecimalFormat df = new DecimalFormat("#.###");
        for (int k=0;k<distanceMatrix.length;k++) {
            for (int i=0;i<distanceMatrix.length;i++) {
                for (int j=0;j<distanceMatrix.length;j++) {
                    if (distanceMatrix[i][k] + distanceMatrix[k][j] < distanceMatrix[i][j]) {
                        distanceMatrix[i][j] = Double.parseDouble(df.format(distanceMatrix[i][k] + distanceMatrix[k][j]));
                        Next[i][j] = Next[i][k];
                    }
                }
            }
        }


        //Task 3: Get the shortest path
        int startCity = 3;
        String[] searchedCities = new String[]{"Wichita_KS","Minneapolis_MN","Jacksonville_FL"};

        for (String x :searchedCities) {
            for (int y=0;y<cities.length;y++){
                if (Objects.equals(cities[y], x)){
                    //Find the Path
                    ArrayList<Integer> path = new ArrayList<>();
                    path.add(startCity);
                    int u = startCity;
                    while(u!= y){
                        u = Next[u][y];
                        path.add(u);
                    }

                    //Visualize the path
                    double cost = 0.0;
                    for (int z = 0; z <path.size()-1; z++){
                        graph.getVertex(cities[path.get(z)]).setColor("red");
                        graph.getVertex(cities[path.get(z)]).setSize(3.0f);
                        graph.getVertex(cities[path.get(z+1)]).setColor("red");
                        graph.getVertex(cities[path.get(z+1)]).setSize(3.0f);

                        graph.getLinkVisualizer(cities[path.get(z)],cities[path.get(z+1)]).setColor("red");
                        graph.getLinkVisualizer(cities[path.get(z)],cities[path.get(z+1)]).setThickness(3.0f);
                        cost += graph.getEdgeData(cities[path.get(z)],cities[path.get(z+1)]);
                    }
                    //Visualize the end and start nodes
                    graph.getVertex(cities[path.getLast()]).setColor("green");
                    graph.getVertex(cities[path.getLast()]).setSize(5.0f);
                    graph.getVertex(cities[path.getLast()]).setLabel(cities[path.getFirst()]+" to "+cities[path.getLast()] + ": "+df.format(cost));
                    graph.getVertex(cities[path.getFirst()]).setSize(5.0f);
                    break;
                }
            }

        }


        bridges.setDataStructure(graph);
        bridges.visualize();

    }
}