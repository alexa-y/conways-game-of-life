/**
 * @author Ben Y
 */
package conwaysgameoflife;

import java.util.Map.Entry;
import java.util.Random;

/**
 * TODO: Add cell recycling 
 */

public class ConwaysGameOfLife {
    
    public class Config {
	// Will stop execution of the program when net number of alive cells remains consistent between generations
	// Execution will always halt if 0 alive cells remain
	public static final boolean STOP_WHEN_STAGNATED = true;
	
	// Characters to be printed when printCurrentGeneration() is called
	public static final char ALIVE_CHAR = '&';
	public static final char DEAD_CHAR = '-';
	
	// Print current generation every X generations
	public static final int PRINT_FREQUENCY = 10;
	
	public static final int MINIMUM_STARTING_CELLS = 10;
	public static final int MAXIMUM_STARTING_CELLS = 100;
	
	public static final int STARTING_DIMENSIONS = 20;
    }
    
    public static final int COORD_MASK = 0xFFFF;
    public static final int COORD_SHIFT = 16;
    
    public static final int STATE_MASK = 0x1;
    
    public static final int NEIGHBOR_MASK = 0xFF;
    public static final int NEIGHBOR_SHIFT = 1;
    
    private static ConwaysGameOfLife singleton;
    
    private Grid currentGeneration;
    
    private int generation = 1;
    private int aliveCells = 0;

    public static void main(String[] args) {
	ConwaysGameOfLife.getSingleton().start(System.currentTimeMillis());
    }
    
    public static ConwaysGameOfLife getSingleton() {
	if (singleton == null)
	    singleton = new ConwaysGameOfLife();
	return singleton;
    }
    
    public void start(long seed) {
	System.out.println("Using seed: " + seed);
	currentGeneration = new Grid();
	Random r = new Random(seed);
	int liveCells = r.nextInt(Config.MAXIMUM_STARTING_CELLS - Config.MINIMUM_STARTING_CELLS) + Config.MINIMUM_STARTING_CELLS;
	for (int i = 0; i < liveCells; i++) {
	    int x = (Config.STARTING_DIMENSIONS / 2) - r.nextInt(Config.STARTING_DIMENSIONS);
	    int y = (Config.STARTING_DIMENSIONS / 2) - r.nextInt(Config.STARTING_DIMENSIONS);
	    currentGeneration.put(Grid.getAddress(x, y), 1);
	}
	aliveCells = currentGeneration.size();
	calculateNeighbors();
	printCurrentGeneration();
	int lastCells = -1;
	while (aliveCells > 0 && (aliveCells != lastCells || !Config.STOP_WHEN_STAGNATED)) {
	    lastCells = aliveCells;
	    calculateNeighbors();
	    incrementGeneration();
	    if (generation % Config.PRINT_FREQUENCY == 0)
		printCurrentGeneration();
	}
	printCurrentGeneration();
    }
    
    public void calculateNeighbors() {
	currentGeneration.entrySet().stream().forEach((e) -> {
	    int curVal = e.getValue();
	    
	    // Reset bits 1-9, as these are being recalculated in this method
	    curVal = (curVal | (NEIGHBOR_MASK << NEIGHBOR_SHIFT)) ^ (NEIGHBOR_MASK << NEIGHBOR_SHIFT);
	    int mask = 0;
	    
	    // extract x and y for current cell
	    short xForCell = (short)((e.getKey() >> COORD_SHIFT) & COORD_MASK);
	    short yForCell = (short)(e.getKey() & COORD_MASK);
	    for (Direction d : Direction.values()) {
		Integer cell = currentGeneration.get(Grid.getAddress(xForCell + d.getX(), yForCell + d.getY()));
		if (cell == null) {
		    if ((curVal & STATE_MASK) > 0)
			currentGeneration.put(Grid.getAddress(xForCell + d.getX(), yForCell + d.getY()), 0);
		    continue;
		}
		
		mask |= (cell & STATE_MASK) << d.getBit();
	    }
	    e.setValue(curVal | (mask << NEIGHBOR_SHIFT));
	});
    }
    
    public void incrementGeneration() {
	generation++;
	aliveCells = 0;
	currentGeneration.entrySet().stream().forEach((i) -> {
	    int val = i.getValue();
	    int neighbors = (val >> NEIGHBOR_SHIFT) & NEIGHBOR_MASK;
	    int neighborCount = Integer.bitCount(neighbors);
	    if ((val & STATE_MASK) > 0) { // alive cell
		if (neighborCount < 2 || neighborCount > 3) // current cell dies
		    val = (val | STATE_MASK) ^ STATE_MASK;
		else if (neighborCount == 2 || neighborCount == 3)
		    val |= STATE_MASK;
	    } else { // dead cell
		if (neighborCount == 3)
		    val |= STATE_MASK;
	    }
	    aliveCells += val & STATE_MASK; // increments by 1 if current cell is alive
	    i.setValue(val);
	});
    }
    
    public void printCurrentGeneration() {
	short minX = 0, minY = 0, maxX = 0, maxY = 0;
	int totalCells = currentGeneration.size();
	// Acquire grid bounds for printing
	for (Entry<Integer, Integer> i : currentGeneration.entrySet()) {
	    short[] coords = Grid.coordsForKey(i.getKey());
	    if (coords[0] > maxX)
		maxX = coords[0];
	    if (coords[0] < minX)
		minX = coords[0];
	    if (coords[1] > maxY)
		maxY = coords[1];
	    if (coords[1] < minY)
		minY = coords[1];
	}
	System.out.println("Generation: " + generation);
	System.out.println("Alive cells: " + aliveCells);
	System.out.println("Total cells: " + totalCells);
	System.out.println("Max X: " + maxX + " Max Y: " + maxY + " Min X: " + minX + " Min Y: " + minY);
	for (int y = maxY; y >= minY; y--) {
	    StringBuilder sb = new StringBuilder().append('|');
	    for (int x = minX; x <= maxX; x++) {
		Integer cell = currentGeneration.get(Grid.getAddress(x, y));
		if (cell == null)
		    sb.append(" |");
		else if ((cell & STATE_MASK) > 0)
		    sb.append(Config.ALIVE_CHAR).append("|");
		else
		    sb.append(Config.DEAD_CHAR).append("|");
	    }
	    System.out.println(sb.toString());
	}
    }
    
}
