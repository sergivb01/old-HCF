package net.veilmc.hcf.timer.event;

import net.veilmc.hcf.timer.Timer;
<<<<<<< HEAD
=======
import net.veilmc.hcf.timer.Timer;
>>>>>>> origin/new
import com.google.common.base.Optional;

import java.util.UUID;

<<<<<<< HEAD
=======
import net.veilmc.hcf.timer.Timer;
>>>>>>> origin/new
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerExpireEvent
		extends Event{
	private static final HandlerList handlers = new HandlerList();
	private final Optional<UUID> userUUID;
	private final Timer timer;

	public TimerExpireEvent(Timer timer){
		this.userUUID = Optional.absent();
		this.timer = timer;
	}

	public TimerExpireEvent(UUID userUUID, Timer timer){
		this.userUUID = Optional.fromNullable(userUUID);
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

