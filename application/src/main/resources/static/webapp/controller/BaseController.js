sap.ui.define([
    "sap/ui/core/mvc/Controller"
], function (Controller) {
    "use strict";
    return Controller.extend("sap.enterpriseEventing.ui.controller.BaseController", {

        /**
         * Method to carry out patch request to the backend.
         * @public
         */
        doPatchRequest: function () {
            var oTokenModel = this.getModel("tokenModel");

            var oModel = this.getModel();
            oModel.loadData(
                "/rest/businesspartner/address?token=" + oTokenModel.getData().token,
                oModel.getJSON(),
                true,
                "PATCH",
                true,
                "false",
                {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                }
            );
        },

        /**
         * Method to carry out GET request to the backend. This request will get the address data.
         * @public
         */
        loadAddress: function () {
            this.getModel().loadData("/rest/businesspartner/address", {
                token: this.getModel("tokenModel").getData().token
            });
        },

        /**
         * Convenience method for accessing the router in every controller of the application.
         * @public
         * @returns {sap.ui.core.routing.Router} the router for this component
         */
        getRouter: function () {
            return this.getOwnerComponent().getRouter();
        },

        /**
         * Convenience method for getting the model by name in every controller of the application.
         * @public
         * @param {string} sName the model name
         * @returns {sap.ui.model.Model} the model instance
         */
        getModel: function (sName) {
            return this.getOwnerComponent().getModel(sName);
        },

        /**
         * Convenience method for setting the model in every controller of the application.
         * @public
         * @param {sap.ui.model.Model} oModel the model instance
         * @param {string} sName the model name
         */
        setModel: function (oModel, sName) {
            this.getOwnerComponent().setModel(oModel, sName);
        }
    });
});