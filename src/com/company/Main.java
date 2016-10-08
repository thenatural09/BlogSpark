package com.company;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static HashMap<String,User> users = new HashMap<>();

    public static void main(String[] args) {
        Spark.get(
                "/",
                (request,response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = users.get(name);

                    HashMap m = new HashMap<>();
                    if (user != null) {
                        m.put("name",user.name);
                        m.put("messages",user.messages);
                    }
                    return new ModelAndView(m,"home.html");
                },
                new MustacheTemplateEngine()

        );

        Spark.get (
                "/login",
                (request,response) -> new ModelAndView(null,"login.html"),
                new MustacheTemplateEngine()
        );

        Spark.post (
                "/login",
                (request,response) -> {
                    String name = request.queryParams("loginName");
                    String password = request.queryParams("loginPassword");
                    ArrayList<Message> messages = new ArrayList<>();
                    User user = users.get(name);
                    if (user == null) {
                        user = new User(name,password,messages);
                        users.put(name,user);
                    }
                    else if(!password.equals(user.password)) {
                        response.redirect("/");
                        return null;
                    }
                    Session session = request.session();
                    session.attribute("loginName",name);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post (
                "/logout",
                (request,response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post (
                "/createMessage",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = users.get(name);
                    if (user == null) {
                        return null;
                    }
                    String line = request.queryParams("messageLine");
                    user.messages.add(new Message(line));
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post (
                "/deleteMessage",
                (request,response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = users.get(name);
                    if (user == null) {
                        return null;
                    }
                    String linNum = request.queryParams("deleteNumLine");
                    int i = Integer.valueOf(linNum) - 1;
                    if (i > user.messages.size()) {
                        response.redirect("/");
                        return null;
                    }
                    user.messages.remove(i);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post (
                "/editMessage",
                (request,response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = users.get(name);
                    if (user == null) {
                        return null;
                    }
                    String linNum = request.queryParams("editNumLine");
                    int i = Integer.valueOf(linNum) - 1;
                    String linEdit = request.queryParams("editLine");
                    Message message = new Message(linEdit);
                    if(i > user.messages.size()) {
                        response.redirect("/");
                        return null;
                    }
                    user.messages.set(i,message);
                    response.redirect("/");
                    return null;
                }
        );
    }
}
