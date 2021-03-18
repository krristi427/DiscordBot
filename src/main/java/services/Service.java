package services;

import bot.Bot;
import dataObjects.RegisterEntry;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public abstract class Service {

    protected Bot bot = Bot.getInstance();

    protected Service() {

        List<Method> methods = Arrays.asList(this.getClass().getMethods());

        methods.forEach(method -> {

            RegisterEntry entry = new RegisterEntry(this, method);
            bot.register(entry);
        });
    }




}
