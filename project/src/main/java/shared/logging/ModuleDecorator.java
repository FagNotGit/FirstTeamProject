package shared.logging;

import shared.logging.abstractions.Level;
import shared.logging.abstractions.Logger;
import shared.logging.abstractions.LoggerDecorator;

public class ModuleDecorator extends LoggerDecorator {

	public ModuleDecorator(Logger wrapped) { super(wrapped); }

	@Override
	public void log(Level level, String message) {
		this.wrapped.log(level, String.format("[%s] %s", message));
	}

}
