package com.snake2D.game.basic;

import com.snake2D.game.com.snake2D.game.states.GameStateManager;
import com.snake2D.game.com.snake2D.game.states.MainState;
import com.snake2D.game.input.KeyboardInputHandler;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferStrategy;


public class Game extends Canvas implements Runnable
{
    public static final String TITLE = "Snake";

    public static final int TILE_SIZE = 16;
    public static final int TILES_X = 30;
    public static final int TILES_Y = 30;

    public static final int WIDTH = TILE_SIZE * TILES_X;
    public static final int HEIGHT = TILE_SIZE * TILES_Y;

    private boolean running = false;

    private JFrame frame;

    private static final long FPS = 60L;
    private static final long FRAME_TIME_NANO = 1000000000L / FPS;

    private GameStateManager gameStateManager;
    public static KeyboardInputHandler keyboardInputHandler;
    
    public Game()
    {
        setMinimumSize( new Dimension( WIDTH, HEIGHT ) );
        setMaximumSize( new Dimension( WIDTH, HEIGHT ) );
        setPreferredSize( new Dimension( WIDTH, HEIGHT ) );

        frame = new JFrame( TITLE );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLayout( new BorderLayout() );

        frame.add( this, BorderLayout.CENTER );
        frame.pack();

        frame.setResizable( false );
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
        
        keyboardInputHandler = new KeyboardInputHandler();
        frame.addKeyListener( keyboardInputHandler );
    }
    
    public synchronized void start()
    {
        running = true;
        new Thread( this ).start();
    }

    @Override
    public void run()
    {
        gameStateManager = new GameStateManager();
        gameStateManager.setGameState( new MainState( gameStateManager ) );

        long currTime = System.nanoTime();
        long lastTime;
        double updateRatio = 0;
        long milliSecCounter = System.currentTimeMillis();
        int fps = 0;

        while( running )
        {
            lastTime = currTime;
            currTime = System.nanoTime();

            updateRatio += ( ( double ) currTime - ( double ) lastTime ) / FRAME_TIME_NANO;

            while( updateRatio >= 1 )
            {
                update( ( float ) updateRatio );

                updateRatio -= 1;
            }

            render();

            fps++;

            if( System.currentTimeMillis() - milliSecCounter >= 1000 )
            {
                milliSecCounter += 1000;
                System.out.println( "FPS: " + fps );
                fps = 0;
            }
        }
    }

    public void update( float updateRatio )
    {
        gameStateManager.update( updateRatio );
        keyboardInputHandler.update();
    }

    public void render()
    {
        BufferStrategy bufferStrategy = getBufferStrategy();

        if( bufferStrategy == null )
        {
            createBufferStrategy( 3 );
            return;
        }

        Graphics graphics = bufferStrategy.getDrawGraphics();

        graphics.setColor( Options.backgroundColor );
        graphics.fillRect( 0, 0, getWidth(), getHeight() );

        gameStateManager.render( ( Graphics2D ) graphics );

        graphics.dispose();
        bufferStrategy.show();
    }

    public synchronized void dispose()
    {
        gameStateManager.dispose();

        running = false;

        frame.dispose();
    }

    public static void main( String[] args )
    {
        new Game().start();
    }
}