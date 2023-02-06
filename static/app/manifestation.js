// TODO: admin vidi sve komentare (prihvacene i odbijene) - u sklopu manifestacije?
// TODO: kupac moze da vidi komentare koje je prodavac odobrio
//		 moze da ostavi komentar ako je posjetio manifestaciju (prosla manifestacija i rezervisana karta)
//		 	moze i da ocjeni manifestaciju (prosla manifestacija i rezervisana karta)
 
Vue.component("manifestation", {
    data: function () {
            return {
                manifestation: null,
                count: 1,
                ticketType: null,
				user: null,
				comments: [],
				canComment: false,
				sendComment: {},
				manifestationRating: null
			}
    },
	template: `
		<div><navbar></navbar>
        <div class="container containerManifestation">
            <div class="row">
                <div v-if="manifestation" class="col-2">
                    <img v-bind:src="'img/' + manifestation.posterPath" width="100%">
                </div>
                <div v-if="manifestation" class="col-2">
					<table> 
						<tr>
							<td id="ulijevo">
			                    <p id="home-manifestation">Home / Manifestation</p>
			                    <h2>{{ manifestation.title }}</h2>
			                    <h4>{{ manifestation.priceRegular }}$</h4>
			                    <label v-if="user != null && user.userType === 'CUSTOMER'" for="tickets">Choose a ticket:</label><br>
			                    <select v-if="user != null && user.userType === 'CUSTOMER'" v-model="ticketType" name="ticketType" id="ticketType">
			                        <option value="VIP">VIP</option>
			                        <option value="REGULAR">REGULAR</option>
			                        <option value="FAN_PIT">FAN PIT</option>
			                    </select>
								<br><br>
			                    <input v-if="user != null && user.userType === 'CUSTOMER'" v-model="count" id="count" type="number" value="1">
								<button v-if="user != null && user.userType === 'CUSTOMER' && isActive(manifestation.dateTime) && manifestation.status == 'ACTIVE'" type="button" v-on:click="addToCart()" class="btn">Add To Cart</button>
								<button v-else class="btn" style="visibility: hidden">Add To Cart</button>
							</td>
							<td id="udesno">
			                    <h3><i class="fa fa-indent" aria-hidden="true"></i> Manifestation Details</h3><br>
			                    <p>Type: {{manifestation.type}}</p>
								<p>Available seats: {{manifestation.seats}}</p>
								<p>Date and time: {{new Date(parseInt(manifestation.dateTime)) | dateFormat("DD.MM.YYYY. HH:mm")}}</p>
								<p>Status: {{manifestation.status}}</p>
								<p>Adress: {{manifestation.location.address}}</p>
								<p v-if="manifestationRating">Rating: {{manifestationRating}}</p>
								<h3>Remaining tickets:</h3>
								<p>VIP: {{manifestation.ticketsCount.VIP}} </p>
								<p>Regular: {{manifestation.ticketsCount.REGULAR}} </p>
								<p>FAN PIT: {{manifestation.ticketsCount.FAN_PIT}} </p>

							</td>
						</tr>
					</table>
                </div>
            </div>

			<!--<div id="mapa" class="map"></div>-->
			<olmap v-if="manifestation" :longitude="manifestation.location.longitude" :latitude="manifestation.location.latitude"></olmap>
			
			</br><h2>Comments: </h2><br/>
            <div class="containerComment">
                 <ul v-if="comments" class="simple-nested">
	                <li v-for="comment in comments">
	                    <div class="comment" v-on:click="onCommentClicked($event)">
	                        <p><a href="">{{comment.username}}</a></p>
	                        <em>Date</em>
	                        <p>Comment: {{comment.text}}</p>
							<p>Rating: {{comment.rating}} </p>
							<div v-if="user != null">
								<div v-if="user.userType == 'ADMINISTRATOR' || user.userType === 'SALESMAN'">
									<table>
										<tr>
											<td>
												<p> {{comment.status}} </p>
											</td>
											<td id="approve-comment" v-if="user.userType === 'SALESMAN' && comment.status == 'INITIAL'">
												<a href="/" v-on:click="changeStatus($event,'approve',comment.id)" style="margin-left: 40em;"> Approve </a> 
											</td>
											<td id="refuse-comment" v-if="user.userType === 'SALESMAN' && comment.status == 'INITIAL'">
												<a href="/" v-on:click="changeStatus($event,'refuse',comment.id)" style="margin-left: 3em;"> Refuse </a> 
											</td>
										</tr>
									</table>
								</div>
							</div>
	                        <p><a href="">Reply</a></p>
	                    </div>
	                </li>
	            </ul>
            </div>
			
			<div v-if="canComment" class="leave-comment">
			  <h2>Leave Us a Comment</h2>
			  <form>
			   <textarea v-model="sendComment.text" id='commenttext' placeholder='Add Your Comment'></textarea>
				<table>
					<tr>
						<td>
							<label>Rating: </label>
						</td>
						<td>
							<div class="rate">
			    				<input v-model="sendComment.rating" v-on:click="setRating($event)" type="checkbox" id="star5" name="rateStar" value="5" />
							    <label for="star5" title="text">5 stars</label>
							    <input v-model="sendComment.rating" v-on:click="setRating($event)" type="checkbox" id="star4" name="rateStar" value="4" />
							    <label for="star4" title="text">4 stars</label>
							    <input v-model="sendComment.rating" v-on:click="setRating($event)" type="checkbox" id="star3" name="rateStar" value="3" />
							    <label for="star3" title="text">3 stars</label>
							    <input v-model="sendComment.rating" v-on:click="setRating($event)" type="checkbox" id="star2" name="rateStar" value="2" />
							    <label for="star2" title="text">2 stars</label>
							    <input v-model="sendComment.rating" v-on:click="setRating($event)" type="checkbox" id="star1" name="rateStar" value="1" />
							    <label for="star1" title="text">1 star</label>
							</div>
						</td>
					</tr>
				</table>
			    <div class="btn-comment">
			      <input type="submit" value='Comment' v-on:click="addComment($event)">
			      <button id='clear-comment' href='#'>Cancel</button>
			    </div>
			  </form>
			</div>
		</div>
		</div>`
		,
    mounted () {
	/*
		this.$nextTick(function() {
			this.mapInit();
		});
		*/
		axios
            .get('rest/manifestations/' + this.$route.params.id)
            .then(response => {this.manifestation = response.data;	this.getUser(); this.getRating();});
    },
    methods: {
		getRating: function() {
			axios({
				method: 'get',
				url: 'rest/manifestations/rating/' + this.$route.params.id
			 }).then((response) => {
                 if (response.data > 0) {
					this.manifestationRating = response.data;
				 }	
             });
		},
		isActive: function(datetime) {
			let currentDate = new Date();
			let checkDate = new Date(datetime);
			console.log(currentDate);
			console.log(checkDate);
			if(checkDate > currentDate)
				return true;
			return false;
		},
		changeStatus: function(event, status, commentid) {
			event.preventDefault();
			axios({
				method: 'put',
				url: 'rest/comments/change-status?commentid='+commentid+'&status='+status
			 }).then((response) => {
                 if (response.data === true) {
                    taost("Comment status updated!");
					this.getComments("/salesman/"+this.$route.params.id);
				 }	
                 else
                    alert("User not signed in!");
             });
		},
		onCommentClicked: function(event) {
			if($(event.currentTarget).height() > 100) {
				$(event.currentTarget).animate({height:'50px'}, 500);
			} else {
				$(event.currentTarget).animate({height:'100%'}, 1000);
			}
		},
		setRating: function(event) {
			event.preventDefault();
			this.sendComment.rating = event.currentTarget.value;
			// let targetId = event.currentTarget.id;
			let valTarget = event.currentTarget.value;
			// $(targetId).prop("checked", "true");
			//alert("Change rating");
			
			$('input:checkbox').each(function() {
				var $this = $(this);
				if(parseInt($this.val()) <= (parseInt(valTarget))) {
					$this.prop("checked", true);
					$('label[for='+$this.attr("id")+']').css("color", "#ffc700");
				} else {
					$this.prop("checked", false);
					$('label[for='+$this.attr("id")+']').css("color", "#cccccc");
				}
			});
		},
		addComment: function(event) {
			event.preventDefault();
			if($("#commenttext").val() && this.sendComment.rating) {
				// submit comment
				this.sendComment.manifestationID = this.manifestation.id;
				this.sendComment.username = this.user.username;
				axios({
					method: 'post',
					url: 'rest/comments/',
					data: this.sendComment
				}).then((response) => {
	                if (response.data)
	                    toast("Comment is created. Salesman must approve it.");
	                else
	                    toast("User not signed in!");
	            });
			} else {
				alert("Enter your comment and rating and then submit");
			}
		},
		getUser: function() {
			this.user = JSON.parse(localStorage.getItem('user'));	
			if(this.user == null) {
				this.getComments("/customer/"+this.$route.params.id);
				return;
			}
			if(this.user.userType === "ADMINISTRATOR") {
				this.getComments("/admin/"+this.$route.params.id);
			}
			else if(this.user.userType === "SALESMAN") {
				this.getComments("/salesman/"+this.$route.params.id);
			}
			else if(this.user.userType === "CUSTOMER") {
				this.getComments("/customer/"+this.$route.params.id);
				this.checkIfCanComment();
			}
		},
		checkIfCanComment: function() {
			let jwt = JSON.parse(localStorage.getItem('user')).jwt;
			axios({
				method: 'get',
				url: 'rest/can-comment/'+ this.$route.params.id,
				headers: { 'Authorization':'Bearer ' + jwt }
			}).then((response) => {
				if(response.data == true)
					this.canComment = true;
            });
		},
		getComments: function(urlType) {
			axios({
				method: 'get',
				url: 'rest/comments' + urlType
			}).then((response) => {
                // alert("Comments loaded!");
				this.comments = response.data;
            });
		},
		addToCart: function() {
			let user = JSON.parse(window.localStorage.getItem('user'));
			if (!user) return;
			let jwt = user.jwt;
			axios({
				method: 'POST',
				url: 'rest/cart/',
				data: {manifestationID: this.manifestation.id, count: this.count, ticketType: this.ticketType},
				headers: { 'Authorization':'Bearer ' + jwt }
			}).then((response) => {
				if ("error" in response.data) {
					alert(response.data.message)
					return
				}
                toast("Product added! Click on cart icon to view added items.");
            });
        },
/*	
		mapInit: function() {
			var vectorSource = new ol.source.Vector({});
			var vectorLayer = new ol.layer.Vector({
    			source: vectorSource
  			});
			
			// create custom marker image with custom text in bottom
		  	var iconStyle = new ol.style.Style({
		    	image: new ol.style.Icon({
		      	anchor: [12, 37],
		      	anchorXUnits: 'pixels', //'fraction'
		      	anchorYUnits: 'pixels',
		      	opacity: 0.8,
		      	src: 'https://maps.google.com/mapfiles/ms/micons/blue.png'
		    	})
		  	});
	
			var marker;
			setMarker = function(coordinate) {
			  marker = new ol.Feature(
			    new ol.geom.Point(ol.proj.fromLonLat(coordinate))
			  );
			  marker.setStyle(iconStyle);
			  vectorSource.addFeature(marker);
			}

			let map = new ol.Map({
		        // the map will be created using the id mapa
		        target: document.getElementById("mapa"),
		        layers: [
		          // adding a background tiled layer
		          new ol.layer.Tile({
		            source: new ol.source.OSM() // tiles are served by OpenStreetMap
		          }), vectorLayer
		        ],
		
		        // the map view will initially show the area of Novi Sad
		        view: new ol.View({
		          zoom: 13,
				  center: ol.proj.fromLonLat([19.86421097721076, 45.25226720627225]),
		          constrainResolution: true
		        }),
		      });
			setMarker([19.86421097721076, 45.25226720627225]);

			map.on('click', function(event) {
				var coordinates = ol.proj.toLonLat(event.coordinate);
				console.log(coordinates);
				vectorSource.clear();
				setMarker(coordinates);
			});
			
		}*/
	},
	
	filters: {
    	dateFormat: function (value, format) {
    		var parsed = moment(value);
    		return parsed.format(format);
		}
	}
})