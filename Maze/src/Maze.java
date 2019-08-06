import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.*;

// represents a player in the game
class Player {

}

// represents a vertex in the maze
class Vertex {
  int x;
  int y;
  boolean visited;
  boolean onPath;
  ArrayList<Edge> aroundEdges;

  Vertex(int x, int y) {
    this.x = x;
    this.y = y;
    this.visited = false;
    this.onPath = false;
    this.aroundEdges = new ArrayList<Edge>();
  }

  // a unique integer for each vertex, used for reference of a vertex
  int unique() {
    return 25 * y + x;
  }
}

// represents an edge in the maze
class Edge {
  Vertex start;
  Vertex end;
  int weight;

  Edge(Vertex start, Vertex end, int weight) {
    this.start = start;
    this.end = end;
    this.weight = weight;
  }
}

// represents the Maze Game World
class MazeItWorld extends World {

  Player player;
  ArrayList<Vertex> vertices;
  ArrayList<Edge> walls;
  static final int HEIGHT = 25;
  static final int WIDTH = 25;
  static final int SCALE = 20;

  MazeItWorld() {
    initialize();
  }

  // initializes the maze game with its vertices and walls
  void initialize() {
    ArrayList<ArrayList<Vertex>> vertexList2D = initialVertices();
    ArrayList<Edge> edgesList = getEdges(vertexList2D);
    kruskalVertex(vertexList2D);
    vertices = new ArrayList<Vertex>();
    walls = getWalls(vertexList2D, edgesList);

    for (ArrayList<Vertex> listVertex : vertexList2D) {
      for (Vertex vertex : listVertex) {
        vertices.add(vertex);
      }
    }
  }

  // gets all the walls for the maze
  ArrayList<Edge> getWalls(ArrayList<ArrayList<Vertex>> vertexList2D, ArrayList<Edge> allEdges) {
    ArrayList<Edge> wallsList = new ArrayList<Edge>();
    for (Edge edge : allEdges) {
      boolean working = true;
      for (ArrayList<Vertex> listVertex : vertexList2D) {
        for (Vertex vertex : listVertex) {
          for (Edge edge2 : vertex.aroundEdges) {
            if (edge.equals(edge2) || (edge.end == edge2.start && edge.start == edge2.end)) {
              working = false;
            }
          }
        }
      }
      if (working) {
        wallsList.add(edge);
      }
    }
    return wallsList;
  }

  // gets all the edges for the maze
  ArrayList<Edge> getEdges(ArrayList<ArrayList<Vertex>> vertexList2D) {
    ArrayList<Edge> allEdges = new ArrayList<Edge>();
    for (ArrayList<Vertex> listVertex : vertexList2D) {
      for (Vertex vertex : listVertex) {
        for (Edge edge : vertex.aroundEdges) {
          allEdges.add(edge);
        }
      }
    }
    return allEdges;
  }

  // initializes the vertices for the maze
  ArrayList<ArrayList<Vertex>> initialVertices() {
    ArrayList<ArrayList<Vertex>> vertexList2D = new ArrayList<ArrayList<Vertex>>();
    for (int i = 0; i < WIDTH; i++) {
      ArrayList<Vertex> listVertex = new ArrayList<Vertex>();
      for (int j = 0; j < HEIGHT; j++) {
        listVertex.add(new Vertex(i, j));
      }
      vertexList2D.add(listVertex);
    }
    Random rand = new Random();
    for (ArrayList<Vertex> listVertex : vertexList2D) {
      for (Vertex vertex : listVertex) {
        if (vertex.x != WIDTH - 1) {
          vertex.aroundEdges.add(
              new Edge(vertex, vertexList2D.get(vertex.x + 1).get(vertex.y), rand.nextInt(1000)));
        }
        if (vertex.y != HEIGHT - 1) {
          vertex.aroundEdges.add(
              new Edge(vertex, vertexList2D.get(vertex.x).get(vertex.y + 1), rand.nextInt(1000)));
        }
        if (vertex.x != 0) {
          vertex.aroundEdges.add(
              new Edge(vertex, vertexList2D.get(vertex.x - 1).get(vertex.y), rand.nextInt(1000)));
        }
        if (vertex.y != 0) {
          vertex.aroundEdges.add(
              new Edge(vertex, vertexList2D.get(vertex.x).get(vertex.y - 1), rand.nextInt(1000)));
        }
      }
    }
    return vertexList2D;
  }

