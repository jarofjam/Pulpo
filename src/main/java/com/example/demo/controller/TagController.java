package com.example.demo.controller;

import com.example.demo.domain.Tag;
import com.example.demo.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TagController {
    @Autowired
    TagService tagService;

    @RequestMapping(value = "/api/graph/tag", method = RequestMethod.GET)
    public List<Tag> findAll() {
        return tagService.findAll();
    }

    @RequestMapping(value = "/api/graph/tag", method = RequestMethod.POST)
    public Tag create(@RequestBody List<String> tag) {
        return tagService.create(tag);
    }

    @RequestMapping(value = "/api/graph/rib", method = RequestMethod.POST)
    public void addRibs(@RequestBody List<String> tags) {
        tagService.addRibs(tags);
    }

    @RequestMapping(value = "/api/graph/friends/{tag}", method = RequestMethod.GET)
    public List<String> findFriends(@PathVariable String tag) {
        return  tagService.findFriends(tag);
    }
}
