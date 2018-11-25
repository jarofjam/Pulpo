package com.example.demo.service;

import com.example.demo.domain.Meets;
import com.example.demo.domain.Tag;
import com.example.demo.dto.TagDto;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.TagRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TagService {
    private TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

//CRUD
    public TagDto createTag(String tag) {
        return tagToTagDto(tagRepository.createTag(tag));
    }

    public void createRibs(List<String> tags) {
        Collections.sort(tags);
        int n = tags.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                String a = tags.get(i);
                String b = tags.get(j);
                tagRepository.createRib(a, b);
            }
        }
    }

    public TagDto updateTagName(String oldName, String newName) {
        return tagToTagDto(tagRepository.updateTagName(oldName, newName));
    }

    public List<TagDto> findAll() {
        List<Tag> tags = new ArrayList<>();

        tagRepository.findAll().forEach(tags::add);

        return tagListToTagDtoList(tags);
    }

    public void deleteTag(String name) {
        tagRepository.deleteRibsByTag(name);
        tagRepository.deleteTagByName(name);
    }

    public void deleteRibsBetweenTags(String name1, String name2) {
        tagRepository.deleteRibsBetweenTags(name1, name2);
    }

//Logic
    public List<String> findFriends(String name) {
        return tagRepository.findRibsByDistanceFromTag(name, ":MEETS*..4");
//        return tagRepository.findCloseFriends(name);
    }

//Additional
    private Tag findTagByName(String name) {
        List<Tag> tags = tagRepository.findAllByName(name);

        if (tags.size() == 0) {
            throw new BadRequestException();
        }

        return tags.get(0);
    }

    private TagDto tagToTagDto(Tag tag) {
        TagDto tagDto = new TagDto();

        BeanUtils.copyProperties(tag, tagDto);

        return tagDto;
    }

    private Tag tagDtoToTag(TagDto tagDto) {
        Tag tag = new Tag();

        BeanUtils.copyProperties(tagDto, tag);

        return tag;
    }

    private List<TagDto> tagListToTagDtoList(List<Tag> tags) {
        List<TagDto> tagDtos = new ArrayList<>();

        if (tags == null) {
            return tagDtos;
        }

        for (Tag tag :tags) {
            tagDtos.add(tagToTagDto(tag));
        }

        return tagDtos;
    }
}
