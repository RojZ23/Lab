package com.example.lab;

import com.example.lab.User;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataStore {
    private Map<String, User> users = new HashMap<>();
    private Map<String, List<String>> userAssets = new HashMap<>(); // Simple list of asset strings

    public boolean addUser(User user) {
        if(users.containsKey(user.getUsername())) return false;
        users.put(user.getUsername(), user);
        userAssets.put(user.getUsername(), new ArrayList<>());
        return true;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public boolean validateUser(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }

    public void addAsset(String username, String asset) {
        userAssets.getOrDefault(username, new ArrayList<>()).add(asset);
    }

    public List<String> getAssets(String username) {
        return userAssets.getOrDefault(username, Collections.emptyList());
    }
}
