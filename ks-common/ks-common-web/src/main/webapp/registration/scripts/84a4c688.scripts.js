"use strict";angular.module("regCartApp",["configuration","ngCookies","ngResource","ngSanitize","ui.router","ui.bootstrap"]).config(["$stateProvider","$urlRouterProvider",function(a,b){b.otherwise("/myCart"),a.state("root",{templateUrl:"partials/main.html",controller:"MainCtrl"}).state("root.cart",{url:"/myCart",templateUrl:"partials/cart.html",controller:"CartCtrl"}).state("root.schedule",{url:"/mySchedule",templateUrl:"partials/schedule.html",controller:"ScheduleCtrl"}).state("root.additionalOptions",{url:"/options",templateUrl:"partials/additionalOptions.html"})}]),angular.module("configuration",[]).constant("APP_URL","http://localhost:8081/ks-with-rice-embedded-dev/services/"),angular.module("regCartApp").controller("MainCtrl",["$scope","TermsService","APP_URL",function(a,b,c){console.log("In Main Controller"),a.appUrl=c.replace("/services/","/"),a.terms=b.query({active:!0},function(){angular.forEach(a.terms,function(b){b.currentTerm&&(a.termId=b.termId)})})}]),angular.module("regCartApp").controller("CartCtrl",["$scope","CartService",function(a,b){console.log("CartController!"),console.log(a.termId),a.$watch("termId",function(c){console.log("term id has changed"),c&&b.getCart().query({termId:c,userId:"admin"},function(b){a.cart=b})}),a.add=function(){b.addCourseToCart().query({cartId:a.cart.cartId,courseCode:a.courseCode,termId:a.termId,regGroupCode:a.regCode,gradingMethod:"",credits:""},function(b){console.log("response: "+JSON.stringify(b)),console.log("Searched for course: "+a.courseCode+" Term: "+a.termId),console.log("Added item:"),a.cart.items.unshift(b)})},a.cancelNew=function(){a.newCcartItem=null,a.showNew=!1},a.delete=function(c){var d,e=a.cart.items[c],f=e.actionLinks;angular.forEach(f,function(a){"removeItemFromCart"==a.action&&(d=a.uri)}),b.removeItemFromCart(d).query({},function(b){a.cart.items.splice(c,1);var d;angular.forEach(b.actionLinks,function(a){"addCourseToCart"==a.action&&(d=a.uri)}),a.userMessage={txt:e.courseCode+"("+e.regGroupCode+") has been successfully removed from your cart.   ",actionLink:d,linkText:"Undo"},a.userActionSuccessful=!0})},a.invokeActionLink=function(c){a.userActionSuccessful=!1,b.invokeActionLink(c).query({},function(b){a.cart.items.unshift(b)})},a.editItem=function(b){a.newCredits=b.credits,a.newGrading=b.grading,b.editing=!0},a.cancelEditItem=function(a){a.editing=!1},a.updateCartItem=function(c,d,e){console.log("Updating:"),console.log(e),b.updateCartItem().query({cartId:a.cart.cartId,cartItemId:c.cartItemId,credits:d,grading:e,userId:"admin"},function(b){console.log(a),c.credits=b.credits,c.grading=b.grading,c.editing=!1})},a.register=function(){b.submitCart().query({cartId:a.cart.cartId,userId:"admin"},function(){console.log("Submiting cart."),b.getCart().query({termId:a.termId,userId:"admin"},function(b){a.cart=b})})}}]);var cartServiceModule=angular.module("regCartApp");cartServiceModule.controller("ScheduleCtrl",["$scope","ScheduleService",function(a,b){a.schedules=b.query({person:"admin"},function(a){console.log("got schedule data back"),console.log(a)})}]),angular.module("regCartApp").service("CartService",["$resource","APP_URL",function(a,b){this.getCart=function(){return a(b+"CourseRegistrationCartClientService/searchForCart",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.getGradingOptions=function(){return a(b+"CourseRegistrationCartClientService/getStudentRegistrationOptions",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.addCourseToCart=function(){return a(b+"CourseRegistrationCartClientService/addCourseToCart",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.removeItemFromCart=function(c){return a(b+c,{},{query:{method:"GET",cache:!1,isArray:!1}})},this.invokeActionLink=function(c){return a(b+c,{},{query:{method:"GET",cache:!1,isArray:!1}})},this.updateCartItem=function(){return a(b+"CourseRegistrationCartClientService/updateCartItem",{},{query:{method:"GET",cache:!1,isArray:!1}})},this.submitCart=function(){return a(b+"CourseRegistrationCartClientService/submitCart",{},{query:{method:"GET",cache:!1,isArray:!1}})}}]),angular.module("regCartApp").factory("TermsService",["$resource","APP_URL",function(a,b){return a(b+"ScheduleOfClassesService/terms",{},{query:{method:"GET",cache:!0,isArray:!0}})}]),angular.module("regCartApp").factory("ScheduleService",["$resource","APP_URL",function(a,b){return a(b+"CourseRegistrationClientService/personschedule",{},{query:{method:"GET",cache:!1,isArray:!0}})}]);