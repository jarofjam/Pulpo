
function findAttrById(list, id) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].id === id) {
            return list[i];
        }
    }
    return null;
}

function findValueIndexByAttributeId(list, id) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].attr_id === id) {
            return i;
        }
    }
    return -1;
}

function rearrangeText(input) {
    var output = new Object();
    var attributes = [];
    var text = '';

    var skip = false;
    var inside_brackets = false;
    var inside_value = false;

    var attribute = new Object();
    var name = '';
    var value = '';

    for (var i = 0; i < input.length; i++) {
        var c = input[i];

        if (c === '<') {
            inside_brackets = true;
            skip = true;
            continue;
        }
        if (c === '>') {
            inside_brackets = false;
            if (attribute.name != null) {
                text += '$1$';
                attributes.push(attribute);
            }
            attribute = new Object;
            continue;
        }

        if (inside_brackets) {
            if (c === ' ') {
                skip = false;
                continue;
            }
            if (skip) {
                continue;
            }

            if (c === '=') {
                continue;
            }
            if (c === '\'') {
                if (inside_value) {
                    inside_value = false;
                    attribute[name] = value;
                    name = '';
                    value = '';
                    continue;
                } else {
                    inside_value = true;
                    continue;
                }
            }
            if (inside_value) {
                value += c;
            } else {
                name += c;
            }
        } else {
            text += c;
        }
    }

    output['attributes'] = attributes;
    output['text'] = text;

    return output;
}

var templateApi = Vue.resource('/api/moderator/template/{id}?department={department}');
var departmentApi = Vue.resource('/api/department');

Vue.component('option-department', {
    props: ['department'],
    template:
        '<option :value=department.name>{{ department.name }}</option>'
});

Vue.component('text-part', {
    props: ['text', 'attributes', 'add_value'],
    data: function() {
        return {
            attr: null
        }
    },
    template:
        '<span>' +
        '<template v-if="this.attr">' +
            '<input :placeholder=this.attr.placeholder />' +
        '</template>' +
        '<template v-else>' +
            '{{ text }}' +
        '</template>' +
        '</span>',
    created: function() {
        this.attr = findAttrById(this.attributes, this.text);
    }
});

Vue.component('template-row', {
    props: ['template', 'templates'],
    data: function() {
        return {
            values: [],
            text_parts: []
        }
    },
    template:
        '<tr>' +
            '<td>{{ this.template.department }}</td>' +
            '<td>{{ this.template.topic }}</td>' +
            '<td>' +
                '<text-part ' +
                    'v-for="part in text_parts" :key="part" ' +
                    ':text="part" :attributes="template.attributes" ' +
                '/>' +
            '</td>' +
            '<td>{{ this.template.author }}</td>' +
            '<td>{{ this.template.created }}</td>' +
            '<td>{{ this.template.removed }}</td>' +
            '<td>' +
                '<input v-if="!this.template.removed" type="button" class="danger_zone" v-on:click="cancel" value="Cancel" style="width: 70px; margin-bottom: 5px" />' +
                '<br/>' +
                '<input type="button" class="danger_zone" v-on:click="remove" value="Delete" style="width: 70px;" />' +
            '</td>' +
        '</tr>',
    methods: {
        cancel: function() {
            this.template['remove'] = true;

            templateApi.update({id: this.template.id}, this.template);

            var date = new Date();
            this.template.removed = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
        },
        remove: function() {
            templateApi.remove({id: this.template.id}, this.template);
            this.templates.splice(this.templates.indexOf(this.template), 1);
        }
    },
    created: function() {
        this.text_parts = this.template.text.split('$');
        for (var i = 0, j = 0; i < this.text_parts.length; i++) {
            if (i % 2 !== 0) {
                this.text_parts[i] = this.template.attributes[j].id;
                j++;
            }
        };

        this.values = [];
        for (var i = 0; i < this.template.attributes.length; i++) {
            this.values.push({attr_id: this.template.attributes[i].id, value: ''});
        }
    },
});

