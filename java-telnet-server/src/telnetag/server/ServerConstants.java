package telnetag.server;

public class ServerConstants 
{
	/*
	 * client exit code
	 */
	public static final int EXIT_CODE = 1;
	/*
	 * command for exit
	 */
	public static final String EXIT_CMD = "exit";
	/*
	 * information sends by help command
	 */
	public static final String HELP_MSG = 
			"Available commands are:\r\n" +
			"help                               - print help information\r\n" +
			"cd [dir]                           - change current directory\r\n" +
			"dir                                - view directory content\r\n" +
			"rename [file|dir] /t [to_file|dir] - change current directory\r\n" +
			"mkdir [dir_name]                   - create new directory\r\n" +
			"rmdir [dir_name]                   - remove directory\r\n" +
			"erase [file_name]                  - delete file\r\n" +
			"type  [file_name]                  - print file content\r\n" +
			"copy  [file|dir] /t [to_file|dir]  - print file content\r\n" +
			"exit                               - close session\r\n";
}
