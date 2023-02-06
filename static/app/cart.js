Vue.component("cart", {
    data: function () {
            return {
                items: [],
                discount: 0,
                total: 0
            }
    },
    template: `
        <div>
            <navbar></navbar>
            <div class="container cart">
                <table>
                    <tr>
                        <th>Ticket</th>
                        <th>Amount</th>
                        <th>Price</th>
                    </tr>
                    <tr v-for="item in items">
                        <td>
                            <div class="cart-info">
                                <img src="img/baja.jpg">
                                <div>
                                    <p>{{ item.manifestationID }}</p>
                                    <small>Price: {{ item.tickets[0].price }}$</small>
                                    <br>
                                    <a href="">Remove</a>
                                </div>
                            </div>
                        </td>
                        <td><input type="number" :value="item.tickets.length" readonly></td>
                        <td>{{ item.price }}</td>
                    </tr>
                </table>
                <div class="total-price">
                    <table>
                        <tr>
                            <td>Price:</td>
                            <td> {{ total }}$</td>
                        </tr>
                        <tr>
                            <td>Discount:</td>
                            <td>{{ discount }}$</td>
                        <tr>
                            <td>Total:</td>
                            <td>{{ total - discount }}$</td>
                        </tr>
                        <tr><a href='#' v-on:click="submit()" type="button" class="btn" value="Submit">Submit</a><td></td></tr>
                    </table>
                </div>
            </div>
        </div>`,
    mounted () {
        let user = JSON.parse(localStorage.getItem('user'));
        if (!user) return;
        let jwt = user.jwt;
        axios({
            method: 'GET',
            url: 'rest/cart/',
            headers: { 'Authorization':'Bearer ' + jwt }
        }).then((response) => {
            if ("error" in response.data) {
                alert(response.data.message);
                return;
            }
            this.items = response.data.items;
            this.discount = response.data.discount;
            this.total = response.data.total;
        })
    },
    methods: {
        submit : function() {
            let user = JSON.parse(localStorage.getItem('user'));
            if (!user) return;
            let jwt = user.jwt;
            axios({
				method: 'POST',
                url: 'rest/cart/submit',
                headers: { 'Authorization':'Bearer ' + jwt }
			}).then((response) => {
                if ("error" in response.data) {
                    alert(response.data.message);
                    return;
                }
                toast("Cart successfully submitted!");
                window.location.href = '/#/';
            });
        }
    }
})