var temp = new Vue({
    el: '#template',
    data: function() {
        return {
            templates: [],
            departments: [],

            choose_department: false,
            chosen_department: 'All',

            template_editor_visible: false,
            create_template_department: null,
            create_template_topic: '',
            template_input: '',
            database_input: ''
        }
    },
    template:
        '<div>' +
            '<div v-if="!template_editor_visible">' +
                '<input type="button" class="notChosen" value="Create new template" v-on:click="show_template_editor"/>' +
            '</div>' +
            '<div v-else>' +
                '<input type="button" class="chosen" value="Create new template"/>' +
                '<div style=" border: 2px solid black; width: 70%; height: 70%; position: fixed; left: 15%; top: 15%; background-color: white">' +
                    '<input type="button" class="danger_zone" value="X" v-on:click="hide_template_editor" style="float: right;"/>' +
                    '<p style="text-align: center">Template editor<input type="button" class="notChosen" value="Save" v-on:click="save"/></p>' +
                    '<hr/>' +

                    '<p style="margin-left: 10px;">' +
                        'Choose department: ' +
                        '<select v-model="create_template_department">' +
                            '<option-department ' +
                                'v-for="department in departments" :key="department.id" ' +
                                ':department="department" ' +
                            '/>' +
                        '</select>' +
                    '</p>' +
                    '<p style="margin-left: 10px;">Enter topic: <input type="text" v-model="create_template_topic" /></p>' +
                    '<textarea cols="53" rows="16" style="float:left; margin: 0 10px" v-model="template_input" placeholder="Enter template html">{{ template_input }}</textarea>' +
                    '<div style="width: 49%; height: 245px; border: 1px solid gray; margin: 0 10px; position: absolute; right: 0;" v-html="template_input"></div>' +
                '</div>' +
            '</div>' +
            '<table>' +
                '<caption>Templates</caption>' +
                '<tr>' +
                    '<th v-if="this.choose_department" v-on:change="done_choose_department" >' +
                        '<select v-model="chosen_department">' +
                        '<option value="All">All</option>' +
                        '<option-department ' +
                            'v-for="department in departments" :key="department.id" ' +
                            ':department="department" ' +
                        '/>' +
                    '</select>' +
                    '</th>' +
                        '<th v-else v-on:click="do_choose_department" class="clickable" title="Filer by department">Department: {{ this.chosen_department }}</th>' +
                    '<th>Topic</th>' +
                    '<th>Text</th>' +
                    '<th>Author</th>' +
                    '<th>Created</th>' +
                    '<th>Canceled</th>' +
                    '<th>Actions</th>' +
                '</tr>' +
                '<template v-if="templates.length === 0">' +
                    'No templates found' +
                '</template>' +
                '<template v-else>' +
                    '<template-row ' +
                        'v-for="template in templates" :key="template.id" ' +
                        ':template="template" :templates="templates"' +
                    '/>' +
                '</template>' +
            '</table>' +
        '</div>',
    methods:{
        save: function() {
            if (
                this.create_template_department != null &&
                this.create_template_topic != '' &&
                this.template_input != ''
            ) {
                this.database_input = rearrangeText(this.template_input);
            }
            var template = {
                topic: this.create_template_topic,
                department: this.create_template_department,
                text: this.database_input.text,
                attributes: this.database_input.attributes
            };

            templateApi.save({}, template);
            this.hide_template_editor();

            location.reload();
        },
        do_choose_department: function() {
            this.choose_department = true;
        },
        done_choose_department: function() {
            this.refresh_templates();
            this.choose_department = false;
        },
        show_template_editor: function() {
            this.template_editor_visible = true;
        },
        hide_template_editor: function() {
            this.database_input = null;
            this.template_input = '';
            this.create_template_department = '';
            this.create_template_topic = '';
            this.template_editor_visible = false;
        },
        refresh_templates: function() {
            this.templates = [];

            templateApi.get({department: this.chosen_department}).then(
                result => result.json().then(
                    data => data.forEach(template => this.templates.push(template))
                )
            )
        }
    },
    created: function() {
        this.refresh_templates();

        departmentApi.get().then(
            result => result.json().then(
                data => data.forEach(department => this.departments.push(department))
            )
        );
    }
});