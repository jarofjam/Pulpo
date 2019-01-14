
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

var templateApi = Vue.resource('/api/applicant/template?department={department}');
var departmentApi = Vue.resource('/api/department');
var requestApi = Vue.resource('/api/applicant/request');
var typicalRequestApi = Vue.resource('/api/applicant/typicalrequest');

//Departments
Vue.component('department-button', {
   props:['department', 'choose_department'],
   template:
       '<div :title="department.description" v-on:click="choose" class="department_button">' +
            '{{ department.name }}' +
       '</div>',
    methods: {
       choose: function() {
           this.choose_department(this.department);
       }
    }
});

Vue.component('department-buttons', {
    props: ['departments', 'choose_department'],
    template:
        '<div>' +
            '<p>Choose department</p>' +
            '<hr/>' +
            '<div v-if="departments.length !== 0">' +
                '<department-button ' +
                        'v-for="department in departments" :key="department.id" ' +
                        ':department="department" ' +
                        ':choose_department="choose_department"' +
                '/>' +
            '</div>' +
            '<div v-else>' +
                'No department found' +
            '</div>' +
        '</div>'
});

//Templates
Vue.component('template-button', {
    props: ['template', 'choose_template'],
    template:
        '<div v-on:click="choose" class="template_button">' +
            '{{ template.topic }}' +
            '<template v-if="template.duration">' +
                '<hr/>' +
                'approximate duration: {{template.duration}}' +
            '</template>' +
        '</div>',
    methods: {
        choose: function() {
            this.choose_template(true, this.template);
        }
    }
});

Vue.component('template-buttons', {
    props: ['department', 'choose_template'],
    data: function() {
        return {
            templates: []
        }
    },
    watch: {
        department: function(newState, oldState) {
            this.templates = [];
            templateApi.get({department: newState.name}).then(
                result => result.json().then(
                    data => data.forEach(template => {
                        if (!template.removed) {
                            this.templates.push(template);
                        }
                    })
                )
            );
        }
    },
    template:
        '<div v-if="department">' +
            '<p>{{ department.name }}</p>' +
            '<hr/>' +
            'templates:' +
            '<div v-if="this.templates.length !== 0">' +
                '<template-button ' +
                    'v-for="template in templates" :key="template.id" ' +
                    ':template="template" ' +
                    ':choose_template="choose_template" ' +
                '/>' +
            '</div>' +
            '<div v-else>' +
                'No templates found' +
            '</div>' +
            '<hr/>' +
            '<div class="template_button" v-on:click="choose">Create new request</div>' +
        '</div>',
    methods: {
        choose: function() {
            this.choose_template(false, 42);
        }
    }
});

//Typical Request
Vue.component('text-part', {
    props: ['text', 'attributes', 'add_value'],
    data: function() {
        return {
            attr: null,
            value: ''
        }
    },
    template:
        '<span>' +
            '<template v-if="this.attr">' +
                '<input :placeholder=this.attr.placeholder v-model="value" v-on:change="change"/>' +
            '</template>' +
            '<template v-else>' +
                '{{ text }}' +
            '</template>' +
        '</span>',
    created: function() {
        this.attr = findAttrById(this.attributes, this.text);
    },
    methods: {
        change: function() {
            this.add_value(this.attr.id, this.value)
        }
    }
});

