sap.ui.define([
	"sap/ui/core/util/MockServer",
	"sap/ui/model/json/JSONModel"
], function (MockServer, JSONModel) {
	"use strict";
	return {
		init: function () {
			// create
			var rootUri = "/rest/";

			var oUriParameters = jQuery.sap.getUriParameters();
			// configure
			MockServer.config({
				autoRespond: true,
				autoRespondAfter: oUriParameters.get("serverDelay") || 1000
			});

			var oMockServer = new MockServer({
				rootUri: rootUri,
				requests: this.getRequests()
			});
			// start
			oMockServer.start();
		},
		
		getRequests: function () {
			var request =  [];
			
			var oModel = new JSONModel();
			oModel.loadData("../localService/mockdata/example.json", null , false);
			
			request.push({
			      method: "GET",
			      path: new RegExp("businesspartner/address?(.*)ValidToken"),
			      response: function(oXhr/*, sUrlParams*/) {
			      	oXhr.respondJSON(200, {}, oModel.getJSON());
			        return true;
			      }
			});
			
			request.push({
			      method: "HEAD",
			      path: new RegExp("businesspartner/address?(.*)ValidToken"),
			      response: function(oXhr/*, sUrlParams*/) {
			      	oXhr.respondJSON(200, {}, JSON.stringify({}));
			        return true;
			      }
			});
			
			request.push({
			      method: "PATCH",
			      path: new RegExp("businesspartner/address?(.*)ValidToken"),
			      response: function(oXhr/*, sUrlParams*/) {
			      	oXhr.respondJSON(202, {}, JSON.stringify({}));
			        return true;
			      }
			});

			var oWrongTokenModel = new JSONModel();
			oWrongTokenModel.loadData("../localService/mockdata/tokenExpired.json", null , false);
			request.push({
				method: "GET",
				path: new RegExp("businesspartner/address?(.*)WrongToken"),
				response: function(oXhr/*, sUrlParams*/) {
					oXhr.respondJSON(500, {}, oWrongTokenModel.getJSON());
					return true;
				}
			});


            var oCountryModel = new JSONModel();
            oCountryModel.loadData("../localService/mockdata/countries.json", null , false);

            request.push({
                method: "GET",
                path: new RegExp("countries?(.*)ValidToken"),
                response: function(oXhr/*, sUrlParams*/) {
                    oXhr.respondJSON(200, {}, oCountryModel.getJSON());
                    return true;
                }
            });

			request.push({
				method: "GET",
				path: new RegExp("countries?(.*)WrongToken"),
				response: function(oXhr/*, sUrlParams*/) {
					oXhr.respondJSON(200, {}, oCountryModel.getJSON());
					return true;
				}
			});
			
			return request;
		}
		
	};
});