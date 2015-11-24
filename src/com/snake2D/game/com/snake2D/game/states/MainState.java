package com.snake2D.game.com.snake2D.game.states;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.TileObserver;
import java.util.Random;

import com.snake2D.game.basic.Game;
import com.snake2D.game.basic.Options;

/**
 * Created by philipp on 12.11.15.
 */
public class MainState extends GameState
{
    private Integer[] elements = new Integer[ Game.TILES_X * Game.TILES_Y ];

    private static final int NO_ELEMENT = -1;

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

    private int headX;
    private int headY;

    private int foodX;
    private int foodY;

    private int bodyLength = 1;

    private boolean gameOver = false;

    private Random random;

    public MainState( GameStateManager gameStateManager )
    {
        super( gameStateManager );
    }

    @Override
    public void init()
    {
        Game.setTitle( Game.TITLE + "   Score: " + bodyLength );

        moveStepTime = Options.getStepTime();

        getFluidMotionStepTime = moveStepTime / Game.TILE_SIZE;

        for( int i = 0; i < Game.TILES_X * Game.TILES_Y; i++ )
        {
            elements[ i ] = NO_ELEMENT;
        }

        random = new Random();

        headX = random.nextInt( Game.TILES_X );
        headY = random.nextInt( Game.TILES_Y );

        setNextElement( headX, headY, headX, headY - 1 );

        generateFood();
    }

    private int getElement( int elementX, int elementY )
    {
        return elementY * Game.TILES_X + elementX;
    }

    private int getElementX( int element )
    {
        return element % Game.TILES_X;
    }

    private int getElementY( int element )
    {
        return element / Game.TILES_X;
    }

    private void setNextElement( int thisElement, int nextElement )
    {
        elements[ thisElement ] = nextElement;
    }

    private void setNextElement( int thisElementX, int thisElementY, int nextElementX, int nextElementY )
    {
        elements[ thisElementY * Game.TILES_X + thisElementX ] = nextElementY * Game.TILES_X + nextElementX;
    }

    private int getNextElement( int thisElement )
    {
        return elements[ thisElement ];
    }

    private int getNextElement( int thisElementX, int thisElementY )
    {
        return elements[ thisElementY * Game.TILES_X + thisElementX ];
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
        if( Game.keyboardInputHandler.isKeyDown( KeyEvent.VK_UP ) && moveDirection != DOWN )
            inputDirection = UP;
        else if( Game.keyboardInputHandler.isKeyDown( KeyEvent.VK_DOWN ) && moveDirection != UP )
            inputDirection = DOWN;
        else if( Game.keyboardInputHandler.isKeyDown( KeyEvent.VK_LEFT ) && moveDirection != RIGHT )
            inputDirection = LEFT;
        else if( Game.keyboardInputHandler.isKeyDown( KeyEvent.VK_RIGHT ) && moveDirection != LEFT )
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
        // head is start element
        int thisElement = getElement( headX, headY );

        // repeat until we get the last element which hasn't a next element
        while( getNextElement( thisElement ) != NO_ELEMENT )
        {
            // check the value of the next element:
            // is it 'NO_ELEMENT'? -> then 'thisElement' is the last element of the snake
            int nextElement = getNextElement( thisElement );
            if( getNextElement( nextElement ) == NO_ELEMENT )
            {
                // remove the last tile from the snake
                setNextElement( thisElement, NO_ELEMENT );
            }

            thisElement = nextElement;
        }
    }

    private void updateHead()
    {
        // coordinates for next head position
        int newHeadX = headX;
        int newHeadY = headY;

        // evaluate move direction and change position accordingly
        if( moveDirection == UP )
            newHeadY--;
        else if( moveDirection == DOWN )
            newHeadY++;
        else if( moveDirection == LEFT )
            newHeadX--;
        else if( moveDirection == RIGHT )
            newHeadX++;
        else
            ;

        // update move direction
        moveDirection = inputDirection;

        // check out of bounds x
        if( newHeadX >= Game.TILES_X )
            newHeadX = 0;
        else if( newHeadX < 0 )
            newHeadX = Game.TILES_X - 1;
        else
            ;

        // check out of bounds y
        if( newHeadY >= Game.TILES_Y )
            newHeadY = 0;
        else if( newHeadY < 0 )
            newHeadY = Game.TILES_Y - 1;
        else
            ;

        // check collision with existing tile
        int elementInFront = getNextElement( newHeadX, newHeadY );
        if( elementInFront != NO_ELEMENT )
        {
            gameOver = true;
            return;
        }

        // move head
        setNextElement( newHeadX, newHeadY, headX, headY );

        // update stored head coordinates
        headX = newHeadX;
        headY = newHeadY;

        // if we collected food ...
        if( headX == foodX && headY == foodY )
        {
            // generate new food
            generateFood();

            // increase body length
            bodyLength++;

            // update score
            Game.setTitle( Game.TITLE + "   Score: " + bodyLength );

            // update the head one more time
            updateHead();
        }
    }

