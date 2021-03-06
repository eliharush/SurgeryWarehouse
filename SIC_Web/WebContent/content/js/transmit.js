var entities;
var isTransmiting;
var interval;
var MAX_PROGRESS=290;
var PROGRESS_AMOUNT=10;

$(function(){
	$('#homeButton').click(function(){
		location.href="index.html";
	});
	
	$('#transmitButton').click(transmit);

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

	$("#errorPopup").dialog({
		dialogClass:'error',
		title:'שגיאה',
		autoOpen:false,
		resizable: false,
		height:200,
		width:250,
		modal: true,
		buttons: {
			"אישור":function(){
				$(this).dialog("close");
			}
		},
		open: function() {
			$buttonPane = $(this).next();
			$buttonPane.find('button:first').addClass('dialog-button').addClass('ok-button-2');
		}
	});

	$("#infoPopup").dialog({
		dialogClass:'info',
		title:'הודעה',
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
	
	

	$("#itemsPopup").dialog({
		dialogClass:'itemsForCount',
		autoOpen:false,
		resizable: false,
        height:385,
        width:605,
        modal: true
    });
	
	$("#progressPopup").dialog({
		dialogClass:'transmiting',
		title:'שידור',
		autoOpen:false,
		resizable: false,
        height:230,
        width:329,
        modal: true,
        buttons: {
            "סגור": function(){$(this).dialog("close");}
        },
        beforeClose:closeProgress,
        open: function() {
            $buttonPane = $(this).next();
            $buttonPane.find('button:first').addClass('dialog-button').addClass('close-button2');
        }
    });

	getEntitiesForTransmit();
	
});


function getEntitiesForTransmit(){
	var url=restManager.REST_END_POINT.TRANSMIT;
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetentitiesOk);
}


function selectRow(rowId){
	$('#row_' + rowId).toggleClass('selected');
}

function selectAllRows(){
	var selectAll=true;
	if($('#selectAll').hasClass('select-all-full')){
		//deselect all
		selectAll=false;
	}
	for(var i=0;i<entities.length;i++){
		var entityType = entities[i].entityType;
		var entityId = (entityType=="COUNT") ? entities[i].countId : entities[i].orderId;
		var entityUniqueId = entityType + "_" + entityId;
		if(selectAll){
			$('#row_' + entityUniqueId).addClass('selected');
		}
		else{
			$('#row_' + entityUniqueId).removeClass('selected');
		}
	}
	$('#selectAll').toggleClass('select-all-full');
}

function ajaxGetentitiesOk(data){
	entities=eval(data);
	
	$('#countTableBody').html('');
	var trHtml;
	for(var i=0;i<entities.length;i++){
		var entityType = entities[i].entityType;
		var d=(entityType=="COUNT") ? new Date(entities[i].countDate) : new Date(entities[i].orderDate); 
		var startDate=sicGeneralUtil.padd(d.getDate(),2,'0',false) + "/" + sicGeneralUtil.padd(d.getMonth()+1,2,'0',false) + "/" + d.getFullYear();
		var startTime=sicGeneralUtil.padd(d.getHours(),2,'0',false) + ":" + sicGeneralUtil.padd(d.getMinutes(),2,'0',false);
		var entityId = (entityType=="COUNT") ? entities[i].countId : entities[i].orderId;
		var entityUniqueId = entityType + "_" + entityId;
		var entityTypeDesc;
		if(entityType=="COUNT"){
			entityTypeDesc = "ספירה";
		}
		else {
		    switch (entities[i].type) {
		        case "URGENT": entityTypeDesc = "הזמנה דחופה";
		            break;
		        case "DESK": entityTypeDesc = "הזמנת דלפק";
		            break;

		        default: entityTypeDesc = "הזמנה יזומה";

		    }
		}
		
		
		trHtml="<tr id='row_"+entityUniqueId+"'>" +
		"<td width='20%' class='select'><div class='choose' onclick='selectRow(\""+ entityUniqueId+"\")'></div>" +
				"<div class='cancel_count' onclick='cancelEntityClick(\""+entityUniqueId+"\")'></div>"+
				"<a href='#' onclick='showItemsForCustomer(\"" + i + "\")'>" + entities[i].customer.id+"</a></td>" +
		"<td width='40%'>"+entities[i].customer.name+"</td>" +
		"<td width='10%'>" +entityTypeDesc+ "</td>" +
		"<td width='15%'>"+startDate+"</td>"+
		"<td width='15%'>"+startTime+"</td>"+
		"</tr>";
		$('#countTableBody').append(trHtml);
	}
	$('.table-holder').show();
}

function showItemsForCustomer(index){
	$('#itemsTableBody').html('');
	var line;
	var trHtml;
	var type=entities[index].entityType;
	if(type=="COUNT"){
		$('#itemsPopupQtyHeader').html("כמות שנספרה");
		$('#itemsTableLotHeader').show();
	}
	else{
		$('#itemsPopupQtyHeader').html("כמות שהוזמנה");
		$('#itemsTableLotHeader').hide();
	}
	
	
	for(var i=0;i<entities[index].lines.length;i++){
		line=entities[index].lines[i];
		trHtml="<tr>" +
		"<td width='30%'>" + line.product.custProductId + "</td>" +
		"<td width='30%'>" + line.product.custProductDesc + "</td>";
		if(type=="COUNT"){
		trHtml+= "<td width='20%'>" + line.product.lot1 + "-" + line.product.lot2 + "-" + line.product.lot3 + "</td>";
		}
		trHtml+= "<td>" + ((type=="COUNT")? line.countQty:line.orderQty) + "</td></tr>";
		$('#itemsTableBody').append(trHtml);
	}
	$('#itemsPopup').dialog({title:'סה"כ שורות ' + entities[index].lines.length}).dialog("open");
}

function transmit(){
	if(!restManager.isConnected()){
		$('#warnPopup #msg').html("לא ניתן לבצע את הפעולה, אנא פנה למנהל מערכת");
		$('#warnPopup').dialog("option","buttons",[{text:"אישור",click:function(){
			$(this).dialog("close");
		}}]).dialog("open");
		return;
	}
	if($('#countTableBody tr.selected').length==0){
		$('#warnPopup #msg').html("יש לסמן לפחות שורה אחת לשידור.");
		$('#warnPopup').dialog("option","buttons",[{text:"אישור",click:function(){
			$(this).dialog("close");
		}}]).dialog("open");
		return;
	}
	
	
	
	if(isTransmiting){
		return;
	}
	$('#progresser').width(0);
	$('#progressText').html("מתחבר לשרת...");
	isTransmiting=true;
	$("#progressPopup").dialog("open");
	
	var countIdStr="";
	$('#countTableBody tr.selected').each(function(){
		if(countIdStr!=""){
			countIdStr+="&";
		}
		countIdStr+="entity=" + $(this).attr('id').substring(4);
	});
	var url=restManager.REST_END_POINT.TRANSMIT + "?" + countIdStr;
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.POST, restManager.AJAX_CONTENT_TYPE.JSON, ajaxTransmitOk,ajaxTransmitFail);
	setTimeout(setProgress, 500);
}

