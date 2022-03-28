package com.example.uss_springboot;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UssController {

    @GetMapping("/test")
    public String test(){
        return "testing";
    }

    @GetMapping("/documents")
    public List<UssDocument> getAll(){
        // TODO fill with the necessary list
        List<UssDocument> ussDocuments = new ArrayList<UssDocument>();
        ussDocuments.add(new UssDocument("test", "test", "test", "test", "test", "test"));
        return ussDocuments;
    }

    @PostMapping("/documents")
    public List<UssDocument> getQuery(@RequestBody UssQuery query){
        // TODO link up the querying portion
        return null;
    }
}

