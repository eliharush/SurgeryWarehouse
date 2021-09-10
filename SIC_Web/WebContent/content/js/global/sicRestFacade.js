SICRestFacade=function(){
		
	
		var isConnected=null;
		this.continueConTest=true;
		
		this.startConnectionTesting=function(){
			this.continueConTest=true;
			this.testConnected();
		};
		
		this.stopConnectionTesting=function(){
			this.continueConTest=false;
		};
		
		this.AJAX_REQUEST_TYPE= {
			GET		: "GET",
			POST	: "POST",
			PUT		: "PUT",
			DELETE	: "DELETE"
		};
		
		this.AJAX_CONTENT_TYPE= {
			JSON	: "application/json",
			FORM	: "application/x-www-form-urlencoded"
		};
		this.REST_SERVER_PREFIX=sicServer + "/rest";	
		this.REST_END_POINT={
			IS_DATA_FRESH:this.REST_SERVER_PREFIX + "/import/last",	
			IMPORT_TABLES:this.REST_SERVER_PREFIX + "/import",
			HOSPITAL:this.REST_SERVER_PREFIX + "/hospital",
			CUSTOMER:"/customer",
			GENERAL:"/general",
			ICOUNT:this.REST_SERVER_PREFIX +"/count",
			HISTORY:this.REST_SERVER_PREFIX + "/general/history",
			TRANSMIT:this.REST_SERVER_PREFIX + "/transmit",
			APPROVERS:this.REST_SERVER_PREFIX  + "/general/approvers",
			MOCK_REST :this.REST_SERVER_PREFIX + "/test/interest",
			TEST_CONNECTION:this.REST_SERVER_PREFIX +"/import/connected",
			GENERAL_PRODUCT:"/generalProduc",
			SEARCH_CUSTOMER:this.REST_SERVER_PREFIX + "/hospital/{hospitalId}/customer/{custNo}",
			SEARCH_MAN_PRODUCTS:this.REST_SERVER_PREFIX + "/hospital/{hospitalId}/customer/{custNo}/manProducts",
			SEARCH_MARA_MAN_PRODUCTS:this.REST_SERVER_PREFIX + "/hospital/{hospitalId}/customer/{custNo}/maraManProducts",
			SEARCH_CUST_PRODUCTS: this.REST_SERVER_PREFIX + "/hospital/{hospitalId}/customer/{custNo}/products",
			SEARCH_HOSPITAL_PRODUCTS: this.REST_SERVER_PREFIX + "/hospital/{hospitalId}/customer/{productId}/hospitalProducts",
			GET_COUNT:this.REST_SERVER_PREFIX +"/count/{countId}",
			RESTART_COUNT:this.REST_SERVER_PREFIX +"/count/{countId}?restart=true",
			DELETE_COUNT:this.REST_SERVER_PREFIX +"/count/{countId}?restart=true&type={countType}&deleteCurrent=true",
			START_COUNT:this.REST_SERVER_PREFIX +"/count?hospitalId={hospitalId}&customerId={customerId}&type={type}",
			SAVE_COUNT:this.REST_SERVER_PREFIX +"/count/{countId}?finish={finish}",
			CANCEL_COUNT:this.REST_SERVER_PREFIX +"/count/{countId}",
			GET_GENERAL_PRODUCT:this.REST_SERVER_PREFIX + "/general/generalProduc/{custProductId}",
			COUNT_HISTORY:this.REST_SERVER_PREFIX +"/count/{countId}/history",
			GET_ORDER:this.REST_SERVER_PREFIX +"/order/{orderId}",
			START_ORDER:this.REST_SERVER_PREFIX +"/order?hospitalId={hospitalId}&customerId={customerId}&type={type}",
			SAVE_ORDER:this.REST_SERVER_PREFIX +"/order/{orderId}?finish={finish}",
			CANCEL_ORDER:this.REST_SERVER_PREFIX +"/order/{orderId}",
			ORDER_HISTORY:this.REST_SERVER_PREFIX +"/order/{orderId}/history"
			
		};
		
		
		
		/**
		 * Converts form data to Json
		 * @param oData - the form
		 * @param oFieldsToIgnore - an array of fields to ignore (do not JSON them)
		 * @returns JSON object
		 */
		 function convertFormToObject (oData, oFieldsToIgnore){
			var oJson = {};
			//take all fields which are not readonly, file or radio 
			//Note ! radio should be set to a  hidden text before sendign form 
			$(':input:not([readonly="readonly"]):not([type="file"]):not([type="radio"])', oData).each(function() {
				var bIgnore = false;
				if ($(this).prop('name') == oConstants.EMPTY_STRING) {
					//no name - ignore it
					bIgnore = true;
				}
				else if (oFieldsToIgnore && oFieldsToIgnore.length > 0) {
					//found it in the ignore list - ignore it :-)
					for (var i=0; i<oFieldsToIgnore.length; i++) {
						if ($(this).prop('name') == oFieldsToIgnore[i]) {
							bIgnore = true;
							break;
						}
					}
				}
				if (!bIgnore) {
					//set json key value
					oJson[$(this).prop('name')] = ($(this).val()==oConstants.EMPTY_STRING || isNaN($(this).val())) ? $(this).val() : parseInt($(this).val());
				}
		    });
			return oJson;
		};
		
		/**
		 * Converts a form data to JSON String
		 * @param oData - the form data
		 * @param oFieldsToIgnore - array of fields to ignore
		 * @returns Json string
		 */
		function convertFormToJson(oData, oFieldsToIgnore){
			//var oJson=convertFormToObject(oData, oFieldsToIgnore);
//			return $.toJSON(oJson);
			return JSON.stringify(oData);
		};
		
		/**
		 * Sends an AJax request to the REST Endpoint
		 * @param sUrl - url to the REST endpoint
		 * @param sType - type of request (GET/POST/DELETE/PUT)
		 * @param sContentType - type of content (json/form etc...) 
		 * @param fSuccessFunction - handler for success
		 * @param fFailureFunction - handler for failure
		 * @param oData - data to transfer
		 * @param oFieldsToIgnore - fields to ignore in data
		 */
		this.sendAjaxRequest=function(sUrl, sType, sContentType, fSuccessFunction, fFailureFunction, oData, oFieldsToIgnore,async) {
			var sData = ""; 
			if (oData) {
				if (typeof oData == 'string') {
					sData = oData;
				}
				else {
					if (oData.elements) {
						// oData is a form - convert it to Json string
						sData = convertFormToJson(oData, oFieldsToIgnore);
					}
					else {
						// oData is Json - convert to string
						//sData = $.toJSON(oData);
						sData = JSON.stringify(oData);
					}
				}
			}		
			var bAsync=(async==undefined) ? true : async;
			var failHandler=(fFailureFunction==undefined || fFailureFunction==null) ? this.handleAjaxError : fFailureFunction;
			$.ajax({
				url:sUrl,
				type:sType,
				data:sData,
				contentType:sContentType,
				success:fSuccessFunction,
				error:failHandler,
				async:bAsync
				/*'beforeSend': function(xhr) {
					var iUserId=(oUserProfile!=null) ? oUserProfile.id : 0;
					var key= Crypto.util.bytesToBase64(Crypto.charenc.Binary.stringToBytes(iUserId+''));
					xhr.setRequestHeader("Authorization", "WF_JS " + key);
					var location=oConstants.DEFAULT_COUNTRY_ID + ";" + oConstants.DEFAULT_STATE_ID;
					location=Crypto.util.bytesToBase64(Crypto.charenc.Binary.stringToBytes(location));
					xhr.setRequestHeader("WF_I18N", location);
				}*/
			});	
		};
		
		/**
		 * Default handler to handle ajax request failure
		 * @param jqXHR
		 * @param textStatus
		 * @param errorThrown
		 */
		this.handleAjaxError=function(jqXHR, textStatus, errorThrown){
			var status=parseInt(jqXHR.status);
			alert("ajax error with status ["+status+"]");
			//TODO:add dialog impl
			//oDialog.showDialog(oDialog.TYPE.ERROR,{message:getErrorMessage(status)});
		};
		
		this.testConnected=function(){
			
			restManager.sendAjaxRequest(restManager.REST_END_POINT.TEST_CONNECTION, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, function(data){
				restManager.setConnected(eval(data));
				if(restManager.isConnected()){
					$('#connectionStat').addClass("connected").removeClass("disconnected").html("מחובר");
				}
				else{
					$('#connectionStat').addClass("disconnected").removeClass("connected").html("מנותק");
				}
				if(restManager.continueConTest){
					setTimeout(restManager.testConnected, 20000);
				}
			}, function(){
				restManager.setConnected(false);
				$('#connectionStat').addClass("disconnected").removeClass("connected").html("מנותק");
				if(restManager.continueConTest){
					setTimeout(restManager.testConnected, 20000);
				}
			});
		};
		this.isConnected=function(){
			return isConnected;
		};
		this.setConnected=function(connected){
			isConnected=connected;
		};
		
		/**
		 * default messages for ajax request failure
		 * @param status
		 * @returns
		 */
		function getErrorMessage(status){
			switch(status){
				case 400:return jQuery.i18n.prop('strings.error.clientMessage.badRequest');
				break;
				case 403:return jQuery.i18n.prop('strings.error.clientMessage.forbidden');
				break;
				case 404:return jQuery.i18n.prop('strings.error.clientMessage.notFound');
				break;
				case 500:return jQuery.i18n.prop('strings.error.clientMessage.serverError');
				break;
				default:return jQuery.i18n.prop('strings.error.clientMessage.globalError');
			}
		};
};


var restManager=new SICRestFacade();
restManager.startConnectionTesting();