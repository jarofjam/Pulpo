
function findIndexById(list, id) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].id === id) {
            return i;
        }
    }
    return -1;
}

function findValueIndexByAttributeId(list, id) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].attr_id === id) {
            return i;
        }
    }
    return -1;
}

var requestApi = Vue.resource('/api/applicant/request/{id}?department={department}&status={status}');
var typicalRequestApi = Vue.resource('/api/applicant/typicalrequest/{id}?department={department}&status={status}');
var departmentApi = Vue.resource('/api/department');
var statusApi = Vue.resource('/api/status');
var templateApi = Vue.resource('/api/applicant/template/{id}');

Vue.component('temp-part', {
    props: ['text', 'attributes', 'values', 'add_value'],
    data: function() {
        return {
            attr: null,
            value: '',
            value_id: ''
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
        var index = findIndexById(this.attributes, this.text);
        if (index != -1) {
            this.attr = this.attributes[index];
            this.value = this.values[index].value;
            this.value_id = this.values[index].id;
        }
    },
    methods: {
        change: function() {
            this.add_value(this.value_id, this.value)
        }
    }
});

Vue.component('request-row', {
    props: ['request'],
    data: function() {
        return{
            text: '',
            status: this.request.status,
            topic: this.request.topic,
            attributes: [],

            edit_topic: false,
            edit_status: false,
            edit_text: false,

            update: false,

            text_parts: [],
            temp_parts: [],
            values: this.request.values
        }
    },
    template:
        '<tr>' +
            '<td>{{ request.department }}</td>' +
            '<td v-if="this.edit_topic && !this.request.typical"><textarea v-on:change="done_edit_topic" v-model="topic"> {{this.topic}} </textarea></td>' +
                '<td v-else v-on:click="do_edit_topic" class="clickable" title="Edit" >{{ this.topic }}</td>' +
            '<td v-if="this.edit_text">' +
                '<template v-if="this.request.typical">' +
                    '<temp-part ' +
                        'v-for="part in temp_parts" :key="part" ' +
                        ':text="part" :attributes="attributes" :values="values" ' +
                        ':add_value="add_value" ' +
                    '/>' +
                '</template>' +
                '<template v-else>' +
                    '<textarea v-model="text" v-on:change="done_edit_text">{{ this.text }}</textarea>' +
                '</template>' +
            '</td>' +
                '<td v-else v-on:click="do_edit_text" class="clickable">{{ this.text }}</td>' +
            '<td>{{ request.comment }}</td>' +
            '<td>{{ this.status }}</td>' +
            '<td>{{ request.author }}</td>' +
            '<td>{{ request.performer }}</td>' +
            '<td>{{ request.moderator }}</td>' +
            '<td>{{ request.created }}</td>' +
            '<td>{{ request.finished }}</td>' +
            '<td>{{ this.removed }}</td>' +
            '<td>' +
                '<input type="button" class="cool_zone" v-if="update" v-on:click="do_update" value="Update" style="width: 70px; margin-bottom: 3px" />' +
                '<input v-if="this.status !== \'Canceled\'" type="button" class="danger_zone" v-on:click="cancel" value="Cancel" style="width: 70px;" />' +
            '</td>' +
        '</tr>',
    methods: {
        add_value: function(value_id, value) {
            var index = findIndexById(this.values, value_id);
            this.values[index].value = value;
            this.update = true;
        },
        do_edit_topic: function() {
            this.edit_topic = this.status !== 'Canceled';
        },
        done_edit_topic: function() {
            this.edit_topic = false;
            this.update = true;
        },
        do_edit_text: function() {
            if (this.status !== 'Canceled') {
                this.edit_text = true;

                if (this.request.typical) {
                    this.temp_parts = this.template.text.split('$');
                    this.attributes = this.template.attributes;
                    for (var i = 0, j = 0; i < this.temp_parts.length; i++) {
                        if (i % 2 !== 0) {
                            this.temp_parts[i] = this.template.attributes[j].id;
                            j++;
                        }
                    };
                }
            }
        },
        done_edit_text: function() {
            this.edit_text = false;
            this.update = true;
            this.text_parts = this.temp_parts;
            this.text = '';
            for (var i = 0, j = 0; i < this.text_parts.length; i++) {
                if (i % 2 === 0) {
                    this.text += this.text_parts[i];
                } else {
                    this.text += this.values[j].value;
                    j++;
                }
            }
        },
        do_update:function() {
            this.done_edit_text();

            this.request.topic = this.topic;
            this.request.status = this.status;
            this.request.values = this.values;

            if (this.request.typical) {
                typicalRequestApi.update({id: this.request.id}, this.request).then(
                    result => result.json().then(
                        data => {
                            this.text = data.description;
                            this.status = data.status;
                            this.topic = data.topic;
                            this.values = data.values;
                        }
                    )
                )
            } else {
                this.request.description = this.text;
                requestApi.update({id: this.request.id}, this.request).then(
                    result => result.json().then(
                        data => {
                            this.removed = data.removed;
                        }
                    )
                );
            }
            this.update = false;
        },
        cancel: function() {
            this.status = "Canceled";
            this.request.remove = true;
            var date = new Date();
            this.removed = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
            if (this.request.typical) {
                typicalRequestApi.update({id: this.request.id}, this.request);
            } else {
                requestApi.update({id: this.request.id}, this.request).then(
                    result => result.json().then(
                        data => {
                            this.removed = data.removed;
                        }
                    )
                );
            }
        }
    },
    created: function(){
        if (this.request.typical) {
            var text_parts = this.request.text.split('$');
            for (var i = 0, j = 0; i < text_parts.length; i++) {
                if (i % 2 === 0) {
                    this.text += text_parts[i];
                } else {
                    this.text += this.request.values[j].value;
                    j++;
                }
            }

            templateApi.get({id: this.request.template}).then(
                result => result.json().then(
                    data => {
                        this.template = data;
                    }
                )
            )

        } else {
            this.text = this.request.description;
        }

    }
});

Vue.component('option-department', {
    props: ['department'],
    template:
        '<option :value=department.name>{{ department.name }}</option>'
});

Vue.component('option-status', {
    props: ['status'],
    template:
        '<option :value=status.name>{{ status.name }}</option>'
});

var applicant = new Vue({
    el: '#applicant',
    data: function() {
        return {
            requests: [],
            departments: [],
            statuses: [],
            templates: [],
            chosen_department: 'All',
            chosen_status: 'All',

            choose_department: false,
            choose_status: false
        }
    },
    template:
        '<div>' +
            '<table>' +
                '<caption> My requests </caption>' +
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
                    '<th>Comment</th>' +
                    '<th v-if="this.choose_status" v-on:change="done_choose_status" >' +
                        '<select v-model="chosen_status">' +
                            '<option value="All">All</option>' +
                            '<option-status ' +
                                'v-for="status in statuses" :key="status.id"' +
                                ':status="status" ' +
                            '/>' +
                        '</select>' +
                    '</th>' +
                        '<th v-else v-on:click="do_choose_status" class="clickable" title="Filer by status">Status: {{ this.chosen_status }}</th>' +
                    '<th>Author</th>' +
                    '<th>Performer</th>' +
                    '<th>Moderator</th>' +
                    '<th>Created</th>' +
                    '<th>Finished</th>' +
                    '<th>Canceled</th>' +
                    '<th>Actions</th>' +
                '</tr>' +
                '<template v-if="requests.length === 0">' +
                    'No requests found' +
                '</template>' +
                '<template v-else>' +
                    '<request-row ' +
                        'v-for="request in requests" :key="request.id" ' +
                        ':request="request" ' +
                    '/>' +
                '</template>' +
            '</table>' +
        '</div>',
    methods: {
        do_choose_department: function() {
            this.choose_department = true;
        },
        done_choose_department: function() {
            this.refresh_requests();
            this.choose_department = false;
        },
        do_choose_status: function() {
            this.choose_status = true;
        },
        done_choose_status: function() {
            this.refresh_requests();
            this.choose_status = false;
        },
        refresh_requests: function() {
            this.requests = [];

            requestApi.get({department: this.chosen_department, status: this.chosen_status}).then(
                result => result.json().then(
                    data => data.forEach(request => {
                        request.typical = false;
                        this.requests.push(request);
                    })
                )
            );

            typicalRequestApi.get({department: this.chosen_department, status: this.chosen_status}).then(
                result => result.json().then(
                    data => data.forEach(typicalRequest => {
                        typicalRequest.typical = true;
                        this.requests.push(typicalRequest);
                    })
                )
            )
        }
    },
    created: function() {
        this.refresh_requests();

        departmentApi.get().then(
            result => result.json().then(
                data => data.forEach(department => this.departments.push(department))
            )
        );

        statusApi.get().then(
            result => result.json().then(
                data => data.forEach(status => this.statuses.push(status))
            )
        );

        templateApi.get().then(
            result => result.json().then(
                data => data.forEach(template => this.templates.push(template))
            )
        );
    }
});