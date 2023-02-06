Vue.component("salesmans", {
    data: function () {
            return {
                salesmans: [],
				newSalesman: {},
				admin: null,
				searchParams: {},
				editSalesmanData: {},
				editMode: false    
			}
    },
	template: `
		<div><navbar></navbar>
        <div class="container manageSalesman">
			<div class="search-bar">
				<input class="search-field username" name="search" type="text" placeholder="Username" v-model="searchParams.username">
				<input class="search-field firstName" type="text" placeholder="First name" v-model="searchParams.firstName">
				<input class="search-field lastName" type="text" placeholder="Last name" v-model="searchParams.lastName">
				<button class="search-btn" type="submit" v-on:click="searchSalesmans()">
					<i class="fa fa-search"></i>
				</button>
			</div>
			<div class="sort-filter">
				<h3>Sort by: </h3>
				<select class="search-select" v-model="searchParams.sortType" v-on:change="searchSalesmans()">
					<option value="empty">-----</option>
					<option value="usernameAscending">Username  &#8593;</option>
					<option value="usernameDesceding">Username  &#8595;</option>
					<option value="firstNameAsceding">First name  &#8593</option>
					<option value="firstNameDesceding">First name  &#8595</option>
					<option value="lastNameAsceding">Last name  &#8593</option>
					<option value="lastNameDesceding">Last name  &#8595</option>
				</select>

				<h3> Filter by: </h3>
				<select id="filterSelect" class="search-select" v-model="searchParams.filterType" v-on:change="searchSalesmans()">
					<option value="empty">-----</option>
					<option value="BLOCKED">BLOCKED</option>
					<option value="PRESENT">PRESENT</option>
				</select>
				
				<a href='/' type="button" class="btn" id="addnew">Add new</a>
			</div>
			
			<div id="popup" style="display: none;">
			    <div class="overlay"></div>
			    <div class="content">
			      <header>
			        <div id="close">✖</div>
			      </header>
				  <table>
	                <tr><td><label for="fname">First name</label></td></tr>
					<tr>
						<td v-if="editMode"><input type="text" name="fname" id="fname" value="Mika" v-model="editSalesmanData.firstName" required></td>
						<td v-else><input type="text" name="fname" id="fname" value="Mika" v-model="newSalesman.firstName" required></td>
					</tr>
					<tr><td><label for="lname">Last name</label></td></tr>
	                <tr>
						<td v-if="editMode"><input type="text" name="lname" id="lname" value="Mikic" v-model="editSalesmanData.lastName" required></td>
						<td v-else><input type="text" name="lname" id="lname" value="Mikic" v-model="newSalesman.lastName" required></td>
					</tr>
					<tr><td><label for="uname">Username</label></td></tr>
					<tr>
						<td v-if="editMode"><input type="text" name="uname" id="uname" value="username" v-model="editSalesmanData.username" required></td>
						<td v-else><input type="text" name="uname" id="uname" value="username" v-model="newSalesman.username" required></td>
					</tr>
					<tr><td><label for="password">Password</label></td></tr>
					<tr>
						<td v-if="editMode"><input type="text" name="password" id="password" value="password" v-model="editSalesmanData.password"></td>
						<td v-else><input type="text" name="password" id="password" value="password" v-model="newSalesman.password" required></td>
					</tr>
					<tr><td><label class="legend">Gender: </label></td></tr>
					<tr>
						<td>
							<input v-if="editMode" v-model="editSalesmanData.gender" v-on:click="onChange($event)" value="MALE" id="setMale" type="radio" name="set_gender">
							<input v-else v-model="newSalesman.gender" v-on:click="onChange($event)" value="MALE" id="setMale" type="radio" name="set_gender">
							<label for="setMale">Male</label>
							<input v-if="editMode" v-model="editSalesmanData.gender" v-on:click="onChange($event)" value="FEMALE" id="setFemale" type="radio" name="set_gender">
							<input v-else v-model="newSalesman.gender" v-on:click="onChange($event)" value="FEMALE" id="setFemale" type="radio" name="set_gender">
							<label for="setFemale">Female</label>
						</td>
					</tr>
					<tr><td><label>Date of birth</label></td></tr>
					<tr>
						<td v-if="editMode">
							<vuejs-datepicker v-model="editSalesmanData.birthDate" format="dd.MM.yyyy" required id="dpvue" transfer="true" placeholder="10.07.1856." required></vuejs-datepicker>
						</td>
						<td v-else>
							<vuejs-datepicker v-model="newSalesman.birthDate" format="dd.MM.yyyy" required id="dpvue" transfer="true" placeholder="10.07.1856." required></vuejs-datepicker>
						</td>
					</tr>
            	  </table>
			      <a v-if="editMode" type="button" href='/' v-on:click="tryEdit($event, editSalesmanData)" class="btn" id="create">Edit account</a>
				  <a v-else type="button" href='/' v-on:click="addNew($event)" class="btn" id="create">Create an account</a>
			      <a type="button" href='/' class="btn" id="cancel">Cancel</a>
			    </div>
			</div>
			
            <table id="salesmantable">
                <tr>
                    <th>Username</th>
                    <th>First name</th>
                    <th>Last name</th>
					<th>Gender</th>
					<th>Birth date</th>
					<th></th>
					<th></th>
                </tr>
                <tr v-for="item in salesmans">
                    <td>{{ item.username }}</td>
                    <td>{{ item.firstName }}</td>
                    <td>{{ item.lastName }}</td>
					<td>{{ item.gender }}</td>
					<td>{{ new Date(parseInt(item.birthDate)) | dateFormat("DD.MM.YYYY. HH:mm")}}</td>
					<td><a href='/' v-on:click="editSalesman($event, item)" id="edit">Edit</a></td>
					<td><a href='/' v-on:click="removeSalesman($event, item.username)" id="remove">Remove</a></td>
                </tr>
            </table>
		</div>
		</div>`
		,
    mounted () {	// error on v-bind:disabled="mode=='BROWSE'"
        this.listSalesmans();
		
		$("#addnew").click(function(e) {
			e.preventDefault();
			$("#popup").show();
		});
			
		$("#close, #cancel").click(function(e) {
			e.preventDefault();
 			$("#popup").hide();
		});
    },
	methods : {
		searchSalesmans: function() {
			axios({
				method: 'POST',
				url: 'rest/salesmans/ssf',
				data: this.searchParams
			}).then((response) => {
				if(response.data == null)
					alert("No salesman with that parameters!");
				else {
					this.salesmans = response.data;
				}
			}, (error) => {console.log(error);});
		},
		listSalesmans: function() {
			axios({
				method: 'GET',
				url: 'rest/salesmans/',
			}).then(response => {
				this.salesmans = response.data;
			});
		},
		addNew: function(e) {
			e.preventDefault();
		   	this.addSalesman();
			$("#popup").hide();
		},
		addSalesman: function() {
			let user = JSON.parse(localStorage.getItem('user'));
			if (!user.jwt) return;
			let jwt = user.jwt;
			this.editMode = false;

			let date = new Date(this.newSalesman.birthDate);
			this.newSalesman.birthDate = date.getTime();
			this.newSalesman.deleted = false;
			this.newSalesman.blocked = false;
			axios({
				method: 'POST',
				url: 'rest/salesmans/',
				data: this.newSalesman,
				headers: { 'Authorization':'Bearer ' + jwt },
			}).then((response) => {
				if ("error" in response.data) {
					alert(response.data.message);
					return;	
				}
				toast("Salesman successfully added!");
				this.listSalesmans();
            });
        },
		onChange: function(event) {
			this.newSalesman.gender = event.target.value;
			if(this.newSalesman.gender === 'Male') {
				$("#setMale").checked = true;
				$("#setFemale").checked = false;
			}
			else {
				$("#setMale").checked = false;
				$("#setFemale").checked = true;
			}
				
		},
		removeSalesman: function(e, username) {
			 e.preventDefault();
			 let user = JSON.parse(localStorage.getItem('user'));
			 if (!user.jwt) return;
			 let jwt = user.jwt;
		   	 axios({
				method: 'delete',
				url: 'rest/salesmans/'+ username,
				headers: { 'Authorization':'Bearer ' + jwt },
			 }).then((response) => {
				if ("error" in response.data) {
					alert(response.data.message);
					return;	
				}
				toast("Salesman successfully removed!");
				this.listSalesmans();
             });
		},
		editSalesman: function(e, salesman) {
			e.preventDefault();
			this.editMode = true;
			this.editSalesmanData = Vue.util.extend({}, salesman);
			$("#popup").show();
			$("#fname").val(this.editSalesmanData.firstName);
			$("#lname").val(this.editSalesmanData.lastName);
			$("#uname").val(this.editSalesmanData.username);
			$("#uname").attr('readonly', true);
			$("#password").attr('placeholder', 'Enter new if want to change');
			$("#dpvue").val(new Date(this.editSalesmanData.birthDate));
			this.editSalesmanData.birthDate = new Date(this.editSalesmanData.birthDate);
		},
		tryEdit: function(event, salesman) {
			event.preventDefault();
			let user = JSON.parse(localStorage.getItem('user'));
			if (!user.jwt) return;
			let jwt = user.jwt;
		   	 axios({
				method: 'PUT',
				url: 'rest/salesmans/'+ salesman.username,
				data: salesman,
				headers: { 'Authorization':'Bearer ' + jwt },
			 }).then((response) => {
				$("#popup").hide();
				if ("error" in response.data) {
					alert(response.data.message);
					return;	
				}
				toast("Salesman details successfully changed!");
				this.listSalesmans();
             });
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