
function getIndex(list, id) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].id === id) {
            return i;
        }
    }

    return -1;
}

var roleApi = Vue.resource('/api/role{/id}');

Vue.component('role-form', {
    props: ['roles', 'role_for_edit'],
    data: function () {
        return {
            id: '',
            name: '',
            description: ''
        }
    },
    watch: {
        role_for_edit: function(newState, oldState) {
            this.id = newState.id;
            this.name = newState.name;
            this.description = newState.description;
        }
    },
    template:
        '<div style="margin: 10px;">' +
            '<input type="text" placeholder="Enter role name" v-model="name"/><br/>' +
            '<textarea placeholder="Enter role description" cols="30" rows="7" v-model="description"></textarea><br/>' +
            '<input type="button" value="Save" v-on:click="save_role"/>' +
        '</div>',
    methods: {
        save_role: function () {
            var role = {
                name: this.name,
                description: this.description
            };

            if (this.id) {
                var index = getIndex(this.roles, this.id);
                roleApi.update({id: this.id}, role).then(result =>
                    result.json().then(data => {
                        var index = getIndex(this.roles, data.id);
                        this.roles.splice(index, 1, data);
                    })
                )
            } else {
                roleApi.save({}, role).then(result =>
                    result.json().then(data => {
                        this.roles.push(data);
                        this.name = '';
                        this.description = '';
                    })
                )
            }
        }
    }
});

Vue.component('role-info', {
    props: ['role', 'roles', 'edit_outer'],
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
                '<input type="button" value="edit" v-on:click="edit_inner" />' +
                '<input type="button" value="delete" v-on:click="remove"/>' +
            '</div>' +
            '<hr/>' +
        '</div>',
    methods: {
        edit_inner: function() {
            this.edit_outer(this.role);
        },
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
    data: function() {
        return {
            role: null
        }
    },
    template:
        '<div style="width: 350px;">' +
            '<role-form :roles="roles" :role_for_edit="role"/>' +
            '<role-info v-for="role in roles" :role="role" :roles="roles" :key="role.id" :edit_outer="edit_outer" />' +
        '</div>',
    created: function() {
        roleApi.get().then(result =>
            result.json().then(data => {
                data.forEach(role => this.roles.push(role))
            })
        )
    },
    methods: {
        edit_outer: function(role) {
            this.role = role;
        }
    }
});

var role = new Vue({
    el: '#role_ui',
    template: '<role-list :roles="roles"/>',
    data: {
        roles: []
    }
});