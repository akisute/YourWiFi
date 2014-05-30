"use strict";

var express = require('express');
var app = express();

var parseExpressHttpsRedirect = require('parse-express-https-redirect');
var parseExpressCookieSession = require('parse-express-cookie-session');

var SIGNING_SECRET = 'E`PH59%C#0tCP@{3';

// Global app configuration section
app.set('views', 'cloud/views');
app.set('view engine', 'jade');
app.use(express.bodyParser());
app.use(express.cookieParser(SIGNING_SECRET));
app.use(express.cookieSession());
app.use(express.csrf());
app.use(parseExpressHttpsRedirect());
app.use(parseExpressCookieSession());

// Custom app configuration to set global variables for template engine
app.use(function(request, response, next) {
  response.locals.user = Parse.User.current();
  response.locals._csrf = request.session._csrf;
  next();
});

// URL routing section
var app_index = require("cloud/apps/index.js");
var app_usersession = require("cloud/apps/usersession.js");

app.get('/',            app_index.GETHandler);
app.get('/index',       app_index.RedirectHandler);
app.get('/index.html',  app_index.RedirectHandler);
app.get('/login',       app_usersession.loginGETHandler);
app.post('/login',      app_usersession.loginPOSTHandler);
app.get('/logout',      app_usersession.logoutGETHandler);

// Begin listening
app.listen();
