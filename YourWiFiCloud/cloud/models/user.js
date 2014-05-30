"use strict";

var _ = require("underscore");
_.str = require("cloud/libs/underscore.string.min.js");

var models_role = require("cloud/models/role.js");

exports.getAdminUserAsync = function() {
  var query = new Parse.Query(Parse.User);
  query.equalTo("username", "admin");
  return query.first();
}

exports.createAdminUserAsync = function(password) {
  var user = new Parse.User();
  user.set("username", "admin");
  user.set("password", password);
  return user.signUp()
  .then(function(user) {
    return models_role.applyAdminRoleAsync(user);
  });
}