// Nilay Barde
/*
 * HOW TO PLAY:
 * Press "p" to start to manually solve the maze
 * Use arrow keys to move around the player on the maze
 * Press "b" to see the breadth first search algorithm
 * Press "d" to see the depth first search algorithm
 * Press "v" to toggle in seeing the visited vertices
 * Press "r" to reset the maze
 * 
 */
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.*;

// represents a player in the game
class Player {
  Vertex curr;
  int a;

  Player() {
    this.curr = new Vertex(0, 0);
    this.a = 0;
  }

  // where player moves when moveLeft is called
  void moveLeft(ArrayList<Vertex> vertices, ArrayList<Edge> walls) {
    if (a - MazeItWorld.HEIGHT < 0 || a - MazeItWorld.HEIGHT > vertices.size()) {
      return;
    }
    for (Edge e : walls) {
      if (e.start.y == curr.y && e.end.y == curr.y
          && ((e.start.x == curr.x && e.end.x == vertices.get(a - MazeItWorld.HEIGHT).x)
              || (e.end.x == curr.x && e.start.x == vertices.get(a - MazeItWorld.HEIGHT).x))) {
        return;
      }
    }
    curr.onPath = false;
    a = a - MazeItWorld.HEIGHT;
    curr = vertices.get(a);
    curr.visited = true;
    curr.onPath = true;
  }

  // where player moves when moveRight is called
  void moveRight(ArrayList<Vertex> vertices, ArrayList<Edge> walls) {
    if (a + MazeItWorld.HEIGHT >= vertices.size()) {
      return;
    }
    for (Edge e : walls) {
      if (e.start.y == curr.y && e.end.y == curr.y
          && ((e.start.x == curr.x && e.end.x == vertices.get(a + MazeItWorld.HEIGHT).x)
              || (e.end.x == curr.x && e.start.x == vertices.get(a + MazeItWorld.HEIGHT).x))) {
        return;
      }
    }
    curr.onPath = false;
    a = a + MazeItWorld.HEIGHT;
    curr = vertices.get(a);
    curr.visited = true;
    curr.onPath = true;
  }

  // where player moves when moveUp is called
  void moveUp(ArrayList<Vertex> vertices, ArrayList<Edge> walls) {
    if (a % MazeItWorld.WIDTH == 0 || a == 0) {
      return;
    }
    for (Edge e : walls) {
      if (e.start.x == curr.x && e.end.x == curr.x
          && ((e.start.y == curr.y && e.end.y == vertices.get(a - 1).y)
              || (e.end.y == curr.y && e.start.y == vertices.get(a - 1).y))) {
        return;
      }
    }
    curr.onPath = false;
    a = a - 1;
    curr = vertices.get(a);
    curr.visited = true;
    curr.onPath = true;

  }

  // where player moves when moveDown is called
  void moveDown(ArrayList<Vertex> vertices, ArrayList<Edge> walls) {
    if ((a + 1) % MazeItWorld.WIDTH == 0) {
      return;
    }
    for (Edge e : walls) {
      if (e.start.x == curr.x && e.end.x == curr.x
          && ((e.start.y == curr.y && e.end.y == vertices.get(a + 1).y)
              || (e.end.y == curr.y && e.start.y == vertices.get(a + 1).y))) {
        return;
      }
    }
    curr.onPath = false;
    a = a + 1;
    curr = vertices.get(a);
    curr.visited = true;
    curr.onPath = true;

  }
}

// represents a vertex in the maze
class Vertex {
  int x;
  int y;
  boolean visited;
  boolean onPath;
  ArrayList<Edge> aroundEdges;
  Vertex left;
  Vertex right;
  Vertex bottom;
  Vertex top;

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

  HashMap<Integer, Edge> cameFromEdge = new HashMap<Integer, Edge>();
  Queue<Vertex> worklist = new LinkedList<Vertex>();
  Stack<Vertex> worklist2 = new Stack<Vertex>();
  Player player = new Player();
  ArrayList<Vertex> vertices;
  ArrayList<Edge> walls;
  static final int HEIGHT = 25;
  static final int WIDTH = 25;
  static final int SCALE = 25;
  Random rand = new Random(24);
  boolean visitedToggle = false;
  boolean b = false;
  boolean d = false;
  boolean isSolved = false;

  MazeItWorld() {
    initialize();
    resetLists();
  }

