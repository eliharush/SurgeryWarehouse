var isInOrder=false;
var customer;
var orderType, orderSubType;
var custProducts;
var custProductMap=[];
var custManProducts;
var custMaraManProducts;
var shownProducts;
var invOrder;
var partialOrderOrderer=0;
//var countContinue;
var lastSelectedProduct;
var lastSelectedCustomer;
var sortType,sortDir;
var linesOrdered;
var approvers;
var customers;
var currentProductId=-1;
//var currentFoundProductHandler=null;
var calcValueBeforeCancel=null;
var isSearchForswhm;

function sortProducts(prod1,prod2){
	var p1="",p2="";
	switch (sortType){
	case "id":p1=prod1.custProductId.toLowerCase();
	p2=prod2.custProductId.toLowerCase();
	break;
	case "desc": p1=prod1.custProductDesc.toLowerCase();
	p2=prod2.custProductDesc.toLowerCase();
	break;
	case "lot1":	p1=prod1.lot1;
	p2=prod2.lot1;
	break;
	case "lot2":	p1=prod1.lot2;
	p2=prod2.lot2;
	break;
	case "lot3":	p1=prod1.lot3;
	p2=prod2.lot3;
	break;
	case "stdQuantity": p1=prod1.stdQuantity;
	p2=prod2.stdQuantity;
	break;
	case "qty": p1=$("#qty_" + prod1.id).val();
	p2=$("#qty_" + prod2.id).val();
	break;
	}



	if(sortDir=="DESC"){
		if (p1 < p2) //sort string ascending
			return -1;
		if (p1 > p2)
			return 1;
		return 0; //default return value (no sorting)
	}
	else{
		if (p1 > p2) //sort string ascending
			return -1;
		if (p1 < p2)
			return 1;
		return 0; //default return value (no sorting)
	}
}


function OrderCustProduct(prod){
	var product=null;
	var oreredQty=0;
	var swhmProduct=false;

	function ctor(){
		product=prod;
	}

	ctor();

	this.setProduct=function(prod){
		product=prod;
	};

	this.getProduct=function(){
		return product;
	};


	this.setOrderQty=function(argOrderedQty){
		oreredQty=argOrderedQty;
	};

	this.getOrderQty=function(){
		return oreredQty;
	};
	
	this.isswhmProduct=function(){
		return swhmProduct;
	};
	
	this.setswhmProduct=function(argswhmProduct){
		swhmProduct = argswhmProduct;
	};

}

