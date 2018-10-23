
var userApi = Vue.resource('/api/user{/id}');

Vue.component('user-form', {
    props: ['users'],
    data: function() {
        return {
            data: ''
        }
    },
    template:
        '<div>' +
            '<input type="text" placeholder="Enter something" v-model="data"/>' +
            '<input type="button" value="Add" v-on:click="save_user" />' +
        '</div>',
    methods: {
        save_user: function () {
            var user = { data: this.data };
            userApi.save({}, user).then(result =>
                result.json().then(data => {
                    this.users.push(data);
                    this.data = '';
                })
            );
        }
    }
});

Vue.component('user-info', {
    props: ['user', 'users'],
    template:
        '<div>' +
            '<b>{{ user.id }}) </b>{{ user.data }}' +
            '<span style="position: absolute; right: 0;">' +
                '<input type="button" value="edit" />' +
                '<input type="button" value="delete" v-on:click="remove" />' +
            '</span>' +
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
        '<div style="position: relative; width: 300px;">' +
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
