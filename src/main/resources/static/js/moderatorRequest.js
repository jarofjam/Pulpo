
var requestApi = Vue.resource('/api/moderator/request{/id}?department={department}&status={status}');
var typicalRequestApi = Vue.resource('/api/moderator/typicalrequest{/id}?department={department}&status={status}');
var departmentApi = Vue.resource('/api/department');
var statusApi = Vue.resource('/api/status');

Vue.component('request-row', {
    props: ['request', 'requests'],
    data: function() {
        return {
            topic: this.request.topic,
            comment: this.request.comment,
            status: this.request.status,
            moderator: this.request.moderator,
            removed: this.request.removed,
            text: '',

            edit_topic: false,
            edit_comment: false,
            edit_status: false,

            update: false
        }
    },
    template:
        '<tr>' +
            '<td>{{ request.department }}</td>' +
            '<td v-if="this.edit_topic"><textarea cols="30" rows="10" v-on:change="done_edit_topic" v-model="topic"> {{this.topic}} </textarea></td>' +
                '<td v-else v-on:click="do_edit_topic" class="clickable" title="Edit" >{{ this.topic }}</td>' +
            '<td>{{ this.text }}</td>' +
            '<td v-if="this.edit_comment"><textarea cols="30" rows="10" v-on:change="done_edit_comment" v-model="comment"> {{this.comment}} </textarea></td>' +
                '<td v-else v-on:click="do_edit_comment" class="clickable" title="Edit" >{{ this.comment }}</td>' +
            '<td v-if="this.edit_status" v-on:change="done_edit_status">' +
                '<select v-model="status">' +
                    '<option value="New">New</option>' +
                    '<option value="Checked">Checked</option>' +
                    '<option value="Invalid">Invalid</option>' +
                    '<option value="Canceled">Canceled</option>' +
                '</select>'+
            '</td>' +
                '<td v-else v-on:click="do_edit_status" class="clickable" title="Edit">{{ this.status }}</td>' +
            '<td>{{ request.author }}</td>' +
            '<td>{{ request.performer }}</td>' +
            '<td>{{ this.moderator }}</td>' +
            '<td>{{ request.created }}</td>' +
            '<td>{{ request.finished }}</td>' +
            '<td>{{ this.removed }}</td>' +
            '<td>' +
                '<input type="button" class="cool_zone" v-if="update" v-on:click="do_update" value="Update" style="width: 70px; margin-bottom: 3px" />' +
                '<input type="button" class="danger_zone" v-on:click="remove" value="Delete" style="width: 70px;" />' +
            '</td>' +
        '</tr>',
    methods: {
        do_edit_topic: function() {
            if (this.status !== 'Canceled' && this.status !== 'Finished') {
                this.edit_topic = true;
            }
        },
        done_edit_topic: function() {
            this.edit_topic = false;
            this.update = true;
        },
        do_edit_comment: function() {
            if (this.status !== 'Canceled' && this.status !== 'Finished') {
                this.edit_comment = true;
            }
        },
        done_edit_comment: function() {
            this.edit_comment = false;
            this.update = true;
        },
        do_edit_status: function() {
            if (this.status !== 'Canceled' && this.status !== 'Finished') {
                this.edit_status = true;
            }
        },
        done_edit_status: function() {
            this.edit_status = false;
            this.update = true;
        },
        do_update: function() {
            this.request.topic = this.topic;
            this.request.comment = this.comment;
            this.request.status = this.status;

            if (this.request.typical) {
                typicalRequestApi.update({id: this.request.id}, this.request);
            } else {
                requestApi.update({id: this.request.id}, this.request).then(
                    result => result.json().then(
                        data => {
                            this.status = data.status;
                            this.comment = data.comment;
                            this.topic = data.topic;
                            this.moderator = data.moderator;
                            this.removed = data.removed;
                        }
                    )
                );
            }
            this.update = false;
        },
        remove: function() {
            if (this.request.typical) {
                typicalRequestApi.remove({id: this.request.id}, this.request).then(
                    result => {
                        if (result.ok) {
                            this.requests.splice(this.requests.indexOf(this.request), 1);
                        }
                    }
                )
            } else {
                requestApi.remove({id: this.request.id}, this.request).then(
                    result => {
                        if (result.ok) {
                            this.requests.splice(this.requests.indexOf(this.request), 1);
                        }
                    }
                )
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

var requests = new Vue({
    el: '#request-list',
    data: {
        requests: [],
        departments: [],
        statuses: [],

        chosen_department: 'All',
        chosen_status: 'All',

        choose_department: false,
        choose_status: false
    },
    template:
        '<div>' +
            '<table>' +
                '<caption> Requests </caption>' +
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
                        ':request="request" :requests="requests"' +
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
    }
});