$(function(){
	$('#homeButton').click(function(){
		if(isInOrder){
			$('#warnPopup #msg').html("הזמנה בתהליך, האם לחזור לתפריט ראשי ?");
			$('#warnPopup').dialog("option","buttons",[{text:"אישור",click:function(){
				$(this).dialog("close");
				location.href="index.html";
				
			}},{text:"ביטול",click:function(){
				$(this).dialog("close");
			}}]).dialog("open");
			
		}
		else{
			location.href="index.html";
		}
	});




	$('#txtSelectCustomer').forceNumericOnly().bind('keypress',function(e){
		var key = e.charCode || e.keyCode || 0;
		if(key==13){
			searchCustomer();	
		}
		else{
			$('#customerDesc').html('');
		}
	});

	$('#btnSelectCustomer').click(searchCustomer);
	$('#startButton').click(startOrder);
	$('#finishButton').click(endOrder);
	$('#cancelButton').click(cancelOrderClick);
	$('#hidProduct').bind('keypress',function(e){
		var key = e.charCode || e.keyCode || 0;
		if(key==13){
			var prod=$.trim($(this).val());
			searchProduct(prod.toUpperCase());
		}
	});

	$("#itemsPopup").dialog({
		dialogClass:'items',
		title:'פריטים לבחירה',
		autoOpen:false,
		resizable: false,
		height:330,
		width:500,
		modal: true,
		buttons: {
			"אישור":function(){
				if(lastSelectedProduct==""){
					alert("אנא בחר פריט.");
					return;
				}
				handleOneProductFound(custProductMap[lastSelectedProduct].getProduct(), false);
				$(this).dialog("close");
			},
			"ביטול": function(){
				$(this).dialog("close");
				focusOnProduct();
			}
		},
		//beforeClose:closeLoading,
		open: function() {
			$buttonPane = $(this).next();
			$buttonPane.find('button:first').addClass('dialog-button').addClass('close-button2');
			$buttonPane.find('button:last').addClass('dialog-button').addClass('ok-button');                        
		}
	});

	$("#itemPopup").dialog({
		dialogClass:'item',
		title:'',
		autoOpen:false,
		resizable: false,
		height:200,
		width:400,
		modal: false,
		buttons: {
			"אישור":function(){
				$(this).dialog("close");
			}
		},
		open: function() {
			$buttonPane = $(this).next();
			$buttonPane.find('button:last').addClass('dialog-button').addClass('ok-button');                        
		}
	});


	$("#customersPopup").dialog({
		dialogClass:'customers',
		title:'',
		autoOpen:false,
		resizable: false,
		height:330,
		width:450,
		modal: true,
		buttons: {
			"אישור":function(){
				if(lastSelectedCustomer==""){
					alert("אנא בחר לקוח.");
					return;
				}
				customer=customers[lastSelectedCustomer];
				getManProductsForCustomer();
				$(this).dialog("close");
			},
			"ביטול": function(){
				$(this).dialog("close");
			}
		},
		//beforeClose:closeLoading,
		open: function() {
			$buttonPane = $(this).next();
			$buttonPane.find('button:first').addClass('dialog-button').addClass('close-button2');
			$buttonPane.find('button:last').addClass('dialog-button').addClass('ok-button');                        
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


	$("#approverPopup").dialog({
		dialogClass:'approver',
		title:'מאשר',
		autoOpen:false,
		resizable: false,
		height:360,
		width:350,
		position:"top",
		modal: true,
		buttons: {
			"אשר": function() {
				approveFinishOrder();	

			},
			"סגור": function() {
				$( this ).dialog( "close" );
			}
		}
	});

	$('.sigPad').signaturePad({defaultAction:"drawIt"});


	$('#empPass').bind('keypress',function(e){
		var key = e.charCode || e.keyCode || 0;
		if(key==13){
			approveFinishOrder();	
		}
	});

	// get the cursor back on id product for the barcod reader
	$(document).bind('keydown',function(e){
		if (isInOrder && $(":focus").length==0){
			$('#hidProduct').focus();
		}
	});


	$(window).unload(function(){
		if(isInOrder){
			saveOrder(false);
		}
	});

	getApprovers();


});

function getApprovers(){
	var url=restManager.REST_END_POINT.APPROVERS;
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetApproversOk);
}

function ajaxGetApproversOk(data){
	approvers=eval(data);
	for(var i=0;i<approvers.length;i++){
		$('#empName').append("<option value='" + approvers[i].id + "'>" + approvers[i].id + "</option>");
	}

	$('#txtSelectCustomer').focus();
}


function searchCustomer(){
	$('#startButton').removeClass('start_button').addClass('start_button_disabled');
	var custNo;
	if($('#txtSelectCustomer').val()==""){
		custNo="__EMPTY__";
	}
	else{
		//wrap customer no
		custNo=$('#txtSelectCustomer').val();
	}
	//search for customer
	var url=restManager.REST_END_POINT.SEARCH_CUSTOMER.replace("{hospitalId}", hospitalId).replace("{custNo}",custNo);
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetCustomerOk);
}

function ajaxGetCustomerOk(data, textStatus, jqXHR){
	customers=eval(data);
	if(customers==null || customers.length==0){
		$('#warnPopup #msg').html("עגלה/יחידה אינה קיימת.");
		$('#warnPopup').dialog("option","buttons",[{text:"אישור",click:function(){
			$(this).dialog("close");
			$('#txtSelectCustomer').focus();
		}}]).dialog("open");
		return;
	}
	if(customers.length==1){
		//found one
		customer=customers[0];
		getManProductsForCustomer();
		return;
	}
	fillCustomersTable();
}

function fillCustomersTable(){
	$('#customersTableBody').html("");
	lastSelectedCustomer="";
	var trHtml;
	for(var i=0;i<customers.length;i++){
		trHtml="<tr id='row_"+i+"'><td width='10%'><div class='select' id='selCustomer_"+i+"'></div></td>" +
		"<td>" + customers[i].id + "</td><td>" + customers[i].name + "</td></tr>";
		$('#customersTableBody').append(trHtml);
	}
	$('[id^="selCustomer_"]').bind('click',function(){
		var id=$(this).attr("id").substring(12);
		if(lastSelectedCustomer!=""){
			$('#row_' + lastSelectedCustomer).removeClass('selected');
		}
		lastSelectedCustomer=id;
		$('#row_' + lastSelectedCustomer).addClass('selected');
	});
	$( "#customersPopup" ).dialog("open");
}


function getManProductsForCustomer(){
	var url=restManager.REST_END_POINT.SEARCH_MAN_PRODUCTS.replace("{hospitalId}",hospitalId).replace("{custNo}", customer.id);
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetCustomerManProductsrOk);
}