    private void generateFood()
    {
        // get all free elements by subtracting the body length from all available elements
        int noElementsCount = Game.TILES_X * Game.TILES_Y - bodyLength;

        // array which stores indices of free elements
        Integer[] noElements = new Integer[ noElementsCount ];

        // index of noElements
        int i = 0;
        // get all free indices
        for( int element = 0; element < Game.TILES_X * Game.TILES_Y; element++ )
        {
            if( getNextElement( element ) == NO_ELEMENT )
            {
                noElements[ i ] = element;
                i++;
            }
        }

        // get random index
        int randomNoElement = random.nextInt( noElementsCount );

        // update food tile
        foodX = noElements[ randomNoElement ] % Game.TILES_X;
        foodY = noElements[ randomNoElement ] / Game.TILES_X;
    }

    @Override
    public void render( Graphics2D graphics2D )
    {
        // render food
        graphics2D.setColor( Options.foodColor );
        graphics2D.fillRect( foodX * Game.TILE_SIZE, foodY * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE );

        int prevX = headX;
        int prevY = headY;

        if( moveDirection == UP )
        {
            if( headY == 0 )
                prevY = Game.TILES_Y - 1;
            else
                prevY--;
        }
        else if( moveDirection == DOWN )
        {
            if( headY == Game.TILES_Y - 1 )
                prevY = 0;
            else
                prevY++;
        }
        else if( moveDirection == LEFT )
        {
            if( headX == 0 )
                prevX = Game.TILES_X - 1;
            else
                prevX--;
        }
        else if( moveDirection == RIGHT )
        {
            if( headX == Game.TILES_X - 1 )
                prevX = 0;
            else
                prevX++;
        }

        int thisX;
        int thisY;

        int thisElement = getElement( headX, headY );
        int nextElement = getNextElement( thisElement );

        int xOffset = 0;
        int yOffset = 0;

        int xPos;
        int yPos;

        int teleport;

        while( nextElement != NO_ELEMENT )
        {
            teleport = -1;

            thisX = getElementX( thisElement );
            thisY = getElementY( thisElement );

            xOffset = 0;
            yOffset = 0;

            // move up
            if( thisY > prevY )
            {
                if( thisY == Game.TILES_Y - 1 && prevY == 0 )
                {
                    xOffset = 0;
                    yOffset = fluidMotionStep;

                    teleport = UP;
                }
                else
                {
                    xOffset = 0;
                    yOffset = -fluidMotionStep;
                }
            }
            // move down
            else if( thisY < prevY )
            {
                if( thisY == 0 && prevY == Game.TILES_Y - 1 )
                {
                    xOffset = 0;
                    yOffset = -fluidMotionStep;

                    teleport = DOWN;
                }
                else
                {
                    xOffset = 0;
                    yOffset = fluidMotionStep;
                }
            }
            // move left
            else if( thisX > prevX )
            {
                if( thisX == Game.TILES_X - 1 && prevX == 0 )
                {
                    xOffset = fluidMotionStep;
                    yOffset = 0;

                    teleport = LEFT;
                }
                else
                {
                    xOffset = -fluidMotionStep;
                    yOffset = 0;
                }
            }
            // move right
            else if( thisX < prevX )
            {
                if( thisX == 0 && prevX == Game.TILES_X - 1 )
                {
                    xOffset = -fluidMotionStep;
                    yOffset = 0;

                    teleport = RIGHT;
                }
                else
                {
                    xOffset = fluidMotionStep;
                    yOffset = 0;
                }
            }
            else;

            xPos = thisX * Game.TILE_SIZE + xOffset;
            yPos = thisY * Game.TILE_SIZE + yOffset;

            graphics2D.setColor( Options.snakeColor );
            graphics2D.fillOval( xPos, yPos, Game.TILE_SIZE, Game.TILE_SIZE );

            if( teleport != -1 )
            {
                if( teleport == UP )
                    yPos = yOffset - Game.TILE_SIZE;
                else if( teleport == DOWN )
                    yPos = ( Game.TILES_Y ) * Game.TILE_SIZE - fluidMotionStep ;
                else if( teleport == LEFT )
                    xPos = xOffset - Game.TILE_SIZE;
                else if( teleport == RIGHT )
                    xPos = ( Game.TILES_X ) * Game.TILE_SIZE - fluidMotionStep;
                else;

                graphics2D.fillOval( xPos, yPos, Game.TILE_SIZE, Game.TILE_SIZE );
            }

            prevX = getElementX( thisElement );
            prevY = getElementY( thisElement );

            thisElement = getNextElement( thisElement );
            nextElement = getNextElement( thisElement );
        }
    }

    @Override
    public void dispose()
    {

    }
}