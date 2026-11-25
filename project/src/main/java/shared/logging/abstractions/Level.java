package shared.logging.abstractions;

public enum Level {
	DEBUG, INFO, WARNING, FATAL;
	
	@Override
	public String toString() {
	    return name().charAt(0) + name().substring(1).toLowerCase();
	}
}
