package shared.logging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import shared.logging.abstractions.Level;
import shared.logging.abstractions.Logger;

public class FileLogger implements Logger {
	private final Logger logger;
	private Path file;

    public FileLogger(Path file) {
		this.logger = new TimestampDecorator(new ConsoleLogger());
		
        try {
            Path parent = file.getParent();
            this.file = file;
            if (parent != null) { Files.createDirectories(parent); }
            if (!Files.exists(file)) { Files.createFile(file); }
        } catch (IOException e) {
        	this.logger.log(Level.FATAL, String.format("Не удалось инициализировать лог-файл (%s)%n", file));
        } catch (NullPointerException e) {
        	this.logger.log(Level.FATAL, String.format("Не удалось инициализировать лог-файл: Путь до файла не может иметь значение null%n", file));
        }
    }
    
	@Override
	public synchronized void log(Level level, String message) {
		String line = String.format("[%s][%s] %s%n", level.toString(), message);
        try {
        	Files.writeString(file, line,
	            StandardCharsets.UTF_8,
	            StandardOpenOption.CREATE,
	            StandardOpenOption.APPEND);
        } catch (IOException e) {
        	this.logger.log(Level.FATAL, String.format("Не удалось произвести запись в лог-файл (%s)", file));
        }
	}
}
