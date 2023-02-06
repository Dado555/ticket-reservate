Vue.component("login", {
    data: function () {
            return {
				username: null,
				password: null,
				rusername: null,
				rpassword: null,
				firstName: null,
				lastName: null,
				gender: null,
				birthDate: null,
				user: null
            }
    },
	template: `
		<div>
			<navbar></navbar>
			<div class="login-page">
				<div class="container">
					<div class="row">
						<div class="col-2">
							<img class="logo-img" src="img/background.png" width="100%">
						</div>
						<div class="col-2">
							<div class="form-container">
								<div class="form-btn">
									<span v-on:click="selectLogin()">Login</span>
									<span v-on:click="selectRegistration()">Register</span>
									<hr id="indicator">
								</div>
								<form id="loginForm" action="">
									<input v-model="username" type="text" id="username" placeholder="Username" class="standardInput"> 
									<input v-model="password" type="password" id="password" placeholder="Password" class="standardInput"> 
									<button type="reset" v-on:click="login()" class="btn">Login</button>
									<a href="#">Forgot password?</a>
								</form>
								<form id="registrationForm" action="">
									<input v-model="rusername" type="text" id="rusername" placeholder="Username" required class="standardInput"> 
									<input v-model="rpassword" type="password" id="rpassword" placeholder="Password" required class="standardInput"> 
									<input v-model="firstName" type="text" id="firstName" placeholder="First Name" required class="standardInput">
									<input v-model="lastName" type="text" id="lastName" placeholder="Last Name" required class="standardInput">
									<span class="legend">Gender: </span>
									<input v-model="gender" @change="onChange($event)" value="MALE" id="setMale" type="radio" name="set_gender">
									<label for="setMale">Male</label>
									<input v-model="gender" @change="onChange($event)" value="FEMALE" id="setFemale" type="radio" name="set_gender">
									<label for="setFemale">Female</label>
									<vuejs-datepicker v-model="birthDate" format="dd.MM.yyyy" required id="dpvue" 
										transfer="true" placeholder="Date of birth"></vuejs-datepicker>
									<button type="button" v-on:click="registration()" class="btn">Register</button>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>`,
	methods: {
		login: function() {
			axios({
				method: 'POST',
				url: 'rest/login',
				data: {username: this.username, password: this.password},
			}).then((response) => {
				if ("error" in response.data) {
					alert(response.data.message)
					return
				}
				this.user = response.data;
				window.localStorage.setItem('user', JSON.stringify(response.data));
				window.location.href = '/#/';
				toast("Welcome " + this.username + "!");
			});
		},
		registration: function() {
			let date = new Date(this.birthDate);
			axios({
				method: 'POST',
				url: 'rest/registration',
				data: {username: this.rusername, password: this.rpassword, firstName: this.firstName, lastName: this.lastName, gender: this.gender, 
					   birthDate: date.getTime(), ticketIDs: [], points: 0, deleted: false, blocked: false, suspicious: false}
			}).then((response) => {
				if ("error" in response.data) {
					alert(response.data.message);
					document.getElementsByName("rusername").value = "";
					return;
				}
				document.getElementById("registrationForm").reset();
				window.location.href = '/#/';
				toast("You are successfully registered!");
			});
        },
		selectRegistration: function() {
			var loginForm = document.getElementById("loginForm");
			var regForm = document.getElementById("registrationForm");
			var indicator = document.getElementById("indicator");
			regForm.style.visibility = "visible";
			loginForm.style.visibility = "hidden";
			regForm.style.transform = "translateX(0px)";
			loginForm.style.transform = "translateX(0px)";
			indicator.style.transform = "translateX(100px)";
		},
		selectLogin: function() {
			var loginForm = document.getElementById("loginForm");
			var regForm = document.getElementById("registrationForm");
			var indicator = document.getElementById("indicator");
			regForm.style.visibility = "hidden";
			loginForm.style.visibility = "visible";
			regForm.style.transform = "translateX(300px)";
			loginForm.style.transform = "translateX(300px)";
			indicator.style.transform = "translateX(0px)";
		},
		onChange: function(event) {
			this.gender = event.target.value;
		}
	},
	components: {
      	vuejsDatepicker
    }
})