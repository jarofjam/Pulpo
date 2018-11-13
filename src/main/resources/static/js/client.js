
function getIndex(list, id) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].id === id) {
            return i;
        }
    }

    return -1;
}

var clientPostApi = new Vue.resource('/api/client/request/{id}');
var clientGetApi = new Vue.resource('/api/client/request?status={status}');
var statusApi = new Vue.resource('/api/status');

Vue.component('request', {
    props: ['request', 'choose_outer', 'statuses'],
    data: function() {
        return {
            status: ''
        }
    },
    template:
        '<div class="title"  v-on:click="choose_inner">' +
            '<div><b>{{ request.topic }}</b></div><hr/>' +
            '<div><i>Статус: {{ this.status }}</i></div>' +
            '<div><i>Дата создания: {{ request.created }}</i></div>' +
            '<div><i>Дедлайн: {{ request.deadline }}</i></div>' +
        '</div>',
    methods: {
        choose_inner: function() {
            this.choose_outer(this.request);
        }
    },
    created: function() {
        this.status = this.statuses[getIndex(this.statuses, this.request.status)].status_title;
    }
});

Vue.component('title-list', {
    props: ['requests', 'statuses', 'choose_outer', 'semi_topic'],
    template:
        '<div>' +
            '<p>Мои заявки: {{ semi_topic }}</p><hr/>' +
            '<template v-if="requests.length !== 0">' +
                '<request v-for="request in requests"  style="text-align: left;" :request="request" :statuses="statuses" :key="request.id" :choose_outer="choose_outer"/>' +
            '</template>' +
            '<p v-else>Отсутствуют</p>' +
        '</div>'
});

Vue.component('request-content', {
    props: ['request', 'requests', 'statuses', 'all'],
    data: function() {
        return {
            id: '',
            description: '',
            status: '',
            edit_flag: false,
            open: null,
            removed: null
        }
    },
    watch: {
        request: function(newState, oldState) {
            this.id = newState.id;
            this.description = newState.description;
            this.status = this.statuses[getIndex(this.statuses, newState.status)].status_title;
            this.open = (newState.removed === null);
            this.cancelInfo = newState.cancelInfo;
            this.removed = newState.removed;
        }
    },
    template:
        '<div v-if="request">' +
            '<p>Детали заявки: </p><hr/>' +
            '<p><b>Статус: </b>{{ this.status }} {{ this.cancelInfo }} {{ this.removed }}</p>' +
            '<p><b>Отдел: </b>{{ request.department }}</p>' +
            '<p><b>Тема: </b>{{ request.topic }}</p>' +
            '<p><b>Комментарий по заявке: </b>{{ request.comment }}</p>' +
            '<p><b>Детали заявки: </b>{{ this.description }}</p>' +
            '<div v-if="open">' +
                '<div v-if="this.edit_flag">' +
                    '<p><b>Редактировать детали: </b><br/> <textarea cols="50" rows="12" v-model="description"></textarea></p>' +
                    '<input type="button" value="Сохранить детали" v-on:click="save"/>' +
                '</div>' +
                '<div v-else>' +
                    '<input type="button" value="Редактировать детали" v-on:click="edit"/>' +
                '</div>' +
                '<hr/><input type="button" value="Отменить заявку" v-on:click="cancel"/>' +
            '</div>' +
        '</div>',
    methods: {
        edit: function() {
            this.edit_flag = true;
        },
        save: function() {
            this.edit_flag = false;
            var request = {
                description: this.description
            };

            clientPostApi.update({id: this.id}, request).then(result =>
                result.json().then(data => {
                    var index = getIndex(this.requests, data.id);
                    this.requests.splice(index, 1, data);
                    this.description = data.description;
                    this.status = this.statuses[getIndex(this.statuses, data.status)].status_title;
                    this.cancelInfo = data.cancelInfo;
                    this.removed = data.removed;
                })
            )
        },
        cancel: function() {
            var request = {};

            clientPostApi.update({id: this.id}, request).then(result =>
                result.json().then(data => {
                    var index = getIndex(this.requests, data.id);
                    if (this.all) {
                        this.requests.splice(index, 1, data);
                    }
                    else {
                        this.requests.splice(index, 1);
                    }
                    this.description = data.description;
                    this.status = this.statuses[getIndex(this.statuses, data.status)].status_title;
                    this.cancelInfo = data.cancelInfo;
                    this.removed = data.removed;
                })
            )
            this.open = false;
        }
    }
});

Vue.component('status-button', {
    props: ['status', 'choose_status'],
    template:
        '<div class="title" v-on:click="choose">' +
            '{{ status.status_title }}' +
        '</div>',
    methods: {
        choose: function() {
            this.choose_status(this.status.status_title, this.status.id);
        }
    }
});

var client = new Vue({
    el: '#client',
    template:
        '<div style="text-align: center;">' +
            '<div class="block">' +
                '<p>Заявки по статусу: </p><hr/>' +
                '<div class="title" v-on:click="all_status">Все</div>' +
                '<status-button v-for="status in statuses" :status="status" :choose_status="choose_status" :key="status.id" />' +
            '</div>' +
            '<div class="block" style="width: 370px; border-left: 1px solid gray;">' +
                '<title-list  :requests="requests" :statuses="statuses" :choose_outer="choose_outer" :semi_topic="semi_topic"/>' +
            '</div>' +
            '<div class="block" style="width: 500px; position: fixed; left:700px; text-align: left; border-left: 1px solid gray;">' +
                '<request-content  :request="request" :requests="requests" :statuses="statuses" :all="all"/>' +
            '</div>' +
        '</div>',
    data: function() {
        return {
            requests: [],
            statuses: [],
            request: null,
            semi_topic: 'Все',
            chosen_status: 'ALL',
            all: true
        }
    },
    created: function() {

        statusApi.get().then(result =>
            result.json().then(data => {
                data.forEach(status => this.statuses.push(status))
            })
        );
    },
    mounted: function() {
        clientGetApi.get({status: this.chosen_status}).then(result =>
            result.json().then(data => {
                data.forEach(request => this.requests.push(request))
            })
        );
    },
    methods: {
        choose_outer: function(request) {
            this.request = request;
        },
        choose_status: function(semi_topic, chosen_status) {
            this.semi_topic = semi_topic;
            this.chosen_status = chosen_status;
            this.all = false;
            this.fetch_again();
        },
        all_status: function() {
            this.semi_topic = 'Все';
            this.chosen_status = 'ALL';
            this.all = true;
            this.fetch_again();
        },
        fetch_again: function() {
            this.requests = [];
            clientGetApi.get({status: this.chosen_status}).then(result =>
                result.json().then(data => {
                    data.forEach(request => this.requests.push(request))
                })
            )
        }
    }
});