import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy{
    private PriorityQueue<Node> openSet;
    private List<Node> closedSet;


    private final WorldModel world;

    public AStarPathingStrategy(WorldModel world) {
        this.world = world;
    }

    @Override
    public List<Point> computePath(Point start, Point end, Predicate<Node> canPassThrough, BiPredicate<Node, Point> withinReach, Function<Node, Stream<Node>> potentialNeighbors) {

        Node s = new Node(start);
        s.gCost = 0;
        s.hCost = s.p.manhattanDistance(end);

        s.fCost = s.gCost + s.hCost;


        openSet = new PriorityQueue<>(new NodeComparator());
        closedSet = new ArrayList<Node>();

        openSet.add(s);
        while (!openSet.isEmpty()) {

            Node current = openSet.poll();
            openSet.remove(current);

            closedSet.add(current);


            if (withinReach.test(current, end)) {
                return ConstructPath(current);
            } else {
                Stream<Node> neighborStream = potentialNeighbors.apply(current);
                List<Node> neighborList = neighborStream
                        .filter(canPassThrough)
                        .toList();


                for (Node neighbor : neighborList) {

                    if (!closedSet.contains(neighbor)) {

                        int tentativeGCost = current.gCost + 1;

                        if (!openSet.contains(neighbor) || tentativeGCost < neighbor.gCost) {
                            neighbor.gCost = tentativeGCost;
                            neighbor.hCost = neighbor.p.manhattanDistance(end);
                            neighbor.fCost = neighbor.gCost + neighbor.hCost;
                            neighbor.last = current;

                            if (!openSet.contains(neighbor)) {
                                openSet.add(neighbor);
                            }




                        }
                    }
                }
            }
        }
            return new ArrayList<>();

    }

        public List<Point> ConstructPath(Node current){
            List<Point> path = new ArrayList<>();
            while (current.last != null) {
                path.add(0,current.p);
                current = current.last;
            }
            return path;
        }
    }






