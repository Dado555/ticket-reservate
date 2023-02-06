const home = {template: '<home></home>'}
const login = {template: '<login></login>'}
const navbar = {template: '<navbar></navbar>'}
const manifestations = {template: '<manifestations></manifestations>'}
const manifestation = {template: '<manifestation></manifestation>'}
const tickets = {template: '<tickets></tickets>'}
const customers = {template: '<customers></customers>'}
const salesmans = {template: '<salesmans></salesmans>'}
const cart = {template: '<cart></cart>'}
const profile = {template: '<profile></profile>'}
const olmap = {template: '<olmap></olmap>'}

const router = new VueRouter({
    mode: 'hash',
    routes: [
        {path: '/', component: home},
        {path: '/login', component: login},
        {path: '/manifestations', component: manifestations},
        {path: '/manifestations/:id', component: manifestation},
        {path: '/tickets', component: tickets},
        {path: '/customers', component: customers},
        {path: '/salesmans', component: salesmans},
        {path: '/cart', component: cart},        
        {path: '/:username', component: profile}
    ]
});

var app = new Vue({
    router,
    el: '#core'
})