function ajaxGetCustomerManProductsrOk(data, textStatus, jqXHR){
	custManProducts=eval(data);
	getMaraManProductForCustomer();
}

function getMaraManProductForCustomer(){
	var url=restManager.REST_END_POINT.SEARCH_MARA_MAN_PRODUCTS.replace("{hospitalId}",hospitalId).replace("{custNo}", customer.id);
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetCustomerMaraManProductsrOk);	
}

function ajaxGetCustomerMaraManProductsrOk(data, textStatus, jqXHR){
	custMaraManProducts=eval(data);
	getProductForCustomer();
}

function getProductForCustomer(){
	var url=restManager.REST_END_POINT.SEARCH_CUST_PRODUCTS.replace("{hospitalId}",hospitalId).replace("{custNo}", customer.id);
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetCustomerProductsrOk);
}

function searchHospitalProducts(productId, cb) {
    var url = restManager.REST_END_POINT.SEARCH_HOSPITAL_PRODUCTS.replace("{hospitalId}", hospitalId).replace("{productId}", productId);
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetSearchHospitalProductsOk);

    function ajaxGetSearchHospitalProductsOk(data) {
        var products = data;
        var result = null;
        var isswhmProduct = true;
        //not mitadef: "כ"
        for (var i = 0, len = products.length; i < len; i++) {
            if (products[i].mitadef != "כ")
            {
                result = products[i];
                custProductMap[result.id] = new OrderCustProduct(result);
                if (result.productId != productId)
                    isswhmProduct = false;
                break;
            }
        }
        
        if (cb)
        {
            cb(result, isswhmProduct);
        }
    }
}



function prepareCustProductMap(){
	for(var i=0;i<custProducts.length;i++){
		custProductMap[custProducts[i].id]=new OrderCustProduct(custProducts[i]);
	}
}

function ajaxGetCustomerProductsrOk(data, textStatus, jqXHR){	
	custProducts=eval(data);
	prepareCustProductMap();
	$('#txtSelectCustomer').val(customer.id);
	$('#customerDesc').html(customer.name).css('display','inline');



	if(customer.notTransmitedOrderId){
		$('#warnPopup #msg').html("לא ניתן לבצע הזמנה ללקוח זה, עד לשידור ההזמנה האחרונה.");
		$('#warnPopup').dialog("option","buttons",[{text:"אישור",click:function(){
			$(this).dialog("close");
			$('#txtSelectCustomer').val('').focus();
		}}]).dialog("open");
		cleanCustomer();
		return;
	}
	else if(customer.currentOrderId>0){

		var msg="ללקוח זה יש הזמנה ";
		if(customer.currentOrderType=="URGENT"){
			msg+="דחופה ";
		}
		else{
			msg+="יזומה";
		}
		msg+=". האם ברצונך להמשיכה או לבטלה ?";
		$('#warnPopup #msg').html(msg);
		$('#warnPopup').dialog({width:500}).dialog("option","buttons",[{text:"המשך",click:function(){
			$(this).dialog("close");
			continueOrder();
			$('#startButton').removeClass('start_button_disabled').addClass('start_button');
		}},
		{text:"מחק הזמנה",click:function(){
			$(this).dialog("close");
			deleteOrder();
			$('#startButton').removeClass('start_button_disabled').addClass('start_button');
		}},
		{text:"ביטול",click:function(){
			$(this).dialog("close");
			cleanCustomer();
			$('#txtSelectCustomer').focus();
		}}]).dialog("open");

	}
	else{
		$('#startButton').removeClass('start_button_disabled').addClass('start_button');
	}

}

