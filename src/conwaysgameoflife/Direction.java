/**
 * @author Ben Y
 */
package conwaysgameoflife;
    
public enum Direction {
    NORTH(0, 0, 1),
    NORTHEAST(1, 1, 1),
    EAST(2, 1, 0),
    SOUTHEAST(3, 1, -1),
    SOUTH(4, 0, -1),
    SOUTHWEST(5, -1, -1),
    WEST(6, -1, 0),
    NORTHWEST(7, -1, 1);

    private int x, y, bit;
    private Direction(int bit, int x, int y) {
	this.bit = bit;
	this.x = x;
	this.y = y;
    }

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }
    
    public int getBit() {
	return bit;
    }
}