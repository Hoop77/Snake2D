package com.snake2D.game.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public final class KeyboardInputHandler implements KeyListener{
	private int[] keys = new int[256];
	
	private boolean[] key_state_up = new boolean[256];
	private boolean[] key_state_down = new boolean[256];
	private boolean[] key_state_prev = new boolean[256];
	
	private boolean keyPressed = false;
	private boolean keyReleased = false;
	
	private String keyCache = "";
	
	public KeyboardInputHandler() {}
	
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() >= 0 && e.getKeyCode() < 256)
		{
			if( !key_state_down[ e.getKeyCode() ] )
			{
				keys[ e.getKeyCode() ] = (int) System.currentTimeMillis();
				key_state_down[ e.getKeyCode() ] = true;
				key_state_up[ e.getKeyCode() ] = false;
				keyPressed = true;
				keyReleased = false;
			}
		}
	}
	
	public void keyReleased(KeyEvent e) 
	{
		if( e.getKeyCode() >= 0 && e.getKeyCode() < 256 ) 
		{
			if( !key_state_up[ e.getKeyCode() ] )
			{
				keys[e.getKeyCode()] = 0;
				key_state_up[e.getKeyCode()] = true;
				key_state_down[e.getKeyCode()] = false;
				keyPressed = false;
				keyReleased = true;
			}
		}
	}
	
	public void keyTyped(KeyEvent e){
		keyCache += e.getKeyChar();
	}
	
	public boolean isKeyDown(int key){
		return key_state_down[key];
	}
	
	public boolean isKeyUp(int key){
		return key_state_up[key];
	}
	
	public boolean isAnyKeyDown(){
		return keyPressed;
	}
	
	public boolean isAnyKeyUp(){
		return keyReleased;
	}
	
	public boolean isKeyClicked( int key )
	{
		return (int) System.currentTimeMillis() - keys[ key ] < 100
				&& key_state_prev[ key ] 
				&& !key_state_down[ key ];
	}
	
	public boolean isKeyPressing( int key )
	{
		return (int) System.currentTimeMillis() - keys[ key ] >= 100; 
	}
	
	public boolean isKeyPressed( int key )
	{
		return key_state_prev[ key ]
				&& !key_state_down[ key ];
	}
	
	public void update()
	{
		key_state_up = new boolean[256];
		keyReleased = false;
		if(keyCache.length() > 1024)
		{
			keyCache = "";
		}
		
		for(int k = 0; k < 256; k++)
		{
			key_state_prev[ k ] = key_state_down[ k ];
		}
	}
}

