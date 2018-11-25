package com.example.demo.controller;

import com.example.demo.domain.Tag;
import com.example.demo.dto.TagDto;
import com.example.demo.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TagController {
    @Autowired
    TagService tagService;

//CRUD
    @RequestMapping(value = "/api/graph/tag", method = RequestMethod.POST)
    public TagDto createTag(@RequestBody String tag) {
        return tagService.createTag(tag);
    }

    @RequestMapping(value = "/api/graph/rib", method = RequestMethod.POST)
    public void createRibs(@RequestBody List<String> tags) {
        tagService.createRibs(tags);
    }

    @RequestMapping(value = "/api/graph/tag", method = RequestMethod.GET)
    public List<TagDto> findAll() {
        return tagService.findAll();
    }

    @RequestMapping(value = "/api/graph/tag/{oldName}/{newName}", method = RequestMethod.PUT)
    public TagDto updateTagName(@PathVariable String oldName, @PathVariable String newName) {
        return tagService.updateTagName(oldName, newName);
    }

    @RequestMapping(value = "/api/graph/tag/{name}", method = RequestMethod.DELETE)
    public void deleteTag(@PathVariable String name) {
        tagService.deleteTag(name);
    }

    @RequestMapping(value = "/api/graph/rib/{name1}/{name2}", method = RequestMethod.DELETE)
    public void deleteRibsBetweenTags(@PathVariable String name1, @PathVariable String name2) {
        tagService.deleteRibsBetweenTags(name1, name2);
    }

//Logic
    @RequestMapping(value = "/api/graph/tag/friends/{name}", method = RequestMethod.GET)
    public List<String> findFriends(@PathVariable String name) {
        return tagService.findFriends(name);
    }

}
