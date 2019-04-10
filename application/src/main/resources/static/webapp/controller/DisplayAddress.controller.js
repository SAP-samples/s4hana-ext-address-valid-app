sap.ui.define([
    "sap/enterpriseEventing/ui/controller/BaseController",
    "sap/ui/model/json/JSONModel",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
], function (BaseController, JSONModel, MessageToast, MessageBox) {
    "use strict";
    return BaseController.extend("sap.enterpriseEventing.ui.controller.DisplayAddress", {
        /* =========================================================== */
        /* lifecycle methods                                           */
        /* =========================================================== */

        /**
         * Called when the display address controller is instantiated. It sets up the event handling for the display/edit communication and other lifecycle tasks.
         * @public
         */
        onInit: function () {
            var oViewModel = new JSONModel({
                busy: false,
                delay: 0,
                countryName: "" //we will use this field to show the country name as user friendly instead of ISO code.
            });
            this.setModel(oViewModel, "viewModel");

            this.getRouter().getRoute("confirmAddress").attachEventOnce("patternMatched", this._onPatternMatched.bind(this));

            // attach event handlers for the data model to show busy whenever there is a request to the back-end
            this.getModel().attachRequestSent(function () {
                this.getModel("viewModel").setProperty("/busy", true);
            }.bind(this));

            // attach requestCompleted handler to react on success case
            this.getOwnerComponent().getModel().attachRequestCompleted(this._onRequestCompleted.bind(this));
        },

        /* =========================================================== */
        /* event handlers                                              */
        /* =========================================================== */

        /**
         * Event handler called when approve button is clicked.
         * Calls the base class function to do the PATCH request
         * @public
         */
        onApprovePress: function () {
            this.doPatchRequest();
        },

        /**
         * Event handler called when edit button is clicked.
         * Navigates to the edit page.
         * @public
         */
        onEditPress: function () {
            this.getRouter().navTo("editAddress");
        },

        /**
         * Event handler called when confirmAddress route was hit. This will be called only once.
         * Security token is stored in a model to be used by all views that need it.
         * @private
         */
        _onPatternMatched: function (oParam) {
            // get the security token and store it for later use.
            var sToken = oParam.getParameter("arguments").token;
            this.getOwnerComponent().setModel(new JSONModel({
                token: sToken
            }), "tokenModel");

            this.loadAddress();

            this._getCountries();
        },

        /**
         * Event handler called each time a request via default model was made and completed.
         * This function reacts in case of success.
         * It sets the busy indicator to false, shows user a MessageToast,
         * and makes the buttons invisible.
         * Also it does the job expected from SimpleType.formatValue function: Format the value in model to
         * show in UI.
         * @private
         */
        _onRequestCompleted: function (oEvent) {
            var oParams = oEvent.getParameters();
            if (!oParams.success) // dont do anything on fail, because ErrorHandler deals with it.
                return;

            // change the country name from ISO code to user friendly name (SimpleType.formatValue)
            this.getModel("countries").pSequentialImportCompleted.then(function (value) {
                var sISOCode = this.getModel().getProperty("/country");
                var oCurrentCountry = this.getModel("countries").getData().find(function (oArrayItem) {
                    return oArrayItem.Country === sISOCode;
                });
                if(typeof oCurrentCountry !== 'undefined'){
                    this.getModel("viewModel").setProperty("/countryName", oCurrentCountry.CountryName);
                }

                // set the view model busy indicator to false on successful call
                this.getModel("viewModel").setProperty("/busy", false);
            }.bind(this));

            // Only show success in case of patch
            if (oParams.type != "PATCH")
                return;

            MessageToast.show("The address was successfully approved. You can safely close the browser.", {
                closeOnBrowserNavigation: false,
                duration: 10000
            });

            // set the buttons invisible
            this.byId("editButton").setVisible(false);
            this.byId("approveButton").setVisible(false);
        },

        /* =========================================================== */
        /* begin: internal methods                                     */
        /* =========================================================== */

        /**
         * Fetches the countries and fills the "countries" model.
         * @function
         * @private
         */
        _getCountries: function () {
            var oCountryModel = this.getModel("countries");
            oCountryModel.loadData("/rest/countries", {
                token: this.getModel("tokenModel").getData().token
            });
        }

    });
});