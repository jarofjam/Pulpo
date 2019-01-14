
var userApi = Vue.resource('/api/user/{id}');
var roleApi = Vue.resource('/api/role');
var departmentApi = Vue.resource('/api/department');

Vue.component('option-department', {
    props: ['department'],
    template:
        '<option :value=department.name>{{ department.name }}</option>'
});

Vue.component('user-row', {
    props: ['user', 'users'],
    template:
        '<tr>' +
            '<td>{{ user.department }}</td>' +
            '<td>{{ user.username }}</td>' +
            '<td>{{ user.realName }}</td>' +
            '<td>{{ user.managerOf }}</td>' +
            '<td>' +
                '<ul style="list-style-type: none">' +
                    '<li v-for="role in user.roles" :key=role :role="role" >{{role}}</li>' +
                '</ul>' +
            '</td>' +
            '<td>{{ user.created }}</td>' +
            '<td>{{ user.removed }}</td>' +
            '<td>' +
                '<input v-if="!this.user.removed" type="button" class="danger_zone" v-on:click="cancel" value="Disable" style="width: 70px; margin-bottom: 5px" />' +
                '<br/>' +
                '<input type="button" class="danger_zone" v-on:click="remove" value="Delete" style="width: 70px;" />' +
            '</td>' +
        '</tr>',
    methods: {
        cancel: function() {
            this.user.remove = true;
            userApi.update({id: this.user.id}, this.user).then(
                result => result.json().then(
                    data => this.users.splice(this.users.indexOf(this.user), 1, data)
                )
            )
        },
        remove: function() {
            userApi.remove({id: this.user.id});
            this.users.splice(this.users.indexOf(this.user), 1);
        }
    }
});

var admin = new Vue({
    el: '#admin',
    data: function() {
        return {
            users: [],
            roles: [],
            departments: [],

            choose_department: false,
            chosen_department: 'All',

            user_editor_visible: false,
            create_user_data: new Object(),

            user_roles: []
        }
    },
    template:
        '<div>' +
            '<div v-if="!user_editor_visible">' +
                '<input type="button" class="notChosen" value="Create new user" v-on:click="show_user_editor"/>' +
            '</div>' +
            '<div v-else>' +
                '<input type="button" class="chosen" value="Create new user"/>' +
                '<div style=" border: 2px solid black; width: 30%; position: fixed; left: 30%; top: 15%; background-color: white">' +
                    '<input type="button" class="danger_zone" value="X" v-on:click="hide_user_editor" style="float: right;"/>' +
                    '<p style="text-align: center">User editor<input type="button" class="notChosen" value="Save" v-on:click="save"/></p>' +
                    '<hr/>' +

                    '<p style="margin-left: 10px;">' +
                        'Choose department: ' +
                        '<select v-model="create_user_data[\'department\']">' +
                            '<option-department ' +
                                'v-for="department in departments" :key="department.id" ' +
                                ':department="department" ' +
                            '/>' +
                        '</select>' +
                    '</p>' +
                    '<p style="margin-left: 10px;">Username <input type="text" v-model="create_user_data[\'username\']" /></p>' +
                    '<p style="margin-left: 10px;">Password <input type="text" v-model="create_user_data[\'password\']" /></p>' +
                    '<p style="margin-left: 10px;">Real name <input type="text" v-model="create_user_data[\'realName\']" /></p>' +
                    '<input style="margin-left: 10px;" type="checkbox" id="ADMIN" value="ADMIN" v-model="user_roles">' +
                    '<label for="ADMIN">ADMIN</label><br/>' +
                    '<input style="margin-left: 10px;" type="checkbox" id="MODERATOR" value="MODERATOR" v-model="user_roles">' +
                    '<label for="MODERATOR">MODERATOR</label><br/>' +
                    '<input style="margin-left: 10px;" type="checkbox" id="PERFORMER" value="PERFORMER" v-model="user_roles">' +
                    '<label for="PERFORMER">PERFORMER</label><br/>' +
                    '<input style="margin-left: 10px; margin-bottom: 20px;" type="checkbox" id="APPLICANT" value="APPLICANT" v-model="user_roles">' +
                    '<label for="APPLICANT">APPLICANT</label><br/>' +
                '</div>' +
            '</div>' +
            '<table>' +
                '<caption>Users</caption>' +
                '<tr>' +
                    '<th>Department</th>' +
                    '<th>Username</th>' +
                    '<th>Real name</th>' +
                    '<th>Manager of</th>' +
                    '<th>Roles</th>' +
                    '<th>Created</th>' +
                    '<th>Disabled</th>' +
                    '<th>Actions</th>' +
                '</tr>' +
                '<template v-if="users.length === 0">' +
                    'No users found' +
                '</template>' +
                '<template v-else>' +
                    '<user-row ' +
                        'v-for="user in users" :key="user.id" ' +
                        ':user="user" :users="users"' +
                    '/>' +
                    '</template>' +
            '</table>' +
        '</div>',
    methods: {
        show_user_editor: function() {
            this.user_editor_visible = true;
        },
        hide_user_editor: function() {
            this.user_editor_visible = false;
            this.create_user_data = new Object();
            this.user_roles = [];
        },
        save: function() {
            var user = this.create_user_data;
            user['roles'] = this.user_roles;

            userApi.save(user).then(
                result => result.json().then(
                    data => this.users.push(data)
                )
            )
            this.hide_user_editor();
        }
    },
    created: function() {

        roleApi.get().then(
            result => result.json().then(
                data => data.forEach(role => this.roles.push(role))
            )
        )

        departmentApi.get().then(
            result => result.json().then(
                data => data.forEach(department => this.departments.push(department))
            )
        )

        userApi.get({department: this.chosen_department}).then(
            result => result.json().then(
                data => data.forEach(user => this.users.push(user))
            )
        )

    }
});