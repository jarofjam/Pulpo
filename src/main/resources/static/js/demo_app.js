
var userApi = Vue.resource('/api/user{/id}');

Vue.component('user-form', {
    props: ['users'],
    data: function() {
        return {
            login: '',
            password: '',
            email: '',
            realName: ''
        }
    },
    template:
        '<div>' +
            '<input type="text" placeholder="Enter login" v-model="login"/>' +
            '<input type="text" placeholder="Enter password" v-model="password"/>' +
            '<input type="text" placeholder="Enter your email" v-model="email"/>' +
            '<input type="text" placeholder="Enter your real name" v-model="realName"/>' +
            '<input type="button" value="Add" v-on:click="save_user" />' +
        '</div>',
    methods: {
        save_user: function () {
            var user = {
                login: this.login,
                password: this.password,
                email: this.email,
                realName: this.realName
            };

            userApi.save({}, user).then(result =>
                result.json().then(data => {
                    this.users.push(data);
                    this.login = '';
                    this.password = '';
                    this.email = '';
                    this.realName = '';
                })
            );
        }
    }
});

Vue.component('user-info', {
    props: ['user', 'users'],
    template:
        '<div>' +
            '<span>' +
                '<b>User#{{ user.id }}</b><br>' +
                '<i>Joined: {{ user.joined }}</i>' +
                '<div><i>Login:</i> <b>{{ user.login }} </b></div>' +
                '<div><i>Password:</i> <b>{{ user.password }}</b></div>' +
                '<div><i>E-mail:</i> <b>{{ user.email }}</b></div>' +
                '<div><i>Real name:</i> <b>{{ user.realName }}</b></div>' +
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
    el: '#app',
    template: '<user-list :users="users"/>',
    data: {
        users: []
    }
});