function changeHeaderState(disable){
	$('.header input').attr("disabled",disable);
}

function continueOrder(){
	orderType=customer.currentOrderType;
	if(orderType=="URGENT"){
		$('#radUrgent').attr('checked',true);
	}
	else{
		$('#radRegular').attr('checked',true);
	}
	changeHeaderState(true);
	var url=restManager.REST_END_POINT.GET_ORDER.replace("{orderId}",customer.currentOrderId);
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxAddOrderOk,ajaxAddOrderFail);
}

function deleteOrder(){
	orderType=customer.currentOrderType;
	var url=restManager.REST_END_POINT.CANCEL_ORDER.replace("{orderId}",customer.currentOrderId);
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxDeleteOrderOk,ajaxDeleteOrderFail);
}

function ajaxDeleteOrderOk(){

}

function ajaxDeleteOrderFail(){
	$('#errorPopup #msg').html("כישלון במחיקת הזמנה קיימת.");
	$('#errorPopup').dialog("open");
}

function startOrder(){
	if(isInOrder || $('#startButton').hasClass('start_button_disabled')){
		return;
	}

	orderType=$('input[name=radOrderType]:checked').val();
	orderSubType=$('#radDesk').prop('checked');
    
	var orderTypeDB = orderType;
	if (orderSubType)
	{
	    orderType = "URGENT"
	    orderTypeDB = "DESK";
	}
	changeHeaderState(true);
	var url=restManager.REST_END_POINT.START_ORDER.replace("{hospitalId}",customer.hospital.id).replace("{customerId}",customer.id)
	.replace("{type}", orderTypeDB);
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.POST, restManager.AJAX_CONTENT_TYPE.JSON, ajaxAddOrderOk,ajaxAddOrderFail);
}

function getAllProductsExeptCurrentCart()
{
    
}

function ajaxAddOrderFail(){
	isInOrder=false;
	changeHeaderState(false);
	$('#errorPopup #msg').html("כישלון ביצירת רשומת הזמנה ללקוח.");
	$('#errorPopup').dialog("open");
}

function ajaxAddOrderOk(data){
	$('#orderTableBody').html('');
	shownProducts=[];
	$('#finishButton').removeClass('finish_button_disabled').addClass('finish_button');
	$('#cancelButton').removeClass('cancel_button_disabled').addClass('cancel_button');
	$('#startButton').removeClass('start_button').addClass('start_button_disabled');
	invOrder=eval(data);
	var d=new Date(invOrder.orderDate);
	var orderDate=sicGeneralUtil.padd(d.getDate(),2,'0',false) + "/" + sicGeneralUtil.padd(d.getMonth()+1,2,'0',false) + "/" + d.getFullYear();
	var orderTime=sicGeneralUtil.padd(d.getHours(),2,'0',false) + ":" + sicGeneralUtil.padd(d.getMinutes(),2,'0',false);
	$('#orderDate').html(orderDate);
	$('#orderTime').html(orderTime);
	$('.counting-date .content').show();
	isInOrder=true;
	linesOrdered=0;
	
	if(invOrder.lines!=null){
		var prod;
		for(var i=0;i<invOrder.lines.length;i++){
			if(invOrder.lines[i].product!=null){
				prod=custProductMap[invOrder.lines[i].product.id];
				linesOrdered++;
				prod.setOrderQty(invOrder.lines[i].orderQty);
				prod.setswhmProduct(invOrder.lines[i].swhmProduct);
				
				shownProducts[shownProducts.length]=prod.getProduct();
				addProductToOrder(prod.getProduct(), i, prod.getOrderQty(),prod.isswhmProduct());
			}
		}
	}

	$('#spnLinesInOrder').html(linesOrdered);
	$('#linesDiv').show();

	$('#orderTableHolder').show();
	$('#hidProduct').show();
	focusOnProduct();
}

