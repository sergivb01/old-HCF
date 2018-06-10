package net.veilmc.hcf.timer.event;

import com.google.common.base.Optional;
import net.veilmc.hcf.timer.PlayerTimer;
import net.veilmc.hcf.timer.Timer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class TimerClearEvent
		extends Event{
	private static final HandlerList handlers = new HandlerList();
	private final Optional<UUID> userUUID;
	private final Timer timer;

	public TimerClearEvent(Timer timer){
		this.userUUID = Optional.absent();
		this.timer = timer;
	}

	public TimerClearEvent(UUID userUUID, PlayerTimer timer){
		this.userUUID = Optional.of(userUUID);
		this.timer = timer;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}

	public Optional<UUID> getUserUUID(){
		return this.userUUID;
	}

	public Timer getTimer(){
		return this.timer;
	}

	public HandlerList getHandlers(){
		return handlers;
	}
}