  // constructs the minimum spanning tree using kruskals algorithm
  void kruskalVertex(ArrayList<ArrayList<Vertex>> vertexList2D) {

    ArrayList<Edge> edgesInTree = getEdges(vertexList2D);
    HashMap<Integer, Integer> represenatives = new HashMap<Integer, Integer>();

    for (ArrayList<Vertex> vertList : vertexList2D) {
      for (Vertex vert : vertList) {
        vert.aroundEdges = new ArrayList<Edge>();
      }
    }

    ArrayList<Edge> allTrees = new ArrayList<Edge>();
    ArrayList<Edge> edgesInTreeSorted = sort(edgesInTree);
    for (int i = 0; i <= (25 * HEIGHT) + WIDTH; i++) {
      represenatives.put(i, i);
    }
    while (allTrees.size() < (HEIGHT * WIDTH) - 1) {
      Edge cheapestEdge = edgesInTreeSorted.get(0);

      if (this.find(represenatives, cheapestEdge.end.unique()) == this.find(represenatives,
          cheapestEdge.start.unique())) {

        edgesInTreeSorted.remove(0);

      } else {
        allTrees.add(cheapestEdge);
        cheapestEdge.start.aroundEdges.add(cheapestEdge);
        cheapestEdge.end.aroundEdges
            .add(new Edge(cheapestEdge.end, cheapestEdge.start, cheapestEdge.weight));

        int temp = (find(represenatives, cheapestEdge.end.unique()));

        represenatives.remove(find(represenatives, cheapestEdge.end.unique()));
        represenatives.put(temp, find(represenatives, cheapestEdge.start.unique()));
      }
    }
  }

  // finds the given key in the given hashmap with its value being the same as the
  // key
  int find(HashMap<Integer, Integer> hashmap, int key) {
    if (hashmap.get(key) == key) {
      return key;
    } else {
      return this.find(hashmap, hashmap.get(key));
    }
  }

  // sorts the given arraylist of edges using quick sort
  ArrayList<Edge> sort(ArrayList<Edge> listEdge) {
    if (listEdge.size() <= 1) {
      return listEdge;
    }
    
    IComparator<Edge> comp = new CompareEdges();

    return quickSort(listEdge, comp);
  }

  // sorts the given array using the given comparator with quicksort
  ArrayList<Edge> quickSort(ArrayList<Edge> arr, IComparator<Edge> comp) {
    return quickSortHelper(arr, comp, 0, arr.size());
  }

  // returns the partition integer
  int partition(ArrayList<Edge> source, IComparator<Edge> comp, int loIdx, int hiIdx, Edge pivot) {
    int curlo = loIdx;
    int curhi = hiIdx - 1;
    while (curlo < curhi) {
      while (curlo < hiIdx && comp.compareTo(source.get(curlo), pivot) <= 0) {
        curlo = curlo + 1;
      }
      while (curhi >= loIdx && comp.compareTo(source.get(curhi), pivot) > 0) {
        curhi = curhi - 1;
      }
      if (curlo < curhi) {
        Edge temp = source.get(curhi);
        source.set(curhi, source.get(curlo));
        source.set(curlo, temp);
      }
    }
    Edge temp2 = source.get(curhi);
    source.set(curhi, source.get(loIdx));
    source.set(loIdx, temp2);
    return curhi;
  }

  // helper method for quick sort algorithm
  ArrayList<Edge> quickSortHelper(ArrayList<Edge> source, IComparator<Edge> comp, int loIdx, int hiIdx) {

    if (loIdx >= hiIdx) {
      return source;
    }

    Edge pivot = source.get(loIdx);
    int pivotIndex = partition(source, comp, loIdx, hiIdx, pivot);
    quickSortHelper(source, comp, loIdx, pivotIndex);
    quickSortHelper(source, comp, pivotIndex + 1, hiIdx);

    return source;

  }

