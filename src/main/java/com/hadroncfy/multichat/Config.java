package com.hadroncfy.multichat;

import java.util.HashMap;
import java.util.Map;

import net.kyori.text.Component;

public class Config {
    public Map<String, Component> serverAlias = new HashMap<>();
    public Formats formats = new Formats();
    public boolean sendHelloMessage = false;
}