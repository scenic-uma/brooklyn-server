package brooklyn.location;

/**
 * A range of ports (indicator for Location and other APIs).
 * Using BasicPortRange this is adaptable from a number, a string, or a collection of numbers or a strings.
 * String may be of the form:
 *   <li> "80": just 80
 *   <li> "8080-8090": limited range sequentially; ie try 8080, then 8081, ..., then 8090, then give up
 *   <li> "8080-8000": as above, but descending; ie try 8080, then 8079, ..., then 8000, then give up
 *   <li> "8000+": unlimited range sequentially; ie try 8000, then 8001, then 8002, etc
 *   <li> "80,8080,8000,8080-8099": different ranges, in order; ie try 80, then 8080, then 8000, then 8080 (again), then 8081, ..., then 8099, then give up
 */
public interface PortRange extends Iterable<Integer> {
}