function attachNumPad(selector){
	selector.keyboard({
		openOn:'',
		layout:'custom',
		customLayout: { 'default': ['5 6 7 8 9', '0 1 2 3 4','{a} . {b}  {c} '] },
		restrictInput : true,
		preventPaste : true, 
		autoAccept : false,
		lockInput:true,
		position : {
			of : $(window	), // null (attach to input/textarea) or a jQuery object (attach elsewhere)
			my : 'center top',
			at : 'center top',
			at2: 'center top' // used when "usePreview" is false (centers keyboard at the bottom of the input/textarea)
		},
		accepted : function(e, keyboard, el){
			if(isNaN($(el).val())){
				handleOrderLine($(el).attr('id').substr(4),$(el).val(),($(el).attr('isswhm')=="true"));
				$(el).val('');
				$('#warnPopup #msg').html("כמות לא תקינה.");
				$('#warnPopup').dialog("option","buttons",[{text:"אישור",click:function(){
					$(this).dialog("close");
					focusOnProduct();
				}}]).dialog("open");
				return;
			}
			$('[id^=countRow_]').removeClass('incount');
			handleOrderLine($(el).attr('id').substr(4),$(el).val(),($(el).attr('isswhm')=="true"));
			focusOnProduct();
		},
		visible:function(e,k,el){
			$('[id^=countRow_]').removeClass('incount');
			$('#countRow_' + $(el).attr('id').substr(4)).addClass('incount');
			currentProductId = $(el).attr('id').substr(4);
		},
		canceled:function(e, keyboard, el){
			$('[id^=countRow_]').removeClass('incount');
			if(calcValueBeforeCancel){
				$(el).val(calcValueBeforeCancel);
				calcValueBeforeCancel = null;
			}
			focusOnProduct();
		},
		beforeClose:function(e, keyboard, el){
			calcValueBeforeCancel = $(el).val();
		}

	});
	
	$(selector).bind('focus',function(){
		var prodId = $(this).attr('id').substr(4);
		var line=custProductMap[prodId];
		var prod = line.getProduct();
		var $that = $(this);
		if(!($(this).attr('isswhm')=="true") && prod.comment){
			$('#infoPopup #msg').html(prod.comment);
			$('#infoPopup').dialog("option","buttons",[{text:"אישור",click:function(){
				$(this).dialog("close");
				//$('#qty_' + prod.id).focus();
				$that.keyboard().getkeyboard().reveal();

			}}]).dialog("open");
		}
		else{
			$that.keyboard().getkeyboard().reveal();
		}
	});
}



function sortTable(argSortType){

	if(sortType==argSortType){
		sortDir=(sortDir=="DESC") ? "ASC" : "DESC";
	}
	else{
		sortType=argSortType;
		sortDir="DESC";
	}

	$('.table-header .sort').removeClass('sort_desc').removeClass('sort_asc').addClass('sort_regular');
	if(sortDir=="DESC"){
		$('.table-header #sort_' + argSortType).removeClass('sort_regular').addClass('sort_desc');
	}
	else{
		$('.table-header #sort_' + argSortType).removeClass('sort_regular').addClass('sort_asc');
	}
	shownProducts.sort(sortProducts);
	$('#orderTableBody').html('');
	for(var i=0;i<shownProducts.length;i++){
		addProductToOrder(shownProducts[i],i, custProductMap[shownProducts[i].id].getOrderQty(),custProductMap[shownProducts[i].id].isswhmProduct());
	}
}


