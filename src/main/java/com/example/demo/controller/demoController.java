package com.example.demo.controller;

import com.example.demo.exception.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class demoController {

    private Map<String, Map<String, String>> list = new HashMap<String, Map<String, String>>() {{
        put("1", new HashMap<String, String>() {{ put("data", "DATA_1"); }});
        put("2", new HashMap<String, String>() {{ put("data", "DATA_2"); }});
        put("3", new HashMap<String, String>() {{ put("data", "DATA_3"); }});
    }};
    private int counter = list.size() + 1;


    @RequestMapping(value = "/api/demo", method = RequestMethod.GET)
    public Map<String, Map<String, String>> getAll() {
        return list;
    }

    @RequestMapping(value = "api/demo", method = RequestMethod.POST)
    public Map<String, String> create(@RequestBody Map<String, String> data) {
        list.put(String.valueOf(counter++), data);

        return data;
    }

    @RequestMapping(value = "api/demo/{id}", method = RequestMethod.GET)
    public Map<String, String> read(@PathVariable String id) {
        Map<String, String> dataUnit = list.get(id);
        if (dataUnit == null) {
            throw new NotFoundException();
        }

        return dataUnit;
    }

    @RequestMapping(value = "api/demo/{id}", method = RequestMethod.PUT)
    public Map<String, String> update(@PathVariable String id, @RequestBody Map<String, String> data) {
        checkId(id);

        list.put(id, data);

        return data;
    }

    @RequestMapping(value = "api/demo/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String id) {
        checkId(id);

        list.remove(id);
    }

    private void checkId(String id) {
        if (list.get(id) == null) {
            throw new NotFoundException();
        }
    }

}
