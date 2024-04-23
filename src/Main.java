import bridges.base.GraphAdjList;
import bridges.base.GraphAdjMatrix;
import bridges.connect.Bridges;
import bridges.validation.RateLimitException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.regex.*;

import static java.lang.Integer.parseInt;

public class Main {
    public static void main(String[] args) throws RateLimitException, IOException {

        Bridges bridges = new Bridges(19,"duckydee","348122572003");
        //bridges.setCoordSystemType("albersusa");
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
                    boolean isCity = true;
                    try {
                        Double.parseDouble(matcher.group(2));
                    } catch (NumberFormatException e) {
                        isCity = false;
                    }
                    if (isCity) {
                        //City
                        graph.addVertex(matcher.group(1), matcher.group(1));
                        graph.getVertex(matcher.group(1)).setLocation(Double.parseDouble(matcher.group(2)), Double.parseDouble(matcher.group(3)));
                    } else {
                        graph.addEdge(matcher.group(1), matcher.group(2));
                    }
                }
            }
        }catch (FileNotFoundException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        bridges.setDataStructure(graph);
        bridges.visualize();


        System.out.println("Hello world!");
    }
}