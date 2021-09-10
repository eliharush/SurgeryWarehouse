var sicServer=window.location.protocol + '//' + window.location.host;
var hospitalId="0000000193";//For current state

var sicGeneralUtil={
		isValidEmailAddress:function(emailAddress) {
			var pattern = new RegExp(/^(("[\w-+\s]+")|([\w-+]+(?:\.[\w-+]+)*)|("[\w-+\s]+")([\w-+]+(?:\.[\w-+]+)*))(@((?:[\w-+]+\.)*\w[\w-+]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i);
			return pattern.test(emailAddress);
		},
		trim:function(stringToTrim) {
			return (stringToTrim!=null) ? stringToTrim.replace(/^\s+|\s+$/g,"") : stringToTrim;
		},
		clearForm:function(oForm) {
			$(':input', oForm)
			 .not(':button, :submit, :reset')
			 .filter(function(){if(this.type!='hidden'){return true;}})
			 .val('')
			 .removeAttr('checked')
			 .removeAttr('selected');
		},
		enableForm:function(oForm, bEnable) {
			$(':input', oForm)
			 .not(':button, :submit, :reset')
			 .filter(function(){if(this.type!='hidden'){return true;}})
			 .attr("disabled",!bEnable);
		},
		createErrorField:function(el,msg){
			$('body').append($('<div/>',{
				'id':$(el).attr("id") + "error",
				'class':'error',
				'text':msg
			}).css("position","absolute").css("left",$(el).offset().left).css("top",$(el).offset().top -10));
		},
		clearErrorMessages:function() {
			$('.error').remove();
		},
		getUrlVars:function()
		{
		    var vars = [], hash;
		    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
		    for(var i = 0; i < hashes.length; i++)
		    {
		        hash = hashes[i].split('=');
		        vars.push(hash[0]);
		        vars[hash[0]] = hash[1];
		    }
		    return vars;
		},
		cutTextLength:function(sText, nMaxLength) {
			if (sText.length > nMaxLength) {
				sText = sText.substring(0, nMaxLength-3) + "...";
			}
			return sText;
		},
		isIE:function() {
			return $.browser.msie;
		},
		isIE8orLess:function(){
			return isIE() && parseInt($.browser.version, 10)<9; 
		},
		isIE7orLess:function(){
			return isIE() && parseInt($.browser.version, 10)<8; 
		},
		isIE9orGreater:function(){
			return isIE() && parseInt($.browser.version, 10)>=9; 
		},
		getProtocol:function() {
			var protocol = location.protocol;
			return protocol.substring(0, protocol.length-1);
		},
		createFade:function(){
			$('body').append('<div id="fade"></div>'); 
		    $('#fade').css({'filter':'alpha(opacity=70)'}).fadeIn();
		},
		
		destroyFade:function(){
			$('#fade').fadeOut().remove();
		},
		padd:function(str,requiredLength,padChar,front){
			str=str.toString();
			if(str.length<requiredLength){
				var currLen=str.length;
				for(var i=0;i<(requiredLength-currLen);i++){
					if(front){
						str+=padChar;
					}
					else{
						str=padChar+str;
					}
				}
			}
			return str;
		},
		getStatusText:function(status){
			for(var i=0;i<inventoryCountStatus.length;i++){
				if(inventoryCountStatus[i].id==status){
					return inventoryCountStatus[i].name;
				}
			}
		}
};

jQuery.fn.forceNumericOnly = function() {
    return this.each(function()
    {
        $(this).keydown(function(e)
        {
        	nPrevNumericValue=$(this)[0].value;
            var key = e.charCode || e.keyCode || 0;
            // allow backspace, tab, delete, arrows, numbers and keypad numbers ONLY
            return (
                key == 8 || 
                key == 9 ||
                key == 13 ||
                key == 46 ||
                (key >= 37 && key <= 40) ||
                (key >= 48 && key <= 57) ||
                (key >= 96 && key <= 105));
        });
    });
};

jQuery.fn.limitNumericText = function(limit){
	return this.each(function(){
		$(this).keyup(function(e){	
			/*var key = e.charCode || e.keyCode || 0;
			if(!((key >= 48 && key <= 57) || (key >= 96 && key <= 105))){
				return true;
			}*/
			var value=$(this)[0].value;
			value=(!isNaN(value) && value!="") ? parseInt(value) : 0;
			if (value > limit){
				$(this).val(nPrevNumericValue);
			}
		});
	});
	
	
};

var inventoryCountStatus=[
		{id:"IN_PROGRESS",name:"בעבודה"},
		{id:"COMPLETED",name:"אושרה"},
		{id:"TRANSMITED",name:"שודרה"},
		{id:"CANCELED",name:"בוטלה"},
		{id:"WO_ORDER",name:"ללא הזמנה"}
];


