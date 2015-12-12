package com.snake2D.game.basic;

import java.awt.*;

/**
 * Created by philipp on 19.11.15.
 */
public class Options
{
    // Color Options
    public static Color backgroundColor = new Color( 100, 240, 100 );
    public static Color snakeColor = new Color( 30, 30, 30 );
    public static Color foodColor = new Color( 180, 50, 50 );

    // Speed
    public static final int MIN_STEP_TIME = 100;
    public static final int MAX_STEP_TIME = 500;
    public static float speed = 0;

    public static int getStepTime()
    {
        float range = (MAX_STEP_TIME - MIN_STEP_TIME) * speed;
        return MIN_STEP_TIME + (int) range;
    }

    // fluid
    public static boolean fluid = true;
}
