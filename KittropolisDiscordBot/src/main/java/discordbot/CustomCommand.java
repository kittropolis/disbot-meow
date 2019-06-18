package discordbot;

import java.util.regex.Pattern;

public class CustomCommand {
	private String command, helpString, name;
	private Pattern parameters;
	private CustomCommandHandler handler;
	public CustomCommand(String n,String c, Pattern p, CustomCommandHandler h, String s) {
		setName(n);
		setCommand(c);
		setParameters(p);
		setHandler(h);
		setHelpString(s);
	}
	public CustomCommand(String n, String c, String p, int f, CustomCommandHandler h, String s) {
		this(n,c,Pattern.compile(p,f),h,s);
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public Pattern getParameters() {
		return parameters;
	}
	public void setParameters(Pattern parameters) {
		this.parameters = parameters;
	}
	public CustomCommandHandler getHandler() {
		return handler;
	}
	public void setHandler(CustomCommandHandler handler) {
		this.handler = handler;
	}
	public String getHelpString() {
		return helpString;
	}
	public void setHelpString(String helpString) {
		this.helpString = helpString;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
