package dataObjects;

import services.Service;

import java.lang.reflect.Method;


public class RegisterEntry {

    private Service service;
    private Method method;
    String command;

    public RegisterEntry(Service service, Method method) {
        this.service = service;
        this.method = method;
        command = method.getName();
    }

    public Service getService() {
        return service;
    }

    public Method getMethod() {
        return method;
    }

    public String getCommand() {
        return command;
    }
}
