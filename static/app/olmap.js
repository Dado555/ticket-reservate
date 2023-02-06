Vue.component("olmap", {
	props: ['longitude', 'latitude', 'resize'],
    data: function () {
            return {
                userType: null,
				map: null
			}
    },
    template: `
        <div id="mapa" class="map"></div>
        `,
    mounted () {
		this.$nextTick(function() {
			this.mapInit();
		});
    },
	watch: {
		resize: function() {
			this.map.updateSize();
		}
	},
    methods: {
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

			this.map = new ol.Map({
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
			if(this.longitude)
				setMarker([this.longitude, this.latitude]);
			else
				setMarker([19.86421097721076, 45.25226720627225]);
			
			if(this.longitude === undefined) {
				this.map.on('click', (event) => {
					var coordinates = ol.proj.toLonLat(event.coordinate);
					console.log(coordinates);
					vectorSource.clear();
					setMarker(coordinates);
					$('input[name=longitude]:text').val(coordinates[0]);
					$('input[name=latitude]:text').val(coordinates[1]);
					this.getAddress(coordinates[0], coordinates[1]);
				});
			}
		},
		getAddress: function(lon, lat) {
			axios({
				method: 'get',
				url: 'https://nominatim.openstreetmap.org/reverse?lat='+ lat +'&lon='+lon + '&format=json'
			 }).then((response) => {
                if(response.data != null) {
					console.log(response.data);
					$('input[name=zip]:text').val(response.data.address.postcode);
					$('input[name=city]:text').val(response.data.address.city + ", " + response.data.address.country);
					$('input[name=street]:text').val(response.data.address.road + " " + response.data.address.house_number);
				}
             });
		}
    }
})