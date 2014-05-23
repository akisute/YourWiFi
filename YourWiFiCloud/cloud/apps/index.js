"use strict";

var Promise = require("cloud/libs/promise.js");
var _ = require("underscore");


exports.handler = function(request, response) {
  response.render("index");
}