function ajaxTransmitOk(){
	isTransmiting=false;
	setTimeout(function(){
		$('#progresser').width(290);
		setTimeout(function(){
			$('#progressPopup').dialog("close");
		}, 500);
	},1000);
	$('#progressText').html("שידור עבר בהצלחה.");
	getEntitiesForTransmit();
}

function ajaxTransmitFail(jqXHR, textStatus, errorThrown){
	isTransmiting=false;
	if(jqXHR.responseText=="CONNECTION"){
		$('#progressText').html("לא ניתן לבצע את הפעולה, אנא פנה למנהל מערכת");
	}
	else{
		
		$('#progressText').html(".השידור נכשל");
	}
	setTimeout(function(){ $('#progresser').width(290);},1000);
}

function closeProgress(){
	if(!isTransmiting){
		return true;
	}
	return false;
}

function setProgress(){
	if(!isTransmiting){
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
		if(isTransmiting){	
		setProgress();
		}
		
	});
	
}

function cancelEntityClick(rowId){
	
	var arr=rowId.split('_');
	var msg="האם לבטל את ה";
	if(arr[0]=="COUNT"){
		msg+="ספירה ?";
	}
	else{
		msg+="ההזמנה ?";
	}
	
	$('#warnPopup #msg').html(msg);
	$('#warnPopup').dialog("option","buttons",[{text:"אישור",click:function(){
		$(this).dialog("close");
		
		cancelEntity(arr[0],arr[1]);
	}},{text:"ביטול",click:function(){
		$(this).dialog("close");
	}}]).dialog("open");
}


function cancelEntity(entityType,entityId){
	var url;
	if(entityType=="COUNT"){
		url=restManager.REST_END_POINT.CANCEL_COUNT.replace("{countId}",entityId);
	}
	else{
		url=restManager.REST_END_POINT.CANCEL_ORDER.replace("{orderId}",entityId);
	}
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.DELETE, restManager.AJAX_CONTENT_TYPE.JSON, ajaxCancelEntityOk);
}

function ajaxCancelEntityOk(){
	getEntitiesForTransmit();
}



