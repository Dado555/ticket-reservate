Vue.component("navbar", {
    data: function () {
            return {
                userType: null
			}
    },
    template: `
        <header>
            <div class="logo">
            <a href="#/"><img src="img/logo.png" width="125px"></a>
            </div>
            <nav>
                <ul class="menu-items">
                    <li><a href='#/'>Home</a></li>
                    <li v-if="userType === 'ADMINISTRATOR'"><a href='#/salesmans'>Salesmans</a></li>
                    <li v-if="userType === 'ADMINISTRATOR'"><a href='#/customers'>Customers</a></li>
                    <li v-if="userType === 'ADMINISTRATOR' || userType === 'SALESMAN'"><a href='#/manifestations'>Manifestations</a></li>
                    <li v-if="userType === 'ADMINISTRATOR' || userType === 'SALESMAN' || userType === 'CUSTOMER'"><a href='#/tickets'>Tickets</a></li>
                    <!--<li v-if="userType === 'SALESMAN' || userType === 'CUSTOMER'"><a href='#/comments'>Comments</a></li>-->
                    <li v-if="userType === 'CUSTOMER'"><a href='#/profile'>Profile</a></li>
                    
                    <li v-if="userType == null"><a id="aLogin" href='#/login' class = "btn">Sign In</a></li>
                    <li v-if="userType != null"><a v-on:click="logout()" id="aLogout" href='#/' class="btn">Sign Out</a></li>
                </ul>
            </nav>
            <a v-if="userType === 'CUSTOMER'"id="aCart" class="shcart" href="#/cart"><img src="img/cart.png" width="30px" height="30px"></a>
        </header>
        `,
    mounted () {
        let user = JSON.parse(localStorage.getItem('user'));
        if (!user) return;
        this.userType = user.userType;
    },
    methods: {
        logout: function() {
            localStorage.removeItem('user');
            window.location.href = '/'
        }
    }
})