sap.ui.define([
    "sap/ui/core/UIComponent",
    "sap/ui/model/json/JSONModel",
    "sap/enterpriseEventing/ui/controller/ErrorHandler"
], function (UIComponent, JSONModel, ErrorHandler) {
    "use strict";
    return UIComponent.extend("sap.enterpriseEventing.ui.Component", {
        metadata: {
            manifest: "json"
        },
        init: function () {
            // call the init function of the parent
            UIComponent.prototype.init.apply(this, arguments);

            // set empty models. It will be filled by DispalyAddress
            // the reason to set them here is to hand them to error handler.
            this.setModel(new JSONModel());
            this.setModel(new JSONModel(), "countries");

            this.getRouter().initialize();

            this._oErrorHandler = new ErrorHandler(this);
        },


        /**
         * The component is destroyed by UI5 automatically.
         * In this method, the ErrorHandler is destroyed.
         * @public
         * @override
         */
        destroy: function () {
            this._oErrorHandler.destroy();
            // call the base component's destroy function
            UIComponent.prototype.destroy.apply(this, arguments);
        }


    });
});