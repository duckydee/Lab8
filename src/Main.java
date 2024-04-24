import bridges.base.Edge;
import bridges.base.GraphAdjList;
import bridges.base.GraphAdjMatrix;
import bridges.base.LinkVisualizer;
import bridges.connect.Bridges;
import bridges.data_src_dependent.OsmVertex;
import bridges.validation.RateLimitException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.regex.*;

import static java.lang.Integer.lowestOneBit;
import static java.lang.Integer.parseInt;

public class Main {
    public static void main(String[] args) throws RateLimitException, IOException {
        Bridges bridges = new Bridges(19,"duckydee","348122572003");
        bridges.setCoordSystemType("albersusa");
        bridges.setMapOverlay(true);
        GraphAdjList<String, String, Double> graph = new GraphAdjList<>();

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
                        graph.addVertex(matcher.group(1), matcher.group(1));
                        graph.getVertex(matcher.group(1)).setLocation(Double.parseDouble(matcher.group(3)), Double.parseDouble(matcher.group(2)));
                        graph.getVertex(matcher.group(1)).setLabel(matcher.group(1));
                    } catch (NumberFormatException e) {
                        //Path
                        graph.addEdge(matcher.group(1), matcher.group(2), Double.valueOf(matcher.group(3)));
                        graph.getLinkVisualizer(matcher.group(1),matcher.group(2)).setLabel(String.valueOf(Double.valueOf(matcher.group(3))));
                    }
                }
            }
        }catch (FileNotFoundException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        //Task 2: Implement the Algorithm
        GraphAdjMatrix<String, String, Double> ShortestPath = new GraphAdjMatrix<>();
        for (String point : graph.getVertices().keySet()) {
            for (Edge<String, Double> edge : graph.getAdjacencyList(point)) {
                ShortestPath.addEdge(edge.getFrom(),edge.getTo(),edge.getEdgeData().intValue());
            }
        }



        for (String k : graph.getVertices().keySet()){
            for (String i : graph.getVertices().keySet()){
                for (String j : graph.getVertices().keySet()) {
                    try {
                        ShortestPath.setEdgeData(i, j,
                            Math.min(ShortestPath.getEdgeData(i, j),
                            (ShortestPath.getEdgeData(i, k) + ShortestPath.getEdgeData(k, j))));
                    } catch (NullPointerException e) {

                    }
                }
            }

        }

        bridges.setDataStructure(ShortestPath);
        bridges.visualize();

    }
}