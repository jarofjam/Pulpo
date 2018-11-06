
function getIndex(list, id) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].id === id) {
            return i;
        }
    }

    return -1;
}

var clientApi = new Vue.resource('/api/client/request/{id}');

Vue.component('request', {
    props: ['request', 'choose_outer'],
    template:
        '<div class="title"  v-on:click="choose_inner">' +
            '<div><b>{{ request.topic }}</b></div><hr/>' +
            '<div><i>Дедлайн: {{ request.deadline }}</i></div>' +
        '</div>',
    methods: {
        choose_inner: function() {
            this.choose_outer(this.request);
        }
    }
});

Vue.component('title-list', {
    props: ['requests', 'choose_outer'],
    template:
        '<div class="main-column">' +
            '<p>Мои заявки</p><hr/>' +
            '<request v-for="request in requests"  style="text-align: left;" :request="request" :key="request.id" :choose_outer="choose_outer"/>' +
        '</div>'
});

Vue.component('request-content', {
    props: ['request', 'requests'],
    data: function() {
        return {
            description: '',
            id: ''
        }
    },
    watch: {
        request: function(newState, oldState) {
            this.id = newState.id;
            this.description = newState.description;
        }
    },
    template:
        '<div v-if="request" class="main-column" style="text-align: left;">' +
            '<p>Детали заявки: </p><hr/>' +
            '<p><b>Статус: </b>{{ request.status }}</p>' +
            '<p><b>Отдел: </b>{{ request.department }}</p>' +
            '<p><b>Тема: </b>{{ request.topic }}</p>' +
            '<p><b>Комментарий по заявке: </b>{{ request.comment }}</p>' +
            '<p><b>Детали заявки: </b><br/> <textarea cols="50" rows="12" v-model="description"></textarea></p>' +
            '<input type="button" value="Обновить данные" v-on:click="save"/>' +
        '</div>',
    methods: {
        save: function() {
            var request = {
                description: this.description
            }

            clientApi.update({id: this.id}, request).then(result =>
                result.json().then(data => {
                    var index = getIndex(this.requests, data.id);
                    this.requests.splice(index, 1, data);
                    description = data.description;
                })
            )
        }
    }
});

var client = new Vue({
    el: '#client',
    template:
        '<div style="text-align: center;">' +
            '<title-list style="width: 25%" :requests="requests" :choose_outer="choose_outer" />' +
            '<request-content style="width: 55%" :request="request" :requests="requests" />' +
        '</div>',
    data: function() {
        return {
            requests: [],
            request: null
        }
    },
    created: function() {

        clientApi.get().then(result =>
            result.json().then(data => {
                data.forEach(request => this.requests.push(request))
            })
        )
    },
    methods: {
        choose_outer: function(request) {
            this.request = request;
        }
    }
});