var entityHistory;

$(function(){
	$('#homeButton').click(function(){
		location.href="index.html";
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


	$("#errorPopup").dialog({
		dialogClass:'error',
		title:'שגיאה',
		autoOpen:false,
		resizable: false,
		height:200,
		width:250,
		modal: true,
		buttons: {
			"סגור":function(){
				$(this).dialog("close");
			}
		},
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

	$('#selCountStatus').change(drawTable);	
	fillStatusesInSelect();
	getHistory();
});


function fillStatusesInSelect(){
	for(var i=0;i<inventoryCountStatus.length;i++){
		$('#selCountStatus').append("<option value='"+inventoryCountStatus[i].id+"'>"+inventoryCountStatus[i].name+"</option>");
	}
}

function getHistory(){
	var url=restManager.REST_END_POINT.HISTORY;
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetHistoryOk);
}


function ajaxGetHistoryOk(data){
	entityHistory=eval(data);
	drawTable();
}

function drawTable(){
	$('#transmitTableBody').html('');
	var trHtml;
	var filterStatus=$('#selCountStatus').val();
	for(var i=0;i<entityHistory.length;i++){
		if(filterStatus!="ALL" && entityHistory[i].entity.status!=filterStatus){
			continue;
		}
		var entityType = entityHistory[i].entity.entityType;
		var entityId = (entityType == "COUNT") ? entityHistory[i].entity.countId : entityHistory[i].entity.orderId; 
		var entityUniqeId = entityType + "_" + entityId;
		var d=(entityType=="COUNT") ? new Date(entityHistory[i].entity.countDate) : new Date(entityHistory[i].entity.orderDate) ;
		var startDate=sicGeneralUtil.padd(d.getDate(),2,'0',false) + "/" + sicGeneralUtil.padd(d.getMonth()+1,2,'0',false) + "/" + d.getFullYear();
		var startTime=sicGeneralUtil.padd(d.getHours(),2,'0',false) + ":" + sicGeneralUtil.padd(d.getMinutes(),2,'0',false);
		var customerId=parseInt(entityHistory[i].entity.customer.id, 10);
		var transDate="",tStatus="";
		if(entityHistory[i].transmit!=null){
			var d=new Date(entityHistory[i].transmit.transDate);
			transDate=sicGeneralUtil.padd(d.getDate(),2,'0',false) + "/" + sicGeneralUtil.padd(d.getMonth()+1,2,'0',false) + "/" + d.getFullYear();
			transTime=sicGeneralUtil.padd(d.getHours(),2,'0',false) + ":" + sicGeneralUtil.padd(d.getMinutes(),2,'0',false);
			tStatus=(entityHistory[i].transmit.transStatus=="OK") ? "הצלחה" : "כישלון";
		}

		var cancelIcon="";
		if(entityHistory[i].entity.status=="IN_PROGRESS" || entityHistory[i].entity.status=="COMPLETED"){
			cancelIcon="<div class='cancel_count' onclick='cancelEntityClick(\""+entityUniqeId+"\")'></div>";
		}
		var entityTypeDesc;
		if(entityType=="COUNT"){
			entityTypeDesc = "ספירה";
		}
		else{
			if(entityHistory[i].entity.type=="URGENT"){
				entityTypeDesc = "הזמנה דחופה";
			}
			else{
				
			  if(entityHistory[i].entity.type=="DESK"){
				  entityTypeDesc = "הזמנת דלפק";
			  }
			  else{
				  entityTypeDesc = "הזמנה יזומה";
			  }
			  }
			
		}

		trHtml="<tr>" +
		"<td width='15%'>" +  cancelIcon +customerId+"</td>" +
		"<td width='22%'>"+entityHistory[i].entity.customer.name+"</td>" +
		"<td width='10%'>"+entityTypeDesc+"</td>" +
		"<td width='10%'><a href='#'onclick='showEntityData(\""+entityUniqeId+"\")'>"+  sicGeneralUtil.getStatusText(entityHistory[i].entity.status)+"</a></td>" +
		"<td width='13%'>"+startDate  +"</td>" +
		"<td width='7%'>"+startTime  +"</td>" +
		"<td width='12%'>"+transDate  +"</td>" +
		"<td width='10%'>"+tStatus+"</td>"+
		"</tr>";
		$('#transmitTableBody').append(trHtml);
	}
	$('.table-holder').show();
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
	getHistory();
}

function showEntityData(rowId){
	var arr=rowId.split('_');
	var url;
	if(arr[0]=="COUNT"){
		url=restManager.REST_END_POINT.COUNT_HISTORY.replace("{countId}",arr[1]);
	}
	else{
		url=restManager.REST_END_POINT.ORDER_HISTORY.replace("{orderId}",arr[1]);
	}
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, showEntityDataOk);
}

function showEntityDataOk(data){
	$('#itemsTableBody').html('');
	var line;
	var trHtml;
	if(data.entityType=="COUNT"){
		$('#itemsPopupQtyHeader').html("כמות שנספרה");
		$('#itemsTableLotHeader').show();
	}
	else{
		$('#itemsPopupQtyHeader').html("כמות שהוזמנה");
		$('#itemsTableLotHeader').hide();
	}
	for(var i=0;i<data.lines.length;i++){
		line=data.lines[i];
		trHtml="<tr>";
		if(line.product!=null){
			trHtml+="<td width='30%'>" + line.product.custProductId+ "</td>" +
			"<td width='30%'>" + line.product.custProductDesc + "</td>";
			if(data.entityType=="COUNT"){
				trHtml+="<td width='20%'>" + line.product.lot1 + "-" + line.product.lot2 + "-" + line.product.lot3 + "</td>";
			}
		}
		else{
			trHtml+="<td width='30%'>  -  </td>" +
			"<td width='30%'>  -  </td>";
			if(data.entityType=="COUNT"){
				trHtml+="<td width='20%'>  -  </td>";
			}
		}
		if(data.entityType=="COUNT"){
			
			if(line.countQty==-1){
				trHtml+="<td width='20%'>לא נספר</td></tr>";
			}
			else{
				trHtml+="<td width='20%'>" + line.countQty + "</td></tr>";
			}
		}
		else{
			trHtml+="<td width='20%'>" + line.orderQty + "</td></tr>";
		}
		$('#itemsTableBody').append(trHtml);
	}
	$('#itemsPopup').dialog({title:'סה"כ שורות ' + data.lines.length}).dialog("open");
}



