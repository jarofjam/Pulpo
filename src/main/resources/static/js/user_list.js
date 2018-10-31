var userApi = Vue.resource('/api/user{/id}');

Vue.component('user-form', {
    props: ['users'],
    data: function() {
        return {
            username: '',
            password: '',
            role: '',
            realName: '',
            department: ''
        }
    },
    template:
        '<div>' +
        '<input type="text" placeholder="Enter username" v-model="username"/>' +
        '<input type="text" placeholder="Enter password" v-model="password"/>' +
        '<input type="text" placeholder="Enter user role" v-model="role"/>' +
        '<input type="text" placeholder="Enter user real name" v-model="realName"/>' +
        '<input type="text" placeholder="Enter user department" v-model="department"/>' +
        '<input type="button" value="Add" v-on:click="save_user" />' +
        '</div>',
    methods: {
        save_user: function () {
            var user = {
                active: true,
                username: this.username,
                password: this.password,
                role: this.role,
                realName: this.realName,
                department: this.department
            };

            userApi.save({}, user).then(result =>
                result.json().then(data => {
                    this.users.push(data);
                    this.username = '';
                    this.password = '';
                    this.role = '';
                    this.realName = '';
                    this.department = '';
                })
            );
        }
    }
});

Vue.component('user-info', {
    props: ['user', 'users'],
    template:
        '<div v-if="user.active">' +
            '<span>' +
                '<b>User#{{ user.id }}</b><br>' +
                '<i>Created: {{ user.created }}</i><br>' +
                '<i>Removed: {{ user.removed }}</i><br>' +
                '<div><i>Username:</i> <b>{{ user.username }} </b></div>' +
                '<div><i>Password:</i> <b>{{ user.password }}</b></div>' +
                '<div><i>Real name:</i> <b>{{ user.realName }}</b></div>' +
                '<div><i>Role:</i> <b>{{ user.role }}</b></div>' +
                '<div><i>Department:</i> <b>{{ user.department }}</b></div>' +
            '</span>' +
                '<div style="padding: 10px; margin-top: 10px; border-top: 2px dashed grey">' +
                '<input type="button" value="edit" />' +
                '<input type="button" value="delete" v-on:click="remove" />' +
            '</div>' +
        '<hr>' +
        '</div>',
    methods: {
        remove: function () {
            userApi.remove({id: this.user.id}).then(result => {
                if (result.ok) {
                this.users.splice(this.users.indexOf(this.user), 1);
            }
        });
        }
    }
});

Vue.component('user-list', {
    props: ['users'],
    template:
        '<div style="position: relative; width: 250px;">' +
        '<user-form :users="users"/>' +
        '<user-info v-for="user in users" :key="user.id" :users="users" :user="user" />' +
        '</div>',
    created: function () {
        userApi.get().then(result =>
        result.json().then(data =>
        data.forEach(user => this.users.push(user))
    )
    )
    }
});

var app = new Vue({
    el: '#admin_panel',
    template: '<user-list :users="users"/>',
    data: {
        users: []
    }
});