sap.ui.define([
        "sap/ui/base/Object",
        "sap/m/MessageBox"
    ], function (UI5Object, MessageBox) {
        "use strict";

        return UI5Object.extend("sap.enterpriseEventing.ui.controller.ErrorHandler", {

            /**
             * Handles application errors by automatically attaching to the model events and displaying errors when needed.
             * @class
             * @param {sap.ui.core.UIComponent} oComponent reference to the app's component
             * @public
             */
            constructor: function (oComponent) {
                this._oComponent = oComponent;
                this._bMessageOpen = false;
                this._bTechnicalErrorMessageShownAlready = false;

                // handle the default model error
                oComponent.getModel().attachRequestCompleted(function (oEvent) {
                    var oParams = oEvent.getParameters();

                    if (!oParams.success){
                        this._bTechnicalErrorMessageShownAlready = true;
                        this._showServiceError(oParams.errorobject);
                    }
                }, this);

                // handle the countries model
                oComponent.getModel("countries").attachRequestCompleted(function (oEvent) {
                    var oParams = oEvent.getParameters();

                    if (!oParams.success && !this._bTechnicalErrorMessageShownAlready)
                        this._showServiceError(oParams.errorobject);
                }, this);
            },

            /**
             * Shows a {@link sap.m.MessageBox} when a service call has failed.
             * Only the first error message will be display.
             * @param {object} oDetails a technical error object to be displayed on request
             * @private
             */
            _showServiceError: function (oDetails) {
                if (this._bMessageOpen) {
                    return;
                }
                var sDetails = this._prepareErrorDetailsToShow(oDetails);
                this._bMessageOpen = true;
                MessageBox.error(
                    "Sorry, a technical error occurred! Please try again later." +
                    "\nIf the error persist, please contact your SAP representative.",
                    {
                        id: "serviceErrorMessageBox",
                        details: sDetails,
                        actions: [MessageBox.Action.CLOSE],
                        onClose: function () {
                            this._bMessageOpen = false;
                            this._oComponent.getRouter().getTargets().display("technicalError");
                        }.bind(this)
                    }
                );
            },

            /**
             * Prepares an object to show in the {@link sap.m.MessageBox}.
             * @param {object} oDetails a technical error object received from backend
             * @private
             * @returns {string} formatted text to show in {@link sap.m.MessageBox} as details.
             */
            _prepareErrorDetailsToShow: function (oDetails) {
                var sErrorMessageParsed = "<p>Backend responded with the following information:<br>";

                if (!oDetails.responseText) {
					sErrorMessageParsed +=
						JSON.stringify(oDetails) + "</p>";
                } else {
                    var formattedResponse = oDetails.responseText.replace(/\\n/g, "\n");
                    formattedResponse = formattedResponse.replace(/\\"/g, "\"");
					sErrorMessageParsed += "<pre>" + formattedResponse + "</pre>";
                }

                sErrorMessageParsed +=
                    "<p><strong>This can happen if:</strong></p>" +
                    "<ul>" +
                    "<li>The url is wrong. Please try to click on the link you received via email.</li>" +
                    "<li>Backend system is down</li>" +
                    "<li>Wrong S/4 destination configuration</li>" +
                    "<li>Missing/wrong communication arrangement</li>" +
                    "<li>Technical user is locked due to several log on trial with wrong credentials<br>" +
                    "You can simply check this by using <em>Display Technical Users</em> app in your S/4HANA.<br>" +
                    "If this is the case, you can simply unclock your user in the app as well.</li>" +
                    "<li>a backend component is not <em>available</em></li>" +
                    "<li>You are not connected to the internet</li>" +
                    "</ul>" +
                    "<p>Get more help <a href='https://help.sap.com/viewer/index' target='_top'>here</a>.";

                return sErrorMessageParsed;
            }

        });

    }
);