package com.sergivb01.hcf.combatlog;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoggerDeathEvent
		extends Event{
	private static final HandlerList handlers = new HandlerList();
	private final LoggerEntity loggerEntity;

	public LoggerDeathEvent(LoggerEntity loggerEntity){
		this.loggerEntity = loggerEntity;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}

	public LoggerEntity getLoggerEntity(){
		return this.loggerEntity;
	}

	public HandlerList getHandlers(){
		return handlers;
	}
}

