[![REUSE status](https://api.reuse.software/badge/github.com/SAP-samples/s4hana-ext-address-valid-app)](https://api.reuse.software/info/github.com/SAP-samples/s4hana-ext-address-valid-app)
# SAP S/4HANA Cloud Extensions - Collaborative Address Validation Using SAP Enterprise Eventing Services
This repository contains the sample code for the [Collaborative Address Validation Using SAP Enterprise Eventing Services tutorial](http://tiny.cc/s4-address-valid-app).

*This code is only one part of the tutorial, so please follow the tutorial before attempting to use this code.*

## Description

The [Collaborative Address Validation Using SAP Enterprise Eventing Services tutorial](http://tiny.cc/s4-address-valid-app) showcases an extension to an SAP S/4HANA Cloud system. This scenario is a collaborative use case to enable customers to validate their company address changes. Once the address of a customer is being changed in the SAP S/4HANA Cloud system, a business event is being pushed to the connected SAP Business Technology Platform (BTP) (Cloud Foundry) account. Upon receiving the event, the Java application sends a notification email to the contact person including address details. The email also contains a secured link to an SAPUI5 application where the contact person can confirm the address and adjust it when necessary. The confirmed address is then written back to the SAP S/4HANA Cloud system.

#### SAP Extensibility Explorer

This tutorial is one of multiple tutorials that make up the [SAP Extensibility Explorer](https://sap.com/extends4) for SAP S/4HANA Cloud.
SAP Extensibility Explorer is a central place where anyone involved in the extensibility process can gain insight into various types of extensibility options. At the heart of SAP Extensibility Explorer, there is a rich repository of sample scenarios which show, in a hands-on way, how to realize an extensibility requirement leveraging different extensibility patterns.


Requirements
-------------
- An account in SAP Business Technology Platform (BTP) with a subaccount in the Cloud Foundry environment and Enterpise Messaging, Destination and Authorization & Trust Management services enabled.
- An SAP S/4HANA Cloud tenant. **This is a commercial paid product.**
- [Java SE **8** Development Kit (JDK)](https://www.oracle.com/technetwork/java/javase/downloads/index.html) and [Apache Maven](http://maven.apache.org/download.cgi) to build the application.

Download and Installation
-------------
This repository is a part of the [Download the Consumer Application](https://help.sap.com/viewer/7dde0e0e3a294f01a6f7870731c5e4ad/SHIP/en-US/627ffa846cc54f729279335f3686cd0e.html) step in the tutorial. Instructions for use can be found in that step.

[Please download the zip file by clicking here](https://github.com/SAP/s4hana-ext-address-valid-app/archive/master.zip) so that the code can be used in the tutorial.


Known issues
---------------------
There are no known major issues.

How to obtain support
---------------------
If you have issues with this sample, please open a report using [GitHub issues](https://github.com/SAP/s4hana-ext-address-valid-app/issues).

License
-------
Copyright © 2020 SAP SE or an SAP affiliate company. All rights reserved.
This project is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE file](LICENSES/Apache-2.0.txt).
