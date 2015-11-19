package com.snake2D.game.com.snake2D.game.states;

import java.awt.*;

/**
 * Created by philipp on 12.11.15.
 */
public class GameStateManager
{
    private GameState currentGameState;

    public GameStateManager()
    {
    }

    public void update( float updateRatio )
    {
        currentGameState.update( updateRatio );
    }

    public void render( Graphics2D graphics2D )
    {
        currentGameState.render( graphics2D );
    }

    public void dispose()
    {
        currentGameState.dispose();
    }

    public void setGameState( GameState gameState )
    {
        if( currentGameState != null )
            currentGameState.dispose();

        currentGameState = gameState;
        gameState.init();
    }
}