  // creates the color corresponding to which cell it is in the maze
  Color createColor(Vertex vertex) {
    if (vertex.x == WIDTH - 1 && vertex.y == HEIGHT - 1) {
      return Color.magenta;
    } else if (vertex.x == 0 && vertex.y == 0) {
      return Color.green;
    } else {
      return Color.gray;
    }
  }

  // creates the scene of the maze
  public WorldScene makeScene() {
    WorldScene ws = new WorldScene(WIDTH * SCALE, HEIGHT * SCALE);
    for (Vertex vertex : vertices) {
      Color color = createColor(vertex);
      ws.placeImageXY(new RectangleImage(SCALE, SCALE, OutlineMode.SOLID, color),
          (vertex.x * SCALE) + (SCALE * 1 / 2), (vertex.y * SCALE) + (SCALE * 1 / 2));
    }
    for (Edge edge : walls) {
      if (edge.end.x == edge.start.x) {
        ws.placeImageXY(new RectangleImage(SCALE, SCALE / 10, OutlineMode.SOLID, Color.black),
            (edge.end.x * SCALE) + (SCALE * 1 / 2),
            ((edge.end.y + edge.start.y) * SCALE / 2) + (SCALE * 1 / 2));
      } else {
        ws.placeImageXY(new RectangleImage(SCALE / 10, SCALE, OutlineMode.SOLID, Color.black),
            ((edge.end.x + edge.start.x) * SCALE / 2) + (SCALE * 1 / 2),
            (edge.end.y * SCALE) + (SCALE * 1 / 2));
      }
    }
    return ws;
  }
}

// Examples class for the maze
class ExamplesMaze {

  MazeItWorld mazeworld = new MazeItWorld();

  void testGame(Tester t) {
    MazeItWorld m = new MazeItWorld();
    m.bigBang(MazeItWorld.WIDTH * MazeItWorld.SCALE, MazeItWorld.HEIGHT * MazeItWorld.SCALE, 0.005);
  }

  // tests the sort method
  void testSort(Tester t) {
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(0, 10);
    Vertex v3 = new Vertex(5, 5);
    Vertex v4 = new Vertex(2, 4);
    Edge e1 = new Edge(v1, v2, 5);
    Edge e2 = new Edge(v3, v4, 2);
    Edge e3 = new Edge(v3, v1, 10);

    ArrayList<Edge> wall1 = new ArrayList<Edge>();
    wall1.add(e2);
    wall1.add(e1);
    wall1.add(e3);

    mazeworld.walls = new ArrayList<Edge>();
    mazeworld.walls.add(e1);
    mazeworld.walls.add(e2);
    mazeworld.walls.add(e3);

    t.checkExpect(mazeworld.sort(mazeworld.walls), wall1);

  }

  // tests the createColor method
  void testCreateColor(Tester t) {
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(mazeworld.WIDTH - 1, mazeworld.HEIGHT - 1);
    Vertex v3 = new Vertex(15, 15);

    t.checkExpect(mazeworld.createColor(v1), Color.green);
    t.checkExpect(mazeworld.createColor(v2), Color.magenta);
    t.checkExpect(mazeworld.createColor(v3), Color.gray);
  }

  // tests the Find method
  void testFind(Tester t) {
    HashMap<Integer, Integer> hashmap = new HashMap<Integer, Integer>();
    hashmap.put(0, 0);
    hashmap.put(1, 1);
    hashmap.put(2, 2);

    t.checkExpect(mazeworld.find(hashmap, 0), 0);
    t.checkExpect(mazeworld.find(hashmap, 1), 1);
    t.checkExpect(mazeworld.find(hashmap, 2), 2);

  }

  // tests the kruskal's vertex algorithm
  void testKruskalVertex(Tester t) {

  }

  // tests the edge compare method
  void testEdgeCompare(Tester t) {

  }

  // tests the getWalls method
  void testGetWalls(Tester t) {

  }

  // tests the getEdges method
  void testGetEdges(Tester t) {

  }

  // tests the initialVertices method
  void testInitialVertices(Tester t) {

  }
}
