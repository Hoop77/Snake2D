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

    private int inputDirection = DOWN;
    private int moveDirection = DOWN;

    private int moveStepTime;

    private int getFluidMotionStepTime;
    private int fluidMotionStep = 0;

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
        moveStepTime = Options.getStepTime();

        getFluidMotionStepTime = moveStepTime / Game.TILE_SIZE;

        for( int i = 0; i < Game.TILES_X * Game.TILES_Y; i++ )
        {
            tiles[ i ] = new Tile();
        }

        headX = 5;
        headY = 5;

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

        long deltaTime = System.currentTimeMillis() - time;

        fluidMotionStep = ( int ) deltaTime / getFluidMotionStepTime;

        if( deltaTime >= moveStepTime )
        {
            nextStep();
        }
    }

    private void handleInput()
    {
        if( Game.keyboardInputHandler.isKeyPressed( KeyEvent.VK_UP ) && moveDirection != DOWN )
            inputDirection = UP;
        else if( Game.keyboardInputHandler.isKeyPressed( KeyEvent.VK_DOWN ) && moveDirection != UP )
            inputDirection = DOWN;
        else if( Game.keyboardInputHandler.isKeyPressed( KeyEvent.VK_LEFT ) && moveDirection != RIGHT )
            inputDirection = LEFT;
        else if( Game.keyboardInputHandler.isKeyPressed( KeyEvent.VK_RIGHT ) && moveDirection != LEFT )
            inputDirection = RIGHT;
    }

    private void nextStep()
    {
        updateBody();
        updateHead();

        // now, that the snake was updated, reset the fluid motion step
        fluidMotionStep = 0;

        time = System.currentTimeMillis();
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

        // evaluate move direction and change position accordingly
        if( moveDirection == UP )
            nextHeadY--;
        else if( moveDirection == DOWN )
            nextHeadY++;
        else if( moveDirection == LEFT )
            nextHeadX--;
        else if( moveDirection == RIGHT )
            nextHeadX++;
        else
            ;

        // update move direction
        moveDirection = inputDirection;

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

    @Override
    public void render( Graphics2D graphics2D )
    {
        // render food
        graphics2D.setColor( Options.foodColor );
        graphics2D.fillRect( food.x * Game.TILE_SIZE, food.y * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE );

        int xPrev = headX;
        int yPrev = headY;

        if( moveDirection == UP )
            yPrev--;
        else if( moveDirection == DOWN )
            yPrev++;
        else if( moveDirection == LEFT )
            xPrev--;
        else if( moveDirection == RIGHT )
            xPrev++;
        else
            ;

        int xCurr = headX;
        int yCurr = headY;

        Tile currTile = getTile( xCurr, yCurr );

        int xNext = currTile.x;
        int yNext = currTile.y;

        int xOffset = 0;
        int yOffset = 0;

        while( xNext != Tile.FREE )
        {
            // move up
            if( yCurr > yPrev )
            {
                xOffset = 0;
                yOffset = -fluidMotionStep;
            }
            // move down
            else if( yCurr < yPrev )
            {
                xOffset = 0;
                yOffset = fluidMotionStep;
            }
            // move left
            else if( xCurr > xPrev )
            {
                xOffset = -fluidMotionStep;
                yOffset = 0;
            }
            // move right
            else if( xCurr < xPrev )
            {
                xOffset = fluidMotionStep;
                yOffset = 0;
            }
            else
                ;

            int xPos = xCurr * Game.TILE_SIZE + xOffset;
            int yPos = yCurr * Game.TILE_SIZE + yOffset;

            graphics2D.setColor( Options.snakeColor );
            graphics2D.fillOval( xPos,
                                 yPos,
                                 Game.TILE_SIZE,
                                 Game.TILE_SIZE );

            xPrev = xCurr;
            yPrev = yCurr;

            xCurr = xNext;
            yCurr = yNext;

            currTile = getTile( xNext, yNext );
            xNext = currTile.x;
            yNext = currTile.y;
        }
    }

    @Override
    public void dispose()
    {

    }
}