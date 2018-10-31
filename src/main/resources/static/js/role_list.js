
var roleApi = Vue.resource('/api/role{/id}');

Vue.component('role-form', {
    props: ['roles'],
    data: function () {
        return {
            name: '',
            description: ''
        }
    },
    template:
        '<div style="margin: 10px;">' +
            '<input type="text" placeholder="Enter role name" v-model="name"/><br/>' +
            '<textarea placeholder="Enter role description" cols="30" rows="7" v-model="description"></textarea><br/>' +
            '<input type="button" value="Create" v-on:click="save_role"/>' +
        '</div>',
    methods: {
        save_role: function () {
            var role = {
                name: this.name,
                description: this.description
            }

            roleApi.save({}, role).then(result =>
                result.json().then(data => {
                    this.roles.push(data);
                    this.name = '';
                    this.description = '';
            })
            )
        }
    }
});

Vue.component('role-info', {
    props: ['role', 'roles'],
    template:
        '<div>' +
            '<span>' +
                '<b>Role#{{ role.id }}</b><br/>' +
                '<b>{{ role.name }}</b><br/>' +
                '<i>(Created: {{ role.created }}</i>)<br/>' +
                '<i>Description:</i><br/>' +
                '<b><i style="margin: 7px;">{{ role.description }}</i></b>' +
            '</span>' +
            '<div style="padding: 10px; margin-top: 10px; border-top: 2px dashed grey">' +
                '<input type="button" value="edit" />' +
                '<input type="button" value="delete" v-on:click="remove"/>' +
            '</div>' +
            '<hr/>' +
        '</div>',
    methods: {
        remove: function () {
            roleApi.remove({id: this.role.id}).then(result => {
                if (result.ok) {
                    this.roles.splice(this.roles.indexOf(this.role), 1);
                }}
            )
        }
    }
});

Vue.component('role-list', {
    props: ['roles'],
    template:
        '<div style="width: 350px;">' +
            '<role-form :roles="roles" />' +
            '<role-info v-for="role in roles" :role="role" :roles="roles" :key="role.id"/>' +
        '</div>',
    created: function() {
        roleApi.get().then(result =>
            result.json().then(data => {
                data.forEach(role => this.roles.push(role))
            })
        )
    }
});

var role = new Vue({
    el: '#role_list',
    template: '<role-list :roles="roles"/>',
    data: {
        roles: []
    }
});