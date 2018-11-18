package com.example.demo.service;

import com.example.demo.domain.Tag;
import com.example.demo.repository.TagRepository;
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

    public List<Tag> findAll() {
        List<Tag> tags = new ArrayList<>();

        tagRepository.findAll().forEach(tags::add);

        return tags;
    }

    public Tag create(List<String> tag) {
        return tagRepository.createTag(tag.get(0), tag.get(1));
    }

    public void addRibs(List<String> tags) {
        Collections.sort(tags);
        int n = tags.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                String a = tags.get(i);
                String b = tags.get(j);
                Integer times = tagRepository.createRib(a, b);
                if (times == 2) {
                    tagRepository.unite(a, b);
                }
            }
        }
    }

    public List<String> findFriends(String tag) {
        return tagRepository.findFriends(tag);
    }
}
