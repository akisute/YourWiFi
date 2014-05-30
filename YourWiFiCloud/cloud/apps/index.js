"use strict";

exports.GETHandler = function(request, response) {
  response.render("index");
}

exports.RedirectHandler = function(request, response) {
  response.redirect(303, '/');
}
