package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;


import java.io.File;
import java.io.IOException;



public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/json");
        //There is no need for an adventure class, the example below is just for convenience
        //if you're using jackson. It's also a preview to your next assignment :)
        //Adventure adventure = new ObjectMapper().readValue(file, Adventure.class);
        //Gson gson = new Gson();
    }
}