function addProductToOrder(prod,count,qty,isswhm){
	var trClass=(count%2==0)? "even":"odd";
	if(qty!=undefined){
		trClass+=" selected";
	}
	var cancelIcon="<div class='cancel_row' onclick='cancelRowClick(event,\""+prod.id+"\")'></div>";
	var productId = (isswhm) ? prod.productId : prod.custProductId;
	var trHtml="<tr id='countRow_"+prod.id+"' class='" + trClass + "'>" +
	"<td width='30%' class='text-center first' id='prodTd_"+count+"' title='"+productId+"'>"+ cancelIcon + productId+"</td>" +
	"<td width='40%' title='"+prod.custProductDesc+"'>"+prod.custProductDesc+"</td>";
	
	if(isswhm){
		trHtml+= "<td width='15%' class='text-center'>"+prod.salesUnitDesc+"</td>";
	}
	else{
		trHtml+= "<td width='15%' class='text-center'>"+prod.customerUnitDesc+"</td>";
	}
	trHtml+="<td width='15%' class='qty text-center'><input type='text' isswhm='"+isswhm+"' class='numpad' id='qty_" + prod.id + "'";

	if(qty!=undefined){
		trHtml+=" value='"+qty+"'";
	}
	trHtml+="/>";

	trHtml+= "</td></tr>";
	$('#orderTableBody').append(trHtml);
	attachNumPad($('#qty_' + prod.id));
	
	
	
}

function cancelRowClick(e,id){
	//delete custProductMap[id];
	var iToSplice=-1;
	for(var i=0;i<shownProducts.length;i++){
		if(shownProducts[i].id==id){
			iToSplice=i;
			break;
		}
	}
	if(iToSplice>-1){
		shownProducts.splice(iToSplice,1);
	}
	$('#countRow_' + id).remove();
	
	$("tr[id^='countRow_']").each(function(i){
		var trClass=((i+1)%2==0)? "even":"odd";
		$(this).removeClass("odd even").addClass(trClass);
	});
	
	$('#spnLinesInOrder').html(--linesOrdered);
	
	e.preventDefault();
}

function showDescForItem(e,id){

	$('#itemPopup #txtMiniItemCode').val(custProductMap[id].getProduct().custProductId);
	$('#itemPopup #txtMiniItemStdQty').val(custProductMap[id].getProduct().stdQuantity);

	$('#itemPopup').dialog("option","position",[e.clientX-300,e.clientY]).dialog('open');

}

function focusOnProduct(){
	setTimeout(function(){$('#hidProduct').val('').focus();},0);
}

function searchProduct(searchProdId){
    var foundProducts=new Array();
    var searchProdIdOrg=searchProdId;
	//if its urgent check swhm product
	if(orderType=="URGENT"){
		for(var i=0;i<custProducts.length;i++){
			if(custProducts[i].productId == searchProdId){
				handleOneProductFound(custProducts[i], true);
				return;
			}
		}
	}
	//not srael product - search manufactur product first
	for(var i=0;i<custManProducts.length;i++){
		if(custManProducts[i].manProductId==searchProdId){
			//find it set the cust product id to be the one searched
			searchProdId=custManProducts[i].custProductId;
			break;
		}
	}
	
	var maraFlag=false;
	//after that search Mara manufactur product
	for(var i=0;i<custMaraManProducts.length;i++){
		if(custMaraManProducts[i].manProductId==searchProdId){
			//search customer product
			for(var j=0;j<custProducts.length;j++){
				if(custProducts[j].productId == custMaraManProducts[i].productId){
					if(orderType=="URGENT" || (orderType!="URGENT" && custProducts[j].mitadef=='כ')){
						foundProducts[foundProducts.length]=custProducts[j];
						break;
					}
				}
			}
			maraFlag=true;
		}
	}
	
	
	
	
	if(!maraFlag){
	
		//search customer product
		for(var i=0;i<custProducts.length;i++){
			
			if(custProducts[i].custProductId == searchProdId){
				if(orderType=="URGENT" || (orderType!="URGENT" && custProducts[i].mitadef=='כ')){
					foundProducts[foundProducts.length]=custProducts[i];
				}
			}
		}
	}

	if (foundProducts.length == 0 && orderType == "URGENT") {
	    searchHospitalProducts(searchProdIdOrg, function (result, isswhmProduct) {
	        if (result)
	        {
	            handleOneProductFound(result, isswhmProduct);
	        }
	        else
	        {
	            keepSearch();
	        }
	    });
	}
	else
	{
	    keepSearch();
	}

	function keepSearch() {
	    //no product found call it and get out
	    if (foundProducts.length == 0) {
	        $('#warnPopup #msg').html("מוצר [" + searchProdId + "] לא נמצא במאגר.");
	        $('#warnPopup').dialog("option", "buttons", [{
	            text: "אישור", click: function () {
	                $(this).dialog("close");
	                focusOnProduct();
	            }
	        }]).dialog("open");
	        return;
	    }
	    if (foundProducts.length == 1) {
	        handleOneProductFound(foundProducts[0], false);
	        return;
	    }

	    //found more than one - check which is already in order 
	    var finalFoundProduct = [];
	    for (var i = 0; i < foundProducts.length; i++) {
	        if ($('#qty_' + foundProducts[i].id).length == 0) {
	            finalFoundProduct[finalFoundProduct.length] = foundProducts[i];
	        }
	    }
	    if (finalFoundProduct.length == 1) {
	        handleOneProductFound(finalFoundProduct[0], false);
	        return;
	    }
	    if (finalFoundProduct.length == 0) {
	        //fill items ih table
	        fillItemsTable(foundProducts);
	    }
	    else {
	        fillItemsTable(finalFoundProduct);
	    }
	}
	
}





