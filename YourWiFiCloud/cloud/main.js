"use strict";

require("cloud/app.js");

var _ = require("underscore");
_.str = require("cloud/libs/underscore.string.min.js");

var models_user = require("cloud/models/user.js");

//----------------------------------------------------------------------------
// Cloud Functions
//----------------------------------------------------------------------------

Parse.Cloud.define("create_admin_user", function(request, response) {
  Parse.Cloud.useMasterKey();
  
  var password = request.params.password;
  if (!password) {
    response.error("Bad Request. You must specify the password.");
  } else if (!password.match(/^\w{4,32}$/)) {
    response.error("Bad Request. The password must be 4-32 letters of words.");
  }
  
  models_user.getAdminUserAsync()
  .then(function(user) {
    if (user) {
      return Parse.Promise.when(Parse.Promise.as(user), Parse.Promise.as("already_exists"));
    } else {
      return Parse.Promise.when(models_user.createAdminUserAsync(password), Parse.Promise.as(null));
    }
  }).then(function(user, already_exists) {
    if (already_exists) {
      response.success("Admin user already exists.");
    } else {
      response.success("Admin user created.");
    }
  }).fail(function(error) {
    response.error("Internal Server Error: " + error.message);
  });
});

//----------------------------------------------------------------------------
// Object Hooks
//----------------------------------------------------------------------------

Parse.Cloud.beforeSave(Parse.User, function(request, response) {
  if (request.object.get("username") != "admin") {
    response.error("This user is not allowed to create.");
  }
  response.success();
});

Parse.Cloud.beforeSave(Parse.Role, function(request, response) {
  if (request.object.get("name") != "admin") {
    response.error("This role is not allowed to create.");
  }
  response.success();
});
