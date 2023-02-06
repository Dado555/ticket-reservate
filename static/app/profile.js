Vue.component("profile", {
    data: function () {
            return {
                user: {},
				username: null
			}
    },
    template: `
        <div><navbar></navbar>
		<div class="containerProfile">
            <div class="leftbox">
                <nav>
                    <a v-on:click="tabs(0)" class="tab">
                        <i class="fa fa-user"></i>
                    </a>
                    <a v-on:click="tabs(1)" class="tab">
                        <i class="fa fa-tasks"></i>
                    </a>
                    <a v-on:click="tabs(2)" class="tab">
                        <i class="fa fa-cog"></i>
                    </a>
                </nav>
            </div>
            <div class="rightbox">
                <div v-if="user" class="profile tabShow">
                    <h1>Personal info</h1> 
                    <h2>username</h2>
                    <input type="text" class="input" v-model="user.username" readonly>
					<h2>First name</h2>
                    <input type="text" class="input" v-model="user.firstName" required>
					<h2>Last name</h2>
                    <input type="text" class="input" v-model="user.lastName" required>
                    <h2>Birthday</h2>
					<div id="birthdaypicker">
						<vuejs-datepicker v-model="user.birthDate" format="dd.MM.yyyy" id="dpvue" transfer="true" placeholder="10.07.1856." required></vuejs-datepicker>
                    </div>
					<h2>Gender</h2>
                    <!--<input type="text" class="input" v-model="user.gender" required>-->
					<input v-model="user.gender" v-on:click="onChange($event)" value="MALE" id="setMale" type="radio" name="set_gender">
					<label for="setMale">Male</label>
					<input v-model="user.gender" v-on:click="onChange($event)" value="FEMALE" id="setFemale" type="radio" name="set_gender">
					<label for="setFemale">Female</label>
                    <h2>Password</h2>
                    <input type="password" class="input" v-model="user.password" placeholder="To change password input new here..">
					<p></p>
                    <button class="btn" v-on:click="updateUser()">Update</button>
                </div>
                <div class="privacy tabShow">
                    <h1>Privacy settings</h1> 
                    <h2>Points: {{user.points}}</h2>
                    <p></p>
                    <h2>User type: {{user.customerType.name}}</h2>
                    <p></p>
                    <h2>Discount: {{user.customerType.discount}}</h2>
                    <p></p>
                    <h2>Required points for user upgrade: {{user.customerType.requiredPoints}}</h2>
                </div>
                <div class="settings tabShow">
                    <h1>Account settings</h1> 
                    <h2>Sync WatchList</h2>
                    <p></p>
                    <h2>Hold Subscription</h2>
                    <p></p>
                    <h2>Cancel Subscription</h2>
                    <p></p>
                    <h2>Your Devices</h2>
                    <p></p>
                    <h2>Referrals</h2>
                    <p></p>
                    <button class="btn">Update</button>
                </div>
            </div>
        </div>
        </div>`
		,
    mounted () {
        this.username = (JSON.parse(localStorage.getItem('user'))).username;
		this.getUser();
        this.tabs(1);
		this.changeActive();
    },
	methods : {
		getUser: function() {
			let userr = JSON.parse(localStorage.getItem('user'));
			if (!userr.jwt) return;
			let jwt = userr.jwt;
			axios({
				method: 'get',
				url: 'rest/'+ this.username,
				headers: { 'Authorization':'Bearer ' + jwt },
			 }).then((response) => {
				if ("error" in response.data) {
					alert(response.data.message);
					return;	
				}
				this.user = response.data;
				if (this.user.userType === "CUSTOMER") {
					axios({
						method: 'get',
						url: 'rest/customers/'+ this.user.username,
						headers: { 'Authorization':'Bearer ' + jwt },
					 }).then((response) => {
						if ("error" in response.data) {
							alert(response.data.message);
							return;	
						}
						this.user = response.data;
					});
             	}
			});
		},
		changeActive: function() {
			$(".tab").click(function() {
				$(this).addClass("active").siblings().removeClass("active");
     		})
		},
		tabs: function(panelIndex) {
    		const tab = document.querySelectorAll(".tabShow");
    		tab.forEach(function(node) {
        		node.style.display = "none";
    		});
    		tab[panelIndex].style.display = "block";
		},
		updateUser: function() {
			let userr = JSON.parse(localStorage.getItem('user'));
			if (!userr.jwt) return;
			if(this.user.password == null)
				this.user.password = "";
			this.user.birthDate = new Date(this.user.birthDate).getTime();
			let jwt = userr.jwt;
			 axios({
				method: 'POST',
				url: 'rest/customers/'+ this.user.username,
				data: this.user,
				headers: { 'Authorization':'Bearer ' + jwt },
			 }).then((response) => {
				if ("error" in response.data) {
					alert(response.data.message);
					return;	
				}
				toast("Customer details successfully changed!");
             });
		},
		onChange: function(event) {
			this.user.gender = event.target.value;
			if(this.user.gender === 'Male') {
				$("#setMale").checked = true;
				$("#setFemale").checked = false;
			}
			else {
				$("#setMale").checked = false;
				$("#setFemale").checked = true;
			}
		}
	},
	components: {
      	vuejsDatepicker
    }
})