function fillItemsTable(prods){
	$('#itemsTableBody').html("");
	lastSelectedProduct="";
	var trHtml;
	for(var i=0;i<prods.length;i++){
		trHtml="<tr id='row_"+prods[i].id+"'><td width='10%'><div class='select' id='selItem_"+prods[i].id+"'></div></td>" +
		"<td width='20%'>" + prods[i].productId + "</td><td width='40%'><div class='fixed-width-250'>" + prods[i].custProductDesc + "</div></td></tr>";
		$('#itemsTableBody').append(trHtml);
	}
	$('[id^="selItem"]').bind('click',function(){
		var id=$(this).attr("id").substring(8);
		if(lastSelectedProduct!=""){
			$('#row_' + lastSelectedProduct).removeClass('selected');
		}
		lastSelectedProduct=id;
		$('#row_' + lastSelectedProduct).addClass('selected');
	});
	$( "#itemsPopup" ).dialog("open");
}

function handleOneProductFound(prod,isswhm){
	//if not isswhm and has message show it
	if($('#qty_' + prod.id).length==0){
		shownProducts[shownProducts.length]=prod;
		addProductToOrder(prod,++partialOrderOrderer,undefined,isswhm);
		$('#spnLinesInOrder').html(++linesOrdered);
	}
	$('#qty_' + prod.id).focus();
	return;
}

function handleOrderLine(id,qty,isswhm){
	var orderedQty=parseFloat(qty);
	var line=custProductMap[id];
	if(isNaN(qty) || qty=="" || orderedQty==0){
		orderedQty = 1;
	}
	//check if not swhm and need to multiply 
	if(!isswhm && line.getProduct().countFactor){
		orderedQty = parseFloat((orderedQty *line.getProduct().countFactor).toFixed(2));
	}
	
	
	//put the calculated quantity in the value field
	if(!isswhm){
		orderedQty = calcOrdQty(orderedQty, line.getProduct().minimalOrderCount);
	}
	
	$('#qty_' + id).val(orderedQty);
	
	if(!$('#countRow_' + id).hasClass("selected")){
		$('#countRow_' + id).addClass("selected");
		
	}
	line.setOrderQty(orderedQty);
	line.setswhmProduct(isswhm);
}

function calcOrdQty(ordQty,minimalQty){
	
	if(!minimalQty || ordQty==0){
		return ordQty;
	}
	else{
		if(ordQty<=minimalQty){
			return minimalQty;
		}
		else{
			var div=Math.round(ordQty/minimalQty);
			return div*minimalQty;
		}
	}
}


