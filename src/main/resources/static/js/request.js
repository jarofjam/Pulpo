
var requestApi = Vue.resource('/api/applicant/request');

Vue.component('department', {

});

Vue.component('department-list', {
    props: ['set_department'],
    template:
        '<div class="block" style="width: 200px; position: fixed;">' +
            '<p>Choose department</p><hr/>' +
            '<template v-for="department in departments"/>' +
        '</div>',
    created: function() {

    }
});

Vue.component('template-preview', {
    props: ['i'],
    template:
        '<p class="template_button">Typical request<b>#{{ i }}</b></p>'

});

Vue.component('template-list', {
    props: ['department', 'set_template'],
    template:
        '<div v-if="department" class="block" style="width: 370px; margin-left: 250px;">' +
            '<p><b>{{ department }}</b><br/>' +
                '</p><hr/>' +
            '<div>' +
                '<template v-for="i in 15">' +
                    '<template-preview class="template_button" :i="i" />' +
                '</template>' +
                '<hr/>' +
                '<p class="template_button" v-on:click="new_request">Create new request</p>' +
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
            comment: 'No comment'
        }
    },
    template:
        '<div v-if="form" class="block" style="width: 450px; text-align: left; position: fixed; left: 670px;">' +
            '<p>Request form</p><hr/>' +
            '<template v-if="form.type === \'new\' ">' +
                '<p><b>Отдел: </b>{{ department }}</p>' +
                '<b>Тема: </b><input style="width: 300px;" type="text" v-model="topic" />' +
                '<p><b>Детали заявки: </b></p><textarea cols="50" rows="12" v-model="description"></textarea>' +
                '<div class="button" style="width: 110px;" v-on:click="create">Create new request</div>' +
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
                    comment: this.comment
                };

                requestApi.save({}, request);

            } else {
                this.message = 'Please, complete the form';
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
                '<div class="button" style="width: 150px;" v-on:click="show">Create new request</div>' +
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