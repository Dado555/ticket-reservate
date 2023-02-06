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

function getAvailable(manifestations) {
	var manifs = [];
	for(var m of manifestations) {
		if(parseInt(m.seats) > 0) {
			manifs.push(m);
		}
	}
	return manifs;
}

Vue.component("home", {
    data: function () {
            return {
                manifestations: [],
				types: null,
				available: null,
				location:null, // {{ location.coords.latitude }}, {{ location.coords.longitude}}
    			locationAllowed: false,
				searchManif: {},
				user: null,
				page: 1,
				pages: [1],
				ssf: false
            }
    },
	template: `
		<div>
			<navbar></navbar>
			<div class="container">
				<div class="search-bar">
					<input class="search-field search" name="search" type="text" placeholder="Search..." v-model="searchManif.title">
					<input class="search-field city" type="text" placeholder="City: " v-model="searchManif.cityCountry">

					<input class="search-field priceFrom" type="number" placeholder="From" min="0.00" max="1000000.00" step="1" v-model="searchManif.fromPrice">
					<input class="search-field priceTo" type="number" placeholder="To" min="0.00" max="1000000.00" step="1" v-model="searchManif.toPrice">

					<input class="search-field dateFrom" placeholder="From" format="DD.MM.YYYY." type="text" onfocus="(this.type='date')" v-model="searchManif.fromDate" required/>
					<input class="search-field dateTo" placeholder="To" format="DD.MM.YYYY." type="text" onfocus="(this.type='date')"  v-model="searchManif.toDate" required/>

					<button class="search-btn" type="button" v-on:click="searchManifestations()">
						<i class="fa fa-search"></i>
					</button>
				</div>
				<div class="sort-filter">
					<h3>Sort by: </h3>
					<select class="search-select" v-model="searchManif.sortType" v-on:change="searchManifestations()">
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
					<select id="filterSelect" class="search-select" v-model="searchManif.filterType" v-on:change="searchManifestations()">
						<option value="empty">-----</option>
						<option value="concert">concert</option>
						<option value="theater">theater</option>
						<option value="movie">movie</option>
						<!--<option v-for="m in this.types" :value="m" >{{ m }}</option>-->
					</select>
				</div>
				<div class="row" v-for="n in Math.ceil(manifestations.length/4)">
					<div class="col-4" v-for="m in manifestations.slice((n-1)*4, n*4)">
						<a v-bind:href="'#/manifestations/' + m.id"><img v-bind:src="'img/' +m.posterPath" width="200" height="240"></a>
						<table>
							<tr>
								<td><h4>{{m.title}}</h4></td>
								<td id="priceHome"><h4>{{m.priceRegular}}rsd</h4></td>
							</tr>
							<tr>
								<td><h4>{{m.type}}</h4></td>
								<td></td>
							</tr>
							<tr>
								<td><h4>{{m.datetime | dateFormat("DD.MM.YYYY. HH:mm")}}</h4></td>
								<td></td>
							</tr>
							<tr>
								<td>{{m.location.address}}</td>
								<td></td>
							</tr>
						</table>
					</div>
				</div>
				
				<nav aria-label="Page navigation example">
					<ul class="pagination">
						<li class="page-item">
							<button type="button" class="page-link" v-if="page != 1" @click="ssf ? getPageSsf(page-1) : getPage(page-1)"> Previous </button>
						</li>
						<li class="page-item">
							<button type="button" class="page-link" v-for="pageNumber in pages.slice(page-1, page+5)" @click="ssf ? getPageSsf(pageNumber) : getPage(pageNumber)"> {{pageNumber}} </button>
						</li>
						<li class="page-item">
							<button type="button" v-if="page < pages.length" class="page-link" @click="ssf ? getPageSsf(page+1) : getPage(page+1)"> Next </button>
						</li>
					</ul>
				</nav>	
			</div>
		</div>`,
	created() {
	    //is geolocation supported
	    if(!("geolocation" in navigator)) {
		  alert("Geolocation is not available.");
	      return;
	    }
	
	    this.locationAllowed = true;
	    // get position
	    navigator.geolocation.getCurrentPosition(pos => {
	      this.locationAllowed = false;
	      this.location = pos;
	      this.searchManif.currentLongitude = pos.coords.longitude; 
		  this.searchManif.currentLatitude = pos.coords.latitude;

	    }, err => {
	      this.locationAllowed = false;
	    })
  	},
    mounted () {
		this.user = JSON.parse(localStorage.getItem('user'));
		this.searchManif.sortType = "dateTimeAscending";
		this.searchManifestations();
		/*
		axios({
			method: 'GET',
			url: 'rest/manifestations/page/1' 
		}).then(response => {
				if(response.data == null)
					return;
				this.pages = [1];
				this.manifestations = fixDate(response.data); 
				if(this.manifestations.length == 8) {
					this.pages.push(2);
				}
				this.types = getTypes(response.data); 
				// this.available = getAvailable(response.data);
			})
		*/
    },
	filters: {
    	dateFormat: function (value, format) {
    		var parsed = moment(value);
    		return parsed.format(format);
    	},
		timeFormat: function (value, format) {
			var parsed = moment(value);
			return parsed.format(format);
		}
   	},
	methods: {
		getPage: function(number) {
			this.page = number;
			axios({
			method: 'GET',
			url: 'rest/manifestations/page/'+number 
			}).then(response => {
				if(response.data == null)
					return;
				this.manifestations = fixDate(response.data); 
				if(this.manifestations.length == 8) {
					if(this.pages.indexOf(number+1) == -1)
						this.pages.push(number+1);
				}
				this.types = getTypes(response.data); 
			})
		},
		getPageSsf: function(pageNumber) {
			this.page = pageNumber;
			this.ssf = true;
			this.searchManif.toDate = this.dateFix(this.searchManif.toDate);
			this.searchManif.fromDate = this.dateFix(this.searchManif.fromDate);
			this.searchManif.fromPrice = this.inputFix(this.searchManif.fromPrice);
			this.searchManif.toPrice = this.inputFix(this.searchManif.toPrice);
			this.searchManif.availableTickets = null;
			axios({
				method: 'post',
				url: 'rest/manifestations/ssf/'+pageNumber,
				data: this.searchManif
			}).then(response => {
				if(response.data == null) {
					alert("No manifestations with that parameters!");
					return;
				}
				this.manifestations = fixDate(response.data); 
				if(this.manifestations.length == 8) {
					if(this.pages.indexOf(pageNumber+1) == -1)
						this.pages.push(pageNumber+1);
				}
				this.types = getTypes(response.data); 
			}, (error) => {console.log(error);});
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
		searchManifestations: function() {
			this.ssf = true;
			this.searchManif.toDate = this.dateFix(this.searchManif.toDate);
			this.searchManif.fromDate = this.dateFix(this.searchManif.fromDate);
			this.searchManif.fromPrice = this.inputFix(this.searchManif.fromPrice);
			this.searchManif.toPrice = this.inputFix(this.searchManif.toPrice);
			this.searchManif.availableTickets = null;
			axios({
				method: 'post',
				url: 'rest/manifestations/ssf/1',
				data: this.searchManif
			}).then((response) => {
				if(response.data == null) {
					alert("No manifestations with that parameters!");
					return;
				}
				this.pages = [1];
				this.manifestations = fixDate(response.data);
				if(this.manifestations.length == 8) {
					this.pages.push(2);
				}
			}, (error) => {console.log(error);});
		}
	},
	components: {
      	vuejsDatepicker
    }
})