
function getIndex(list, id) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].id === id) {
            return i;
        }
    }

    return -1;
}

var performerApi = Vue.resource('/api/performer/request/{id}');
var departmentApi = Vue.resource('/api/department/request');

Vue.component('public-request-summary', {
    props: ['request', 'choose_outer'],
    template:
        '<div v-on:click="choose_inner" class="summary" v-if="request.performer === null && request.status != \'Ожидает проверки модератором\'">' +
            '<div><b>{{ request.topic }}</b></div><hr/>' +
            '<div><i>Срок выполнения: {{ request.deadline }}</i></div>' +
        '</div>',
    methods: {
        choose_inner: function() {
            this.choose_outer(this.request, 'public');
        }
    }
});

Vue.component('private-request-summary', {
    props: ['request', 'choose_outer'],
    template:
        '<div v-on:click="choose_inner" class="summary">' +
            '<div><b>{{ request.topic }}</b></div><hr/>' +
            '<div><i>Срок выполнения: {{ request.deadline }}</i></div>' +
        '</div>',
    methods: {
        choose_inner: function() {
            this.choose_outer(this.request, 'private');
        }
    }
});

Vue.component('public-requests', {
    props: ['requests', 'choose_outer'],
    template:
        '<div class="block" style="width: 30%">' +
            '<p>Заявки моего департамента:</p><hr/>' +
            '<public-request-summary v-for="request in requests" :key="request.id" :request="request" :choose_outer="choose_outer"/>' +
        '</div>'
});

Vue.component('private-requests', {
    props: ['requests', 'choose_outer'],
    template:
        '<div class="block" style="width: 30%">' +
            '<p>Мои заявки: </p><hr/>' +
            '<private-request-summary v-for="request in requests" :key="request.id" :request="request" :choose_outer="choose_outer"/>' +
        '</div>'
});

Vue.component('request-info', {
    props: ['request', 'requestByDepartment', 'requestByPerformer'],
    template:
        '<div class="block" style="width: 50%; border-left: 1px solid grey;">' +
            '<p>Детали заявки: </p><hr/>' +
            '<p><b>Статус: </b>{{ request.status }}</p>' +
            '<p><b>Отдел: </b>{{ request.department }}</p>' +
            '<p><b>Тема: </b>{{ request.topic }}</p>' +
            '<p><b>Комментарий по заявке: </b>{{ request.comment }}</p>' +
            '<p><b>Детали заявки: </b>{{ request.description }}</p>' +
            '<input type="button" value="Взять на исполнение" v-on:click="take"/>' +
        '</div>',
    methods: {
        take: function() {
            var request = {
                comment: this.request.comment,
                status: 'На исполнении'
            }

            performerApi.update({id: this.request.id}, request).then(result =>
                result.json().then(data => {
                    var index = getIndex(this.requestByDepartment, data.id);
                    this.requestByDepartment.splice(index, 1);
                    this.requestByPerformer.push(data);
                })
            )
        }
    }
});

Vue.component('request-form', {
    props: ['request', 'requestByDepartment', 'requestByPerformer'],
    data: function() {
        return {
            id: '',
            comment: ''
        }
    },
    watch: {
        request: function(newState, oldState) {
            this.id = newState.id;
            this.comment = newState.comment;
        }
    },
    template:
        '<div class="block" style="width: 50%; border-left: 1px solid grey;">' +
            '<p>Детали заявки: </p><hr/>' +
            '<p><b>Статус: </b>{{ request.status }}</p>' +
            '<p><b>Отдел: </b>{{ request.department }}</p>' +
            '<p><b>Тема: </b>{{ request.topic }}</p>' +
            '<p><b>Комментарий по заявке: </b><br/><textarea cols="50" rows="12" v-model="comment"></textarea></p>' +
            '<p><b>Детали заявки: </b>{{ request.description }}</p>' +
            '<input type="button" value="Обновить комментарий" v-on:click="save" />' +
        '</div>',
    methods: {
        save: function() {
            var request = {
                status: this.request.status,
                comment: this.comment
            }

            performerApi.update({id: this.request.id}, request).then(result =>
            result.json().then(data => {
                    var index = getIndex(this.requestByPerformer, data.id);
                    this.requestByPerformer.splice(index, 1, data);
                })
            )
        }
    }
});

var performer = new Vue({
    el: '#performer',
    template:
        '<div>' +
            '<div>' +
                '<p class="summary" v-on:click="setPublic">Заявки моего департамента</p>'+
                '<p class="summary" v-on:click="setPrivate">Мои заявки</p>'+
            '</div>' +
            '<div>' +
                '<public-requests v-if="public" :requests="requestByDepartment" :choose_outer="choose_outer" />' +
                '<private-requests v-if="!public" :requests="requestByPerformer" :choose_outer="choose_outer" />' +
            '</div>' +
            '<div v-if="request">' +
                '<request-info v-if="this.form_type === \'public\'" :request="request" :requestByDepartment="requestByDepartment" :requestByPerformer="requestByPerformer" />' +
                '<request-form v-else :request="request" :requestByDepartment="requestByDepartment" :requestByPerformer="requestByPerformer" />' +
            '</div>' +
        '</div>',
    data: function() {
        return {
            request: null,
            requestByDepartment: [],
            requestByPerformer: [],
            public: true,
            form_type: ''
        }
    },
    methods: {
        choose_outer: function(request, form_type) {
            this.request = request;
            this.form_type = form_type;
        },
        setPrivate: function() {
            this.public = false;
        },
        setPublic: function() {
            this.public = true;
        }
    },
    created: function() {
        departmentApi.get().then(result =>
            result.json().then(data =>
                data.forEach(request => this.requestByDepartment.push(request))
            )
        );

        performerApi.get().then(result =>
            result.json().then(data =>
                data.forEach(request => this.requestByPerformer.push(request))
            )
        )
    }
});