
function getId(list, name) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].name === name) {
            return list[i].id;
        }
    }

    return -1;
}

var tagApi = Vue.resource('/api/graph/tag');
var ribApi = Vue.resource('/api/graph/rib');

Vue.component('tag-name', {
   props: ['tag'],
   template:
       '<div>' +
            '{{ tag.name }} ({{ tag.parent }})' +
       '</div>'
});

Vue.component('tag-form', {
    props:['tags'],
    data: function() {
        return {
            name: '',
            parent: ''
        }
    },
    template:
        '<div>' +
            '<input type="text" placeholder="Tag name" v-model="name" />' +
            '<input type="text" placeholder="Tag parent" v-model="parent" />' +
            '<input type="button" value="Add" v-on:click="save" />' +
        '</div>',
    methods: {
        save: function () {
            var tag = [this.name, this.parent];

            tagApi.save(tag).then(result =>
                result.json().then(data => {
                    this.tags.push(data);
                    this.name = '';
                    this.parent = '';
                })
            )
        }
    }
});

Vue.component('rib-input', {
    props: ['tags'],
    data: function() {
        return {
            tag_1: '',
            tag_3: '',
            tag_2: ''
        }
    },
    template:
        '<div>' +
            '<input type="text" placeholder="tag_1" v-model="tag_1"/>' +
            '<input type="text" placeholder="tag_2" v-model="tag_2"/>' +
            // '<input type="text" placeholder="tag_3" v-model="tag_3"/>' +
            '<input type="button" value="Add rib" v-on:click="add" /> ' +
        '</div>',
    methods: {
        add: function() {
            var ribs = [this.tag_1, this.tag_2];//, this.tag_3];

            ribApi.save(ribs).then(result =>
                result.json().then(data => {
                    this.tag_1 = '',
                    // this.tag_3 = '',
                    this.tag_2 = '';
                })
            )
        }
    }
});

var tag = new Vue({
    el: '#tag',
    template:
        '<div>' +
            '<tag-form :tags="tags" />' +
            '<rib-input :tags="tags" />' +
            '<tag-name v-for="tag in tags" :tag="tag" :key="tag.id" />' +
        '</div>',
    data: function() {
        return {
            tags: []
        }
    },
    created: function() {
        tagApi.get().then(result =>
            result.json().then(data => {
                data.forEach(tag => this.tags.push(tag))
            })
        );
    }
});