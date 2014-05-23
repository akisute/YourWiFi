"use strict";

require("cloud/app.js");

var Promise = require("cloud/libs/promise.js");
var _ = require("underscore");
_.str = require("cloud/libs/underscore.string.min.js");

Parse.Cloud.define("create_admin_user", function(request, response) {
  response.success("hello, world!");
});
