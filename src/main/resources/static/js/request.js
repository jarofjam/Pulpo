
var requestApi = Vue.resource('/api/client/request');

Vue.component('department-list', {
    props: ['set_department'],
    template:
        '<div class="block" style="width: 15%;">' +
            '<p>Выберите отдел</p><hr/>' +
            '<div class="department" v-on:click="CR">Бухгалтерия</div>' +
            '<div class="department" v-on:click="IT">IT</div>' +
            '<div class="department" v-on:click="HR">Отдел кадров</div>' +
            '<div class="department" v-on:click="AD">Администрация</div>' +
        '</div>',
    methods: {
        CR: function() {
            this.set_department('Бухгалтерия');
        },
        IT: function() {
            this.set_department('IT');
        },
        HR: function() {
            this.set_department('Отдел кадров');
        },
        AD: function() {
            this.set_department('Администрация');
        }
    }
});

Vue.component('template-preview', {
    props: ['i'],
    template:
        '<p class="template_button">Типовая заявка <b>#{{ i }}</b></p>',

});

Vue.component('template-list', {
    props: ['department', 'set_template'],
    template:
        '<div v-if="department" class="block" style="width: 25%;">' +
            '<p><b>{{ department }}</b><br/>' +
                '</p><hr/>' +
            '<div>' +
                '<template v-for="i in 5">' +
                    '<template-preview class="template_button" :i="i" />' +
                '</template>' +
                '<hr/>' +
                '<p class="template_button" v-on:click="new_request">Создать новую заявку</p>' +
            '</div>' +
        '</div>',
    methods: {
        new_request: function() {
            this.set_template({type: 'new'})
        }
    }
});

Vue.component('request-form', {
    props: ['form', 'department', 'hide'],
    data: function() {
        return {
            message: '',
            topic: '',
            description: '',
            comment: 'Комментарий отсутствует'
        }
    },
    template:
        '<div v-if="form" class="block" style="width: 45%; text-align: left;">' +
            '<p>Форма заявки</p><hr/>' +
            '<template v-if="form.type === \'new\' ">' +
                '<p><b>Отдел: </b>{{ department }}</p>' +
                '<b>Тема: </b><input style="width: 300px;" type="text" v-model="topic" />' +
                '<p><b>Детали заявки: </b></p><textarea cols="50" rows="12" v-model="description"></textarea>' +
                '<div class="button" style="width: 110px;" v-on:click="create">Создать заявку</div>' +
                '{{ this.message }}' +
            '</template>' +
            '<template v-else>' +
                //Later
            '</template>' +
        '</div>',
    methods: {
        create: function() {
            if (this.topic && this.description) {
                this.hide();

                var request = {
                    topic: this.topic,
                    description: this.description,
                    department: this.department,
                    comment: this.comment,
                    status: 'Ожидает назначения исполнителя'
                };

                requestApi.save({}, request);

            } else {
                this.message = 'Заполните все поля';
            }
        }
    }
});

var request = new Vue({
    el: '#request',
    template:
        '<div>' +
            '<div v-if="display === \'hide\'">' +
                '<p>Заявка успешно создана</p>' +
                '<div class="button" style="width: 150px;" v-on:click="show">Создать новую заявку</div>' +
            '</div>'  +
            '<div v-if="display === \'show\'" style="text-align: center;">' +
                '<department-list :department="department" :set_department="set_department" />' +
                '<template-list :department="department" :set_template="set_template" />' +
                '<request-form :form="form" :department="department" :hide="hide"/>' +
            '</div>' +
        '</div>',
    data: function() {
        return {
            department: '',
            form: null,
            display: 'show'
        }
    },
    methods: {
        set_department: function(department) {
            this.department = department;
        },
        set_template: function(form) {
            this.form = form;
        },
        hide: function() {
            this.display = 'hide';
        },
        show: function () {
            this.display = 'show';
            this.department = '';
            this.form = null;
        }
    }
});