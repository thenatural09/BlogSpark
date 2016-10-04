package com.company;

import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static User user;
    public static ArrayList<User> users = new ArrayList<>();

    public static void main(String[] args) {
        Spark.get(
                "/",
                (request,response) -> {
                    HashMap m = new HashMap<>();
                    if (user != null) {
                        m.put("name",user.name);
                        m.put("messages",user.messages);
                    }
                    m.put("users",users);
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
                    user = new User(name,password,messages);
                    users.add(user);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post (
                "/logout",
                (request,response) -> {
                    user = null;
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post (
                "/createMessage",
                (request, response) -> {
                    String line = request.queryParams("messageLine");
                    user.messages.add(new Message(line));
                    response.redirect("/");
                    return null;
                }
        );
    }
}