function saveOrder(finish){
	//prepare count lines
	var lines=new Array();
	var id;
	$('[id^="qty_"]').each(function(){
		id=$(this).attr('id').substr(4);
		if((custProductMap[id].getOrderQty()==null || custProductMap[id].getOrderQty()==="" || custProductMap[id].getOrderQty()===0) && finish){
			return;
		}
		var line={};
		line.product=custProductMap[id].getProduct();
		line.orderQty=custProductMap[id].getOrderQty();
		line.swhmProduct=custProductMap[id].isswhmProduct();
		lines[lines.length]= line;
	});
	invOrder.lines=lines;
	var url=restManager.REST_END_POINT.SAVE_ORDER.replace("{orderId}",invOrder.orderId).replace("{finish}", finish);
	var okHandler=undefined,failHandler=undefined;
	if(finish){
		okHandler=ajaxFinishOrderOk;
		failHandler=ajaxFinishOrderFail;
	}
	else{
		//okHandler=failHandler=function(){location.href="index.html";};
	}
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.PUT, restManager.AJAX_CONTENT_TYPE.JSON, okHandler,failHandler,invOrder,undefined,false);
}

function endOrder(){
	if(!isInOrder || $('#finishButton').hasClass('finish_button_disabled')){
		return;
	}
	
	var lineLen=$('[id^=qty_]').length;
	if(lineLen==0){
		$('#warnPopup #msg').html("לא ניתן לסיים הזמנה ללא שורות הזמנה.");
		$('#warnPopup').dialog("option","buttons",[{text:"אישור",click:function(){
			$(this).dialog("close");
			focusOnProduct();
		}}]).dialog("open");
		return;
	}
	$('#empPass').val('');
	$('#approverPopup').dialog("open");


}



function approveFinishOrder(){
	var approver=null;
	for(var i=0;i<approvers.length;i++){
		if(approvers[i].id==$('#empName').val()){
			approver=approvers[i];
			break;
		}
	}
	if($('#empPass').val().toUpperCase()!=approver.password){
		$('#warnPopup #msg').html("סיסמא שגויה למאשר.");
		$('#warnPopup').dialog("option","buttons",[{text:"אישור",click:function(){
			$(this).dialog("close");
		}}]).dialog("open");
		return;
	}
	invOrder.approver=approver;
	saveOrder(true);
}


function cancelOrderClick(){
	if(!isInOrder || $('#cancelButton').hasClass('cancel_button_disabled')){
		return;
	}
	$('#warnPopup #msg').html("האם לבטל את ההזמנה?");
	$('#warnPopup').dialog("option","buttons",[{text:"אישור",click:function(){
		$(this).dialog("close");
		cancelOrder();
	}},{text:"ביטול",click:function(){
		$(this).dialog("close");
	}}]).dialog("open");
}

function cancelOrder(){
	var url=restManager.REST_END_POINT.CANCEL_ORDER.replace("{orderId}",invOrder.orderId);
	restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.DELETE, restManager.AJAX_CONTENT_TYPE.JSON, ajaxFinishOrderOk);
}



function cleanCustomer(){
	customer=null;
	custProductMap=[];
	custProducts=null;
	$('#txtSelectCustomer').val("");
	$('#customerDesc').html("").hide();


}

function ajaxFinishOrderOk(){
	$('#approverPopup').dialog("close");
	$('.sigPad').signaturePad().clearCanvas();
	changeHeaderState(false);
	isInOrder=false;
	cleanCustomer();
	$('#txtSelectCustomer').focus();
	$('.order-date .content').hide();
	$('#orderTableBody').html('');
	$('#orderTableHolder').hide();
	$('#hidProduct').hide();
	$('#linesDiv').hide();
	$('#finishButton').removeClass('finish_button').addClass('finish_button_disabled');
	$('#cancelButton').removeClass('cancel_button').addClass('cancel_button_disabled');
}

function ajaxFinishOrderFail(){
	$('#approverPopup').dialog("close");
	$('#errorPopup #msg').html("סיום הזמנה נכשל");
	$('#errorPopup').dialog("open");
}



