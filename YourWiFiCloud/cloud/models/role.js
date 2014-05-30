"use strict";

var _ = require("underscore");
_.str = require("cloud/libs/underscore.string.min.js");

exports.getAdminRoleAsync = function() {
  var query = new Parse.Query(Parse.Role);
  query.equalTo("name", "admin");
  return query.first();
}

exports.createAdminRoleAsync = function() {
  var acl = new Parse.ACL();
  acl.setPublicReadAccess(true);
  acl.setPublicWriteAccess(false);
  var role = new Parse.Role("admin", acl);
  return role.save();
}

exports.applyAdminRoleAsync = function(user) {
  return exports.getAdminRoleAsync()
  .then(function(role) {
    if (role) {
      return Parse.Promise.as(role);
    } else {
      return exports.createAdminRoleAsync();
    }
  }).then(function(role) {
    role.getUsers().add(user);
    return role.save();
  });
}