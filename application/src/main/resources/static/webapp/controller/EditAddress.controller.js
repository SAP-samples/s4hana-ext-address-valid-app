sap.ui.define([
    "sap/enterpriseEventing/ui/controller/BaseController",
    "sap/ui/model/json/JSONModel",
    "sap/m/MessageToast",
    "sap/m/Dialog",
    'sap/ui/model/Filter'
], function (BaseController, JSONModel, MessageToast, Dialog, Filter) {
    "use strict";
    return BaseController.extend("sap.enterpriseEventing.ui.controller.EditAddress", {

        /* =========================================================== */
        /* lifecycle methods                                           */
        /* =========================================================== */

        /**
         * Called when the edit address controller is instantiated.
         * @public
         */
        onInit: function () {
            this.fieldChanged = false;
        },

        /* =========================================================== */
        /* event handlers                                              */
        /* =========================================================== */

        /**
         * Event handler called when cancel button is clicked.
         * Handles the unsaved dialog, and navigates back to display address.
         * @public
         */
        onCancelPress: function () {
            if (this.fieldChanged) {
                this._showUnsavedChangesDialog();
                return;
            }

            this.getRouter().navTo("confirmAddress", {token: this.getModel("tokenModel").getData().token});
        },

        /**
         * Event handler called when approve button is clicked.
         * Calls the base class function to do the PATCH request, and navigates back to the display view.
         * @public
         */
        onApprovePress: function () {
            // call the base function to do PATCH request
            this.doPatchRequest();

            this.getModel().pSequentialImportCompleted.then(
                this.getRouter().navTo("confirmAddress", {token: this.getModel("tokenModel").getData().token}));
        },

        /**
         * Event handler called when value in input field is changed.
         * @public
         */
        onFieldChange: function () {
            this.fieldChanged = true;
        },

        /**
         * Event handler called when value help requested.
         * This request happens whenever user clicks on the country in edit mode.
         * This function needs to show the value help dialog for country.
         * @public
         */
        onValueHelpRequest: function () {
            MessageToast.show("DEBUG: onValueHelpRequest triggered");
            var oView = this.getView();
            var oDialog = oView.byId("idChooseCountryDialog");
            // create dialog lazily
            if (!oDialog) {
                // create dialog via fragment factory
                oDialog = sap.ui.xmlfragment(oView.getId(), "sap.enterpriseEventing.ui.view.ChooseCountryDialog", this);
                oView.addDependent(oDialog);
            }

            // set the current country as selected item in the list
            var oList = this.byId("idChooseCountryList");
            var sCountryName = this.getModel("viewModel").getProperty("/countryName");
            var oItems = oList.getItems();
            var oCurrentCountryListItem = oItems.find(function (oItem) {
                return oItem.getProperty("title") === sCountryName;
            });
            oList.setSelectedItem(oCurrentCountryListItem);

            oDialog.open();
        },

        /**
         * Event handler called when cancel is pressed in the dialog.
         * @public
         */
        onChooseCountryCloseDialog: function () {
            this.getView().byId("idChooseCountryDialog").close();
        },

        /**
         * Event handler called when a country is selected in the value help dialog.
         * This function needs to set both Country name and ISO code in the corresponding models,
         * trigger field change and close the dialog
         * @public
         */
        onChooseCountry: function (oEvent) {
            var oListItem = oEvent.getParameter("listItem");
            var oCtx = oListItem.getBindingContext("countries");
            var oCountry = oCtx.getObject();

            // set the view model, user friendly country name to display
            this.getModel("viewModel").setProperty("/countryName", oCountry.CountryName);

            // set the defaul model, ISO code of the country to do the patch request
            this.getModel().setProperty("/country", oCountry.Country);

            //field has changed
            this.onFieldChange();

            this.onChooseCountryCloseDialog();
        },

        /**
         * Event handler called when user searches a country in the value help dialog.
         * This function needs to set the filter with the query to perform the search operation.
         * @public
         */
        onSearch: function (oEvent) {
            // add filter for search
            var aFilters = [];
            var sQuery = oEvent.getSource().getValue();
            if (sQuery && sQuery.length > 0) {
                var filter = new Filter("CountryName", sap.ui.model.FilterOperator.Contains, sQuery);
                aFilters.push(filter);
            }

            // update list binding
            var oList = this.byId("idChooseCountryList");
            var oBinding = oList.getBinding("items");
            oBinding.filter(aFilters, "Application");
        },

        /* =========================================================== */
        /* begin: internal methods                                     */
        /* =========================================================== */

        /**
         * Shows a dialog to the user to ask about unsaved changes.
         * @function
         * @private
         */
        _showUnsavedChangesDialog: function () {
            var dialog = new Dialog({
                title: 'Warning!',
                type: 'Message',
                state: 'Warning',
                content: new sap.m.Text({
                    text: "There are unsaved changes! Are you sure you want to navigate back without saving?"
                }),
                beginButton: new sap.m.Button({
                    text: 'Yes',
                    press: function () {
                        dialog.close();

                        if (this.fieldChanged) {
                            // need to reset the changes but JSONModel does not have resetChanges function
                            // to reset the changes, we fetch fresh data.
                            this.loadAddress();
                            this.fieldChanged = false;
                        }
                        this.onCancelPress();
                    }.bind(this)
                }),
                endButton: new sap.m.Button({
                    text: 'No',
                    press: function () {
                        dialog.close();
                    }
                }),
                afterClose: function () {
                    dialog.destroy();
                }
            });

            dialog.open();
        }

    });
});