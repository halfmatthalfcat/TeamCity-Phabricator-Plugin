package com.couchmate.teamcity.phabricator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.couchmate.teamcity.phabricator.PhabLogger;

public final class CommandBuilder {

    private String command = null;
    private String action = null;
    private String workingDir = null;
    private List<String> args = new ArrayList<String>();
    private PhabLogger logger = null;

    private static Boolean isNullOrEmpty(Object o){
        if(o == null) return true;
        else if(String.valueOf(o).equals("")) return true;
        else return false;
    }

    private static String formatFlag(String flag){
        Pattern withFlag = Pattern.compile("^\\-\\-[a-zA-Z0-9-]+$");
        Pattern singleWord = Pattern.compile("^\\w$");
        Matcher m = withFlag.matcher(flag.trim());
        Matcher m1 = singleWord.matcher(flag.trim());
        if(m.matches()) return flag.trim();
        else if(m1.matches()) return String.format("--%s", flag);
        else throw new IllegalArgumentException(String.format("%s is not a valid flag", flag));
    }

    public CommandBuilder setWorkingDir(String workingDir){
        if(isNullOrEmpty(workingDir)) throw new IllegalArgumentException("Need to provide valid working directory");
        else this.workingDir = workingDir;
        return this;
    }

    public CommandBuilder setCommand(String cmd){
        if(isNullOrEmpty(cmd)) throw new IllegalArgumentException("Need to provide a valid command");
        else this.command = cmd;
        return this;
    }

    public CommandBuilder setAction(String action){
        if(isNullOrEmpty(action)) throw new IllegalArgumentException("Need to provide a valid action");
        else this.action = action;
        return this;
    }

    public CommandBuilder setArg(String arg){
        if(isNullOrEmpty(arg)) throw new IllegalArgumentException("Need to provide a valid argument");
        else this.args.add(arg);
        return this;
    }

    public CommandBuilder setArg(int pos, String arg){
        if(isNullOrEmpty(arg)) throw new IllegalArgumentException("Need to provide a valid argument");
        else this.args.add(pos, arg);
        return this;
    }

    public CommandBuilder setFlag(String flag){
        if(isNullOrEmpty(flag)) throw new IllegalArgumentException("Must provide a valid flag");
        else this.args.add(formatFlag(flag));
        return this;
    }

    public CommandBuilder setFlag(int pos, String flag){
        if(isNullOrEmpty(flag)) throw new IllegalArgumentException("Must provide a valid flag");
        else this.args.add(pos, formatFlag(flag));
        return this;
    }

    public CommandBuilder setArgWithValue(StringKeyValue keyValue){
        this.args.add(String.format("%s %s", keyValue.getKey(), keyValue.getValue()));
        return this;
    }

    public CommandBuilder setArgWithValue(int pos, StringKeyValue keyValue){
        this.args.add(pos, String.format("%s %s", keyValue.getKey(), keyValue.getValue()));
        return this;
    }

    public CommandBuilder setFlagWithValue(StringKeyValue keyValue){
        this.args.add(String.format("%s %s", formatFlag(keyValue.getKey()), keyValue.getValue()));
        return this;
    }

    public CommandBuilder setFlagWithValue(int pos, StringKeyValue keyValue){
        this.args.add(pos, String.format("%s %s", formatFlag(keyValue.getKey()), keyValue.getValue()));
        return this;
    }

    public CommandBuilder setArgWithValueEquals(StringKeyValue keyValue){
        this.args.add(String.format("%s=%s", keyValue.getKey(), keyValue.getValue()));
        return this;
    }

    public CommandBuilder setArgWithValueEquals(int pos, StringKeyValue keyValue){
        this.args.add(pos, String.format("%s=%s", keyValue.getKey(), keyValue.getValue()));
        return this;
    }

    public CommandBuilder setFlagWithValueEquals(int pos, StringKeyValue keyValue){
        this.args.add(pos, String.format("%s=%s", formatFlag(keyValue.getKey()), keyValue.getValue()));
        return this;
    }

    public CommandBuilder setFlagWithValueEquals(StringKeyValue keyValue){
        this.args.add(String.format("%s=%s", formatFlag(keyValue.getKey()), keyValue.getValue()));
        return this;
    }

    public Command build(){
        if(isNullOrEmpty(this.command)) throw new IllegalArgumentException("Must provide a command");
        else this.args.add(0, this.command);
        if(!isNullOrEmpty(this.action)) this.args.add(1, this.action);

        return new Command(
                args.toArray(new String[args.size()]),
                this.workingDir
        );
    }

    public class Command {

        private final File workingDir;
        private ProcessBuilder processBuilder;
        private Process process;
        private PhabLogger logger;
        private Command(){
            this.workingDir = null;
        }

        public Command(
                final String[] args,
                final String workingDir
        ){
            this.workingDir = isNullOrEmpty(workingDir) ? null : new File(workingDir);
            this.logger = new PhabLogger();
            this.processBuilder = new ProcessBuilder(args);
            this.processBuilder.directory(this.workingDir);
            this.processBuilder.inheritIO();
        }

        public Command exec(){
            try{ process = this.processBuilder.start(); }
            catch (Exception e) { logger.warn(e.getMessage(), e); }
            return this;
        }

        public int join(){
            try{ return this.process.waitFor(); }
            catch (Exception e) { logger.warn(e.getMessage(), e); return 666; }
        }

    }

}
