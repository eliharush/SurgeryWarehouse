var isFresh;
var isLoading=false;
var interval;
var MAX_PROGRESS=290;
var PROGRESS_AMOUNT=50;

$(function(){
	
	$('.menu-item').click(menuItemClicked);
	
	$('#loadCloseButton').click(closeLoading).html('סגירה');
	$('#loadLoadButton').click(loadTables).html('טעינה');
	
	$("#loadPopup").dialog({
		dialogClass:'loading',
		title:'טעינה',
		autoOpen:false,
		resizable: false,
        height:230,
        width:329,
        modal: true,
        buttons: {
            "טעינה":loadTables,
            "סגור": function(){$(this).dialog("close");}
        },
        beforeClose:closeLoading,
        open: function() {
            $buttonPane = $(this).next();
            $buttonPane.find('button:first').addClass('dialog-button').addClass('close-button2');
            $buttonPane.find('button:last').addClass('dialog-button').addClass('load-button');                        
        }
    });
	
	$("#warnPopup").dialog({
		dialogClass:'warning',
		title:'אזהרה',
		autoOpen:false,
		resizable: false,
		height:200,
		width:250,
		modal: true,
		open: function() {
			$buttonPane = $(this).next();
			$buttonPane.find('button:first').addClass('dialog-button').addClass('ok-button-2');
		}
	});
	
	isDataFresh();
});

function isDataFresh(){
	restManager.sendAjaxRequest(restManager.REST_END_POINT.IS_DATA_FRESH, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxIsFreshOk);
};

function ajaxIsFreshOk(data, textStatus, jqXHR){
	isFresh=eval(data);
}

function menuItemClicked(){
	var linkName=$(this).find('ul').attr('class');
	var menuItemNo=parseInt(linkName.substring(4),10);
	switch (menuItemNo){
	case 1:
		if(!restManager.isConnected()){
			$('#warnPopup #msg').html("לא ניתן לבצע את הפעולה, אנא פנה למנהל מערכת");
			$('#warnPopup').dialog("option","buttons",[{text:"אישור",click:function(){
				$(this).dialog("close");
			}}]).dialog("open");
			return;
		}
		openLoading();
		break;
	case 2:
		if(!isFresh){
			$('#warnPopup #msg').html("נתוני המערכת אינם מעודכנים. עליך לבצע טעינה.");
			$('#warnPopup').dialog("option","buttons",[{text:"אישור",click:function(){
				$(this).dialog("close");
			}}]).dialog("open");
			break;
		}
		location.href="counting.html";
		break;
	case 3:location.href="transmit.html";
		break;
	case 4:location.href="history.html";
		break;
	case 5:location.href="order.html";
		break;
	}	
};

function openLoading(){
	$('#progresser').width(0);
	$('#loadText').html("&nbsp;");
	$( "#loadPopup" ).dialog("open");
}

function closeLoading(){
	if(!isLoading){
		return true;
	}
	return false;
}

function loadTables(){
	if(isLoading){
		return;
	}
	$('#progresser').width(0);
	$('#loadText').html("טוען טבלאות...");
	isLoading=true;
	restManager.sendAjaxRequest(restManager.REST_END_POINT.IMPORT_TABLES, restManager.AJAX_REQUEST_TYPE.POST, restManager.AJAX_CONTENT_TYPE.JSON, ajaxLoadOk,ajaxLoadingFailed);
	setTimeout(setProgress, 500);
	
}

function setProgress(){
	if(!isLoading){
		return;
	}
	var progress=$('#progresser').width();
	if(progress>=MAX_PROGRESS){
		$('#progresser').width(0);
		progress=0;
	}
	if(progress+PROGRESS_AMOUNT>MAX_PROGRESS){
		progress=MAX_PROGRESS;
	}
	else{
		progress+=PROGRESS_AMOUNT;
	}
	$('#progresser').animate({width:progress},1000,function(){
		if(isLoading){	
		setProgress();
		}
		
	});
	
}

function ajaxLoadOk(){
	isLoading=false;
	setTimeout(function(){ 
		$('#progresser').width(290);
		setTimeout(function(){$('#loadPopup').dialog("close");},500);
	},1000);
	$('#loadText').html('טעינה הושלמה בהצלחה.');
	isDataFresh();
	
}

function ajaxLoadingFailed(){
	isLoading=false;
	setTimeout(function(){ $('#progresser').width(290);},1000);
	$('#loadText').html('לא ניתן לבצע את הפעולה, אנא פנה למנהל מערכת');
}