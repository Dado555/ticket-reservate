Vue.component("customers", {
    data: function () {
            return {
                customers: [],
				searchParams:{}
			}
    },
	template: `
		<div><navbar></navbar>
        <div class="container manageCustomer">
			<div class="search-bar-customer">
				<input class="search-field username" name="search" type="text" placeholder="Username" v-model="searchParams.username">
				<input class="search-field firstName" type="text" placeholder="First name" v-model="searchParams.firstName">
				<input class="search-field lastName" type="text" placeholder="Last name" v-model="searchParams.lastName">
				<button class="search-btn-customer" type="submit" v-on:click="searchCustomers()">
					<i class="fa fa-search"></i>
				</button>
			</div>
			<div class="sort-filter-customer">
				<h3>Sort by: </h3>
				<select class="search-select-customer" v-model="searchParams.sortType" v-on:change="searchCustomers()">
					<option value="empty">-----</option>
					<option value="usernameAscending">Username  &#8593;</option>
					<option value="usernameDesceding">Username  &#8595;</option>
					<option value="firstNameAsceding">First name  &#8593</option>
					<option value="firstNameDesceding">First name  &#8595</option>
					<option value="lastNameAsceding">Last name  &#8593</option>
					<option value="lastNameDesceding">Last name  &#8595</option>
					<option value="pointsAsceding">Points  &#8593</option>
					<option value="pointsDesceding">Points  &#8595</option>
				</select>

				<h3> Filter by: </h3>
				<select id="filterSelect" class="search-select-customer" v-model="searchParams.filterType" v-on:change="searchCustomers()">
					<option value="empty">-----</option>
					<option value="regular">Regular users</option>
					<option value="bronze">Bronze users</option>
					<option value="silver">Silver users</option>
					<option value="gold">Gold users</option>
				</select>
			</div>
            <table id="customertable">
                <tr>
                    <th>Username</th>
                    <th>First name</th>
                    <th>Last name</th>
					<th>Gender</th>
					<th>Birth date</th>
					<th></th>
					<th></th>
                </tr>
                <tr v-for="customer in customers">
					<td>{{ customer.username }}</td>
					<td>{{ customer.firstName }}</td>
					<td>{{ customer.lastName }}</td>
					<td>{{ customer.gender }}</td>
					<td>{{ new Date(parseInt(customer.birthDate)) | dateFormat("DD.MM.YYYY. HH:mm")}}</td>
					<td><a href='/' v-on:click="removeCustomer($event, customer.username)" id="remove">Remove</a></td>
					<td v-if="!customer.suspicious"></td>
					<td v-if="customer.suspicious && customer.blocked">
						<a href='/' v-on:click="blockCustomer($event, customer.username, false)" id="block">Unblock</a>
					</td>
					<td v-if="customer.suspicious && !customer.blocked">
						<a href='/' v-on:click="blockCustomer($event, customer.username, true)" id="block">Block</a>
					</td>
					<td v-else></td>
                </tr>
            </table>
        </div></div>`
		,
    mounted () {
        this.listCustomers();
    },
	methods : {
		listCustomers: function() {
			axios({
				method: 'GET',
				url: 'rest/customers/'
			}).then(response => {
				this.customers = response.data;
			});
		},
		removeCustomer: function(e, username) {
			 e.preventDefault();
			 let user = JSON.parse(localStorage.getItem('user'));
			 if (!user) return;
			 let jwt = user.jwt;
		   	 axios({
				method: 'DELETE',
				url: 'rest/customers/'+ username,
				headers: { 'Authorization':'Bearer ' + jwt },
			 }).then((response) => {
                if ("error" in response.data) {
					alert(response.data.message);
					return;
				}
				toast("Customer successfully removed!");
				this.listCustomers();
             });
		},
		blockCustomer: function(e, username, blocked) {
			e.preventDefault();
			let user = JSON.parse(localStorage.getItem('user'));
			if (!user) return;
			let jwt = user.jwt;
			axios({
				method: 'PUT',
				url: 'rest/customers/'+ username,
				data: blocked,
				headers: { 'Authorization':'Bearer ' + jwt },
			 }).then((response) => {
                if ("error" in response.data) {
					alert(response.data.message);
					return;
				}
				toast("Customer successfully blocked!");
				this.listCustomers();
             });
		},
		searchCustomers: function() {
			let user = JSON.parse(localStorage.getItem('user'));
			console.log(user);
			if (!user) return;
			let jwt = user.jwt;
			console.log(this.searchParams);
			axios({
				method: 'POST',
				url: 'rest/customers/ssf/ssf',
				data: this.searchParams,
				headers: { 'Authorization':'Bearer ' + jwt }
			}).then((response) => {
				if(response.data == null)
					toast("No customers with that parameters!");
				else 
					this.customers = response.data;
				console.log(response.data);
			}, (error) => {console.log(error);});
		}
	},
	filters: {
    	dateFormat: function (value, format) {
    		var parsed = moment(value);
    		return parsed.format(format);
		}
	},
	components: {
      	vuejsDatepicker
    }
})