  MazeItWorld(Random rand) {
    initialize();
    this.rand = rand;
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

  // creates a HashMap of the lowest weight vertices
  void kruskalVertex(ArrayList<ArrayList<Vertex>> vertexList2D) {

    HashMap<Integer, Integer> represenatives = new HashMap<Integer, Integer>();
    ArrayList<Edge> allEdgesSorted = sort(getEdges(vertexList2D));

    for (ArrayList<Vertex> vertList : vertexList2D) {
      for (Vertex vert : vertList) {
        vert.aroundEdges = new ArrayList<Edge>();
      }
    }

    for (int i = 0; i <= (1000 * HEIGHT) + WIDTH; i++) {
      represenatives.put(i, i);
    }

    int totalCells = HEIGHT * WIDTH;

    ArrayList<Edge> holder = new ArrayList<Edge>();

    while (holder.size() < totalCells - 1) {
      Edge cheapestEdge = allEdgesSorted.get(0);

      if (this.find(represenatives, cheapestEdge.end.unique()) == this.find(represenatives,
          cheapestEdge.start.unique())) {

        allEdgesSorted.remove(0);

      } else {
        holder.add(cheapestEdge);
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

    IComparator comp = new CompareEdges();

    return quickSort(listEdge, comp);
  }

  // sorts the given array using the given comparator with quicksort
  ArrayList<Edge> quickSort(ArrayList<Edge> arr, IComparator comp) {
    return quickSortHelper(arr, comp, 0, arr.size());
  }

  // returns the partition integer
  int partition(ArrayList<Edge> source, IComparator comp, int loIdx, int hiIdx, Edge pivot) {
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
  ArrayList<Edge> quickSortHelper(ArrayList<Edge> source, IComparator comp, int loIdx, int hiIdx) {

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
    } else if (vertex.onPath && vertex.visited) {
      return Color.blue;
    } else if (vertex.visited) {
      return Color.cyan;
    } else {
      return Color.gray;
    }
  }

  // creates the scene of the maze
  public WorldScene makeScene() {
    WorldScene ws = new WorldScene(WIDTH * SCALE, HEIGHT * SCALE);
    Color color;
    for (Vertex vertex : vertices) {
      if (visitedToggle) {
        color = toggleVertices(vertex);
      } else {
        color = createColor(vertex);
      }
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

  // when the user clicks "r" it will reset the maze
  // Effect: generates a new board when "r" is hit and resets count to 0
  public void onKeyEvent(String ke) {
    if (ke.equals("r")) {
      this.initialize();
      player.a = 0;
    } else if (ke.equals("left")) {
      this.player.moveLeft(vertices, walls);
    } else if (ke.equals("up")) {
      this.player.moveUp(vertices, walls);
    } else if (ke.equals("right")) {
      this.player.moveRight(vertices, walls);
    } else if (ke.equals("down")) {
      this.player.moveDown(vertices, walls);
    } else if (ke.equals("v")) {
      if (visitedToggle) {
        visitedToggle = false;
      } else {
        visitedToggle = true;
      }
      this.makeScene();
    } else if (ke.equals("b")) {
      resetVertices();
      resetLists();
      b = true;
      d = false;
      worklist.add(vertices.get(0));
    } else if (ke.equals("d")) {
      resetLists();
      resetVertices();
      b = false;
      d = true;
      worklist2.add(vertices.get(0));
    } else if (ke.equals("p")) {
      resetLists();
      resetVertices();
      b = false;
      d = false;
    }
  }

  // resets the onPath and visited booleans on all of the vertices
  public void resetVertices() {
    for (Vertex v : vertices) {
      v.onPath = false;
      v.visited = false;
    }
    
  }
  
  public void resetLists() {
    cameFromEdge.clear();
    worklist.clear();
    worklist2.clear();
  }

  // on Tick method
  public void onTick() {
    // if breadth first search is requested
    if (b) {
      if (!(worklist.isEmpty())) {
        Vertex next = worklist.poll();
        if (next.equals(vertices.get(vertices.size() - 1))) {
          isSolved = true;
          reconstruct(cameFromEdge, next);
        } else if (!(next.visited)) {
          next.visited = true;
          for (Edge e : next.aroundEdges) {
            if (!(next.equals(e.start)) && !(e.start.visited)) {
              worklist.add(e.start);
              cameFromEdge.put(e.start.unique(), e);
            } else if (!(e.end.visited)) {
              worklist.add(e.end);
              cameFromEdge.put(e.end.unique(), e);
            }
          }
        }
      }
    }
    // if depth first search is requested
    if (d) {
      if (!(worklist2.isEmpty())) {
        Vertex next = worklist2.pop();
        if (next.equals(vertices.get(vertices.size() - 1))) {
          isSolved = true;
          reconstruct(cameFromEdge, next);
        } else if (!(next.visited)) {
          next.visited = true;
          for (Edge e : next.aroundEdges) {
            if (!(next.equals(e.start)) && !(e.start.visited)) {
              worklist2.add(e.start);
              cameFromEdge.put(e.start.unique(), e);
            } else if (!(e.end.visited)) {
              worklist2.add(e.end);
              cameFromEdge.put(e.end.unique(), e);
            }
          }
        }
      }
    }
  }

  // constructs our path to the start from the end
  void reconstruct(HashMap<Integer, Edge> h, Vertex next) {
    Vertex curr = next;
    while (!curr.equals(vertices.get(0))) {
      curr.onPath = true;
      curr = h.get(curr.unique()).start;
    }
  }

  // changes the colors for toggling the visited vertices
  Color toggleVertices(Vertex vertex) {
    if (vertex.x == WIDTH - 1 && vertex.y == HEIGHT - 1) {
      return Color.magenta;
    } else if (vertex.x == 0 && vertex.y == 0) {
      return Color.green;
    } else if (vertex.onPath && vertex.visited) {
      return Color.blue;
    } else {
      return Color.gray;
    }
  }

  // ends the world
  public WorldEnd worldEnds() {

    WorldImage text = new TextImage("You Win!", 50, FontStyle.BOLD_ITALIC, Color.BLACK);
    WorldScene ws2 = this.lastScene("");
    if (this.player.curr.x == WIDTH - 1 && this.player.curr.y == HEIGHT - 1) {
      ws2.placeImageXY(text, 250, 250);
      return new WorldEnd(true, ws2);
    }
    if (isSolved) {
      ws2.placeImageXY(text, 250, 250);
      return new WorldEnd(true, ws2);
    }
    return this.lastWorld;

  }
}

// Examples class for the maze
class ExamplesMaze {

  Random rand1 = new Random(24);
  MazeItWorld mazeworld = new MazeItWorld(rand1);
  Player player1 = new Player();

  void testGame(Tester t) {
    MazeItWorld m = new MazeItWorld();
    m.bigBang(MazeItWorld.WIDTH * MazeItWorld.SCALE, MazeItWorld.HEIGHT * MazeItWorld.SCALE,
        0.0005);
  }

  // initializes data
  void initdata() {
    this.mazeworld = new MazeItWorld(new Random(24));

  }

  // tests the sort method
  void testSort(Tester t) {
    this.initdata();
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
    this.initdata();
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(MazeItWorld.WIDTH - 1, MazeItWorld.HEIGHT - 1);
    Vertex v3 = new Vertex(15, 15);

    t.checkExpect(mazeworld.createColor(v1), Color.green);
    t.checkExpect(mazeworld.createColor(v2), Color.magenta);
    t.checkExpect(mazeworld.createColor(v3), Color.gray);
  }

  // tests the Find method
  void testFind(Tester t) {
    this.initdata();
    HashMap<Integer, Integer> hashmap = new HashMap<Integer, Integer>();
    hashmap.put(0, 0);
    hashmap.put(1, 1);
    hashmap.put(2, 2);

    t.checkExpect(mazeworld.find(hashmap, 0), 0);
    t.checkExpect(mazeworld.find(hashmap, 1), 1);
    t.checkExpect(mazeworld.find(hashmap, 2), 2);

  }

  // tests the edge compare method
  void testEdgeCompare(Tester t) {
    this.initdata();
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(0, 10);
    Vertex v3 = new Vertex(5, 5);
    Vertex v4 = new Vertex(2, 4);
    Edge e1 = new Edge(v1, v2, 5);
    Edge e2 = new Edge(v3, v4, 2);
    Edge e3 = new Edge(v3, v1, 10);
    IComparator comp = new CompareEdges();

    t.checkExpect(comp.apply(e2, e1), true);
    t.checkExpect(comp.apply(e3, e1), false);
    t.checkExpect(comp.apply(e3, e3), false);

  }

  // test worldEnds
  void worldEnds(Tester t) {
    this.initdata();
    t.checkExpect(new MazeItWorld().worldEnds(), false);
    this.initdata();
    this.mazeworld.onKeyEvent("b");
    t.checkExpect(this.mazeworld.worldEnds(), true);
    this.initdata();
    this.mazeworld.onKeyEvent("d");
    t.checkExpect(this.mazeworld.worldEnds(), true);
  }

  // changes the colors for toggling the visited vertices
  void testtoggleVertices(Tester t) {
    this.initdata();
    this.mazeworld.onKeyEvent("right");
    t.checkExpect(mazeworld.toggleVertices(mazeworld.vertices.get(624)), Color.magenta);
    t.checkExpect(mazeworld.toggleVertices(mazeworld.vertices.get(0)), Color.green);
    t.checkExpect(mazeworld.toggleVertices(mazeworld.vertices.get(20)), Color.gray);
    t.checkExpect(mazeworld.toggleVertices(mazeworld.vertices.get(25)), Color.blue);
  }

  // testOnKeyEvent
  void testOnKeyEvent(Tester t) {
    this.initdata();
    this.mazeworld.onKeyEvent("r");
    t.checkExpect(this.mazeworld.vertices.get(1).visited, false);
    this.mazeworld.onKeyEvent("left");
    t.checkExpect(this.mazeworld.vertices.get(0).visited, false);
    this.mazeworld.onKeyEvent("up");
    t.checkExpect(this.mazeworld.vertices.get(0).visited, false);
    this.mazeworld.onKeyEvent("down");
    t.checkExpect(this.mazeworld.vertices.get(24).visited, false);
    this.mazeworld.onKeyEvent("right");
    t.checkExpect(this.mazeworld.vertices.get(1).visited, true);
  }

  // test resetvertices
  void testresetVertices(Tester t) {
    this.initdata();
    this.mazeworld.resetVertices();
    t.checkExpect(this.mazeworld.vertices.get(1).visited, false);
    t.checkExpect(this.mazeworld.vertices.get(0).visited, false);
  }
}
