/**
 * @author Ben Y
 */
package conwaysgameoflife;

import java.util.concurrent.ConcurrentHashMap;

public class Grid extends ConcurrentHashMap<Integer, Integer> {
    
    public static Integer getAddress(int x, int y) {
	return (Integer)((x << ConwaysGameOfLife.COORD_SHIFT) | (y & ConwaysGameOfLife.COORD_MASK));
    }
    
    public static short[] coordsForKey(int key) {
	short xForCell = (short)((key >> ConwaysGameOfLife.COORD_SHIFT) & ConwaysGameOfLife.COORD_MASK);
	short yForCell = (short)(key & ConwaysGameOfLife.COORD_MASK);
	return new short[] { xForCell, yForCell };
    }
}
