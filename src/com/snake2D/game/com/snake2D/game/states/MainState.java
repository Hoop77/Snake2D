package com.snake2D.game.com.snake2D.game.states;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

import com.snake2D.game.basic.Game;
import com.snake2D.game.basic.Options;
import com.snake2D.game.tile.Tile;

/**
 * Created by philipp on 12.11.15.
 */
public class MainState extends GameState
{
    private Tile[] tiles = new Tile[ Game.TILES_X * Game.TILES_Y ];

    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;

    private int direction;

    private static long stepTime;

    private long time = 0;

    private int headX, headY;

    private int bodyLength = 1;

    private Tile food;

    private boolean gameOver = false;

    private Random random;

    public MainState( GameStateManager gameStateManager )
    {
        super( gameStateManager );
    }

    @Override
    public void init()
    {
        stepTime = Options.getStepTime();

        for( int i = 0; i < Game.TILES_X * Game.TILES_Y; i++ )
        {
            tiles[ i ] = new Tile();
        }

        headX = 5;
        headY = 5;
        direction = DOWN;

        setTile( headX, headY, headX, headY - 1 );

        food = new Tile( 10, 10 );

        random = new Random();
    }

    private void setTile( int xt, int yt, int x, int y )
    {
        tiles[ yt * Game.TILES_Y + xt ].x = x;
        tiles[ yt * Game.TILES_Y + xt ].y = y;
    }

    private void freeTile( int xt, int yt )
    {
        tiles[ yt * Game.TILES_X + xt ].x = Tile.FREE;
    }

    private Tile getTile( int xt, int yt )
    {
        return tiles[ yt * Game.TILES_X + xt ];
    }

    @Override
    public void update( float updateRatio )
    {
        if( gameOver )
            return;

        handleInput();

        if( System.currentTimeMillis() - time >= stepTime )
        {
            nextStep();
        }
    }

    private void nextStep()
    {
        updateBody();
        updateHead();

        time = System.currentTimeMillis();
    }

    private void log()
    {
        int xt = headX;
        int yt = headY;

        Tile currTile = getTile( xt, yt );

        while( currTile.x != Tile.FREE )
        {
            System.out.println( xt + "," + yt + " -> " + currTile.x + "," + currTile.y );

            xt = currTile.x;
            yt = currTile.y;

            currTile = getTile( xt, yt );
        }

        System.out.println( "" );
    }

    private void updateBody()
    {
        int xt = headX;
        int yt = headY;

        Tile currTile;

        while( xt != Tile.FREE )
        {
            currTile = getTile( xt, yt );                       // get the next tile

            Tile nextTile = getTile( currTile.x, currTile.y );  // get the tile which the current tile is pointing at

            if( nextTile.x == Tile.FREE )                       // if that tile is free -> the current tile is the end of the snake
            {
                freeTile( xt, yt );                             // free that tile
            }

            xt = currTile.x;                                    // get x-coordinate of the next tile
            yt = currTile.y;                                    // get y-coordinate of the next tile
        }
    }

    private void updateHead()
    {
        // coordinates for next head position
        int nextHeadX = headX;
        int nextHeadY = headY;

        // evaluate direction and change position accordingly
        if( direction == UP )
            nextHeadY--;
        else if( direction == DOWN )
            nextHeadY++;
        else if( direction == LEFT )
            nextHeadX--;
        else if( direction == RIGHT )
            nextHeadX++;
        else
            ;

        // check out of bounds x
        if( nextHeadX >= Game.TILES_X )
            nextHeadX = 0;
        else if( nextHeadX < 0 )
            nextHeadX = Game.TILES_X - 1;
        else
            ;

        // check out of bounds y
        if( nextHeadY >= Game.TILES_Y )
            nextHeadY = 0;
        else if( nextHeadY < 0 )
            nextHeadY = Game.TILES_Y - 1;
        else
            ;

        // check collision with existing tile
        Tile nextTile = getTile( nextHeadX, nextHeadY );
        if( nextTile.x != Tile.FREE )
        {
            gameOver = true;
            return;
        }

        // move head
        setTile( nextHeadX, nextHeadY, headX, headY );

        // update stored head coordinates
        headX = nextHeadX;
        headY = nextHeadY;

        // if we collected food ...
        if( headX == food.x && headY == food.y )
        {
            // generate new food
            generateFood();

            // increase body length
            bodyLength++;

            // update the head one more time
            updateHead();
        }
    }

    private void generateFood()
    {
        // get all free tiles by subtracting the body length from all available tiles
        int freeTilesCount = Game.TILES_X * Game.TILES_Y - bodyLength;

        // array which stores indices of free tiles
        Integer[] freeTiles = new Integer[ freeTilesCount ];

        // get all free indices
        int i = 0;                                      // current index
        for( int y = 0; y < Game.TILES_Y; y++ )
        {
            for( int x = 0; x < Game.TILES_X; x++ )
            {
                if( getTile( x, y ).x != Tile.FREE )
                {
                    freeTiles[ i ] = y * Game.TILES_X + x;
                    i++;
                }
            }
        }

        // get random index
        int randomIndex = random.nextInt( freeTilesCount );

        // update food tile
        food.x = randomIndex % Game.TILES_X;
        food.y = randomIndex / Game.TILES_X;
    }

    private void handleInput()
    {
        if( Game.keyboardInputHandler.isKeyPressed( KeyEvent.VK_UP ) && direction != DOWN )
            direction = UP;
        else if( Game.keyboardInputHandler.isKeyPressed( KeyEvent.VK_DOWN ) && direction != UP )
            direction = DOWN;
        else if( Game.keyboardInputHandler.isKeyPressed( KeyEvent.VK_LEFT ) && direction != RIGHT )
            direction = LEFT;
        else if( Game.keyboardInputHandler.isKeyPressed( KeyEvent.VK_RIGHT ) && direction != LEFT )
            direction = RIGHT;
    }

    @Override
    public void render( Graphics2D graphics2D )
    {
        // render food
        graphics2D.setColor( Options.foodColor );
        graphics2D.fillRect( food.x * Game.TILE_SIZE, food.y * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE );

        int xt = headX;
        int yt = headY;

        Tile currTile = getTile( xt, yt );

        while( currTile.x != Tile.FREE )
        {
            graphics2D.setColor( Options.snakeColor );
            graphics2D.fillRect( xt * Game.TILE_SIZE, yt * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE );

            xt = currTile.x;
            yt = currTile.y;

            currTile = getTile( xt, yt );
        }
    }

    @Override
    public void dispose()
    {

    }
}