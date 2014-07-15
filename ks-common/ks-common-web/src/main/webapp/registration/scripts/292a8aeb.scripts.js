"use strict";angular.module("regCartApp",["configuration","ngAnimate","ngCookies","ngResource","ngSanitize","ngTouch","ui.router","ui.bootstrap"]).config(["$stateProvider","$urlRouterProvider","$httpProvider",function(a,b,c){b.otherwise("/myCart");var d={templateUrl:"partials/cart.html",controller:"CartCtrl"},e={templateUrl:"partials/schedule.html",controller:"ScheduleCtrl"},f={templateUrl:"partials/searchForm.html",controller:"SearchFormCtrl"};a.state("root",{"abstract":!0,views:{root:{templateUrl:"partials/main.html",controller:"MainCtrl"}}}).state("root.schedule",{url:"/mySchedule",views:{"":e,mycart:d,schedule:e,searchform:f}}).state("root.cart",{url:"/myCart",views:{"":e,mycart:d,schedule:e,searchform:f}}).state("root.search",{url:"/search/:searchCriteria",views:{"":{templateUrl:"partials/search.html",controller:"SearchCtrl"},mycart:d,schedule:e,searchform:f}}),c.interceptors.push("loginInterceptor")}]),angular.module("configuration",[]).value("APP_URL","ks-with-rice-bundled-dev/services/").value("DEFAULT_TERM","kuali.atp.2012Fall"),angular.module("regCartApp").constant("URLS",{scheduleOfClasses:"ScheduleOfClassesClientService",courseRegistration:"CourseRegistrationClientService",courseRegistrationCart:"CourseRegistrationCartClientService",developmentLogin:"DevelopmentLoginClientService"}).constant("STATE",function(){var a={failed:"kuali.lpr.trans.state.failed",processing:"kuali.lpr.trans.state.processing",succeeded:"kuali.lpr.trans.state.succeeded",item:{failed:"kuali.lpr.trans.item.state.failed",processing:"kuali.lpr.trans.item.state.processing",succeeded:"kuali.lpr.trans.item.state.succeeded",waitlist:"kuali.lpr.trans.item.state.waitlist",waitlistActionAvailable:"kuali.lpr.trans.item.state.waitlistActionAvailable"}};return{lpr:a,action:[a.item.waitlistActionAvailable],error:[a.failed,a.item.failed],processing:[a.processing,a.item.processing],success:[a.succeeded,a.item.succeeded],waitlist:[a.item.waitlist]}}()).constant("STATUS",{action:"action",editing:"editing",error:"error","new":"new",processing:"processing",success:"success",waitlist:"waitlist"}).constant("GRADING_OPTION",{audit:"kuali.resultComponent.grade.audit",letter:"kuali.resultComponent.grade.letter",passFail:"kuali.resultComponent.grade.passFail"}).constant("ACTION_LINK",{removeItemFromCart:"removeItemFromCart",undoDeleteCourse:"undoDeleteCourse"}).constant("VALIDATION_ERROR_TYPE",{maxCredits:"kuali.lpr.trans.message.credit.load.exceeded",timeConflict:"kuali.lpr.trans.message.time.conflict",waitlistAvailable:"kuali.lpr.trans.message.waitlist.available",waitlistWaitlisted:"kuali.lpr.trans.message.waitlist.waitlisted",waitlistFull:"kuali.lpr.trans.message.waitlist.full",waitlistNotOffered:"kuali.lpr.trans.message.waitlist.not.offered",transactionException:"kuali.lpr.trans.message.exception",transactionItemException:"kuali.lpr.trans.item.message.exception"}).constant("VALIDATION_SUCCESS_TYPE",{waitlistStudentRemoved:"kuali.lpr.trans.message.waitlist.student.removed",waitlistUpdated:"kuali.lpr.trans.message.waitlist.options.updated",courseUpdated:"kuali.lpr.trans.message.course.updated",courseDropped:"kuali.lpr.trans.message.course.dropped",personRegistered:"kuali.lpr.trans.message.person.registered"}),angular.module("regCartApp").constant("SEARCH_FACETS",[{label:"Seats",id:"seatsAvailable",optionsProvider:function(a){var b=0;angular.forEach(a,function(a){a.seatsAvailable>0&&b++});var c=[];return b>0&&c.push({label:"Seats available",value:"seatsAvailable",count:b}),c},filter:function(a){return a.seatsAvailable>0}},{label:"Credits",id:"creditOptions",optionsKey:"creditOptions"},{label:"Course Level",id:"courseLevel",optionsKey:"courseLevel",prepare:function(a){console.log(a),angular.forEach(a,function(a){if(angular.isUndefined(a.courseLevel)){var b=a.courseNumber.substring(0,1)+"00";a.courseLevel=b}})}},{label:"Course Prefix",id:"coursePrefix",optionsKey:"coursePrefix"}]),angular.module("regCartApp").controller("MainCtrl",["$scope","$location","$state","TermsService","ScheduleService","GlobalVarsService","APP_URL","DEFAULT_TERM","LoginService","MessageService","$modal",function(a,b,c,d,e,f,g,h,i,j,k){console.log("In Main Controller"),a.appUrl=g.replace("/services/","/"),a.termId=null,a.termName="",a.studentIsEligibleForTerm=!0,a.$watch("termId",function(b){b&&(a.termName=d.getTermNameForTermId(a.terms,b),b===h?(console.log("checking term eligibility"),d.checkStudentEligibilityForTerm().query({termId:b},function(b){a.studentIsEligibleForTerm=angular.isDefined(b.isEligible)&&b.isEligible||!1},function(b){console.log("Error while checking if term is open for registration",b),a.studentIsEligibleForTerm=!1})):(console.log("term eligibility check bypassed - term != default term"),a.studentIsEligibleForTerm=!0),e.getScheduleFromServer().query({termId:b},function(b){console.log("called rest service to get schedule data - in main.js"),f.updateScheduleCounts(b),a.cartCredits=f.getCartCredits,a.cartCourseCount=f.getCartCourseCount,a.registeredCredits=f.getRegisteredCredits,a.registeredCourseCount=f.getRegisteredCourseCount,a.waitlistedCredits=f.getWaitlistedCredits,a.waitlistedCourseCount=f.getWaitlistedCourseCount,a.showWaitlistedSection=f.getShowWaitlistedSection,a.userId=f.getUserId}))}),a.terms=d.getTermsFromServer().query({termCode:null,active:!0},function(b){a.termId=h,d.setTermId(a.termId),a.termName=d.getTermNameForTermId(b,a.termId)}),a.messages=j.getMessages().query({messageKey:null}),a.logout=function(){i.logout().query({},function(){console.log("Logging out"),location.reload()})},a.goToPage=function(a){console.log("Navigating to page: "+a),b.url(a)},a.$on("sessionExpired",function(){console.log("Received event sessionExpired"),k.open({backdrop:"static",templateUrl:"partials/sessionExpired.html",controller:"SessionCtrl"})}),a.$parent.uiState=c.current.name,a.$on("$stateChangeStart",function(b,c){a.$parent.uiState=c.name})}]).controller("SessionCtrl",["$scope","LoginService",function(a,b){a.logout=function(){b.logout().query({},function(){console.log("Session expired...logging out"),location.reload()})}}]),angular.module("regCartApp").controller("CartCtrl",["$scope","$modal","$timeout","STATE","STATUS","GRADING_OPTION","ACTION_LINK","CartService","ScheduleService","GlobalVarsService",function(a,b,c,d,e,f,g,h,i,j){function k(b){h.getCart().query({termId:b},function(b){a.cart=b;for(var c,f=[],g=!1,h=0;h<a.cart.items.length;h++){var i=a.cart.items[h];if(j.getCorrespondingStatusFromState(i.state)===e.processing){i.status=e.processing;var k=angular.copy(i);a.cartResults.items.push(k),a.cartResults.state=d.lpr.processing,a.cartResults.status=e.processing,g=!0,c=i.cartId}else f.push(i)}a.cart.items=f,g&&p(c)})}function l(d,g,i,j,k,l,m){a.courseAdded=!1,h.addCourseToCart().query({cartId:d,courseCode:g,termId:i,regGroupCode:j,regGroupId:k,gradingOptionId:l,credits:m},function(b){console.log("Searched for course: "+a.courseCode+", Term: "+a.termId),a.courseCode="",a.regCode="",a.cart.items.unshift(b),console.log("Started to glow..."),b.addingNewCartItem=!0,c(function(){b.addingNewCartItem=!1},2e3),a.courseAdded=!0},function(c){console.log("CartId:",d),404===c.status?(""!==c.data&&-1!==c.data.indexOf(g)&&(c.data=c.data.replace(g,"<strong>"+g+"</strong>")),a.userMessage={txt:c.data,type:e.error},a.courseAdded=!0):400===c.status?(console.log("CartId: ",d),b.open({backdrop:"static",templateUrl:"partials/additionalOptions.html",resolve:{item:function(){return c.data},cartId:function(){return d}},controller:["$rootScope","$scope","item","cartId",function(a,b,c,d){console.log("Controller for modal... Item: ",c),b.newCartItem=c,b.newCartItem.credits=b.newCartItem.newCredits=b.newCartItem.creditOptions[0],b.newCartItem.grading=b.newCartItem.newGrading=f.letter,b.newCartItem.editing=!0,b.dismissAdditionalOptions=function(){console.log("Dismissing credits and grading"),b.$close(!0)},b.saveAdditionalOptions=function(c){c.editing=!1,console.log("Save credits and grading for cartId:",d),a.$broadcast("addCourseToCart",d,b.newCartItem.courseCode,b.newCartItem.termId,b.newCartItem.regGroupCode,b.newCartItem.regGroupId,b.newCartItem.newGrading,b.newCartItem.newCredits),b.$close(!0)}}]}),a.courseAdded=!0):(console.log("Error with adding course",c.data.consoleMessage),""!==c.data.genericMessage&&-1!==c.data.genericMessage.indexOf(g+" ("+j+")")&&(c.data.genericMessage=c.data.genericMessage.replace(g+" ("+j+")","<strong>"+g+" ("+j+")</strong>")),a.userMessage={txt:c.data.genericMessage,type:c.data.type,detail:c.data.detailedMessage},a.courseAdded=!0)})}function m(){var b=0,c=0,d=0;angular.forEach(a.cartResults.items,function(a){switch(a.status){case e.success:b++;break;case e.waitlist:case e.action:c++;break;case e.error:d++}}),a.cartResults.successCount=b,a.cartResults.waitlistCount=c,a.cartResults.errorCount=d}function n(){if(!a.cart)return 0;for(var b=0,c=0;c<a.cart.items.length;c++)b+=Number(a.cart.items[c].credits);return b}a.statuses=e,a.oneAtATime=!1,a.isCollapsed=!0;var o=!1;a.cartResults={items:[]},a.$watch("termId",function(b){console.log("term id has changed - cart"),a.cartResults.items.splice(0,a.cartResults.items.length),a.userMessage&&a.userMessage.txt&&a.removeUserMessage(),b&&(o=!0,k(b))}),a.$watchCollection("cart.items",function(b){a.creditTotal=n(),b&&(j.setCartCourseCount(b.length),j.setCartCredits(a.creditTotal))}),a.getStatusMessageFromStatus=function(a){var b="";return a===e.success?b=" - Success!":(a===e.error||a===e.action)&&(b=" - Failed"),b},a.addRegGroupToCart=function(){a.courseCode=a.courseCode.toUpperCase(),l(a.cart.cartId,a.courseCode,a.termId,a.regCode,null,null,null)},a.addCartItemToCart=function(b){l(a.cart.cartId,null,a.termId,null,b.regGroupId,b.grading,b.credits)},a.$on("addCourseToCart",function(a,b,c,d,e,f,g,h){console.log("Received event addCourseToCart ",a),l(b,c,d,e,f,g,h)}),a.cancelNewCartItem=function(){a.newCartItem=null,a.showNew=!1},a.$on("deleteCartItem",function(b,c){var d=a.cart.items[c],f=d.actionLinks,i=null;angular.forEach(f,function(a){a.action===g.removeItemFromCart&&(i=a.uri)}),h.removeItemFromCart(i).query({},function(b){a.cart.items.splice(c,1);var f=null;angular.forEach(b.actionLinks,function(a){a.action===g.undoDeleteCourse&&(f=a.uri)}),a.userMessage={txt:"Removed <b>"+d.courseCode+"("+d.regGroupCode+")</b>",actionLink:f,linkText:"Undo",type:e.success},a.userActionSuccessful=!0})}),a.invokeActionLink=function(b){a.userActionSuccessful=!1,h.invokeActionLink(b).query({},function(b){a.cart.items.unshift(b),a.userMessage={txt:""}})},a.addCartItemToWaitlist=function(a){console.log("Adding cart item to waitlist... "),i.registerForRegistrationGroup().query({courseCode:a.courseCode,regGroupId:a.regGroupId,gradingOption:a.grading,credits:a.credits,allowWaitlist:!0},function(b){a.state=d.lpr.item.processing,a.status=e.processing,a.cartItemId=b.registrationRequestItems[0].id,c(function(){},250),console.log("Just waited 250, now start the polling"),p(b.id)})},a.removeAlertMessage=function(a){a.alertMessage=null},a.removeUserMessage=function(){a.userMessage.txt=null,a.userMessage.linkText=null},a.register=function(){h.submitCart().query({cartId:a.cart.cartId},function(b){a.userMessage={txt:""},console.log("Submitted cart. RegReqId["+b.id+"]"),a.cartResults=angular.copy(a.cart),a.cart.items.splice(0,a.cart.items.length),a.showConfirmation=!1,a.cartResults.state=d.lpr.processing,a.cartResults.status=e.processing,a.creditTotal=0,angular.forEach(a.cartResults.items,function(a){a.state=d.lpr.item.processing,a.status=e.processing}),c(function(){},250),console.log("Just waited 250, now start the polling"),p(b.id)})};var p=function(b){a.pollingCart=!1,c(function(){i.getRegistrationStatus().query({regReqId:b},function(c){a.cart.state=c.state,angular.forEach(c.responseItemResults,function(b){angular.forEach(a.cartResults.items,function(c){c.cartItemId===b.registrationRequestItemId&&(c.state=b.state,c.type=b.type,c.status=j.getCorrespondingStatusFromState(b.state),c.statusMessages=b.messages),c.status===e.processing&&(a.pollingCart=!0)})}),a.pollingCart?(console.log("Continue polling"),p(b)):(console.log("Stop polling"),a.cart.status="",a.cartResults.state=d.lpr.item.succeeded,a.cartResults.successCount=0,a.cartResults.waitlistCount=0,a.cartResults.errorCount=0,m(),angular.forEach(a.cartResults.items,function(a){switch(a.status){case e.waitlist:case e.action:a.waitlistMessage=j.getCorrespondingMessageFromStatus(a.status)}}),i.getScheduleFromServer().query({termId:a.termId},function(b){console.log("called rest service to get schedule data - in cart.js"),j.updateScheduleCounts(b),a.registeredCredits=j.getRegisteredCredits,a.registeredCourseCount=j.getRegisteredCourseCount}))})},1e3)};a.removeCartResultItem=function(b){a.cartResults.items.splice(b,1),m()}}]),angular.module("regCartApp").controller("ScheduleCtrl",["$scope","$modal","$timeout","STATUS","GRADING_OPTION","ScheduleService","GlobalVarsService",function(a,b,c,d,e,f,g){a.getSchedules=g.getSchedule,a.registeredCredits=g.getRegisteredCredits,a.registeredCourseCount=g.getRegisteredCourseCount,a.waitlistedCredits=g.getWaitlistedCredits,a.waitlistedCourseCount=g.getWaitlistedCourseCount,a.numberOfDroppedWailistedCourses=0,a.userId=g.getUserId,a.$on("removeWaitlistStatusMessage",function(b,c){c.statusMessage=null,a.numberOfDroppedWailistedCourses=a.numberOfDroppedWailistedCourses-1,0===a.numberOfDroppedWailistedCourses&&(a.showWaitlistMessages=!1)}),a.$on("dropRegistered",function(b,c,e){console.log("Open drop confirmation for registered course"),f.dropRegistrationGroup().query({masterLprId:e.masterLprId},function(a){e.dropping=!1,e.dropProcessing=!0,h(a.id,e)},function(b){a.userMessage={txt:b.data,type:d.error}})}),a.$on("dropWaitlist",function(b,c,e){console.log("Open drop confirmation for waitlist course"),f.dropFromWaitlist().query({masterLprId:e.masterLprId},function(b){a.numberOfDroppedWailistedCourses=a.numberOfDroppedWailistedCourses+1,a.showWaitlistMessages=!0,e.dropping=!1,e.dropProcessing=!0,h(b.id,e)},function(a){e.statusMessage={txt:a.data,type:d.error}})});var h=function(a,b){console.log("start polling for course to be dropped from schedule"),b.statusMessage={txt:"<strong>"+b.courseCode+" ("+b.regGroupCode+")</strong> drop processing",type:d.processing},c(function(){f.getRegistrationStatus().query({regReqId:a},function(c){var e,f=g.getCorrespondingStatusFromState(c.state);switch(f){case d.new:case d.processing:console.log("continue polling"),h(a,b);break;case d.success:console.log("stop polling - success"),b.dropped=!0,b.dropProcessing=!1,b.waitlisted?(g.setWaitlistedCredits(parseFloat(g.getWaitlistedCredits())-parseFloat(b.credits)),g.setWaitlistedCourseCount(parseInt(g.getWaitlistedCourseCount())-1),e="Removed from waitlist for <strong>"+b.courseCode+" ("+b.regGroupCode+")</strong> successfully"):(g.setRegisteredCredits(parseFloat(g.getRegisteredCredits())-parseFloat(b.credits)),g.setRegisteredCourseCount(parseInt(g.getRegisteredCourseCount())-1),e="<strong>"+b.courseCode+" ("+b.regGroupCode+")</strong> dropped successfully"),b.statusMessage={txt:e,type:d.success};break;case d.error:console.log("stop polling - error"),b.dropProcessing=!1,e=c.responseItemResults[0].messages[0],b.statusMessage={txt:e,type:d.error}}})},1e3)}}]),angular.module("regCartApp").controller("SearchCtrl",["$scope","$interval","SearchService","SEARCH_FACETS",function(a,b,c,d){function e(d){if(d!==a.searchCriteria||null===d){angular.isDefined(f)&&null!==f&&(b.cancel(f),f=null);var h=a.termId;if(!h)return console.log("Search blocked - no termId exists"),void(f=b(function(){a.termId&&(b.cancel(f),e(d))},100,20));console.log('Searching for "'+d+'"'),g=d,c.searchForCourses().query({termId:h,criteria:d},function(b){g===d?(console.log('Search for "'+d+'" complete. Results: '+b.length),a.searchResults=c.filterResults(b,d),a.searchCriteria=d):console.log('Search completed but not the most recent, ignoring results: "'+d+'" !== "'+g+'"')},function(a){console.log("Error searching for courses: ",a)})}}a.facets=d,a.searchCriteria="",a.searchResults=[],a.$watch("termId",function(){a.searchCriteria="",a.searchResults=[]}),a.$on("$stateChangeSuccess",function(a,b,c){angular.isDefined(c.searchCriteria)&&e(c.searchCriteria)}),a.facetFilter=function(b){var c=!0;return angular.forEach(a.facets,function(a){c&&angular.isArray(a.selectedOptions)&&a.selectedOptions.length>0&&(a.filter(b,a.selectedOptions)||(c=!1))}),c};var f,g=""}]).controller("SearchFormCtrl",["$scope","$window",function(a,b){a.courseSearchCriteria="",a.$on("$stateChangeSuccess",function(b,c,d){angular.isDefined(d.searchCriteria)&&(a.courseSearchCriteria=d.searchCriteria)}),a.submit=function(){a.courseSearchCriteria&&(console.log("Submitting search form: "+a.courseSearchCriteria),a.goToPage("/search/"+b.encodeURIComponent(a.courseSearchCriteria)),a.searchSubmitted=!0)}}]),angular.module("regCartApp").filter("formatValidationMessage",["VALIDATION_ERROR_TYPE","MessageService",function(a,b){function c(a,b,c){var d=e(a.messageKey,c),f=[];if(a.courseCode&&f.push({masterLprId:a.masterLprId,courseCode:a.courseCode}),a.conflictingCourses&&angular.forEach(a.conflictingCourses,function(a){f.push(a)}),f.length){var g=null,h=[],i=[];b&&(angular.isDefined(b.cartItemId)?g=b.cartItemId:angular.isDefined(b.masterLprId)&&(g=b.masterLprId));for(var j=0;j<f.length;j++){if(f[j].masterLprId){if(f[j].masterLprId===g||i.indexOf(f[j].masterLprId)>=0)continue;i.push(f[j].masterLprId)}f[j].courseCode&&h.push("<strong>"+f[j].courseCode+"</strong>")}h.length&&(d+=" ("+h.join(", ")+")")}return d}function d(a,b){var c=e(a.messageKey,b);if(a.maxCredits){var d=parseFloat(a.maxCredits);c+=" (<strong>"+d+" credits</strong>)"}return c}function e(a,c){return b.getMessage(c,a)}return function(b,f,g){var h="";if(b)if("string"==typeof b)h=b;else if(b.messageKey)switch(b.messageKey){case a.timeConflict:h=c(b,f,g);break;case a.maxCredits:h=d(b,g);break;default:h=e(b.messageKey,g)}return h}}]),angular.module("regCartApp").service("CartService",["ServiceUtilities","URLS",function(a,b){this.getCart=function(){return a.getData(b.courseRegistrationCart+"/searchForCart")},this.getGradingOptions=function(){return a.getData(b.courseRegistrationCart+"/getStudentRegistrationOptions")},this.addCourseToCart=function(){return a.postData(b.courseRegistrationCart+"/addCourseToCart")},this.removeItemFromCart=function(b){return a.deleteData(b)},this.invokeActionLink=function(b){return a.getData(b)},this.updateCartItem=function(){return a.putData(b.courseRegistrationCart+"/updateCartItem")},this.submitCart=function(){return a.getData(b.courseRegistrationCart+"/submitCart")},this.undoDeleteCourse=function(){return a.getData(b.courseRegistrationCart+"/undoDeleteCourse")}}]),angular.module("regCartApp").service("TermsService",["ServiceUtilities","URLS","DEFAULT_TERM",function(a,b,c){var d=c;this.checkStudentEligibilityForTerm=function(){return a.getData(b.scheduleOfClasses+"/checkStudentEligibilityForTerm")},this.getTermId=function(){return d},this.setTermId=function(a){d=a},this.getTermsFromServer=function(){return a.getArray(b.scheduleOfClasses+"/terms")},this.getTermNameForTermId=function(a,b){var c;return angular.forEach(a,function(a){a.termId===b&&(c=a.termName)}),c}}]),angular.module("regCartApp").service("ScheduleService",["ServiceUtilities","URLS",function(a,b){this.getScheduleFromServer=function(){return a.getData(b.courseRegistration+"/personschedule")},this.updateScheduleItem=function(){return a.putData(b.courseRegistration+"/updateScheduleItem")},this.updateWaitlistItem=function(){return a.putData(b.courseRegistration+"/updateWaitlistEntry")},this.dropRegistrationGroup=function(){return a.deleteData(b.courseRegistration+"/dropRegistrationGroup")},this.dropFromWaitlist=function(){return a.deleteData(b.courseRegistration+"/dropFromWaitlistEntry")},this.registerForRegistrationGroup=function(){return a.getData(b.courseRegistration+"/registerreggroup")},this.getRegistrationStatus=function(){return a.getData(b.courseRegistration+"/getRegistrationStatus")}}]),angular.module("regCartApp").service("LoginService",["ServiceUtilities","URLS",function(a,b){this.logOnAsAdmin=function(){return a.getData(b.developmentLogin+"/login")},this.logout=function(){return a.getData(b.developmentLogin+"/logout")}}]),angular.module("regCartApp").service("MessageService",["$resource",function(a){this.getMessages=function(){return a("json/messages.json",{},{query:{method:"GET",cache:!1,isArray:!0}})},this.getMessage=function(a,b){var c="";return angular.forEach(a,function(a){a.messageKey===b&&(c=a.message)}),c}}]),angular.module("regCartApp").service("SearchService",["ServiceUtilities","URLS","$resource",function(a,b,c){function d(){return c("json/static-search-results.json",{},{query:{method:"GET",cache:!1,isArray:!0}})}this.searchForCourses=function(){return d()},this.filterResults=function(a,b){var c=[];return b=b.toLowerCase(),angular.forEach(a,function(a){(a.courseCode.toLowerCase().indexOf(b)>-1||a.longName.toLowerCase().indexOf(b)>-1)&&c.push(a)}),c}}]),angular.module("regCartApp").factory("loginInterceptor",["$q","$injector","$window","$rootScope",function(a,b,c,d){return{response:function(b){var c=b.data;return"object"!=typeof c&&c.indexOf("Kuali Student Login")>-1?(console.log("Informing user that session has expired..."),d.$broadcast("sessionExpired"),a.reject(b)):b},responseError:function(d){if(0===d.status){console.log("Failed to execute request - trying to login");var e=b.get("LoginService");e.logOnAsAdmin().query({userId:"admin",password:"admin"},function(){console.log("Logged in, reloading page."),c.location.reload()},function(){console.log("Not Logged in, reloading page."),c.location.reload()})}return a.reject(d)}}}]),angular.module("regCartApp").service("GlobalVarsService",["STATE","STATUS",function(a,b){var c,d,e,f=0,g=0,h=0,i=0,j=0;this.getCartCredits=function(){return f},this.setCartCredits=function(a){f=a},this.getCartCourseCount=function(){return g},this.setCartCourseCount=function(a){g=a},this.getRegisteredCredits=function(){return c},this.setRegisteredCredits=function(a){c=a},this.getRegisteredCourseCount=function(){return h},this.setRegisteredCourseCount=function(a){h=a},this.getWaitlistedCredits=function(){return i},this.setWaitlistedCredits=function(a){i=a},this.getWaitlistedCourseCount=function(){return j},this.setWaitlistedCourseCount=function(a){j=a},this.getSchedule=function(){return d},this.setSchedule=function(a){d=a},this.getUserId=function(){return e},this.setUserId=function(a){e=a},this.getCorrespondingStatusFromState=function(c){var d=b.new;return a.processing.indexOf(c)>=0?d=b.processing:a.success.indexOf(c)>=0?d=b.success:a.error.indexOf(c)>=0?d=b.error:a.waitlist.indexOf(c)>=0?d=b.waitlist:a.action.indexOf(c)>=0&&(d=b.action),d},this.updateScheduleCounts=function(a){var b=a.studentScheduleTermResults,c=a.userId,d=0,e=0,f=0,g=0;this.setSchedule(b),angular.forEach(b,function(a){angular.forEach(a.registeredCourseOfferings,function(a){d+=parseFloat(a.credits),e++;var b=0;angular.forEach(a.gradingOptions,function(){b++}),a.gradingOptionCount=b}),angular.forEach(a.waitlistCourseOfferings,function(a){f+=parseFloat(a.credits),g++;var b=0;angular.forEach(a.gradingOptions,function(){b++}),a.gradingOptionCount=b})}),this.setRegisteredCourseCount(e),this.setRegisteredCredits(d),this.setWaitlistedCredits(f),this.setWaitlistedCourseCount(g),this.setUserId(c)},this.getCorrespondingMessageFromStatus=function(a){var c="";return a===b.waitlist&&(c="If a seat becomes available you will be registered automatically"),c}}]),angular.module("regCartApp").service("ServiceUtilities",["$resource","APP_URL",function(a,b){function c(a){var b=[];for(var c in a)a[c]&&b.push(encodeURIComponent(c)+"="+encodeURIComponent(a[c]));return b.join("&")}this.getData=function(c){return a(b+c,{},{query:{method:"GET",cache:!1,isArray:!1}})},this.deleteData=function(c){return a(b+c,{},{query:{method:"DELETE",cache:!1,isArray:!1}})},this.postData=function(d){return a(b+d,{},{query:{headers:{"Content-Type":"application/x-www-form-urlencoded; charset=UTF-8"},method:"POST",cache:!1,isArray:!1,transformRequest:function(a){return c(a)}}})},this.putData=function(d){return a(b+d,{},{query:{headers:{"Content-Type":"application/x-www-form-urlencoded; charset=UTF-8"},method:"PUT",cache:!1,isArray:!1,transformRequest:function(a){return c(a)}}})},this.getArray=function(c){return a(b+c,{},{query:{method:"GET",cache:!1,isArray:!0}})}}]),angular.module("regCartApp").directive("courseCard",function(){return{transclude:!0,scope:{schedules:"=",credits:"=",type:"@"},templateUrl:"partials/courseCard.html",controller:"CardCtrl"}}).directive("courseAccordion",function(){return{restrict:"E",transclude:!0,scope:{course:"=",type:"@",cardIndex:"=",cartId:"="},templateUrl:"partials/courseAccordion.html",controller:"CardCtrl"}}).controller("CardCtrl",["$scope","$timeout","GlobalVarsService","CartService","ScheduleService","STATUS","GRADING_OPTION",function(a,b,c,d,e,f,g){function h(){var b;switch(a.type){case"waitlist":b={heading:"Waitlisted",prefix:"waitlisted",prefix2:"waitlist_",prefix3:"waitlist_",remove:"Remove"};break;case"cart":b={heading:"Cart",prefix:"cart",prefix2:"",prefix3:"cart_",remove:"Remove"};break;default:b={heading:"Registered",prefix:"reg",prefix2:"",prefix3:"schedule_",remove:"Drop"}}return b}function i(b){console.log("Updating registered course:"),console.log(b.newCredits),console.log(b.newGrading),e.updateScheduleItem().query({courseCode:b.courseCode,regGroupCode:b.regGroupCode,masterLprId:b.masterLprId,termId:a.termId,credits:b.newCredits,gradingOptionId:b.newGrading},function(a){c.setRegisteredCredits(parseFloat(c.getRegisteredCredits())-parseFloat(b.credits)+parseFloat(a.credits)),l(b,a)},function(a){b.statusMessage={txt:a.data,type:f.error}})}function j(b){console.log("Updating waitlisted course:"),console.log(b.newCredits),console.log(b.newGrading),e.updateWaitlistItem().query({courseCode:b.courseCode,regGroupCode:b.regGroupCode,masterLprId:b.masterLprId,termId:a.termId,credits:b.newCredits,gradingOptionId:b.newGrading},function(a){c.setWaitlistedCredits(parseFloat(c.getWaitlistedCredits())-parseFloat(b.credits)+parseFloat(a.credits)),l(b,a)},function(a){b.statusMessage={txt:a.data,type:f.error}})}function k(b){console.log("Updating cart item. Grading: "+b.newGrading+", credits: "+b.newCredits),d.updateCartItem().query({cartId:a.cartId,cartItemId:b.cartItemId,credits:b.newCredits,gradingOptionId:b.newGrading},function(a){console.log("old: "+b.credits+" To: "+a.credits),console.log("old: "+b.gradingOptionId+" To: "+a.gradingOptionId),l(b,a)})}function l(c,d){console.log(d);var e=c.credits,f=c.gradingOptionId;switch(c.credits=d.credits,a.type){case"cart":c.gradingOptionId=d.grading,c.grading=c.gradingOptionId;break;default:c.gradingOptionId=d.gradingOptionId}c.editing=!1,c.isopen=!c.isopen,c.newGrading!==f&&(c.editGradingOption=!0,b(function(){c.editGradingOption=!1},2e3)),c.newCredits!==e&&(c.editCredits=!0,b(function(){c.editCredits=!1},2e3))}a.config=h(),"cart"===a.type&&(a.course.gradingOptionId=a.course.grading),a.courseOfferings=function(b){var c;switch(a.type){case"waitlist":c=b.waitlistCourseOfferings;break;default:c=b.registeredCourseOfferings}return c},a.dropCourse=function(b,c){switch(console.log("course-card index: "+b),a.type){case"cart":a.$emit("deleteCartItem",b);break;default:console.log("Open drop confirmation"),c.dropping=!0,a.index=b,a.course=c}},a.cancelDropConfirmation=function(a){a.dropping=!1},a.removeStatusMessage=function(a){a.statusMessage=null},a.showBadge=function(a){return a.gradingOptionId!==g.letter||a.editGradingOption},a.editItem=function(a){a.newCredits=a.credits,a.newGrading=a.gradingOptionId,a.editing=!0},a.updateItem=function(b){switch(a.type){case"registered":i(b);break;case"waitlist":j(b);break;case"cart":k(b)}},a.removeWaitlistStatusMessage=function(b){a.$emit("removeWaitlistStatusMessage",b)},a.dropRegistrationGroup=function(b,c){a.$emit("dropRegistered",b,c)},a.dropFromWaitlist=function(b,c){a.$emit("dropWaitlist",b,c)},a.gradingOption=function(b){return b.gradingOptions[a.course.gradingOptionId]},a.courseTitle=function(b){var c;switch(a.type){case"cart":c=b.courseTitle;break;default:c=b.longName}return c}}]),angular.module("regCartApp").directive("courseOptions",function(){return{restrict:"E",transclude:!0,scope:{course:"=",maxOptions:"@max",prefix:"@",showAll:"@",moreButtonSelectBehavior:"@moreBehavior",cancelFn:"&onCancel",submitFn:"&onSubmit"},templateUrl:"partials/courseOptions.html",controller:["$scope","$modal",function(a,b){function c(a,b,c){if(a.length<=g)return!0;var d=a.indexOf(b),e=a.indexOf(c),f=2,h=Math.max(0,Math.min(d-f,a.length-g)),i=Math.min(h+g,a.length)-1;return e>=h&&i>=e}function d(){var c=a.$new();c.course=angular.copy(f),c.cancel=function(){},c.submit=function(){},f.editing=!1;var d=b.open({backdrop:"static",template:'<div class="kscr-AdditionalOptions"><course-options course="course" show-all="true" max="'+g+'" prefix="modal_'+(a.prefix?a.prefix:"")+'" on-submit="modalSubmit()" on-cancel="modalCancel()"></course-options></div>',scope:c,controller:["$scope",function(a){a.showAllCreditOptions=!0,a.showAllGradingOptions=!0,a.modalCancel=function(){a.$dismiss("cancel")},a.modalSubmit=function(){a.$close(a.course)}}]});d.result.then(function(b){f.newGrading=b.newGrading,f.newCredits=b.newCredits,a.submit()},function(){a.cancel()})}function e(){a.showAllCreditOptions=h,a.showAllGradingOptions=h}var f=a.course,g=a.maxOptions||4,h=a.showAll?!0:!1,i=a.moreButtonSelectBehavior||"expand";a.showAllCreditOptions=h,a.showAllGradingOptions=h,a.gradingOptions=[],f&&f.gradingOptions&&angular.forEach(f.gradingOptions,function(a,b){this.push({key:b,label:a})},a.gradingOptions),a.creditOptionsFilter=function(b){return!f||a.showAllCreditOptions?!0:c(f.creditOptions,f.credits,b)},a.gradingOptionsFilter=function(b){return!f||a.showAllGradingOptions?!0:c(Object.keys(f.gradingOptions),f.grading,b.key)},a.showMoreCreditOptions=function(){"expand"===i?a.showAllCreditOptions=!0:d()},a.showMoreGradingOptions=function(){"expand"===i?a.showAllGradingOptions=!0:d()},a.shouldShowMoreCreditOptionsToggle=function(){return!a.showAllCreditOptions&&f.creditOptions.length>g},a.shouldShowMoreGradingOptionsToggle=function(){return!a.showAllGradingOptions&&Object.keys(f.gradingOptions).length>g},a.cancel=function(){console.log("Canceling options changes"),f.newCredits=f.credits,f.newGrading=f.grading||f.gradingOptionId,f.status="",f.editing=!1,a.cancelFn&&a.cancelFn({course:f}),e()},a.submit=function(){console.log("Submitting options form"),a.submitFn&&a.submitFn({course:f}),e()},a.showGradingHelp=function(){b.open({templateUrl:"partials/gradingOptionsHelp.html"})}}]}}),angular.module("regCartApp").directive("focusMe",["$timeout","$parse",function(a,b){return{link:function(c,d,e){var f=b(e.focusMe);c.$watch(f,function(b){b===!0&&a(function(){d[0].focus()})}),d.bind("blur",function(){a(function(){d[0].focus()})})}}}]).directive("focusOnce",["$timeout","$parse",function(a,b){return{link:function(c,d,e){var f=b(e.focusOnce);c.$watch(f,function(b){b===!0&&a(function(){d[0].focus()})})}}}]).directive("dropMenu",["$window",function(a){return{controller:["$scope",function(b){return angular.element(a).bind("resize",function(){b.dropMenu===!0&&(b.dropMenu=!1)})}],templateUrl:"dropMenu.html"}}]),angular.module("regCartApp").directive("searchFacet",[function(){return{restrict:"ECA",scope:{facet:"=",results:"="},templateUrl:"partials/searchFacet.html",controller:["$scope",function(a){function b(b){angular.isFunction(a.facet.prepare)&&a.facet.prepare(b),a.options=a.facet.optionsProvider(b),a.facet.selectedOptions=[]}angular.isFunction(a.facet.optionsProvider)||(a.facet.optionsProvider=function(b){var c=[];return angular.isDefined(a.facet.optionsKey)&&null!==a.facet.optionsKey?angular.forEach(b,function(b){if(angular.isDefined(b[a.facet.optionsKey])){var d=b[a.facet.optionsKey];angular.isArray(d)||(d=[d]),angular.forEach(d,function(a){var b=null;
angular.forEach(c,function(c){null===b&&c.value===a&&(b=c)}),null===b&&(b={label:a,value:a,count:0},c.push(b)),b.count++})}}):console.log('Facet "'+facet.id+'" is missing the required optionsKey value'),c}),angular.isFunction(a.facet.filter)||(a.facet.filter=function(b,c){if(angular.isDefined(a.facet.optionsKey)&&null!==a.facet.optionsKey&&angular.isDefined(b[a.facet.optionsKey])){var d=b[a.facet.optionsKey];if(angular.isArray(d)){var e=!1;return angular.forEach(d,function(a){e||-1===c.indexOf(a)||(e=!0)}),e}return-1!==c.indexOf(d)}return!1}),a.options=[],a.facet.selectedOptions=[],a.$watch("results",function(a){console.log("Search Results changed, updating facets"),b(a)}),a.clearSelectedOptions=function(){a.facet.selectedOptions=[]},a.hasSelected=function(){return a.facet.selectedOptions.length>0},a.isSelected=function(b){return-1!==a.facet.selectedOptions.indexOf(b.value)},a.toggleOption=function(b){var c=a.facet.selectedOptions.indexOf(b.value);-1===c?a.facet.selectedOptions.push(b.value):a.facet.selectedOptions.splice(c,1)}}]}}]),angular.module("regCartApp").directive("sticky",["$timeout","$window","$document",function(a,b,c){return{restrict:"CA",scope:{},link:function(d,e){a(function(){function d(){var a=(b.pageYOffset||j.scrollTop())-(j.clientTop||0),c=a>=k;!l&&c?f():l&&!c&&g()}function f(){h||(h=angular.element('<div class="util-sticky-placeholder"/>')),h.css("height",e.outerHeight()+"px"),h.insertBefore(e),e.addClass("util-sticky--stuck"),l=!0}function g(){h&&(h.remove(),h=null),e.removeClass("util-sticky--stuck"),l=!1}var h,i=angular.element(b),j=angular.element(c),k=e.offset().top,l=!1;e.addClass("util-sticky"),i.on("scroll",d),i.on("resize",function(){a(d)}),d(),e.bind("$destroy",function(){h&&(h.remove(),h=null)})})}}}]);