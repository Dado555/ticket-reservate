Vue.component("tickets", {
    data: function () {
            return {
				user: null,
				salesman: null,
                tickets: [],
				searchParams: {},
				map: new Map(),
				manifestations: [],
				currentManifestation: null
			}
    },
	template: `
		<div><navbar></navbar>
        <div class="container manageTicket">
			<div class="search-bar">
				<input class="search-field search" name="search" type="text" placeholder="Search..." v-model="searchParams.manifestationTitle">
				<input class="search-field priceFrom" type="number" placeholder="From" min="0.00" max="1000000.00" step="1" v-model="searchParams.priceFrom">
				<input class="search-field priceTo" type="number" placeholder="To" min="0.00" max="1000000.00" step="1" v-model="searchParams.priceTo">
				<input class="search-field dateFrom" placeholder="From" format="DD.MM.YYYY." type="text" onfocus="(this.type='date')" v-model="searchParams.dateFrom" required/>
				<input class="search-field dateTo" placeholder="To" format="DD.MM.YYYY." type="text" onfocus="(this.type='date')"  v-model="searchParams.dateTo" required/>

				<button class="search-btn" type="submit" v-on:click="searchTickets()">
					<i class="fa fa-search"></i>
				</button>
			</div>
			<div class="sort-filter">
				<h3>Sort by: </h3>
				<select class="search-select" v-model="searchParams.sortType" v-on:change="searchTickets()">
					<option value="empty">-----</option>
					<option value="manifestationTitleAscending">Title  &#8593;</option>
					<option value="manifestationTitleDescending">Title  &#8595;</option>
					<option value="dateTimeAscending">Date  &#8593</option>
					<option value="dateTimeDescending">Date  &#8595</option>
					<option value="priceAscending">Price  &#8593</option>
					<option value="priceDescending">Price  &#8595</option>
				</select>

				<h3> Filter by type: </h3>
				<select id="filterSelect" class="search-select" v-model="searchParams.filterType" v-on:change="searchTickets()">
					<option value="empty">-----</option>
					<option value="VIP">VIP</option>
					<option value="REGULAR">REGULAR</option>
					<option value="FAN_PIT">FAN_PIT</option>
				</select>
				
				<h3> Filter by status: </h3>
				<select id="filterSelect" class="search-select" v-model="searchParams.filterStatus" v-on:change="searchTickets()">
					<option value="empty">-----</option>
					<option value="UNRESERVED">UNRESERVED</option>
					<option value="RESERVED">RESERVED</option>
					<option value="CANCELED">CANCELED</option>
				</select>
			</div>
			<template v-if="tickets.length != 0">
	            <table v-if="manifestations.length != 0" id="ticketTable">
					<tbody>
	                <tr>
	                    <th>Manifestation</th>
	                    <th>Price</th>
	                    <th>User</th>
						<th>Status</th>
						<th>Type</th>
						<th></th>
	                </tr>
	                <tr v-for="item in tickets">

	                    <td>{{ item.manifestationID }} : {{ item.title }}</td>
	                    <td>{{ item.price }}</td>
	                    <td>{{ item.customerUsername }}</td>
						<td>{{ item.status }}</td>
						<td>{{ item.type}}</td>
						<td v-if="user.userType === 'CUSTOMER'"><a href='/' v-on:click="cancelTicket($event, item)" id="cancelTicket">Cancel</a></td>
	                </tr>
					</tbody>
	            </table>
			</template>
		</div>
		</div>`
		,
    mounted () {
		this.listTickets();
    },
	methods : {
		listTickets: function() {
			this.user = JSON.parse(window.localStorage.getItem('user'));
			let jwt = this.user.jwt;
			axios({
				method: 'GET',
				url: 'rest/' + this.user.username + '/tickets',
				headers: { 'Authorization':'Bearer ' + jwt }
			}).then((response) => {
				this.tickets = response.data;
				axios({
					method: 'GET',
					url: 'rest/manifestations/',
					headers: { 'Authorization':'Bearer ' + jwt },
				}).then(response => {
					this.manifestations = fixDate(response.data); 
					// this.types = getTypes(response.data);
					this.addTitle();
				});
			});
		},
		checkLength: function() {
			if(this.tickets.length != 0){
				alert(this.tickets.length);
				return true;
			}
			return false;
		},
		addTitle: function() {
			this.tickets.forEach((element) => {
				element.title = this.findCurrentManifestation(element).title;
			});
		},
		findCurrentManifestation: function(item) {
			return this.manifestations.find(manifestation => manifestation.id == item.manifestationID);
		},
		searchTickets: function() {
			let user = JSON.parse(window.localStorage.getItem('user'));
			let jwt = user.jwt;
			this.fixParameters();
			axios({
				method: 'POST',
				url: 'rest/tickets/ssf',
				data: this.searchParams,
				headers: { 'Authorization':'Bearer ' + jwt }
			}).then((response) => {
				if(response.data == null)
					alert("No ticket with that parameters!");
				else {
					this.tickets = response.data;
					this.addTitle();
				}
				this.prepareOnClicks();
			}, (error) => {console.log(error);});
		},
		addNewTicket: function(e) {
			e.preventDefault();
			let user = JSON.parse(localStorage.getItem('user'));
			if (!user.jwt) return;
			let jwt = user.jwt;
	
			this.newTicket.deleted = false;
			axios({
				method: 'POST',
				url: 'rest/tickets/',
				data: this.newTicket,
				headers: { 'Authorization':'Bearer ' + jwt },
			}).then((response) => {
				$("#popup").hide();
				if ("error" in response.data) {
					alert(response.data.message);
					return;	
				}
				toast("Ticket successfully added!");
				this.listTickets();
	        });
        },
		editTicket: function(e, ticket) {
			e.preventDefault();
			this.editMode = true;
			this.editTicketData = Vue.util.extend({}, ticket);
			$("#popup").show();
		},
		cancelTicket: function(e, ticket) {
			e.preventDefault();
			let user = JSON.parse(window.localStorage.getItem('user'));
			if (!user)
				return;
			let jwt = user.jwt;
			axios({
				method: 'PUT',
				url: 'rest/tickets/' + ticket.id + '/cancel',
				data: this.newTicket,
				headers: { 'Authorization':'Bearer ' + jwt },
			}).then((response) => {
				if ("error" in response.data) {
					alert(response.data.message);
					return;	
				}
				this.listTickets();
			});
		},
		prepareOnClicks: function() {
			$("#addNewTicket").click(function(e) {
				e.preventDefault();
				$("#popup").show();
			});
			$("#close, #cancel").click(function(e) {
				e.preventDefault();
	 			$("#popup").hide();
			});
		},
		inputFix: function(input) {
			if (isNaN(parseFloat(input)) || input == '') {
			    input = null;
			}
			return input;
		},
		dateFix: function(input) {
			if(input == '') {
				input = null;
			}
			return input;
		},
		fixParameters: function() {

		}
	}
})

function fixDate(manifestations) {
	for (var s of manifestations) {
		s.datetime = new Date(parseInt(s.dateTime));
	}
	return manifestations;
}