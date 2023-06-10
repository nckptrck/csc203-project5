import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface PathingStrategy {
    /**
     * Returns a prefix of a path from the start point to a point within reach
     * of the end point. This path is only valid ("clear") when returned, but
     * may be invalidated by movement of other entities. The prefix includes
     * neither the start nor end points.
     * 
     * @param start the point to begin the search from
     * @param end the point to search for a point within reach of
     * @param canPassThrough a function that returns true if the given point is traversable
     * @param withinReach a function that returns true if both points are within reach of each other
     * @param potentialNeighbors a function that returns the neighbors of a given point, as a stream
     */
    List<Point> computePath(
        Point start,
        Point end,
        Predicate<Node> canPassThrough, BiPredicate<Node, Point> withinReach, Function<Node, Stream<Node>> potentialNeighbors
    );

    /**
     * A static constant: it's a lambda function that returns neighbors of a given point as a stream.
     * Example Usage:
     * Stream<Point> neighbors = PathingStrategy.CARDINAL_NEIGHBORS.apply(new Point(0, 0));
     */
    static final Function<Node, Stream<Node>> CARDINAL_NEIGHBORS =
            node ->
                    Stream.<Node>builder()

                            .add(new Node(new Point(node.p.x + 1, node.p.y)))
                            .add(new Node(new Point(node.p.x, node.p.y + 1)))
                            .add(new Node(new Point(node.p.x, node.p.y - 1)))
                            .add(new Node(new Point(node.p.x - 1, node.p.y)))

                            .build();


    static final Function<Point, Stream<Point>> CARDINAL_NEIGHBORS_POINTS =
            p ->
                    Stream.<Point>builder()

                            .add(new Point(p.x + 1, p.y))
                            .add(new Point(p.x, p.y + 1))
                            .add(new Point(p.x, p.y - 1))
                            .add(new Point(p.x - 1, p.y))

                            .build();
}