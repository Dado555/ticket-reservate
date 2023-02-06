Vue.component("manifestations", {
    data: function () {
            return {
                manifestations: null,
				newManifestation: {location: {}},
				editManifestationData: {location: {}},
				searchParams: {},
				types: null,
				userType: null,
				editMode: false,
				ticketDTO: {},
				hiddenForm: true,
				resize: false
			}
    },
	template: `
		<div><navbar></navbar>
        <div class="container manageManifestations">
			<div class="search-bar">
				<input class="search-field search" name="search" type="text" placeholder="Search..." v-model="searchParams.title">
				<input class="search-field city" type="text" placeholder="City: " v-model="searchParams.cityCountry">

				<input class="search-field priceFrom" type="number" placeholder="From" min="0.00" max="1000000.00" step="1" v-model="searchParams.fromPrice">
				<input class="search-field priceTo" type="number" placeholder="To" min="0.00" max="1000000.00" step="1" v-model="searchParams.toPrice">

				<input class="search-field dateFrom" placeholder="From" format="DD.MM.YYYY." type="text" onfocus="(this.type='date')" v-model="searchParams.fromDate" required/>
				<input class="search-field dateTo" placeholder="To" format="DD.MM.YYYY." type="text" onfocus="(this.type='date')"  v-model="searchParams.toDate" required/>

				<button class="search-btn" type="submit" v-on:click="searchManifestations()">
					<i class="fa fa-search"></i>
				</button>
			</div>
			<div class="sort-filter">
				<h3>Sort by: </h3>
				<select class="search-select" v-model="searchParams.sortType" v-on:change="searchManifestations()">
					<option value="empty">-----</option>
					<option value="titleAscending">Title  &#8593;</option>
					<option value="titleDescending">Title  &#8595;</option>
					<option value="dateTimeAscending">Date  &#8593</option>
					<option value="dateTimeDescending">Date  &#8595</option>
					<option value="priceRegularAscending">Price  &#8593</option>
					<option value="priceRegularDescending">Price  &#8595</option>
					<option value="locationAscending">Location  &#8593</option>
					<option value="locationDescending">Location  &#8595</option>
				</select>

				<h3> Filter by: </h3>
				<select id="filterSelect" class="search-select" v-model="searchParams.filterType" v-on:change="searchManifestations()">
					<option value="empty">-----</option>
					<option value="available">Available seats</option>
					<option value="concert">concert</option>
					<option value="theater">theater</option>
					<option value="movie">movie</option>
					<!--<option v-for="m in this.types" :value="m" >{{ m }}</option>-->
				</select>
				<a href='/' v-if="userType === 'SALESMAN'" v-on:click="newClicked($event)" type="button" class="btn" id="addNewManifestation">Add new</a>
			</div>
			
			<div id="popup" class="popup" style="display: none;">
			    <div class="overlay"></div>
			    <div class="content" style="display: table-cell;">
			      <header>
			        <div id="close">✖</div>
			      </header>
				  <table>
					  <tr><td><label for="poster">Poster</label></td></tr>
					  <tr><td>
					  	<input type="file" accept="image/*," v-on:change="updatePoster($event)"">
					  </td></tr>
	                <tr><td><label for="title">Title</label></td></tr>
					<tr>
						<td v-if="editMode"><input type="text" name="title" id="title" value="" v-model="editManifestationData.title" required></td>
						<td v-else><input type="text" name="title" id="title" value="" v-model="newManifestation.title" required></td>
					</tr>
					<tr><td><label for="type">Type</label></td></tr>
	                <tr>
						<td v-if="editMode"><input type="text" name="type" id="type" value="" v-model="editManifestationData.type" required></td>
						<td v-else><input type="text" name="type" id="type" value="" v-model="newManifestation.type" required></td>
					</tr>
					<tr v-if="editMode"><td><label for="seats">Seats</label></td></tr>
	                <tr v-if="editMode">
						<td v-if="editMode"><input type="number" name="seats" id="seats" value="" v-model="editManifestationData.seats" required></td>
					</tr>
					<tr><td><label for="dateTime">Date and time</label></td></tr>
					<tr>
						<td v-if="editMode">
							<vuejs-datepicker v-model="editManifestationData.dateTime" format="dd.MM.yyyy" required id="dpvue" transfer="true" placeholder="10.07.1856." required></vuejs-datepicker>
						</td>
						<td v-else>
							<vuejs-datepicker v-model="newManifestation.dateTime" format="dd.MM.yyyy" required id="dpvue" transfer="true" placeholder="10.07.1856." required></vuejs-datepicker>
						</td>
					</tr>
					<tr><td><label for="location">Location</label></td></tr>
					<tr>
						<td v-if="editMode"><input type="text" name="location" id="location" value="" v-model="editManifestationData.location.address" required readonly></td>
						<td v-else><input type="text" name="location" id="location" value="" v-model="newManifestation.location.address" required readonly></td>
					</tr>
					<tr v-if="editMode"><td><label for="status">Status</label></td></tr>
	                <tr v-if="editMode">
						<td>
							<select v-model="editManifestationData.status">
								<option value="ACTIVE">ACTIVE</option>
								<option value="INACTIVE">INACTIVE</option>
							</select>
						</td>
					</tr>
					
					<tr><td><label for="price">Regular price</label></td></tr>
	                <tr>
						<td v-if="editMode"><input type="number" name="price" id="price" value="" v-model="editManifestationData.priceRegular" required></td>
						<td v-else><input type="number" name="seats" id="seats" value="" v-model="newManifestation.priceRegular" required></td>
					</tr>
					
	        	  </table>
			      <a v-if="editMode" type="button" href='/' v-on:click="tryEdit($event, editManifestationData)" class="btn" id="create">Edit</a>
				  <a v-else type="button" href='/' v-on:click="addNewManifestation($event)" class="btn" id="create">Create</a>
			      <a type="button" href='/' class="btn" id="cancel">Cancel</a>
			  </div>
			
				<div class="locationForm" style="display: table-cell;">
					<olmap :resize="resize"></olmap>
					<div class="contact-form">
						<h1 class="title">Confirm address</h1>
						<h2 class="subtitle">Find coordinates on map. Other informations enter down below.</h2>
						<form action="">
							<input type="text" name="street" placeholder="Street name (Draze Mihajlovica 16)" />
							<input type="text" name="city" placeholder="City and country (Novi Sad, Srbija)" />
							<input type="text" name="zip" placeholder="Zip code (21000)"/>
							<input type="text" name="longitude" placeholder="Longitude" readonly />
							<input type="text" name="latitude" placeholder="Latitude" readonly />
							<button v-on:click="setLocation($event)" class="btn-set-location">Set location</button>
						</form>
					</div>
				</div>
			</div>
			
			<!--<div id="mapaManifestation" class="map"></div>-->
			
			<div id="ticketPopup" class="popup" style="display: none;">
				<div class="overlay"></div>
				<div class="content">
					<table>
						<tr>
						<td><label for="tickets">Choose a ticket:</label></td>
			            <td>
							<select v-model="ticketDTO.type" name="ticketType" id="ticketType">
				                <option value="VIP">VIP</option>
				                <option value="REGULAR">REGULAR</option>
				                <option value="FAN_PIT">FAN PIT</option>
				            </select>
						</td>
						</tr><tr><td><input v-model="ticketDTO.count" id="count" type="number" value="1"></td></tr>
					</table>
					<a type="button" href='/' v-on:click="addTicket($event)" class="btn" id="create">Add Tickets</a>
				    <a type="button" href='/' class="btn" id="cancel">Cancel</a>
				</div>
			</div>
			
			<table id="manifestationtable">
				<tr>
					<th>Poster</th>
                    <th>Title</th>
                    <th>Type</th>
                    <th>Seats</th>
					<th>Price</th>
					<th>Date and time</th>
					<th>Location</th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
                </tr>
				<tr v-for="manifestation in manifestations">
					<td><img v-bind:src="'img/' + manifestation.posterPath" height="60" width="60"></td>
                    <td>{{ manifestation.title }}</td>
                    <td>{{ manifestation.type }}</td>
                    <td>{{ manifestation.seats }}</td>
					<td>{{ manifestation.priceRegular }}</td>
					<td>{{ new Date(parseInt(manifestation.dateTime)) | dateFormat("DD.MM.YYYY. HH:mm") }}</td>
					<td width="200px">{{ manifestation.location.address }}</td>
					<td><a href='/' v-on:click="showTicketPopup($event, manifestation.id)" id="addTicket">Tickets &#x271A;</a></td>
					<td><a href='/' v-on:click="editManifestation($event, manifestation)" id="edit">Edit</a></td>
					<td v-if="manifestation.status === 'INACTIVE'">
						<a v-if="userType === 'ADMINISTRATOR'" href='/' v-on:click="approveManifestation($event, manifestation.id)" id="approve">Approve</a>
					</td>
					<td v-else></td>
                </tr>
            </table>
		</div>
		</div>`
		,
    mounted () {	
		let user = JSON.parse(localStorage.getItem('user'));
		if (!user.jwt) return;
		this.userType = user.userType;
		
        this.listManifestations(user);
		this.prepareOnClicks();
    },
	methods : {
		setLocation: function(event){
			event.preventDefault();
			if(this.editMode) {
				this.editManifestationData.location.longitude = $('input[name=longitude]').val();
				this.editManifestationData.location.latitude = $('input[name=latitude]').val();
				this.editManifestationData.location.address = $('input[name=street]').val() + ", " + $('input[name=city]').val() + ', ' + $('input[name=zip]').val();
			} else {
				this.newManifestation.location.longitude = $('input[name=longitude]').val();
				this.newManifestation.location.latitude = $('input[name=latitude]').val();
				this.newManifestation.location.address = $('input[name=street]').val() + ", " + $('input[name=city]').val() + ', ' + $('input[name=zip]').val();
			}
			$('input[name=location]:text').val(this.newManifestation.location.address);
		},
		listManifestations: function(user) {
			let address = 'rest/manifestations/';
			if(user.userType === 'SALESMAN') {
				address += 'salesman/' + user.username;
			}
			axios({
					method: 'GET',
					url: address,
					headers: { 'Authorization':'Bearer ' + user.jwt },
				}).then(response => {
					this.manifestations = fixDate(response.data); 
					this.types = getTypes(response.data);
			});
		},
		updatePoster: function(event) {
			var files = event.target.files || event.dataTransfer.files
			if (!files.length)
				return
				
			let poster = files[0].name;
			this.newManifestation.posterPath = poster;
			this.editManifestation.posterPath = poster;
		},
		approveManifestation: function(e, id) {
			e.preventDefault();
			let user = JSON.parse(localStorage.getItem('user'));
			if (!user.jwt) return;
			let jwt = user.jwt;
			axios({
				method: 'PUT',
				url: 'rest/manifestations/'+ id + '/approve',
				headers: { 'Authorization':'Bearer ' + jwt },
			}).then((response) => {
				if ("error" in response.data) {
					alert(response.data.message);
					return;	
				}
				toast("Manifestation successfully approved!");
				this.listManifestations();
			});
		},
		addNewManifestation: function(e) {
			e.preventDefault();
			let user = JSON.parse(localStorage.getItem('user'));
			if (!user.jwt) return;
			let jwt = user.jwt;
	
			let date = new Date(this.newManifestation.dateTime);
			this.newManifestation.dateTime = date.getTime();
			this.newManifestation.deleted = false;
			this.newManifestation.seats = 0;
			this.newManifestation.status = "INACTIVE";
			axios({
				method: 'POST',
				url: 'rest/manifestations/',
				data: this.newManifestation,
				headers: { 'Authorization':'Bearer ' + jwt },
			}).then((response) => {
				$("#popup").hide();
	            if ("error" in response.data) {
					alert(response.data.message);
					return;	
				}
				toast("Manifestation successfully added!");
				this.listManifestations(user);
	        });
        },
		editManifestation: function(e, manifestation) {
			e.preventDefault();
			this.editMode = true;
			this.resize = true;
			this.editManifestationData = Vue.util.extend({}, manifestation);
			$('#popup').css("display", "table");
			$("#popup").show();
			$(".content").css("transform", "translate(-10%, -37%)");
		},
		tryEdit: function(event, manifestation) {
			event.preventDefault();
			let user = JSON.parse(localStorage.getItem('user'));
			if (!user.jwt) return;
			let jwt = user.jwt;
			axios({
				method: 'PUT',
				url: 'rest/manifestations/'+ manifestation.id,
				data: manifestation,	
				headers: { 'Authorization':'Bearer ' + jwt },
			}).then((response) => {
				$("#popup").hide();
				if ("error" in response.data) {
					alert(response.data.message);
					return;	
				}
				toast("Manifestation details changed!");
				this.listSalesmans();
			});
		},
		showTicketPopup: function(event, id) {
			event.preventDefault();
			this.ticketDTO.manifestationID = id;
			$("#ticketPopup").show();
		},
		addTicket: function(event) {
			event.preventDefault();
			let user = JSON.parse(localStorage.getItem('user'));
			if (!user.jwt) return;
			let jwt = user.jwt;
			axios({
				method: 'POST',
				url: 'rest/tickets/',
				data: this.ticketDTO,
				headers: { 'Authorization':'Bearer ' + jwt },
			 }).then((response) => {
				 $("#ticketPopup").hide();
			     if(response.data == true) {
					toast("Tickets added!");	
					this.listManifestations(user); 
					return;
				 }
				 if ("error" in response.data) {
					alert(response.data.message);
					return;	
				}
	            
             });
		},
		newClicked: function(e) {
			e.preventDefault();
			$("#popup").show();
			$(".content").css("transform", "translate(-37%, -37%)");
			this.resize = true;
			this.editMode = false;
			// this.mapInit();
		},
		prepareOnClicks: function() {			
			$("#close, #cancel").click(function(e) {
				e.preventDefault();
	 			$("#popup").hide();
				$("#ticketPopup").hide();
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
			this.searchManifestations.currentLongitude = 0; 
		  	this.searchManifestations.currentLatitude = 0;
			this.searchManifestations.availableTickets = null;
			this.searchParams.toDate = this.dateFix(this.searchParams.toDate);
			this.searchParams.fromDate = this.dateFix(this.searchParams.fromDate);
			this.searchParams.fromPrice = this.inputFix(this.searchParams.fromPrice);
			this.searchParams.toPrice = this.inputFix(this.searchParams.toPrice);
		},
		searchManifestations: function() {
			this.fixParameters();
			let user = JSON.parse(localStorage.getItem('user'));
			if (!user.jwt) return;
			let jwt = user.jwt;
			axios({
					method: 'POST',
					url: 'rest/manifestations/ssf/-1',
					data: this.searchParams,
					headers: { 'Authorization':'Bearer ' + jwt }
				}).then((response) => {
					if(response.data == null)
						alert("No customers with that parameters!");
					else {
						this.manifestations = fixDate(response.data);
					}
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

function fixDate(manifestations) {
	for (var s of manifestations) {
		s.datetime = new Date(parseInt(s.dateTime));
	}
	return manifestations;
}

function getTypes(manifestations) {
	var types = new Map();
	for(var m of manifestations){
		types.set(m.type.toLowerCase(), m.type);
	}
	return Array.from(types.keys());
}