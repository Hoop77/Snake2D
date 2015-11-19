package com.snake2D.game.tile;

import java.awt.*;

/**
 * Created by philipp on 12.11.15.
 */
public class Tile
{
	public static final int FREE = -1;
	
    public int x = FREE;
    public int y = FREE;

    public Tile() {}

    public Tile( int x, int y )
    {
        this.x = x;
        this.y = y;
    }
}
