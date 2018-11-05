
function getIndex(list, id) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].id === id) {
            return i;
        }
    }

    return -1;
}

var userApi = Vue.resource('/api/user{/id}');

Vue.component('user-form', {
    props: ['users', 'user_for_edit'],
    data: function() {
        return {
            id: '',
            username: '',
            password: '',
            realName: '',
            department: ''
        }
    },
    watch: {
        user_for_edit: function(newState, oldState) {
            this.id = newState.id;
            this.username = newState.username;
            this.password = newState.password;
            this.realName = newState.realName;
            this.department = newState.department;
        }
    },
    template:
        '<div style="margin: 10px;">' +
        '<input type="text" placeholder="Enter username" v-model="username"/>' +
        '<input type="text" placeholder="Enter password" v-model="password"/>' +
        '<input type="text" placeholder="Enter user real name" v-model="realName"/>' +
        '<input type="text" placeholder="Enter user department" v-model="department"/><br/>' +
        '<input type="button" value="Save" v-on:click="save_user" />' +
        '</div>',
    methods: {
        save_user: function () {
            var user = {
                username: this.username,
                password: this.password,
                realName: this.realName,
                department: this.department
            };

            if (this.id) {
                userApi.update({id: this.id}, user).then(result =>
                    result.json().then(data => {
                        var index = getIndex(this.users, data.id);
                        this.users.splice(index, 1, data);
                    })
                )
            } else {
                userApi.save({}, user).then(result =>
                    result.json().then(data => {
                        this.users.push(data);
                    })
                );
            }

            this.username = '';
            this.password = '';
            this.realName = '';
            this.department = '';
        }
    }
});

Vue.component('user-info', {
    props: ['user', 'users', 'edit_outer'],
    template:
        '<div>' +
            '<span>' +
                '<b>User#{{ user.id }}</b>' +
                '<i v-if="user.active" >' +
                    '<u>(active)</u>' +
                '</i><br/>' +
                '<i>Created: {{ user.created }}</i><br>' +
                '<i>Removed: {{ user.removed }}</i><br>' +
                '<div><i>Username:</i> <b>{{ user.username }} </b></div>' +
                '<div><i>Password:</i> <b>{{ user.password }}</b></div>' +
                '<div><i>Real name:</i> <b>{{ user.realName }}</b></div>' +
                '<div><i>Department:</i> <b>{{ user.department }}</b></div>' +
            '</span>' +
                '<div style="padding: 10px; margin-top: 10px; border-top: 2px dashed grey">' +
                    '<input type="button" value="edit" v-on:click="edit_inner" />' +
                    '<input type="button" value="delete" v-on:click="remove" />' +
                '</div>' +
            '<hr>' +
        '</div>',
    methods: {
        edit_inner: function() {
            this.edit_outer(this.user)
        },
        remove: function() {
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
    data: function() {
        return {
            user: null
        }
    },
    template:
        '<div style="position: relative; width: 250px;">' +
            '<user-form :users="users" :user_for_edit="user"/>' +
            '<user-info v-for="user in users" :key="user.id" :users="users" :user="user" :edit_outer="edit_outer" />' +
        '</div>',
    created: function () {
        userApi.get().then(result =>
            result.json().then(data =>
                data.forEach(user => this.users.push(user))
            )
        )
    },
    methods: {
        edit_outer: function(user) {
            this.user = user;
        }
    }
});

var user = new Vue({
    el: '#user_ui',
    template: '<user-list :users="users"/>',
    data: {
        users: []
    }
});