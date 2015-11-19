package com.snake2D.game.com.snake2D.game.states;

import java.awt.*;

/**
 * Created by philipp on 12.11.15.
 */
public abstract class GameState
{
    GameStateManager gameStateManager;

    public GameState( GameStateManager gameStateManager )
    {
        this.gameStateManager = gameStateManager;
    }

    public abstract void init();
    public abstract void update( float delta );
    public abstract void render( Graphics2D graphics2D );
    public abstract void dispose();
}

