package com.edusoho.kuozhi.imserver.command;

import com.edusoho.kuozhi.imserver.ImServer;
import java.lang.reflect.Constructor;
import java.util.Hashtable;

/**
 * Created by su on 2016/3/18.
 */
public class CommandFactory {

    private static CommandFactory instance;

    private Hashtable<String, Class<? extends ICommand>> mCommandMap;

    public CommandFactory()
    {
        mCommandMap = new Hashtable<>();
        initCommandList();
    }

    protected void initCommandList() {
        mCommandMap.put("pong", PongCommand.class);
        mCommandMap.put("add", AddCommand.class);
        mCommandMap.put("success", SuccessCommand.class);
        mCommandMap.put("message", MessageCommand.class);
        mCommandMap.put("offlineMsg", OfflineMsgCommand.class);
    }

    public static synchronized CommandFactory getInstance() {
        if (instance == null) {
            instance = new CommandFactory();
        }
        return instance;
    }

    public ICommand create(ImServer server, String cmd) {
        Class commandClass = mCommandMap.get(cmd);
        if (commandClass == null) {
            return new EmptyCommand();
        }

        try {
            Constructor constructor = commandClass.getConstructor(ImServer.class);
            return (ICommand) constructor.newInstance(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new EmptyCommand();
    }
}
