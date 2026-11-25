package shared.logging;

import shared.logging.abstractions.Level;
import shared.logging.abstractions.Logger;
import shared.logging.abstractions.LoggerDecorator;

public class LevelFilterDecorator extends LoggerDecorator {
	private final Level filteredLevel;
	
	public LevelFilterDecorator(Logger wrapped, Level filteredLevel) { 
		super(wrapped); 
		this.filteredLevel = filteredLevel;
	}

	@Override
	public void log(Level level, String message) {
		if (level == filteredLevel) {
			this.wrapped.log(level, message);
		}
	}
	
}