Vue.component('typical-request-form', {
    props: ['template', 'submit_request'],
    data: function() {
        return {
            values: [],
            text_parts: []
        }
    },
    watch: {
        template: function(newState, oldState) {
            this.text_parts = newState.text.split('$');
            for (var i = 0, j = 0; i < this.text_parts.length; i++) {
                if (i % 2 !== 0) {
                    this.text_parts[i] = newState.attributes[j].id;
                    j++;
                }
            };

            this.values = [];
            for (var i = 0; i < newState.attributes.length; i++) {
                this.values.push({attr_id: newState.attributes[i].id, value: ''});
            }
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
    template:
        '<div v-if="template">' +
            '<p style="text-align: center">Request</p>' +
            '<hr/>' +
            '<p><b>Topic: </b>{{ template.topic }}</p>' +
            '<p>Please, fill in the gaps</p>' +
            '<div>' +
                '<text-part ' +
                    'v-for="part in text_parts" :key="part" ' +
                    ':text="part" :attributes="template.attributes" ' +
                    ':add_value="add_value"' +
                '/>' +
            '</div>' +
            '<div v-if="template.duration">' +
                'Approximate duration {{ template.duration }}' +
            '</div>' +
            '<hr/>' +
            '<input type="button" value="Create request" v-on:click="save"/>' +
        '</div>',
    methods: {
        add_value: function(attr_id, value) {
            var index = findValueIndexByAttributeId(this.values, attr_id);
            this.values[index].value = value;
        },
        save: function() {
            var typicalRequest = {
                template: this.template.id,
                values: this.values
            };

            typicalRequestApi.save({}, typicalRequest).then(
                result => {if (result.ok) {this.submit_request()}}
            );
        }
    }
});

//Request
Vue.component('request-form', {
    props: ['department', 'submit_request'],
    data: function() {
        return {
            topic: null,
            description: null
        }
    },
    template:
        '<div>' +
            '<p style="text-align: center">Request</p>' +
            '<hr/>' +
            'Department: {{ department.name }}' +
            '<p>Topic: <input type="text" v-model="topic" /></p>' +
            '<p>Description: </p><textarea v-model="description" cols="40" rows="15"></textarea> '+
            '<hr/>' +
            '<input type="button" value="Create request" v-on:click="save" />' +
        '</div>',
    methods: {
        save: function() {
            var request = {
                department: this.department.name,
                topic: this.topic,
                description: this.description
            };

            if (this.department != null && this.topic != null && this.description != null) {
                requestApi.save({}, request).then(
                    result => {if (result.ok) {this.submit_request()}}
                )
            }
        }
    }
});

var request = new Vue({
    el: '#request',
    template:
        '<div>' +
            '<div v-if="!this.created">' +
                '<div id="department_buttons">' +
                    '<department-buttons ' +
                        ':departments="departments" ' +
                        ':choose_department="choose_department"' +
                    '/>' +
                '</div>' +
                '<div id="template_buttons">' +
                    '<template-buttons ' +
                        ':department="chosen_department" ' +
                        ':choose_template="choose_template"' +
                    '/>' +
                '</div>' +
                '<div id="request_form">' +
                    '<div v-if="this.chosen_template">' +
                        '<div v-if="this.template_based">' +
                            '<typical-request-form ' +
                                ':template="chosen_template" ' +
                                ':submit_request="submit_request"' +
                            '/>' +
                        '</div>' +
                        '<div v-else>' +
                            '<request-form ' +
                                ':department="chosen_department" ' +
                                ':submit_request="submit_request" ' +
                            '/>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
            '</div>' +
            '<div v-else>' +
                '<div>Request was created</div>' +
                '<input type="button" value="Create new request" v-on:click="create_another" />' +
            '</div>' +
        '</div>',
    data: function() {
        return {
            departments: [],
            templates: [],
            chosen_department: null,
            chosen_template: null,
            created: false,
            template_based: null
        }
    },
    methods: {
        choose_department: function(department) {
            this.chosen_department = department;
            this.chosen_template = null;
        },
        choose_template: function (template_based, template) {
            this.template_based = template_based;
            this.chosen_template = template;
        },
        submit_request: function() {
            this.created = true;
        },
        create_another: function() {
            this.created = false;
            this.chosen_template = null;
            this.chosen_department = null;
        }
    },
    created: function() {
        departmentApi.get().then(
            result => result.json().then(
                data => data.forEach(department => this.departments.push(department))
            )
        );
    }
});