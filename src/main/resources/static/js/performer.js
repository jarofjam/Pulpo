
var departmentApi = Vue.resource('/api/department');
var statusApi = Vue.resource('/api/status');
var requestApi = Vue.resource('/api/performer/request/{id}?department={department}&status={status}');
var typicalRequestApi = Vue.resource('api/performer/typicalrequest/{id}?department={department}&status={status}');

var departmentRequestApi = Vue.resource('/api/performer/department/request');
var departmentTypicalRequestApi = Vue.resource('api/performer/department/typicalrequest');

Vue.component('department-request-row', {
    props: ['request', 'requests'],
    data: function() {
        return {
            text: ''
        }
    },
    template:
        '<tr>' +
            '<td>{{ request.topic }}</td>' +
            '<td>{{ this.text }}</td>' +
            '<td>{{ request.comment }}</td>' +
            '<td>{{ request.author }}</td>' +
            '<td>{{ request.moderator }}</td>' +
            '<td>{{ request.created }}</td>' +
            '<td><input type="button" class="cool_zone" value="Sign up" v-on:click="signUp" /></td>' +
        '</tr>',
    methods: {
        signUp: function() {
            if (this.request.typical) {
                this.request.status = 'Ongoing';

                typicalRequestApi.update({id: this.request.id}, this.request).then(
                    this.requests.splice(this.requests.indexOf(this.request), 1)
                )
            } else {
                this.request.status = 'Ongoing';

                requestApi.update({id: this.request.id}, this.request).then(
                    this.requests.splice(this.requests.indexOf(this.request), 1)
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


Vue.component('my-request-row', {
    props: ['request'],
    data: function() {
        return {
            text: '',
            status: this.request.status,
            comment: this.request.comment,
            finished: this.request.finished,
            removed: this.request.removed,

            edit_comment: false,
            edit_status: false,

            update: false
        }
    },
    template:
        '<tr>' +
            '<td>{{ request.topic }}</td>' +
            '<td>{{ this.text }}</td>' +
            '<td v-if="this.edit_comment"><textarea v-on:change="done_edit_comment" v-model="comment"> {{this.comment}} </textarea></td>' +
                '<td v-else v-on:click="do_edit_comment" class="clickable" title="Edit" >{{ this.comment }}</td>' +
            '<td v-if="this.edit_status" v-on:change="done_edit_status">' +
                '<select v-model="status">' +
                    '<option value="Ongoing">Ongoing</option>' +
                    '<option value="Invalid">Invalid</option>' +
                    '<option value="Canceled">Canceled</option>' +
                    '<option value="Finished">Finished</option>' +
                '</select>'+
            '</td>' +
            '<td v-else v-on:click="do_edit_status" class="clickable" title="Edit">{{ this.status }}</td>' +
            '<td>{{ request.author }}</td>' +
            '<td>{{ request.performer }}</td>' +
            '<td>{{ request.moderator }}</td>' +
            '<td>{{ request.created }}</td>' +
            '<td>{{ this.finished }}</td>' +
            '<td>{{ this.removed }}</td>' +
            '<td>' +
                '<input type="button" class="cool_zone" v-if="update" v-on:click="do_update" value="Update" style="width: 70px; margin-bottom: 3px" />' +
                '<br/>' +
                '<input v-if="this.status !== \'Canceled\' && this.status !== \'Finished\'" type="button" class="danger_zone" v-on:click="cancel" value="Cancel" style="width: 70px;" />' +
            '</td>' +
        '</tr>',
    methods: {
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
            this.request.comment = this.comment;
            this.request.status = this.status;

            if (this.request.typical) {
                typicalRequestApi.update({id: this.request.id}, this.request);
                if (this.request.status === 'Finished') {
                    var date = new Date();
                    this.finished = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
                }
            } else {
                requestApi.update({id: this.request.id}, this.request).then(
                    result => result.json().then(
                        data => {
                            this.removed = data.removed;
                            this.finished = data.finished;
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
                        }
                    )
                );
            }
        },
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

var performer = new Vue({
    el: '#performer',
    data: function() {
        return {
            departments: [],
            statuses: [],
            departmentRequests: [],
            myRequests: [],

            chosen_department: 'All',
            chosen_status: 'All',

            choose_department: false,
            choose_status: false,

            myOrDepartment: 'my'
        }
    },
    template:
        '<div>' +
            '<div v-if="myOrDepartment === \'department\'">' +
                '<input class="notChosen" type="button" value="Show my requests" v-on:click="show_my" />' +
                '<input class="chosen" type="button" value="Show my department\'s requests" v-on:click="show_department" />' +

                '<table>' +
                    '<caption>Free requests for my department</caption>' +
                    '<tr>' +
                        '<th>Topic</th>' +
                        '<th>Text</th>' +
                        '<th>Comment</th>' +
                        '<th>Author</th>' +
                        '<th>Moderator</th>' +
                        '<th>Created</th>' +
                        '<th>Actions</th>' +
                    '</tr>' +
                    '<template v-if="departmentRequests.length === 0">' +
                        'No requests found' +
                    '</template>' +
                    '<template v-else>' +
                        '<department-request-row ' +
                            'v-for="request in departmentRequests" :key="request.id" ' +
                            ':request="request" :requests="departmentRequests"' +
                        '/>' +
                    '</template>' +
                '</table>' +
            '</div>' +
            '<div v-if="myOrDepartment === \'my\'">' +
                '<input class="chosen" type="button" value="Show my requests" v-on:click="show_my" />' +
                '<input class="notChosen" type="button" value="Show my department\'s requests" v-on:click="show_department" />' +
                '<table>' +
                    '<tr>' +
                        '<th>Topic</th>' +
                        '<th>Text</th>' +
                        '<th>Comment</th>' +
                        '<th v-if="this.choose_status" v-on:change="done_choose_status" >' +
                            '<select v-model="chosen_status">' +
                                '<option value="All">All</option>' +
                                '<option-status ' +
                                    'v-for="status in statuses" :key="status.id" ' +
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
                    '<template v-if="myRequests.length === 0">' +
                        'No requests found' +
                    '</template>' +
                    '<template v-else>' +
                        '<my-request-row ' +
                        'v-for="request in myRequests" :key="request.id" ' +
                        ':request="request"' +
                        '/>' +
                    '</template>' +
                '</table>' +
            '</div>' +
        '</div>',
    methods: {
        show_my: function() {
            this.myOrDepartment = 'my';
            this.refresh_my_requests();
        },
        show_department: function() {
            this.myOrDepartment = 'department';
            this.refresh_department_requests();
        },
        refresh_my_requests: function() {
            this.myRequests = [];

            requestApi.get({department: this.chosen_department, status: this.chosen_status}).then(
                result => result.json().then(
                    data => data.forEach(request => {
                        request.typical = false;
                        this.myRequests.push(request);
                    })
                )
            );

            typicalRequestApi.get({department: this.chosen_department, status: this.chosen_status}).then(
                result => result.json().then(
                    data => data.forEach(typicalRequest => {
                        typicalRequest.typical = true;
                        this.myRequests.push(typicalRequest);
                    })
                )
            );
        },
        refresh_department_requests: function() {
            this.departmentRequests = [];

            departmentRequestApi.get().then(
                result => result.json().then(
                    data => data.forEach(request => {
                        request.typical = false;
                        this.departmentRequests.push(request);
                    })
                )
            )
            departmentTypicalRequestApi.get().then(
                result => result.json().then(
                    data => data.forEach(request => {
                        request.typical = true;
                        this.departmentRequests.push(request);
                    })
                )
            )
        },
        do_choose_status: function() {
            this.choose_status = true;
        },
        done_choose_status: function() {
            this.refresh_my_requests();
            this.choose_status = false;
        }
    },
    created: function() {
        this.refresh_my_requests();
        this.refresh_department_requests();

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